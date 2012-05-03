package stallone.doubles.io;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.regex.Pattern;
import stallone.api.doubles.Doubles;


/**
 * @author  Martin Senne
 */
public class SparseDoubleArrayAsciiReader extends AbstractAsciiMatrixReader {

    private Pattern sparseHeader = Pattern.compile("SPARSE\\s+[0-9]+\\s+[0-9]+\\s*");

    public SparseDoubleArrayAsciiReader(final String filename) throws FileNotFoundException, IOException {

        // improve, factory should depend on size of matrix
        super(filename);
    }

    @Override
    public boolean checkHeader() {
        return sparseHeader.matcher(getLine(0)).matches();
    }

    @Override
    protected void readToMatrix() {

        // read header
        int numberOfRows = 0;
        int numberOfColumns = 0;

        final String headerLine = getLine(0);

        if (sparseHeader.matcher(headerLine).matches()) {

            // header is
            // SPARSE <rows> <columns>
            final String[] elements = whiteSpacePattern.split(headerLine);

            if (elements.length == 3) {
                numberOfRows = Integer.parseInt(elements[1]);
                numberOfColumns = Integer.parseInt(elements[2]);
            } else {
                throw new RuntimeException("Invalid header for sparse matrix, should be 'SPARSE <rows> <columns>'.");
            }
        } else {
            throw new RuntimeException("Invalid header found.");
        }

        // create target matrix
        m = Doubles.create.sparseMatrix(numberOfRows, numberOfColumns);

        for (int i = getDataStart(), n = getDataEnd(); i < n; i++) {
            final String currentLine = getLine(i);
            final String[] elements = whiteSpacePattern.split(currentLine);
            final int rowLength = elements.length;

            if (rowLength == 3) {
                final int rowIdx = Integer.parseInt(elements[0]);
                final int colIdx = Integer.parseInt(elements[1]);
                final double val = Double.parseDouble(elements[2]);
                m.set(rowIdx, colIdx, val);
            } else {
                throw new RuntimeException("Expecting 3 entries per line: rowIndex columnIndex value.");
            }
        }

        System.out.println("Sparse matrix " + "with dimension ( " + numberOfRows + " x " + numberOfColumns + " ) " +
            "read successfully from file '" + filename + "'.");
    }
}
