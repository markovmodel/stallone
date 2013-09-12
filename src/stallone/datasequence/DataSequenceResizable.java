/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import stallone.api.doubles.IDoubleArray;
import java.util.Iterator;


/**
 *
 * @author noe
 */
public class DataSequenceResizable extends DataSequenceArray
{
    private int currentSize;
    
    public DataSequenceResizable(int maxSize, int dimension)
    {
        super(new double[maxSize][dimension]);
    }
    
    public void set(int i, IDoubleArray x)
    {
        data[i] = x;
    }
    
    @Override
    public int size()
    {
        return(currentSize);
    }
    
    public void setSize(int s)
    {
        currentSize = s;
    }
    
    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(new DataSetInputResizableIterator(data,currentSize));
    }
    
}
class DataSetInputResizableIterator implements Iterator<IDoubleArray>
{
    Object[] data;
    int size;
    int i;
    
    public DataSetInputResizableIterator(Object[] _data, int _size)
    {
        this.data = _data;
        this.size = _size;
    }
    
    @Override
    public boolean hasNext()
    {
        return(i < size);
    }

    @Override
    public IDoubleArray next()
    {
        IDoubleArray res = (IDoubleArray)data[i];
        i++;
        return(res);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}