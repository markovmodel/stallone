/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.strings;

/**
 * The API Object for Algebra usage. Has default objects for vector/matrix operations, and factories for datatypes and algorithms
 * @author noe
 */
public class Strings
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final StringUtilities util = new StringUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final StringFactory create = new StringFactory();
}
