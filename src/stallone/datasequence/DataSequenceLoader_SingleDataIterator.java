/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataInput;
import stallone.api.doubles.IDoubleArray;


/**
 *
 * @author noe
 */

public class DataSequenceLoader_SingleDataIterator implements Iterator<IDoubleArray>
{

    protected IDataInput loader;
    protected int itraj = 0, iindex = 0;
    private int stepsize = 1;

    public DataSequenceLoader_SingleDataIterator(IDataInput _loader)
    {
        this.loader = _loader;
        this.stepsize = 1;
    }

    public DataSequenceLoader_SingleDataIterator(IDataInput _loader, int _stepsize)
    {
        this.loader = _loader;
        this.stepsize = _stepsize;
    }

    @Override
    public boolean hasNext()
    {
        return (itraj < loader.numberOfSequences() - 1 || (itraj == loader.numberOfSequences() - 1 && iindex < loader.size(itraj)));
    }

    @Override
    public IDoubleArray next()
    {
        IDoubleArray res = null;
        try
        {
            res = loader.load(itraj, iindex);
            advance();
        } catch (IOException e)
        {
            throw (new RuntimeException(e));
        }
        return (res);
    }

    protected void advance()
            throws IOException
    {
        iindex += stepsize;
        if (iindex >= loader.size(itraj))
        {
            itraj++;
            iindex = 0 + (loader.size(itraj)-iindex);
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
