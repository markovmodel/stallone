/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;

/**
 *
 * @author noe
 */
public interface INeighborSearch
{
    public void setData(IDataSequence data);

    public void setMetric(IMetric<IDoubleArray> m);

    /**
     *
     * @param index Data point index
     * @return
     */
    public int nearestNeighbor(int index);

    /**
     * Nearest neighbor to arbitrary point x
     * @param x
     * @return
     */
    public int nearestNeighbor(IDoubleArray x);

    public int[] nearestNeighbors(int index, int N);

    public int[] nearestNeighbors(IDoubleArray x, int N);

    public int[] neighbors(int index, double distance);

    public int[] neighbors(IDoubleArray x, double distance);

}
