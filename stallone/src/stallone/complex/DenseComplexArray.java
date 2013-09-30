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

import stallone.api.algebra.IComplexNumber;
import stallone.api.complex.IComplexArray;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * Complex dense matrix implementation based on a one dimensional array.
 *
 * @author  Martin Senne
 */
public class DenseComplexArray extends AbstractComplexArray
{

    /**
     * Contains matrix data in a column-major way:
     *
     * <pre>
    / 0 = 0r  4 = 2r \
    |  1 = 0i  5 = 2i  |
    |  2 = 1r  6 = 3r  |
    \  3 = 1i  7 = 3i /
     * </pre>
     */
    protected double[] data;
    protected int rows, cols;

    /**
     * Create a new dense double matrix with dimension {@code rows} times {@code cols}.
     *
     * @param  rows  number of rows the new matrix has.
     * @param  cols  number of columns the new matrix has.
     */
    public DenseComplexArray(final int _rows, final int _cols)
    {
        rows = _rows;
        cols = _cols;
        data = new double[2 * rows * cols];
    }

    /**
     * Constructor create a matrix from other matrix.
     *
     * @param  source  is the source matrix.
     */
    public DenseComplexArray(final IComplexArray source)
    {
        this(source.rows(), source.columns());
        copyFrom(source);
    }

    /**
     * Create a new dense complex matrix from two dimensional complex array {@code value[numRows][numCols]}. The array
     * {@code value} must have all rows of the same length.
     *
     * @param  value  the complex array to be stored in the matrix.
     */
    public DenseComplexArray(final IComplexNumber[][] value)
    {
        rows = value.length;
        cols = value[0].length;
        data = new double[rows * cols * 2];

        for (int i = 0; i < rows; i++)
        {

            if (cols != value[i].length)
            {
                throw new IllegalArgumentException("input array has rows " + "of uneven length");
            }
            else
            {

                for (int j = 0; j < cols; j++)
                {
                    data[2 * (i + (j * rows))] = value[i][j].getRe();
                    data[(2 * (i + (j * rows))) + 1] = value[i][j].getIm();
                }
            }
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
    public double getRe(final int i, final int j)
    {
        final int idx = (i + (j * rows)) * 2;

        return data[idx];
    }

    @Override
    public double getIm(final int i, final int j)
    {
        final int idx = (i + (j * rows)) * 2;

        return data[idx + 1];
    }

    //@Override
    public IComplexNumber getScalar(final int i, final int j)
    {
        final int idx = (i + (j * rows)) * 2;

        return new ComplexNumber(data[idx], data[idx + 1]);
    }

    //@Override
    public IComplexNumber getScalar(final int i, final int j, final IComplexNumber target)
    {
        final int idx = (i + (j * rows)) * 2;
        target.setComplex(data[idx], data[idx + 1]);

        return target;
    }

    @Override
    public void setRe(final int i, final int j, final double real)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] = real;
    }

    @Override
    public void setIm(final int i, final int j, final double imaginary)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx + 1] = imaginary;
    }

    //@Override
    public void setScalar(final int i, final int j, final IComplexNumber complex)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] = complex.getRe();
        data[idx + 1] = complex.getIm();
    }

    @Override
    public void set(final int i, final int j, final double real, final double imaginary)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] = real;
        data[idx + 1] = imaginary;
    }

    @Override
    public void copyFrom(final IComplexArray matrix)
    {
        assertEqualDimensions(this,matrix);

        if (matrix instanceof DenseComplexArray)
            {
                System.arraycopy(((DenseComplexArray) matrix).data, 0, this.data, 0, rows * cols * 2);
            }
            else
            {
                super.copyFrom(matrix);
            }
    }

    @Override
    public void zero()
    {
        for (int j = 0; j < cols; j++)
        {
            for (int i = 0; i < rows; i++)
            { // i are rows
                this.set(i, j, 0.0d, 0.0d);
            }
        }
    }

    @Override
    public boolean isReal()
    {
        final int n = data.length;

        for (int i = 1; i < n; i += 2)
        {

            if (data[i] != 0.0d)
            {
                return false;
            }
        }

        return true;
    }

    /*
    @Override
    public boolean storesComplex()
    {
        return true;
    }*/

    //@Override
    public void addRe(final int i, final int j, final double real)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] += real;
    }

    //@Override
    public void addIm(final int i, final int j, final double imaginary)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx + 1] += imaginary;
    }

    //@Override
    public void add(final int i, final int j, final double real, final double imaginary)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] += real;
        data[idx + 1] += imaginary;
    }

    //@Override
    public void add(final int i, final int j, final IComplexNumber scalar)
    {
        final int idx = (i + (j * rows)) * 2;
        data[idx] += scalar.getRe();
        data[idx + 1] += scalar.getIm();
    }

    @Override
    public IComplexArray copy()
    {
        return new DenseComplexArray(this);
    }

    /*@Override
    protected Iterator<IScalarOfMatrix> getFullIterator() {
    return new DenseComplexFullMatrixIterator(this);
    }
    
    @Override
    protected Iterator<IScalarOfMatrix> getNonZeroIterator() {
    return new DenseComplexNonZeroMatrixIterator(this);
    }*/
    @Override
    public IComplexArray create(int rows, int cols)
    {
        return (new DenseComplexArray(rows, cols));
    }

    @Override
    public IComplexArray create(int size)
    {
        return (new DenseComplexArray(size, 1));
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

}
