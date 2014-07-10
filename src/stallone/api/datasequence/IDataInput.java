/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import java.util.List;
import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * Interface for loaders that retrieve DataSequences (e.g. trajectories)
 * from file systems or data bases. Shields the usage of this data from the
 * file / database access in order to efficiently deal with slow loading times,
 * while at the same time keeping the memory and file handle usage low.
 *
 * @author noe
 */
public interface IDataInput extends IDataProcessor
{
    /**
     * Total number of sequences. Need to init() first before calling this method.
     * @return
     */
    public int numberOfSequences();

    /**
     * Dimension of the data objects.
     * @return
     */
    public int dimension();

    /**
     * Total number of data objects. Need to init() first before calling this method.
     * @return
     */
    public int size();

    /**
     * size of the sequence with the given index.
     * @param trajIndex trajectory index
     * @return
     */
    public int size(int trajIndex);

    /**
     * Returns the name of the trajectory with given index
     * @param trajIndex trajectory index
     * @return 
     */
    public String name(int trajIndex);

    /**
     * Returns an iterable that can iterate over single data objects. Does not require scan() to be called.
     * Only single data objects are loaded into memory and only one file is open at a time
     */
    public Iterable<IDoubleArray> singles();

    
    /**
     * Returns an iterable that can iterate over pairs of data objects with the given spacing. 
     * Does not require scan() to be called.
     * Only two single data objects are loaded into memory and only one file is open at a time
     */
    public Iterable<IDoubleArray[]> pairs(int spacing);
    

    /**
     * Returns an iterable that can iterate over single data sequences. Does not require scan() to be called.
     * Each data sequence is fully loaded into memory at a time. Only one file is open at a time
     * @return
     */
    public Iterable<IDataSequence> sequences();

    /**
     * Gets a single data set.
     * @param sequenceIndex
     * @param frameIndex
     * @return
     * @throws IOException
     */
    public IDoubleArray get(int sequenceIndex, int frameIndex);

    /**
     * Gets access to a single data sequence. If this object represents a reader, 
     * this sequence will generally not be fully in memory but lazy loaded
     * from disk or another resource.
     * @param sequenceIndex
     * @return access to the requested data sequence
     * @throws IOException
     */
    public IDataSequence getSequence(int sequenceIndex);
}
