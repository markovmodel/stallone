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
package stallone.doubles.mtj;

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleArray;
import stallone.doubles.AbstractDoubleArray;

/**
 * This class wraps a MTJ matrix into the Sherlock IMatrix interfaces.
 *
 * @author  Martin Senne
 */
public class WrappedMTJMatrix extends AbstractDoubleArray
{
    private no.uib.cipr.matrix.Matrix mtjMatrix;
    private int rows,cols;

    public WrappedMTJMatrix(no.uib.cipr.matrix.Matrix mtjMatrix)
    {
        rows = mtjMatrix.numRows();
        cols = mtjMatrix.numColumns();
        this.mtjMatrix = mtjMatrix;
    }

    @Override
    public double get(int i, int j)
    {
        return mtjMatrix.get(i, j);
    }

        @Override
    public void set(int i, int j, double x)
    {
        mtjMatrix.set(i, j, x);
    }

    @Override
    public void zero() {
        mtjMatrix.zero();
    }

    @Override
    public IDoubleArray create(int rows, int cols)
    {
        throw new UnsupportedOperationException("Cannot copy a wrapped MTJ matrix.");
    }

    @Override
    public IDoubleArray create(int size)
    {
        throw new UnsupportedOperationException("Matrix create with single size variable not supported.");
    }

    @Override
    public int rows()
    {
        return rows;
    }

    @Override
    public int columns()
    {
        return cols;
    }

    @Override
    public IDoubleArray copy()
    {
        IDoubleArray res = doublesNew.matrix(rows,cols);
        res.copyFrom(this);
        return(res);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

}
