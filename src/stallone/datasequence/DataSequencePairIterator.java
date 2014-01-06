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
 * Default implementation for a IDataSequence iterator
 *
 * @author noe
 */
public class DataSequenceIterator implements Iterator<IDoubleArray>
{
    private IDataSequence seq ;
    int i=0;

   public DataSequenceIterator(IDataSequence _seq)
   {
       seq = _seq;
   }

    @Override
    public boolean hasNext()
    {
        return i < seq.size();
    }

    @Override
    public IDoubleArray next()
    {
        IDoubleArray res = seq.get(i);
        i++;
        return res;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported.");
    }


}
