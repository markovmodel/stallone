/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.complex;

import stallone.api.complex.*;

/**
 *
 * @author noe
 */
public class ComplexArrayIterator implements IComplexIterator
{
    protected ComplexArrayElement o;
    protected IComplexArray X;
    protected int i=0,j=0;
    protected int rows,cols;
    
   
    public ComplexArrayIterator(IComplexArray _X)
    {
        this.X = _X;
        this.rows = X.rows();
        this.cols = X.columns();
        o = new ComplexArrayElement(X,0,0);
    }
        
    @Override
    public void reset()
    {
        i = 0;
        j = 0;
    }

    
    @Override
    public boolean hasNext()
    {
        return (i<rows && j < cols);
    }
    

    @Override
    public int row()
    {
        return(i);
    }

    @Override
    public int column()
    {
        return(j);
    }

    @Override
    public void advance()
    {
        if (j == cols-1)
        {
            i++;
            j=0;
        }
        else
        {
            j++;
        }
    }

    @Override
    public double get()
    {
        return(X.get(i,j));
    }
    
    @Override
    public void set(double newValue)
    {
        X.set(i,j, newValue);
    }

    @Override
    public IComplexElement next()
    {
        o.setIndex(i,j);
        advance();
        return o;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    public int getIndex()
    {
        return(i*cols+j);
    }

    @Override
    public double getRe()
    {
        return X.getRe(i,j);
    }

    @Override
    public double getIm()
    {
        return X.getIm(i,j);
    }

    @Override
    public void set(double re, double im)
    {
        X.set(i,j,re,im);
    }

    @Override
    public void setRe(double x)
    {
        X.setRe(i,j,x);
    }

    @Override
    public void setIm(double x)
    {
        X.setIm(i,j,x);
    }
}
