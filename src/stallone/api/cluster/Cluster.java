/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

/**
 *
 * @author noe
 */
public class Cluster
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final ClusterUtilities util = new ClusterUtilities();

    /**
     * The default factory for algebra algorithms, such as Eigenvalue decomposition
     */
    public static final ClusterFactory create = new ClusterFactory();

}
