/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.DataArray;
import stallone.datasequence.DataList;
import stallone.datasequence.DataSequenceList;
import stallone.datasequence.DataInput_SingleReaderImpl;
import stallone.datasequence.io.AsciiDataSequenceReader;
import stallone.datasequence.io.AsciiDataSequenceWriter;
import stallone.datasequence.io.DataSequenceFileFormats;

/**
 *
 * @author noe
 */
public class DataSequenceFactory
{
    /**
     * Creates a multi sequence loader, the universal file accessor to load 
     * data from a set of data sequences.
     * @param files a list of data files
     * @param reader the data reader used to access the data files
     * @return A multi sequence loader to load data from the multi sequence
     */
    public IDataSequenceLoader multiSequenceLoader(List<String> files, IDataReader reader)
            throws IOException
    {
        DataInput_SingleReaderImpl loader = new DataInput_SingleReaderImpl(files, reader);
        loader.init();
        return (loader);
    }

    /**
     * Creates a multi sequence loader, the universal file accessor to load 
     * data from a set of data sequences.
     * @param files a list of data files whose file type is determined automaticalla
     * @return A multi sequence loader to load data from the multi sequence
     */
    public IDataSequenceLoader multiSequenceLoader(List<String> files)
            throws IOException
    {
        IDataReader sequenceLoader = DataSequenceFileFormats.createReader(files.get(0));
        sequenceLoader.scan();
        return multiSequenceLoader(files, sequenceLoader);
    }

    /**
     * Creates a multi sequence loader, the universal file accessor to load 
     * data from a set of data sequences.
     * @param file a list of data files
     * @param reader the data reader used to access the data files
     * @return A multi sequence loader to load data from the multi sequence
     */
    public IDataSequenceLoader multiSequenceLoader(String file, IDataReader reader)
            throws IOException
    {
        List<String> files = new ArrayList<String>();
        files.add(file);
        return multiSequenceLoader(files, reader);
    }
    
    /**
     * Creates a multi sequence loader, the universal file accessor to load 
     * data from a set of data sequences.
     * @param file a data file
     * @return A multi sequence loader to load data from the multi sequence
     */
    public IDataSequenceLoader multiSequenceLoader(String file)
            throws IOException
    {
        IDataReader reader = DataSequenceFileFormats.createReader(file);
        List<String> files = new ArrayList<String>();
        files.add(file);
        return multiSequenceLoader(files, reader);
    }

    /**
     * Wraps the input to an IDataInput object, the universal accessor for 
     * a set of data sequences.
     * @param data a list of files
     * @param reader the data reader to be used
     * @return An IDataInput object to access the data
     */
    public IDataInput dataInput(List<String> files, IDataReader reader)
            throws IOException
    {
        return multiSequenceLoader(files, reader);
    }

    /**
     * Wraps the input to an IDataInput object, the universal accessor for 
     * a set of data sequences.
     * @param data a list of file names or data sequences
     * @return An IDataInput object to access the data
     */
    public IDataInput dataInput(List data) 
            throws IOException
    {
        if (data.get(0) instanceof String)
            return multiSequenceLoader(data);
        if (data.get(0) instanceof IDataSequence)
        {
            DataSequenceList res = new DataSequenceList();
            res.addAll(data);
            return res;
        }   
        else
            throw new IllegalArgumentException("Type not supported");
    }

    /**
     * Wraps the input to an IDataInput object, the universal accessor for 
     * a set of data sequences.
     * @param data an single data file
     * @return An IDataInput object to access the data
     */
    public IDataInput dataInput(String file) 
            throws IOException    
    {
        return multiSequenceLoader(file);
    }    

    /**
     * Wraps the input to an IDataInput object, the universal accessor for 
     * a set of data sequences.
     * @param data an array of data sequences
     * @return An IDataInput object to access the data
     */
    public IDataInput dataInput(IDataSequence[] data)
    {
         DataSequenceList res = new DataSequenceList();
         res.addAll(Arrays.asList(data));
         return res;
    }
    
    /**
     * Wraps the input to an IDataInput object, the universal accessor for 
     * a set of data sequences.
     * @param data a single data sequence
     * @return An IDataInput object to access the data
     */
    public IDataInput dataInput(IDataSequence data)
    {
         DataSequenceList res = new DataSequenceList();
         res.add(data);
         return res;
    }
    
    /**
     * Creates an empty data sequence of flexible size
     * @return a data list object
     */
    public IDataList list()
    {
        DataList ds = new DataList();
        return(ds);
    }

    /**
     * Creates an empty data sequence of fixed size
     * @return a data sequence object
     */
    public IDataSequence array(int size)
    {
        DataArray ds = new DataArray(size);
        return(ds);
    }

    /**
     * Creates an empty data sequence of fixed size
     * @return a data sequence object
     */
    public IDataSequence array(IDoubleArray[] content)
    {
        DataArray ds = new DataArray(content);
        return(ds);
    }
    
    /**
     * Creates a reader to a data file. Its type is automatically determined by 
     * file extension
     * @param file
     * @return a data reader to the specified file
     * @throws IOException when an IO exception occurs while attempting to
     * access the file.
     */
    public IDataReader reader(String file)
            throws IOException
    {
        IDataReader sequenceReader = DataSequenceFileFormats.createReader(file);
        sequenceReader.scan();
        return sequenceReader;
    }
    
    /**
     * Creates a reader to a tabulated ascii data file. Data sets are assumed 
     * to be separated by line breaks, data elements within a line are assumed
     * to be separated by any white spaces.
     * @return a data reader to the specified file
     * @throws IOException when an IO exception occurs while attempting to
     * access the file.
     */
    public IDataReader readerASCII(String file)
            throws IOException
    {
        AsciiDataSequenceReader input = new AsciiDataSequenceReader(file);
        return(input);
    }
    
    
    /**
     * Creates a writer that writes data to a file, using the file name extension
     * to determine the file type. Currently supported: dcd and ascii
     * @param file the file name to write to. If the file exists, it will be
     * overwritten.
     * @param size the number of data sets expected that will also go to the
     * data file's meta information (e.g. header), if exists. 
     * Most writers tolerate writing more or less data sets than specified here,
     * But the header will not be automatically updated
     * @param dimension the number of data elements per data set
     * @return a data writer object
     * @throws FileNotFoundException if the specified file path is inaccessible
     */
    public IDataWriter writer(String file, int size, int dimension)
            throws FileNotFoundException, IOException
    {
        return DataSequenceFileFormats.createWriter(file, size, dimension);
    }

    
    /**
     * Creates a writer that writes data to stdout, using tabs and line breaks
     * as delimiters.
     * @return a data writer object
     */
    public IDataWriter writerConsole()
    {
        AsciiDataSequenceWriter out = new AsciiDataSequenceWriter(System.out);
        return(out);
    }

    /**
     * Creates a writer that writes data to stdout, using the specified
     * delimiters.
     * @param dataDelimiter the delimiter to be used after each data element
     * @param datasetDelimiter the delimiter to be used after each data set
     * @return a data writer object
     */
    public IDataWriter writerConsole(String dataDelimiter, String datasetDelimiter)
    {
        AsciiDataSequenceWriter out = new AsciiDataSequenceWriter(System.out);
        out.setOutputDelimiters(dataDelimiter, datasetDelimiter);
        return(out);
    }

    /**
     * Creates a writer that writes data to a tabulated ascii file, 
     * using tabs and line breaks as delimiters.
     * @param file the file name to write to. If the file exists, it will be
     * overwritten.
     * @return a data writer object
     * @throws FileNotFoundException if the specified file path is inaccessible
     */
    public IDataWriter writerASCII(String file)
            throws FileNotFoundException
    {
        AsciiDataSequenceWriter out = new AsciiDataSequenceWriter(file);
        return(out);
    }
    

    /**
     * Creates a writer that writes data to a tabulated ascii file, 
     * using tabs and line breaks as delimiters.
     * @param file the file name to write to. If the file exists, it will be
     * overwritten.
     * @param dataDelimiter the delimiter to be used after each data element
     * @param datasetDelimiter the delimiter to be used after each data set
     * @return a data writer object
     * @throws FileNotFoundException if the specified file path is inaccessible
     */
    public IDataWriter writerASCII(String file, String dataDelimiter, String datasetDelimiter)
            throws FileNotFoundException
    {
        AsciiDataSequenceWriter out = new AsciiDataSequenceWriter(file);
        out.setOutputDelimiters(dataDelimiter, datasetDelimiter);
        return(out);
    }

}
