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
package stallone.api.algebra;

import stallone.api.doubles.IDoubleArray;



/**
 * Interface for the LU decomposition algorithm.
 *
 * @author  Martin Senne, Tomaso Frigato
 */
public interface ILUDecomposition {

    /**
     * Set matrix A, for which to perform LU-Decomposition.
     *
     * @param  matrixA
     */
    void setMatrix(IDoubleArray A);

    /**
     * Execute, after input parameters have been set.
     */
    void perform();

    /**
     * Return lower triangular factor in new matrix.
     *
     * @return  L
     */
    IDoubleArray getL();

    /**
     * Get upper triangular factor in new matrix.
     *
     * @return  U
     */
    IDoubleArray getU();

    /**
     * Check for matrix nonsingularity.
     *
     * @return  true if U, and hence A, is nonsingular.
     */
    boolean isNonsingular();

    // int[] getPivot();
    //
    // IMatrix solve(IMatrix B);
    //

    /**
     * Calculate determinant.
     *
     * @return  determinant.
     */
    double det();
}
