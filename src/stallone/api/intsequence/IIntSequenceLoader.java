/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import java.io.IOException;
import java.util.List;
import stallone.api.ints.IIntArray;

/**
 *
 * Interface for loaders that retrieve DataSequences (e.g. trajectories) 
 * from file systems or data bases. Shields the usage of this data from the
 * file / database access in order to efficiently deal with slow loading times,
 * while at the same time keeping the memory and file handle usage low. 
 * 
 * @author noe
 */
public interface IIntSequenceLoader
{
    public void setLoader(IIntReader loader);

    /**
     * Adds a link to a file or data base entry of a data sequence
     * @param link filename or URL
     */
    public void addSource(String link);

    /**
     * Scans all files or data base entries. 
     */
    public void scan()
            throws IOException;

    /**
     * Total number of sequences
     * @return 
     */
    public int numberOfSequences();

    /**
     * Total number of data objects
     * @return 
     */
    public int size();

    /** 
     * size of the sequence with the given index
     * @param trajIndex
     * @return 
     */
    public int size(int trajIndex);

    /**
     * Returns an iterable that can iterate over single data objects.
     * Only single data objects are loaded into memory and only one file is open at a time
     */
    public Iterable<Integer> getSingleIntLoader();

    
    /**
     * Returns an iterable that can iterate over single data sequences.
     * Each data sequence is fully loaded into memory at a time. Only one file is open at a time
     * @return 
     */
    public Iterable<IIntArray> getSingleSequenceLoader();

    /**
     * Memory requirement for the given sequence
     */ 
    public long memorySizeOfSingleSequence(int index);

    /**
     * Memory requirement for the largest single sequence
     */ 
    public long memorySizeOfLargestSequence();

    /**
     * Memory requirement for everything
     */ 
    public long memorySizeTotal();

    /**
     * Loads a single int
     * @param sequenceIndex
     * @param frameIndex
     * @return
     * @throws IOException 
     */
    public int load(int sequenceIndex, int frameIndex)
            throws IOException;
    
    /**
     * The entire subset, as given by the index set is loaded and returned.
     * @param indexes nx2 array with trajectory and within-trajectory indexes
     * @return 
     */
    public IIntArray loadSequence(int sequenceIndex)
            throws IOException;
    
    /**
     * Loads everything into memory
     * @return 
     */
    public List<IIntArray> loadAll()
            throws IOException;
            
}
