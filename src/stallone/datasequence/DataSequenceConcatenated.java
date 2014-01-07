/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.Iterator;
import java.util.List;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequenceConcatenated implements IDataSequence
{
    protected List<IDataSequence> seqs;
    protected int totalsize;
    protected int dimension = -1;

    protected int[] microindex2trajindex;
    protected int[] microindex2localindex;

    protected DataSequenceConcatenated()
    {
    }
    
    public DataSequenceConcatenated(List<IDataSequence> _seqs)
    {
        this.seqs = _seqs;

        for (int i=0; i<_seqs.size(); i++)
        {
            totalsize += _seqs.get(i).size();

            // dimension
            if (dimension == -1)
                dimension = _seqs.get(i).dimension();
            else
                if (dimension != _seqs.get(i).dimension())
                    throw new IllegalArgumentException("Data Sequence List has inconsistent dimensionality");
        }

        microindex2trajindex = new int[totalsize];
        microindex2localindex = new int[totalsize];
        int k=0;
        for (int i=0; i<_seqs.size(); i++)
        {
            for (int j=0; j<_seqs.get(i).size(); j++)
            {
                microindex2trajindex[k] = i;
                microindex2localindex[k] = j;
                k++;
            }
        }
    }

    @Override
    public int size()
    {
        return totalsize;
    }

    @Override
    public int dimension()
    {
        return dimension;
    }

    @Override
    public double getTime(int i)
    {
        return seqs.get(microindex2trajindex[i]).getTime(microindex2localindex[i]);
    }

    @Override
    public IDoubleArray get(int i)
    {
        return seqs.get(microindex2trajindex[i]).get(microindex2localindex[i]);
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return seqs.get(microindex2trajindex[i]).getView(microindex2localindex[i]);
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
