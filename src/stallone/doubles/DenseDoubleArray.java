/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.doubles;

import static stallone.api.API.alg;
import stallone.api.API;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * Universal class for order 1 or 2 arrays, where the data is stored as an economic 1D-array.
 * Matrices are stored in row major format
 * 
 * @author noe
 */
public class DenseDoubleArray extends AbstractDoubleArray
{
    protected double[] x;
    private int nrow, ncol;

    /**
     * Creates a column vector
     * @param n 
     */
    public DenseDoubleArray(int n)
    {
        x = new double[n];
        nrow = n;
        ncol = 1;
    }

    /**
     * Creates a matrix
     * @param _nrow
     * @param _ncol 
     */
    public DenseDoubleArray(int _nrow, int _ncol)
    {
        nrow = _nrow;
        ncol = _ncol;
        x = new double[_nrow*_ncol];
    }

    /**
     * Creates a column vector initialized by _x
     * @param _x 
     */
    public DenseDoubleArray(double[] _x)
    {
        nrow = _x.length;
        ncol = 1;
        this.x = _x;
    }

    /**
     * Creates a matrix initialized by _x. Attention: Data is copied!
     * @param _x 
     */
    public DenseDoubleArray(double[][] _x)
    {
        nrow = _x.length;
        ncol = _x[0].length;
        x = new double[nrow*ncol];
        int k=0;
        for (int i=0; i<nrow; i++)
            for (int j=0; j<ncol; j++)
                x[k++] = _x[i][j];
    }

    /**
     * Creates a matrix initialized by _x. Attention: Data is copied!
     * @param _x 
     */
    public DenseDoubleArray(IDoubleArray _x)
    {
        nrow = _x.rows();
        ncol = _x.columns();
        x = new double[nrow*ncol];
        int k=0;
        for (int i=0; i<nrow; i++)
            for (int j=0; j<ncol; j++)
                x[k++] = _x.get(i,j);
    }
    
    /**
     * Reshapes the array
     * @param i
     * @return 
     */
    public void reshape(int nrownew, int ncolnew)
    {
        if (nrownew*ncolnew != nrow*ncol)
            throw(new IllegalArgumentException("Trying to reshape a ("+nrow+","+ncol+") array into a ("+nrow+","+ncol+") array failed. Shapes are not compatible"));
        nrow = nrownew;
        ncol = ncolnew;
    }
            
    
    @Override
    public double get(int i)
    {
        try
        {
            return(x[i]);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error: Trying to access element ("+i+","+0+") in a ("+nrow+","+ncol+") array"));
        }
    }

    @Override
    public double get(int i, int j)
    {
        try
        {
            // column vector access
            if (ncol == 1 && j == 0)
                return x[i];
            // row vector access
            if (nrow == 1 && i == 0)
                return x[j];
            // matrix access
            return x[i*ncol + j];
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error: Trying to access element ("+i+","+j+") in a ("+nrow+","+ncol+") array"));
        }
    }

    @Override
    public void set(int i, double v)
    {
        try
        {
            x[i] = v;
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error: Trying to access element ("+i+","+0+") in a ("+nrow+","+ncol+") array"));
        }
    }

    @Override
    public void set(int i, int j, double val)
    {
        try
        {
            // column vector access
            if (ncol == 1 && j == 0)
                x[i] = val;
            // row vector access
            else if (nrow == 1 && i == 0)
                x[j] = val;
            else
            // matrix access
                x[i*ncol + j] = val;
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error: Trying to access element ("+i+","+j+") in a ("+nrow+","+ncol+") array"));
        }
    }
    @Override
    public void copyFrom(IDoubleArray other)
    {
        for (int i=0; i<other.size(); i++)
            x[i] = other.get(i);
    }

    public void set(double[] _x)
    {
        this.x = _x;
        nrow = _x.length;
        ncol = 1;
    }

    @Override
    public double[] getArray()
    {
        return(x);
    }

    @Override
    public int size()
    {
        return(x.length);
    }

    @Override
    public IDoubleArray copy()
    {
        DenseDoubleArray res = new DenseDoubleArray(PrimitiveDoubleTools.copy(x));
        res.reshape(nrow, ncol);
        return(res);
    }

    @Override
    public IDoubleArray create(int size)
    {
        return(new DenseDoubleArray(new double[size]));
    }

    @Override
    public int order()
    {
        if (nrow == 1 || ncol == 1)
            return 1;
        else
            return 2;
    }

    @Override
    public int rows()
    {
        return(nrow);
    }

    @Override
    public int columns()
    {
        return(ncol);
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        return(new DenseDoubleArray(rows, columns));
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }
    
    public static void main(String[] args)
    {
        DenseDoubleArray da = new DenseDoubleArray(2,2);
        
        da.set(0,0,0.9);
        da.set(0,1,0.1);
        da.set(1,0,0.1);
        da.set(1,1,0.9);
    
        
        IDoubleArray da2 = alg.product(da, da);
        System.out.println(da2);
    }

}
