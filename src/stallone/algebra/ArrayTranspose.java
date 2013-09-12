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

import static stallone.doubles.DoubleArrayTest.assertColumns;
import static stallone.doubles.DoubleArrayTest.assertRows;
import static stallone.doubles.DoubleArrayTest.assertSquare;
import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

/**
 * Compute transpose of a complex matrix.
 *
 * @author  Tomaso Frigato, Martin Senne
 */
public class ArrayTranspose //implements IMatrixTranspose 
{
    public IDoubleArray transposeToNew(final IDoubleArray a)
    {
        IDoubleArray res = a.create(a.columns(),a.rows());
        transpose(a,res);
        return res;
    }

    
    //@Override
    public void transpose(final IDoubleArray a, final IDoubleArray target)
    {
        final int rows = target.rows();
        final int cols = target.columns();

        // check consistency
        assertRows(target, a.columns());
        assertColumns(target, a.rows());

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                target.set(i, j, a.get(j, i));
            }
        }
    }
    
    //@Override
    public void transpose(final IDoubleArray a)
    {
        final int rows = a.rows();
        final int cols = a.columns();

        // check consistency
        assertSquare(a);
        double h;

        for (int i = 0; i < rows; i++)
        {
            for (int j = i; j < cols; j++)
            {
                h = a.get(i, j);
                a.set(i,j,a.get(j,i));
                a.set(j,i,h);
            }
        }
    }

    
    public IComplexArray conjugateTransposeToNew(final IComplexArray a)
    {
        IComplexArray res = a.create(a.columns(),a.rows());
        transpose(a,res);
        return res;
    }
    
    /**
     * (non-Javadoc).
     *
     * @see  sherlock.math.algebra.impl.IMatrixTranspose#transpose(sherlock.datatypes.algebra.api.IMatrix, sherlock.datatypes.algebra.api.IMatrix)
     */
    //@Override
    public void conjugateTranspose(final IComplexArray a, final IComplexArray target)
    {
        final int rows = target.rows();
        final int cols = target.columns();

        // check consistency
        assertRows(target, a.columns());
        assertColumns(target, a.rows());

//        final IComplexNumber t = ComplexNumber.createZero();

//        double re, im;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                target.setRe(i, j, a.getRe(j, i));
                target.setIm(i, j, -a.getIm(j, i));
            }
        }
    }
    
    //@Override
    public void conjugateTranspose(final IComplexArray a)
    {
        final int rows = a.rows();
        final int cols = a.columns();

        // check consistency
        assertSquare(a);
        double hre, him;

        for (int i = 0; i < rows; i++)
        {
            for (int j = i; j < cols; j++)
            {
                hre = a.getRe(i, j);
                him = a.getIm(i, j);
                a.set(i,j,a.getRe(j,i),a.getIm(j,i));
                a.set(j,i,hre,him);
            }
        }
    }
    
}
