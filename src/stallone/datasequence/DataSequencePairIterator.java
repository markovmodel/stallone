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
public class DataSequencePairIterator implements Iterator<IDoubleArray[]>
{
    private IDataSequence seq;
    int spacing = 1;
    int i1=0, i2=1;
    IDoubleArray[] pair = new IDoubleArray[2];

   public DataSequencePairIterator(IDataSequence _seq, int _spacing)
   {
       seq = _seq;
       this.spacing = _spacing;
       i1 = 0;
       i2 = i1+_spacing;
   }

    @Override
    public boolean hasNext()
    {
        return i2 < seq.size();
    }

    @Override
    public IDoubleArray[] next()
    {
        pair[0] = seq.get(i1);
        pair[1] = seq.get(i2);
        i1++;
        i2++;
        return pair;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported.");
    }


}

