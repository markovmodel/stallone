/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.stat;

/**
 *
 * @author noe
 */
public class Statistics
{
    /**
     * The default object for providing algebraic operations for Vector/Matrix objects, such as add and multiply
     */
    public static final StatisticsUtilities util = new StatisticsUtilities();

    /**
     * The default factory for algebra algorithms, such as Eigenvalue decomposition
     */
    public static final StatisticsFactory create = new StatisticsFactory();
}
