/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;

/**
 *
 * @author noe
 */
public class ArrayIndexMap
{
    public static IArrayIndexMap createMap(IDoubleArray data, int _top, int _left, int _bottom, int _right)
    {
        return new ArrayBlockIndexMap(data, _top, _left, _bottom, _right);
    }

    public static IArrayIndexMap createMap(IDoubleArray data, int _from, int _to)
    {
        return new ArrayBlockIndexMap(data, _from, _to);
    }

    public static IArrayIndexMap createMap(IDoubleArray data, IIntArray _Irows, IIntArray _Icols)
    {
        return new ArraySelectedIndexMap(data, _Irows, _Icols);
    }

    public static IArrayIndexMap createMap(IDoubleArray data, IIntArray _Iindexes)
    {
        return new ArraySelectedIndexMap(data, _Iindexes);
    }

}

class ArrayBlockIndexMap implements IArrayIndexMap
{
    private int left, top, right, bottom;
    private int nrows, ncols, size;

    public ArrayBlockIndexMap(IDoubleArray data, int _top, int _left, int _bottom, int _right)
    {
        this.left = _left;
        this.top = _top;
        this.right = _right;
        this.bottom = _bottom;

        this.nrows = _bottom - _top;
        this.ncols = _right - _left;
        this.size = nrows * ncols;
    }

    public ArrayBlockIndexMap(IDoubleArray data, int _from, int _to)
    {
        if (data.order() > 1)
        {
            throw (new IllegalArgumentException("Cannot use order-1 array view constructor for a table"));
        }

        if (data.columns() == 1)
        {
            this.top = _from;
            this.bottom = _to;
            this.left = 0;
            this.right = 1;

            this.nrows = _to - _from;
            this.ncols = 1;
            this.size = nrows;
        }
        if (data.rows() == 1)
        {
            this.left = _from;
            this.right = _to;
            this.top = 0;
            this.bottom = 1;

            this.nrows = 1;
            this.ncols = _to - _from;
            this.size = ncols;
        }
    }

    @Override
    public int getRow(int row)
    {
        return (row + top);
    }

    @Override
    public int getColumn(int column)
    {
        return (column + left);
    }

    public int rows()
    {
        return(nrows);
    }

    public int columns()
    {
        return(ncols);
    }

    public int size()
    {
        return(size);
    }
}

class ArraySelectedIndexMap implements IArrayIndexMap
{

    private IDoubleArray data;
    private IIntArray Irows, Icols;
    private int nrows, ncols, size;

    public ArraySelectedIndexMap(IDoubleArray data, IIntArray _Irows, IIntArray _Icols)
    {
        this.Irows = _Irows;
        this.Icols = _Icols;

        nrows = Irows.size();
        ncols = Icols.size();
        size = nrows*ncols;
    }

    public ArraySelectedIndexMap(IDoubleArray data, IIntArray _Iindexes)
    {
        if (data.order() > 1)
        {
            throw (new IllegalArgumentException("Cannot use order-1 array view constructor for a table"));
        }

        if (data.columns() == 1)
        {
        this.Irows = _Iindexes;
        nrows = Irows.size();
        this.Icols = Ints.create.arrayFrom(0);
        ncols = 1;
        }
        if (data.rows() == 1)
        {
        this.Icols = _Iindexes;
        ncols = Icols.size();
        this.Irows = Ints.create.arrayFrom(0);
        nrows = 1;
        }

        size = nrows*ncols;
    }

    @Override
    public int getRow(int row)
    {
        return(Irows.get(row));
    }

    @Override
    public int getColumn(int column)
    {
        return(Icols.get(column));
    }
    public int rows()
    {
        return(nrows);
    }

    public int columns()
    {
        return(ncols);
    }

    public int size()
    {
        return(size);
    }
}