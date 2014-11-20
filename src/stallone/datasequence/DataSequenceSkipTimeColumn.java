package stallone.datasequence;

import java.util.Iterator;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

public class DataSequenceSkipTimeColumn implements IDataSequence
{
    private IDataSequence seq;
    private int[] selectedRows;
    private int[] selectedColumns;

    /**
     * just skip the first index
     * 
     * @param in
     * @return
     */
    private IDoubleArray getDataView(IDoubleArray in)
    {
        IDoubleArray view = in.view(selectedRows, selectedColumns);
        return view;
    }

    public DataSequenceSkipTimeColumn(IDataSequence seq)
    {
        this.seq = seq;

        int dim = seq.dimension();
        int[] selCols = new int[dim - 1];
        // we select the whole row.
        this.selectedRows = new int[] { 0 };

        // select all rows despite the first one.
        for (int i = 0; i < selCols.length; i++)
        {
            selCols[i] = i + 1;
        }

        this.selectedColumns = selCols;
    }

    @Override
    public int size()
    {
        return seq.size();
    }

    @Override
    public int dimension()
    {
        return seq.dimension() - 1;
    }

    @Override
    public double getTime(int i)
    {
        return seq.getTime(i);
    }

    @Override
    public IDoubleArray get(int i)
    {
        return getDataView(seq.get(i));
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return getDataView(seq.getView(i));
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
        return new PairIterable(this, spacing);
    }

}
