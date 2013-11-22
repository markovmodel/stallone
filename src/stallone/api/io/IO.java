/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.io;

/**
 *
 * @author noe
 */
public class IO
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final IOUtilities util = new IOUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final IOFactory create = new IOFactory();

}
