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
public class ArrayElementProduct //implements IMatrixSum 
{
    public IDoubleArray multiplyToNewDense(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        multiplyDense(a,b,target);
        return target;
    }

    public IDoubleArray multiplyToNewSparse(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        multiplySparse(a,b,target);
        return target;
    }
    
    public void multiplyToSparse(final IDoubleArray a, final IDoubleArray b)
    {
        multiplySparse(a,b,a);
    }

    public void multiplyToDense(final IDoubleArray a, final IDoubleArray b)
    {
        multiplyDense(a,b,a);
    }
    
    public void multiplyDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        // direct access may not be efficient
        // Extract some parameters for easier access
        final int colsA = a.columns();
        final int rowsA = a.rows();

        for (int i = 0; i < rowsA; i++)
        {
            for (int j = 0; j < colsA; j++)
            {
                target.set(i, j, a.get(i, j) * b.get(i, j));
            }
        }
    }
    
    //@Override
    public void multiplySparse(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        if (a != target)
        {
            target.zero();
        
            for (IDoubleIterator it = a.nonzeroIterator(); it.hasNext(); it.advance())
                target.set(it.row(), it.column(), it.get());
        }

        for (IDoubleIterator it = b.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            target.set(i, j, target.get(i,j) * it.get());        
        }
      }
    
    public IComplexArray multiplyToNewDense(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        multiplyDense(a,b,target);
        return target;
    }

    public IComplexArray multiplyToNewSparse(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray target = a.copy();
        multiplySparse(a,b,target);
        return target;
    }    
    
    public void multiplyToDense(final IComplexArray a, final IComplexArray b)
    {
        multiplyDense(a,b,a);
    }
    
    public void multiplyToSparse(final IComplexArray a, final IComplexArray b)
    {
        multiplySparse(a,b,a);
    }
    
    //@Override
    public void multiplyDense(final IComplexArray a, final IComplexArray b, final IComplexArray target)
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
                double aRe = a.getRe(i,j);
                double aIm = a.getIm(i,j);
                double bRe = b.getRe(i,j);
                double bIm = b.getIm(i,j);

                target.set(i, j, aRe*bRe - aIm*bIm, aRe*bIm + aIm*bRe);
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
                target.set(it.row(), it.column(), it.getRe(), it.getIm());
        }
        
        for (IComplexIterator it = b.nonzeroComplexIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();

                double aRe = it.getRe();
                double aIm = it.getIm();
                double bRe = b.getRe(i,j);
                double bIm = b.getIm(i,j);

                target.set(i, j, aRe*bRe - aIm*bIm, aRe*bIm + aIm*bRe);
        }
      }    
}
