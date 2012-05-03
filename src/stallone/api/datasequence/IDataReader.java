/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import java.util.Iterator;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IDataReader extends IDataSequence
{
    public void setSource(String name);
    
    /**
     * Opens or re-opens the file.
     */
    public void open()
            throws IOException;
        
    /**
     * Checks the entire file for size, dimension, consistency and health
     */
    public void scan()
            throws IOException;
    
    /**
     * Gets the number of data objects in the sequence
     * @return 
     */
    public int size();
    
    /**
     * Gets the dimension of the data objects in the sequence
     * @return 
     */
    public int dimension();
    
    /**
     * Returns an estimate of the memory requirements of the full data sequence when loaded
     * @return 
     */
    public long memorySize();
    
    /**
     * Gets single data object
     * @param index
     * @return 
     */
    public IDoubleArray get(int index);
    
    /**
     * Loads the data sequence completely into memory. 
     * Attention - this may crash due to insufficient memory, so be sure to test beforehand.
     * @return 
     */
    public IDataSequence load();
    
    public Iterator<IDoubleArray> iterator();
    
    /**
     * Closes the file, but keeps file information as long as this object is alive
     */
    public void close()
            throws IOException;
}
