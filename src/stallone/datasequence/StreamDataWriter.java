/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import stallone.api.doubles.IDoubleArray;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import stallone.api.datasequence.IDataWriter;
import stallone.doubles.DoubleIO;

/**
 *
 * @author noe
 */
public class StreamDataWriter implements IDataWriter
{
    private PrintStream out;
    private String dataDelimiter = " ";
    private String datasetDelimiter = "\n";

    public StreamDataWriter(PrintStream _out)
    {
        this.out = _out;
    }

    public StreamDataWriter(String filename)
            throws FileNotFoundException
    {
        this.out = new PrintStream(filename);
    }

    public void setOutputDelimiters(String _dataDelimiter, String _datasetDelimiter)
    {
        this.dataDelimiter = _dataDelimiter;
        this.datasetDelimiter = _datasetDelimiter;
    }
    
    @Override
    public void add(IDoubleArray data)
    {
        DoubleIO.print(data, dataDelimiter, out);
        out.print(datasetDelimiter);
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
    {
        if (out != System.out)
            out.close();
    }
}
