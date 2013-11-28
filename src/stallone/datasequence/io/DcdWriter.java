package stallone.datasequence.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.datasequence.IDataWriter;
import stallone.api.doubles.IDoubleArray;

/**
 * Basic writer for DCD trajectory files.
 *
 * @author Martin Senne, Frank Noe
 */
public class DcdWriter implements IDataWriter
{
    /**
     * Stream to write to.
     */
    private OutputStream stream = null;
    /**
     * Dimension of data to write out. Is cartesian coordinates and must be
     * dividable by 3.
     */
    private final int nDimension;
    /**
     * Number of frames to write out.
     */
    private final int nFrames;
    /**
     * Number of frames alread written.
     */
    private int framesWritten;
    
    private ByteBuffer bb;

    /**
     * Constructor for writer, which writes DCD to a given file with name {@¢ode
     * filename} and writes immediately the header information.
     *
     * @param filename is the filename to write to
     * @param nFrames number of frames
     * @param nDimensions number of dimension.
     */
    public DcdWriter(String filename, int _nFrames, int _nDimensions)
    {
        try
        {
            stream = new BufferedOutputStream(new FileOutputStream(filename));
        } catch (FileNotFoundException ex)
        {
            Logger.getLogger(DcdWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.nFrames = _nFrames;
        this.nDimension = _nDimensions;
        framesWritten = 0;
        if (_nDimensions % 3 != 0)
        {
            throw new IllegalArgumentException("Dimension of trajectory must be dividable by 3.");
        }

        // write out header information
        writeHeader(nFrames, _nDimensions);
    }

    /**
     * Write out header. TODO: Fix header information properly. Is this
     * CHARMM??? What is it? What about endianess?
     *
     * @param nFrames number of frames, this trajectory has.
     * @param nDimensions number of dimension. Must be dividable by 3.
     */
    private void writeHeader(int nFrames, int nDimensions)
    {
        try
        {
            int startStep = 1;
            int freqSaving = 1;

            byte[] bytes = new byte[276];
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(84);  // 84
            bb.put(("CORD").getBytes());
            bb.putInt(nFrames); // nFrames
            bb.putInt(startStep);
            bb.putInt(freqSaving);
            for (int i = 0; i < 17; i++)
            {
                bb.putInt(0); // junk
            }
            bb.putInt(84);  // End of 84-byte-rec
            bb.putInt(164);  // Start of 164-byte-rec
            bb.putInt(2);  // 2*80 bytes follow
            for (int i = 0; i < 80; i++)
            {
                bb.put((byte) 0); // title1
            }
            for (int i = 0; i < 80; i++)
            {
                bb.put((byte) 0); // title2
            }
            bb.putInt(164);  // End of 164-byte-rec
            bb.putInt(4);  // Start of 4-byte-rec
            bb.putInt(nDimensions / 3); // nAtoms
            bb.putInt(4);  // End of 4-byte-rec
            stream.write(bytes);
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Append a frame to the DCD trajectory.
     *
     * @param v contains the frame data of the trajectory frame. Ecpected format
     * of v is
     * <pre>x1 y1 y1 x2 y2 y3</pre>.
     */
    @Override
    public void add(IDoubleArray v)
    {
        try
        {
            // write crds
            int n = v.size();

            if (n % 3 != 0)
            {
                throw new IllegalArgumentException("Vector has not a dimension, which is dividable by 3. Can not write out.");
            }
            else
            {

                // format:
                //         size_of_block x1      x2      size_of_block
                //         <int>         <float> <float> <int>
                //         size_of_block y1      y2      size_of_block
                //         <int>         <float> <float> <int>
                //         size_of_block z1      z2      size_of_block
                //         <int>         <float> <float> <int>
                // 4 bytes per float, 4 bytes per int

                int numberOfAtoms = n / 3;
                
                int nbytes = 4 * 3 * 2 + 4 * 3 * numberOfAtoms;
                
                if(bb == null || bb.capacity() < nbytes) {
                    bb = ByteBuffer.wrap(new byte[nbytes]);
                    bb.order(ByteOrder.LITTLE_ENDIAN);
                }

                bb.clear();
                // write all x's, then all y's, then all z's
                for (int d = 0; d < 3; d++)
                {
                    int chunksize = 4 * numberOfAtoms;

                    bb.putInt(chunksize);

                    for (int a = 0; a < numberOfAtoms; a++)
                    {
                        int idx = a * 3 + d;
                        bb.putFloat((float) v.get(idx));
                    }

                    bb.putInt(chunksize);
                }
                stream.write(bb.array(), 0, nbytes);
            }

            // increase the number of written frames
            framesWritten++;

        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void addAll(Iterable<IDoubleArray> data)
    {
        for (IDoubleArray d : data)
            add(d);
    }
    /**
     * Close the writer.
     *
     * Throws IllegalStateException, if number of written frames does not match
     * number of frames specified in constructor.
     */
    @Override
    public void close()
    {
        try
        {
            stream.close();
            if (framesWritten != nFrames)
            {
                throw new IllegalStateException("Number of written frames does not match number of specified frames."
                        + "(" + framesWritten + " written|" + nFrames + " specified).");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }


}
