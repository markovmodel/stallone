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
public class SparseVectorNonzeroIterator implements IDoubleIterator
{
    protected IDoubleArray x;
    private SparseVectorIndexMap map;
    protected int nonZeroSize;
    protected int i=0;
    private DoubleArrayElement o;

    public SparseVectorNonzeroIterator(IDoubleArray _x, SparseVectorIndexMap _map)
    {
        this.x = _x;
        map = _map;
        this.nonZeroSize = map.usedNonZero;
        this.o = new DoubleArrayElement(_x);
    }

    @Override
    public void reset()
    {
        i=0;
    }

    public boolean hasNext()
    {
        return(i<nonZeroSize);
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
        return(map.nonZeroIndices[i]);
    }

    /**
     * Returns the current value
     * @return
     */
    @Override
    public double get()
    {
        //System.out.println(" getting "+i+" "+map.nonZeroIndices[i]+" "+x.get(map.nonZeroIndices[i]));
        return(x.get(map.nonZeroIndices[i]));
    }

    /**
     * Sets the current value
     */
    @Override
    public void set(double newValue)
    {
        x.set(map.nonZeroIndices[i], newValue);
    }

    @Override
    public IDoubleElement next()
    {
        //System.out.println(i+" "+map.nonZeroIndices[i]+" "+x.get(map.nonZeroIndices[i]));
        o.setIndex(getIndex());
        double y = get();
        //System.out.println(" setting "+y);
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
        if (x.rows() == 1)
            return 0;
        else
            return(getIndex());
    }

    @Override
    public int column()
    {
        if (x.columns() == 1)
            return 0;
        else
            return(getIndex());
    }


}
