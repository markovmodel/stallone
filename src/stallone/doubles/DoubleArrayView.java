/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.ints.PrimitiveIntArray;

/**
 *
 * @author noe
 */
public class DoubleArrayView extends AbstractDoubleArray
{

    protected IDoubleArray data;
    protected IArrayIndexMap map;

    /**
     * Generates a view to the data using the window top,left (inclusive) to bottom,right (exclusive)
     */
    public DoubleArrayView(IDoubleArray _data, int _top, int _left, int _bottom, int _right)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, _top, _left, _bottom, _right);
    }

    /**
     * Costructor for order 1 array views
     * @param _data
     * @param _from
     * @param _to 
     */
    public DoubleArrayView(IDoubleArray _data, int _from, int _to)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, _from, _to);
    }

    public DoubleArrayView(IDoubleArray _data, int[] rowIndexes, int[] colIndexes)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, new PrimitiveIntArray(rowIndexes), new PrimitiveIntArray(colIndexes));
    }

    public DoubleArrayView(IDoubleArray _data, IIntArray rowIndexes, IIntArray colIndexes)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, rowIndexes, colIndexes);
    }

    public DoubleArrayView(IDoubleArray _data, int[] indexes)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, new PrimitiveIntArray(indexes));
    }

    public DoubleArrayView(IDoubleArray _data, IIntArray indexes)
    {
        this.data = _data;
        this.map = ArrayIndexMap.createMap(data, indexes);
    }

    @Override
    public int rows()
    {
        return (map.rows());
    }

    @Override
    public int columns()
    {
        return (map.columns());
    }

    @Override
    public double get(int i, int j)
    {
        int imap = map.getRow(i);
        int jmap = map.getColumn(j);
        return (data.get(imap, jmap));
    }

    @Override
    public void set(int i, int j, double x)
    {
        data.set(map.getRow(i), map.getColumn(j), x);
    }

    @Override
    public IDoubleArray copy()
    {
        IDoubleArray res = data.create(map.rows(), map.columns());
        copyInto(res);
        return (res);
    }

    @Override
    public IDoubleArray create(int size)
    {
        return (data.create(size));
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        return (data.create(rows, columns));
    }

    @Override
    public boolean isSparse()
    {
        return data.isSparse();
    }
}
