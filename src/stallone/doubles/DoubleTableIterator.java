/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;
import stallone.api.doubles.IDoubleIterator;
import stallone.doubles.DoubleTableElement;

/**
 *
 * @author noe
 */
public class DoubleTableIterator implements IDoubleIterator
{
    protected DoubleTableElement o;
    protected IDoubleArray X;
    protected int i=0,j=0;
    protected int rows,cols;


    public DoubleTableIterator(IDoubleArray _X)
    {
        this.X = _X;
        this.rows = X.rows();
        this.cols = X.columns();
        o = new DoubleTableElement(X,0,0);
    }

    @Override
    public void reset()
    {
        i = 0;
        j = 0;
    }


    @Override
    public boolean hasNext()
    {
        return (i<rows && j < cols);
    }


    @Override
    public int row()
    {
        return(i);
    }

    @Override
    public int column()
    {
        return(j);
    }

    @Override
    public void advance()
    {
        if (j == cols-1)
        {
            i++;
            j=0;
        }
        else
        {
            j++;
        }
    }

    @Override
    public double get()
    {
        return(X.get(i,j));
    }

    @Override
    public void set(double newValue)
    {
        X.set(i,j, newValue);
    }

    @Override
    public IDoubleElement next()
    {
        o.setIndex(i,j);
        advance();
        return o;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public int getIndex()
    {
        return(i*cols+j);
    }
}
