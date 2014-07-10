/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequencePairIterable implements Iterable<IDoubleArray[]>
{
    private IDataSequence seq;
    private int spacing = 1;

    public DataSequencePairIterable(IDataSequence _seq, int _spacing)
    {
        this.seq = _seq;
        this.spacing = _spacing;
    }

    @Override
    public Iterator<IDoubleArray[]> iterator()
    {
        return (new DataSequencePairIterator(seq, spacing));
    }
}
