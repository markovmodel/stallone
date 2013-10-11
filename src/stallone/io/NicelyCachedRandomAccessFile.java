package stallone.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import stallone.api.io.IReleasableFile;

/**
 * Class which efficiently caches read operation of a random access file. Nearly all read-methods of {@link
 * RandomAccessFile} are available. For fastest file access use {@link #readToBuffer(int) } in combination with {@link
 * #fitsInBuffer(int)}.
 *
 * <p>Internally, this class uses Java NIO FileChannel, to load data efficiently to its caching buffers.</p>
 *
 * @author   Martin Senne
 * @version  0.2
 */
public class NicelyCachedRandomAccessFile implements IReleasableFile
{
    private String filename;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VERBOSE = false;
    protected ByteBuffer pageBuffer;
    protected int pageSize;
    protected long currentPos;
    protected long pageStart; // inclusive
    protected long pageEnd; // not inclusive
    protected RandomAccessFile randomAccessFile;
    protected FileChannel randomAccessChannel;
    protected long filesize;

    public NicelyCachedRandomAccessFile(String _filename) throws FileNotFoundException, IOException
    {
        this(_filename, 8192);
    }

    public NicelyCachedRandomAccessFile(String _filename, int pageSize) throws FileNotFoundException, IOException
    {
        this.filename = _filename;
        this.randomAccessFile = new RandomAccessFile(_filename, "r");
        this.randomAccessChannel = randomAccessFile.getChannel();

        this.pageSize = pageSize;
        this.pageBuffer = ByteBuffer.allocate(pageSize);

        this.filesize = randomAccessFile.length();
        this.currentPos = 0;
        this.pageStart = 0;
        this.pageEnd = -1;
    }

    /**
     * Guarantees, that the next size bytes can be read without destroying or overwriting the buffer content
     * (pageBuffer) by continues read operations via {@link #readToBuffer(int) }.
     *
     * @param   size
     *
     * @return  if size bytes fit in buffer. If "false" is returned, the size is larger than the current pageBuffer
     *          size. Use {@link #changePageSize(int) } to set page buffer size accordingly.
     */
    public boolean fitsInBuffer(int size) throws IOException
    {

        if (size <= this.pageSize)
        {

            if (DEBUG_VERBOSE)
            {
                System.out.println("fitsInBuffer: enough space");
                System.out.println("  pageEnd: " + pageEnd + " currentPos: " + currentPos + " size " + size);
            }

            if (pageEnd <= (currentPos + size))
            { // set position such that all data fits in buffer

                if (DEBUG_VERBOSE)
                {
                    System.out.println("  Force reload via makePageAvailable.");
                }

                makePageAvailable(true);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Changes page size. Internally creates new pageBuffer and forces reload.
     *
     * @param   newPageSize  new size of page buffer.
     *
     * @throws  IOException
     */
    public void changePageSize(int newPageSize) throws IOException
    {

        if (DEBUG_VERBOSE)
        {
            System.out.println("changePageSize: currentPos: " + currentPos);
        }

        // set new page size and allocate appropriate buffer
        this.pageSize = newPageSize;
        this.pageBuffer = ByteBuffer.allocate(newPageSize);

        // force reload of pageBuffer with new size
        makePageAvailable(true);
    }

    protected void makePageAvailable(boolean forceLoad) throws IOException
    {

        if (DEBUG_VERBOSE)
        {
            System.out.println("makePageAailable: before: pageStart: " + pageStart + " currentPos " + currentPos
                    + " pageEnd " + pageEnd);
        }

        // check range and load new page if neccessary
        if ((currentPos < pageStart) || (pageEnd <= currentPos) || (forceLoad))
        {

            if (DEBUG)
            {

                if (forceLoad)
                {
                    System.out.println("  Reloading - forced.");
                }
                else
                {
                    System.out.println("  Reloading.");
                }
            }

            // check against file size
            if (currentPos < filesize)
            {
                // page aligned
                // pageStart = (currentPos / pageSize) * pageSize;

                // not page aligned
                pageStart = currentPos;
            }
            else
            {
                throw new IOException("Invalid position");
            }

            // truncate pageEnd according to filesize
            pageEnd = pageStart + pageSize;

            if (pageEnd > filesize)
            {
                pageEnd = filesize;
            }

            randomAccessChannel.position(pageStart);
            pageBuffer.rewind();

            int actPageLength = (int) (pageEnd - pageStart);
            int readDataLength = randomAccessChannel.read(pageBuffer);

            if (DEBUG_VERBOSE)
            {
                System.out.println("  Page length wanted: " + actPageLength);
                System.out.println("  Data actually read: " + readDataLength);
            }

        } // end if

        // Set position in page buffer accordingly
        // Even if no new page is loaded, this can be neccessary e.g.
        // seek or skipBytes have changed position
        pageBuffer.position((int) (currentPos - pageStart));

        if (DEBUG_VERBOSE)
        {
            System.out.println("makePageAailable: after: pageStart: " + pageStart + " currentPos " + currentPos
                    + " pageEnd " + pageEnd);
        }


    }

    /**
     * Seek to position pos in file.
     *
     * @param   pos
     *
     * @throws  IOException
     */
    public void seek(long pos) throws IOException
    {
        currentPos = pos;
    }

    /**
     * Read numberOfBytes from current file position into a ByteBuffer, which is returned. If you use {@link
     * #readToBuffer(pageSize)} subsequently within saving the ByteBuffer content, MAKE SURE that you have called {@link
     * #fitsInBuffer(int)} called with the cumulative number of bytes beforehand. <code>if ( fitsInBuffer( 12 ) ) {
     * ByteBuffer buffer1 = readToBuffer( 4 ); ByteBuffer buffer2 = readToBuffer( 8 ); }</code>
     *
     * @param   numberOfBytes
     *
     * @return
     *
     * @throws  IOException
     */
    public ByteBuffer readToBuffer(int numberOfBytes) throws IOException
    {

        makePageAvailable(false); // important: this needs to happen before pageEnd is used below: a new page can be
        // loaded

        if (DEBUG)
        {
            System.out.println("readToBuffer: Reading " + numberOfBytes + " bytes.");
        }

        if (DEBUG_VERBOSE)
        {
            System.out.println("readToBuffer before: currentPos: " + currentPos + " pageStart: " + pageStart);
        }

        int remaining = (int) (pageEnd - currentPos);

        if (numberOfBytes > pageSize)
        {
            throw new RuntimeException("Requesting more bytes than page size. " + "(" + numberOfBytes
                    + " bytes requested, " + pageSize + " bytes page size). " + "This seems to be a programmatic error.");
        }
        else
        {

            if (numberOfBytes <= remaining)
            { // buffer contains enough elements

                int inBufferPos = (int) (currentPos - pageStart);

                // save current buffer limit
                int savedLimit = pageBuffer.limit();

                // shorten buffer and create appropriate slice
                pageBuffer.limit(inBufferPos + numberOfBytes);

                ByteBuffer retBuffer = pageBuffer.slice();

                // restore page buffer limit
                pageBuffer.limit(savedLimit);

                if (DEBUG_VERBOSE)
                {
                    System.out.println("  Returned buffer (Size: " + numberOfBytes + ") is " + retBuffer);
                    System.out.println("  Read page buffer              : " + pageBuffer);
                }

                // advance currentPos
                currentPos += numberOfBytes;

                if (DEBUG_VERBOSE)
                {
                    System.out.println("readToBuffer after : currentPos: " + currentPos + " pageStart: " + pageStart);
                }

                return retBuffer;

            }
            else
            { // buffer does not contain enough elements

                if (DEBUG_VERBOSE)
                {
                    System.out.println("readToBuffer: round the corner refill.");
                }

                ByteBuffer newRoundTheEdgeBuffer = ByteBuffer.allocate(numberOfBytes);

                int firstBlockSize = remaining;
                int secondBlockSize = numberOfBytes - remaining;

                // read and copy first part
                for (int i = 0; i < firstBlockSize; i++)
                {
                    newRoundTheEdgeBuffer.put(pageBuffer.get());
                }

                // make available second part
                currentPos += remaining;
                makePageAvailable(false);

                // read and copy second part
                for (int i = 0; i < secondBlockSize; i++)
                {
                    newRoundTheEdgeBuffer.put(pageBuffer.get());
                }

                currentPos = pageBuffer.position() + pageStart;

                return newRoundTheEdgeBuffer;
            } // end if-else
        } // end if-else
    }

    /**
     * Skip n bytes. That means that the positions is advanced by n bytes. If n would move the file position behind the
     * file end position, then the posiition is moved to the file end.
     *
     * @param   n  number of bytes actually moved forward.
     *
     * @return
     *
     * @throws  IOException
     */
    public int skipBytes(int n) throws IOException
    {

        if ((currentPos + n) > filesize)
        {
            int diff = (int) (filesize - currentPos);
            currentPos = filesize;

            if (DEBUG)
            {
                System.out.println("Skipping " + n + " bytes. File limit exceeded. Current position afterwards: "
                        + currentPos);
            }

            return diff;
        }
        else
        {
            currentPos += n;

            if (DEBUG)
            {
                System.out.println("Skipping " + n + " bytes. Current position afterwards: " + currentPos);
            }

            return n;
        }
    }

    /**
     * Get length of file.
     *
     * @return  length of file in bytes.
     *
     * @throws  IOException
     */
    public long length() throws IOException
    {
        return randomAccessFile.length();
    }

    /**
     * Get current position in file.
     *
     * @return  current position.
     */
    public long getFilePointer()
    {
        return currentPos;
    }
    /*
     * public int read() throws IOException {
     *  if ( currentPos < filesize ) {
     *      makePageAvailable();
     *      int ret = pageBuffer.get();
     *      setPosition( pageBuffer.position() + pageStart );
     *      return ret;
     *  } else {
     *      return -1;
     *  }
     * }
     *
     * public int readInt() throws IOException {
     *  makePageAvailable();
     *  int value = pageBuffer.getInt();
     *  setPosition( pageBuffer.position() + pageStart );
     *  return value;
     * }
     *
     * public float readFloat() throws IOException {
     *  makePageAvailable();
     *  float value = pageBuffer.getFloat();
     *  setPosition( pageBuffer.position() + pageStart );
     *  return value;
     * }
     */
    /*
     *  public void close() throws IOException {
     *      randomAccessChannel.close();
     *      randomAccessFile.close();
     *  }
     *
     *  public void readFully( byte[] b ) throws IOException {
     *      int size = b.length;
     *      makePageAvailable(); // important: this needs to happen before pageEnd is used below: a new page can be
     * loaded
     *      int remaining = ( int ) ( pageEnd - currentPos );
     *
     *      if ( size > pageSize ) {
     *          System.out.println( "Reading large amount of data :) ." );
     *
     *
     *      } else {
     *          if ( size <= remaining ) { // buffer contains enough elements
     *              pageBuffer.get( b );
     *              setPosition( pageBuffer.position() + pageStart );
     *          } else { // buffer does not contain enough elements
     *              int firstBlockSize = remaining;
     *              int secondBlockSize = size - remaining;
     *              // first part
     *              pageBuffer.get( b, 0, firstBlockSize );
     *              setPosition( currentPos + remaining );
     *              // second part
     *              makePageAvailable();
     *              pageBuffer.get( b, firstBlockSize, secondBlockSize );
     *              setPosition( pageBuffer.position() + pageStart );
     *          }
     *      }
     *  }
     *
     *  public final String readLine() throws IOException {
     *      StringBuffer input = new StringBuffer();
     *      int c = -1;
     *      boolean eol = false;
     *
     *      while (!eol) {
     *          switch (c = read()) {
     *          case -1:
     *          case '\n':
     *              eol = true;
     *              break;
     *          case '\r':
     *              eol = true;
     *              long cur = getFilePointer();
     *              if ((read()) != '\n') {
     *                  seek(cur);
     *              }
     *              break;
     *          default:
     *              input.append((char)c);
     *              break;
     *          }
     *      }
     *
     *      if ((c == -1) && (input.length() == 0)) {
     *          return null;
     *      }
     *      return input.toString();
     *  } */

    @Override
    public void close() throws IOException
    {
        randomAccessFile.close();
    }

    @Override
    public void open() throws IOException
    {
        this.randomAccessFile = new RandomAccessFile(filename, "r");
        this.randomAccessChannel = randomAccessFile.getChannel();
    }

    @Override
    public String getFileName()
    {
        return(filename);
    }
}
