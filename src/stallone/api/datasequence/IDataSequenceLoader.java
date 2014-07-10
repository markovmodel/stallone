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
