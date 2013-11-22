/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.intsequence;

import java.io.IOException;
import java.io.PrintStream;
import stallone.api.ints.IIntArray;
import stallone.api.intsequence.IIntWriter;

/**
 *
 * @author noe
 */
public class IntSequenceWriterAsciiDense implements IIntWriter
{
    PrintStream ps;

    public IntSequenceWriterAsciiDense(String file)
            throws IOException
    {
        ps = new PrintStream(file);
    }

    @Override
    public void close()
    {
        ps.close();
    }

    @Override
    public void add(int data)
    {
        ps.println(data);
    }

    @Override
    public void addAll(IIntArray data)
    {
        for (int i=0; i<data.size(); i++)
            ps.println(data.get(i));
    }
}
