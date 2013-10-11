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

import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

/**
 * @author  Martin Senne
 */
public class ArrayNumericalEquality //implements INumericalEquality<IMatrix> 
{
    public boolean numericallyEqual(final IDoubleArray o1, final IDoubleArray o2, final double precision)
    {
        final int rows = o1.rows();
        final int cols = o1.columns();

        if (rows != o2.rows() || cols != o2.columns())
        {
            return false;
        }

        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (Math.abs(o1.get(i, j) - o2.get(i, j)) > precision)
                {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean numericallyEqual(final IComplexArray o1, final IComplexArray o2, final double precision)
    {
        final int rows = o1.rows();
        final int cols = o1.columns();

        if (rows != o2.rows() || cols != o2.columns())
        {
            return false;
        }

        double p2 = precision*precision;
        double diffRe, diffIm;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                diffRe = o1.getRe(i, j) - o2.getRe(i, j);
                diffIm = o1.getIm(i, j) - o2.getIm(i, j);
                if (diffRe*diffRe + diffIm*diffIm > p2)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
