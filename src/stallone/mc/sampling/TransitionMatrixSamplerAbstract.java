/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import static stallone.api.API.*;
import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.mc.MarkovModel;

/**
 *
 * @author noe
 */
public abstract class TransitionMatrixSamplerAbstract implements ITransitionMatrixSampler
{
    // transition matrix, posterior, observed counts
    protected IDoubleArray T, C;
    protected double logLikelihood = 0;
    
    public TransitionMatrixSamplerAbstract(IDoubleArray counts)
    {
        this.init(counts);
    }

    public TransitionMatrixSamplerAbstract(IDoubleArray counts, IDoubleArray Tinit)
    {
        this.init(counts, Tinit);
    }
    
    @Override
    public void init(IDoubleArray _C, IDoubleArray Tinit)
    {
        this.C = _C;
        if (Tinit == null)
            this.T = MarkovModel.util.estimateT(eraseNegatives(_C));
        else
            this.T = Tinit;
        this.logLikelihood = MarkovModel.util.logLikelihood(T, C);
    }

    @Override
    public final void init(IDoubleArray _C)
    {
        init(_C, null);
    }
    
    protected static IDoubleArray eraseNegatives(IDoubleArray cin)
    {
        IDoubleArray cout = cin.copy();
        for (IDoubleIterator it = cout.nonzeroIterator(); it.hasNext(); it.advance())
        {
            if (it.get()<0)
                it.set(0);
        }
        return cout;
    }
    
    
    @Override
    public IDoubleArray sample(int steps)
    {
        for (int i = 0; i < steps; i++)
        {
            step();
        }

        this.logLikelihood = MarkovModel.util.logLikelihood(T, C);

        return (T);
    }

    /**
    Checks whether the given element is still within [0,1] or else puts it back to that
    value.
     */
    protected void ensureValidElement(int i, int j)
    {
        if (T.get(i, j) < 0)
        {
            T.set(i, j, 0);
        }
        if (T.get(i, j) > 1)
        {
            T.set(i, j, 1);
        }
    }

    protected boolean isElementValid(int i, int j)
    {
        if (T.get(i, j) < 0)
        {
            return false;
        }
        if (T.get(i, j) > 1)
        {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param mu invariant density
     * @return 
     */
    protected double computeDetailedBalanceError(IDoubleArray mu)
    {
        double err = 0;
        for (int i=0; i<T.rows(); i++)
        {
            for (int j=0; j<T.columns(); j++)
            {
                err += Math.abs(mu.get(i)*T.get(i,j) - mu.get(j)*T.get(j,i));
            }
        }
        return err;
    }
    
    /**
    Makes sure that the row still sums up to 1.
     */
    protected void ensureValidRow(int i)
    {
        IDoubleArray r = T.viewRow(i);
        Algebra.util.scale(1.0 / Doubles.util.sum(r), r);
    }

    protected abstract boolean step();

    @Override
    public double logLikelihood()
    {
        return (logLikelihood);
    }

}
