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
 * Interface for the solution of a linear system A*X = B, where A and B are square matrix.
 *
 * @author  Tomaso Frigato, Martin Senne
 */
public interface ILinearMatrixSystem {

    /**
     * Sets the coefficient matrix {@code A}.
     *
     * @param  a  a square matrix
     */
    void setMatrix(IDoubleArray A);

    /**
     * Sets the coefficient matrix {@code B}.
     *
     * @param  b  the coefficient matrix
     */
    void setCoefficientMatrix(IDoubleArray B);

    /**
     * Solve linear system.
     *
     * @return  the vector {@code X}, solution of Ax = b.
     */
    void perform();

    /**
     * Get solution x of A*X = b.
     *
     * @return  solution
     */
    IDoubleArray getSolutionMatrix();

}
