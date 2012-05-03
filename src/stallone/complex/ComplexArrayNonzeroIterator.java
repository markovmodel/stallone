/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.complex;

import stallone.api.complex.IComplexArray;

/**
 *
 * @author noe
 */
public class ComplexArrayNonzeroIterator extends ComplexArrayIterator
{

    public ComplexArrayNonzeroIterator(IComplexArray _x)
    {
        super(_x);

        proceedToNonzero();
    }

    @Override
    public void reset()
    {
        proceedToNonzero();
    }

    private void proceedToNonzero()
    {
        if (hasNext())
        {
            while (hasNext() && X.get(i, j) == 0)
            {
                advance();
            }
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
