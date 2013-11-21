/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.*;
import stallone.datasequence.io.AsciiDataSequenceReader;
import stallone.datasequence.io.DataSequenceFileFormats;

/**
 *
 * @author noe
 */
public class DataSequenceFactory
{
    public Iterator<IDoubleArray> iterator(IDataSequence seq)
    {
        return new DataSequenceIterator(seq);
    }
    public IDataSequenceLoader dataSequenceLoader(List<String> files, IDataReader sequenceLoader)
            throws IOException
    {
        DataSequenceLoader_SingleReaderImpl loader = new DataSequenceLoader_SingleReaderImpl(files, sequenceLoader);
        return (loader);
    }  
    
    public IDataSequenceLoader dataSequenceLoader(List<String> files)
            throws IOException
    {
        IDataReader sequenceLoader = DataSequenceFileFormats.createLoader(files.get(0));
        return dataSequenceLoader(files, sequenceLoader);
    }

    public IDataReader dataSequenceLoader(String file)
            throws IOException
    {
        IDataReader sequenceLoader = DataSequenceFileFormats.createLoader(file);
        sequenceLoader.scan();
        return sequenceLoader;
    }

    public Iterable<IDoubleArray> interleavedDataIterable(IDataSequenceLoader loader, int stepsize)
    {
        return new DataSequenceLoader_SingleDataIterable(loader,stepsize);
    }
    
    public IDataList createDatalist()
    {
        DataList ds = new DataList();
        return(ds);
    }

    /**
     * Writes to standard out
     * @param dimension
     * @return 
     */
    public IDataWriter createConsoleDataOutput(int dimension)
    {
        StreamDataWriter out = new StreamDataWriter(System.out);
        return(out);
    }

    /**
     * Writes to standard out
     * @param dimension
     * @return 
     */
    public IDataWriter createConsoleDataOutput(int dimension, String dataDelimiter, String datasetDelimiter)
    {
        StreamDataWriter out = new StreamDataWriter(System.out);
        out.setOutputDelimiters(dataDelimiter, datasetDelimiter);
        return(out);
    }

    /**
     * Writes to file. Automatically identifies filetype by extension.
     * @param size
     * @param dimension
     * @return 
     */
    public IDataWriter createDataWriter(String file, int size, int dimension)
            throws FileNotFoundException, IOException
    {
        return DataSequenceFileFormats.createWriter(file, size, dimension);
    }    
    
    /**
     * Writes to file
     * @param dimension
     * @return 
     */
    public IDataWriter createASCIIDataWriter(String file, int dimension)
            throws FileNotFoundException
    {
        StreamDataWriter out = new StreamDataWriter(file);
        return(out);
    }
    
    /**
     * Writes to file
     * @param dimension
     * @return 
     */
    public IDataWriter createASCIIDataWriter(String file, int dimension, String dataDelimiter, String datasetDelimiter)
            throws FileNotFoundException
    {
        StreamDataWriter out = new StreamDataWriter(file);
        out.setOutputDelimiters(dataDelimiter, datasetDelimiter);
        return(out);
    }
    
    /**
     * creates an ascii file reader
     * @param dimension
     * @return 
     */
    public IDataReader createASCIIDataReader(String file)
            throws IOException
    {        
        AsciiDataSequenceReader input = new AsciiDataSequenceReader(file);
        return(input);
    }    
}
