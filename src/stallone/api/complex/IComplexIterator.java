/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.complex;

import java.util.Iterator;

/**
 *
 * @author noe
 */
public interface IComplexIterator extends Iterator<IComplexElement>
{
    public void reset();

    public void advance();

    public int getIndex();

    public int row();

    public int column();

    public double get();

    public double getRe();

    public double getIm();

    public void set(double x);

    public void set(double re, double im);

    public void setRe(double x);

    public void setIm(double x);

    @Override
    public boolean hasNext();

    @Override
    public IComplexElement next();
}
