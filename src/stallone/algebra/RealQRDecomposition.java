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

import stallone.api.algebra.*;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;



/**
 * QR decomposition.<br/>
 *
 * <p>For an m-by-n matrix A with m >= n, the QR decomposition is an m-by-n orthogonal matrix Q and an n-by-n upper
 * triangular matrix R so that</p>
 *
 * <pre>
   A = Q*R.
 * </pre>
 *
 * <p>The QR decompostion always exists, even if the matrix does not have full rank, so the method will never fail.</p>
 *
 * <p>The primary use of the QR decomposition is in the least squares solution of nonsquare systems of simultaneous
 * linear equations. This will fail if {@link #isFullRank()} returns false.</p>
 *
 * @author  Martin Senne
 */
public class RealQRDecomposition implements IQRDecomposition {

    /** Matrix for internal storage of decomposition. */
    private IDoubleArray qrMatrix;

    /** Row dimesions. */
    private int m;

    /** Column dimensions. */
    private int n;

    /** Array for internal storage of diagonal of R. */
    private double[] Rdiag;

    /**
     * QR Decomposition, computed by Householder reflections.
     */
    public RealQRDecomposition() {
    }

    @Override
    public void setMatrix(final IDoubleArray matrixA) {

        // Initialize.
        qrMatrix = Doubles.create.array(matrixA.rows(), matrixA.columns());
        qrMatrix.copyFrom(matrixA);
        m = matrixA.rows();
        n = matrixA.columns();
    }

    @Override
    public void perform() {
        Rdiag = new double[n];

        // Main loop.
        for (int k = 0; k < n; k++) {

            // Compute 2-norm of k-th column without under/overflow.
            double nrm = 0;

            for (int i = k; i < m; i++) {
                nrm = hypot(nrm, qrMatrix.get(i, k));
            }

            if (nrm != 0.0) {

                // Form k-th Householder vector.
                if (qrMatrix.get(k, k) < 0.0d) {
                    nrm = -nrm;
                }

                for (int i = k; i < m; i++) {
                    qrMatrix.set(i, k, qrMatrix.get(i, k) / nrm);
                }

                qrMatrix.set(k, k, qrMatrix.get(k, k) + 1.0d);

                // Apply transformation to remaining columns.
                for (int j = k + 1; j < n; j++) {
                    double s = 0.0;

                    for (int i = k; i < m; i++) {
                        s += qrMatrix.get(i, k) * qrMatrix.get(i, j);
                    }

                    s = -s / qrMatrix.get(k, k);

                    for (int i = k; i < m; i++) {
                        final double a = s * qrMatrix.get(i, k);
                        qrMatrix.set(i, j, qrMatrix.get(i, j) + a);
                    }
                }
            } // end if

            Rdiag[k] = -nrm;
        } // end for
    }

    /**
     * Check if matrix has full rank.
     *
     * @return  true if R, and hence A, has full rank.
     */
    @Override
    public boolean isFullRank() {

        for (int j = 0; j < n; j++) {

            if (Rdiag[j] == 0.0d) {
                return false;
            }
        }

        return true;
    }

    /**
     * Return the Householder vectors.
     *
     * @return  lower trapezoidal matrix whose columns define the reflections
     */
    public IDoubleArray getH() {
        final IDoubleArray X = Doubles.create.array(m, n);

        for (int i = 0; i < m; i++) {

            for (int j = 0; j < n; j++) {

                if (i >= j) {
                    X.set(i, j, qrMatrix.get(i, j));
                } else {
                    X.set(i, j, 0.0d);
                }
            }
        }

        return X;
    }

    /**
     * Return the upper triangular factor.
     *
     * @return  R
     */
    @Override
    public IDoubleArray getR() {
        final IDoubleArray X = Doubles.create.array(n, n);

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                if (i < j) {
                    X.set(i, j, qrMatrix.get(i, j));
                } else if (i == j) {
                    X.set(i, j, Rdiag[i]);
                } else {
                    X.set(i, j, 0.0d);
                }
            }
        }

        return X;
    }

    /**
     * Generate and return the (economy-sized) orthogonal factor.
     *
     * @return  Q
     */
    @Override
    public IDoubleArray getQ() {
        final IDoubleArray X = Doubles.create.array(m, n);

        for (int k = n - 1; k >= 0; k--) {

            for (int i = 0; i < m; i++) {
                X.set(i, k, 0.0d);
            }

            X.set(k, k, 1.0d);

            for (int j = k; j < n; j++) {

                if (qrMatrix.get(k, k) != 0.0d) {
                    double s = 0.0;

                    for (int i = k; i < m; i++) {
                        s += qrMatrix.get(i, k) * X.get(i, j);
                    }

                    s = -s / qrMatrix.get(k, k);

                    for (int i = k; i < m; i++) {
                        final double a = s * qrMatrix.get(i, k);
                        X.set(i, j, X.get(i, j) + a);
                    }
                }
            }
        } // end for

        return X;
    }

    /**
     * Least squares solution of A*X = B.
     *
     * @param      B  A Matrix with as many rows as A and any number of columns.
     *
     * @return     X that minimizes the two norm of Q*R*X-B.
     *
     * @exception  IllegalArgumentException  Matrix row dimensions must agree.
     * @exception  RuntimeException          Matrix is rank deficient.
     */
    public IDoubleArray solve(final IDoubleArray B) {
        final int b_rows = B.rows();
        final int b_cols = B.columns();

        if (b_rows != m) {
            throw new IllegalArgumentException("Matrix row dimensions must agree.");
        }

        if (!this.isFullRank()) {
            throw new RuntimeException("Matrix is rank deficient.");
        }

        // Copy right hand side
        final IDoubleArray X = B.copy();

        // Compute Y = transpose(Q)*B
        for (int k = 0; k < n; k++) {

            for (int j = 0; j < b_cols; j++) {
                double s = 0.0;

                for (int i = k; i < m; i++) {
                    s += qrMatrix.get(i, k) * X.get(i, j);
                }

                s = -s / qrMatrix.get(k, k);

                for (int i = k; i < m; i++) {
                    final double a = s * qrMatrix.get(i, k);
                    X.set(i, j, X.get(i, j) + a);
                }
            }
        }

        // Solve R*X = Y;
        for (int k = n - 1; k >= 0; k--) {

            for (int j = 0; j < b_cols; j++) {
                X.set(k, j, X.get(k, j) / Rdiag[k]);
            }

            for (int i = 0; i < k; i++) {

                for (int j = 0; j < b_cols; j++) {
                    final double a = X.get(k, j) * qrMatrix.get(i, k);
                    X.set(i, j, X.get(i, j) - a);
                }
            }
        }

        return X.viewBlock(0, 0, n, b_cols);
    }

    /**
     * @param   a
     * @param   b
     *
     * @return
     */
    public static double hypot(final double a, final double b) {
        double r;

        if (Math.abs(a) > Math.abs(b)) {
            r = b / a;
            r = Math.abs(a) * Math.sqrt(1 + (r * r));
        } else if (b != 0) {
            r = a / b;
            r = Math.abs(b) * Math.sqrt(1 + (r * r));
        } else {
            r = 0.0;
        }

        return r;
    }
}
