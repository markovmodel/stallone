/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.stat.IDiscreteDistribution;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class DiscreteDistribution implements IParametricFunction, IParameterEstimator, IDiscreteDistribution
{
    private double[] priorCount; // prior count to be used when estimating from counts
    private double[] count; // actual count
    //
    private double[] p, pinc;
    //private double weight; // total weight in the present estimate p;

    public DiscreteDistribution(double[] _p)
    {
        p = _p;
        pinc = new double[_p.length];
        //
        priorCount = new double[_p.length];
        count = new double[_p.length];
        //weight = 0;
        updateInc();
    }

    public DiscreteDistribution(IDoubleArray arr)
    {
        this(arr.getArray());
    }

    public void setPrior(double[] _prior)
    {
        priorCount = _prior;
    }

    private final void updateInc()
    {
        pinc[0] = p[0];
        for (int j = 1; j < pinc.length; j++)
        {
            pinc[j] = pinc[j - 1] + p[j];
        }
    }

    /**
     *
     * @param c current state
     * @return next state
     */
    public int sample()
    {
        double r = Math.random();
        int to = 0;
        for (; to < pinc.length && pinc[to] <= r; to++);
        return (to);
    }

    @Override
    public IDoubleArray getParameters()
    {
        return doublesNew.arrayFrom(p);
    }

    @Override
    public void setParameters(IDoubleArray par)
    {
        for (int i=0; i<p.length; i++)
            p[i] = par.get(i);
        updateInc();
        //weight = 1;
    }

    @Override
    public int getNumberOfVariables()
    {
        return p.length;
    }

    /**
     *
     * @param x observed histogram
     * @return
     */
    @Override
    public double f(double... x)
    {
        if (x.length == p.length)
        {
            double logP = 0;
            for (int i=0; i<x.length; i++)
            {
                if (p[i] == 0)
                {
                    if (x[i] != 0)
                        return 0;
                }
                else
                    logP += x[i] * Math.log(p[i]);
            }
            //System.out.println("p = "+doubleArrays.toString(p,", ")+" --> L ("+doubleArrays.toString(x,", ")+") = "+Math.exp(logP));
            return Math.exp(logP);
        }
        else
        {
            if (x.length == 1)
                return p[(int)x[0]];
            else
                throw new IllegalArgumentException("incompatible input vector");
        }
    }

    @Override
    public double f(IDoubleArray x)
    {
        return f(x.getArray());
    }

    @Override
    public DiscreteDistribution copy()
    {
        DiscreteDistribution dd = new DiscreteDistribution(doubleArrays.copy(p));
        dd.setPrior(doubleArrays.copy(priorCount));
        return dd;
    }

    @Override
    public IDoubleArray estimate(IDataSequence data)
    {
        initialize();
        addToEstimate(data);
        return getEstimate();
    }

    @Override
    public IDoubleArray estimate(IDataSequence data, IDoubleArray weights)
    {
        initialize();
        addToEstimate(data, weights);
        return getEstimate();
    }

    @Override
    public void initialize()
    {
        java.util.Arrays.fill(count, 0);
        updateInc();
    }

    @Override
    public void initialize(IDoubleArray initPar)
    {
        p = initPar.getArray();
        updateInc();
    }

    private void count2p()
    {
        double[] totalcounts = doubleArrays.add(priorCount, count);
        this.p = doubleArrays.multiply(1.0/doubleArrays.sum(totalcounts), totalcounts);
        updateInc();
    }


    @Override
    public void addToEstimate(IDataSequence data)
    {
        if (data.dimension() == p.length)
        {
            for (IDoubleArray arr : data)
            {
                doubleArrays.increment(count, arr.getArray());
            }
        }
        else
        {
            if (data.dimension() == 1)
            {
                for (IDoubleArray arr : data)
                {
                    count[(int)arr.get(0)] += 1.0;
                }
            }
            else
                throw new IllegalArgumentException("incompatible dimension of observation");
        }

        count2p();
    }

    @Override
    public void addToEstimate(IDataSequence data, IDoubleArray weights)
    {
        if (data.dimension() == count.length)
        {
            for (int i=0; i<data.size(); i++)
            {
                IDoubleArray arr = data.get(i);
                double w = weights.get(i);
                for (int j=0; j<count.length; j++)
                {
                    count[j] += w*arr.get(j);
                }
            }
        }
        else
        {
            if (data.dimension() == 1)
            {
                for (int i=0; i<data.size(); i++)
                {
                    IDoubleArray arr = data.get(i);
                    count[(int)arr.get(0)] += weights.get(i);
                }
            }
            else
                throw new IllegalArgumentException("incompatible dimension of observation");
        }


        count2p();
    }

    @Override
    public IDoubleArray getEstimate()
    {
        return doublesNew.arrayFrom(p);
    }
}
