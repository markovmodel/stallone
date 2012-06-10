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
public class IntSequenceWriterAsciiSparse implements IIntWriter
{
    PrintStream ps;
    boolean hasData = false, lastDataWritten = false;
    int time = 0;
    int lastData = 0;
    
    public IntSequenceWriterAsciiSparse(String file)
            throws IOException
    {
        ps = new PrintStream(file);
    }

    @Override
    public void close()
    {
        if (!lastDataWritten) // then also write the previous step
        {
            ps.println(time-1+"\t"+lastData);
        }
        ps.close();
    }

    @Override
    public void add(int data)
    {
        if (!hasData) // always write first data point
        {
            hasData = true;
            ps.println(time+"\t"+data);
            lastData = data;
            lastDataWritten = true;
        }
            
        if (data != lastData) // new data.
        {
            if (!lastDataWritten) // then also write the previous step
            {
                ps.println(time-1+"\t"+lastData);
            }
            ps.println(time+"\t"+data);
            lastDataWritten = true;
        }
        else
        {
            lastDataWritten = false;
        }

        time++;
    }

    @Override
    public void addAll(IIntArray data)
    {
        for (int i=0; i<data.size(); i++)
            ps.println(data.get(i));
    }
}
