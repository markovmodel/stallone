/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IHMMHiddenVariables;

/**
 *
 * @author noe
 */
public class HMMCountMatrixEstimator
{

    public final static int MODE_MAXPATH = 1, MODE_VITERBI = 2, MODE_BAUMWELCH = 3;
    private int countmode = MODE_BAUMWELCH;
    private boolean countEvents = false;
    private HMMForwardModel model = null;
    private double[][] C = null;

    public HMMCountMatrixEstimator(boolean eventBased, HMMForwardModel _model)
    {
        this.model = _model;
        this.countEvents = eventBased;
        this.C = new double[_model.getNStates()][_model.getNStates()];
    }

        /**
     *
     * @param _countmode modes defined in HMMCountMatrixEstimator
     */
    public void setCountMode(int _countmode)
    {
        if (_countmode == HMMCountMatrixEstimator.MODE_MAXPATH
                || _countmode == HMMCountMatrixEstimator.MODE_VITERBI
                || _countmode == HMMCountMatrixEstimator.MODE_BAUMWELCH)
        {
            this.countmode = _countmode;
        }
        else
            throw(new IllegalArgumentException("non-existing count mode"));
    }

    public void initialize()
    {
        this.C = new double[model.getNStates()][model.getNStates()];
    }

    public void addToEstimate(IDataSequence obs, int itraj, IHMMHiddenVariables hidden)
    {
        if (countmode == MODE_MAXPATH)
        {
            addEstimateMaxPath(obs, hidden);
        }
        else if(countmode == MODE_VITERBI)
        {
            addEstimateViterbi(obs, hidden);
        }
        else if(countmode == MODE_BAUMWELCH)
        {
            addEstimateBaumWelch(obs, itraj, hidden);
        }
        else
            throw(new RuntimeException("Should not be here"));
    }

    private void addEstimateMaxPath(IDataSequence obs, IHMMHiddenVariables hidden)
    {
        for (int t = 0; t < hidden.size() - 1; t++)
        {
            int s1 = hidden.mostProbableState(t);
            int s2 = hidden.mostProbableState(t + 1);
            C[s1][s2] += 1;
        }

        // if event-based counting then add times stayed in each state
        if (countEvents)
        {
            for (int t = 0; t < hidden.size() - 1; t++)
            {
                int s = hidden.mostProbableState(t);
                double dt = obs.getTime(t + 1) - obs.getTime(t);
                C[s][s] += dt - 1;
            }
        }
    }

    private void addEstimateViterbi(IDataSequence obs, IHMMHiddenVariables hidden)
    {
        throw (new RuntimeException("Viterbi is not implemented yet."));
    }

    public double[][] baumWelchTransition(IDataSequence obs, int itraj, int time1, IHMMHiddenVariables hidden)
    {
        double[][] Ct = new double[C.length][C[0].length];

        for (int i = 0; i < hidden.nStates(); i++)
        {
            for (int j = 0; j < hidden.nStates(); j++)
            {
                Ct[i][j] = hidden.getAlpha(time1, i)
                        * model.getPtrans(itraj, time1, i, j)
                        * hidden.getPout(time1 + 1, j)
                        //* model.getPout(par, itraj, time1 + 1, j)
                        * hidden.getBeta(time1 + 1, j);
            }
        }

        double norm = DoublesPrimitive.util.sum(Ct);

        Ct = DoublesPrimitive.util.multiply(1.0 / norm, Ct);
        
        // if event-based counting then add times stayed in each state
        if (countEvents)
        {
                double dt = obs.getTime(time1 + 1) - obs.getTime(time1);
                // add length of dtime to diagonal counts
                double ka = (dt-1)/(2.0);
                double kb = (dt-1)/(2.0);
                for (int i=0; i<Ct.length; i++)
                {
                        Ct[i][i] += ka*hidden.getAlpha(time1,i) + kb*hidden.getBeta(time1+1,i);
                }
        }
                
        return (Ct);
    }

    private void addEstimateBaumWelch(IDataSequence obs, int itraj, IHMMHiddenVariables hidden)
    {
        for (int t = 0; t < hidden.size()-1; t++)
        {
            C = DoublesPrimitive.util.add(C, baumWelchTransition(obs, itraj, t, hidden));
        }
    }

    public IDoubleArray getEstimate()
    {
        return(Doubles.create.array(C));
    }
}
