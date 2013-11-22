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

import stallone.doubles.PrimitiveDoubleArray;
import stallone.doubles.AbstractDoubleArray;
import stallone.api.doubles.IDoubleArray;
import static stallone.doubles.DoubleArrayTest.*;

/**
 *
 * @author  Martin Senne
 */
public class WrappedMTJVector extends AbstractDoubleArray {

    private no.uib.cipr.matrix.Vector mtjVector;
    private int size;

    public WrappedMTJVector(no.uib.cipr.matrix.Vector mtjVector) {
        size = mtjVector.size();
        this.mtjVector = mtjVector;
    }

    @Override
    public double get(int i) {
        return mtjVector.get(i);
    }

    @Override
    public double get(int i, int j)
    {
        assertColumnExists(this, j);
        return mtjVector.get(i);
    }

    @Override
    public void set(int i, double x)
    {
        mtjVector.set(i,x);
    }

    @Override
    public void set(int i, int j, double x)
    {
        assertColumnExists(this, j);
        mtjVector.set(i,x);
    }



    @Override
    public void zero() {
        mtjVector.zero();
    }

    @Override
    public IDoubleArray create(int size)
    {
        throw new UnsupportedOperationException("Cannot create a wrapped MTJ matrix.");
    }

    @Override
    public IDoubleArray create(int rows, int cols)
    {
        throw new UnsupportedOperationException("Cannot create a wrapped MTJ matrix.");
    }

    @Override
    public int rows()
    {
        return size;
    }

    @Override
    public int columns()
    {
        return 1;
    }

    @Override
    public IDoubleArray copy()
    {
        IDoubleArray res = new PrimitiveDoubleArray(size);
        res.copyFrom(this);
        return res;
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }


}
