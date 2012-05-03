/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.algebra;

import stallone.api.doubles.IDoubleArray;


/**
 * Interface for the Cholesky decomposition of a (positive definite) square
 * matrix A into an upper triangular matrix A=R^T*R.
 *
 * @author Axel Rack
 */
public interface ICholeskyDecomposition {

    /**
     * Check whether the Cholesky decomposition of input A exists.
     *
     * @return True if R exists and thus, input A is positive definite, false
     * otherwise.
     */
    public boolean hasDecomposition();

    /**
     * Get the result matrix R which is the Cholesky decomposition A=R^T*R. If
     * the input matrix A is not positive definite the Cholesky algorithm fails
     * and R does not exist.
     *
     * @return The result matrix R.
     */
    public IDoubleArray getRMatrix();

    /**
     * Get the input matrix A of the decomposition.
     *
     * @return The input matrix A.
     */
    public IDoubleArray getInputMatrix();
}
