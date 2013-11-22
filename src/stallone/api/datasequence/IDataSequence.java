/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.util.Iterator;
import stallone.api.doubles.IDoubleArray;

/**
 * A fixed-size and ordered sequence of IDoubleArray objects that can be read and iterated
 *
 * @author noe
 */
public interface IDataSequence extends Iterable<IDoubleArray>
{
    public int size();

    public int dimension();

    /**
     * Returns the time of element i. If no time is defined for this sequence, the index i is returned.
     * @param i
     * @return
     */
    public double getTime(int i);

    public IDoubleArray get(int i);

    /**
     * Gets a view to the i'th data.
     * @param i
     * @return
     */
    public IDoubleArray getView(int i);

    @Override
    public Iterator<IDoubleArray> iterator();

    /**
     * Returns an iterator over pairs of data objects spaced at the given lagtime.
     * @param lag
     * @return An iterator whose objects are pairs (2-sequences) of data objects of type T
     */
    //public Iterator<IDataSequence> pairIterator(int spacing);

}
