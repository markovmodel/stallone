/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.doubles;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DoubleArrayNonzeroIterator extends DoubleArrayIterator
{
    public DoubleArrayNonzeroIterator(IDoubleArray _x)
    {
        super(_x);
        goToFirstNonzero();
    }

    private void goToFirstNonzero()
    {
        // set i to the next nonzero index
        for (i = 0; i<size; i++)
            if (x.get(i) != 0)
                break;
    }

    @Override
    public void reset()
    {
        goToFirstNonzero();
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
        for (; i<size; i++)
            if (x.get(i) != 0)
                break;
    }

}
