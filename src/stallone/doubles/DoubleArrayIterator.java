/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.doubles;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;

/**
 *
 * @author noe
 */
public class DoubleArrayIterator implements IDoubleIterator
{
    protected IDoubleArray x;
    protected int size;
    protected int i=0;
    private DoubleArrayElement o;

    public DoubleArrayIterator(IDoubleArray _x)
    {
        this.x = _x;
        this.size = x.size();
        this.o = new DoubleArrayElement(_x);
    }

    @Override
    public void reset()
    {
        i=0;
    }

    public boolean hasNext()
    {
        return(i<size);
    }

    @Override
    public void advance()
    {
        i++;
    }

    @Override
    public int getIndex()
    {
        return(i);
    }

    @Override
    public double get()
    {
        return(x.get(i));
    }

    @Override
    public void set(double newValue)
    {
        x.set(i, newValue);
    }

    @Override
    public IDoubleElement next()
    {
        o.setIndex(i);
        o.set(x.get(i));
        i++;
        return(o);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public int row()
    {
        return(i);
    }

    @Override
    public int column()
    {
        return(0);
    }


}
