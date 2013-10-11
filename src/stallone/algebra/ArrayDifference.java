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

import stallone.api.complex.IComplexIterator;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * Generic implementation of IMatrixSum for complex operands.
 *
 * @author  Frank Noe
 */
public class ArrayDifference //implements IMatrixSum 
{

    public IDoubleArray subtractToNewDense(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        subtractDense(a, b, target);
        return target;
    }

    public IDoubleArray subtractToNewSparse(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        subtractSparse(a, b, target);
        return target;
    }

    public void subtractFromSparse(final IDoubleArray a, final IDoubleArray b)
    {
        assertEqualDimensions(a, b);

        for (IDoubleIterator it = b.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            a.set(i, j, a.get(i, j) - it.get());
        }
    }

    public void subtractFromDense(final IDoubleArray a, final IDoubleArray b)
    {
        subtractDense(a, b, a);
    }

    public void subtractDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        // direct access may not be efficient
        // Extract some parameters for easier access
        final int colsA = a.columns();
        final int rowsA = a.rows();
        final int colsB = b.columns();
        final int rowsB = b.rows();

        for (int i = 0; i < rowsA; i++)
        {
            for (int j = 0; j < colsA; j++)
            {
                target.set(i, j, a.get(i, j) - b.get(i, j));
            }
        }
    }

    //@Override
    public void subtractSparse(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        if (a != target)
        {
            target.zero();

            for (IDoubleIterator it = a.nonzeroIterator(); it.hasNext(); it.advance())
            {
                target.set(it.row(), it.column(), it.get());
            }
        }

        for (IDoubleIterator it = b.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            target.set(i, j, target.get(i, j) - it.get());
        }
    }

    public IComplexArray subtractToNewDense(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        subtractDense(a, b, target);
        return target;
    }

    public IComplexArray subtractToNewSparse(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        subtractSparse(a, b, target);
        return target;
    }

    public void subtractFromDense(final IComplexArray a, final IComplexArray b)
    {
        subtractDense(a, b, a);
    }

    public void subtractFromSparse(final IComplexArray a, final IComplexArray b)
    {
        assertEqualDimensions(a, b);

        for (IComplexIterator it = b.nonzeroComplexIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            a.set(it.row(), it.column(), a.getRe(i, j) + it.getRe(), b.getIm(i, j) + it.getIm());
        }
    }

    //@Override
    public void subtractDense(final IComplexArray a, final IComplexArray b, final IComplexArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        // Extract some parameters for easier access
        final int colsA = a.columns();
        final int rowsA = a.rows();

        // For each row ...
        for (int j = 0; j < rowsA; j++)
        {
            // For each column
            for (int i = 0; i < colsA; i++)
            {

                target.set(i, j, a.getRe(i, j) - b.getRe(i, j), a.getIm(i, j) - b.getIm(i, j));
            }
        }
    }

    //@Override
    public void subtractSparse(final IComplexArray a, final IComplexArray b, final IComplexArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        if (a != target)
        {
            target.zero();

            for (IComplexIterator it = a.nonzeroComplexIterator(); it.hasNext(); it.advance())
            {
                target.set(it.row(), it.column(), it.getRe(), it.getIm());
            }
        }

        for (IComplexIterator it = b.nonzeroComplexIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            target.set(i, j, target.getRe(i, j) - it.getRe(), target.getIm(i, j) - it.getIm());
        }
    }
}
