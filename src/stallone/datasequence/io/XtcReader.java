package stallone.datasequence.io;

import static stallone.api.API.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.doubles.PrimitiveDoubleTable;

/**
 * Reader for compressed gromacs xtc format.
 *
 * @author  Martin Senne, Frank Noe
 */
public class XtcReader implements IDataReader
{
    private XtcFile trajectory;
    private IDoubleArray preconstructedFrame;
    private int[] selection = null;

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
        preconstructedFrame = new PrimitiveDoubleTable(trajectory.nrAtoms, 3);
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

    @Override
    public void select(int[] _selection)
    {
        this.selection = _selection;
    }

    @Override
    public int[] getSelection()
    {
        if (this.selection == null)
            return intArrays.range(trajectory.nrAtoms);
        else
            return this.selection;
    }

    //@Override
    public IDoubleArray get(int frameIndex, IDoubleArray target)
    {
        try
        {
            if (selection == null)
                return trajectory.getPositionsAt(frameIndex, target);
            else
            {
                trajectory.getPositionsAt(frameIndex, this.preconstructedFrame);
                for (int i=0; i<selection.length; i++)
                {
                    target.set(i, 0, preconstructedFrame.get(selection[i],0));
                    target.set(i, 1, preconstructedFrame.get(selection[i],1));
                    target.set(i, 2, preconstructedFrame.get(selection[i],2));
                }
                return target;
            }
        } 
        catch (IOException ex)
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
        if (selection == null)
            return(get(frameIndex, doublesNew.array(trajectory.nrAtoms,3)));
        else
            return(get(frameIndex, doublesNew.array(selection.length,3)));
    }

    @Override
    public IDoubleArray getView(int index)
    {
        return(get(index));
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(new DataReaderIterator(this));
    }

    @Override
    public Iterator<IDoubleArray[]> pairIterator(int spacing)
    {
        return new DataReaderPairIterator(this, spacing);
    }

    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        class PairIterable implements Iterable<IDoubleArray[]>
        {
            private IDataReader seq;
            private int spacing = 1;

            public PairIterable(IDataReader _seq, int _spacing)
            {
                this.seq = _seq;
                this.spacing = _spacing;
            }

            @Override
            public Iterator<IDoubleArray[]> iterator()
            {
                return (new DataReaderPairIterator(seq, spacing));
            }
        }
        return new PairIterable(this,spacing);
    }
    
    @Override
    public IDataSequence load()
    {
        IDataList res = DataSequence.create.list();
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
