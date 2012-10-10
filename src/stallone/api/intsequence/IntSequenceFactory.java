/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import java.io.IOException;
import java.util.List;
import stallone.api.ints.IIntArray;
import stallone.api.io.IO;
import stallone.api.strings.Strings;
import stallone.intsequence.*;
import stallone.io.CachedAsciiFileReader;

/**
 *
 * @author noe
 */
public class IntSequenceFactory
{
    public IIntSequenceLoader intSequenceLoader(List<String> files) 
            throws IOException
    {
        // test first file
        String firstFile = files.get(0);
        CachedAsciiFileReader firstReader = IO.create.asciiReader(firstFile);
        String[] words = Strings.util.split(firstReader.getLine(0));
        IIntReader sequenceLoader;
        if (words.length == 1)
        {
            sequenceLoader = new IntSequenceReaderAsciiDense();
        }
        else
        {
            sequenceLoader = new IntSequenceReaderAsciiSparse();
        }
        return intSequenceLoader(files, sequenceLoader);
    }

    public IIntSequenceLoader intSequenceLoader(List<String> files, IIntReader sequenceLoader)
    {
        IntSequencesFileLoader loader = new IntSequencesFileLoader();
        loader.setLoader(sequenceLoader);
        for (String s : files)
            loader.addSource(s);
        return (loader);
    }    
    
    public IIntWriter intSequenceWriter(String file)
            throws IOException
    {
        return new IntSequenceWriterAsciiDense(file);
    }
    
    public IntArrayTokenizer intSequenceTokenizer(IIntArray arr)
    {
        return new IntArrayTokenizer(arr);
    }
}
