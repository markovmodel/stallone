/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dataprocessing;

import stallone.api.coordinates.ICoordinateTransform;
import stallone.api.coordinates.IParametrizedCoordinateTransform;
import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class Transformer implements IDataInput
{
    private ICoordinateTransform transform;
    private IDataInput input;

    
    public Transformer(ICoordinateTransform _transform)
    {
        this.transform = _transform;
    }

    public Transformer(IDataInput _input, ICoordinateTransform _transform)
    {
        addSender(_input);
        this.transform = _transform;
    }
    
    //==========================================================================
    //
    // Data processing methods
    //
    //==========================================================================
    

    @Override
    public boolean hasInput()
    {
        return (this.input != null);
    }

    /**
     * Sets the receiver when called once. 
     * @throws RuntimeException when called twice because PCA can only have one input.
     * @param receiver
     */
    @Override
    public final void addSender(IDataProcessor sender)
    {
        if (this.input != null)
            throw new RuntimeException("Trying to add a second sencer to PCA. This is not possible.");
        
        if (sender instanceof IDataInput)
            this.input = (IDataInput)sender;
        else
            throw new IllegalArgumentException("Illegal input type: sender must be an instance of IDataInput");
    }


    /**
     * Does nothing
     * @param sender 
     */
    @Override
    public void addReceiver(IDataProcessor receiver)
    {
    }


    @Override
    public void init()
    {
        if (transform instanceof IParametrizedCoordinateTransform)
            ((IParametrizedCoordinateTransform)transform).setupTransform(input);
    }

    /**
     * Nothing to do
     */
    @Override
    public void run()
    {
    }

    /**
     * Nothing to do
     */
    @Override
    public void cleanup()
    {
    }
    
    
    
    

    @Override
    public int numberOfSequences()
    {
        return input.numberOfSequences();
    }

    @Override
    public int dimension()
    {
        return transform.dimension();
    }

    @Override
    public int size()
    {
        return input.size();
    }

    @Override
    public int size(int trajIndex)
    {
        return input.size(trajIndex);
    }

    @Override
    public String name(int trajIndex)
    {
        return input.name(trajIndex);
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
        return (new DataInput_SingleSequenceIterable(this));
    }

    @Override
    public IDoubleArray get(int sequenceIndex, int frameIndex)
    {
        return transform.transform(input.get(sequenceIndex, frameIndex));
    }

    @Override
    public IDataSequence getSequence(int sequenceIndex)
    {
        return new TransformedSequence(input.getSequence(sequenceIndex), transform);
    }

    
}
