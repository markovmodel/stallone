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

    public static void main(String[] args)
    {
        SparseRealMatrix M = new SparseRealMatrix(10,10);
        M.set(1,1, 1);
        M.set(1,2, 2);
        M.set(3,1, 3);
        M.set(4,6, 4);
        M.set(0,0, 5);
        M.set(9,3, 6);
        
        for (IDoubleIterator it = M.nonzeroIterator(); it.hasNext();)
        {
            IDoubleElement e = it.next();
            System.out.println(e.row()+" "+e.column()+" "+e.get());
        }
    }
    
}


class SparseRealMatrixNonzeroIterator implements IDoubleIterator
{
    IIntList nonzeroRows;
    private SparseRealMatrix M;
    private IDoubleIterator vit;
    int n;
    int irow=0;
    
    SparseRealMatrixNonzeroIterator(SparseRealMatrix _M)
    {
        M = _M;
        n = _M.rows();

        nonzeroRows = intsNew.list(0);
        for (int i=0; i<_M.rows(); i++)
        {
            if (_M.rowVectors[i].getNumberOfNonzero() > 0)
                nonzeroRows.append(i);
        }
        
        irow = 0;
        int row = nonzeroRows.get(irow);
        vit = _M.rowVectors[row].nonzeroIterator();
    }    
    
    @Override
    public void reset()
    {
        irow = 0;
    }

    @Override
    public void advance()
    {
        if (vit == null)
            return;
        
        System.out.println(" _advance()");
        if (vit.hasNext())
        {
            System.out.println("  _vit.advance()");
            vit.advance();
        }
        else
        {
            irow++;
            if (irow < nonzeroRows.size())
                vit = M.rowVectors[nonzeroRows.get(irow)].nonzeroIterator();
            else
                vit = null;
            System.out.println("  irow = "+irow);
            System.out.println("  vit = "+vit);
        }
    }

    @Override
    public int getIndex()
    {
        int row = nonzeroRows.get(irow);
        int col = vit.getIndex();
        return row * n + col;
    }

    @Override
    public int row()
    {
        return nonzeroRows.get(irow);
    }

    @Override
    public int column()
    {
        return vit.getIndex();
    }

    @Override
    public double get()
    {
        return vit.get();
    }

    @Override
    public void set(double x)
    {
        vit.set(x);
    }

    @Override
    public boolean hasNext()
    {
        if (irow >= nonzeroRows.size())
            return false;
        return vit.hasNext();
    }

    @Override
    public IDoubleElement next()
    {
        IDoubleElement res;
        if (vit != null)
            res= vit.next();
        else
            res= null;
        advance();
        return res;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
