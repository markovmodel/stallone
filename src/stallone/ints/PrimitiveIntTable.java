package stallone.ints;

import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;



/**
 *
 * @author noe
 */
public class PrimitiveIntTable implements IIntArray
{
    protected int[][] x;
    private int ndim = 3;
    private IntArrayView rowview = null;
    private IntArrayView colview = null;

    public PrimitiveIntTable(int[][] _x)
    {
        this.x = _x;
        this.ndim = x[0].length;
        
        for (int i=1; i<_x.length; i++)
            if (_x[i].length != ndim)
                throw(new IllegalArgumentException("Trying to construct DoubleTable with ragged array. Only rectangular arrays are permitted."));
    }

    public PrimitiveIntTable(int rows, int cols)
    {
        this(new int[rows][cols]);
    }

    @Override
    public int get(int row, int col)
    {
        return (x[row][col]);
    }

    @Override
    public void set(int row, int col, int val)
    {
        x[row][col] = val;
    }
    
    @Override
    public void copyFrom(IIntArray other)
    {
        int size = other.size();
        if (size != size())
        {
            throw(new IllegalArgumentException("Trying to copy from array with different size"));
        }
        for (int i=0; i<size; i++)
            set(i, other.get(i));
    }

    @Override
    public int get(int i)
    {
        int row = i / ndim;
        int col = i % ndim;
        return (x[row][col]);
    }

    @Override
    public void set(int i, int v)
    {
        int row = i / ndim;
        int col = i % ndim;
        x[row][col] = v;
    }

    @Override
    public int[] getArray()
    {
        return (PrimitiveIntTools.flatten(x));
    }

    @Override
    public int[][] getTable()
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
    public IIntArray copy()
    {
        return (new PrimitiveIntTable(PrimitiveIntTools.copy(x)));
    }

    @Override
    public IIntArray create(int size)
    {
        throw new UnsupportedOperationException("Not supported.");
    }
    
    @Override
    public IIntArray create(int rows, int cols)
    {
        return (new PrimitiveIntTable(rows,cols));
    }

    
    @Override
    public IIntIterator iterator()
    {
        return (new IntTableIterator(this));
    }

    @Override
    public int[] getRow(int i)
    {
        return (x[i]);
    }

    @Override
    public int[] getColumn(int i)
    {
        return(PrimitiveIntTools.getColumn(x,i));
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof IIntArray))
        {
            return (false);
        }
        IIntArray oo = (IIntArray) o;
        for (int i = 0; i < oo.size(); i++)
        {
            if (oo.get(i) != this.get(i))
            {
                return (false);
            }
        }
        return (true);
    }

    /*@Override
    public Iterator rowIterator()
    {
        class PrimitiveDoubleTableRowIterator implements Iterator<IIntArray>
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
            public IIntArray next()
            {
                IIntArray res = new PrimitiveDoubleArray(x[i]);
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
    public String toString()
    {
        return(Ints.util.toString(this));
    }

    //@Override
    @Override
    public IIntArray viewRow(int i)
    {
        if (rowview == null)
        {
            rowview = new IntArrayView(this, i, 0, i+1, columns());
            return(rowview);
        }
        else
        {
            rowview.setView(i, 0, i+1, columns());
            return(rowview);
        }
    }

    @Override
    public IIntArray viewColumn(int j)
    {
        if (colview == null)
        {
            colview = new IntArrayView(this, 0, j, rows(), j+1);
            return(colview);
        }
        else
        {
            colview.setView(0, j, rows(), j+1);
            return(colview);
        }
    }

    @Override
    public int order()
    {
        return(2);
    }

    @Override
    public IIntIterator nonzeroIterator()
    {
        return(new IntTableNonzeroIterator(this));
    }

    @Override
    public void copyInto(IIntArray other)
    {
        if (size() != other.size())
            throw(new IllegalArgumentException("Inconsistent sizes. This array has size "+size()+" other array has size "+other.size()));
        
        Ints.util.copyInto(this, 0, size(), other, 0);
    }
    

}


