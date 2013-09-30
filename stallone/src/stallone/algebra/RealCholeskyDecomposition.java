/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.algebra;

import java.security.InvalidParameterException;

import stallone.api.algebra.*;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;


/**
 * Cholesky decomposition.<br/>
 *
 * <p>For an n-by-n matrix A, the Cholesky decomposition is the n-by-n
 * upper triangular matrix R so that</p>
 *
 * <pre>
 * A = R^T*R is satisfied.
 * </pre>
 *
 * <p>The Cholesky decomposition exists, iff the input matrix A is positive
 * definite. Otherwise the algorithm will fail, so that the Cholesky
 * decomposition can be used as a test for positive definiteness of A using
 * {@link #hasDecomposition()} .
 * </p>
 *
 * @author Axel Rack
 */
public class RealCholeskyDecomposition implements ICholeskyDecomposition {

    /**
     * The input matrix A for the decomposition.
     */
    private IDoubleArray matrixA;
    /**
     * The result matrix R of the decomposition.
     */
    private IDoubleArray matrixR;

    /**
     * Create a new instance of this which tries to compute the decomposition
     * matrix L from A. If the input A is not positive definite, the
     * decomposition will fail and L will be {@code null}.
     *
     * <p>
     * If input A is not purely real or non-symmetric, an unchecked
     * {@see InvalidParameterException} is thrown.
     * </p>
     *
     * @param input The input matrix, which must be purely real and symmetric.
     */
    public RealCholeskyDecomposition(IDoubleArray input) {
        setInputMatrix(input);
        setResultMatrix(computeDecomposition(input));
    }

    @Override
    public boolean hasDecomposition() {
        return getRMatrix() != null;
    }

    /**
     * Get the decomposition matrix L if it exists.
     * 
     * @return The output matrix L or {@code null}, if the algorithm failed.
     */
    @Override
    public IDoubleArray getRMatrix() {
        return matrixR;
    }

    @Override
    public IDoubleArray getInputMatrix() {
        return matrixA;
    }

    /**
     * Set the input matrix A to given matrix.
     * 
     * @param matrix The new input matrix.
     */
    protected void setInputMatrix(IDoubleArray matrix) {
        if (matrix == null) {
            throw new InvalidParameterException("Input matrix must not be null");
        }
        if (!Algebra.util.isSymmetric(matrix)) {
            throw new InvalidParameterException("Input matrix must be symmetric");
        }
        this.matrixA = matrix;
    }

    /**
     * Set the decomposition result matrix R to given {@code IMatrix}.
     *
     * @param matrix The new result.
     */
    protected void setResultMatrix(IDoubleArray matrix) {
        this.matrixR = matrix;
    }

    /**
     * Computes the Cholesky decomposition of given input matrix A, i.e.
     * a matrix L satisying A=R^T*R for positive definite A.<br>
     * If A is not positive definite, the algorithm fails.
     *
     * @param input The {@code IMatrix} to be decomposed.
     * @return The upper triangular decomposition matrix R, or {@code null} if
     * the algorithm fails.
     */
    private IDoubleArray computeDecomposition(IDoubleArray input) {
        int n = input.rows();
        IDoubleArray result = Doubles.create.array(n, n);
        result.zero();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                double sum = 0.0;
                for (int k = 0; k < j; k++) {
                    sum += result.get(i, k) * result.get(j, k);
                }
                if (i == j) {
                    result.set(i, i, Math.sqrt(input.get(i, i) - sum));
                } else {
                    result.set(i, j, 1 / result.get(j, j) *
                            (input.get(i, j) - sum));
                }
            }
            if (result.get(i, i) <= 0) {
                result = null;
                break;
            }
        }

        return result;
    }
}
