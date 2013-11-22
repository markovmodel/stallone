/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class DataSequenceLoader_SingleReaderImpl
        implements IDataSequenceLoader
{
    private List<String> sources;
    private boolean scanned = false;
    private IDataReader reader;
    private int dimension = -1;
    private int currentSource; // the source that is currently in the loader
    private boolean isOpen = false; // when the loader is currently open

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
     * @param _sources
     */
    public DataSequenceLoader_SingleReaderImpl(List<String> _sources, IDataReader _reader)
            throws IOException
    {
        this.sources = _sources;
        this.reader = _reader;

        // basic checks: do files exist?
        // initialize file size info.
        if (sources == null)
            throw new NullPointerException("List of sources provided to DataSequenceLoader is null. Cannot open input files.");
        for (int i=0; i<sources.size(); i++)
        {
            if (!(new File(sources.get(i))).exists())
                throw new IOException("Could not open file: "+sources.get(i));
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
                reader.close();
            }
            reader.setSource(sources.get(index));
            reader.open();
            isOpen = true;
            reader.scan();

            currentSource = index;
        }
    }

    /**
     * Scans all files or data base entries. Makes the info methods available,
     * i.e. methods to query the memory requirements, number of frames, etc.
     */
    @Override
    public void scan()
            throws IOException
    {
        info.clear();
        dimension = -1;

        for (int i = 0; i < sources.size(); i++)
        {
            makeAvailable(i);

            DataSequenceInfo ds = new DataSequenceInfo();
            if (this.dimension == -1)
            {
                dimension = reader.dimension();
            }
            else
            {
                if (dimension != reader.dimension())
                {
                    throw (new IOException("Input files have inconsistent dimension: " + sources.get(i) + " has dimension " + reader.dimension() + ", while dimension " + dimension + " was set be the file(s) read earlier."));
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
            }
            catch (IOException e)
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
            }
            catch (IOException e)
            {
                IO.util.error("Exception while trying to open trajectory " + sources.get(0) + ":\n" + e);
            }
            dimension = reader.dimension();
        }
        return (this.info.get(trajIndex).size);
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
            }
            catch (IOException e)
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
    public Iterable<IDoubleArray> getSingleDataLoader()
    {
        return (new DataSequenceLoader_SingleDataIterable(this));
    }

    /**
     * Returns an iterable that can iterate over single data sequences. Each
     * data sequence is fully loaded into memory at a time. Only one file is
     * open at a time
     *
     * @return
     */
    @Override
    public Iterable<IDataSequence> getSingleSequenceLoader()
    {
        return (new DataSequenceLoader_SingleSequenceIterable(this));
    }

    @Override
    public IDoubleArray load(int sequenceIndex, int frameIndex)
            throws IOException
    {
        makeAvailable(sequenceIndex);
        return reader.get(frameIndex);
    }

    @Override
    public IDataSequence loadSequence(int sequenceIndex)
            throws IOException
    {
        makeAvailable(sequenceIndex);
        return reader.load();
    }

    /**
     * Loads everything into memory
     *
     * @return
     */
    @Override
    public List<IDataSequence> loadAll()
            throws IOException
    {
        List<IDataSequence> res = new ArrayList<IDataSequence>();
        for (int i = 0; i < sources.size(); i++)
        {
            makeAvailable(i);
            res.add(reader.load());
        }
        return (res);
    }
}
