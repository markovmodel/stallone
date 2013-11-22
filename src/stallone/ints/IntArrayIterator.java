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
public class IntArrayIterator implements IIntIterator
{
    protected IIntArray x;
    protected int size;
    protected int i=0;
    private IntArrayElement o;

    public IntArrayIterator(IIntArray _x)
    {
        this.x = _x;
        this.size = x.size();
        this.o = new IntArrayElement(_x);
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
    public int get()
    {
        return(x.get(i));
    }

    /**
     * Sets the current value
     */
    @Override
    public void set(int newValue)
    {
        x.set(i, newValue);
    }

    @Override
    public IIntElement next()
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
