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
package stallone.api.complex;

import stallone.complex.ComplexNumber;
import stallone.complex.DenseComplexArray;
import stallone.api.algebra.IComplexNumber;

/**
 * Factory for creating algebra data structures that return Sherlocks own implementation of the data stractures. You
 * should not use this class directly. Use AlgebraDTFactory.getInstance() instead.
 *
 * <p>Example of use: Algebra.data.createMatrix(50,50);</p>
 *
 * @author  Martin Senne, Christoph Thöns, Frank Noe
 */
public class ComplexFactory
{
    /////////////////////////////////////////////////////////////////////////////
    //
    // Data types
    //
    /////////////////////////////////////////////////////////////////////////////

    public IComplexArray array(final int size)
    {
        IComplexArray res = new DenseComplexArray(size, 1);
        return (res);
    }

    /**
     * General constructor. Creates currently only dense complex arrays
     */
    public IComplexArray array(final int rows, final int cols)
    {
        IComplexArray res = new DenseComplexArray(rows, cols);
        return (res);
    }

    public IComplexArray array(final double[][] real, final double[][] imag)
    {
        IComplexArray res = array(real.length, real[0].length);

        for (int i = 0; i < real.length; i++)
        {
            for (int j = 0; j < real[i].length; j++)
            {
                res.set(i, j, real[i][j], imag[i][j]);
            }
        }

        return res;
    }

    public IComplexArray array(final IComplexNumber[][] values)
    {
        return new DenseComplexArray(values);
    }

    /**
     * Creates a complex row vector
     * @param dimension
     * @return
     */
    public IComplexArray row(final int dimension)
    {
        return array(1, dimension);
    }

    /**
     * Creates a complex row vector
     * @param dimension
     * @return
     */
    public IComplexArray row(double[] re, double[] im)
    {
        IComplexArray res = row(re.length);
        for (int i = 0; i < re.length; i++)
        {
            res.set(i, re[i], im[i]);
        }
        return res;
    }

    /**
     * Creates a complex column vector
     * @param dimension
     * @return
     */
    public IComplexArray column(final int dimension)
    {
        return array(dimension, 1);
    }

    /**
     * Creates a complex row vector
     * @param dimension
     * @return
     */
    public IComplexArray column(double[] re, double[] im)
    {
        IComplexArray res = column(re.length);
        for (int i = 0; i < re.length; i++)
        {
            res.set(i, re[i], im[i]);
        }
        return res;
    }

    public IComplexArray diag(int size, double value)
    {
        IComplexArray M = array(size, size);
        for (int i = 0; i < size; i++)
        {
            M.set(i, i, value);
        }
        return (M);
    }

    public IComplexArray diag(double... values)
    {
        IComplexArray M = array(values.length, values.length);
        for (int i = 0; i < values.length; i++)
        {
            M.set(i, i, values[i]);
        }
        return (M);
    }

    public IComplexArray diag(IComplexArray values)
    {
        IComplexArray M = array(values.size(), values.size());
        for (int i = 0; i < values.size(); i++)
        {
            M.set(i, i, values.getRe(i), values.getIm(i));
        }
        return (M);
    }

    public IComplexArray identity(final int dim)
    {
        final IComplexArray identityMatrix = array(dim, dim);

        for (int i = 0; i < dim; i++)
        {
            identityMatrix.set(i, i, 1.0d);
        }

        return identityMatrix;
    }

    public IComplexNumber complexScalar(final double real, final double imaginary)
    {
        return new ComplexNumber(real, imaginary);
    }

    public IComplexNumber complexScalar()
    {
        return new ComplexNumber(0.0d, 0.0d);
    }

}
