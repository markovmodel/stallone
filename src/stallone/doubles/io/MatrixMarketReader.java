package stallone.doubles.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import stallone.api.API;
import stallone.api.doubles.IDoubleArray;

/**
 * <p>MatrixMarketReader class.</p>
 *
 * @author marscher
 */
public class MatrixMarketReader
{
    private MatrixMarketReader()
    {
    }

    /**
     * <p>reads a sparse matrix in matrix market format from a given filename</p>
     *
     * @param filename filename to read matrix from
     * @return a {@link stallone.api.doubles.IDoubleArray} object containing the sparse matrix.
     * @throws java.io.IOException if any.
     */
    public static IDoubleArray read(String filename) throws java.io.IOException
    {
        InputStream s = new FileInputStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(s));

        // read type code initial line
        String line = br.readLine();

        // read comment lines if any
        boolean comment = true;
        while (comment)
        {
            line = br.readLine();
            comment = line.startsWith("%");
        }

        // line now contains the size information which needs to be parsed
        String[] str = line.split("( )+");
        int nRows = (Integer.valueOf(str[0].trim())).intValue();
        int nColumns = (Integer.valueOf(str[1].trim())).intValue();
//        int nNonZeros = (Integer.valueOf(str[2].trim())).intValue();

        // now we're into the data section
        IDoubleArray matrix = API.doublesNew.sparseMatrix(nRows, nColumns);
        double x;
        while ((line = br.readLine()) != null)
        {
            str = line.split("( )+");
            int i = (Integer.valueOf(str[0].trim())).intValue();
            int j = (Integer.valueOf(str[1].trim())).intValue();
            if(str.length < 3) // for pattern matrices set a dummy value of 1.
                x = 1;
            else
                x = (Double.valueOf(str[2].trim())).doubleValue();
            matrix.set(i - 1, j - 1, x);
        }

        br.close();
        return matrix;
    }
}
