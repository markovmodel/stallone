/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.*;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataList
    implements IDataList
{
    private ArrayList<IDoubleArray> list = new ArrayList();
    
    public DataList()
    {
        list = new ArrayList();
    }

    public DataList(int size)
    {
        list = new ArrayList(size);
    }
    
    @Override
    public int dimension()
    {
        return(get(0).size());
    }
    
    @Override
    public IDoubleArray get(int i)
    {
        return(list.get(i));
    }
    
    @Override
    public IDoubleArray getView(int i)
    {
        return(get(i));
    }

    //@Override
    public Iterator<IDataSequence> pairIterator(int spacing)
    {
        return(new DataPairIterator(this, spacing));
    }

    @Override
    public double getTime(int i)
    {
        return i;
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return list.iterator();
    }
    
    @Override
    public boolean add(IDoubleArray x)
    {
        return list.add(x);
    }

    @Override
    public boolean remove(IDoubleArray x)
    {
        return list.remove(x);
    }
    
    @Override
    public IDoubleArray remove (int i)
    {
        return list.remove(i);
    }

    @Override
    public void set(int i, IDoubleArray x)
    {
        list.set(i,x);
    }

}
