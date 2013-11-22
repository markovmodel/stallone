/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;

import java.util.Iterator;

/**
 *
 * @author noe
 */
public interface IIntIterator extends Iterator<IIntElement>
{
    public void reset();

    public void advance();


    public int getIndex();

    public int row();

    public int column();

    public int get();

    public void set(int x);

    @Override
    public boolean hasNext();

    @Override
    public IIntElement next();
}
