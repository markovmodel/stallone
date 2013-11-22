/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;
import stallone.api.algebra.*;
import stallone.doubles.AbstractDoubleArray;




/**
 *
 * @author noe
 */
public class PosteriorCountMatrix extends AbstractDoubleArray
{
    private IDoubleArray prior, obs;

    public PosteriorCountMatrix(IDoubleArray _prior, IDoubleArray _obs)
    {
        this.prior = _prior;
        this.obs = _obs;
    }

    @Override
    public double get(int i, int j)
    {
        return(prior.get(i,j)+obs.get(i,j));
    }

    @Override
    public int rows()
    {
        return(obs.rows());
    }

    @Override
    public int columns()
    {
        return(obs.columns());
    }

    @Override
    public void set(int i, int j, double value)
    {
        obs.set(i,j,value);
    }

    @Override
    public void copyFrom(IDoubleArray other)
    {
        int _size = other.size();
        if (_size != size())
            throw(new IllegalArgumentException("Trying to copy from array with different size"));

        for (int i=0; i<_size; i++)
            set(i,other.get(i));
    }

    public void setPrior(IDoubleArray _prior)
    {
        prior = _prior;
    }

    public void setObservation(IDoubleArray _obs)
    {
        obs = _obs;
    }

    @Override
    public void zero()
    {
        obs.zero();
    }

    @Override
    public IDoubleArray copy()
    {
        return(new PosteriorCountMatrix(prior.copy(), obs.copy()));
    }

    @Override
    public void copyInto(IDoubleArray target)
    {
        target.zero();
        for (IDoubleIterator it = this.nonzeroIterator(); it.hasNext(); it.advance())
        {
            target.set(it.row(), it.column(), it.get());
        }
    }

    @Override
    public IDoubleArray create(int rows, int cols)
    {
        return(new PosteriorCountMatrix(prior.create(rows,cols), obs.create(rows,cols)));
    }

    @Override
    public IDoubleArray create(int size)
    {
        throw new UnsupportedOperationException("Linear create not supported for matrix.");
    }


    @Override
    public IDoubleIterator iterator()
    {
        IDoubleArray sum = Algebra.util.add(prior, obs);
        IDoubleIterator itElements = sum.iterator();
        return(new PosteriorCountMatrixIterator(this, itElements));
    }

    @Override
    public IDoubleIterator nonzeroIterator()
    {
        IDoubleArray sum = Algebra.util.add(prior, obs);
        IDoubleIterator itElements = sum.nonzeroIterator();
        return(new PosteriorCountMatrixIterator(this, itElements));
    }

    @Override
    public double[][] getTable()
    {
        double[][] res = prior.getTable();
        for (IDoubleIterator it = obs.nonzeroIterator(); it.hasNext(); it.advance())
            res[it.row()][it.column()] += it.get();
        return(res);
    }

    @Override
    public boolean isSparse()
    {
        return prior.isSparse() && obs.isSparse();
    }
}

class PosteriorCountMatrixIterator implements IDoubleIterator
{
    private PosteriorCountMatrix pcm;
    private IDoubleIterator itElements;

    public PosteriorCountMatrixIterator(PosteriorCountMatrix _pcm, IDoubleIterator _itElements)
    {
        this.pcm = _pcm;
        this.itElements = _itElements;
    }

    @Override
    public void reset()
    {
        itElements.reset();
    }

    @Override
    public boolean hasNext()
    {
        return(itElements.hasNext());
    }

    @Override
    public void advance()
    {
        itElements.advance();
    }

    @Override
    public int row()
    {
        return(itElements.row());
    }

    @Override
    public int column()
    {
        return(itElements.column());
    }

    @Override
    public double get()
    {
        return(pcm.get(itElements.row(), itElements.column()));
    }

    @Override
    public void set(double newValue)
    {
        pcm.set(itElements.row(), itElements.column(), newValue);
    }

    @Override
    public IDoubleElement next()
    {
        return(itElements.next());
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public int getIndex()
    {
        return(itElements.getIndex());
    }
}

