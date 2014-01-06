/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import stallone.api.doubles.IDoubleArray;
import stallone.api.datasequence.IDataSequence;
import java.util.Iterator;
import stallone.doubles.PrimitiveDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequenceArray implements IDataSequence
{
    protected double[] times = null;
    protected IDoubleArray[] data = null;
    //double[][] data;

    /**
     * Untimed data sequence
     * @param _data
     */
    public DataSequenceArray(IDoubleArray[] _data)
    {
        this.data = _data;
    }

    /**
     * Timed data sequence
     * @param _times
     * @param _data
     */
    public DataSequenceArray(double[] _times, IDoubleArray[] _data)
    {
        this.times = _times;
        this.data = _data;
    }

    /**
     * Untimed data array
     * @param _data
     */
    public DataSequenceArray(double[][] _data)
    {
        this.data = new IDoubleArray[_data.length];
        for (int i=0; i<this.data.length; i++)
            this.data[i] = new PrimitiveDoubleArray(_data[i]);
    }

    /**
     * Timed data array
     * @param _data
     */
    public DataSequenceArray(double[] _times, double[][] _data)
    {
        this(_data);
        this.times = _times;
    }

    public DataSequenceArray(IDoubleArray _data)
    {
        this(_data.getTable());
    }

    public DataSequenceArray(IDoubleArray _times, IDoubleArray _data)
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
        return(new ElementIterator(data));
    }

    //@Override
    public Iterator<IDataSequence> pairIterator(int spacing)
    {
        return(new PairIterator(data,spacing));
    }

    @Override
    public double getTime(int i)
    {
        if (times == null)
            return i;
        else
            return times[i];
    }

    private class ElementIterator implements Iterator<IDoubleArray>
    {
        private IDoubleArray[] data;
        private int i=0;

        public ElementIterator(IDoubleArray[] _data)
        {
            this.data = _data;
        }

        @Override
        public boolean hasNext()
        {
            return(i<data.length);
        }

        @Override
        public IDoubleArray next()
        {
            IDoubleArray res = data[i];
            i++;
            return res;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private class PairIterator implements Iterator<IDataSequence>
    {
        private IDoubleArray[] data;
        private int i=0, spacing;

        private IDoubleArray[] d12 = new IDoubleArray[2];
        private DataSequenceArray res = new DataSequenceArray(d12);


        public PairIterator(IDoubleArray[] _data, int _spacing)
        {
            this.data = _data;
            this.spacing = _spacing;
        }

        @Override
        public boolean hasNext()
        {
            return(i+spacing<data.length);
        }

        @Override
        public IDataSequence next()
        {
            d12[0] = data[i];
            d12[1] = data[i+spacing];
            i++;
            return res;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

}
