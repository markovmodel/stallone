/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import stallone.io.BlockFileReader;
import stallone.io.CachedAsciiFileReader;

/**
 *
 * @author noe
 */
public class IOFactory
{
    public CachedAsciiFileReader asciiReader(String file) throws FileNotFoundException, IOException
    {
        CachedAsciiFileReader reader = new CachedAsciiFileReader(file);
        reader.scan();
        return reader;
    }

    public BlockFileReader asciiNumberReader(String file) throws FileNotFoundException, IOException
    {
        BlockFileReader reader = new BlockFileReader(file);
        reader.scan();
        return reader;
    }

}
