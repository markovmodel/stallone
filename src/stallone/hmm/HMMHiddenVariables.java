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
import stallone.api.ints.IIntArray;
import stallone.api.io.IO;

/**
 *
 * Class for holding the hidden variables of a HMM. Actual size can be smaller than the capacity
 *
 * @author noe
 */
public class HMMHiddenVariables implements IHMMHiddenVariables
{
    private int capacity, length, nstates;
    private double[][] alpha, beta, gamma, pout;
    private double[] alphanorms, betanorms;

    public HMMHiddenVariables(int ntimesteps, int _nstates)
    {
        this.capacity = ntimesteps;
        this.length = ntimesteps;
        this.nstates = _nstates;
        this.alpha = new double[capacity][nstates];
        this.beta = new double[capacity][nstates];
        this.gamma = new double[capacity][nstates];
        this.pout = new double[capacity][nstates];
        this.alphanorms = new double[capacity];
        this.betanorms = new double[capacity];
    }

    @Override
    public String toString()
    {
        StringBuilder strbuf = new StringBuilder();
        strbuf.append(DoublesPrimitive.util.toString(alpha, "\t", "\n")).append("\n\n");
        strbuf.append(DoublesPrimitive.util.toString(beta, "\t", "\n")).append("\n\n");
        strbuf.append(DoublesPrimitive.util.toString(gamma, "\t", "\n")).append("\n\n");
        return (strbuf.toString());
    }

    /**
     * Sets the length to a smaller value than the capacity.
     * @param l
     */
    public void setLength(int l)
    {
        if (l<0 || l>capacity)
            IO.util.error("Trying to set illegal length "+l+" in Hidden Variables. Capacity is "+capacity);

        this.length = l;
    }

    /**
     * Sets the hidden variables on the observed events according to this path
     */
    public void setPath(IDataSequence observation, IIntArray path)
    {
        int L;
        if (observation != null)
        {
            L = observation.size();
        }
        else
        {
            L = path.size();
        }
        setLength(L);

        /*
        System.out.println("L = "+L+" obs size = "+observation.size()+" path length = "+path.size());

        if (L == 7849)
        {
            System.out.println("first: "+observation.getTime(0)+"\t"+observation.get(0));
            System.out.println("first: "+observation.getTime(7848)+"\t"+observation.get(7848));
            System.exit(0);
        }*/

        double x;
        for (int t=0; t<L; t++)
        {
            int ti=t;
            if (observation != null)
                ti = (int)Math.round(observation.getTime(t));

            for (int s=0; s<nstates; s++)
            {
                if (s == path.get(ti))
                {
                    x = 1;
                }
                else
                {
                    x = 0;
                }

                setAlpha(t,s,x);
                setBeta(t,s,x);
                setPout(t,s,x);
            }
        }

        updateGamma();
    }

    public void setPath(IIntArray path)
    {
        setPath(null, path);
    }


    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public int size()
    {
        return length;
    }

    @Override
    public int nStates()
    {
        return nstates;
    }

    private void checkIndex(int t)
    {
        if (t < 0 || t >= capacity)
            IO.util.error("Accessing illegal time index "+t+" in Hidden Variables. We have "+length+" timesteps available.");
        if (t >= length)
            IO.util.error("Accessing illegal time index "+t+" in Hidden Variables. Capacity is "+capacity+", but only "+length+" timesteps are accessible in this trajectory.");
    }

    private void checkIndex(int t, int s)
    {
        if (t < 0 || t >= capacity)
            IO.util.error("Accessing illegal time index "+t+" in Hidden Variables. We have "+length+" timesteps available.");
        if (t >= length)
            IO.util.error("Accessing illegal time index "+t+" in Hidden Variables. Capacity is "+capacity+", but only "+length+" timesteps are accessible in this trajectory.");
        if (s < 0 || s >= nstates)
            IO.util.error("Accessing illegal state index "+s+" in Hidden Variables. We have "+nstates+" states available.");
    }

    public void setAlpha(int t, int s, double x)
    {
        checkIndex(t,s);
        this.alpha[t][s] = x;
    }

    public void addAlpha(int t, int s, double x)
    {
        checkIndex(t,s);
        this.alpha[t][s] += x;
    }

    public void setBeta(int t, int s, double x)
    {
        checkIndex(t,s);
        this.beta[t][s] = x;
    }

    public void addBeta(int t, int s, double x)
    {
        checkIndex(t,s);
        this.beta[t][s] += x;
    }

    public void updateGamma()
    {
        for (int t = 0; t < length; t++)
        {
            for (int i = 0; i < nstates; i++)
            {
                this.gamma[t][i] = alpha[t][i] * beta[t][i];
            }
            gamma[t] = DoublesPrimitive.util.multiply(1.0 / DoublesPrimitive.util.sum(gamma[t]), gamma[t]);
        }
    }

    @Override
    public int[] getMaxPath()
    {
        int[] traj = new int[length];
        for (int t = 0; t < length; t++)
        {
            traj[t] = DoublesPrimitive.util.maxIndex(gamma[t]);
        }
        return (traj);
    }

    public void setPout(int t, int s, double x)
    {
        checkIndex(t,s);
        this.pout[t][s] = x;
    }

    public boolean checkPout(int t)
    {
        if (DoublesPrimitive.util.sum(this.pout[t]) <= 0 || Double.isNaN(DoublesPrimitive.util.sum(this.pout[t])))
        {
            return (false);
        }
        else
        {
            return (true);
        }
    }

    @Override
    public double getAlpha(int t, int s)
    {
        checkIndex(t);
        return (alpha[t][s]);
    }

    public double[] getAlpha(int t)
    {
        checkIndex(t);
        return (alpha[t]);
    }

    public double getAlphaNorm(int t)
    {
        checkIndex(t);
        return (alphanorms[t]);
    }

    @Override
    public double getBeta(int t, int s)
    {
        checkIndex(t,s);
        return (beta[t][s]);
    }

    public double[] getGamma(int t)
    {
        checkIndex(t);
        return (gamma[t]);
    }

    @Override
    public double getGamma(int t, int s)
    {
        checkIndex(t,s);
        return (gamma[t][s]);
    }

    @Override
    public int mostProbableState(int t)
    {
        checkIndex(t);
        return (DoublesPrimitive.util.maxIndex(gamma[t]));
    }

    public double[] getTotalStateCounts()
    {
        double[] res = new double[nstates];
        for (int i = 0; i < nstates; i++)
        {
            for (int t = 0; t<length; t++)
                res[i] += gamma[t][i];
        }
        return (res);
    }

    @Override
    public double getPout(int t, int s)
    {
        checkIndex(t,s);
        return (pout[t][s]);
    }

    public double[] getPout(int t)
    {
        checkIndex(t);
        return (pout[t]);
    }

    public void normalizeAlpha(int t)
    {
        checkIndex(t);
        alphanorms[t] = DoublesPrimitive.util.sum(alpha[t]);
        if (alphanorms[t] == 0 || Double.isNaN(alphanorms[t]))
        {
            throw (new RuntimeException("sum of Alpha Variables 0 or NaN. Cannot estimate pathway:\n"
                    + "t = " + t + "\n"
                    + "a = " + DoublesPrimitive.util.toString(alpha[t], ", ") + " \n"));
        }
        alpha[t] = DoublesPrimitive.util.multiply(1.0 / alphanorms[t], alpha[t]);
    }

    @Override
    public double logLikelihood()
    {
        double L = 0;
        for (int i = 0; i < alphanorms.length; i++)
        {
            L += Math.log(alphanorms[i]);
        }
        return (L);
    }

    public void normalizeBeta(int t)
    {
        checkIndex(t);
        betanorms[t] = DoublesPrimitive.util.sum(beta[t]);
        if (betanorms[t] == 0 || Double.isNaN(betanorms[t]))
        {
            throw (new RuntimeException("sum of Beta Variables 0 or NaN. Cannot estimate pathway"));
        }
        beta[t] = DoublesPrimitive.util.multiply(1.0 / betanorms[t], beta[t]);
    }

    @Override
    public IDoubleArray getGammaByState(int s)
    {
        return Doubles.create.array(DoublesPrimitive.util.getColumn(gamma, s));
    }
}
