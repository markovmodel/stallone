/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.doubles.AbstractDoubleArray;

/**
 *
 * @author noe
 */
public class SymmetricMatrix extends AbstractDoubleArray
{
    private IDoubleArray underlyingMatrix;
    private int rows, cols;

    public SymmetricMatrix(IDoubleArray _underlyingMatrix)
    {
        rows = _underlyingMatrix.rows();
        cols = _underlyingMatrix.columns();
        this.underlyingMatrix = _underlyingMatrix;
    }

    @Override
    public IDoubleArray create(int rows, int cols)
    {
        return(underlyingMatrix.create(rows,cols));
    }

    @Override
    public IDoubleArray copy()
    {
        return(underlyingMatrix.copy());
    }

    @Override
    public void zero()
    {
        underlyingMatrix.zero();
    }

    @Override
    public IDoubleArray create(int size)
    {
        return(underlyingMatrix.create(size));
    }

        @Override
    public double get(int i, int j)
    {
        if (i<j)
            return(underlyingMatrix.get(i,j));
        else
            return(underlyingMatrix.get(j,i));
    }

        @Override
    public void set(int i, int j, double x)
    {
        if (i<j)
            underlyingMatrix.set(i,j,x);
        else
            underlyingMatrix.set(j,i,x);
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
    public boolean isSparse()
    {
        return underlyingMatrix.isSparse();
    }
}
