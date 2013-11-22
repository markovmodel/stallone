package stallone.datasequence.io;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataReader;
import stallone.api.doubles.Doubles;
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
    private IDoubleArray preconstructedArray;

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
        preconstructedArray = Doubles.create.array(trajectory.nrAtoms, 3);
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
            float[][] positions = trajectory.getPositionsAt(frameIndex);

            int noOfAtoms = positions.length;

            for (int i = 0; i < positions.length; i++)
            {
                int idx = i * 3;
                target.set(idx, positions[i][0]);
                target.set(idx + 1, positions[i][1]);
                target.set(idx + 2, positions[i][2]);
            }

            return target;
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
        return(get(frameIndex, preconstructedArray));
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
        for (int i=0; i<this.size(); i++)
            res.add(get(i).copy());
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
