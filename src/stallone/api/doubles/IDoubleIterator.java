/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import java.util.Iterator;

/**
 *
 * @author noe
 */
public interface IDoubleIterator extends Iterator<IDoubleElement>
{
    public void reset();

    public void advance();


    public int getIndex();

    public int row();

    public int column();

    public double get();

    public void set(double x);

    @Override
    public boolean hasNext();

    @Override
    public IDoubleElement next();
}
