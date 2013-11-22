/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleElement;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DoubleArrayElement implements IDoubleElement
{
    private IDoubleArray arr;
    private int index;

    public DoubleArrayElement(IDoubleArray _arr)
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
    public double get()
    {
        return(arr.get(index));
    }

    @Override
    public void set(double value)
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
