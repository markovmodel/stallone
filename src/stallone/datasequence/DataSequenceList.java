/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.ArrayList;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequenceList extends ArrayList<IDataSequence> implements IDataInput
{
    @Override
    public int dimension()
    {
        return(get(0).dimension());
    }

    @Override
    public int numberOfSequences()
    {
        return size();
    }

    @Override
    public int size(int trajIndex)
    {
        return get(0).size();
    }

    @Override
    public Iterable<IDoubleArray> singles()
    {
        return (new DataInput_SingleDataIterable(this));
    }

    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        return (new DataInput_DataPairIterable(this, spacing));
    }

    @Override
    public Iterable<IDataSequence> sequences()
    {
        return new DataInput_SingleSequenceIterable(this);
    }

    @Override
    public IDoubleArray get(int sequenceIndex, int frameIndex)
    {
        return get(sequenceIndex).get(frameIndex);
    }

    @Override
    public IDataSequence getSequence(int sequenceIndex)
    {
        return get(sequenceIndex);
    }
}
