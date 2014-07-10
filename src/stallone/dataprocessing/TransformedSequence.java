/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dataprocessing;

import java.util.Iterator;
import stallone.api.coordinates.ICoordinateTransform;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.DataSequenceIterator;
import stallone.datasequence.DataSequencePairIterable;
import stallone.datasequence.DataSequencePairIterator;

/**
 *
 * @author noe
 */
public class TransformedSequence implements IDataSequence
{
    private IDataSequence input;
    private ICoordinateTransform transform;
    public TransformedSequence(IDataSequence _input, ICoordinateTransform _transform)
    {
        this.input = _input;
        this.transform = _transform;
    }

    @Override
    public int size()
    {
        return input.size();
    }

    @Override
    public int dimension()
    {
        return transform.dimension();
    }

    @Override
    public double getTime(int i)
    {
        return input.getTime(i);
    }

    @Override
    public IDoubleArray get(int i)
    {
        return transform.transform(input.get(i));
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return transform.transform(input.getView(i));
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
        return new DataSequencePairIterable(this,spacing);
    }
    
}
