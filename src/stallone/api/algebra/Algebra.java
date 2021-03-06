/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.algebra;

/**
 * The API Object for Algebra usage. Has default objects for vector/matrix operations,
 * and factories for datatypes and algorithms
 *
 * @author noe
 */
public class Algebra
{
    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final AlgebraFactory create = new AlgebraFactory();

    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final AlgebraUtilities util = new AlgebraUtilities();

}
