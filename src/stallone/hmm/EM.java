/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import java.util.List;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.hmm.*;
import stallone.api.ints.IIntArray;
import stallone.api.io.IO;
import stallone.api.mc.MarkovModel;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class EM implements IExpectationMaximization, IHMM
{
    private List<IDataSequence> obs;
    private HMMForwardModel model;
    private HMMCountMatrixEstimator countMatrixEstimator;
    private IParameterEstimator[] outputModelEstimators;
    private ForwardBackward trajEstimator;
    double  logLikelihood = Double.NEGATIVE_INFINITY;

    // initial hidden path, if set
    private List<IIntArray> initPaths;

    // hidden variables are stored when saveMemory mode is off
    private HMMHiddenVariables[] hidden = null;
    private boolean saveMemory = false;

    // optimization parameters
    private int nStepsMax = 1;
    private double dectol = 0.1;

    // results
    private double[] likelihoods;

    /**
     * @param _obs the observation data
     * @param eventBased if the data is stored event-based (irregular time intervals)
     * @param initialParameters the initial HMM parameters
     * @param _fOut the output model
     * @param _outputModelEstimator the estimator for the output model
     * @param _saveMemory if the hidden trajectories should be always constructed on the fly.
     */
    public EM(List<IDataSequence> _obs, boolean eventBased, int nstates, boolean reversible,
              IParametricFunction _fOut, IParameterEstimator _outputModelEstimator,
              boolean _saveMemory)
    {
        if (_obs == null)
            throw new IllegalArgumentException("Observation is "+null);
        if (_obs.size() == 0)
            throw new IllegalArgumentException("Observation has zero Elements");

        this.obs = _obs;
        this.model = new HMMForwardModel(_obs, eventBased, nstates, reversible, _fOut);
        this.saveMemory = _saveMemory;

        // init E-estimators
        trajEstimator = new ForwardBackward(model);
        if (saveMemory)
        {
            hidden = new HMMHiddenVariables[1];
            int maxsize = 0;
            for (int i = 0; i < hidden.length; i++)
            {
                if (obs.get(i).size() > maxsize)
                {
                    maxsize = obs.get(i).size();
                }
            }
            hidden[0] = new HMMHiddenVariables(maxsize, model.getNStates());
        }
        else
        {
            hidden = new HMMHiddenVariables[obs.size()];
            for (int i = 0; i < hidden.length; i++)
            {
                hidden[i] = new HMMHiddenVariables(obs.get(i).size(), model.getNStates());
            }
        }


        // init M-estimators
        this.countMatrixEstimator = new HMMCountMatrixEstimator(eventBased, model);
        this.outputModelEstimators = new IParameterEstimator[model.getNStates()];
        for (int i = 0; i < outputModelEstimators.length; i++)
        {
            outputModelEstimators[i] = _outputModelEstimator.copy();
        }
    }

    /**
     * Initializes the EM by providing the initial parameter set.
     * @param _par
     */
    public void setInitialParameters(IHMMParameters _par)
    {
        model.setParameters(_par);
    }

    @Override
    public void setInitialPaths(List<IIntArray> _initPaths)
    {
        this.initPaths = _initPaths;
        // set initial counts to make sure Baum-Welch works
        model.setTransitionCounts(MarkovModel.util.estimateC(initPaths, 1));
    }

    /**
     * If false, all hidden pathways will be kept in memory. This is the faster
     * option If true, each hidden pathway will be regenerated whenever needed.
     * This is the memory-saving option.
     *
     * @param sm
     */
    public void setSaveMemory(boolean sm)
    {
        saveMemory = sm;
    }

    /**
     * @param mode MODE_MAXPATH = 1, MODE_VITERBI = 2, MODE_BAUMWELCH = 3;
     * default is: MODE_BAUMWELCH
     */
    public void setCountMode(int mode)
    {
        this.countMatrixEstimator.setCountMode(mode);
    }

    /**
     * Sets the number of EM steps after which the algorithm terminates
     * @param nsteps
     */
    @Override
    public void setMaximumNumberOfStep(int nsteps)
    {
        this.nStepsMax = nsteps;
    }

    /**
     * Sets the maximum admissible decrease of the likelihood over the previous maximum after which the optimization still continues.
     * @param _dectol
     */
    @Override
    public void setLikelihoodDecreaseTolerance(double _dectol)
    {
        this.dectol = _dectol;
    }

    @Override
    public void run()
            throws ParameterEstimationException
    {
        double[] res = new double[nStepsMax];
        logLikelihood = Double.NEGATIVE_INFINITY;

        // if given initial path, take an M-step first with this initial path

        //System.out.println("initial logL = "+logLikelihood);

        if (initPaths != null)
            emStep(initPaths);

        //System.out.println("after 1 em step logL = "+logLikelihood);

        for (int n = 0; n < nStepsMax; n++)
        {
            res[n] = emStep(null);

            //System.out.println("res[n] logL = "+res[n]+" compare to "+logLikelihood);

            if (res[n] + dectol < logLikelihood)
            {
                System.out.println(" next likelihood = " + res[n] + " exiting.");
                likelihoods = DoublesPrimitive.util.subarray(res, 0, n);
                return;
            }
            if (res[n] > logLikelihood)
            {
                logLikelihood = res[n];
            }

            if (Double.isNaN(res[n]))
            {
                System.out.println("NaN in likelihood from E-Step");
                java.util.Arrays.fill(res, Double.NEGATIVE_INFINITY);
                likelihoods = res;
                return;
            }

            /*
            System.out.println(" C = "+C);
            System.out.println(" T = "+model.getParameters().getTransitionMatrix());
            System.out.println(" pi = "+model.getParameters().getInitialDistribution());
            */
        }

        this.likelihoods = res;

    }

    /**
     *
     * @param initPaths Set to the initial pathway (E-step will be skipped) or to null (E-step will be computed)
     * @return
     * @throws ParameterEstimationException
     */
    private double emStep(List<IIntArray> _initPaths)
            throws ParameterEstimationException
    {
        double res = 0;

        // Initialize maximization step
        //IHMMParameters curpar = par.copy(); // working parameters
        countMatrixEstimator.initialize();
        for (int s = 0; s < outputModelEstimators.length; s++)
        {
            outputModelEstimators[s].initialize();
        }

        for (int i = 0; i < obs.size(); i++)
        {
            // Estimation step
            HMMHiddenVariables hiddenCur = null;
            if (saveMemory)
            {
                hidden[0].setLength(obs.get(i).size());
                hiddenCur = hidden[0];
            }
            else
            {
                hiddenCur = hidden[i];
            }

            // Calculate Hidden Variables. Either using the E-step or from the path provided.
            if (_initPaths == null)
            {
                trajEstimator.computePath(i, hiddenCur);
            }
            else
            {
                if (model.isEventBased())
                {
                    hiddenCur.setPath(obs.get(i), _initPaths.get(i));
                }
                else
                {
                    hiddenCur.setPath(_initPaths.get(i));
                }
            }

            // calculate likelihood
            res += hiddenCur.logLikelihood();
            if (Double.isNaN(res))
            {
                break;
            }

            // Update maximization step
            countMatrixEstimator.addToEstimate(obs.get(i), i, hiddenCur);
            for (int s = 0; s < outputModelEstimators.length; s++)
            {
                outputModelEstimators[s].addToEstimate(obs.get(i), hiddenCur.getGammaByState(s));
            }
        }

        // Complete M-Step
        if (!((res < logLikelihood - dectol) || Double.isNaN(res)))
        {
            IDoubleArray C = countMatrixEstimator.getEstimate();
            this.model.setTransitionCounts(C);
            for (int s = 0; s < outputModelEstimators.length; s++)
            {
                this.model.setOutputParameters(s, outputModelEstimators[s].getEstimate());
            }
        }

        return res;
    }

    @Override
    public IHMMHiddenVariables getHidden(int itraj)
    {
        if (saveMemory)
        {
            hidden[0].setLength(obs.get(itraj).size());
            try
            {
                trajEstimator.computePath(itraj, hidden[0]);
            } catch (ParameterEstimationException ex)
            {
                IO.util.error(ex.getMessage());
            }
            return hidden[0];
        }
        else
        {
            return (hidden[itraj]);
        }
    }

    @Override
    public IHMMParameters getParameters()
    {
        return model.getParameters();
    }

    @Override
    public double getLogLikelihood()
    {
        return logLikelihood;
    }

    @Override
    public List<IIntArray> viterbi()
    {
        Viterbi v = new Viterbi(model);
        return v.getPaths();
    }

    @Override
    public IHMM getHMM()
    {
        return this;
    }

    @Override
    public double[] getLogLikelihoodHistory()
    {
        return likelihoods;
    }

    @Override
    public IDoubleArray getTransitionMatrix()
    {
        return model.getParameters().getTransitionMatrix();
    }
}
