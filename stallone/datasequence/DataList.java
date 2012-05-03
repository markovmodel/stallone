/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.ArrayList;
import java.util.Iterator;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataList
    extends ArrayList<IDoubleArray>
    implements IDataList
{
    public DataList()
    {
        super();
    }

    public DataList(int size)
    {
        super(size);
    }
    
    @Override
    public int dimension()
    {
        return(get(0).size());
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
}
