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
    /** size of the referenced vector. */
    protected int size;
    /** state, if getIterable is finished e.g. reached its last position */
    protected boolean finished;
    /**
     * Scalar variable used to store value at next position Is modified by {@link AbstractMatrixIterator#readNext() }.
     */
    protected double nextValue;
    protected DoubleArrayElement element;


    private int currentNonZeroPosition;
    private int nextNonZeroPosition;

    private IDoubleArray vector;
    private SparseVectorIndexMap sparseIndexMap;
    
    public SparseVectorNonzeroIterator(IDoubleArray _vector, SparseVectorIndexMap _map)
    {
        //super(_vector);
        this.vector = _vector;
        sparseIndexMap = _map;
        element = new DoubleArrayElement(_vector);
        /*
        super(SparseRealVector.this);

        if (sparseIndexMap.usedNonZero == 0)
        {
            finished = true;
        }
        else
        {
            currentNonZeroPosition = -1; // not valid
            nextNonZeroPosition = 0;
            nextPos = sparseIndexMap.nonZeroIndices[0];
        }*/
    }

    /*@Override
    protected final void read()
    {
        scalar.setComplex(data[currentNonZeroPosition], 0.0d);
        // standard is:
        // referencedVector.getScalar(currentPos, scalar);
    }

    @Override
    protected final void write()
    {
        data[currentNonZeroPosition] = scalar.getRe();
        // standard is:
        // referencedVector.setScalar(currentPos, scalar);
    }*/

    @Override
    public final void advance()
    {
        currentNonZeroPosition = nextNonZeroPosition;
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
            currentNonZeroPosition = -1; // not valid
            nextNonZeroPosition = 0;
            nextPos = sparseIndexMap.nonZeroIndices[0];
        }
    }
    
    @Override
    public boolean hasNext()
    {
        return !finished;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not allowed.");
    }

    @Override
//    public IScalarOfVector next()
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

    /**
     * Read next scalar at position (nextPos) to variable nextScalar.
     */
    protected void readNext()
    {
        nextValue = vector.get(nextPos);
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
