/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

/**
 *
 * @author noe
 */
public class IntSequence
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final IntSequenceUtilities util = new IntSequenceUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final IntSequenceFactory create = new IntSequenceFactory();        
}
