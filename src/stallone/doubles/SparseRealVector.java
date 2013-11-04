/*
 *  File:
 *  System:
 *  Module:
 *  Author:
 *  Copyright:
 *  Source:              $HeadURL: $
 *  Last modified by:    $Author: $
 *  Date:                $Date: $
 *  Version:             $Revision: $
 *  Description:
 *  Preconditions:
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;

/**
 * Real dense matrix implementation based on a one dimensional array.
 *
 * @author Martin Senne, Frank Noe
 */
public class SparseRealVector extends AbstractDoubleArray
{

    private SparseVectorIndexMap sparseIndexMap;
    protected double[] data;

    /**
     * Create empty sparse real vector of given size.
     *
     * @param size of the vector.
     */
    public SparseRealVector(final int size)
    {
        sparseIndexMap = new MyIndexMap(size);
        data = new double[0];
    }

    /**
     * Copy constructor.
     *
     * @param source object to copy all data from (deep).
     */
    protected SparseRealVector(final SparseRealVector source)
    {
        sparseIndexMap = new MyIndexMap(source.size());
        this.data = new double[getNumberOfNonzero()];
        System.arraycopy(source.data, 0, this.data, 0, sparseIndexMap.usedNonZero);
    }

    public final int getNumberOfNonzero()
    {
        return (sparseIndexMap.usedNonZero);
    }

    @Override
    public void zero()
    {
        sparseIndexMap.usedNonZero = 0;
    }

    @Override
    public IDoubleArray copy()
    {
        return new SparseRealVector(this);
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder("( ");

        for (int i = 0; i < size(); i++)
        {
            builder.append(get(i));

            if (i < (size() - 1))
            {
                builder.append(", ");
            }
        }

        builder.append(" )");

        return builder.toString();
    }

    /**
     * Print internal representation of data. Meant for debugging purposes.
     *
     * @return internal representation as string
     */
    public String toStringOfInternalData()
    {
        return SparseVectorIndexMap.toString(sparseIndexMap.nonZeroIndices, " ") + "\n" + SparseVectorIndexMap.toString(data, " ") + "\n" + "Used: " + sparseIndexMap.usedNonZero;
    }


    /*
     * @Override protected Iterator<IScalarOfVector> getNonZeroIterator() {
     * return new SparseNonZeroVectorIterator(); }
     *
     *
     * @Override protected Iterator<IScalarOfVector> getFullIterator() { return
     * new GenericFullVectorIterator(this);
    }
     */
    @Override
    public IDoubleIterator nonzeroIterator()
    {
        return new SparseVectorNonzeroIterator(this, sparseIndexMap);
    }

    @Override
    public IDoubleIterator iterator()
    {
        return new DoubleArrayIterator(this);
    }

    @Override
    public int rows()
    {
        return (sparseIndexMap.size);
    }

    @Override
    public int columns()
    {
        return (1);
    }

    @Override
    public double get(int i, int j)
    {
        if (j != 0)
        {
            throw new ArrayIndexOutOfBoundsException("Invalid index to column vector: " + i + ", " + j);
        }
        int pos = sparseIndexMap.getPosition(i);
        if (pos < 0)
        {
            throw new ArrayIndexOutOfBoundsException("Invalid index to column vector: " + i + ", " + j);
        }

        return (data[pos]);
    }

    @Override
    public void set(int i, int j, double x)
    {
        if (j != 0)
        {
            throw new ArrayIndexOutOfBoundsException("Invalid index to column vector: " + i + ", " + j);
        }
        int pos = sparseIndexMap.getPosition(i);
        if (pos < 0)
        {
            throw new ArrayIndexOutOfBoundsException("Invalid index to column vector: " + i + ", " + j);
        }

        data[pos] = x;
    }

    @Override
    public IDoubleArray create(final int size)
    {
        return (new SparseRealVector(size));
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        if (columns != 1)
        {
            throw new ArrayIndexOutOfBoundsException("I'm a vector and cannot create a matrix with size: " + rows + ", " + columns);
        }

        return (new SparseRealVector(rows));
    }

    private class MyIndexMap extends SparseVectorIndexMap
    {

        public MyIndexMap(final int _size)
        {
            super(_size);
        }

        public MyIndexMap(final SparseVectorIndexMap base)
        {
            super(base);
        }

        @Override
        protected void augmentData(final int newLength, final int firstBlockLength, final int secondBlockLength)
        {
            final double[] newNonZeroData = new double[newLength];
            System.arraycopy(data, 0, newNonZeroData, 0, firstBlockLength);
            System.arraycopy(data, firstBlockLength, newNonZeroData, firstBlockLength + 1, secondBlockLength);
            data = newNonZeroData;
        }

        @Override
        protected void shiftDataRight(final int firstBlockLength, final int secondBlockLength)
        {
            System.arraycopy(data, firstBlockLength, data, firstBlockLength + 1, secondBlockLength);
        }

        @Override
        protected void shiftDataLeft(final int firstBlockLength, final int secondBlockLength)
        {
            System.arraycopy(data, firstBlockLength + 1, data, firstBlockLength, secondBlockLength);
        }
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }
}
