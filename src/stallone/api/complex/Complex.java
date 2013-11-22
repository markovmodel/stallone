/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.complex;

/**
 * The API Object for Algebra usage. Has default objects for vector/matrix operations,
 * and factories for datatypes and algorithms
 *
 * @author noe
 */
public class Complex
{
    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final ComplexFactory create = new ComplexFactory();


    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final ComplexUtilities util = new ComplexUtilities();

}
