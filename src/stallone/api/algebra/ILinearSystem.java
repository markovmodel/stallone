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
 * Interface for the solution of a linear system Ax = b, where A is a square matrix.
 *
 * @author  Tomaso Frigato, Martin Senne
 */
public interface ILinearSystem {

    /**
     * Sets the coefficient matrix {@code a}.
     *
     * @param  a  a square matrix
     */
    public void setMatrix(IDoubleArray A);

    /**
     * Sets the coefficient vector {@code b}.
     *
     * @param  b  the coefficient vector
     */
    public void setCoefficientVector(IDoubleArray b);

    /**
     * Solve linear system.
     *
     * @return  the vector {@code x}, solution of Ax = b.
     */
    public void perform();

    /**
     * Get solution x of A*x = b.
     *
     * @return  solution
     */
    public IDoubleArray getSolutionVector();

}
