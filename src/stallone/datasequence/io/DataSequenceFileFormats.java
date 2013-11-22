/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataWriter;
import stallone.api.io.IO;
import stallone.datasequence.StreamDataWriter;

/**
 *
 * @author noe
 */
public class DataSequenceFileFormats
{
    private static String getInputFileFormat(String filename)
    {
        String internalFormat;
        String requestedFormat = "auto";

        // xtc | dcd | ascii | {auto}
        if (requestedFormat.equals("auto"))
        {
            String ext = IO.util.getExtension(filename);

            if (ext.equals("xtc"))
            {
                internalFormat = "xtc";
            }
            else if (ext.equals("dcd"))
            {
                internalFormat = "dcd";
            }
            else
            {
                internalFormat = "ascii";
            }
        }
        else
        {
            internalFormat = requestedFormat;
        }

        return (internalFormat);
    }

    public static IDataReader createLoader(String file)
            throws IOException
    {
                String internalFormat = getInputFileFormat(file);
                IDataReader reader = null;

                if (internalFormat.equals("xtc"))
                {
                    reader = new XtcReader(file);
                }
                else if (internalFormat.equals("dcd"))
                {
                    reader = new DcdReader(file);
                }
                else if (internalFormat.equals("ascii"))
                {
                    reader = new AsciiDataSequenceReader(file);
                }

                return reader;
    }

    public static IDataWriter createWriter(String file, int size, int dimension)
            throws FileNotFoundException, IOException
    {
        if (!new File(IO.util.getDirectory(file)).exists())
            throw new FileNotFoundException("File "+file+" cannot be written. Check if path name is valid and file location is accessible.");

        String extension = IO.util.getExtension(file);
        IDataWriter writer = null;
        if (extension.equalsIgnoreCase("dcd"))
        {
            writer = new DcdWriter(file, size, dimension);
        }
        else if (extension.equalsIgnoreCase("xtc"))
        {
            writer = new StreamDataWriter(file);
        }
        else
        {
            writer = new AsciiDataSequenceWriter(file);
        }
        return(writer);
    }

}
