/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.stat.IParameterEstimator;
import stallone.xxx_hmmtest.BinnedFretEfficiencyOutputModel_tmp;

/**
 *
 * @author noe
 */
public class DiscreteDistribution implements IParametricFunction, IParameterEstimator
{
    private double[] priorCount; // prior count to be used when estimating from counts
    private double[] p, pinc;
    private double weight; // total weight in the present estimate p;

    public DiscreteDistribution(double[] _p)
    {
        p = _p;
        pinc = new double[_p.length];
        priorCount = new double[_p.length];
        weight = 0;
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
        weight = 1;
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
        System.out.println("making an unweighted estimate");
        p = doubleArrays.copy(priorCount);
        weight = doubleArrays.sum(priorCount);
        
        if (data.dimension() == p.length)
        {
            for (IDoubleArray arr : data)
            {
                doubleArrays.increment(p, arr.getArray());
                weight += doubleArrays.sum(p);
            }
        }
        else
        {
            if (data.dimension() == 1)
            {
                for (IDoubleArray arr : data)
                {
                    p[(int)arr.get(0)] += 1.0;
                    weight += 1.0;
                }
            }
            else
                throw new IllegalArgumentException("incompatible dimension of observation");
        }
        
        p = doubleArrays.multiply(1.0/doubleArrays.sum(p), p);
        updateInc();
        return doublesNew.arrayFrom(p);
    }

    @Override
    public IDoubleArray estimate(IDataSequence data, IDoubleArray weights)
    {
        /*
        System.out.println("making a weighted estimate with weights "+weights.get(1000)+"....");
        p = doubleArrays.copy(priorCount);
        weight = doubleArrays.sum(priorCount);
        System.out.println("initial p = "+doubleArrays.toString(p,", "));
        System.out.println("weight = "+weight);
       */
        if (data.dimension() == p.length)
        {
            for (int i=0; i<data.size(); i++)
            {
                IDoubleArray arr = data.get(i);
                double w = weights.get(i);
                for (int j=0; j<p.length; j++)
                {
                    p[j] += w*arr.get(j);
                    weight += w;
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
                    p[(int)arr.get(0)] += weights.get(i);
                    weight += weights.get(i);
                }
            }
            else
                throw new IllegalArgumentException("incompatible dimension of observation");
        }

        
        //System.out.println("estimate before normalization: "+doubleArrays.toString(p,", "));
        
        p = doubleArrays.multiply(1.0/doubleArrays.sum(p), p);
        //System.out.println("estimate after normalization: "+doubleArrays.toString(p,", "));
        updateInc();
        //System.out.println("estimate after inc: "+doubleArrays.toString(p,", "));
        return doublesNew.arrayFrom(p);
    }

    @Override
    public void initialize()
    {
        p = doubleArrays.copy(priorCount);
        p = doubleArrays.multiply(1.0/doubleArrays.sum(p), p);        
        updateInc();
        weight = doubleArrays.sum(priorCount);
    }

    @Override
    public void initialize(IDoubleArray initPar)
    {
        p = initPar.getArray();
        updateInc();
        weight = 1.0;
    }

    @Override
    public void addToEstimate(IDataSequence data)
    {
        double[] oldEstimate = doubleArrays.copy(p);
        double oldWeight = weight;

        estimate(data);

        // update p
        p = doubleArrays.addWeighted(oldWeight, oldEstimate, weight, p);
        // remove prior once to avoid double-counting
        p = doubleArrays.subtract(p, priorCount);

        // renormalize
        p = doubleArrays.multiply(1.0/doubleArrays.sum(p), p);        
        weight += oldWeight;
    }

    @Override
    public void addToEstimate(IDataSequence data, IDoubleArray weights)
    {
        double[] oldEstimate = doubleArrays.copy(p);
        double oldWeight = weight;

        estimate(data, weights);

        p = doubleArrays.addWeighted(oldWeight, oldEstimate, weight, p);
        // remove prior once to avoid double-counting
        p = doubleArrays.subtract(p, priorCount);

        // renormalize
        p = doubleArrays.multiply(1.0/doubleArrays.sum(p), p);        
        weight += oldWeight;
    }

    @Override
    public IDoubleArray getEstimate()
    {
        return doublesNew.arrayFrom(p);
    }
}
