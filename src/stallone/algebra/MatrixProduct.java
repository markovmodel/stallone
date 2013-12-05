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

import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * Generic implementation of IMatrixProduct for complex operands.
 *
 * @author  Frank Noe
 */
public class MatrixProduct //implements IMatrixProduct
{
    InnerProduct ip = new InnerProduct(true);

    public IDoubleArray multiplyToNew(final IDoubleArray a, final IDoubleArray b)
    {
        IDoubleArray res;
        if (a.isSparse() && b.isSparse())
        {
            res = doublesNew.sparseMatrix(a.rows(), b.columns());
            multiplySparseSparse(a,b,res);
        }
        else if (a.isSparse() && !b.isSparse())
        {
            res = doublesNew.sparseMatrix(a.rows(), b.columns());
            multiplySparseDense(a,b,res);
        }
        else
        {
            res = doublesNew.denseMatrix(a.rows(), b.columns());
            multiply(a,b,res);
        }
        return res;
    }

    public IDoubleArray multiply(final IDoubleArray a, final IDoubleArray b, final IDoubleArray res)
    {
        if (a.isSparse() && b.isSparse())
        {
            multiplySparseSparse(a,b,res);
        }
        else if (a.isSparse() && !b.isSparse())
        {
            multiplySparseDense(a,b,res);
        }
        else
        {
            multiply(a,b,res);
        }
        return res;
    }
    
    //@Override
    public void multiplyDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        // Extract some parameters for easier access
        final int r = a.columns();
        final int colsB = b.columns();
        final int rowsA = a.rows();

        // check dimensions
        assertCanMultiply(a,b);
        assertRows(target, a.rows());
        assertColumns(target, b.columns());

        for (int l = 0; l < colsB; l++)
        {
            for (int k = 0; k < rowsA; k++)
            {
                double val = 0.0d;

                for (int i = 0; i < r; i++)
                {
                    val += (a.get(k, i) * b.get(i, l));
                }

                target.set(k, l, val);
            }
        }
    }

    public void multiplySparseDense(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        // Extract some parameters for easier access
        final int r = a.columns();
        final int colsB = b.columns();
        final int rowsA = a.rows();

        // check dimensions
        assertCanMultiply(a,b);
        assertRows(target, a.rows());
        assertColumns(target, b.columns());

//        IDoubleArray ri,cj;
        for (int i=0; i<rowsA; i++)
        for (int j=0; j<colsB; j++)
        {
//            ri = a.viewRow(i);
//            cj = b.viewColumn(j);
            target.set(i, j, ip.innerProductSparseDense(a.viewRow(i), b.viewColumn(j)));
        }
    }

    public void multiplySparseSparse(final IDoubleArray a, final IDoubleArray b, final IDoubleArray target)
    {
        // Extract some parameters for easier access
        final int r = a.columns();
        final int colsB = b.columns();
        final int rowsA = a.rows();

        // check dimensions
        assertCanMultiply(a,b);
        assertRows(target, a.rows());
        assertColumns(target, b.columns());

//        IDoubleArray ri,cj;
        for (int i=0; i<rowsA; i++)
        for (int j=0; j<colsB; j++)
        {
//            ri = a.viewRow(i);
//            cj = b.viewColumn(j);
            target.set(i, j, ip.innerProductSparseSparse(a.viewRow(i), b.viewColumn(j)));
        }
    }
    

    public IComplexArray multiplyToNew(final IComplexArray a, final IComplexArray b)
    {
        IComplexArray res = a.create(a.rows(), b.columns());
        multiply(a,b,res);
        return res;
    }

    public void multiply(final IComplexArray a, final IComplexArray b, final IComplexArray target)
    {
        // Extract some parameters for easier access
        final int r = a.columns();
        final int colsB = b.columns();
        final int rowsA = a.rows();

        // check dimensions
        assertCanMultiply(a,b);
        assertRows(target, a.rows());
        assertRows(target, b.columns());

        double sumRe, sumIm, aRe, aIm, bRe, bIm;

        for (int l = 0; l < colsB; l++)
        {
            for (int k = 0; k < rowsA; k++)
            {
                sumRe = 0.0d;
                sumIm = 0.0d;

                for (int i = 0; i < r; i++)
                {
                    aRe = a.getRe(k,i);
                    aIm = a.getIm(k,i);
                    bRe = b.getRe(i,l);
                    bIm = b.getIm(i,l);
                    sumRe += aRe*bRe - aIm*bIm;
                    sumIm += aRe*bIm + aIm*bRe;
                }

                target.setRe(k, l, sumRe);
                target.setIm(k, l, sumIm);
            }
        }
    }

    public void sparseMultiply(final IComplexArray a, final IComplexArray b, final IComplexArray target)
    {
        // Extract some parameters for easier access
        final int r = a.columns();
        final int colsB = b.columns();
        final int rowsA = a.rows();

        // check dimensions
        assertCanMultiply(a,b);
        assertRows(target, a.rows());
        assertRows(target, b.columns());

        IComplexArray ri,cj;
        for (int i=0; i<rowsA; i++)
        for (int j=0; j<colsB; j++)
        {
            ri = a.viewRow(i);
            cj = b.viewColumn(j);
            target.set(i, j, ip.innerProductSparseSparse(a, b));
        }
    }

}
