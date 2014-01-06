/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;



/**
 *
 * Collection of various iterables and iterators for the DataInput
 * 
 * @author noe
 */

class DataInput_SingleDataIterable implements Iterable<IDoubleArray>
{
    private IDataInput loader;
    private int stepsize = 1;

    public DataInput_SingleDataIterable(IDataInput _loader)
    {
        this.loader = _loader;
        this.stepsize = 1;
    }

    public DataInput_SingleDataIterable(IDataInput _loader, int _stepsize)
    {
        this.loader = _loader;
        this.stepsize = _stepsize;
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return (new DataInput_SingleDataIterator(loader,stepsize));
    }
}



class DataInput_SingleDataIterator implements Iterator<IDoubleArray>
{

    protected IDataInput loader;
    protected int itraj = 0, iindex = 0;
    private int stepsize = 1;

    public DataInput_SingleDataIterator(IDataInput _loader)
    {
        this.loader = _loader;
        this.stepsize = 1;
    }

    public DataInput_SingleDataIterator(IDataInput _loader, int _stepsize)
    {
        this.loader = _loader;
        this.stepsize = _stepsize;
    }

    @Override
    public boolean hasNext()
    {
        return (itraj < loader.numberOfSequences() - 1 || (itraj == loader.numberOfSequences() - 1 && iindex < loader.size(itraj) - 1));
    }

    @Override
    public IDoubleArray next()
    {
        IDoubleArray res = null;
        try
        {
            res = loader.get(itraj, iindex);
            advance();
        } catch (IOException e)
        {
            throw (new RuntimeException(e));
        }
        //System.out.println(" read: "+itraj+" "+iindex+"\t"+res.size());
        return (res);
    }

    protected void advance()
            throws IOException
    {
        iindex += stepsize;
        if (iindex >= loader.size(itraj))
        {
            itraj++;
            iindex = 0;
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}




class DataInput_DataPairIterable implements Iterable<IDoubleArray[]>
{
    private IDataInput loader;
    private int spacing = 1, stepsize = 1;

    public DataInput_DataPairIterable(IDataInput _loader, int _spacing)
    {
        this.loader = _loader;
        this.spacing = _spacing;
    }

    public DataInput_DataPairIterable(IDataInput _loader, int _spacing, int _stepsize)
    {
        this.loader = _loader;
        this.spacing = _spacing;
        this.stepsize = _stepsize;
    }
    
    @Override
    public Iterator<IDoubleArray[]> iterator()
    {
        return (new DataInput_DataPairIterator(loader,spacing,stepsize));
    }
}




class DataInput_DataPairIterator implements Iterator<IDoubleArray[]>
{
    protected IDataInput loader;
    protected int itraj = 0, iindex1 = 0, iindex2 = 1;
    private int spacing = 1, stepsize = 1;
    private IDoubleArray[] res = new IDoubleArray[2];

    public DataInput_DataPairIterator(IDataInput _loader, int _spacing)
    {
        this.loader = _loader;
        this.spacing = _spacing;
        iindex1 = 0;
        iindex2 = spacing;
    }

    public DataInput_DataPairIterator(IDataInput _loader, int _spacing, int _stepsize)
    {
        this(_loader, _spacing);
        stepsize = _stepsize;
    }
    
    @Override
    public boolean hasNext()
    {
        return (itraj < loader.numberOfSequences() - 1 
                || (itraj == loader.numberOfSequences() - 1 && iindex2 < loader.size(itraj) - 1));
    }

    @Override
    public IDoubleArray[] next()
    {
        try
        {
            res[0] = loader.get(itraj, iindex1);
            res[1] = loader.get(itraj, iindex2);
            advance();
        } 
        catch (IOException e)
        {
            throw (new RuntimeException(e));
        }
        //System.out.println(" read: "+itraj+" "+iindex+"\t"+res.size());
        return (res);
    }

    protected void advance()
            throws IOException
    {
        iindex1 += stepsize;
        iindex2 += stepsize;
        if (iindex2 >= loader.size(itraj))
        {
            itraj++;
            iindex1 = 0;
            iindex2 = spacing;
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}



class DataInput_SingleSequenceIterable implements Iterable<IDataSequence>
{

    private IDataInput loader;

    DataInput_SingleSequenceIterable(IDataInput _loader)
    {
        this.loader = _loader;
    }

    @Override
    public Iterator<IDataSequence> iterator()
    {
        return new DataInput_SingleSequenceIterator(loader);
    }
}

class DataInput_SingleSequenceIterator implements Iterator<IDataSequence>
{

    private IDataInput loader;
    private int itraj = 0;

    public DataInput_SingleSequenceIterator(IDataInput _loader)
    {
        this.loader = _loader;
    }

    @Override
    public boolean hasNext()
    {
        return (itraj < loader.numberOfSequences());
    }

    @Override
    public IDataSequence next()
    {
        IDataSequence res = loader.getSequence(itraj);
        itraj++;

        return (res);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
