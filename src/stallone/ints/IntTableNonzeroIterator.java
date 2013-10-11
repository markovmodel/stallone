/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.ints;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class IntTableNonzeroIterator extends IntTableIterator
{
    int index1=0, size;
    
    public IntTableNonzeroIterator(IIntArray _x)
    {
        super(_x);
        
        this.size = _x.size();

        goToFirstNonzero();
    }
    
    private void goToFirstNonzero()
    {
        index1=0;
        while(index1<size)
        {
            while(X.get(i,j) == 0)
            {
                advance();
            }
        }
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
        while(index1<size)
        {
            while(X.get(i,j) == 0)
            {
                advance();
            }
        }
    }
    
}
