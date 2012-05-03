/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import java.io.IOException;
import java.util.List;
import stallone.intsequence.*;

/**
 *
 * @author noe
 */
public class IntSequenceFactory
{
    public IIntSequenceLoader intSequenceLoader(List<String> files)
    {
        IIntReader sequenceLoader = new IntReaderAscii();
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
        return new IntSequenceWriter(file);
    }
}
