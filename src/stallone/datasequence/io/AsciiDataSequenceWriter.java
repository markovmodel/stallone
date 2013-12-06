package stallone.datasequence.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.datasequence.IDataWriter;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

/**
 * Class AsciiTrajectoryWriter to write positions of a trajectory to an ascii
 * file.
 *
 * @author Martin Senne, Frank Noe
 */
public class AsciiDataSequenceWriter implements IDataWriter
{
    private boolean fixedPrecision = false;
    private int predigits = 5,postdigits = 5;
    
    private String filename;
    private BufferedWriter writer;
    private double currentTime;

    public AsciiDataSequenceWriter(String filename) throws IOException
    {
        this.filename = filename;
        writer = new BufferedWriter(new FileWriter(filename));
        currentTime = 0.0d;
    }

    public void setFixedPrecision(int pre, int post)
    {
        fixedPrecision = true;
        predigits = pre;
        postdigits = post;
    }
    
    @Override
    public void add(IDoubleArray data)
    {
        try
        {
            String strout;
            if (fixedPrecision)
                strout = Doubles.util.toString(data," "," ",predigits,postdigits);
            else
                strout = Doubles.util.toString(data," "," ");
            writer.write(strout+"\n");
        }
        catch (IOException ex)
        {
            Logger.getLogger(AsciiDataSequenceWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        // System.out.println( x );
    }

    @Override
    public void addAll(Iterable<IDoubleArray> data)
    {
        for (IDoubleArray arr: data)
            add(arr);
    }


    @Override
    public void close()
            throws IOException
    {
        writer.close();
    }
}
