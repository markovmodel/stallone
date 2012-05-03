/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.api.doubles;

/**
 *
 * @author noe
 */
public interface IMetric<T extends IDoubleArray>
{
    /**
     * Returns the distance between two datasets.
     * @param d1
     * @param d2
     * @return
     */
    public double distance(T x, T y);
}
