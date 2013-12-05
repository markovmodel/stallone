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

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;
import stallone.api.ints.IIntList;

/**
 * Class SparseRealMatrix is an implementation based on a number of sparse row vectors.
 *
 * @author  Martin Senne, Frank Noe
 */
public class SparseRealMatrix extends AbstractDoubleArray
{
    protected SparseRealVector[] rowVectors;
    protected int rows,cols;

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
            rowVectors[i] = (SparseRealVector)source.rowVectors[i].copy();
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

    @Override
    public IDoubleIterator nonzeroIterator()
    {
        return new SparseRealMatrixNonzeroIterator(this);
    }

}


/**
 *
 * @author noe
 */
class SparseRealMatrixNonzeroIterator implements IDoubleIterator
{
    private SparseRealMatrix M;
    int nrows, ncols;
    private DoubleTableElement de;
    private IIntList nonzeroRows;

    int irow=0, icol=0;
    int row=0, col=0;
    

    public SparseRealMatrixNonzeroIterator(SparseRealMatrix _M)
    {
        M = _M;
        nrows = _M.rows();
        ncols = _M.columns();
        de = new DoubleTableElement(_M, 0, 0);

        nonzeroRows = intsNew.list(0);
        for (int i=0; i<_M.rows(); i++)
        {
            if (_M.rowVectors[i].getNumberOfNonzero() > 0)
                nonzeroRows.append(i);
        }
        
        reset();
    }

    @Override
    public void reset()
    {
        irow = 0;
        icol = 0;
        row = nonzeroRows.get(irow);
        col = M.rowVectors[row].getIndexMap().nonZeroIndices[icol];
        
        this.de = new DoubleTableElement(M, row, col);
    }

    @Override
    public boolean hasNext()
    {
        if (irow >= nonzeroRows.size())
            return false;
        if (icol >= M.rowVectors[row].getIndexMap().usedNonZero)
            return false;
        return true;
    }

    /**
     * Goes to the next value. Does not return anything. You have to get the content with get().
     * Usage Example:
     *
     * for (IDoubleArrayIterator it = arr.iterator(); it.hasNext(); it.next)
     * {
     *      System.out.println("current element: " + it.get());
     * }
     */
    @Override
    public void advance()
    {
        if (icol+1 < M.rowVectors[row].getIndexMap().usedNonZero)
            icol++;
        else
        {
            irow++;
            icol=0;
        }
        
        // out of bounds: do nothing
        if (irow >= nonzeroRows.size())
            return;
        
        row = nonzeroRows.get(irow);
        col = M.rowVectors[row].getIndexMap().nonZeroIndices[icol];
    }

    /**
     * Returns the current index. Good to know if this is a sparse vector iterator!
     * @return
     */
    @Override
    public int getIndex()
    {
        return(row*ncols+col);
    }

    /**
     * Returns the current value
     * @return
     */
    @Override
    public double get()
    {
        return M.get(row,col);
    }

    /**
     * Sets the current value
     */
    @Override
    public void set(double newValue)
    {
        M.set(row,col,newValue);
    }

    @Override
    public IDoubleElement next()
    {
        de.setIndex(row,col);
        advance();
        return de;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public int row()
    {
        return row;
    }

    @Override
    public int column()
    {
        return col;
    }


}

