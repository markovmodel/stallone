/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import stallone.api.ints.IIntElement;

/**
 *
 * @author noe
 */
public class IntTableIterator implements IIntIterator
{
    protected IIntArray X;
    protected int i=0,j=0;
    protected int rows,cols;


    public IntTableIterator(IIntArray _X)
    {
        this.X = _X;
        this.rows = X.rows();
        this.cols = X.columns();
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
        return(i < cols && j < rows);
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
        j++;
        if (j == cols)
        {
            i++;
            j=0;
        }
    }

    @Override
    public int get()
    {
        return(X.get(i,j));
    }

    @Override
    public void set(int newValue)
    {
        X.set(i,j, newValue);
    }

    @Override
    public IIntElement next()
    {
        return(new IntTableElement(X,i,j));
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
