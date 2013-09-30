/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.io.IOException;
import java.util.Iterator;
import stallone.api.datasequence.IDataReader;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequenceLoaderIterator implements Iterator<IDoubleArray>
{

    private IDataReader reader = null;
    //private IDoubleArray preconstructedVector = null;
    private int index = 0;

    public DataSequenceLoaderIterator(IDataReader _reader)
    {
        this.reader = _reader;
        openThisReader(reader);
        //this.preconstructedVector = Doubles.create.array(_reader._reader.dimension());
    }
    
    private void openThisReader(IDataReader _reader){
            try{
                    _reader.open();
                } 
                catch(IOException e){
                    throw (new RuntimeException("IOException during Trajectory iteration: " + e));
                }
        }
        
        private void closeThisReader(IDataReader _reader){
            try{
                    _reader.close();
                } 
                catch(IOException e){
                    throw (new RuntimeException("IOException during Trajectory iteration: " + e));
                }
        }

    @Override
    public boolean hasNext()
    {
        return (index < reader.size());
    }

    @Override
    public IDoubleArray next()
    {
        IDoubleArray res = reader.get(index);
        index++;
        return (res);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Remove not supported.");
    }
    
    @Override
    protected void finalize() throws Throwable{
        closeThisReader(reader);
        super.finalize();
    }
}
