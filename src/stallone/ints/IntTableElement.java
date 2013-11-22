/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntElement;

/**
 *
 * @author noe
 */
public class IntTableElement implements IIntElement
{
    public IIntArray table;
    public int row, col;

    public IntTableElement(IIntArray _table, int _row, int _col)
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
    public int get()
    {
        return(table.get(row,col));
    }

    @Override
    public int index()
    {
        return(row*table.columns()+col);
    }

    @Override
    public void set(int value)
    {
        table.set(row, col, value);
    }
}
