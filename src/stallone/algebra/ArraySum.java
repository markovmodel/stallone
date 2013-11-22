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
public class ArraySum //implements IMatrixSum
{

    public IDoubleArray addToNewDense(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        sumDense(a, b, target);
        return target;
    }

    public IDoubleArray addToNewSparse(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        sumSparse(a, b, target);
        return target;
    }

    public void addToSparse(final IDoubleArray a, final IDoubleArray b)
    {
        assertEqualDimensions(a, b);

        for (IDoubleIterator it = b.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            a.set(i, j, a.get(i, j) + it.get());
        }
    }

    public void addToDense(final IDoubleArray a, final IDoubleArray b)
    {
        sumDense(a, b, a);
    }

    public void sumDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        // direct access may not be efficient
        // Extract some parameters for easier access
        final int rowsA = a.rows();
        final int colsA = a.columns();

        for (int i = 0; i < rowsA; i++)
        {
            for (int j = 0; j < colsA; j++)
            {
                target.set(i, j, a.get(i, j) + b.get(i, j));
            }
        }
    }

    //@Override
    public void sumSparse(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
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
            target.set(i, j, target.get(i, j) + it.get());
        }
    }

    public IComplexArray addToNewDense(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        sumDense(a, b, target);
        return target;
    }

    public IComplexArray addToNewSparse(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        sumSparse(a, b, target);
        return target;
    }

    public void addToDense(final IComplexArray a, final IComplexArray b)
    {
        sumDense(a, b, a);
    }

    public void addToSparse(final IComplexArray a, final IComplexArray b)
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
    public void sumDense(final IComplexArray a, final IComplexArray b, final IComplexArray target)
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

                target.set(i, j, a.getRe(i, j) + b.getRe(i, j), a.getIm(i, j) + b.getIm(i, j));
            }
        }
    }

    //@Override
    public void sumSparse(final IComplexArray a, final IComplexArray b, final IComplexArray target)
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
            target.set(i, j, target.getRe(i, j) + it.getRe(), target.getIm(i, j) + it.getIm());
        }
    }
}
