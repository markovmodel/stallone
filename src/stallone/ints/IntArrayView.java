/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import stallone.ints.PrimitiveIntTable;

/**
 *
 * @author noe
 */
public class IntArrayView implements IIntArray
{
    private IIntArray data;
    private int left, top, right, bottom;
    private int nrows, ncols, size;
    
    /**
     * Generates a view to the data using the window top,left (inclusive) to bottom,right (exclusive)
     */
    public IntArrayView(IIntArray _data, int _top, int _left, int _bottom, int _right)
    {
        this.data = _data;
        setView(_top, _left, _bottom, _right);
    }
    
    /**
     * Costructor for order 1 array views
     * @param _data
     * @param _from
     * @param _to 
     */
    public IntArrayView(IIntArray _data, int _from, int _to)
    {
        if (_data.order() > 1)
            throw(new IllegalArgumentException("Cannot use order-1 array view constructor for a table"));

        this.data = _data;
        this.setView(_from, _to);
    }
    
    public final void setView(int _top, int _left, int _bottom, int _right)
    {
        this.left = _left;
        this.top = _top;
        this.right = _right;
        this.bottom = _bottom;
        
        this.nrows = _bottom - _top;
        this.ncols = _right - _left;
        this.size = nrows*ncols;
    }

    public final void setView(int _from, int _to)
    {
        if (data.order() > 1)
            throw(new IllegalArgumentException("Cannot use order-1 array view constructor for a table"));
        
        this.top = _from;
        this.bottom = _to;
        this.left = 0;
        this.right = 1;
        
        this.nrows = _to - _from;
        this.ncols = 1;
        this.size = nrows;
    }
    
    
    @Override
    public int size()
    {
        return(size);
    }

    @Override
    public int order()
    {
        int order = 0;
        if (nrows>1)
            order++;
        if (ncols>1)
            order++;
        return(order);
    }

    @Override
    public int rows()
    {
        return(nrows);
    }

    @Override
    public int columns()
    {
        return(ncols);
    }

    @Override
    public int get(int i)
    {
        return(data.get(i/ncols,i%ncols));
    }

    @Override
    public int get(int i, int j)
    {
        return(data.get(i-top,j-left));
    }

    @Override
    public void set(int i, int x)
    {
        data.set(i/ncols,i%ncols,x);
    }

    @Override
    public void set(int i, int j, int x)
    {
        data.set(i-top,j-left,x);
    }

    @Override
    public int[] getArray()
    {
        int[] res = new int[size];
        
        for (int i=0; i<size; i++)
            res[i] = get(i);

        return res;
    }

    @Override
    public int[][] getTable()
    {
        int[][] res = new int[nrows][ncols];
        for (int i=0; i<nrows; i++)
            for (int j=0; j<ncols; j++)
                res[i][j] = get(i,j);

        return res;
    }

    @Override
    public int[] getRow(int row)
    {
        int[] res = new int[ncols];
        
        for (int i=0; i<ncols; i++)
            res[i] = get(row,i);

        return res;                
    }

    @Override
    public IIntArray viewRow(int i)
    {
        return(new IntArrayView(this, i, 0, i+1, ncols));
    }

    @Override
    public int[] getColumn(int col)
    {
        int[] res = new int[nrows];
        
        for (int i=0; i<nrows; i++)
            res[i] = get(i, col);

        return res;                
    }

    @Override
    public IIntArray viewColumn(int j)
    {
        return(new IntArrayView(this, 0, j, nrows, j+1));
    }

    @Override
    public IIntIterator iterator()
    {
        return(new IntTableIterator(this));
    }

    @Override
    public IIntIterator nonzeroIterator()
    {
        return(new IntTableNonzeroIterator(this));
    }

    @Override
    public IIntArray copy()
    {
        return(new PrimitiveIntTable(getTable()));
    }

    @Override
    public void copyFrom(IIntArray other)
    {
        if (size!= other.size())
            throw(new IllegalArgumentException("Incosistent sizes: This array has size "+size+" the other array has size "+other.size()));

        for (int i=0; i<size; i++)
            set(i,other.get(i));
    }

    @Override
    public void copyInto(IIntArray other)
    {
        if (size!= other.size())
            throw(new IllegalArgumentException("Incosistent sizes: This array has size "+size+" the other array has size "+other.size()));

        for (int i=0; i<size; i++)
            other.set(i,get(i));
    }

    @Override
    public IIntArray create(int size)
    {
        return(data.create(size));
    }

    @Override
    public IIntArray create(int rows, int columns)
    {
        return (data.create(rows,columns));
    }

    @Override
    public boolean isSparse()
    {
        return data.isSparse();
    }
    
}
