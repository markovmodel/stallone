/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.hmm;

import java.util.List;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.hmm.IHMMParameters;
import stallone.api.mc.ITransitionMatrixEstimator;
import stallone.api.mc.MarkovModel;

/**
 * This is a basic implementation of IHMMParameters that may be used. However,
 * the user can also choose to implement his own IHMMParameters.
 * This implementation contains transition matrix estimation and stationary
 * distribution estimation.
 * @author noe
 */
public class HMMForwardModel implements IHMMForwardModel
{
    private List<IDataSequence> obs;

    private int nstates;
    private boolean eventBased = false;
    private IHMMParameters par;
    private IParametricFunction[] fOut;

    private ITransitionMatrixEstimator Testimator;
    private MatrixPowerCache matrixPower;

    public HMMForwardModel(List<IDataSequence> _obs, boolean _eventBased, int _nstates, boolean reversible, IParametricFunction _fOut)
    {
        obs = _obs;
        this.nstates = _nstates;
        eventBased = _eventBased;
        fOut = new IParametricFunction[nstates];
        for (int i=0; i<fOut.length; i++)
        {
            fOut[i] = _fOut.copy();
        }

        // find out what is the maximum time step and initialized matrix power cache.
        if (eventBased)
        {
            int dtmax = 1;
            for (IDataSequence seq : obs)
                for (int i=0; i<seq.size()-1; i++)
                {
                    int dt = (int)Math.round(seq.getTime(i+1)-seq.getTime(i));
                    if (dt > dtmax)
                        dtmax = dt;
                }
            if (dtmax > 1000)
                dtmax = 1000;
            matrixPower = new MatrixPowerCache(dtmax);
        }

        // construct transition matrix estimator
        if (reversible)
        {
            Testimator = MarkovModel.create.createTransitionMatrixEstimatorRev();
        }
        else
        {
            Testimator = MarkovModel.create.createTransitionMatrixEstimatorNonrev();
        }
    }

    /**
     * Sets the underlying parameter set.
     * @param _par
     */
    public void setParameters(IHMMParameters _par)
    {
        par = _par;
        for (int i=0; i<fOut.length; i++)
        {
            fOut[i].setParameters(_par.getOutputParameters(i));
        }
    }

    /**
     * Creates deep copy
     * @return
     */
    public HMMForwardModel copy()
    {
        HMMForwardModel res = new HMMForwardModel(obs, eventBased, fOut.length, par.isReversible(), fOut[0]);
        res.setParameters(par);
        return res;
    }

    //@Override
    public int getNStates()
    {
        return nstates;
    }

    //@Override
    public int getNObs()
    {
        return obs.size();
    }

    //@Override
    public int getNObs(int traj)
    {
        return obs.get(traj).size();
    }

    public boolean isEventBased()
    {
        return eventBased;
    }

    //@Override
    public double getP0(int traj, int state)
    {
        return par.getInitialDistribution().get(state);
    }

    //@Override
    public double getPtrans(int traj, int timeindex1, int state1, int state2)
    {
        if (!eventBased)
            return(par.getTransitionMatrix().get(state1,state2));
        else
        {
            IDataSequence seq = obs.get(traj);
            int dt = (int)(seq.getTime(timeindex1+1)-seq.getTime(timeindex1));
            return matrixPower.getPowerElement(par.getTransitionMatrix(), dt, state1, state2);
        }
    }

    //@Override
    public double getPout(int traj, int timeindex, int state)
    {
        IDoubleArray x = obs.get(traj).get(timeindex);
        return fOut[state].f(x);
    }

    public IDoubleArray getObs(int traj, int timeindex)
    {
        IDoubleArray x = obs.get(traj).get(timeindex);
        return x;
    }

    //@Override
    public void setTransitionCounts(IDoubleArray C)
    {
        Testimator.setCounts(C);
        Testimator.estimate();
        IDoubleArray T = Testimator.getTransitionMatrix();
        par.setTransitionMatrix(T);
    }

    //@Override
    public void setOutputParameters(int state, IDoubleArray parOut)
    {
        par.setOutputParameters(state, parOut);
        fOut[state].setParameters(parOut);
    }

    //@Override
    public IHMMParameters getParameters()
    {
        return par;
    }

}
