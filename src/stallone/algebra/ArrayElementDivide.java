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

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * Generic implementation of IMatrixSum for complex operands.
 *
 * @author  Frank Noe
 */
public class ArrayElementDivide //implements IMatrixSum 
{
    public IDoubleArray divideToNewDense(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        divideDense(a,b,target);
        return target;
    }

    public IDoubleArray divideToNewSparse(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray target = a.copy();
        divideSparse(a,b,target);
        return target;
    }
    
    public void divideToSparse(final IDoubleArray a, final IDoubleArray b)
    {
        divideSparse(a,b,a);
    }

    public void divideToDense(final IDoubleArray a, final IDoubleArray b)
    {
        divideDense(a,b,a);
    }
    
    public void divideDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        // direct access may not be efficient
        // Extract some parameters for easier access
        final int colsA = a.columns();
        final int rowsA = a.rows();

        for (int j = 0; j < rowsA; j++)
        {
            for (int i = 0; i < colsA; i++)
            {
                target.set(i, j, a.get(i, j) / b.get(i, j));
            }
        }
    }
    
    //@Override
    public void divideSparse(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        assertEqualDimensions(a, b);
        assertEqualDimensions(a, target);

        if (a != target)
        {
            target.zero();
        }
        
        for (IDoubleIterator it = a.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            target.set(i, j, it.get()/b.get(i,j));
        }        
      }
    

}
