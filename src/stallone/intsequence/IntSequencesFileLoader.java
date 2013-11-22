/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.intsequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import stallone.api.ints.IIntArray;
import stallone.api.intsequence.IIntReader;
import stallone.api.intsequence.IIntSequenceLoader;

/**
 *
 * @author noe
 */
public class IntSequencesFileLoader
        implements IIntSequenceLoader
{

    private IIntReader loader;
    private ArrayList<String> sources = new ArrayList<String>();

    private int currentSource; // the source that is currently in the loader
    private boolean isOpen; // when the loader is currently open

    // info
    class DataSequenceInfo
    {

        protected int size;
        protected long memorySize;
    }
    private ArrayList<DataSequenceInfo> info = new ArrayList<DataSequenceInfo>();
    private int totalSize = 0;
    private long largestMemorySize = 0, totalMemorySize = 0;

    @Override
    public void setLoader(IIntReader _loader)
    {
        this.loader = _loader;
    }

    /**
     * Adds a link to a file or data base entry of a data sequence
     * @param link filename or URL
     */
    @Override
    public void addSource(String link)
    {
        this.sources.add(link);
    }

    /**
     * Scans all files or data base entries.
     */
    @Override
    public void scan()
            throws IOException
    {
        for (String s : sources)
        {
            loader.setSource(s);
            loader.open();
            loader.scan();

            DataSequenceInfo ds = new DataSequenceInfo();
            ds.memorySize = loader.memorySize();
            ds.size = loader.size();
            info.add(ds);

            totalSize += ds.size;
            if (ds.memorySize > largestMemorySize)
            {
                largestMemorySize = ds.memorySize;
            }
            totalMemorySize += ds.memorySize;

            loader.close();
        }
    }

    /**
     * Total number of sequences
     * @return
     */
    @Override
    public int numberOfSequences()
    {
        return (this.info.size());
    }

    /**
     * Total number of data objects
     * @return
     */
    @Override
    public int size()
    {
        return (totalSize);
    }

    /**
     * size of the sequence with the given index
     * @param trajIndex
     * @return
     */
    @Override
    public int size(int trajIndex)
    {
        return (this.info.get(trajIndex).size);
    }

    /**
     * Returns an iterable that can iterate over single data objects.
     * Only single data objects are loaded into memory and only one file is open at a time
     */
    @Override
    public Iterable<Integer> getSingleIntLoader()
    {
        return (new SingleIntIterable());
    }

    /**
     * Returns an iterable that can iterate over single data sequences.
     * Each data sequence is fully loaded into memory at a time. Only one file is open at a time
     * @return
     */
    @Override
    public Iterable<IIntArray> getSingleSequenceLoader()
    {
        return (new SingleSequenceIterable());
    }

    /**
     * Memory requirement for the given sequence
     */
    @Override
    public long memorySizeOfSingleSequence(int index)
    {
        return (info.get(index).memorySize);
    }

    /**
     * Memory requirement for the largest single sequence
     */
    @Override
    public long memorySizeOfLargestSequence()
    {
        return (largestMemorySize);
    }

    /**
     * Memory requirement for everything
     */
    @Override
    public long memorySizeTotal()
    {
        return (totalMemorySize);
    }

    @Override
    public int load(int sequenceIndex, int frameIndex)
            throws IOException
    {
        if (currentSource != sequenceIndex)
        {
            loader.close();
            loader.setSource(sources.get(sequenceIndex));
            loader.open();
        }

        if (!isOpen)
            loader.open();

        return loader.get(frameIndex);
    }

    @Override
    public IIntArray loadSequence(int sequenceIndex)
            throws IOException
    {
        if (currentSource != sequenceIndex)
        {
            loader.close();
            loader.setSource(sources.get(sequenceIndex));
            loader.open();
        }

        if (!isOpen)
            loader.open();

        return loader.load();
    }

    /**
     * Loads everything into memory
     * @return
     */
    @Override
    public List<IIntArray> loadAll()
            throws IOException
    {
        List<IIntArray> res = new ArrayList<IIntArray>();
        for (String s : sources)
        {
            loader.setSource(s);
            loader.open();
            res.add(loader.load());
            loader.close();
        }
        return (res);
    }

    class SingleDataIterator implements Iterator<Integer>
    {
        private int itraj = 0, iindex = 0;

        public SingleDataIterator()
        {
            try
            {
                loader.setSource(sources.get(0));
                loader.open();
            }
            catch (IOException e)
            {
                throw (new RuntimeException(e));
            }
        }

        @Override
        public boolean hasNext()
        {
            return (itraj < numberOfSequences() - 1 || (itraj == numberOfSequences() - 1 && iindex < size(itraj)));
        }

        @Override
        public Integer next()
        {
            Integer res = loader.get(iindex);
            try
            {
                advance();
            } catch (IOException e)
            {
                throw (new RuntimeException(e));
            }
            return (res);
        }

        private void advance()
                throws IOException
        {
            iindex++;
            if (iindex >= size(itraj))
            {
                itraj++;
                loader.close();
                if (itraj < numberOfSequences())
                {
                    loader.setSource(sources.get(itraj));
                    loader.open();
                }

                iindex = 0;
            }
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SingleIntIterable implements Iterable<Integer>
    {

        @Override
        public Iterator<Integer> iterator()
        {
            return (new SingleDataIterator());
        }
    }

    class SingleSequenceIterator implements Iterator<IIntArray>
    {

        private int itraj = 0;

        public SingleSequenceIterator()
        {
            try
            {
                loader.setSource(sources.get(0));
                loader.open();
            } catch (IOException e)
            {
                throw (new RuntimeException(e));
            }
        }

        @Override
        public boolean hasNext()
        {
            return (itraj < numberOfSequences());
        }

        @Override
        public IIntArray next()
        {
            try
            {
                loader.close();

                IIntArray res = loader.load();

                itraj++;
                if (itraj < numberOfSequences())
                {
                    loader.setSource(sources.get(itraj));
                    loader.open();
                }

                return (res);

            } catch (IOException e)
            {
                throw (new RuntimeException(e));
            }
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class SingleSequenceIterable implements Iterable<IIntArray>
    {

        @Override
        public Iterator<IIntArray> iterator()
        {
            return (new SingleSequenceIterator());
        }
    }
}
