/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.Ints;
import stallone.api.ints.IIntList;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;

/**
 *
 * @author noe
 */
public class IntArrayList_FastUtilWrapper implements IIntList
{
    private stallone.doubles.fastutils.IntArrayList list;

    private IntArrayList_FastUtilWrapper(stallone.doubles.fastutils.IntArrayList _list)
    {
        this.list = _list;
    }

    public IntArrayList_FastUtilWrapper()
    {
        list = new stallone.doubles.fastutils.IntArrayList();
    }

    public IntArrayList_FastUtilWrapper(final int capacity)
    {
        list = new stallone.doubles.fastutils.IntArrayList(capacity);
    }

    public IntArrayList_FastUtilWrapper(final int[] a)
    {
        list = new stallone.doubles.fastutils.IntArrayList(a);
    }


    @Override
    public void append(int value)
    {
        list.add(value);
    }

    @Override
    public void appendAll(IIntArray values)
    {
        for (int i=0; i<values.size(); i++)
            list.add(values.get(i));
    }

    @Override
    public void insert(int index, int value)
    {
        list.add(index, value);
    }

    @Override
    public void insertAll(int index, IIntArray values)
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
    public void removeByValue(int value)
    {
        list.rem(value);
    }

    @Override
    public void clear()
    {
        list.clear();
    }

    @Override
    public int get(int i)
    {
        return(list.get(i));
    }

    @Override
    public void set(int i, int v)
    {
        list.set(i, v);
    }

    @Override
    public void copyFrom(IIntArray other)
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
    public IIntList copy()
    {
        return(new IntArrayList_FastUtilWrapper((stallone.doubles.fastutils.IntArrayList)list.clone()));
    }

    @Override
    public IIntArray create(int size)
    {
        return(new IntArrayList_FastUtilWrapper(size));
    }

    @Override
    public int[] getArray()
    {
        return(list.toIntArray());
    }

    @Override
    public IIntIterator iterator()
    {
        return(new IntArrayIterator(this));
    }

    @Override
    public String toString()
    {
        return(Ints.util.toString(this));
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
    public int get(int i, int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        return(get(i));
    }

    @Override
    public void set(int i, int j, int x)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        set(i,x);
    }

    @Override
    public int[][] getTable()
    {
        int[][] res = new int[size()][1];
        for (int i=0; i<res.length; i++)
            res[i][0] = get(i);
        return(res);
    }

    @Override
    public int[] getRow(int i)
    {
        return(new int[]{get(i)});
    }

    @Override
    public int[] getColumn(int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        return(getArray());
    }

    @Override
    public IIntIterator nonzeroIterator()
    {
        return(new IntArrayNonzeroIterator(this));
    }

    @Override
    public void copyInto(IIntArray other)
    {
        if (other.size() != size())
            throw (new IllegalArgumentException("Incompatible sizes. This array has size "+size()+", the other array has size "+other.size()));

        Ints.util.copyInto(this, 0, size(), other, 0);
    }

    @Override
    public IIntArray create(int rows, int columns)
    {
        if (columns > 1)
            throw(new IllegalArgumentException("Cannot create a list with more than one column"));

        return(new IntArrayList_FastUtilWrapper(rows));
    }

    @Override
    public IIntArray viewRow(int i)
    {
        return(new IntArrayView(this, i, 0, i+1, 1));
    }

    @Override
    public IIntArray viewColumn(int j)
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
