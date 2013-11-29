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

/**
 * General solution of a real linear system via means of LU or QR decomposition.
 *
 * @author  Martin Senne
 */
public class RealLinearSystem implements ILinearSystem, ILinearMatrixSystem
{

    IDoubleArray matrixA;
    IDoubleArray coefficientMatrix;
    IDoubleArray solutionMatrix;
    RealLUDecomposition luDecomposition;
    RealQRDecomposition qrDecomposition;

    @Override
    public void setMatrix(final IDoubleArray matrix)
    {
        matrixA = matrix;
        luDecomposition = null;
        qrDecomposition = null;
    }

    @Override
    public void setCoefficientVector(final IDoubleArray coefficientVector)
    {
        coefficientMatrix = coefficientVector.copy();
    }

    @Override
    public void setCoefficientMatrix(final IDoubleArray coefficientMatrix)
    {
        this.coefficientMatrix = coefficientMatrix;
    }

    @Override
    public void perform()
    {

        if (matrixA.rows() == matrixA.columns())
        { // lu case

            if (luDecomposition == null)
            { // create decomposition
                luDecomposition = new RealLUDecomposition();
                luDecomposition.setMatrix(matrixA);
                luDecomposition.perform();
            }

            solutionMatrix = luDecomposition.solve(coefficientMatrix);
        }
        else
        {

            if (qrDecomposition == null)
            { // create decomposition
                qrDecomposition = new RealQRDecomposition();
                qrDecomposition.setMatrix(matrixA);
                qrDecomposition.perform();
            }

            solutionMatrix = qrDecomposition.solve(coefficientMatrix);
        }
    }

    @Override
    public IDoubleArray getSolutionVector()
    {
        return solutionMatrix.viewColumn(0);
    }

    @Override
    public IDoubleArray getSolutionMatrix()
    {
        return solutionMatrix;
    }
}
