package stallone.cluster;

import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import java.util.Iterator;
import stallone.api.cluster.*;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.doubles.EuclideanDistance;

/**
 * Foundation for regular (either time or space) based clustering algorithms.
 *
 * @author  Martin Senne
 */
public abstract class AbstractRegularClustering implements IClustering
{

    /** List of cluster centers. Element are of type {@code <T>}. */
    protected IDataSequence clusterCenters;
    /** The data which is actually clustered. */
    protected Iterable<IDoubleArray> data;
    protected int datasize;
    /** The metric to use for clustering. */
    protected IMetric<IDoubleArray> metric = new EuclideanDistance();
    /** Whether clustering results are available. */
    protected boolean resultsAvailable;
    /**
     * Interface to assign new data to the already determined cluster centers. Here, Voronoi partitioning is used to
     * obtain the nearest cluster center data objects belong to.
     */
    protected IDiscretization voronoiPartitioning;

    /**
     * Constructor. Currently empty.
     */
    public AbstractRegularClustering()
    {
    }

    @Override
    public IDiscretization getClusterAssignment()
    {
        return voronoiPartitioning;
    }

    @Override
    public int getNumberOfClusters()
    {
        requireResultsAvailable();

        return clusterCenters.size();
    }

    /**
     * Executes the clustering algorithm. Use setters or constructor to prepare input data.
     */
    @Override
    public abstract void perform();

    protected void requireResultsAvailable()
    {
        if (this.resultsAvailable == true)
        {
            return;
        }
        else
        {
            throw new RuntimeException("Trying to query results, which are not available.");
        }
    }

    @Override
    public void setInput(IDataSequence _data)
    {
        this.resultsAvailable = false;
        this.data = _data;
        this.datasize = _data.size();
    }

    @Override
    public void setInput(IDataInput _data)
    {
        this.resultsAvailable = false;
        this.data = _data.singles();
        this.datasize = _data.size();
    }

    @Override
    public void setMetric(IMetric<IDoubleArray> _metric)
    {
        this.resultsAvailable = false;
        this.metric = _metric;
    }

    @Override
    public Iterator<IDoubleArray> clusterCenterIterator()
    {
        return clusterCenters.iterator();
    }

    //@Override
    public IDoubleArray getClusterCenter(int i)
    {
        return clusterCenters.get(i);
    }

    @Override
    public IDataSequence getClusterCenters()
    {
        return clusterCenters;
    }

    @Override
    public int assign(IDoubleArray data)
    {
        return(voronoiPartitioning.assign(data));
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray p)
    {
        return clusterCenters.get(assign(p));
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray data)
    {
        return(voronoiPartitioning.assignFuzzy(data));
    }


}
