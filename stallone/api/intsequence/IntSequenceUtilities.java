/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class IntSequenceUtilities
{
    public List<IIntArray> loadIntSequences(List<String> files)
            throws IOException
    {
        return IntSequence.create.intSequenceLoader(files).loadAll();
    }    

    public void writeIntSequences(List<IIntArray> data, List<String> files)
            throws IOException
    {
        if (data.size() != files.size())
            throw new IllegalArgumentException("Number of sequences is different from number of target files");
        
        for (int itraj=0; itraj<data.size(); itraj++)
        {
            PrintStream ps = new PrintStream(files.get(itraj));
            for (int i=0; i<data.get(itraj).size(); i++)
                ps.println(data.get(itraj).get(i));
            ps.close();
        }
    }    
    

}
