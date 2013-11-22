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

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;

/**
 * Class SparseRealMatrix is an implementation based on a number of sparse row vectors.
 *
 * @author  Martin Senne, Frank Noe
 */
public class SparseRealMatrix extends AbstractDoubleArray
{
    private IDoubleArray[] rowVectors;
    private int rows,cols;

    public SparseRealMatrix(final int _rows, final int _cols)
    {
        rows = _rows;
        cols = _cols;
        rowVectors = new SparseRealVector[rows];

        for (int i = 0; i < rows; i++)
        {
            rowVectors[i] = new SparseRealVector(cols);
        }
    }

    private SparseRealMatrix(final SparseRealMatrix source)
    {
        rows = source.rows;
        cols = source.cols;
        this.rowVectors = new SparseRealVector[rows];

        for (int i = 0; i < rows; i++)
        {
            rowVectors[i] = source.rowVectors[i].copy();
        }
    }

    @Override
    public void zero()
    {
        for (int i = 0; i < rows; i++)
        {
            rowVectors[i].zero();
        }
    }

    @Override
    public int rows()
    {
        return(rows);
    }

    @Override
    public int columns()
    {
        return(cols);
    }

    @Override
    public double get(final int i, final int j)
    {
        return rowVectors[i].get(j);
    }


    @Override
    public void set(final int i, final int j, final double real)
    {
        rowVectors[i].set(j, real);
    }


    @Override
    public void copyFrom(final IDoubleArray source)
    {
        zero();

        for (IDoubleIterator it = source.nonzeroIterator(); it.hasNext(); it.advance())
        {
            set(it.row(), it.column(), it.get());
        }
    }

    @Override
    public void copyInto(final IDoubleArray target)
    {
        target.zero();

        for (IDoubleIterator it = nonzeroIterator(); it.hasNext(); it.advance())
        {
            target.set(it.row(), it.column(), it.get());
        }
    }


    @Override
    public IDoubleArray copy()
    {
        return new SparseRealMatrix(this);
    }

    @Override
    public IDoubleArray create(int rows, int cols)
    {
        return (new SparseRealMatrix(rows, cols));
    }

    @Override
    public IDoubleArray create(int size)
    {
        return (new SparseRealMatrix(rows, 1));
    }

    @Override
    public IDoubleArray viewRow(final int r)
    {
        return rowVectors[r];
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

}
