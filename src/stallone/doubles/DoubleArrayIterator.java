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

    /**
     * Goes to the next value. Does not return anything. You have to get the content with get().
     * Usage Example:
     *
     * for (IDoubleArrayIterator it = arr.iterator(); it.hasNext(); it.next)
     * {
     *      System.out.println("current element: " + it.get());
     * }
     */
    @Override
    public void advance()
    {
        i++;
    }

    /**
     * Returns the current index. Good to know if this is a sparse vector iterator!
     * @return
     */
    @Override
    public int getIndex()
    {
        return(i);
    }

    /**
     * Returns the current value
     * @return
     */
    @Override
    public double get()
    {
        return(x.get(i));
    }

    /**
     * Sets the current value
     */
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
