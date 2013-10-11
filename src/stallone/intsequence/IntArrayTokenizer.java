/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.intsequence;

import stallone.api.ints.IIntArray;

/**
 *
 * Splits int arrays into contiguous pieces of the same int
 * 
 * @author noe
 */
public class IntArrayTokenizer
{
    private IIntArray arr;
    private int pos;
    private int start=0, end=1;
    
    public IntArrayTokenizer(IIntArray _arr)
    {
        this.arr = _arr;
        
        this.advance();
    }
    
    public boolean hasMoreTokens()
    {
        return (end < arr.size());
    }
    
    public boolean advance()
    {
        if (end < arr.size())
        {
            // set new start point
            start = end;
            // advance end
            for (end = start+1; end < arr.size() && arr.get(start)==arr.get(end); end++);
            
            return true;
        }
        else
        {
            start = -1;
            end = -1;
            
            return false;
        }
    }
    
    public int getStart()
    {
        return start;
    }
    
    public int getEnd()
    {
        return end;
    }
    
    public int getState()
    {
        return arr.get(start);
    }
}
