/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import java.util.List;

/**
 *
 * Interface for loaders that retrieve DataSequences (e.g. trajectories)
 * from file systems or data bases. Shields the usage of this data from the
 * file / database access in order to efficiently deal with slow loading times,
 * while at the same time keeping the memory and file handle usage low.
 *
 * @author noe
 */
public interface IDataSequenceLoader extends IDataInput
{
    //public void setLoader(IDataReader loader);

    /**
     * Adds a link to a file or data base entry of a data sequence
     * @param link filename or URL
     */
    //public void addSource(String link);

    /**
     * Scans all files or data base entries.
     * This method needs to be called before any of the info methods (size, memorySize...) can be called.
     * Scan will also check the file consistency, make sure that all source files have the same dimensionality
     * and can be successfully read.
     * It is not necessary to scan before using one of the iterables below, but in this case there is
     * no guarantee that the iterable won't crash at a file inconsistency.
     */
    public void scan()
            throws IOException;


    /**
     * Memory requirement for the given sequence. Need to scan() first before calling this method.
     */
    public long memorySizeOfSingleSequence(int index);

    /**
     * Memory requirement for the largest single sequence. Need to scan() first before calling this method.
     */
    public long memorySizeOfLargestSequence();

    /**
     * The entire subset, as given by the index set is loaded and returned. Need to scan() first before calling this method.
     * @param indexes nx2 array with trajectory and within-trajectory indexes
     * @return
     */
    //public IDataSequences loadSubset(IIntArray indexes)
    //        throws IOException;

    /**
     * Memory requirement for everything. Need to scan() first before calling this method.
     */
    public long memorySizeTotal();

    /**
     * Load a single data sequence. Does not require scan() to be called.
     * @param sequenceIndex
     * @return
     * @throws IOException
     */
    public IDataSequence loadSequence(int sequenceIndex);

    /**
     * Loads everything into memory. Does not require scan() to be called.
     * @return
     */
    public IDataInput loadAll();    
}
