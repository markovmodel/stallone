/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.doubles.AbstractDoubleArray;

/**
 *
 * @author noe
 */
public class DiscreteTrajectoryDataSequence implements IDataSequence
{
    private IIntArray dtraj;
    private int nstates;
    private DeltaFunctionDataSet dataset;
            
    public DiscreteTrajectoryDataSequence(IIntArray _dtraj, int _nstates)
    {
        this.dtraj = _dtraj;
        this.nstates = _nstates;
        this.dataset = new DeltaFunctionDataSet(_nstates, 0);
    }

    @Override
    public int size()
    {
        return dtraj.size();
    }

    @Override
    public int dimension()
    {
        return nstates;
    }

    @Override
    public double getTime(int i)
    {
        return i;
    }

    @Override
    public IDoubleArray get(int i)
    {
        dataset.setOneState(dtraj.get(i));
        return dataset;
    }

    @Override
    public IDoubleArray getView(int i)
    {
        dataset.setOneState(dtraj.get(i));
        return dataset;
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DiscreteTrajectoryDataSequenceIterator();
    }

    @Override
    public Iterator<IDoubleArray[]> pairIterator(int spacing)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    class DiscreteTrajectoryDataSequenceIterator implements Iterator<IDoubleArray>
    {
        private int i=0;

        public DiscreteTrajectoryDataSequenceIterator()
        {
        }

        @Override
        public boolean hasNext()
        {
            return i < size();
        }

        @Override
        public IDoubleArray next()
        {
            IDoubleArray res = get(i);
            i++;
            return res;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }    
    
    class DeltaFunctionDataSet extends AbstractDoubleArray
    {
        int size;
        int oneState;

        public DeltaFunctionDataSet(int _size, int _oneState)
        {
            this.size = _size;
            this.oneState = _oneState;
        }

        public void setOneState(int _oneState)
        {
            this.oneState = _oneState;
        }

        @Override
        public int rows()
        {
            return size;
        }

        @Override
        public int columns()
        {
            return 1;
        }

        @Override
        public double get(int i, int j)
        {
            if (i < 0 || i >= size || j != 0)
                throw new IllegalArgumentException("illegal index: "+i+" "+j);
            if (i == oneState)
                return 1;
            return 0;
        }

        @Override
        public void set(int i, int j, double x)
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public IDoubleArray copy()
        {
            return new DeltaFunctionDataSet(size, oneState);
        }

        @Override
        public IDoubleArray create(int _size)
        {
            return new DeltaFunctionDataSet(_size, 0);
        }

        @Override
        public IDoubleArray create(int rows, int columns)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isSparse()
        {
            return true;
        }
    }    
}




