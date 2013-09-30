/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.api.doubles;

/**
 *
 * @author noe
 */
public interface IDifferentiableMetric<T extends IDoubleArray> extends IMetric<T>
{
    /**
     * Returns the derivatives of the distance with respect to x
     * @param d1
     * @param d2
     * @return
     */
    public T gradientX(T x, T y);

    /**
     * Returns the derivatives of the distance with respect to y
     * @param d1
     * @param d2
     * @return
     */
    public T gradientY(T x, T y);

}
