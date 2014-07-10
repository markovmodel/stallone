/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dataprocessing;

/**
 *
 * @author noe
 */
public class DataProcessing
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final DataProcessingUtilities util = new DataProcessingUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final DataProcessingFactory create = new DataProcessingFactory();
}
