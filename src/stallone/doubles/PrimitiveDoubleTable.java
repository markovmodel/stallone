package stallone.doubles;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;



/**
 *
 * @author noe
 */
public class PrimitiveDoubleTable extends AbstractDoubleArray
{
    protected double[][] x;
    private int ndim = 3;
//    private DoubleArrayView rowview = null;
//    private DoubleArrayView colview = null;

    public PrimitiveDoubleTable(double[][] _x)
    {
        this.x = _x;
        this.ndim = x[0].length;
        
        for (int i=1; i<_x.length; i++)
            if (_x[i].length != ndim)
                throw(new IllegalArgumentException("Trying to construct DoubleTable with ragged array. Only rectangular arrays are permitted."));
    }

    public PrimitiveDoubleTable(int rows, int cols)
    {
        this(new double[rows][cols]);
    }

    @Override
    public double get(int row, int col)
    {
        return (x[row][col]);
    }

    @Override
    public void set(int row, int col, double val)
    {
        x[row][col] = val;
    }

    @Override
    public double get(int i)
    {
        int row = i / ndim;
        int col = i % ndim;
        return (x[row][col]);
    }

    @Override
    public void set(int i, double v)
    {
        int row = i / ndim;
        int col = i % ndim;
        x[row][col] = v;
    }

    @Override
    public double[] getArray()
    {
        return (PrimitiveDoubleTools.flatten(x));
    }

    @Override
    public double[][] getTable()
    {
        return (x);
    }

    @Override
    public int size()
    {
        return (x.length * x[0].length);
    }

    @Override
    public int rows()
    {
        return (x.length);
    }

    @Override
    public int columns()
    {
        return (x[0].length);
    }

    @Override
    public IDoubleArray copy()
    {
        return (new PrimitiveDoubleTable(PrimitiveDoubleTools.copy(x)));
    }

    @Override
    public IDoubleArray create(int size)
    {
        return (new PrimitiveDoubleTable(size,1));
    }
    
    @Override
    public IDoubleArray create(int rows, int cols)
    {
        return (new PrimitiveDoubleTable(rows,cols));
    }

    @Override
    public double[] getRow(int i)
    {
        return (x[i]);
    }

    @Override
    public double[] getColumn(int i)
    {
        return(PrimitiveDoubleTools.getColumn(x,i));
    }

    /*@Override
    public Iterator rowIterator()
    {
        class PrimitiveDoubleTableRowIterator implements Iterator<IDoubleArray>
        {
            int i=0;
            double[][] x;
            PrimitiveDoubleTableRowIterator(double[][] _x)
            {
                x=_x;
            }

            @Override
            public boolean hasNext()
            {
                return(i<x.length);
            }

            @Override
            public IDoubleArray next()
            {
                IDoubleArray res = new PrimitiveDoubleArray(x[i]);
                i++;
                return(res);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Not supported.");
            }
        }
        return(new PrimitiveDoubleTableRowIterator(this.x));
    }*/

    @Override
    public void copyInto(IDoubleArray other)
    {
        if (size() != other.size())
            throw(new IllegalArgumentException("Inconsistent sizes. This array has size "+size()+" other array has size "+other.size()));
        
        Doubles.util.copyInto(this, 0, size(), other, 0);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }
    

}


