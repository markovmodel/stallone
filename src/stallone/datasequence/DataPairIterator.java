/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import java.util.Iterator;

/**
 *
 * @author noe
 */
public class DataPairIterator
        implements Iterator<IDataSequence>
{

    private IDataSequence data;
    private int i = 0, spacing;
    private IDataList res = new DataList(2);

    public DataPairIterator(IDataSequence _data, int _spacing)
    {
        this.data = _data;
        this.spacing = _spacing;
    }

    @Override
    public boolean hasNext()
    {
        return (i + spacing < data.size());
    }

    @Override
    public IDataSequence next()
    {
        res.set(0, data.get(i));
        res.set(0, data.get(i + spacing));
        i++;
        i++;
        return res;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
