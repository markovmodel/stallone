/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

/**
 *
 * @author noe
 */
public class DataSequence
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final DataSequenceUtilities util = new DataSequenceUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final DataSequenceFactory create = new DataSequenceFactory();
}
