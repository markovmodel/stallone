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
public class DataSequenceResizable extends DataArray
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
}
