package stallone.datasequence.io;

import static stallone.api.API.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.BitSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.io.NicelyCachedRandomAccessFile;

/**
 * Reader for charmm / xplor / namd dcd files.
 *
 * <p>File specification and ideas for source code gained from sources of MolTools package (Frank Noe). source of VDM
 * plugin: dcdplugin.c ( http://www.ks.uiuc.edu/Research/vmd/plugins/doxygen/dcdplugin_8c-source.html )</p>
 *
 * @author  Martin Senne
 */
public class DcdReader implements IDataReader
{
    private String filename;
    private boolean format__dcd_is_charmm;
    private boolean format__dcd_has_4dims;
    private boolean format__dcd_has_extra_block;
    private ByteOrder byteOrder;
    private NicelyCachedRandomAccessFile niceRandomAccessFile;
    /** Number of sets of coordinates (NSET), that is number of frames. */
    private int numberOfFrames;
    /** Starting timestep (ISTART). */
    private int startingTimestep;
    /** Store NSAVC, the number of timesteps between dcd saves (NSAVC. */
    private int numberOfTimestepsBetweenSaves;
    /** Number of atoms. */
    private int numberOfAtoms;
    /** Number of fixed atoms (NAMNF). */
    private int numberOfFixedAtoms;
    /** Timestep delta (DELTA). */
    private double delta;
    private long frameAreaStartingPosition;
    private long framesizeRegular;
    private long framesizeWithFixed;
    private int nextFrameIndex;

    /** Selection for reading */
    private int[] selection;
    private BitSet selected;
    
    private Logger logger;

    /**
     * empty DcdReader, use {@link DcdReader#setSource(String)},
     * {@link DcdReader#open()} and {@link DcdReader#scan()}
     */
    public DcdReader()
    {
    }
    
    /**
     * constructs DcdReader with given filename
     * @param _filename
     * @throws IOException
     */
    public DcdReader(String _filename) throws IOException
    {
        this.filename = _filename;
        this.logger = Logger.getLogger(DcdReader.class.getName()); 
        initialize();
    }

    @Override
    public void setSource(String name)
    {
        this.filename = name;
    }
    
    @Override
    public void scan()
            throws IOException
    {
        this.initialize();
    }

    private void initialize() throws IOException
    {
        niceRandomAccessFile = new NicelyCachedRandomAccessFile(this.filename);
        byteOrder = detectEndianess();
        readHeader();
        niceRandomAccessFile.changePageSize((int) framesizeRegular);
        
        selection = intArrays.range(numberOfAtoms);
        selected = new BitSet(numberOfAtoms);
        selected.set(0, numberOfAtoms);
    }

    /**
     * First record length indicator (84 bytes) gives the clue of which endian byte order is used 54 00 00 00 =>
     * ByteOrder.LITTLE_ENDIAN 00 00 00 54 => ByteOrder.BIG_ENDIAN.
     *
     * @return  appropiate byte order
     */
    private ByteOrder detectEndianess() throws IOException
    {
        niceRandomAccessFile.seek(0);

        ByteBuffer recordLengthBuffer = niceRandomAccessFile.readToBuffer(4);

        niceRandomAccessFile.seek(0);

        if (recordLengthBuffer.order(ByteOrder.LITTLE_ENDIAN).getInt() == 84)
        {
            logger.log(Level.FINE, "Little endian encoding.");
            return ByteOrder.LITTLE_ENDIAN;
        }
        else if (recordLengthBuffer.order(ByteOrder.BIG_ENDIAN).getInt() == 84)
        {
            logger.log(Level.FINE, "Big endian encoding.");
            return ByteOrder.BIG_ENDIAN;
        }
        else
        {
            throw new IOException("Invalid record length of first record.");
        }
    }

    /**
     * Read next record.
     *
     * @param   buffer  is the target
     *
     * @return
     *
     * @throws  IOException
     */
    private ByteBuffer readNextRecord() throws IOException
    {
        // read record length (int)
        int recordLength = niceRandomAccessFile.readToBuffer(4).order(byteOrder).getInt();

        // make sure, data fits in one buffer
        // niceRandomAccessFile.fitsInBuffer( recordLength );

        ByteBuffer recordByteBuffer = niceRandomAccessFile.readToBuffer(recordLength).order(byteOrder);

        // read away second length indicator at the end of the record
        int verifyRecordLength = niceRandomAccessFile.readToBuffer(4).order(byteOrder).getInt();

        // check record length
        if (recordLength == verifyRecordLength)
        {
            logger.log(Level.FINEST, "Record (length=" + recordLength + ") found and read.");
            return recordByteBuffer;
        }
        else
        {
            throw new IOException("Invalid file data.");
        }
    }

    /**
     * Skip record.
     *
     * @throws  IOException
     */
    private void skipNextRecord() throws IOException
    {

        // read record length (int)
        int recordLength = niceRandomAccessFile.readToBuffer(4).order(byteOrder).getInt();

        niceRandomAccessFile.skipBytes(recordLength);

        int verifyRecordLength = niceRandomAccessFile.readToBuffer(4).order(byteOrder).getInt();

        // check record length
        if (recordLength == verifyRecordLength)
        {
            logger.log(Level.FINEST, "Record (length=" + recordLength + ") found and skipped.");
            return;
        }
        else
        {
            throw new IOException("Invalid file data.");
        }
    }

    /**
     * Reads the header information. This are the first 3 records: 1. Main header record 2. Description record 3. Number
     * of atoms record
     */
    private void readHeader() throws IOException
    {

        // unset all flags
        format__dcd_is_charmm = false;
        format__dcd_has_4dims = false;
        format__dcd_has_extra_block = false;

        ByteBuffer mainHeaderRecord = readNextRecord();

        // check for proper size of first record and CORD identifier
        if ((mainHeaderRecord.remaining() == 84)
                && (mainHeaderRecord.get(0) == 'C')
                && (mainHeaderRecord.get(1) == 'O')
                && (mainHeaderRecord.get(2) == 'R')
                && (mainHeaderRecord.get(3) == 'D'))
        { // this is cord header

            // CHARMm-genereate DCD files set the last integer in the
            // header, which is unused by X-PLOR, to its version number.
            // Checking if this is nonzero tells us this is a CHARMm file
            // and to look for other CHARMm flags.
            if (mainHeaderRecord.getInt(80) != 0)
            {
                format__dcd_is_charmm = true;

                logger.log(Level.FINE, "CHARMM format DCD file (also NAMD 2.1 and later).");

                if (mainHeaderRecord.getInt(44) != 0)
                {
                    format__dcd_has_extra_block = true;

                    logger.log(Level.FINE, "  Has extra block.");
                }

                if (mainHeaderRecord.getInt(48) != 0)
                {
                    format__dcd_has_4dims = true;

                    logger.log(Level.FINE, "  Has 4-dims.");
                }
            }
            else
            {
//                format__dcd_is_xplor = true; // must be an X-PLOR format DCD file
                logger.log(Level.FINE, "X-PLOR format DCD file (also NAMD 2.0 and earlier).");
            } // end if-else

            // Store the number of sets of coordinates (NSET)
            this.numberOfFrames = mainHeaderRecord.getInt(4);

            logger.log(Level.FINE, "  Number of sets of coordinates: " + this.numberOfFrames);

            // Store ISTART, the starting timestep
            this.startingTimestep = mainHeaderRecord.getInt(8);

            logger.log(Level.FINE, "  Starting timestep            : " + this.startingTimestep);

            // Store NSAVC, the number of timesteps between dcd saves
            this.numberOfTimestepsBetweenSaves = mainHeaderRecord.getInt(12);

            logger.log(Level.FINE, "  Timesteps between dcd saves  : " + this.numberOfTimestepsBetweenSaves);

            // Store NAMNF, the number of fixed atoms */
            this.numberOfFixedAtoms = mainHeaderRecord.getInt(36);

            logger.log(Level.FINE, "  Number of fixed atoms        : " + this.numberOfFixedAtoms);

            if (this.numberOfFixedAtoms > 0)
            {
                throw new RuntimeException("Sorry, read of fixed atom coordinates currently not implemented.");
            }

            // Timestep delta. Read in the timestep, DELTA
            // Note: DELTA is stored as a double with X-PLOR but as a float with CHARMm
            if (format__dcd_is_charmm)
            {
                float ftmp = mainHeaderRecord.getFloat(40); // is this safe on Alpha?
                this.delta = (double) ftmp;
            }
            else
            {
                this.delta = mainHeaderRecord.getDouble(40);
            }

            logger.log(Level.FINE, "  Timestep                     : " + this.delta);

            ByteBuffer descriptionHeaderRecord = readNextRecord();
            int lines = descriptionHeaderRecord.getInt();
            byte[] line = new byte[80];

            for (int i = 0; i < lines; i++)
            {
                descriptionHeaderRecord.get(line);
            }

            ByteBuffer noOfAtomsRecord = readNextRecord();
            this.numberOfAtoms = noOfAtomsRecord.getInt(0);

            logger.log(Level.FINE, "No of atoms : " + this.numberOfAtoms);

            // get position framestart
            this.frameAreaStartingPosition = niceRandomAccessFile.getFilePointer();

            // calculate framesize
            this.framesizeRegular = 0;

            long dims = 3;

            if (format__dcd_has_4dims)
            {
                dims = 4;
            }

            long fullExtraframesize = 0;

            if (format__dcd_has_extra_block)
            {
                fullExtraframesize = 4 + 48 + 4; // length indicator + extra block (unit cell) + length indicator
            }

            // Size of a regular frame (every frame, if no fixed atoms.
            // Or every frame expect the first when fixed coordinates are used.
            long cordRecordSize = (numberOfAtoms - numberOfFixedAtoms) * 4;
            long fullRecordSize = 4 + cordRecordSize + 4;

            // First frame size, if fixed coordinates are used
            long cordRecordSizeWithFixed = numberOfAtoms * 4;
            long fullRecordSizeWithFixed = 4 + cordRecordSizeWithFixed + 4;

            this.framesizeWithFixed = ((fullRecordSizeWithFixed * dims) + fullExtraframesize);
            this.framesizeRegular = ((fullRecordSize * dims) + fullExtraframesize);
            this.nextFrameIndex = 0;

            // integrity check by file length
            long endpos = frameAreaStartingPosition + (numberOfFrames * framesizeRegular);

            if (niceRandomAccessFile.length() == endpos)
            {
                logger.log(Level.FINE, "Checking file size vs. calculated number of frames .... OK.");
            }
            else
            {
                throw new IOException("Real file size does not match the calculated file size. "
                        + "Maybe an interrupt has occured during creation of trajectory file." + "Real: "
                        + niceRandomAccessFile.length() + "  Expected: " + endpos);
            }

        }
        else
        { // not the cord header ..... => error
            throw new IOException("Invalid file format. Header corrupted or not found.");
        } // end if-else
    }

    /**
     * Read a frame at current position (advances in file).
     *
     * @param   factory  to construct vector
     *
     * @return  vector with frame data, that is
     *
     *          <pre>x_0, y_0, z_0, x_1, y_1, z_1, ....</pre>
     *
     * @throws  IOException
     */
    private IDoubleArray readNextFrame(IDoubleArray target) throws IOException
    {
    	// ensure frame fits into buffer.
        niceRandomAccessFile.fitsInBuffer((int) framesizeRegular);

        if (format__dcd_has_extra_block)
        {
            // read away extra block, which contains unit cell per frame
            skipNextRecord();
        }

        int n = this.numberOfAtoms - this.numberOfFixedAtoms;

        FloatBuffer xBuffer = readNextRecord().asFloatBuffer();
        FloatBuffer yBuffer = readNextRecord().asFloatBuffer();
        FloatBuffer zBuffer = readNextRecord().asFloatBuffer();

        int r = 0;
        float x,y,z;
        for (int i = 0; i < n; i++)
        {
            x = xBuffer.get();
            y = yBuffer.get();
            z = zBuffer.get();
            if (selected.get(i))
            {
                target.set(r, 0, x);
                target.set(r, 1, y);
                target.set(r, 2, z);
                r++;
            }
        }

        // increment last position
        nextFrameIndex++;

        if (format__dcd_has_4dims)
        {
            skipNextRecord();
        }

        return target;
    }

    /**
     * Get position in file for frame with index frameIndex.
     *
     * @param   frameIndex
     *
     * @return  position.
     */
    private long getPositionOfFrame(int frameIndex)
    {

        if (frameIndex > 0)
        {
            return frameAreaStartingPosition + framesizeWithFixed + ((frameIndex - 1) * framesizeRegular);
        }
        else if (frameIndex == 0)
        {
            return frameAreaStartingPosition;
        }
        else
        {
            throw new IndexOutOfBoundsException("Index of frame is negative.");
        }
    }

    @Override
    public int size()
    {
        return this.numberOfFrames;
    }

    @Override
    public int dimension()
    {
        return this.numberOfAtoms * 3;
    }

    public double getTimeOfFrame(int frameIndex)
    {
        int currentStep = startingTimestep + numberOfTimestepsBetweenSaves * frameIndex;
        return ((double) currentStep) * delta;
    }

    public IDoubleArray get(int frameIndex, IDoubleArray target)
    {
        try
        {
            // position if necessary
            if ((frameIndex < 0) || (this.numberOfFrames <= frameIndex))
            {
                throw new IndexOutOfBoundsException("Invalid frame index " + frameIndex + ".");
            }

            if (frameIndex != this.nextFrameIndex)
            {
                this.nextFrameIndex = frameIndex;
                niceRandomAccessFile.seek(getPositionOfFrame(frameIndex));

                logger.log(Level.FINEST, "Repositioning for frame " + frameIndex + " to "
                            + niceRandomAccessFile.getFilePointer());
            }
            else
            {
                logger.log(Level.FINEST, "File position is okay. Frame " + frameIndex + " is at "
                            + niceRandomAccessFile.getFilePointer());
            }

            return readNextFrame(target);
        } 
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException("Problems reading DCD, caught I/O exception."
                    + " Message: " + ex.getMessage());
        } // end try-catch
    }

    @Override
    public void close() throws IOException
    {
        niceRandomAccessFile.close();
    }

    @Override
    public void open() throws IOException
    {
        niceRandomAccessFile.open();
    }

    @Override
    public double getTime(int frameIndex)
    {
        return delta*frameIndex;
    }

    @Override
    public void select(int[] _selection)
    {
        if (_selection == null)
            _selection = intArrays.range(numberOfAtoms);
        this.selection = _selection;
        
        // clear current selection
        selected.clear();
        // adopt to given selection
        for (int i=0; i<_selection.length; i++)
            selected.set(_selection[i]);
    }
    
    @Override
    public int[] getSelection()
    {
        return this.selection;
    }

    
    @Override
    public IDoubleArray get(int frameIndex)
    {
        return get(frameIndex, Doubles.create.array(selection.length,3));
    }

    @Override
    public IDoubleArray getView(int index)
    {
        return get(index);
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DataReaderIterator(this);
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
        for (int i=0; i<this.size(); i++)
            res.add(get(i));
        return res;
    }

    public String getFileName()
    {
        return filename;
    }

    @Override
    public long memorySize()
    {
        return this.size()*this.dimension()*8;
    }
}
