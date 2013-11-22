/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleList;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DoubleArrayList_FastUtilWrapper
    extends AbstractDoubleArray
    implements IDoubleList
{
    private stallone.doubles.fastutils.DoubleArrayList list;

    private DoubleArrayList_FastUtilWrapper(stallone.doubles.fastutils.DoubleArrayList _list)
    {
        this.list = _list;
    }

    public DoubleArrayList_FastUtilWrapper()
    {
        list = new stallone.doubles.fastutils.DoubleArrayList();
    }

    public DoubleArrayList_FastUtilWrapper(final int capacity)
    {
        list = new stallone.doubles.fastutils.DoubleArrayList(capacity);
    }

    public DoubleArrayList_FastUtilWrapper(final double[] a)
    {
        list = new stallone.doubles.fastutils.DoubleArrayList(a);
    }


    @Override
    public void append(double value)
    {
        list.add(value);
    }

    @Override
    public void appendAll(IDoubleArray values)
    {
        for (int i=0; i<values.size(); i++)
            list.add(values.get(i));
    }

    @Override
    public void insert(int index, double value)
    {
        list.add(index, value);
    }

    @Override
    public void insertAll(int index, IDoubleArray values)
    {
        for (int i=0; i<values.size(); i++)
            list.add(index+i, values.get(i));
    }

    @Override
    public void remove(int index)
    {
        list.remove(index);
    }

    @Override
    public void removeRange(int from, int to)
    {
        for (int i=from; i<to; i++)
            list.remove(from);
    }

    @Override
    public void removeByValue(double value)
    {
        list.rem(value);
    }

    @Override
    public double get(int i)
    {
        return(list.get(i));
    }

    @Override
    public void set(int i, double v)
    {
        list.set(i, v);
    }

    @Override
    public void copyFrom(IDoubleArray other)
    {
        list.ensureCapacity(other.size());
        for (int i=0; i<other.size(); i++)
            list.set(i, other.get(i));
    }

    @Override
    public int size()
    {
        return(list.size());
    }

    @Override
    public IDoubleList copy()
    {
        return(new DoubleArrayList_FastUtilWrapper((stallone.doubles.fastutils.DoubleArrayList)list.clone()));
    }

    @Override
    public IDoubleArray create(int size)
    {
        return(new DoubleArrayList_FastUtilWrapper(size));
    }

    @Override
    public double[] getArray()
    {
        return(list.toDoubleArray());
    }

    @Override
    public int order()
    {
        return(1);
    }

    @Override
    public int rows()
    {
        return(size());
    }

    @Override
    public int columns()
    {
        return(1);
    }

    @Override
    public double get(int i, int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        return(get(i));
    }

    @Override
    public void set(int i, int j, double x)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        set(i,x);
    }

    @Override
    public double[][] getTable()
    {
        double[][] res = new double[size()][1];
        for (int i=0; i<res.length; i++)
            res[i][0] = get(i);
        return(res);
    }

    @Override
    public double[] getRow(int i)
    {
        return(new double[]{get(i)});
    }

    @Override
    public double[] getColumn(int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        return(getArray());
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        if (columns > 1)
            throw(new IllegalArgumentException("Cannot create a list with more than one column"));

        return(new DoubleArrayList_FastUtilWrapper(rows));
    }

    @Override
    public IDoubleArray viewRow(int i)
    {
        return(new DoubleArrayView(this, i, 0, i+1, 1));
    }

    @Override
    public IDoubleArray viewColumn(int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a column array. Column index "+j+" does not exist!"));

        return(this);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

}
