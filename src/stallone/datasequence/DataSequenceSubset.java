/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class DataSequenceSubset implements IDataSequence
{
    private IDataSequence fullSequence;
    private IIntArray selection;

    public DataSequenceSubset(IDataSequence _fullSequence, IIntArray _selection)
    {
        this.fullSequence = _fullSequence;
        this.selection = _selection;
    }

    @Override
    public int size()
    {
        return selection.size();
    }

    @Override
    public int dimension()
    {
        return fullSequence.dimension();
    }

    @Override
    public double getTime(int i)
    {
        int sel = selection.get(i);
        return fullSequence.getTime(sel);
    }

    @Override
    public IDoubleArray get(int i)
    {
        int sel = selection.get(i);
        return fullSequence.get(sel);
    }

    @Override
    public IDoubleArray getView(int i)
    {
        int sel = selection.get(i);
        return fullSequence.getView(sel);
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DataSequenceIterator(this);
    }

    @Override
    public Iterator<IDoubleArray[]> pairIterator(int spacing)
    {
        return new DataSequencePairIterator(this, spacing);
    }

    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        class PairIterable implements Iterable<IDoubleArray[]>
        {
            private IDataSequence seq;
            private int spacing = 1;

            public PairIterable(IDataSequence _seq, int _spacing)
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
        return new PairIterable(this,spacing);
    }

}
