package stallone.doubles.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.doubles.IDoubleArray;


/**
 * Write out matrix in sparse format.
 *
 * @author  Martin Senne
 */
public class SparseDoubleArrayAsciiWriter {
    private IDoubleArray m;
    private String filename;

    public SparseDoubleArrayAsciiWriter(IDoubleArray m, String filename) {
        this.m = m;
        this.filename = filename;
    }

    public void perform() {
        BufferedWriter writer = null;

        try {
            String line;
            writer = new BufferedWriter(new FileWriter(filename));

            int cols = m.columns();
            int rows = m.rows();

            // write header
            writer.write("SPARSE " + Integer.toString(rows) + " " + Integer.toString(cols));
            writer.newLine();

            for (int i = 0; i < rows; i++) {

                for (int j = 0; j < cols; j++) {
                    double value = m.get(i, j);

                    if (value != 0.0d) {
                        line = Integer.toString(i) + " " + Integer.toString(j) + " " + Double.toString(value);
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(SparseDoubleArrayAsciiWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(SparseDoubleArrayAsciiWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // end try-catch-finally
    }
}
