/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataInput;

/**
 *
 * @author noe
 */
public class DataSequenceLoader_SingleSequenceIterable implements Iterable<IDataSequence>
{

    private IDataInput loader;

    DataSequenceLoader_SingleSequenceIterable(IDataInput _loader)
    {
        this.loader = _loader;
    }

    @Override
    public Iterator<IDataSequence> iterator()
    {
        return new DataSequenceLoader_SingleSequenceIterator(loader);
    }
}

class DataSequenceLoader_SingleSequenceIterator implements Iterator<IDataSequence>
{

    private IDataInput loader;
    private int itraj = 0;

    public DataSequenceLoader_SingleSequenceIterator(IDataInput _loader)
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
        try
        {
            IDataSequence res = loader.loadSequence(itraj);
            itraj++;

            return (res);

        } catch (IOException e)
        {
            throw (new RuntimeException(e));
        }
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
