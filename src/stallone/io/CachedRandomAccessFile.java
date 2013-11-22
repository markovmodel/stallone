package stallone.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.ByteBuffer;
import stallone.api.io.IReleasableFile;

public class CachedRandomAccessFile implements IReleasableFile
{
    protected String filename;
    protected byte[] page;
    protected ByteBuffer pageBuffer;
    protected int pageSize;
    protected long currentPos;
    protected long pageStart; // inclusive
    protected long pageEnd; // not inclusive
    protected RandomAccessFile randomAccessFile;
    protected long filesize;

    public CachedRandomAccessFile(String _filename) throws FileNotFoundException, IOException
    {
        this(_filename, 8192);
    }

    public CachedRandomAccessFile(String _filename, int pageSize) throws FileNotFoundException, IOException
    {
        if ((pageSize % 8) != 0)
        {
            throw new RuntimeException("Pagesize must be dividable by 8.");
        }

        this.filename = _filename;
        this.randomAccessFile = new RandomAccessFile(_filename, "r");
        this.page = new byte[pageSize];
        this.pageSize = pageSize;
        this.filesize = randomAccessFile.length();
        updatePosition(0);
        this.pageStart = 0;
        this.pageEnd = -1;
    }

    private void updatePosition(long pos) throws IOException
    {
        currentPos = pos;
    }

    /**
     * Make a new page available for reading.
     *
     * @return  remaining bytes in this page
     *
     * @throws  IOException
     */
    protected int makePageAvailable() throws IOException
    {

        // check range and load new page if neccessary
        if ((currentPos < pageStart) || (currentPos >= pageEnd))
        {

            if (currentPos < filesize)
            {
                pageStart = (currentPos / pageSize) * pageSize;
            }
            else
            {
                throw new IOException("Invalid position");
            }

            pageEnd = pageStart + pageSize;

            if (pageEnd > filesize)
            { // modifiy pageEnd if last page is smaller
                pageEnd = filesize;
            }

            randomAccessFile.seek(pageStart);

            int le = (int) (pageEnd - pageStart);
            randomAccessFile.readFully(page, 0, le);
            pageBuffer = ByteBuffer.wrap(page, 0, le);
        }

        // Set position in page buffer accordingly
        // Even if no new page is loaded, this can be neccessary e.g.
        // seek or skipBytes have changed position
        pageBuffer.position((int) (currentPos - pageStart));

        return (int) (pageEnd - currentPos);
    }

    public void seek(long pos) throws IOException
    {
        updatePosition(pos);
    }

    public int skipBytes(int n) throws IOException
    {

        if ((currentPos + n) > filesize)
        {
            int diff = (int) (filesize - currentPos);
            updatePosition(filesize);

            return diff;
        }
        else
        {
            updatePosition(currentPos + n);

            return n;
        }
    }

    public int read() throws IOException
    {

        if (currentPos < filesize)
        {
            makePageAvailable();

            int ret = pageBuffer.get();
            updatePosition(pageBuffer.position() + pageStart);

            return ret;
        }
        else
        {
            return -1;
        }
    }

    public int readInt() throws IOException
    {
        makePageAvailable();

        int value = pageBuffer.getInt();
        updatePosition(pageBuffer.position() + pageStart);

        return value;
    }

    public float readFloat() throws IOException
    {
        makePageAvailable();

        float value = pageBuffer.getFloat();
        updatePosition(pageBuffer.position() + pageStart);

        return value;
    }

    public long length() throws IOException
    {
        return randomAccessFile.length();
    }

    public long getFilePointer()
    {
        return currentPos;
    }

    @Override
    public void close() throws IOException
    {
        randomAccessFile.close();
    }

    @Override
    public void open() throws IOException
    {
        this.randomAccessFile = new RandomAccessFile(filename, "r");
    }

    public void readFully(byte[] b) throws IOException
    {

        int requestedSize = b.length;
        int alreadyRead = 0;

        while (alreadyRead < requestedSize)
        {

            // make page avaibable
            int bytesLeftInThisPage = makePageAvailable();
            int left = requestedSize - alreadyRead;

            if (left <= bytesLeftInThisPage)
            { // enough bytes available
                pageBuffer.get(b, alreadyRead, left);
                alreadyRead += left;
            }
            else
            {
                pageBuffer.get(b, alreadyRead, bytesLeftInThisPage);
                alreadyRead += bytesLeftInThisPage;
            }

            updatePosition(pageBuffer.position() + pageStart);

        }
    }

    public final String readLine() throws IOException
    {
        StringBuilder input = new StringBuilder();
        int c = -1;
        boolean eol = false;

        while (!eol)
        {

            switch (c = read())
            {

                case -1:
                case '\n':
                {
                    eol = true;

                    break;
                }

                case '\r':
                {
                    eol = true;

                    long cur = getFilePointer();

                    if ((read()) != '\n')
                    {
                        seek(cur);
                    }

                    break;
                }

                default:
                {
                    input.append((char) c);

                    break;
                }
            } // end switch
        } // end while

        if ((c == -1) && (input.length() == 0))
        {
            return null;
        }

        return input.toString();
    }

    @Override
    public String getFileName()
    {
        return(filename);
    }


}
