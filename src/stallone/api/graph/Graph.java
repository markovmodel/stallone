/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

/**
 *
 * @author noe
 */
public class Graph
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final GraphUtilities util = new GraphUtilities();

    /**
     * The default factory for algebra datatypes, such as Vectors and Matrices
     */
    public static final GraphFactory create = new GraphFactory();
}
