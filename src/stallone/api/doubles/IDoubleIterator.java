/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import java.util.Iterator;

/**
 *
 * @author noe
 */
public interface IDoubleIterator extends Iterator<IDoubleElement>
{
    /**
     * Resets the index to the first element (first nonzero element if this is a nonzero iterator).
     */
    public void reset();

    /**
     * Goes to the next value. Does not return anything. You have to get the content with get().
     * Usage Example:
     *
     * for (IDoubleArrayIterator it = arr.iterator(); it.hasNext(); it.advance())
     * {
     *      System.out.println("current element: " + it.get());
     * }
     */
    public void advance();

    /**
     * Returns the current index. Good to know if this is a sparse vector iterator!
     * @return
     */
    public int getIndex();

    /**
     * Returns the current row index. Good to know if this is a sparse vector iterator!
     * @return
     */
    public int row();

    /**
     * Returns the current column index. Good to know if this is a sparse vector iterator!
     * @return
     */
    public int column();

    /**
     * Returns the current value
     * @return
     */
    public double get();

    /**
     * Sets the current value
     */
    public void set(double x);

    /**
     * Returns whether the current index is still in the array bounds
     * @return 
     */
    @Override
    public boolean hasNext();

    /**
     * Returns the current element and then advances the index.
     * Usage Example:
     *
     * for (IDoubleArrayIterator it = arr.iterator(); it.hasNext();)
     * {
     *      System.out.println("current element: " + it.next());
     * }
     * 
     * @return 
     */
    @Override
    public IDoubleElement next();
}
