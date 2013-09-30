/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

/**
 *
 * @author noe
 */
public class HMM
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final HMMUtilities util = new HMMUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final HMMFactory create = new HMMFactory();    
}
