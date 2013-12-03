/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;
import stallone.api.doubles.IDoubleIterator;

/**
 *
 * @author noe
 */
public class SparseVectorNonzeroIterator implements IDoubleIterator
{
    /** next position in vector the getIterable will be located at, when calling {@link #next() }. */
    protected int nextPos;
    
    /** state, if getIterable is finished e.g. reached its last position */
    protected boolean finished;

    protected DoubleArrayElement element;

    private int nextNonZeroPosition;

    private SparseVectorIndexMap sparseIndexMap;

    public SparseVectorNonzeroIterator(IDoubleArray _vector, SparseVectorIndexMap _map)
    {
        sparseIndexMap = _map;
        element = new DoubleArrayElement(_vector);
        
        // if there are no non zero elements we are finished immediately
        if (sparseIndexMap.usedNonZero == 0)
        {
            finished = true;
        }
        else
        {
            // start negative here, because advance() increments directly
            nextNonZeroPosition = -1;
            // set nextPos to first non zero index
            nextPos = sparseIndexMap.nonZeroIndices[0];
        }
    }

    @Override
    /**
     * let indices point to next non zero element.
     */
    public final void advance()
    {
        nextNonZeroPosition++;
        
        if (nextNonZeroPosition < sparseIndexMap.usedNonZero)
        {
            nextPos = sparseIndexMap.nonZeroIndices[nextNonZeroPosition];
        }
        else
        {
            finished = true;
        }
    }

    @Override
    public void reset()
    {
        // start position
        if (sparseIndexMap.usedNonZero == 0)
        {
            finished = true;
        }
        else
        {
            nextNonZeroPosition = 0;
            nextPos = sparseIndexMap.nonZeroIndices[0];
        }
    }

    @Override
    public boolean hasNext()
    {
    	// TODO: this used to be cached in finished variable, which one should be properly updated.
        return nextNonZeroPosition +1 < sparseIndexMap.usedNonZero;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not allowed.");
    }

    /**
     * note that next() calls advance(), so make sure not to use both.
     */
    @Override
    public IDoubleElement next()
    {

        // makes "next" the "current" and finds next "next"
        if (!finished)
        {
            // find next
            advance();

            element.setIndex(nextPos);

            // return current
            return element;
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException("Calling next(), but not value available.");
        }
    }

    @Override
    public int getIndex()
    {
        return(nextPos);
    }

    @Override
    public int row()
    {
        return(element.row());
    }

    @Override
    public int column()
    {
        return(element.column());
    }

    @Override
    public double get()
    {
        return(element.get());
    }

    @Override
    public void set(double x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
