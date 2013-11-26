package stallone.datasequence.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.DataSequenceLoaderIterator;

/**
 * Reader for compressed gromacs xtc format.
 *
 * @author  Martin Senne, Frank Noe
 */
public class XtcReader implements IDataReader
{

    private XtcFile trajectory;

    /**
     * Constructor for XtcReader.
     *
     * @param   trajFilename  is the filename of the xtc file to open.
     *
     * @throws  IOException
     */
    public XtcReader(String trajFilename) throws IOException
    {
        trajectory = new XtcFile(trajFilename);
    }

    @Override
    public void setSource(String name)
    {
        trajectory.setSource(name);
    }

    @Override
    public void scan()
            throws IOException
    {
        trajectory.scan();
    }


    @Override
    public int dimension()
    {
        return trajectory.nDOF();
    }

    @Override
    public int size()
    {
        return trajectory.nFrames();
    }

    //@Override
    public IDoubleArray get(int frameIndex, IDoubleArray target)
    {
        try
        {
            return trajectory.getPositionsAt(frameIndex);
        } catch (IOException ex)
        {
            Logger.getLogger(XtcReader.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Unable to read frame " + frameIndex + ".");
        }
    }

    @Override
    public void close()
            throws IOException
    {
        trajectory.close();
    }

    @Override
    public void open()
            throws IOException
    {
        trajectory.open();
    }

    @Override
    public double getTime(int frameIndex)
    {
        try
        {
            return trajectory.getTimeAt(frameIndex);
        }
        catch(IOException e)
        {
            return frameIndex;
        }
    }

    @Override
    public IDoubleArray get(int frameIndex)
    {
        return(get(frameIndex, null));
    }

    @Override
    public IDoubleArray getView(int index)
    {
        return(get(index));
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(new DataSequenceLoaderIterator(this));
    }

    @Override
    public IDataSequence load()
    {
        IDataList res = DataSequence.create.createDatalist();
        for (int i=0; i<this.size(); i++) {
            // TODO : think about it: why should one use a copy here?
            //res.add(get(i).copy());
            res.add(get(i));
        }
        return res;
    }

    //@Override
    public String getFileName()
    {
        return(trajectory.getFileName());
    }

    @Override
    public long memorySize()
    {
        return(this.size()*this.dimension()*8);
    }
}
