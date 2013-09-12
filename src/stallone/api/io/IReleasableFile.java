/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.io;

import java.io.IOException;

/**
 *
 * @author noe
 */
public interface IReleasableFile
{
    /**
     * Releases the file handle by closing the underlying file input stream 
     * without deleting the index information. If the file is already closed, this has no effect.
     * 
     */
    public void close()
            throws IOException;
    
    /**
     * Re-opens the file access by constructing a new input stream. Will re-use index information (if any)
     * that has been generated upon the first time the file was opened. This may cause problems if the file
     * was changed in between.
     * Has no effect if the file is already open.
     * @throws IOException 
     */
    public void open()
            throws IOException;
    
    /**
     * Returns the file this reader refers to
     * @return 
     */
    public String getFileName();    
}
