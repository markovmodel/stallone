package stallone.api.cluster;

import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import java.util.Iterator;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataInput;

/**
 * Each clustering algorithm implements this interface. It provides methods - to set input as an indexes set of vectors
 * e.g. data points - to set clustering metric - to perform clustering - to obtain cluster assignment (either crips via
 * getCluster or fuzzy via getMembership) of passed data points.
 *
 * @author  Martin Senne
 */
public interface IClustering extends IDiscretization
{

    /**
     * Set datapoints which are clustered.
     *
     * @param  datapoints
     */
    public void setInput(IDataSequence datapoints);

    /**
     * Set datapoints which are clustered.
     *
     * @param datapoints
     * @param size number of data points.
     */
    public void setInput(IDataInput data);

    /**
     * Set the metric which is used for clustering.
     *
     * @param  metric
     */
    public void setMetric(IMetric<IDoubleArray> metric);

    /**
     * Execute the clustering algorithm.
     */
    public void perform();

    /**
     * Query the number of clusters obtained after clustering was executed.
     *
     * @return  the number of determined clusters.
     */
    public int getNumberOfClusters();

    /**
     * Get iterator of cluster centers.
     *
     * @return  the cluster center information. May be null, if NOT available or NOT appropriate for the clustering
     *          algorithm.
     */
    public Iterator<IDoubleArray> clusterCenterIterator();

    //public IDoubleArray getClusterCenter(int i);

    public IDataSequence getClusterCenters();

    /**
     * Obtain the crisp cluster the datapoint with index i belongs to. For fuzzy clustering, see {@link getMembership}.
     *
     * @param   i  index of datapoint in datapoint {@link #setClusterInput}.
     *
     * @return  the cluster the datapoint is assigned to.
     */
    //public int getClusterIndex(int i);

    public IIntArray getClusterIndexes();

    /**
     * Obtain the fuzzy clustering assignment for datapoint with index i. For crisp clustering, see {@link getCluster}.
     *
     * @param   i  index of datapoint in datapoint see {@link #setClusterInput}.
     *
     * @return  the fuzzy membership assignment for datapoint with index i.
     */
    //public IDoubleArray getMembership(int i);

    //public IDoubleArray getMembershipsBystate(int state);

    /**
     * Obtain an interface which can be used to query yet unassigned datapoints. (thus not within datapoints passed via
     * setClusterInput).
     *
     * @return  the interface
     */
    public IDiscretization getClusterAssignment();

    /**
     * Get a descriptive name e.g. KMeans, etc ...
     *
     * @return  descriptive name for that clustering algorithm
     */
    //public String getDescriptiveName();
}
