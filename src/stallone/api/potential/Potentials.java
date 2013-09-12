/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.potential;

/**
 *
 * @author noe
 */
public class Potentials
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final PotentialUtilities util = new PotentialUtilities();


    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final PotentialFactory create = new PotentialFactory();    
}
