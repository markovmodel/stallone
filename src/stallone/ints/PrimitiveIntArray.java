/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.ints;


import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;

/**
 *
 * @author noe
 */
public class PrimitiveIntArray implements IIntArray
{
    protected int[] x;

    public PrimitiveIntArray(int n)
    {
        x = new int[n];
    }

    public PrimitiveIntArray(int[] _x)
    {
        this.x = _x;
    }

    @Override
    public int get(int i)
    {
        return(x[i]);
    }

    @Override
    public void set(int i, int v)
    {
        x[i] = v;
    }

    @Override
    public int[] getArray()
    {
        return(x);
    }

    @Override
    public int size()
    {
        return(x.length);
    }

    @Override
    public IIntArray copy()
    {
        int[] xcopy = java.util.Arrays.copyOf(x, x.length);
        return(new PrimitiveIntArray(xcopy));
    }

    @Override
    public IIntArray create(int size)
    {
        return(new PrimitiveIntArray(new int[size]));
    }

    @Override
    public IIntIterator iterator()
    {
        return(new IntArrayIterator(this));
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

    public String toString()
    {
        return(PrimitiveIntTools.toString(x));
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
    public int get(int i, int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        return(x[i]);
    }

    @Override
    public void set(int i, int j, int _x)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a list. Column index "+j+" does not exist!"));

        x[i] = _x;
    }

    @Override
    public int[][] getTable()
    {
        int[][] res = new int[size()][1];
        for (int i=0; i<res.length; i++)
            res[i][0] = x[i];
        return(res);
    }

    @Override
    public int[] getRow(int i)
    {
        return(new int[]{x[i]});
    }

    @Override
    public int[] getColumn(int j)
    {
        return(x);
    }

    @Override
    public IIntIterator nonzeroIterator()
    {
        return(new IntArrayNonzeroIterator(this));
    }

    @Override
    public void copyInto(IIntArray other)
    {
        if (other.size() != size())
            throw (new IllegalArgumentException("Incompatible sizes. This array has size "+size()+", the other array has size "+other.size()));

        Ints.util.copyInto(this, 0, size(), other, 0);
    }

    @Override
    public IIntArray create(int rows, int columns)
    {
        return(new PrimitiveIntTable(rows, columns));
    }

    @Override
    public IIntArray viewRow(int i)
    {
        return(new IntArrayView(this, i, 0, i+1, 1));
    }

    @Override
    public IIntArray viewColumn(int j)
    {
        if (j != 0)
            throw(new ArrayIndexOutOfBoundsException("This is a column array. Column index "+j+" does not exist!"));

        return(this);
    }

    @Override
    public void copyFrom(IIntArray other)
    {
        if (other.size() != size())
            throw (new IllegalArgumentException("Incompatible sizes. This array has size "+size()+", the other array has size "+other.size()));

        Ints.util.copyInto(other, 0, size(), this, 0);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

}
