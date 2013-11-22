/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.discretization;

/**
 *
 * @author noe
 */
public class Discretization
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final DiscretizationUtilities util = new DiscretizationUtilities();


    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final DiscretizationFactory create = new DiscretizationFactory();
}
