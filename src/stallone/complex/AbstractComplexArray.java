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
package stallone.complex;

import stallone.api.complex.IComplexIterator;
import stallone.api.complex.IComplexArray;
import java.io.Serializable;

import stallone.api.complex.ImaginaryView;
import stallone.api.doubles.IDoubleArray;
import stallone.doubles.AbstractDoubleArray;

/**
 * Base class for all vectors.
 *
 * @author  Martin Senne
 */
public abstract class AbstractComplexArray
    extends AbstractDoubleArray
    implements IComplexArray, Serializable
{
    /*protected transient Iterable<IScalarOfMatrix> nonZeroIterable = new Iterable<IScalarOfMatrix>()
    {

        @Override
        public Iterator<IScalarOfMatrix> iterator()
        {
            return getNonZeroIterator();
        }
    };
    protected transient Iterable<IScalarOfMatrix> fullIterable = new Iterable<IScalarOfMatrix>()
    {

        @Override
        public Iterator<IScalarOfMatrix> iterator()
        {
            return getFullIterator();
        }
    };
    protected transient IteratorType defaultIteratorType = IteratorType.NON_ZERO;*/

    @Override
    public int order()
    {
        int order = 0;
        if (rows() > 1)
            order++;
        if (columns() > 1)
            order++;
        return(order);
    }

    @Override
    public boolean isReal()
    {
        for (IComplexIterator it = nonzeroComplexIterator(); it.hasNext(); it.advance())
            if (it.getIm() != 0)
                return false;

        return true;
    }

    @Override
    public double get(int i, int j)
    {
        return getRe(i, j);
    }

    @Override
    public void set(int i, int j, double value)
    {
        setRe(i, j, value);
    }



    @Override
    public double get(int ij)
    {
        return getRe(ij/columns(), ij%columns());
    }

    @Override
    public double getRe(int ij)
    {
        return getRe(ij/columns(), ij%columns());
    }

    @Override
    public double getIm(int ij)
    {
        return getIm(ij/columns(), ij%columns());
    }

    @Override
    public void set(int ij, double value)
    {
        setRe(ij/columns(), ij%columns(), value);
    }

    @Override
    public void setRe(int ij, double value)
    {
        setRe(ij/columns(), ij%columns(), value);
    }

    @Override
    public void setIm(int ij, double value)
    {
        setIm(ij/columns(), ij%columns(), value);
    }


    @Override
    public void set(int i, double re, double im)
    {
        setRe(i,re);
        setIm(i,im);
    }

    @Override
    public void set(int i, int j, double re, double im)
    {
        setRe(i,j,re);
        setIm(i,j,im);
    }


    @Override
    public void zero()
    {
        for(IComplexIterator it = complexIterator(); it.hasNext(); it.advance())
        {
            it.set(0,0);
        }
    }

    @Override
    public void copyFrom(IComplexArray other)
    {
        int _size = other.size();
        if (_size != size())
            throw(new IllegalArgumentException("Trying to copy from array with different size"));

        for (int i=0; i<_size; i++)
            set(i,other.getRe(i),getIm(i));
    }

    @Override
    public void copyInto(IComplexArray other)
    {
        int _size = other.size();
        if (_size != size())
            throw(new IllegalArgumentException("Trying to copy from array with different size"));

        for (int i=0; i<_size; i++)
            other.set(i,getRe(i),getIm(i));
    }

    @Override
    public double[] getRealArray()
    {
        return(getArray());
    }


    @Override
    public double[] getImaginaryArray()
    {
        double[] res = new double[rows()*columns()];
        for (int i=0; i<res.length; i++)
            res[i] = getIm(i);
        return(res);
    }

    @Override
    public double[] getRealRow(int row)
    {
        return(getRow(row));
    }

    @Override
    public double[] getRealColumn(int col)
    {
        return(getColumn(col));
    }

    @Override
    public double[] getImaginaryRow(int row)
    {
        double[] res = new double[columns()];
        for (int i=0; i<res.length; i++)
            res[i] = getIm(row,i);
        return(res);
    }

    @Override
    public double[] getImaginaryColumn(int col)
    {
        double[] res = new double[columns()];
        for (int i=0; i<res.length; i++)
            res[i] = getIm(i,col);
        return(res);
    }

    @Override
    public double[][] getRealTable()
    {
        return(getTable());
    }

    @Override
    public double[][] getImaginaryTable()
    {
        double[][] res = new double[rows()][columns()];
        for (IComplexIterator it = complexIterator(); it.hasNext(); it.advance())
        {
            res[it.row()][it.column()] = it.getIm();
        }
        return(res);
    }
    /**
     * Return default iterator {@link #defaultIteratorType). The default iterator is the iterator over non-zero entries
     * {@link IteratorType#NON_ZERO}. This method is identical to calling {@code
     * getIterable(IteratoryType.NON_ZERO).iterator()}. This method is a convenience method to allow for-loops like
     * <code>for (IScalarOfVector entry : this ) { }</code>. This implementation is required for interface {@link
     * Iterator<IScalarOfVector>}.
     *
     * @return  appropriate non-zero getIterable
     *
     * @see     IteratorType
     */
    /*@Override
    public final Iterator<IScalarOfMatrix> iterator()
    {
        return getIterable(defaultIteratorType).iterator();
    }*/

    /*@Override
    public Iterable<IScalarOfMatrix> getIterable(final IteratorType type)
    {

        switch (type)
        {

            case NON_ZERO:
            {
                return nonZeroIterable;
            }

            case FULL:
            {
                return fullIterable;
            }

            default:
            {
                throw new RuntimeException("Unknown iterator-type.");
            }
        }
    }*/

    /**
     * Subclasses may provide a more specific / faster implementation of a non-zero iterator.
     *
     * @return  standard non zero iterator
     */
    /*protected Iterator<IScalarOfMatrix> getNonZeroIterator()
    {
        return new GenericNonZeroMatrixIterator(this);
    }*/

    /**
     * Subclasses may provide a more specific / faster implementation of a full iterator.
     *
     * @return  standard full iterator
     */
    /*protected Iterator<IScalarOfMatrix> getFullIterator()
    {
        return new GenericFullMatrixIterator(this);
    }*/

    @Override
    public IComplexIterator complexIterator()
    {
        return(new ComplexArrayIterator(this));
    }

    @Override
    public IComplexIterator nonzeroComplexIterator()
    {
        return(new ComplexArrayNonzeroIterator(this));
    }

    /*@Override
    public Iterator<IVector> rowIterator()
    {
        class RowIterator implements Iterator<IVector>
        {
            IMatrix m;
            int i=0;

            RowIterator(IMatrix _m)
            {
                m=_m;
            }

            @Override
            public boolean hasNext()
            {
                return(i<m.rows());
            }

            @Override
            public IVector next()
            {
                IVector res = m.getRow(i);
                i++;
                return(res);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Not supported.");
            }
        }

        return(new RowIterator(this));
    }*/

    @Override
    public IComplexArray viewRow(int r)
    {
        return new ComplexArrayView(this, r, 0, r+1, columns());
    }

    @Override
    public IComplexArray viewColumn(int column)
    {
        return new ComplexArrayView(this, 0, column, rows(), column+1);
    }

    @Override
    public IComplexArray viewBlock(int startRow, int startColumn, int endRow, int endColumn)
    {
        return new ComplexArrayView(this, startRow, startColumn, endRow, endColumn);
    }

    @Override
    public IComplexArray view(int[] selectedRows, int[] selectedColumns)
    {
        return new ComplexArrayView(this, selectedRows, selectedColumns);
    }

    @Override
    public IDoubleArray viewReal()
    {
        return this;
    }

    @Override
    public IDoubleArray viewImaginary()
    {
        return new ImaginaryView(this);
    }

    @Override
    public String toString()
    {
        final StringBuilder strBuf = new StringBuilder();

        int rows = rows();
        int cols = columns();
        if (!isReal())
        {
            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    strBuf.append(getRe(i)+" "+getIm(i)+"i ");
                }

                strBuf.append("\n");
            }
        }
        else
        {
            for (int i = 0; i < rows; i++)
            {

                for (int j = 0; j < cols; j++)
                {
                    strBuf.append(getRe(i, j)+" ");
                }

                strBuf.append("\n");
            }
        } // end if-else

        System.out.println("done");

        return strBuf.toString();
    }
}
