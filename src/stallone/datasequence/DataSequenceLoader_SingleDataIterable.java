/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.Iterator;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DataSequenceLoader_SingleDataIterable implements Iterable<IDoubleArray>
{
    private IDataSequenceLoader loader;
    private int stepsize = 1;

    public DataSequenceLoader_SingleDataIterable(IDataSequenceLoader _loader)
    {
        this.loader = _loader;
        this.stepsize = 1;
    }

    public DataSequenceLoader_SingleDataIterable(IDataSequenceLoader _loader, int _stepsize)
    {
        this.loader = _loader;
        this.stepsize = _stepsize;
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return (new DataSequenceLoader_SingleDataIterator(loader,stepsize));
    }
}


