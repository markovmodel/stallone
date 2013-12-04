/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.DataSequenceIterator;

/**
 *
 * @author noe
 */
public class DatasetTransform 
{
    
/*
    @Override
    public int numberOfSequences()
    {
        return loadedSequences;
    }

    @Override
    public int size()
    {
        return loadedSize;
    }

    @Override
    public int size(int trajIndex)
    {
        return loadedSizes.get(trajIndex);
    }

    @Override
    public IDoubleArray load(int sequenceIndex, int frameIndex) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDataSequence loadSequence(int sequenceIndex) throws IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
class TransformedDataSequence implements IDataSequence
{
    IDataSequence X;
    ICoordinateTransform T;
    
    public TransformedDataSequence(IDataSequence source, ICoordinateTransform transform)
    {
        this.X = source;
        this.T = transform;
    }

    @Override
    public int size()
    {
        return X.size();
    }

    @Override
    public int dimension()
    {
        return T.dimension();
    }

    @Override
    public double getTime(int i)
    {
        return X.getTime(i);
    }

    @Override
    public IDoubleArray get(int i)
    {
        return T.transform(X.get(i));
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return T.transform(X.getView(i));
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DataSequenceIterator(this);
    }*/
}