/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.doubles;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class PrimitiveDoubleArray extends AbstractDoubleArray
{
    protected double[] x;

    public PrimitiveDoubleArray(int n)
    {
        x = new double[n];
    }

    public PrimitiveDoubleArray(double[] _x)
    {
        this.x = _x;
    }

    @Override
    public double get(int i)
    {
        return(x[i]);
    }
    
    @Override
    public double get(int i, int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a column vector. Cannot access column index "+j));
        
        return(x[i]);
    }

    @Override
    public void set(int i, double v)
    {
        x[i] = v;
    }
    
    @Override
    public void set(int i, int j, double val)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a column vector. Cannot access column index "+j));
        
        x[i] = val;
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
        return(new PrimitiveDoubleArray(PrimitiveDoubleTools.copy(x)));
    }

    @Override
    public IDoubleArray create(int size)
    {
        return(new PrimitiveDoubleArray(new double[size]));
    }
    
    @Override
    public int order()
    {
        return(1);
    }

    @Override
    public int rows()
    {
        return(x.length);
    }

    @Override
    public int columns()
    {
        return(1);
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        return(new PrimitiveDoubleTable(rows, columns));
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }
        
}
