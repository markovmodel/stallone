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
package stallone.algebra;

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleArray;
import stallone.api.algebra.*;
import stallone.doubles.PrimitiveDoubleTable;

/**
 * LU Decomposition.
 *
 * <p>For an m-by-n matrix A with m &gt;= n, the LU decomposition is an m-by-n unit lower triangular matrix L, an n-by-n
 * upper triangular matrix U, and a permutation vector piv of length m so that<br/>
 * </p>
 *
 * <pre>
A(piv,:) = L*U.
 * </pre>
 *
 * <p>If m &lt; n, then L is m-by-m and U is m-by-n.</p>
 *
 * <p>The LU decompostion with pivoting always exists, even if the matrix is singular, so the constructor will never
 * fail. The primary use of the LU decomposition is in the solution of square systems of simultaneous linear equations.
 * This will fail if isNonsingular() returns false.</p>
 *
 * @author  Martin Senne
 */
public class RealLUDecomposition implements ILUDecomposition
{

    /** matrix for storage of decomposition. */
    private IDoubleArray luMatrix;
    /** row dimensions of A. */
    private int m;
    /** column dimensions of A. */
    private int n;
    /** pivot sign. */
    private int pivotSign;
    /** pivot vector. */
    private int[] pivots;

    /**
     * LU Decomposition.
     */
    public RealLUDecomposition()
    {
    }

    /**
     * Set matrix A, for which to perform LU-Decomposition.
     *
     * @param  matrixA
     */
    @Override
    public void setMatrix(final IDoubleArray matrixA)
    {
        luMatrix = new PrimitiveDoubleTable(matrixA.rows(), matrixA.columns());
        luMatrix.copyFrom(matrixA);
        m = matrixA.rows();
        n = matrixA.columns();
    }

    @Override
    public void perform()
    {
        // Use a "left-looking", dot-product, Crout/Doolittle algorithm.
        pivots = new int[m];

        for (int i = 0; i < m; i++)
        {
            pivots[i] = i;
        }

        pivotSign = 1;

        IDoubleArray LUrowi;
        IDoubleArray LUcolj;

        for (int j = 0; j < n; j++)
        {

            // Make a copy of the j-th column to localize references.
            LUcolj = luMatrix.viewColumn(j).copy();

            // Apply previous transformations.
            for (int i = 0; i < m; i++)
            {
                LUrowi = luMatrix.viewRow(i);

                // Most of the time is spent in the following dot product.

                final int kmax = Math.min(i, j);
                double s = 0.0d;

                for (int k = 0; k < kmax; k++)
                {
                    s += LUrowi.get(k) * LUcolj.get(k);
                }

                LUcolj.set(i, LUcolj.get(i) - s);
                LUrowi.set(j, LUcolj.get(i));
            }

            // Find pivot and exchange if necessary.
            int p = j;

            for (int i = j + 1; i < m; i++)
            {

                if (Math.abs(LUcolj.get(i)) > Math.abs(LUcolj.get(p)))
                {
                    p = i;
                }
            }

            if (p != j)
            {

                for (int k = 0; k < n; k++)
                {
                    final double t = luMatrix.get(p, k);
                    luMatrix.set(p, k, luMatrix.get(j, k));
                    luMatrix.set(j, k, t);
                }

                final int k = pivots[p];
                pivots[p] = pivots[j];
                pivots[j] = k;
                pivotSign = -pivotSign;
            }

            // Compute multipliers.
            if ((j < m) && (luMatrix.get(j, j) != 0.0d))
            {
                final double r = luMatrix.get(j, j);

                for (int i = j + 1; i < m; i++)
                {
                    luMatrix.set(i, j, luMatrix.get(i, j) / r);
                }
            }
        } // end for
    }

    /**
     * Check for matrix nonsingularity.
     *
     * @return  true if U, and hence A, is nonsingular.
     */
    @Override
    public boolean isNonsingular()
    {

        for (int j = 0; j < n; j++)
        {

            if (luMatrix.get(j, j) == 0.0d)
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Return lower triangular factor in new matrix.
     *
     * @return  L
     */
    @Override
    public IDoubleArray getL()
    {
        final IDoubleArray X = new PrimitiveDoubleTable(m, n);

        for (int i = 0; i < m; i++)
        {

            for (int j = 0; j < n; j++)
            {

                if (i > j)
                {
                    X.set(i, j, luMatrix.get(i, j));
                }
                else if (i == j)
                {
                    X.set(i, j, 1.0d);
                }
                else
                {
                    X.set(i, j, 0.0d);
                }
            }
        }

        return X;
    }

    /**
     * Get upper triangular factor in new matrix.
     *
     * @return  U
     */
    @Override
    public IDoubleArray getU()
    {
        final IDoubleArray X = new PrimitiveDoubleTable(n, n);

        for (int i = 0; i < n; i++)
        {

            for (int j = 0; j < n; j++)
            {

                if (i <= j)
                {
                    X.set(i, j, luMatrix.get(i, j));
                }
                else
                {
                    X.set(i, j, 0.0d);
                }
            }
        }

        return X;
    }

    /**
     * Return pivot permutation vector as int array.
     *
     * @return  piv
     */
    public int[] getPivot()
    {
        final int[] p = new int[m];
        System.arraycopy(pivots, 0, p, 0, m);

        return p;
    }

    /**
     * Calculate determinant.
     *
     * @return     deteterminant of A
     *
     * @exception  IllegalArgumentException  Matrix must be square
     */
    @Override
    public double det()
    {

        if (m != n)
        {
            throw new IllegalArgumentException("Matrix must be square.");
        }

        double d = pivotSign;

        for (int j = 0; j < n; j++)
        {
            d *= luMatrix.get(j, j);
        }

        return d;
    }

    /**
     * Solve A*X = B.
     *
     * @param      B  A Matrix with as many rows as A and any number of columns.
     *
     * @return     X so that L*U*X = B(piv,:)
     *
     * @exception  IllegalArgumentException  if matrix row dimensions do not agree.
     * @exception  RuntimeException          if matrix is singular.
     */
    public IDoubleArray solve(final IDoubleArray B)
    {
        final int b_rows = B.rows();
        final int b_cols = B.columns();

        if (b_rows != m)
        {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }

        if (!isNonsingular())
        {
            throw new RuntimeException("Matrix is singular.");
        }

        // copy right hand side in pivot order
        final IDoubleArray X = new PrimitiveDoubleTable(b_rows, b_cols);

        for (int i = 0; i < b_rows; i++)
        {
            final int pivotedRow = pivots[i];

            for (int j = 0; j < b_cols; j++)
            {
                X.set(i, j, B.get(pivotedRow, j));
            }
        }

        // solve L*Y = B(piv,:)
        for (int k = 0; k < n; k++)
        {

            for (int i = k + 1; i < n; i++)
            {
                final double lu_ik = luMatrix.get(i, k);

                for (int j = 0; j < b_cols; j++)
                {
                    final double a = X.get(k, j) * lu_ik;
                    X.set(i, j, X.get(i, j) - a);
                }
            }
        }

        // solve U*X = Y;
        for (int k = n - 1; k >= 0; k--)
        {
            final double lu_kk = luMatrix.get(k, k);

            for (int j = 0; j < b_cols; j++)
            {
                X.set(k, j, X.get(k, j) / lu_kk);
            }

            for (int i = 0; i < k; i++)
            {
                final double lu_ik = luMatrix.get(i, k);

                for (int j = 0; j < b_cols; j++)
                {
                    final double a = X.get(k, j) * lu_ik;
                    X.set(i, j, X.get(i, j) - a);
                }
            }
        }

        return X;
    }
}
