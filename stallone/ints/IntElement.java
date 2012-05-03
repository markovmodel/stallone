/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.IIntElement;

/**
 *
 * @author noe
 */
public class IntElement implements IIntElement
{
    int row,col,index,x;
    
    public IntElement(int _row, int _col, int _index, int _x)
    {
        this.row = _row;
        this.col = _col;
        this.index = _index;
        this.x = _x;
    }
    
    public void setIndex(int _index)
    {
        this.index = _index;
    }
    
    public void setIndex(int _row, int _col)
    {
        this.row = _row;
        this.col = _col;
    }
    
    public void setValue(int _x)
    {
        this.x = _x;
    }
    
    @Override
    public int index()
    {
        return index;
    }

    @Override
    public int row()
    {
        return(row);
    }

    @Override
    public int column()
    {
        return col;
    }

    @Override
    public int get()
    {
        return x;
    }

    @Override
    public void set(int x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
