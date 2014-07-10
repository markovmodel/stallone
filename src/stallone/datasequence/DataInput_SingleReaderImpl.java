/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import static stallone.api.API.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.doubles.IDoubleArray;
import stallone.api.io.IO;

/**
 *
 * Single-Reader implementation of a DataSequenceLoader. This implementation
 * only keeps a single reader to the source files and only keeps one file open
 * at a time. It is geared towards saving memory and keeping the number of open
 * files at 1, and will work fine as long as data is linearly traversed once
 * through one of the iterators supplied. This implementation will be
 * computationally inefficient if the data is accessed multiply times or via
 * random access. This is because whenever a different file is accessed, its
 * content is read and scanned, even if this has already been done in the past.
 * Using this reader in a random access fashion thus creates much computational
 * overhead.
 *
 * When random access is desired, a different implementation should be used.
 *
 * @author noe
 */
public class DataInput_SingleReaderImpl
        implements IDataSequenceLoader
{
    private List<String> sources;
    private boolean scanned = false;
    private IDataReader reader;
    private int dimension = -1;
    private int currentSource; // the source that is currently in the loader
    private boolean isOpen = false; // when the loader is currently open

    
    /**
     * Does nothing
     */
    @Override
    public void addSender(IDataProcessor sender)
    {
    }

    /**
     * Does nothing
     */
    @Override
    public void addReceiver(IDataProcessor receiver)
    {
    }

    /**
     * Does nothing
     */
    @Override
    public void run()
    {
    }

    /**
     * Does nothing
     */
    @Override
    public void cleanup()
    {
    }

    // info
    class DataSequenceInfo
    {
        protected int size;
        protected long memorySize;
    }
    private ArrayList<DataSequenceInfo> info = new ArrayList<DataSequenceInfo>();
    private int totalSize = 0;
    private long largestMemorySize = 0, totalMemorySize = 0;

    /**
     * Creates a loader object with the list of sources provided.
     *
     * @param _sources
     */
    public DataInput_SingleReaderImpl(List<String> _sources, IDataReader _reader)
            throws IOException
    {
        this.sources = _sources;
        this.reader = _reader;

        // basic checks: do files exist?
        // initialize file size info.
        if (sources == null)
        {
            throw new NullPointerException("List of sources provided to DataSequenceLoader is null. Cannot open input files.");
        }
        for (int i = 0; i < sources.size(); i++)
        {
            if (!(new File(sources.get(i))).exists())
            {
                throw new IOException("Could not open file: " + sources.get(i));
            }
            info.add(new DataSequenceInfo());
        }
    }

    private void makeAvailable(int index)
            throws IOException
    {
        if ((currentSource != index) || !isOpen)
        {
            if (isOpen)
            {
                System.out.println(" ma: closing");
                reader.close();
            }
            reader.setSource(sources.get(index));
            System.out.println(" ma: opening");
            reader.open();
            isOpen = true;
            System.out.println(" ma: scanning");
            reader.scan();

            currentSource = index;
        }
    }

    /**
     * Scans all files or data base entries. Makes the info methods available,
     * i.e. methods to query the memory requirements, number of frames, etc.
     */
    @Override
    public void init()
            //throws IOException
    {
        info.clear();
        dimension = -1;

        for (int i = 0; i < sources.size(); i++)
        {
            try
            {
                makeAvailable(i);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            
            DataSequenceInfo ds = new DataSequenceInfo();
            if (this.dimension == -1)
            {
                dimension = reader.dimension();
            }
            else
            {
                if (dimension != reader.dimension())
                {
                    throw (new RuntimeException("Input files have inconsistent dimension: " + sources.get(i) + " has dimension " + reader.dimension() + ", while dimension " + dimension + " was set be the file(s) read earlier."));
                }
            }
            ds.memorySize = reader.memorySize();
            ds.size = reader.size();
            info.add(ds);

            totalSize += ds.size;
            if (ds.memorySize > largestMemorySize)
            {
                largestMemorySize = ds.memorySize;
            }
            totalMemorySize += ds.memorySize;
        }

        scanned = true;
    }

    /**
     * Total number of sequences
     *
     * @return
     */
    @Override
    public int numberOfSequences()
    {
        return (this.sources.size());
    }

    /**
     * Dimension of the data
     *
     * @return
     */
    @Override
    public int dimension()
    {
        if (dimension == -1)
        {
            try
            {
                makeAvailable(0);
            } catch (IOException e)
            {
                IO.util.error("Exception while trying to open trajectory " + sources.get(0) + ":\n" + e);
            }
            dimension = reader.dimension();
        }
        return dimension;
    }

    /**
     * Total number of data objects
     *
     * @return
     */
    @Override
    public int size()
    {
        if (!scanned)
        {
            IO.util.error("Need to call scan() first before requesting information from a DataSequenceLoader.");
        }
        return (totalSize);
    }

    /**
     * size of the sequence with the given index
     *
     * @param trajIndex
     * @return
     */
    @Override
    public int size(int trajIndex)
    {
        if (!scanned)
        {
            try
            {
                makeAvailable(trajIndex);
                this.info.get(trajIndex).size = reader.size();
                this.info.get(trajIndex).memorySize = reader.memorySize();
            } catch (IOException e)
            {
                IO.util.error("Exception while trying to open trajectory " + sources.get(0) + ":\n" + e);
            }
            dimension = reader.dimension();
        }
        return (this.info.get(trajIndex).size);
    }

    /**
     * Returns the trajectory name. This is either a unique identifier or a full file path.
     * @param trajIndex
     * @return 
     */
    @Override
    public String name(int trajIndex)
    {
        return this.sources.get(trajIndex);
    }
    
    /**
     * Memory requirement for the given sequence
     */
    @Override
    public long memorySizeOfSingleSequence(int trajIndex)
    {
        if (!scanned)
        {
            try
            {
                makeAvailable(trajIndex);
                this.info.get(trajIndex).size = reader.size();
                this.info.get(trajIndex).memorySize = reader.memorySize();
            } catch (IOException e)
            {
                IO.util.error("Exception while trying to open trajectory " + sources.get(0) + ":\n" + e);
            }
            dimension = reader.dimension();
        }
        return (info.get(trajIndex).memorySize);
    }

    /**
     * Memory requirement for the largest single sequence
     */
    @Override
    public long memorySizeOfLargestSequence()
    {
        if (!scanned)
        {
            IO.util.error("Need to call scan() first before requesting information from a DataSequenceLoader.");
        }
        return (largestMemorySize);
    }

    /**
     * Memory requirement for everything
     */
    @Override
    public long memorySizeTotal()
    {
        if (!scanned)
        {
            IO.util.error("Need to call scan() first before requesting information from a DataSequenceLoader.");
        }
        return (totalMemorySize);
    }

    /**
     * Returns an iterable that can iterate over single data objects. Only
     * single data objects are loaded into memory and only one file is open at a
     * time
     */
    @Override
    public Iterable<IDoubleArray> singles()
    {
        return (new DataInput_SingleDataIterable(this));
    }

    /**
     * Returns an iterable that can iterate over single data objects. Only
     * single data objects are loaded into memory and only one file is open at a
     * time
     */
    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        return (new DataInput_DataPairIterable(this, spacing));
    }

    /**
     * Returns an iterable that can iterate over single data sequences. Each
     * data sequence is fully loaded into memory at a time. Only one file is
     * open at a time
     *
     * @return
     */
    @Override
    public Iterable<IDataSequence> sequences()
    {
        return (new DataInput_SingleSequenceIterable(this));
    }

    @Override
    public IDoubleArray get(int sequenceIndex, int frameIndex)
    {
        try
        {
            makeAvailable(sequenceIndex);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(DataInput_SingleReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reader.get(frameIndex);
    }

    @Override
    public IDataSequence getSequence(int sequenceIndex)
    {
        try
        {
            makeAvailable(sequenceIndex);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(DataInput_SingleReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reader;
    }
    
    
    @Override
    public IDataSequence loadSequence(int sequenceIndex)
    {
        try
        {
            makeAvailable(sequenceIndex);
        } catch (IOException ex)
        {
            Logger.getLogger(DataInput_SingleReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reader.load();
    }

    /**
     * Loads everything into memory
     *
     * @return
     */
    @Override
    public IDataInput loadAll()
    {
        List<IDataSequence> res = new ArrayList<IDataSequence>();
        for (int i = 0; i < sources.size(); i++)
        {
            try
            {
                makeAvailable(i);
            } catch (IOException ex)
            {
                Logger.getLogger(DataInput_SingleReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            res.add(reader.load());
        }

        IDataInput ret = null;

        try
        {
            ret = dataNew.dataInput(res);
        } 
        catch (IOException ex) // this cannot happen
        {
            Logger.getLogger(DataInput_SingleReaderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }
}
