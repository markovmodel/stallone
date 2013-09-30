/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;

/**
 *
 * @author noe
 */
public class Dynamics
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final DynamicsUtilities util = new DynamicsUtilities();

    /**
     * The default factory for algebra algorithms, such as Eigenvalue decomposition
     */
    public static final DynamicsFactory create = new DynamicsFactory();

}
