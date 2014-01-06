/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import stallone.api.doubles.IDoubleArray;
import stallone.api.datasequence.IDataSequence;
import java.util.Iterator;
import stallone.api.datasequence.IDataInput;
import stallone.doubles.PrimitiveDoubleArray;

/**
 *
 * @author noe
 */
public class DataArray implements IDataSequence
{
    protected double[] times = null;
    protected IDoubleArray[] data = null;
    //double[][] data;

    /**
     * Untimed data sequence
     * @param _data
     */
    public DataArray(int size)
    {
        this.data = new IDoubleArray[size];
    }

    /**
     * Untimed data sequence
     * @param _data
     */
    public DataArray(IDoubleArray[] _data)
    {
        this.data = _data;
    }

    /**
     * Timed data sequence
     * @param _times
     * @param _data
     */
    public DataArray(double[] _times, IDoubleArray[] _data)
    {
        this.times = _times;
        this.data = _data;
    }

    /**
     * Untimed data array
     * @param _data
     */
    public DataArray(double[][] _data)
    {
        this.data = new IDoubleArray[_data.length];
        for (int i=0; i<this.data.length; i++)
            this.data[i] = new PrimitiveDoubleArray(_data[i]);
    }

    /**
     * Timed data array
     * @param _data
     */
    public DataArray(double[] _times, double[][] _data)
    {
        this(_data);
        this.times = _times;
    }

    public DataArray(IDoubleArray _data)
    {
        this(_data.getTable());
    }

    public DataArray(IDoubleArray _times, IDoubleArray _data)
    {
        this(_times.getArray(), _data.getTable());
    }

    @Override
    public int size()
    {
        return(data.length);
    }

    public int dimension()
    {
        return(data[0].size());
    }

    @Override
    public IDoubleArray get(int i)
    {
        return(data[i]);
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return(data[i]);
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(new DataSequenceIterator(this));
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
    
    @Override
    public double getTime(int i)
    {
        if (times == null)
            return i;
        else
            return times[i];
    }


}
