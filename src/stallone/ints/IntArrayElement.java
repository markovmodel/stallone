/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntElement;

/**
 *
 * @author noe
 */
public class IntArrayElement implements IIntElement
{
    private IIntArray arr;
    private int index;

    public IntArrayElement(IIntArray _arr)
    {
        this.arr = _arr;
    }

    @Override
    public int index()
    {
        return(index);
    }

    public void setIndex(int _index)
    {
        this.index = _index;
    }

    @Override
    public int get()
    {
        return(arr.get(index));
    }

    @Override
    public void set(int value)
    {
        arr.set(index,value);
    }

    @Override
    public int row()
    {
        return(index);
    }

    @Override
    public int column()
    {
        return(0);
    }

}
