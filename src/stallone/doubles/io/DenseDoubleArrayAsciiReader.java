package stallone.doubles.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.regex.Pattern;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

/**
 * @author Martin Senne
 */
public class DenseDoubleArrayAsciiReader extends AbstractAsciiMatrixReader
{
    private Pattern denseHeader = Pattern.compile("DENSE\\s+[0-9]+\\s+[0-9]+\\s*");

    public DenseDoubleArrayAsciiReader(final String filename) throws FileNotFoundException, IOException
    {

        // improve, factory should depend on size of matrix
        super(filename);
    }

    @Override
    public boolean checkHeader()
    {
        return denseHeader.matcher(getLine(0)).matches();
    }

    @Override
    protected void readToMatrix()
    {
        // read header
        int numberOfRows = 0;
        int numberOfColumns = 0;

        final String headerLine = getLine(0);

        if (denseHeader.matcher(headerLine).matches())
        {

            // header is
            // DENSE <rows> <columns>
            final String[] elements = whiteSpacePattern.split(headerLine);

            if (elements.length == 3)
            {
                numberOfRows = Integer.parseInt(elements[1]);
                numberOfColumns = Integer.parseInt(elements[2]);
            }
            else
            {
                throw new RuntimeException("Invalid header for dense matrix, should be 'DENSE <rows> <columns>'.");
            }
        }
        else
        {
            throw new RuntimeException("Invalid header found.");
        }

        // construct matrix
        m = Doubles.create.array(numberOfRows, numberOfColumns);


        int currentMatrixRow = 0;

        for (int i = getDataStart(), n = getDataEnd(); i < n; i++)
        {
            final String currentLine = getLine(i);

            if (currentMatrixRow >= m.rows())
            {
                throw new RuntimeException("Too many matrix rows in file. Expected: " + numberOfRows + ".");
            }


            final String[] elements = whiteSpacePattern.split(currentLine);

            final int rowLength = elements.length;

            if (rowLength == m.columns())
            {

                // creation should be moved outside
                final IDoubleArray row = m.viewRow(currentMatrixRow);

                for (int j = 0; j < rowLength; j++)
                {
                    final double value = Double.parseDouble(elements[j]);
                    row.set(j, value);
                }
            }
            else
            {
                throw new RuntimeException("Too many or too few entries for matrix in line " + i + " of file.");
            }

            currentMatrixRow++;

        } // end for

        if (currentMatrixRow < m.rows())
        {
            throw new RuntimeException("Matrix row(s) missing.");
        }

        System.out.println("Dense matrix " + "with dimension ( " + numberOfRows + " x " + numberOfColumns + " ) "
                + "read successfully from file '" + filename + "'.");

    }
}
