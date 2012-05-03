/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.complex;

import stallone.api.complex.IComplexArray;
import stallone.api.complex.IComplexElement;

/**
 *
 * @author noe
 */
public class ComplexArrayElement implements IComplexElement
{
    public IComplexArray table;
    public int row, col;
    
    public ComplexArrayElement(IComplexArray _table, int _row, int _col)
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

    @Override
    public double re()
    {
        return table.getRe(row,col);
    }

    @Override
    public double im()
    {
        return table.getIm(row,col);
    }

    @Override
    public void setRe(double x)
    {
        table.setRe(row, col, x);
    }

    @Override
    public void setIm(double x)
    {
        table.setIm(row, col, x);
    }
}
