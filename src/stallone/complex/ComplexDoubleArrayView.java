/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.complex;

import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class ComplexDoubleArrayView extends AbstractComplexArray
{
    private IDoubleArray darr;
    private ComplexArrayView rowview = null;
    private ComplexArrayView colview = null;

    public ComplexDoubleArrayView(IDoubleArray _darr)
    {
        this.darr = _darr;
    }

    @Override
    public boolean isReal()
    {
        return(true);
    }

    @Override
    public double getRe(int i)
    {
        return(darr.get(i));
    }

    @Override
    public double getRe(int i, int j)
    {
        return(darr.get(i,j));
    }

    @Override
    public void setRe(int i, double x)
    {
        darr.set(i,x);
    }

    @Override
    public void setRe(int i, int j, double x)
    {
        darr.set(i,j,x);
    }

    @Override
    public double getIm(int i)
    {
        throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public double getIm(int i, int j)
    {
        throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public void setIm(int i, double x)
    {
        throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public void setIm(int i, int j, double x)
    {
        throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public double[] getRealArray()
    {
        return(darr.getArray());
    }

    @Override
    public double[] getImaginaryArray()
    {
        return(new double[darr.size()]);
    }

    @Override
    public double[][] getRealTable()
    {
        return(darr.getTable());
    }

    @Override
    public double[][] getImaginaryTable()
    {
        return(new double[darr.rows()][darr.columns()]);
    }

    @Override
    public double[] getRealRow(int i)
    {
        return(darr.getRow(i));
    }

    @Override
    public double[] getImaginaryRow(int i)
    {
        return(new double[darr.columns()]);
    }

    @Override
    public double[] getRealColumn(int j)
    {
        return(darr.getColumn(j));
    }

    @Override
    public double[] getImaginaryColumn(int j)
    {
        return(new double[darr.rows()]);
    }

    @Override
    public IComplexArray copy()
    {
        return(new ComplexDoubleArrayView(darr.copy()));
    }

    @Override
    public int order()
    {
        return(darr.order());
    }

    @Override
    public int rows()
    {
        return(darr.rows());
    }

    @Override
    public int columns()
    {
        return(darr.columns());
    }

    @Override
    public double[] getArray()
    {
        return(darr.getArray());
    }

    @Override
    public double[][] getTable()
    {
        return(darr.getTable());
    }

    @Override
    public double[] getRow(int i)
    {
        return(darr.getRow(i));
    }

    @Override
    public double[] getColumn(int j)
    {
        return(darr.getColumn(j));
    }


    @Override
    public IComplexArray create(int size)
    {
        return(new ComplexDoubleArrayView(darr.create(size)));
    }

    @Override
    public void set(int i, double re, double im)
    {
        int cols = columns();
        darr.set(i/cols,i%cols,re);

        if (im != 0)
            throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public void set(int i, int j, double re, double im)
    {
        darr.set(i,j,re);

        if (im != 0)
            throw new UnsupportedOperationException("No imaginary part supported");
    }

    @Override
    public IComplexArray create(int rows, int cols)
    {
        return(new ComplexDoubleArrayView(darr.create(rows, cols)));
    }

    @Override
    public boolean isSparse()
    {
        return darr.isSparse();
    }



}
