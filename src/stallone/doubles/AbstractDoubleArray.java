/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;

/**
 *
 * @author noe
 */
public abstract class AbstractDoubleArray implements IDoubleArray
{

    @Override
    public int size()
    {
        return(rows()*columns());
    }

    @Override
    public int order()
    {
        int order = 0;
        if (rows() > 1)
        {
            order++;
        }
        if (columns() > 1)
        {
            order++;
        }
        return (order);
    }

    @Override
    public void zero()
    {
        for (IDoubleIterator it = iterator(); it.hasNext(); it.advance())
            it.set(0);
    }

    @Override
    public double get(int ij)
    {
        int cols = columns();
        return (get(ij / cols, ij % cols));
    }

    @Override
    public void set(int ij, double x)
    {
        int cols = columns();
        set(ij / cols, ij % cols, x);
    }

    @Override
    public double[] getArray()
    {
        int size = size();
        double[] res = new double[size];

        for (int i = 0; i < size; i++)
        {
            res[i] = get(i);
        }

        return res;
    }

    @Override
    public double[][] getTable()
    {
        int nrows = rows();
        int ncols = columns();
        double[][] res = new double[nrows][ncols];
        for (int i = 0; i < nrows; i++)
        {
            for (int j = 0; j < ncols; j++)
            {
                res[i][j] = get(i, j);
            }
        }

        return res;
    }

    @Override
    public double[] getRow(int row)
    {
        int ncols = columns();
        double[] res = new double[ncols];

        for (int i = 0; i < ncols; i++)
        {
            res[i] = get(row, i);
        }

        return res;
    }

    @Override
    public double[] getColumn(int col)
    {
        int nrows = rows();
        double[] res = new double[nrows];

        for (int i = 0; i < nrows; i++)
        {
            res[i] = get(i, col);
        }

        return res;
    }

    @Override
    public IDoubleIterator iterator()
    {
        return (new DoubleTableIterator(this));
    }

    @Override
    public IDoubleIterator nonzeroIterator()
    {
        return (new DoubleTableNonzeroIterator(this));
    }

    @Override
    public void copyFrom(IDoubleArray other)
    {
        int size = size();
        if (size != other.size())
        {
            throw (new IllegalArgumentException("Incosistent sizes: This array has size " + size + " the other array has size " + other.size()));
        }

        for (int i = 0; i < size; i++)
        {
            set(i, other.get(i));
        }
    }

    @Override
    public void copyInto(IDoubleArray other)
    {
        int size = size();
        if (size != other.size())
        {
            throw (new IllegalArgumentException("Incosistent sizes: This array has size " + size + " the other array has size " + other.size()));
        }

        for (int i = 0; i < size; i++)
        {
            other.set(i, get(i));
        }
    }

    @Override
    public IDoubleArray viewRow(int i)
    {
        return (new DoubleArrayView(this, i, 0, i + 1, columns()));
    }

    @Override
    public IDoubleArray viewColumn(int j)
    {
        return (new DoubleArrayView(this, 0, j, rows(), j + 1));
    }

    @Override
    public IDoubleArray viewBlock(int top, int left, int bottom, int right)
    {
        return (new DoubleArrayView(this, top, left, bottom, right));
    }

    @Override
    public IDoubleArray view(int[] selectedRows, int[] selectedColumns)
    {
        return (new DoubleArrayView(this, selectedRows, selectedColumns));
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof IDoubleArray))
        {
            return (false);
        }

        IDoubleArray oo = (IDoubleArray) o;

        if (oo.size () != size())
            return false;

        if (oo.rows() != rows())
            return false;

        if (oo.columns() != columns())
            return false;

        for (int i = 0; i < oo.size(); i++)
        {
            if (oo.get(i) != this.get(i))
            {
                return (false);
            }
        }
        return (true);
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        return hash;
    }

    @Override
    public String toString()
    {
        return(Doubles.util.toString(this));
    }

}
