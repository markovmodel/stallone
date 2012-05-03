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
 * Interface for the QR decomposition.
 *
 * @author  Martin Senne
 */
public interface IQRDecomposition {

    /**
     * Set input matrix to perform qr decomposition for.
     *
     * @param  matrixA
     */
    void setMatrix(IDoubleArray matrixA);

    /**
     * Execute.
     */
    void perform();

    /**
     * Generate and return the (economy-sized) orthogonal factor.
     *
     * @return  Q
     */
    IDoubleArray getQ();

    /**
     * Return the upper triangular factor.
     *
     * @return  R
     */
    IDoubleArray getR();

    /**
     * Check if matrix has full rank.
     *
     * @return  true if R, and hence A, has full rank.
     */
    boolean isFullRank();
}
