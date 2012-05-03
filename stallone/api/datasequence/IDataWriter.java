/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IDataWriter
{
    public void add(IDoubleArray data);

    public void addAll(Iterable<IDoubleArray> data);    
    
    public void close()
            throws IOException;
}
