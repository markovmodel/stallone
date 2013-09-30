/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

/**
 *
 * @author noe
 */
public class MarkovModel
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final MarkovModelUtilities util = new MarkovModelUtilities();

    /**
     * The default factory for algebra algorithms, such as Eigenvalue decomposition
     */
    public static final MarkovModelFactory create = new MarkovModelFactory();

    
}
