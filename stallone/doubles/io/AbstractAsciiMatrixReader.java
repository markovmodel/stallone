package stallone.doubles.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import stallone.api.doubles.IDoubleArray;
import stallone.io.CachedAsciiFileReader;

/**
 * Class AbstractAsciiMatrixReader is the baseclass of Dense and Sparse ascii
 * matrix readers.
 *
 * @author Martin Senne
 */
public abstract class AbstractAsciiMatrixReader extends CachedAsciiFileReader
{
    protected IDoubleArray m;

    public AbstractAsciiMatrixReader(final String filename) throws FileNotFoundException,
            IOException
    {
        super(filename, CachedAsciiFileReader.commentLine);
    }

    public IDoubleArray getMatrix()
    {
        if (m == null)
        {
            readToMatrix();
        }

        return m;
    }

    public int getDataStart()
    {
        return 1;
    }

    public int getDataEnd()
    {
        return getNumberOfLines();
    }

    public abstract boolean checkHeader();

    protected abstract void readToMatrix();
}
