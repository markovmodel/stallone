package stallone.complex.io;

import stallone.complex.io.OutputUtil;
import stallone.api.complex.IComplexArray;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Write out a list of vector, one column for each vector in dense format.
 *
 * @author  Martin Senne
 */
public class DenseAsciiVectorsWriter {
    private List<IComplexArray> vectors;
    private String filename;
    
    private boolean allPurelyReal;

    public DenseAsciiVectorsWriter(List<IComplexArray> vectors, String filename) {
        this.vectors = vectors;
        this.filename = filename;
        
        if (vectors.isEmpty()) {
            throw new IllegalArgumentException("List of vectors is empty.");
        }
        
        boolean first = true;
        int vectorSize = vectors.get(0).size();
        for (IComplexArray v : vectors) {
            if ( v.size() != vectorSize ) {
                throw new IllegalArgumentException("Vector do not all have the same length.");
            }
        }
        
        allPurelyReal = true;
        for (IComplexArray v : vectors) {
            if (!v.isReal()) {
                allPurelyReal = false;
            }
        }
    }

    public void perform() {
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filename));

            int rows = vectors.get(0).size();
            int cols = vectors.size();
            
            if ( allPurelyReal) {
                writer.write("DENSE " + Integer.toString(rows) + " " + Integer.toString(cols));
            } else {
                writer.write("DENSE COMPLEX " + Integer.toString(rows) + " " + Integer.toString(cols));
            }
            writer.newLine();

            for (int i = 0; i < rows; i++) {

                // write one line
                StringBuilder lineBuilder = new StringBuilder();
                
                for (int j = 0; j < cols; j++) {
                    double re = vectors.get(j).getRe(i);
                    double im = vectors.get(j).getIm(i);
                    lineBuilder.append( OutputUtil.scalarToEasyString(re,im) ).append(" ");
                }

                writer.write(lineBuilder.toString());
                writer.newLine();
                //
            }

            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DenseAsciiVectorsWriter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(DenseAsciiVectorsWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        } // end try-catch-finally
    }
    
    /*public static void main(String[] args) {
        IDoubleArray v1 = Doubles.create.array(2);
        v1.set(0, 1.0d);
        v1.set(1, 2.0d);
        IComplexArray v2 = Complex.create.array(2);
        v2.set(0, 1.0, 0.0);
        v2.set(1, 3.0, -5.0);
        
        List<IDoubleArray> vectors = new ArrayList<IDoubleArray>();
        vectors.add(v1);
        vectors.add(v2);
        
        DenseAsciiVectorsWriter writer = new DenseAsciiVectorsWriter(vectors, "/home/fischbac/vectors.dat");
        writer.perform();
        
    }*/
}
