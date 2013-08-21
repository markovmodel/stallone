/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import java.util.TreeSet;
import stallone.api.cluster.INeighborSearch;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IntsPrimitive;
import stallone.discretization.VoronoiDiscretization;

/**
 *
 * Direct nxn nearest neighbor search
 * 
 * @author noe
 */
public class NeighborSearchTrivial implements INeighborSearch
{

    /** The definition of cluster centers which are used for partitioning. */
    private IDataSequence data;
    /** The metric to use for partitioning. */
    private IMetric<IDoubleArray> metric;
    
    
    // for nearest points:
    private VoronoiDiscretization voronoi;

    /**
     * Constructor.
     *
     * @param  clusterCenters  to use for Voronoi assignment
     * @param  metric          appropriate metric.
     */
    public NeighborSearchTrivial(IDataSequence clusterCenters, IMetric<IDoubleArray> metric)
    {
        this.data = clusterCenters;
        this.metric = metric;
        
        this.voronoi = new VoronoiDiscretization(clusterCenters, metric);
    }
    
    @Override
    public void setData(IDataSequence _data)
    {
        this.data = _data;
        this.voronoi.setClusterCenters(_data);
    }

    @Override
    public void setMetric(IMetric<IDoubleArray> m)
    {
        this.metric = m;
        this.voronoi.setMetric(m);
    }

    @Override
    public int nearestNeighbor(int index)
    {
        return this.voronoi.assign(data.get(index));
    }

    @Override
    public int nearestNeighbor(IDoubleArray x)
    {
        return this.voronoi.assign(x);
    }

    @Override
    public int[] nearestNeighbors(int index, int N)
    {
        return nearestNeighbors(data.get(index), N);
    }

    @Override
    public int[] nearestNeighbors(IDoubleArray x, int N)
    {
        double[] distances = new double[data.size()];
        for (int i=0; i<data.size(); i++)
        {
            distances[i] = metric.distance(x, data.get(i));
        }        
        int[] res = DoublesPrimitive.util.smallestIndexes(distances, N);
        return res;
    }

    @Override
    public int[] neighbors(int index, double distance)
    {
        return IntsPrimitive.util.removeByValue(neighbors(data.get(index), distance), index);
    }

    @Override
    public int[] neighbors(IDoubleArray x, double distance)
    {
        double[] distances = new double[data.size()];
        for (int i=0; i<data.size(); i++)
        {
            distances[i] = metric.distance(x, data.get(i));
        }        
        int[] res = DoublesPrimitive.util.smallValueIndexes(distances, distance);
        return res;
    }


}
