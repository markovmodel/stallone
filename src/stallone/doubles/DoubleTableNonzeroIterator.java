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
public class DoubleTableNonzeroIterator extends DoubleTableIterator
{

    public DoubleTableNonzeroIterator(IDoubleArray _x)
    {
        super(_x);
        proceedToNonzero();
    }

    @Override
    public void reset()
    {
        super.reset();
        proceedToNonzero();
    }

    private void proceedToNonzero()
    {
        while (hasNext() && X.get(i, j) == 0)
        {
            super.advance();
        }
    }

    /**
     * Goes to the next value. Does not return anything. You have to get the
     * content with get(). Usage Example:
     *
     * for (IDoubleArrayIterator it = arr.iterator(); it.hasNext(); it.next) {
     * System.out.println("current element: " + it.get()); }
     */
    @Override
    public void advance()
    {
        super.advance();
        proceedToNonzero();
    }
}
