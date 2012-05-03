/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;

/**
 *
 * @author noe
 */
public class DoubleTableElement implements IDoubleElement
{
    public IDoubleArray table;
    public int row, col;
    
    public DoubleTableElement(IDoubleArray _table, int _row, int _col)
    {
        this.table = _table;
        this.row = _row;
        this.col = _col;
    }
    
    public void setIndex(int _row, int _col)
    {
        this.row = _row;
        this.col = _col;
    }
    
    @Override
    public int row()
    {
        return(row);
    }

    @Override
    public int column()
    {
        return(col);
    }
    
    @Override
    public double get()
    {
        return(table.get(row,col));
    }

    @Override
    public int index()
    {
        return(row*table.columns()+col);
    }

    @Override
    public void set(double value)
    {
        table.set(row, col, value);
    }
}
