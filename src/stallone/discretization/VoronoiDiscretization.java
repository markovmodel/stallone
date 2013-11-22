package stallone.discretization;


import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.datasequence.IDataSequence;

/**
 * Performs a voronoi partitioning.
 *
 * @param <T> is the type of data to partition
 *
 * @author  Martin Senne
 */
public class VoronoiDiscretization implements IDiscretization
{

    /** The definition of cluster centers which are used for partitioning. */
    private IDataSequence clusterCenters;
    /** The metric to use for partitioning. */
    private IMetric<IDoubleArray> metric;

    /**
     * Constructor.
     *
     * @param  clusterCenters  to use for Voronoi assignment
     * @param  metric          appropriate metric.
     */
    public VoronoiDiscretization(IDataSequence _clusterCenters, IMetric<IDoubleArray> _metric)
    {
        this.clusterCenters = _clusterCenters;
        this.metric = _metric;
    }


    /**
     * Get the cluster index, the object p is closest to.
     *
     * @param   p  is the object to assign to a cluster
     *
     * @return  the cluster index.
     */
    @Override
    public int assign(IDoubleArray p)
    {
        double minimalDistance = Double.MAX_VALUE;
        int indexOfClosestCluster = -1;

        int n = clusterCenters.size();

        for (int i = 0; i < n; i++)
        {
            IDoubleArray v_i = clusterCenters.get(i);

            // calculate distance
            double currentDistance = metric.distance(p, v_i);

            if (currentDistance < minimalDistance)
            {
                minimalDistance = currentDistance;
                indexOfClosestCluster = i;
            }
        }

        if (indexOfClosestCluster != -1)
        {
            return indexOfClosestCluster;
        }
        else
        {
            throw new RuntimeException("Can not assign cluster.");
        }
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray p)
    {
        return clusterCenters.get(assign(p));
    }

    /**
     * Get the membership assignment of object p.
     *
     * @param   p  is the object whose fuzzy membership assignment is retrieved.
     *
     * @return  the membership assignment of p.
     */
    @Override
    public IDoubleArray assignFuzzy(IDoubleArray p)
    {

        // here we need a factory for vector creation
        IDoubleArray membership = Doubles.create.array(clusterCenters.size());

        for (int j = 0; j < membership.size(); j++)
        {
            membership.set(j, 0.0d);
        }

        int clusterIndex = assign(p);
        membership.set(clusterIndex, 1.0d);

        return membership;
    }

    /**
     * @param  clusterCenters  the clusterCenters to set
     */
    public void setClusterCenters(IDataSequence clusterCenters)
    {
        this.clusterCenters = clusterCenters;
    }

    /**
     * @param  metric  the metric to set
     */
    public void setMetric(IMetric<IDoubleArray> metric)
    {
        this.metric = metric;
    }
}
