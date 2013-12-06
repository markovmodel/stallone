/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import stallone.api.cluster.*;

/**
 *
 * @author noe
 */
public class Coordinates
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final CoordinateUtilities util = new CoordinateUtilities();

    /**
     * The default factory for algebra algorithms, such as Eigenvalue decomposition
     */
    public static final CoordinateFactory create = new CoordinateFactory();

}
