package stallone.datasequence.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

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
    private int predigits = 5, postdigits = 5;
    private String filename;
    private PrintStream out;
    private double currentTime;
    private String dataDelimiter = " ", datasetDelimiter = "\n";

    public AsciiDataSequenceWriter(String filename) 
            throws FileNotFoundException 
    {
        this.filename = filename;
        out = new PrintStream(filename);
        currentTime = 0.0d;
    }

    public AsciiDataSequenceWriter(PrintStream _out)
    {
        this.filename = filename;
        out = _out;
        currentTime = 0.0d;
    }

    public void setOutputDelimiters(String _dataDelimiter, String _datasetDelimiter)
    {
        this.dataDelimiter = _dataDelimiter;
        this.datasetDelimiter = _datasetDelimiter;
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
        String strout;
        if (fixedPrecision)
        {
            strout = Doubles.util.toString(data, dataDelimiter, dataDelimiter, predigits, postdigits);
        }
        else
        {
            strout = Doubles.util.toString(data, dataDelimiter, dataDelimiter);
        }
        out.print(strout+datasetDelimiter);
    }

    @Override
    public void addAll(Iterable<IDoubleArray> data)
    {
        for (IDoubleArray arr : data)
        {
            add(arr);
        }
    }

    @Override
    public void close()
            throws IOException
    {
        out.close();
    }
}
