/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import static stallone.api.API.*;

import java.util.ArrayList;
import java.util.List;

import stallone.api.API;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.hmm.IHMMParameters;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.hmm.IExpectationMaximization;
import stallone.api.hmm.IHMM;

/**
 *
 * @author noe
 */
public class NinjaEstimator implements IExpectationMaximization
{
    // discrete trajectory

    private List<IIntArray> dtraj;
    // estimation parameters
    private int tau = -1;
    // time shift between time grids
    private int timeshift = -1;
    // number of hidden states
    private int nhidden = -1;
    // max number of hmm iterations
    private double maxHMMLInc = -0.01;
    private int nIterHMMMax = 1000;
    // user initialization
    private IDoubleArray initChi = null;
    private IDoubleArray initTC = null;
    // current MSM estimation data
    private IDoubleArray msmC, msmT, msmpi, msmPi, msmCorr;
    private IDoubleArray msmChi, msmTC, msmTimescales;
    // current HMM estimates
    private IHMM hmmEst;
    private IDoubleArray hmmChi, hmmTC, hmmpiC, hmmTimescales;
    private double[] hmmLikelihoodHistory;
    // error estimates

    public NinjaEstimator(List<IIntArray> _dtraj)
    {
        this.dtraj = _dtraj;
    }

    public NinjaEstimator(int _tau, int _timeshift, int _nhidden)
    {
        this.tau = _tau;
        this.timeshift = _timeshift;
        this.nhidden = _nhidden;
    }

    public void setData(List<IIntArray> _dtraj)
    {
        this.dtraj = _dtraj;
    }

    public void setTau(int _tau)
    {
        this.tau = _tau;
    }

    public void setTimeshift(int _timeshift)
    {
        this.timeshift = _timeshift;
    }

    public void setNHiddenStates(int _nhidden)
    {
        this.nhidden = _nhidden;
    }

    public void setDiscreteTrajectory(List<IIntArray> _dtraj)
    {
        this.dtraj = _dtraj;
    }

    public void setNIterHMMMax(int _nIterHMMMax)
    {
        this.nIterHMMMax = _nIterHMMMax;
    }

    public void setHMMLikelihoodMaxIncrease(double _maxHMMLInc)
    {
        this.maxHMMLInc = _maxHMMLInc;
    }

    public void setInit(IDoubleArray TCInit, IDoubleArray ChiInit)
    {
        this.initTC = TCInit;
        this.initChi = ChiInit;
    }

    public IDoubleArray getMSMTransitionMatrix()
    {
        return msmT;
    }

    public IDoubleArray getMSMStationaryDistribution()
    {
        return msmpi;
    }

    public IDoubleArray getMSMTimescales()
    {
        return msmTimescales;
    }

    public IDoubleArray getPCCATransitionMatrix()
    {
        return msmTC;
    }
    
    public IDoubleArray getPCCAOutputProbabilities()
    {
        return msmChi;
    }
    
    public IDoubleArray getHMMTransitionMatrix()
    {
        return hmmTC;
    }

    public IDoubleArray getHMMStationaryDistribution()
    {
        return hmmpiC;
    }

    public IDoubleArray getHMMOutputProbabilities()
    {
        return hmmChi;
    }

    public IDoubleArray getHMMTimescales()
    {
        return hmmTimescales;
    }

    public double[] getHMMLikelihoodHistory()
    {
        return hmmLikelihoodHistory;
    }


    /*
     * private List<IDataSequence> compressTrajectory(IIntArray dtraj) { int
     * nout = ((dtraj.size()-1) / tau) + 1; int nstates = ints.max(dtraj) + 1;
     *
     * double[][] out = new double[nout][nstates];
     *
     * for (int i = 0; i < dtraj.size(); i++) { // closest out index: int iout =
     * i / tau;
     *
     * // are we within averageWindow/2? double timeOfGrid = iout * tau + tau /
     * 2.0; double dist = Math.abs(timeOfGrid - i); if (dist <= (averageWindow /
     * 2)) { out[iout][dtraj.get(i)] += 1.0; } } IDataList seq =
     * dataNew.createDatalist();
     *
     * for (int i = 0; i < out.length; i++) {
     * seq.add(doublesNew.arrayFrom(out[i])); }
     *
     * ArrayList<IDataSequence> res = new ArrayList(); res.add(seq);
     *
     * return res;
    }
     */
    /**
     * Creates a series of subsamples of the given trajectory: (0, tau, 2*tau,
     * ...) (dt, tau+dt, 2*tau+dt, ...) ...
     *
     * @param dtraj discrete trajectory
     * @param dt time shift between two subsequent sampling series
     * @return
     */
    private List<IIntArray> subsamples(IIntArray dtraj, int dt)
    {
        int N = dtraj.size();
        List<IIntArray> res = new ArrayList();
        for (int s = 0; s < tau; s += dt)
        {
            IIntArray I = intsNew.arrayRange(s, N, tau);
            res.add(ints.subToNew(dtraj, I));
        }
        return res;
    }

    private List<IIntArray> subsamples(List<IIntArray> dtraj, int dt)
    {
        List<IIntArray> res = new ArrayList();
        for (int i = 0; i < dtraj.size(); i++)
        {
            res.addAll(subsamples(dtraj.get(i), dt));
        }
        return res;
    }

    private List<IDataSequence> toObservation(List<IIntArray> dTrajectories, int nstates)
    {
        List<IDataSequence> obs = new ArrayList();
        for (int i = 0; i < dTrajectories.size(); i++)
        {
            //DiscreteTrajectoryDataSequence seq = new DiscreteTrajectoryDataSequence(dTrajectories.get(i), nstates);
            DiscreteTrajectorySimpleDataSequence seq = new DiscreteTrajectorySimpleDataSequence(dTrajectories.get(i));
            obs.add(seq);
        }
        return obs;
    }

    private IHMMParameters hmm(List<IDataSequence> observations, IDoubleArray TCInit, IDoubleArray ChiInit)
            throws ParameterEstimationException
    {
        System.out.println("HMM initialized with timescales: "+msm.timescales(TCInit, tau));
        
        // initial parameters
        IHMMParameters par0 = hmmNew.parameters(nhidden, true, true);
        // FIXME: converted to dense, because sparse impl IDoubleArray runs out of bounds in Tarjan algo of graph/connectivity/IntStrongConnectivity 
        IDoubleArray dense = API.doublesNew.array(TCInit.rows(), TCInit.columns());
        TCInit.copyInto(dense);
        par0.setTransitionMatrix(dense);
        for (int i = 0; i < nhidden; i++)
        {
            par0.setOutputParameters(i, ChiInit.viewColumn(i));
        }

        // Estimate
        int nObservableStates = ChiInit.rows();
        double[] uniformPrior = new double[nObservableStates];
        java.util.Arrays.fill(uniformPrior, 1.0 / (double) uniformPrior.length);
        IExpectationMaximization EM = hmmNew.emDiscrete(observations, par0, uniformPrior);
        EM.setMaximumNumberOfStep(nIterHMMMax);
        EM.setLikelihoodDecreaseTolerance(maxHMMLInc);

        System.out.println(" running hmm on " + observations.size() + " x " + observations.get(0).size() + " observations with maxIter " + nIterHMMMax);
        EM.run();
        hmmLikelihoodHistory = EM.getLogLikelihoodHistory();
        System.out.println(" hmm iterations: " + hmmLikelihoodHistory.length);
        System.out.println(" likelihood history: " + doubleArrays.toString(hmmLikelihoodHistory, "\n"));

        // HMM
        this.hmmEst = EM.getHMM();
        IHMMParameters parEst = hmmEst.getParameters();

        return parEst;
    }

    private IDoubleArray getChi(IHMMParameters par)
    {
        int nObservableStates = par.getOutputParameters(0).size();
        IDoubleArray res = doublesNew.array(nObservableStates, nhidden);
        for (int i = 0; i < nhidden; i++)
        {
            IDoubleArray pout = par.getOutputParameters(i);
            for (int k = 0; k < nObservableStates; k++)
            {
                res.set(k, i, pout.get(k));
            }
        }
        return res;
    }

    public void estimateMSM()
    {
        // and estimate reversible MSM on tau
        msmC = msm.estimateC(dtraj, tau);

        //System.out.println("C(tau) = " + msmC);

        msmT = msm.estimateTrev(msmC);
        msmpi = msm.stationaryDistribution(msmT);
        msmPi = doublesNew.diag(msmpi);
        msmCorr = alg.product(msmPi, msmT);
        msmTimescales = msm.timescales(msmT, tau);
        System.out.println("MSM timescales: \n" + doubles.subToNew(msmTimescales, 0, Math.min(5, msmTimescales.size())));

        // get chi and coarse-grained T
        IDoubleArray[] cg = msm.coarseGrain(msmT, nhidden);
        msmTC = cg[0];
        msmChi = cg[1];

        System.out.println("PCCA chi: ");
        System.out.println(msmChi);
        System.out.println("PCCA TC: ");
        System.out.println(msmTC);
        System.out.println("PCCA TC timescales: ");
        System.out.println(msm.timescales(msmTC, tau));
    }

    public void estimateHMM()
            throws ParameterEstimationException
    {
        // coarse grain trajectory to tau and run HMM
        //List<IDataSequence> dtrajCompressed = compressTrajectory(dtraj);
        List<IIntArray> dTrajectories = subsamples(dtraj, timeshift);
        int nstates = intseq.max(dTrajectories) + 1;
        List<IDataSequence> dtrajCompressed = toObservation(dTrajectories, nstates);

        IHMMParameters hmmParameters = hmm(dtrajCompressed, initTC, initChi);
        hmmTC = hmmParameters.getTransitionMatrix();
        hmmpiC = msm.stationaryDistribution(hmmTC);
        hmmChi = getChi(hmmParameters);

        // estimate and output timescales
        hmmTimescales = msm.timescales(hmmTC, tau);
    }

    public void estimate()
            throws ParameterEstimationException
    {
        // test input
        if (nhidden < 0)
        {
            throw new IllegalArgumentException("ABORTING NINJA: number of hidden states not yet set.");
        }
        if (tau < 0)
        {
            throw new IllegalArgumentException("ABORTING NINJA: tau not yet set.");
        }
        if (timeshift < 0)
        {
            throw new IllegalArgumentException("ABORTING NINJA: time shift not yet set.");
        }

        System.out.println("====================================================");
        System.out.println(" Running Ninja estimation with tau = "+tau);
        System.out.println("====================================================");
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println(" MSM reference estimation");
        estimateMSM();

        if (initChi == null && initTC == null)
        {
            initChi = msmChi;
            initTC = msmTC;
            System.out.println("HMM initialization: Using PCCA results");
        }
        else
        {
            System.out.println("HMM initialization: Using user-defined matrices:");
            System.out.println(" init chi: ");
            System.out.println(initChi);
            System.out.println(" init TC: ");
            System.out.println(initTC);
        }
        System.out.println("----------------------------------------------------");
        System.out.println();

        System.out.println("----------------------------------------------------");
        System.out.println(" HMM estimation");

        estimateHMM();

        // print chi:
        System.out.println("chi: \n" + hmmChi);

        // print TC:
        System.out.println("TC: \n" + hmmTC);

        System.out.println("HMM timescales: \n" + hmmTimescales);

        System.out.println("----------------------------------------------------");
        System.out.println();
    }

    ////////////////////////////////////////////////////////////////////////////
    // EM methods
    ////////////////////////////////////////////////////////////////////////////
    
    @Override
    public void setInitialParameters(IHMMParameters par)
    {
        throw new UnsupportedOperationException("Not supported. Initialization is done by MSM");
    }

    @Override
    public void setInitialPaths(List<IIntArray> paths)
    {
        throw new UnsupportedOperationException("Not supported. Initialization is done by MSM");
    }

    @Override
    public void setMaximumNumberOfStep(int nsteps)
    {
        this.nIterHMMMax = nsteps;
    }

    @Override
    public void setLikelihoodDecreaseTolerance(double _dectol)
    {
        this.maxHMMLInc = _dectol;
    }

    @Override
    public double[] getLogLikelihoodHistory()
    {
        return this.hmmLikelihoodHistory;
    }

    @Override
    public void run() throws ParameterEstimationException
    {
        this.estimate();
    }

    @Override
    public IHMM getHMM()
    {
        return this.hmmEst;
    }
}
