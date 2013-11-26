/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import java.util.Iterator;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class DiscreteTrajectorySimpleDataSequence implements IDataSequence
{
    private IIntArray dtraj;
    private IDoubleArray singleEntry = doublesNew.array(1);
            
    public DiscreteTrajectorySimpleDataSequence(IIntArray _dtraj)
    {
        this.dtraj = _dtraj;
    }

    @Override
    public int size()
    {
        return dtraj.size();
    }

    @Override
    public int dimension()
    {
        return 1;
    }

    @Override
    public double getTime(int i)
    {
        return i;
    }

    @Override
    public IDoubleArray get(int i)
    {
        singleEntry.set(0, dtraj.get(i));
        return singleEntry;
    }

    @Override
    public IDoubleArray getView(int i)
    {
        singleEntry.set(0, dtraj.get(i));
        return singleEntry;
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DiscreteTrajectoryDataSequenceIterator();
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
}




