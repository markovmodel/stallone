package stallone.doubles.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.doubles.IDoubleArray;


/**
 * Write out matrix in dense format.
 *
 * @author  Martin Senne
 */
public class DenseDoubleArrayAsciiWriter {
    private IDoubleArray m;
    private String filename;

    public DenseDoubleArrayAsciiWriter(IDoubleArray m, String filename) {
        this.m = m;
        this.filename = filename;
    }

    public void perform() {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filename));

            int cols = m.columns();
            int rows = m.rows();
            writer.write("DENSE " + Integer.toString(rows) + " " + Integer.toString(cols));
            writer.newLine();

            for (int i = 0; i < rows; i++) {

                // write one line
                StringBuilder lineBuilder = new StringBuilder();

                for (int j = 0; j < cols; j++) {
                    lineBuilder.append(Double.toString(m.get(i, j))).append(" ");
                }

                writer.write(lineBuilder.toString());
                writer.newLine();
                //
            }

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DenseDoubleArrayAsciiWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(DenseDoubleArrayAsciiWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // end try-catch-finally
    }
}
