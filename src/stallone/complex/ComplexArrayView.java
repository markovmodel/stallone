/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.complex;

import stallone.api.complex.IComplexArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.doubles.ArrayIndexMap;
import stallone.doubles.IArrayIndexMap;

/**
 *
 * @author noe
 */
public class ComplexArrayView
    extends AbstractComplexArray
//    implements IComplexArray
{
    protected IComplexArray carr;
    protected IArrayIndexMap map;

    /**
     * Generates a view to the data using the window top,left (inclusive) to bottom,right (exclusive)
     */
    public ComplexArrayView(IComplexArray _data, int _top, int _left, int _bottom, int _right)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, _top, _left, _bottom, _right);
    }

    /**
     * Costructor for order 1 array views
     * @param _data
     * @param _from
     * @param _to
     */
    public ComplexArrayView(IComplexArray _data, int _from, int _to)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, _from, _to);
    }

    public ComplexArrayView(IComplexArray _data, int[] rowIndexes, int[] colIndexes)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, Ints.create.arrayFrom(rowIndexes), Ints.create.arrayFrom(colIndexes));
    }

    public ComplexArrayView(IComplexArray _data, IIntArray rowIndexes, IIntArray colIndexes)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, rowIndexes, colIndexes);
    }

    public ComplexArrayView(IComplexArray _data, int[] indexes)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, Ints.create.arrayFrom(indexes));
    }

    public ComplexArrayView(IComplexArray _data, IIntArray indexes)
    {
        this.carr = _data;
        this.map = ArrayIndexMap.createMap(_data, indexes);
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
    public boolean isReal()
    {
        return(carr.isReal());
    }

    @Override
    public double getRe(int i, int j)
    {
        return (carr.getRe(map.getRow(i), map.getColumn(j)));
    }

    @Override
    public void setRe(int i, int j, double x)
    {
        carr.setRe(map.getRow(i), map.getColumn(j), x);
    }

    @Override
    public double getIm(int i, int j)
    {
        return (carr.getIm(map.getRow(i), map.getColumn(j)));
    }

    @Override
    public void setIm(int i, int j, double x)
    {
        carr.setIm(map.getRow(i), map.getColumn(j), x);
    }

    @Override
    public IComplexArray copy()
    {
        IComplexArray res = create(map.rows(), map.columns());
        copyInto(res);
        return res;
    }

    @Override
    public IComplexArray create(int size)
    {
        return(carr.create(size));
    }

    @Override
    public IComplexArray create(int rows, int cols)
    {
        return(carr.create(rows,cols));
    }

    @Override
    public boolean isSparse()
    {
        return carr.isSparse();
    }
}
