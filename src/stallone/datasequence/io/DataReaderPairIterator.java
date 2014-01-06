/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence.io;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataReader;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataReaderPairIterator implements Iterator<IDoubleArray[]>
{

    private IDataReader reader = null;
    //private IDoubleArray preconstructedVector = null;
    private IDoubleArray[] pair = new IDoubleArray[2];
    private int spacing = 1;
    private int i1=0, i2=1;

    public DataReaderPairIterator(IDataReader _reader, int _spacing)
    {
        this.reader = _reader;
        openThisReader(reader);

        this.spacing = _spacing;
        i1 = 0;
        i2 = _spacing;
    }

    private void openThisReader(IDataReader _reader)
    {
        try
        {
            _reader.open();
        } catch (IOException e)
        {
            throw (new RuntimeException("IOException during Trajectory iteration: " + e));
        }
    }

    private void closeThisReader(IDataReader _reader)
    {
        try
        {
            _reader.close();
        } catch (IOException e)
        {
            throw (new RuntimeException("IOException during Trajectory iteration: " + e));
        }
    }

    @Override
    public boolean hasNext()
    {
        return (i2 < reader.size());
    }

    @Override
    public IDoubleArray[] next()
    {
        pair[0] = reader.get(i1);
        pair[1] = reader.get(i2);
        i1++;
        i2++;
        return(pair);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }

    @Override
    protected void finalize() throws Throwable
    {
        closeThisReader(reader);
        super.finalize();
    }
}
