package stallone.complex.io;

import stallone.complex.io.OutputUtil;
import stallone.api.complex.IComplexArray;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.complex.Complex;


/**
 * Write out a vector, either as column or row.
 *
 * @author  Martin Senne
 */
public class DenseAsciiVectorWriter {
    private IComplexArray vector;
    private String filename;
    private Alignment alignment;

    private boolean allPurelyReal;

    public enum Alignment {
        ROW,
        COLUMN
    }

    public DenseAsciiVectorWriter(IComplexArray vector, String filename) {
        this( vector, filename, Alignment.COLUMN );
    }

    public DenseAsciiVectorWriter(IComplexArray vector, String filename, Alignment alignment ) {
        this.vector = vector;
        this.filename = filename;
        this.alignment = alignment;

        allPurelyReal = vector.isReal();
    }

    public void perform() {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filename));

            int n = vector.size();

            for (int i = 0; i < n; i++) {
                double re = vector.getRe(i);
                double im = vector.getIm(i);

                if (alignment.equals(Alignment.COLUMN)) {
                    writer.write( OutputUtil.scalarToEasyString(re,im) );
                    writer.newLine();
                } else {
                    writer.write( OutputUtil.scalarToEasyString(re,im) );
                    writer.write(" ");
                }

            }

            writer.newLine();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DenseAsciiVectorWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(DenseAsciiVectorWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // end try-catch-finally
    }

    public static void main(String[] args) {
        IComplexArray v1 = Complex.create.array(3);
        v1.set(0, 1.0d);
        v1.set(1, 2.0d, -3.0d);
        v1.set(2, 0.0d, 5.0d);

        DenseAsciiVectorWriter writer = new DenseAsciiVectorWriter(v1, "/home/fischbac/vector.dat");
        writer.perform();

    }
}
