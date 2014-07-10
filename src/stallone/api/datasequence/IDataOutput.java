/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import stallone.api.dataprocessing.IDataProcessor;

/**
 *
 * Interface for loaders that retrieve DataSequences (e.g. trajectories)
 * from file systems or data bases. Shields the usage of this data from the
 * file / database access in order to efficiently deal with slow loading times,
 * while at the same time keeping the memory and file handle usage low.
 *
 * @author noe
 */
public interface IDataOutput extends IDataProcessor
{
    /**
     * Set naming convention conservative, i.e. all original filename are kept 
     * but are written to the target path.
     */
    public void namingConventionKeep();

    
    /**
     * Set naming convention conservative, i.e. all original filename are kept 
     * but are written to the target path.
     */
    public void namingConventionChangeExtension(String ext);
    
    
    /**
     * Set naming convention to new, i.e. all filename will be set to
     * [prefix][N][suffix], where N is the index of the trajectory.
     * If you want dots in the filename, they have to be included in prefix or
     * suffix.
     */
    public void namingConventionNew(String _prefix, String _suffix);
}
