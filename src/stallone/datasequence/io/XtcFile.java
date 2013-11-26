package stallone.datasequence.io;

import java.io.*;
import java.nio.*;
import java.util.Arrays;

import stallone.api.doubles.IDoubleArray;
import stallone.api.io.IReleasableFile;
import stallone.doubles.PrimitiveDoubleTable;
import stallone.doubles.fastutils.LongArrayList;
import stallone.io.CachedRandomAccessFile;

/**
 * This class reads data from trajectories which are randomAccessFile gromacs
 * xtc format (syntax based on modification of TrrReader by D. Skanda and J.H.
 * Prinz)
 *
 * @author M. Emal Alekozai, Dominik Skanda, Jan-Hendrik Prinz, Martin Senne
 *
 * <pre>
 * XTC format:
 * ===========
 * The xtc format is a portable format for trajectories. It uses the xdr routines for writing and reading data
 * which was created for the Unix NFS system. The trajectories are written using a reduced precision algorithm
 * which works randomAccessFile the following way: the coordinates (randomAccessFile nm) are multiplied by a scale factor, typically 1000,
 * so that you have coordinates randomAccessFile pm. These are rounded to integer values. Then several other tricks are
 * performed, for instance making use of the fact that atoms close randomAccessFile sequence are usually close randomAccessFile space too
 * (e.g. a water molecule). To this end, the xdr library is extended with a special routine to write 3-D float
 * coordinates. This routine was written by Frans van Hoesel as part of an Europort project.
 *
 * (Source:  http://www.gromacs.org/documentation/reference/online/xtc.html )
 *
 *
 * XTC file specifications:
 * ========================
 * A xtc trajectory can have an arbitrary number of frames. Each frames consists of a "frame header", a
 * "coordinates header" and the "coordinates". If the number of atoms randomAccessFile the trajectory is <=9 the atoms
 * coordinates are not compressed (case a), randomAccessFile the case of >9 atoms the atom coordinates are compressed (case b).
 * The "frame header" is build up similar randomAccessFile both cases. Only the "coordinates header" and the "coordinates" differ.
 * The floating point number are encoded with the IEEE 754 (single-precision) standard.
 *
 * "Frame header" (case a and b):
 * ------------------------------
 * - int, 4byte: magic number mn (gromacs file versions 1995_10=0x000007cb)
 * - int, 4byte: amount atoms n randomAccessFile frame (starts with 1)
 * - int, 4byte: frame number randomAccessFile trajectory nf (starts with 0)
 * - float, 4byte: simulation time t randomAccessFile trajectory
 * - float, 4byte: a_x (3 basisvectors a,b and c of simulation box (tricline PBC))
 * - float, 4byte: a_y
 * - float, 4byte: a_z
 * - float, 4byte: b_x
 * - float, 4byte: b_y
 * - float, 4byte: b_z
 * - float, 4byte: c_x
 * - float, 4byte: c_y
 * - float, 4byte: c_z
 * => Size: 52byte
 *
 * "Coordinates header" and "coordinates" (case a: coordinates uncompressed):
 * ------------------------------------------------------------------------
 * - int, 4byte: amount atoms n randomAccessFile frame (starts with 1) => redundant, bad file design?!
 * - float, 4byte: r1_x (uncompressed x coordinates of atom 1)
 * - float, 4byte: r1_y (uncompressed y coordinates of atom 1)
 * - float, 4byte: r1_z (uncompressed z coordinates of atom 1)
 * ...
 * - float, 4byte: rn_x (uncompressed x coordinates of atom n)
 * - float, 4byte: rn_y (uncompressed y coordinates of atom n)
 * - float, 4byte: rn_z (uncompressed z coordinates of atom n)
 * => Size ("coordinates header"): 4byte
 * => Size ("coordinates"): 12byte*n
 * => Size ("coordinates header" and "coordinates"): 4byte+12byte*n
 *
 * "Coordinates header" and "coordinates" (case b: coordinates compressed):
 * ----------------------------------------------------------------------
 * - int, 4byte: amount atoms n randomAccessFile frame (starts with 1) => redundant, bad file design?!
 * - float, 4byte: precision p, default 1000 (scale factor to reduce coordinate precision from float to int)
 * - int, 4byte: minint_x=floatToInt(min(ri_x)*p) (scaled minimal x coordinate of atom randomAccessFile current frame)
 * - int, 4byte: minint_y=floatToInt(min(ri_y)*p) (scaled minimal y coordinate of atom randomAccessFile current frame)
 * - int, 4byte: minint_z=floatToInt(min(ri_z)*p) (scaled minimal z coordinate of atom randomAccessFile current frame)
 * - int, 4byte: maxint_x=floatToInt(max(ri_x)*p) (scaled maximal x coordinate of atom randomAccessFile current frame)
 * - int, 4byte: maxint_y=floatToInt(max(ri_y)*p) (scaled maximal y coordinate of atom randomAccessFile current frame)
 * - int, 4byte: maxint_z=floatToInt(max(ri_z)*p) (scaled maximal z coordinate of atom randomAccessFile current frame)
 * - int, 4byte: number of bits nb used to code atom coordinates
 * - int, 4byte: length nc of compressed coordinates stream randomAccessFile bytes
 * - bitstream, approx. nc bytes: bitstream of compressed coordinates r1_x,r1_y,r1_z,...,rn_x,rn_y,rn_z
 * => Size ("coordinates header"): 40byte
 * => Size ("coordinates"): roundUpToNearestMultipleOfFourBytes(nc)byte
 * => Size ("coordinates header" and "coordinates"): (40+roundUpToNearestMultipleOfFourBytes(nc))byte
 * Due to the compression, the size of each frame is variable.
 *
 * => Total size frame (uncompressed): (52+4+12*n)byte=(56+12*n)byte
 * => Total size frame (compressed): (52+40+roundUpToNearestMultipleOfFourBytes(nc))byte=(92+roundUpToNearestMultipleOfFourBytes(nc))byte
 *
 *
 * Compressing algorithm:
 * ======================
 * The compressed coordinates are an extremely packed format. Even gzip cannot compress it further.
 * The algorithm is partly explained randomAccessFile:
 * - http://hpcv100.rc.rug.nl/xdrf.html     and
 * - http://hpcv100.rc.rug.nl/xdrfman.html
 *
 * The compressing algorithm consist roughly of 5 steps/ tricks.
 *
 * Step/ trick 1:
 * -----------
 * Reduce the precision of the numbers e.g. 1.325 (float 4byte) get to 1.325*1000=1325 (int 4 byte)
 *
 * Step/ trick 2:
 * -----------
 * Remove the offset of the numbers e.g. atom coordinates randomAccessFile frame are all randomAccessFile the range between 1970 and 1999.
 * (11 bits needed for coding), range is converted to 0 ... 29 (5 bits ares needed for coding).
 *
 * Step/ trick 3:
 * -----------
 * Each atom coordinate consist of 3 component (x,y,z). Combine all 3 numbers to a single number by changing the coding base.
 * In the decimal system the coding base 10-10-...-10 is used. E.g.:
 * - 456 (minimum number range 0-0-0, maximum number range 9-9-9)  randomAccessFile coding base 10-10-10: (4*10+2)*10+3*10^0=456
 * => 9 bits needed for coding
 * - 456 (minimum number range 0-0-0, maximum number range 4-7-6)  randomAccessFile coding base 5-8-7: (4*5+2)*8+3*7^0=179
 * =>  8 bits needed for coding
 *
 * Step/ trick 4:
 * -----------
 * To encode the numbers from step 3 don't use full amount of bits.
 *
 * Step/ trick 5:
 * -----------
 * The main trick of the compressing algortihm is to shrink the value of the numbers.
 * Interchange first with second atom for even better compression of water molecules.
 *
 *
 * WORKFLOW:
 * =========
 * To read a single coordinate the following  minimal amount of steps have to be done:
 *
 * 1. Constructor "XTCTrajectory":
 * Read randomAccessFile entire trajectory and find the start of each frame randomAccessFile array "framePos"
 *
 * 2. Method "readFrame":
 * Read for given frame the "frame header", "coordinate header" and "coordinates" from trajectory.
 * If the coordinates are compressed, don't uncompress them !
 *
 * 3. Method "uncompressFrameCoordinates":
 * Check if function "readFrame" was already called. If the coordinates are compressed, uncompress them !
 *
 *
 * ERROR HANDLING IN INPUT TRAJECTORY:
 * ===================================
 * To aviod aftereffect errors randomAccessFile the analysis the code does not try to correct errors randomAccessFile the trajectory!
 * In general if an error randomAccessFile the trajectory occurs the frame is marked as broken and a warning is issued.
 *
 * Which errors randomAccessFile the input trajectory are recognized:
 * - total frame size is not a multiple of 4 bytes
 * - frame size is not a multiple of 4 bytes
 * - size of frame "coordinates" section is not a multiple of 4 bytes
 *
 *
 *
 * MISC:
 * =====
 * - For the implementation it is assumed, that the "magic nr." and the "amount of atoms randomAccessFile frame" are constant over
 * the entire trajectory.
 *
 * - Coordinates are measured randomAccessFile nm and the time randomAccessFile ps.
 *
 * - "3dfcoord" algorithm (compress coordinates) documentation (Frans van Hoesel, hoesel@chem.rug.nl):
 * http://hpcv100.rc.rug.nl/xdrf.html
 * http://hpcv100.rc.rug.nl/xdrfman.html
 * http://hpcv100.rc.rug.nl/binaries/xdrf.tar.gz
 *
 * - VMD gromacs xtc file format reader:
 * http://www.ks.uiuc.edu/Research/vmd/plugins/doxygen/Gromacs_8h-source.html
 * http://www.ks.uiuc.edu/Research/vmd/plugins/doxygen/Gromacs_8h.html
 *
 * </pre>
 * @version 0.2
 */
public class XtcFile implements IReleasableFile
{
    // general stuff =================================================================

    protected String filename;
    /**
     * The file we are reading from.
     */
    protected CachedRandomAccessFile randomAccessFile;

    /**
     * By this java class supported gromacs versions (="magic number"),
     * 1995_10=0x000007cb.
     */
    protected final int[] supportedMagicNrs =
    {
        1995
    };
    
    // I don't know in detail what this array does, but it has a interesting structure
    // First 9 elements are 0 => if system has =< 9 atoms  coordinates are not compressed ?!
    // Every third element is 2^i , e.g. 2^3=8 (10 element), 2^4=16 (13 element), 2^5=32 (16 element) ...
    // The elements randomAccessFile the array are correlated with the amount of bits used for encoding the
    // atom coordinates
    protected final static int[] xtc_magicints =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        8, 10, 12, 16, 20, 25, 32, 40, 50, 64,
        80, 101, 128, 161, 203, 256, 322, 406, 512, 645,
        812, 1024, 1290, 1625, 2048, 2580, 3250, 4096, 5060, 6501,
        8192, 10321, 13003, 16384, 20642, 26007, 32768, 41285, 52015, 65536,
        82570, 104031, 131072, 165140, 208063, 262144, 330280, 416127, 524287, 660561,
        832255, 1048576, 1321122, 1664510, 2097152, 2642245, 3329021, 4194304, 5284491, 6658042,
        8388607, 10568983, 13316085, 16777216
    };
    
    /**
     * Number of frames (from 1 to n), frameIndex goes from 0 to n-1.
     */
    protected int numOfFrames;
    /**
     * Amount of bytes randomAccessFile file.
     */
    protected long fileSize;
    /**
     * Stores starting positions of each frame randomAccessFile bytes.
     */
    protected long[] framePos;
    /**
     * Stores status of all frames (false:= frame ok, true:= frame broken).
     */
    protected boolean[] frameBroken;
    /**
     * Store the number of the current frame (from 0 to numOfFrames-1), to avoid
     * reading same frame data.
     */
    protected int nrOfCurrentFrameHeader;
    /**
     * Store the nr. of the current frame (from 0 to numOfFrames-1), to avoid
     * reading and decoding same frame data
     */
    protected int nrOfCurrentFrameCoordinates;
    // frame header data =================================================================
    /**
     * Size randomAccessFile bytes of current frame => size may change over
     * trajectory frames If frameSize is defined as int the trajectory can have
     * randomAccessFile the worst case max. 2.1475e+09 atoms. If the simulation
     * contains more atoms, it is perhaps safer to switch the variable typ to
     * long insteed of int. The largest simulation till 2007 had 2.64e+3 atoms
     * (K.Y. Sanbonmatsu and C.-S. Tung. Journal of Structural Biology,
     * 157:470-480, 2007).
     *
     * <p>Calculation: ------------ Int range: -2^31...2^31-1 Worst case
     * (uncompressed) for every atom 3*4byte=12byte are needed to store the
     * coordinates The "frame header" has 52byte, the "coordinate header"
     * (uncompressed case) has 40byte</p>
     *
     * <p>(2^31-1-52-40)/(3*4)=2.1475e+09 atoms (worst case)</p>
     */
    protected int frameSize;
    /**
     * Size randomAccessFile bytes of current frame header => size (52byte) is
     * fixed over trajectory frames.
     */
    protected final int frameHeaderSize = 52;
    /**
     * Size randomAccessFile bytes of "coordiantes header".
     */
    protected int coordinatesHeaderSize;
    /**
     * Size randomAccessFile bytes of coordinates block ("coordinates header"
     * and "coordinates") randomAccessFile current frame of trajectory => size
     * may change over trajectory frames.
     */
    protected int coordinatesHeaderAndCoordinatesSize;
    /**
     * "Magic number" (=Gromacs version= 1995_10=0x000007cb ) read from input
     * trajectory.
     */
    protected int magicNrFileVersion;
    /**
     * Amount of atomes randomAccessFile current frame from "frame header"
     * (should not change over frames).
     */
    protected int nrAtoms;
    /**
     * Simulation frame number randomAccessFile trajectory (not real frame
     * number!), e.g. 0,100,200,....
     */
    protected int step;
    /**
     * Floating point representation of simulation time randomAccessFile
     * trajectory.
     */
    protected float t;
    /**
     * 3x3 matrix , computational box which is stored as a set of three basis
     * vectors, to allow for triclinic PBC. For a rectangular box the box edges
     * are stored on the diagonal of the matrix.
     */
    protected float[] box;
    // frame coordinates data =================================================================
    /**
     * Amount atoms randomAccessFile current frame from "coordinates header" =>
     * content redundant => ignore it!
     */
    protected int nrAtomsCoordinatesHeader;
    /**
     * Scale factor to reduce coordinate precision from float to int (default
     * 1000).
     */
    protected float precision;
    /**
     * Scaled minimal x,y,z coordinate of atom randomAccessFile current frame.
     */
    protected int[] minInt;
    /**
     * Scaled maximal x,y,z coordinate of atom randomAccessFile current frame.
     */
    protected int[] maxInt;
    /**
     * Number of bits nb used to code atom coordinates.
     */
    protected int amountBitsForCompressedCoordinates;
    /**
     * Length of compressed coordinates stream randomAccessFile bytes.
     */
    protected int compressedCoordinatesLengthInByte;
    /**
     * Datastructure to store compressed atom coordinates.
     */
    protected int[] coordinatesCompressed;
    /**
     * Datastructure to store uncompressed atom coordinates.
     */
    protected IDoubleArray coordinatesUncompressed;
    
    protected int[] bytes = new int[32];
    
    /**
     * will be used while frame-wise reading this file
     */
    protected ByteBuffer bb = null;
    
    /**
     * indicates, that currently set files header has been scanned.
     */
    protected boolean initialized = false;

    /**
     * Constructor, open and read input file trajectory.
     *
     * @param filename trajectory (xtc) filename and path
     */
    public XtcFile()
    {
    }

    public XtcFile(String _filename) throws FileNotFoundException, IOException
    {
        this.filename = _filename;
        this.init();
    }

    public void setSource(String _filename)
    {
        this.filename = _filename;
    }

    public void scan()
            throws IOException
    {
        this.init();
    }

    /**
     * Constructor, open and read input file trajectory.
     *
     * @param filename trajectory (xtc) filename and path
     */
    private void init() throws FileNotFoundException, IOException
    {
        // read file header only once.
        if (this.initialized)
            return;
        
        // read input file trajectory
        this.randomAccessFile = new CachedRandomAccessFile(filename);
        // for non cached access
        // this.randomAccessFile2 = new RandomAccessFile( filename, "r" );

        this.fileSize = randomAccessFile.length(); // get amount of bytes randomAccessFile file

        // read main properties from the first frame of the trajectory
        this.magicNrFileVersion = this.randomAccessFile.readInt(); // read  "magic number" (=Gromacs version)
        Arrays.sort(this.supportedMagicNrs); // Ensure array sorted

        if (Arrays.binarySearch(this.supportedMagicNrs, this.magicNrFileVersion) == -1)
        {
            System.out.println("Warning: Wrong magic number, this Gromacs version is not supported!");
        }

        this.nrAtoms = this.randomAccessFile.readInt(); // read number of atoms randomAccessFile current frame (should
        // not change over frames)
        this.step = this.randomAccessFile.readInt(); // simulation frame number randomAccessFile trajectory (not real
        // number!), e.g. 0, 100, 200,....
        this.t = this.randomAccessFile.readFloat(); // floating point representation of simulation time randomAccessFile
        // trajectory
        this.box = new float[]
        {
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat(),
            this.randomAccessFile.readFloat()
        };

        // reset inputfile pointer to start position
        this.randomAccessFile.seek(0);

        LongArrayList tempFramePositions = new LongArrayList(50000); // tmp vector to store the starting position of the
        // frames randomAccessFile input file (length of
        // vector can be increased, the length of a array
        // not !)
        int framesDetected = 0;
        long pos = 0;

        int magicRead; // frameHeader
        int noOfAtomsRead; // frameHeader

        int noOfAtomsRead2; // coordinates header
        int sizeOfCoordinates; // coordinates header

        /*
         * double realExpectedTime = 0.0d; double expectedTime = 0.0d; double
         * eps = 0.1d;
         */

        do
        {
            pos = randomAccessFile.getFilePointer();
            tempFramePositions.add(pos);

            // System.out.println("Frame " + framesDetected + " at " + pos );
            framesDetected++;

            magicRead = randomAccessFile.readInt(); // 4

            if (magicRead == this.magicNrFileVersion)
            {
                noOfAtomsRead = randomAccessFile.readInt(); // 4

                // frameNoRead = randomAccessFile.readInt(); // 4
                // simulationTime = randomAccessFile.readFloat();// 4 is simulation time
                // 36 is 3x3 cell axis a 4bytes each
                randomAccessFile.skipBytes(4 + 4 + 36);
                // 52 randomAccessFile total

                /*
                 * //200 - 0.4 System.out.println( "off: " +
                 * randomAccessFile.getFilePointer() + " frame# " + frameNoRead
                 * + " sim t: " + simulationTime + " expected t after reset: " +
                 * expectedTime + " real t: " + realExpectedTime); if (
                 * Math.abs( (double)simulationTime - expectedTime ) > eps ) {
                 * System.out.println( "Missing: off: " +
                 * randomAccessFile.getFilePointer() + " frame# " + frameNoRead
                 * + " sim t: " + simulationTime + " expected t after reset: " +
                 * expectedTime + " real t: " + realExpectedTime); expectedTime
                 * = simulationTime; } expectedTime += 0.2d; realExpectedTime +=
                 * 0.2d;
                 *
                 */


                // now entering coordinates header
                noOfAtomsRead2 = randomAccessFile.readInt();

                if (noOfAtomsRead == noOfAtomsRead2)
                {

                    if (noOfAtomsRead > 9)
                    { // compressed format (case b)

                        // 4 is precision
                        // 24 = (4 * 2 * 3 ) is min and max of x, y and z
                        // 4 is number of bits used for compression
                        randomAccessFile.skipBytes(4 + 24 + 4);

                        sizeOfCoordinates = randomAccessFile.readInt();

                        int mod4 = sizeOfCoordinates % 4;

                        if (mod4 > 0)
                        {
                            sizeOfCoordinates += (4 - mod4);
                        }

                        randomAccessFile.skipBytes(sizeOfCoordinates); // skip coordinates
                    }
                    else
                    { // uncompressed format (case a)

                        // x, y and z per atom a 4 bytes each
                        randomAccessFile.skipBytes(noOfAtomsRead * 3 * 4);
                    }
                }
                else
                {
                    throw new RuntimeException("Problem with atom sizes");
                } // end if-else
            }
            else
            {
                throw new RuntimeException("No magic bytes found. Error in trajectory.");
            } // end if-else
        }
        while (randomAccessFile.getFilePointer() < randomAccessFile.length());

        this.numOfFrames = framesDetected;

        // copy starting postion of frames from vector into an array and check their size
        int numberOfFrames = tempFramePositions.size();
        this.framePos = new long[numberOfFrames];
        this.frameBroken = new boolean[numberOfFrames];

        long tmpPosition_old = 0; // tmp variable to calculate the size of a frame

        for (int i = 0; i < numberOfFrames; i++)
        {
            long tmpPosition = tempFramePositions.getLong(i);

            if (((tmpPosition - tmpPosition_old) % 4) != 0)
            { // check if the frame size is a multiple of 4 bytes
                this.frameBroken[i] = true; // mark frame as broken
                System.err.println("WARNING: Frame " + i
                        + " (numbering goes from 0 to n-1) has a wrong size. It's not a multiple of 4 bytes. This indicates that the frame may be broken. The frame is marked as broken.");
            }

            tmpPosition_old = tmpPosition;
            this.framePos[i] = tmpPosition;
        }

        if (((this.fileSize - tmpPosition_old) % 4) != 0)
        { // check if the frame size is a multiple of 4 bytes

            int frameNr = numberOfFrames - 1;
            this.frameBroken[frameNr] = true; // mark frame as broken
            System.err.println("WARNING: Last frame " + frameNr
                    + " (numbering goes from 0 to n-1) which was read from the trajectory has a wrong size. It's not a multiple of 4 bytes. This indicates that this frame may be broken. The frame is marked as broken.");
        }

        tempFramePositions = null; // free memory

        // fix size of atom coordinates, to improve optimization
        this.coordinatesUncompressed = new PrimitiveDoubleTable(new double[this.nrAtoms][3]);

        // clean up and reset stuff
        this.randomAccessFile.seek(0); // set inputfile pointer to start position
        this.nrOfCurrentFrameHeader = -1; // no proper frame header date is read randomAccessFile at the moment
        this.nrOfCurrentFrameCoordinates = -1; // no proper frame coordinates date is read randomAccessFile at the
        // moment
        
        
        // setting header initialized.
        this.initialized = true;
    }

    /**
     * Returns the number of degrees of freedom (nAtoms*3).
     */
    public int nDOF()
    {
        return this.nrAtoms * 3;
    }

    /**
     * Returns the number of atoms.
     */
    public int nAtoms()
    {
        return this.nrAtoms;
    }

    /**
     * Amount of frames randomAccessFile trajectory.
     *
     * @return Amount of frames randomAccessFile trajectory (from 1 to n), 0 :=
     * trajectory contains no frames
     */
    public int nFrames()
    { // return amount of frames from 1 to n, 0 := trajectory contains no frames

        // 2 solutions are possible ( variable numOfFrames= this.framePos.length), the TrrReader uses the second
        // solution
        if (this.framePos == null)
        {
            return 0;
        } // trajectory contains no frames

        return this.numOfFrames;
    }

    /**
     * Query if atoms coordinates are stored randomAccessFile the frame.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return query result (false:= coordinates don't exist, true:= coordinates
     * exist)
     */
    public boolean frameHasPosition(int frameIndex) throws IOException
    {

        if (!readFrame(frameIndex))
        {
            return false;
        } // frame doesn't exists => atoms randomAccessFile frame have no coordinates
        else if (this.coordinatesHeaderAndCoordinatesSize > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Get size of frame randomAccessFile bytes.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return frame size randomAccessFile bytes
     */
    public int getFrameSize(int frameIndex)
    {

        if ((this.numOfFrames == 0) || (frameIndex >= this.numOfFrames)) // trajectory contains no frames or query for a
        // frameIndex which is larger then the total
        // amount of frames randomAccessFile trajectory
        {
            return 0;
        }
        else if (frameIndex < (this.numOfFrames - 1)) // query for frame 0 to n-1
        {
            return (int) (this.framePos[frameIndex + 1] - this.framePos[frameIndex]);
        }
        else // query for last frame
        {
            return (int) (this.fileSize - this.framePos[frameIndex]);
        }
    }

    public int getFrameSize(int frameIndex, boolean show)
    {
        System.out.println("From: " + this.framePos[frameIndex]);
        System.out.println("To: " + this.framePos[frameIndex + 1]);

        return getFrameSize(frameIndex);
    }

    /**
     * Check if frame exists randomAccessFile trajectory, read "frame header",
     * "coordinate header" and coordinates (if compressed don't decode them) of
     * current frame.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return read result (true:=frame exists, false:=frame does not exist
     */
    protected boolean readFrame(int frameIndex) throws IOException
    {

        // Check if frame exists randomAccessFile trajectory
        if (frameIndex >= this.numOfFrames)
        { // frame does not exist
            return false;
        }

        if (this.frameBroken[frameIndex])
        { // frame is marked as broken
            System.err.println("WARNING: You are trying to read from a broken frame " + frameIndex
                    + " (numbering goes from 0 to n-1). This is not allowed! Please fix first the error in your trajectory.");

            return false;
        }
        else
        { // frame exist  and is not marked as brocken => read header information from current frame

            if (this.nrOfCurrentFrameHeader != frameIndex)
            { // only read data of new (not current) frame
                this.nrOfCurrentFrameHeader = frameIndex; // store the nr. of the current frame (from 0 to
                // numOfFrames-1), to avoid reading and decoding same frame
                // data
                this.frameSize = getFrameSize(frameIndex); // frame size randomAccessFile bytes
                this.coordinatesHeaderAndCoordinatesSize = this.frameSize - this.frameHeaderSize; // size
                // randomAccessFile
                // byts of coordinates
                // + coordiantesHeader
                this.randomAccessFile.seek(this.framePos[frameIndex]); // set file pointer to corresponiding postion
                // randomAccessFile input file

                // no checking for end of file etc has to be done, this was already done randomAccessFile the
                // constructor
                
                // create a buffer array wich has the size randomAccessFile bytes
                // and ensure current frameSize fits in capacity of byte buffer.
                if(bb == null || bb.capacity() < this.frameSize) {
                    // in case of underrun, allocate 1.5 times more memory than for last frame
                	// to avoid further reallocations.
                	int newSize = (int)Math.floor(this.frameSize*1.5);
                    bb = ByteBuffer.wrap(new byte[newSize]);
                }
                
                // reset input positions and read current frameSize bytes.
                bb.clear();
                this.randomAccessFile.readFully(bb.array(), this.frameSize);
                
                try
                {
                    this.magicNrFileVersion = bb.getInt(); // read  "magic number" (=Gromacs version)
                    this.nrAtoms = bb.getInt(); // read  amount atomes randomAccessFile current frame from frame header
                    this.step = bb.getInt(); // simulation frame number randomAccessFile trajectory (not real number!),
                    // e.g. 0,100,200,....
                    this.t = bb.getFloat(); // floating point representation of simulation time randomAccessFile
                    // trajectory
                    this.box = new float[]
                    {
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat(),
                        bb.getFloat()
                    };
                    this.nrAtomsCoordinatesHeader = bb.getInt(); // in the compressed atom coordinates
                    // block the number of atoms is allso added =>
                    // redundant => read it and throw it away !

                    if (this.nrAtoms > 9)
                    { // coordinates of atoms are compressed
                        this.coordinatesHeaderSize = 40; // size randomAccessFile byts of "coordiantes header"
                        this.precision = bb.getFloat(); // read precision of compressed atom coordinates
                        this.minInt = new int[]
                        {
                            bb.getInt(), bb.getInt(), bb.getInt()
                        };
                        this.maxInt = new int[]
                        {
                            bb.getInt(), bb.getInt(), bb.getInt()
                        };
                        this.amountBitsForCompressedCoordinates = bb.getInt();
                        this.compressedCoordinatesLengthInByte = bb.getInt();

                        int compressedCoordinatesSizeInFourBytesMeasure = (int) (((this.coordinatesHeaderAndCoordinatesSize) / 4) - 10); // size randomAccessFile bytes of
                        // compressed coordinates (without
                        // coordinates header
                        // 40byte=10*4byte)
                        this.coordinatesCompressed = new int[compressedCoordinatesSizeInFourBytesMeasure + 3];

                        if ((this.compressedCoordinatesLengthInByte
                                + ((4 - (this.compressedCoordinatesLengthInByte % 4)) % 4))
                                != (this.coordinatesHeaderAndCoordinatesSize - this.coordinatesHeaderSize))
                        { // check if frame "coordinates" is a multiple of 4
                            this.frameBroken[frameIndex] = true; // mark frame as broken
                            System.err.println("WARNING: Coordinates section of frame " + frameIndex
                                    + " (numbering goes from 0 to n-1) has a wrong size. It's not a multiple of 4 bytes. This indicates that this frames may be broken. Frame marked as broken.");
                        }

                        for (int ii = 3; ii < (compressedCoordinatesSizeInFourBytesMeasure + 3); ii++)
                        {
                            this.coordinatesCompressed[ii] = bb.getInt();
                        }
                    }
                    else
                    { // coordinates of atoms are not compressed
                        this.coordinatesHeaderSize = 4; // size randomAccessFile byts of "coordiantes header"
                        this.precision = 0; // coordinates are not compressed => set precision to the arbitary value 0
                        this.minInt = new int[]
                        {
                            0, 0, 0
                        };
                        this.maxInt = new int[]
                        {
                            0, 0, 0
                        };
                        this.amountBitsForCompressedCoordinates = 0;
                        this.compressedCoordinatesLengthInByte = 0;

                        for (int iiAtom = 0; iiAtom < this.nrAtoms; iiAtom++)
                        {
                            this.coordinatesUncompressed.set(iiAtom, 0, bb.getFloat()); // read x coordinate of atom
                            this.coordinatesUncompressed.set(iiAtom, 1, bb.getFloat()); // read y coordinate of atom
                            this.coordinatesUncompressed.set(iiAtom, 2, bb.getFloat()); // read z coordinate of atom
                        }
                    } // end if-else

                } catch (BufferUnderflowException e)
                {
                    e.printStackTrace();

                    System.out.println("Frame current " + frameIndex + " from " + framePos[frameIndex] + " to "
                            + framePos[frameIndex + 1]);
                    byte[] buf = bb.array();
                    for (int i = 0; i < buf.length; i++)
                    {

                        if ((i % 16) == 0)
                        {
                            System.out.println("");
                        }

                        byte b = buf[i];
                        System.out.print(byteToString(b) + " ");
                    }

                    System.out.println("");

                    System.out.println("Frame current - 1 " + (frameIndex - 1) + " from " + framePos[frameIndex - 1]
                            + " to " + framePos[frameIndex - 1 + 1]);
                    System.out.println("Frame current + 1 " + (frameIndex + 1) + " from " + framePos[frameIndex + 1]
                            + " to " + framePos[frameIndex + 1 + 1]);
                    throw new RuntimeException("Unrecoverable trajectory error.");
                } // end try-catch
                // System.out.println("nrAtomsCoordinatesHeader=" + nrAtomsCoordinatesHeader + " precision="+ precision
                // +" minInt=<" + minInt[0] + "," + minInt[1] + "," + minInt[2]+">
                // maxInt=<"+maxInt[0]+","+maxInt[1]+","+maxInt[2] + "> amountBitsForCompressedCoordinates="+
                // amountBitsForCompressedCoordinates + " compressedCoordinatesLengthInByte=" +
                // compressedCoordinatesLengthInByte +"\n");
            } // end if

            return true;
        } // end if-else
    }

    public static String byteToString(byte in)
    {
        byte ch = 0x00;
        String out = new String("");
        final String[] pseudo =
        {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"
        };

        ch = (byte) (in & 0xF0); // Strip off high nibble
        ch = (byte) (ch >>> 4); // shift the bits down
        ch = (byte) (ch & 0x0F); // must do this is high order bit is on!
        out += pseudo[(int) ch]; // convert the nibble to a String Character

        ch = (byte) (in & 0x0F); // Strip off low nibble
        out += pseudo[(int) ch]; // convert the nibble to a String Character

        return out;
    }

    /**
     * Check if frame exists, uncompress coordinates of frame (if new frame read
     * first randomAccessFile coordinates) Compressed coordinates of all atoms
     * randomAccessFile current frame are stored randomAccessFile the variable
     * "this.coordinatesCompressed" the uncompressed coordinates are stored
     * randomAccessFile "this.coordinatesUncompressed"
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return read result (true:=frame exists, false:=frame does not exist)
     */
    protected boolean uncompressFrameCoordinates(int frameIndex) throws IOException
    {

        if (frameIndex >= this.numOfFrames)
        { // frame does not exist
            return false;
        }

        if (this.frameBroken[frameIndex])
        { // frame is marked as broken
            System.err.println("WARNING: You are trying to read from a broken frame " + frameIndex
                    + " (numbering goes from 0 to n-1). This is not allowed! Please fix first the error in your trajectory.");

            return false;
        }
        
        // frame exist => read header information from current frame
        if (this.nrOfCurrentFrameCoordinates != frameIndex)
        { // only read frame header and coordinates if it is a
            // new frame, if it is already the current frame don't
            // do anything

            if (this.nrOfCurrentFrameHeader != frameIndex)
            {
                readFrame(frameIndex);
            } // reread frame header, only needed randomAccessFile cases of programming errors

            this.nrOfCurrentFrameCoordinates = frameIndex; // store the nr. of the current frame (from 0 to
            // numOfFrames-1), to avoid reading and decoding same
            // frame data

            if (this.nrAtoms > 9)
            { // coordinates of atoms are compressed => uncompress them , if coordinates are
                // not compressed don't do anything
                // uncompress coordinates
                this.coordinatesUncompressed = xdr3dfcoord(this.coordinatesCompressed, this.nrAtoms, this.precision);
            }
        }

        return true;
    }

    /**
     * Return positions of atoms randomAccessFile current frame. Check if
     * current frame exists and return coordinates
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return coordinates of atoms randomAccessFile current frame ((amount
     * atoms)x3 array )
     */
    public IDoubleArray getPositionsAt(int frameIndex) throws IOException
    {
        //      System.out.println("frame index "+frameIndex+" "+randomAccessFile.getFilePointer());

        if (!readFrame(frameIndex))
        {
            return null;
        } // check if frame exists, read "frame header", "coordinate header" and coordinates (if compressed do not
        // decode them) of current frame

        if (!uncompressFrameCoordinates(frameIndex))
        {
            return null;
        } // read header of current frame and check if frame exists

        return this.coordinatesUncompressed;
    }

    /**
     * Return precision of compressing algorithm for current frame The default
     * value is 1000 (scale factor to reduce coordinate precision from float to
     * int).
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return precision used randomAccessFile current frame (default 1000)
     */
    public float getPrecisionAt(int frameIndex) throws IOException
    {

        if (!readFrame(frameIndex))
        {
            return 0;
        } // read header of current frame and check if frame exists

        return this.precision;
    }

    /**
     * Return forces of atoms randomAccessFile current frame Xtc trajectories
     * contain per defintion no forces!
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return null
     */
    public float[] getForcesAt(int frameIndex) throws IOException
    {
        return null;
    }

    /**
     * Return basis vectors of simulation box for current frame The simulation
     * box (tricline PBC) can be defined by 3 basisvectors a,b and c .
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return simulation box vectors of current frame (3x3 array )
     */
    public float[] getBoxAt(int frameIndex) throws IOException
    { // read box vectors from frame header

        if (!readFrame(frameIndex))
        {
            return null;
        } // read header of current frame and check if frame exists

        return this.box;
    }

    /**
     * Return number of atoms randomAccessFile current frame Read header of
     * current frame and check if frame exists.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return number of atoms randomAccessFile current frame
     */
    public int getNumberOfAtomsAt(int frameIndex) throws IOException
    {

        if (!readFrame(frameIndex))
        {
            return 0;
        } // read header of current frame and check if frame exists

        return this.nrAtoms;
    }

    /**
     * Return simulation frame number randomAccessFile trajectory.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return simulation frame number randomAccessFile trajectory
     */
    public int getStepAt(int frameIndex) throws IOException
    {

        if (!readFrame(frameIndex))
        {
            return 0;
        } // read header of current frame and check if frame exists

        return this.step;
    }

    /**
     * Return simulation time of frame randomAccessFile trajectory.
     *
     * @param frameIndex number of frame randomAccessFile trajectory, frameIndex
     * goes from 0 to n-1
     *
     * @return simulation time which corresponds to cuurent frame number
     * randomAccessFile trajectory
     */
    public float getTimeAt(int frameIndex) throws IOException
    {

        if (!readFrame(frameIndex))
        {
            return 0;
        } // read header of current frame and check if frame exists

        return this.t;
    }

    /**
     * Close input file trajectory.
     */
    @Override
    public void close()
            throws IOException
    {
        this.randomAccessFile.close();
    }

    @Override
    public void open()
            throws IOException
    {
        this.randomAccessFile = new CachedRandomAccessFile(filename);
    }

    @Override
    public String getFileName()
    {
        return (filename);
    }

    /**
     * Simplified java implementation of the funtion "static int sizeofint(
     * const int maxSize)" from libxdrf.c.
     *
     * @param maxSize maximum size of rescaled integers (atom coordinate) to
     * compress
     *
     * @return Method returns the number of bits needed to store an integer with
     * given max size
     */
    private static int sizeofint(int maxSize)
    {
        int num = 1;
        int num_of_bits = 0;

        while ((maxSize >= num) && (num_of_bits < 32))
        {
            num_of_bits++;
            num <<= 1;
        }

        return num_of_bits;
    }

    /**
     * Simplified java implementation of the funtion "static int sizeofints(
     * const int num_of_ints, unsigned int sizes[])" from libxdrf.c.
     *
     * <pre>
     * Calculate 'bitsize' of compressed ints given the maximum value for all 3 dimensions randomAccessFile array sizes() (3x1) and the
     * number of small unsigned integers (:=3). Function returns the number of bits needed to read or write the compressed
     * coordinates with the routines receiveints and sendints.
     *
     * The compression uses the following trick it compares each set of x,y and z coordinates with the previous one, and if
     * the difference turns out to be small, it writes only the difference, thereby taking advantage of the reduced number of bits
     * needed to store the difference. The difference are combined into one big integer, saving even more bits. This is best
     * explaned with an example: Suppose dx, dy, dz are all less than 80, then writing these separatly would require 3 times
     * 7 bits or 21 bits. However the integer created by calculating (dx * 80 + dy) * 80 + dz needs only 19 bits of storage.
     * The mutiplication is special randomAccessFile the sense that, by using division, one can recover the exact values of dx, dy and dz. It is
     * like writing the integer randomAccessFile a base 80 number system.
     *
     * The code is optimesed. In general it calculates something similar to
     * - given: dxMax , dyMax , dzMax
     * - result similar to: roundUp [log2 [ ((dxMax*dxMax+dyMax)*dyMax+dzMax) ]]
     *
     * Modification from c to java code:
     * - For Gromacs the variable num_of_ints=3 => randomAccessFile Java implementation the variable is replaced by the number 3 to help
     * the compiler during optimisation
     * - In Java unsigned types does not exist => the corresponding signed types are used, this could cause a error for large
     * inputs (could be interpretated as negative numbers).
     * http://darksleep.com/player/JavaAndUnsignedTypes.html
     * Using the next larger typ needs more bytes for encoding this could cause problem during the bitshift operations.
     * - Java                int (32 bits): -2^31 ... 2^31 - 1      long (64 bits): -2^63 ... 2^63-1
     * - C      unsigned int (32 bits):  0 ... 2^32
     * The variables are interpreted as bits and not as numbers => change of variable may not cause any error
     * => Add to the ToDo list: make a detialed testing to analyse if a error occurs
     * </pre>
     *
     * @param sizes rescaled and shifted size of atom coordinates
     *
     * @return Method returns the number of bits needed to store an integer with
     * given max size
     */
    private static int sizeofints(int[] sizes)
    {
        int i, num;
        int num_of_bytes;
        int num_of_bits;
        int[] bytes = new int[32];
       // randomAccessFile c:  int bytes[32]
        int bytecnt;
        int tmp;
        num_of_bytes = 1;
        bytes[0] = 1;
        num_of_bits = 0;

        for (i = 0; i < 3; i++)
        { // loop over all 3 dimensions;  c variable "num_of_ints" replaced by 3;
            tmp = 0;

            for (bytecnt = 0; bytecnt < num_of_bytes; bytecnt++)
            {
                tmp = (bytes[bytecnt] * sizes[i]) + tmp;
                bytes[bytecnt] = tmp & 0xff;
                tmp >>= 8;
            }

            while (tmp != 0)
            {
                bytes[bytecnt++] = tmp & 0xff;
                tmp >>= 8;
            }

            num_of_bytes = bytecnt;
        }

        num = 1;
        num_of_bytes--;

        while (bytes[num_of_bytes] >= num)
        {
            num_of_bits++;
            num *= 2;
        }

        //        int tmpsum = num_of_bits + (num_of_bytes * 8);
        // System.out.println("num_of_ints=3" +" sizes[0]=" +sizes[0]+" sizes[1]=" +sizes[1]+" sizes[2]=" +sizes[2]+ "
        // num_of_bits=" +num_of_bits+" num_of_bytes=" +num_of_bytes+" tmpsum="+tmpsum);
        return num_of_bits + (num_of_bytes * 8);
    }

    /**
     * simplified java implementation of the funtion "static int receivebits(int
     * buf[], int num_of_bits)" from libxdrf.c.
     *
     * <pre>
     * Method decodes number (integer)  from buffer "buf" using the specified number of bits "num_of_bits".
     * Each atom coordinates would in general need uncompressed 4 byte=32 bits. The compressed coordinates are
     * encoded with a smaller amount of bits. The bits of the compressed coordinates are concated to a large
     * bitstream and then splited into 8 bit (=1 byte) blocks to store them in a byte array.
     * The buffer "buf" contains from position 3 to the end the compressed coordinates of all atoms randomAccessFile the current frame.
     * This function only decodes the coordinates of a single atom. The information which atom coordinate to decode
     * is stored randomAccessFile the first three elements of the buffer "buf":
     *  - buf[0]=cnt : counts the byte position (1 to end) of the "reading head" randomAccessFile the buffer
     *  - buf[1]=lastbits: counts the bit position (7 to 0)  randomAccessFile the current byte of the "reading head" in the buffer
     *  - buf[2]=lastbyte: contains the last 8 bit which were read form the buffer "buf"
     * In the next step reading start from the position right from the "reading head" !
     * An integer (num) is constructed from the extracted bits. This value is returned.
     *
     * Modification from c to java code:
     *  - c uses pointer arithmetik to split content of buf (int array=>32bit) to cbuf (unsigned char array=>8bit) => introduce a for loop and bit opertations to do this randomAccessFile java
     *  - cbuf is implemented in c as unsigned char array (1 byte), java char can store unicode (2 byte), java byte has 1 byte => change variable datatyp to byte
     *  - Converting from byte (8bit) to int (32bit), java adds 24 times 0  but c adds 24 times 1 => introduce bitmask "0...01...1" (16*0 and 16*1) to flip bits (correctResult=wrongResult & bitmask)
     * </pre>
     *
     * @param buf bitstream (array) of compressed atom coordinates (postion
     * randomAccessFile bitstream to decode is written in first three bytes of
     * buffer)
     * @param num_of_bits number of bits used to decode atom coordinates
     *
     * @return Method returns the current (encoded randomAccessFile first three
     * bytes of buf) partial uncompressed coordinate
     */
    private int receivebits(int[] buf, int num_of_bits)
    {

        try
        {
            int cnt, num, cnt_tmp, buf_readPosition, cnt_tmpStart;
            int lastbits, lastbyte; // randomAccessFile c unsigned int => tmp. variable only used for storing bits
            // (bitlevel not numberlevel !) => should cause no error
            final int mask = (1 << num_of_bits) - 1; // create bitmask "0...001...1" (24 zeros and 8 ones)
            final int bitMaskToConvertByteProperlyToIntInJava = 0xff; // =0...01...1 (16*0 and 16*1), converting from
            // byte (8bit) to int (32bit), java adds 24 times
            // 0  but c adds 24 times 1 => introduce bitmask
            // to flip bits

            cnt = buf[0];
            lastbits = buf[1]; // lastbits = (unsigned int) buf[1]; randomAccessFile c unsigned int
            lastbyte = buf[2]; // lastbyte = (unsigned int) buf[2];

            buf_readPosition = (int) (cnt / 4) + 3; // roundDown(cnt/4)+3 ; 1byte=4bit =>4; first 3 elements of buf are
            // reserved;
            cnt_tmp = cnt % 4; // remainder
            cnt_tmpStart = cnt_tmp; // create a copy of remainder for updating cnt=buf[0]

            // int ii_timesFour=0, ii_plusThree=0 , bufLegth_minusThree=buf.length-3; // tmp variables to optimise
            // loop execution
            byte[] cbuf = new byte[4]; // randomAccessFile c unsigned char array (1 byte), java char can store unicode
            // (2 byte), java byte (1 byte) => change variable datatyp to byte

            num = 0;

            while (num_of_bits >= 8)
            {

                // unelegent java implemtation of the c commands :
                // unsigned char * cbuf;
                // cbuf = ((unsigned char *)buf) + 3 * sizeof(*buf);
                // => cbuf is a  tmp variable (1 byte array) which holds a copy of buf (4 byte array) from postion
                // buf[3] to end
                cbuf[0] = (byte) ((buf[buf_readPosition] >> 24) & 0xFF);
                cbuf[1] = (byte) ((buf[buf_readPosition] >> 16) & 0xFF);
                cbuf[2] = (byte) ((buf[buf_readPosition] >> 8) & 0xFF);
                cbuf[3] = (byte) (buf[buf_readPosition] & 0xFF);
                lastbyte = (lastbyte << 8) | (cbuf[cnt_tmp++] & bitMaskToConvertByteProperlyToIntInJava);
                num |= (lastbyte >> lastbits) << (num_of_bits - 8);
                num_of_bits -= 8;
            }

            if (num_of_bits > 0)
            {

                if (lastbits < num_of_bits)
                {
                    cbuf[0] = (byte) ((buf[buf_readPosition] >> 24) & 0xFF);
                    cbuf[1] = (byte) ((buf[buf_readPosition] >> 16) & 0xFF);
                    cbuf[2] = (byte) ((buf[buf_readPosition] >> 8) & 0xFF);
                    cbuf[3] = (byte) (buf[buf_readPosition] & 0xFF);
                    lastbits += 8;
                    lastbyte = (lastbyte << 8) | (cbuf[cnt_tmp++] & bitMaskToConvertByteProperlyToIntInJava);
                }

                lastbits -= num_of_bits;
                num |= (lastbyte >> lastbits) & ((1 << num_of_bits) - 1);
            }

            num &= mask;
            buf[0] = cnt + cnt_tmp - cnt_tmpStart;
            buf[1] = lastbits;
            buf[2] = lastbyte;

            // System.out.print("**arb**"+Integer.toHexString(buf[0])+" " + Integer.toHexString(buf[1]) +" "
            // +Integer.toHexString(buf[2]) + " " + Integer.toHexString(buf[3])+ " " + Integer.toHexString(buf[4])+ " "
            // + Integer.toHexString(buf[5])+  " " +num_of_bits +" " + Integer.toHexString(lastbyte));
            return num;
        } catch (ArrayIndexOutOfBoundsException e)
        { // last try to catch a error if the frame "coordinates" block is
            // broken
            System.err.println("ERROR: Incomplete frame " + this.nrOfCurrentFrameCoordinates
                    + " (numbering goes from 0 to n-1). It's probably an error in the 'coordinates' section of this frame. For safty reasons the JVM is terminated!");
            System.err.println(e.getMessage());
            System.exit(-1);

            return 0;
        } // end try-catch
    }

    /**
     * Simplified java implementation of the funtion "static void
     * receiveints(int buf[], const int num_of_ints, int num_of_bits, unsigned
     * int sizes[], int nums[])" from libxdrf.c.
     *
     * <pre>
     * Modification from c to java code:
     *  - Calculation result "int nums[]" was passed in c code as function parmater (call be reference)
     *     => in java call be reference not possible => give result as return value back to main
     *  - For Gromacs the variable num_of_ints=3 => in Java implementation the variable is replaced by the number 3 to help the compiler during optimisation
     * </pre>
     *
     * @param buf bitstream (array) of compressed atom coordinates (postion
     * randomAccessFile bitstream to decode is written randomAccessFile first
     * three bytes of buffer)
     * @param num_of_bits number of bits used to decode atom coordinates
     * @param sizes rescaled and shifted size of atom coordinates
     *
     * @return Method returns the current (encoded randomAccessFile first three
     * bytes of buf) partial uncompressed coordinate
     */
    private int[] receiveints(int[] buf, int num_of_bits, int[] sizes)
    {
        final int num_of_ints = 3;
        Arrays.fill(this.bytes, 0);
        int[] nums =
        {
            0, 0, 0
        }; // in c function parameter

        int i, j, num_of_bytes = 0, p, num, sizes_valueAtPostionI;
        // {int tmpII=0; printf("*riB* buf=");for(tmpII=1; tmpII<=4; tmpII++){printf("%x " ,buf[tmpII]);} printf(",
        // num_of_ints=%d  num_of_bits=%d  sizes[]={%d %d %d} nums[]={%d %d %d}\n", num_of_ints, num_of_bits,
        // sizes[0],sizes[1],sizes[2],nums[0],nums[1],nums[2]);}

        while (num_of_bits > 8)
        {
            bytes[num_of_bytes++] = receivebits(buf, 8);
            num_of_bits -= 8;
        }

        if (num_of_bits > 0)
        {
            bytes[num_of_bytes++] = receivebits(buf, num_of_bits);
        }

        for (i = num_of_ints - 1; i > 0; i--)
        {
            num = 0;
            sizes_valueAtPostionI = sizes[i];

            for (j = num_of_bytes - 1; j >= 0; j--)
            {
                num = (num << 8) | bytes[j];
                p = num / sizes_valueAtPostionI; // round (p is int not double!)
                bytes[j] = p;
                num = num - (p * sizes_valueAtPostionI);
            }

            nums[i] = num;
        }

        nums[0] = bytes[0] | (bytes[1] << 8) | (bytes[2] << 16) | (bytes[3] << 24);

        // System.out.print("**ari**"+Integer.toHexString(buf[0])+" " + Integer.toHexString(buf[1]) +" "
        // +Integer.toHexString(buf[2]) + " " + Integer.toHexString(buf[3])+ " " + Integer.toHexString(buf[4])+ " " +
        // Integer.toHexString(buf[5])+  " " +num_of_bits +"  Sizes:" + Integer.toHexString(sizes[0])+" "+
        // Integer.toHexString(sizes[1])+" "+ Integer.toHexString(sizes[2])+"  nums:"+nums[0]+" "+ nums[1]+" "+
        // nums[2]+"\n");
        // {int tmpII=0; printf("*riA* buf=");for(tmpII=0; tmpII<=4; tmpII++){printf("%x " ,buf[tmpII]);} printf(",
        // num_of_ints=%d  num_of_bits=%d  sizes[]={%d %d %d} nums[]={%d %d %d}\n", num_of_ints, num_of_bits,
        // sizes[0],sizes[1],sizes[2],nums[0],nums[1],nums[2]);}
        return nums;
    }

    /**
     * Simplified java implementation of the funtion "int xdr3dfcoord(XDR *xdrs,
     * float *fp, int *size, float * precision)" from libxdrf.c.
     *
     * <pre>
     * It is introduced to store specifically 3d coordinates of molecules and it writes it randomAccessFile a compressed way.
     * It starts by multiplying all numbers by precision and rounding the result to integer. Effectively converting
     * all floating point numbers to fixed point. It uses an algorithm for compression that is optimized for
     * molecular data.
     *
     * Modification from c to java code:
     * - Function input "XDR *xdrs" (xdr stream with compressed coordinates) was passed randomAccessFile c code as pointer. In Java use insteed a reference to int array
     * - The decompressed coordinates where stored randomAccessFile the c code randomAccessFile the float pointer "float *fp" (pointer to array of floats, each array has 3 elements). In Java shift this to function return value.
     * </pre>
     *
     * @param coordinatesCompressed bitstream buffer with compressed atom
     * coordinates (postion randomAccessFile bitstream to decode is written
     * randomAccessFile first three bytes of buffer)
     * @param nrAtoms number of atoms randomAccessFile current frame
     * @param precision used for compressing atom coordinates
     *
     * @return Method returns the uncompressed coordinates of the atoms
     * randomAccessFile the current frame
     */
    private IDoubleArray xdr3dfcoord(int[] coordinatesCompressed, int nrAtoms, float precision)
    {
        /*
         * private float[][] xdr3dfcoord(boolean readWriteMode) { XDR *xdrs :=
         * filepointer to xdr stream => float *fp := pointer to array of floats,
         * each array has 3 elements int *size := amount of atoms (=amount of
         * coordinates/3) => nrAtomsFrameHeader float *precision := precision of
         * compresed coordinates => precision readWriteMode: true=:read,
         * false=:write
         */

        IDoubleArray lfp = new PrimitiveDoubleTable(nrAtoms, 3);

        int[] sizeInt = new int[3];
        int[] sizeSmall = new int[3];

        int[] thiscoord = new int[3];
        int[] prevcoord = new int[3];
        int[] bitsizeInt = new int[3];
        int flag, k, run, i, iOutput,/* prevrun,*/ is_smaller, bitSize, tmp;

        final int firstidx = 9; // start postion in array xtc_magicints  of first number !=0
//            final int lastidx = xtc_magicints.length; // max. position of elements in array
        int smallidx = amountBitsForCompressedCoordinates;
//            int maxidx = Math.min(lastidx, smallidx + 8); // select minimum number btween the amount of bits used for
        // compresisng coordinates and the maximum element
        // randomAccessFile array xtc_magicints
//            int minidx = maxidx - 8;
        /*
         * often this equal smallidx
         */
        int smaller = xtc_magicints[Math.max(firstidx, smallidx - 1)] / 2;
        int small = xtc_magicints[smallidx] / 2;
//            int larger = xtc_magicints[maxidx];
        sizeSmall[0] = sizeSmall[1] = sizeSmall[2] = xtc_magicints[smallidx];

        float inv_precision = (float) (1.0 / precision); // calculate invers precision for decoding atom coordinates
        int[] buf = coordinatesCompressed; // randomAccessFile array buf points to buffer with the compressed
        // coordinates
        buf[0] = buf[1] = buf[2] = 0; // buf[0-2] are special and do not contain actual data

        // Calculate the coding base from the maximal digit randomAccessFile the base by adding 1. E.g. 10 base:
        // maximal digit 9 =>9+1=10 =coding base !
        sizeInt[0] = maxInt[0] - minInt[0] + 1;
        sizeInt[1] = maxInt[1] - minInt[1] + 1;
        sizeInt[2] = maxInt[2] - minInt[2] + 1;

        // calculate the amount of bits needed to encode numbers randomAccessFile the range sizeInt
        if ((sizeInt[0] | sizeInt[1] | sizeInt[2]) > 0xffffff)
        {
            bitsizeInt[0] = sizeofint(sizeInt[0]);
            bitsizeInt[1] = sizeofint(sizeInt[1]);
            bitsizeInt[2] = sizeofint(sizeInt[2]);
            bitSize = 0;
            /*
             * flag the use of large sizes
             */
        }
        else
        {
            bitSize = sizeofints(sizeInt);
        }

        run = 0;
        i = 0;
        iOutput = 0;

        while (i < nrAtoms)
        { // for loop implemented a while (easier to optimse)=> for every atom randomAccessFile
            // frame decode coordinate

            if (bitSize == 0)
            {
                thiscoord[0] = receivebits(buf, bitsizeInt[0]);
                thiscoord[1] = receivebits(buf, bitsizeInt[1]);
                thiscoord[2] = receivebits(buf, bitsizeInt[2]);
            }
            else
            {
                thiscoord = receiveints(buf, bitSize, sizeInt);
            }

            i++; // increse for loop counter

            // add intial offset to "compressed coordinates" to  get the original atom coordinates
            thiscoord[0] += minInt[0];
            thiscoord[1] += minInt[1];
            thiscoord[2] += minInt[2];

            prevcoord[0] = thiscoord[0];
            prevcoord[1] = thiscoord[1];
            prevcoord[2] = thiscoord[2];

            flag = receivebits(buf, 1);
            is_smaller = 0;

            if (flag == 1)
            {
                run = receivebits(buf, 5);
                is_smaller = run % 3;
                run -= is_smaller;
                is_smaller--;
            }

            if (run > 0)
            {

                for (k = 0; k < run; k += 3)
                {
                    thiscoord = receiveints(buf, smallidx, sizeSmall);
                    i++;
                    thiscoord[0] += prevcoord[0] - small;
                    thiscoord[1] += prevcoord[1] - small;
                    thiscoord[2] += prevcoord[2] - small;

                    if (k == 0)
                    {

                        // interchange first with second atom for better compression of water molecules
                        tmp = thiscoord[0];
                        thiscoord[0] = prevcoord[0];
                        prevcoord[0] = tmp;
                        tmp = thiscoord[1];
                        thiscoord[1] = prevcoord[1];
                        prevcoord[1] = tmp;
                        tmp = thiscoord[2];
                        thiscoord[2] = prevcoord[2];
                        prevcoord[2] = tmp;
                        lfp.set(iOutput, 0, prevcoord[0] * inv_precision);
                        lfp.set(iOutput, 1, prevcoord[1] * inv_precision);
                        lfp.set(iOutput, 2, prevcoord[2] * inv_precision);
                        iOutput++;
                    }
                    else
                    {
                        prevcoord[0] = thiscoord[0];
                        prevcoord[1] = thiscoord[1];
                        prevcoord[2] = thiscoord[2];
                    }

                    // undo the conversion of atom coordinates from float to int
                    lfp.set(iOutput, 0, thiscoord[0] * inv_precision);
                    lfp.set(iOutput, 1, thiscoord[1] * inv_precision);
                    lfp.set(iOutput, 2, thiscoord[2] * inv_precision);
                    iOutput++;
                } // end for
            }
            else
            {
                // undo the conversion of atom coordinates from float to int
                lfp.set(iOutput, 0, thiscoord[0] * inv_precision);
                lfp.set(iOutput, 1, thiscoord[1] * inv_precision);
                lfp.set(iOutput, 2, thiscoord[2] * inv_precision);
                iOutput++;
            } // end if-else

            smallidx += is_smaller;

            if (is_smaller < 0)
            {
                small = smaller;

                if (smallidx > firstidx)
                {
                    smaller = xtc_magicints[smallidx - 1] / 2;
                }
                else
                {
                    smaller = 0;
                }
            }
            else if (is_smaller > 0)
            {
                smaller = small;
                small = xtc_magicints[smallidx] / 2;
            }

            sizeSmall[0] = sizeSmall[1] = sizeSmall[2] = xtc_magicints[smallidx];
        } // end while

        return lfp; // return decompressed atom coordinates
    }
};
