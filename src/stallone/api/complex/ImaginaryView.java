/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.complex;

import stallone.api.doubles.IDoubleArray;
import stallone.doubles.AbstractDoubleArray;

/**
 *
 * @author noe
 */
public class ImaginaryView extends AbstractDoubleArray
{
    IComplexArray arr;
    
    public ImaginaryView(IComplexArray _arr)
    {
        this.arr = _arr;
    }

    @Override
    public int size()
    {
        return arr.size();
    }

    @Override
    public int order()
    {
        return arr.order();
    }

    @Override
    public int rows()
    {
        return arr.rows();
    }

    @Override
    public int columns()
    {
        return arr.columns();
    }

    @Override
    public double get(int i)
    {
        return arr.getIm(i);
    }

    @Override
    public double get(int i, int j)
    {
        return arr.getIm(i,j);
    }

    @Override
    public void set(int i, double x)
    {
        arr.setIm(i,x);
    }

    @Override
    public void set(int i, int j, double x)
    {
        arr.setIm(i,j,x);
    }

    @Override
    public void zero()
    {
        for (IComplexIterator it = arr.nonzeroComplexIterator(); it.hasNext(); it.advance())
            it.setIm(0);
    }

    @Override
    public double[] getArray()
    {
        return arr.getImaginaryArray();
    }

    @Override
    public double[][] getTable()
    {
        return arr.getImaginaryTable();
    }

    @Override
    public double[] getRow(int i)
    {
        return arr.getImaginaryRow(i);
    }

    @Override
    public double[] getColumn(int j)
    {
        return arr.getImaginaryColumn(j);
    }

    @Override
    public IDoubleArray copy()
    {
        return new ImaginaryView(arr.copy());
    }

    @Override
    public IDoubleArray create(int size)
    {
        return new ImaginaryView(arr.create(size));
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        return new ImaginaryView(arr.create(rows, columns));
    }

    @Override
    public boolean isSparse()
    {
        return arr.isSparse();
    }


}
