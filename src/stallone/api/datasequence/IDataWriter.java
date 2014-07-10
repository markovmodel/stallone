/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * Implementations of this interface can write numerical data 
 * (sequences of IDoubleArrays) into a specific format.
 * This writer is reusable. It can be opened (and closed) arbitrarily often
 * on different files. Objects implementing this class are therefore similar
 * like factories - you can pass instantiations using an empty constructor
 * to another class that will direct the writer to different files.
 * 
 * @author noe
 */
public interface IDataWriter
{
    /**
     * Opens a new file at the target path for writing.
     * @param path Full file path
     * @param _nFrames Number of frames
     * @param _nDimensions Number of dimensions
     */
    public void open(String path, int nFrames, int nDimensions)
            throws IOException;

    /**
     * Adds a data unit
     * @param data 
     */
    public void add(IDoubleArray data);

    /**
     * Adds all data units from the input.
     * @param data 
     */
    public void addAll(Iterable<IDoubleArray> data);

    /**
     * Closes the file, but leaves the writer alive for subsequent actions.
     * You can open the writer again on a different file.
     * @throws IOException 
     */
    public void close()
            throws IOException;
}
