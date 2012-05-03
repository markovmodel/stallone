/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

/**
 *
 * @author noe
 */
public class Functions
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final FunctionUtilities util = new FunctionUtilities();


    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final FunctionFactory create = new FunctionFactory();    
}
