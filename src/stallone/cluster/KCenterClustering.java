package stallone.cluster;

import java.util.Iterator;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.doubles.fastutils.DoubleArrayList;
import stallone.doubles.fastutils.IntArrayList;

/**
 * Implementation based on the work of "A Fast Geometric Clustering Method on
 * Conformatino Space of Biomolecules" J. Sun and Y. Yao and X. Huang and V.
 * Pande and G. Carlsson and L. J. Guibas To appear Correspondence to
 * "guibas@cs.stanford.edu"
 *
 * @author Martin Senne
 */
public class KCenterClustering implements IClustering, IDiscretization
{

    private IDataList clusters = DataSequence.create.createDatalist();
    /**
     * data points which are going to be clustered.
     */
    private Iterable<IDoubleArray> data; // P
    private int size = 0;
    //int dimension;

    /*
     * Metric which is used as distance measure @see
     * biocomp.phaseprofiler.clustering.interfaces.IMetric
     */
    private IMetric<IDoubleArray> distanceMetric;
    /**
     * total number of clusters.
     */
    private int numberOfClusters;
    /**
     * stores the assignment of point to cluster numbers e.g.
     * assignedClusters[2] = 4 says, the third datapoint belongs to cluster 5
     */
    private IntArrayList assignedClusters;
    /**
     * seed of the random generator.
     */
    private long randomSeed;

    /**
     * @see biocomp.phaseprofiler.clustering.interfaces.IClustering
     */
    @Override
    public void perform()
    {
        // these are the minimal distances of all data points to any cluster center
        DoubleArrayList minDistances = new DoubleArrayList();


        // first cluster center is randomly selected
        // Random rnd = new Random(randomSeed);
        // clusterCenterIndexes[0] = rnd.nextInt(dataPoints.size());        

        // first cluster center is equal to first frame
        Iterator<IDoubleArray> it = data.iterator();
        if (!it.hasNext())
        {
            throw (new RuntimeException("Trying to cluster an empty data set"));
        }
        IDoubleArray v_0 = it.next().copy();
        clusters.add(v_0);
        assignedClusters.add(0);
        minDistances.add(0);
        //clusterCenterIndexes[0]=0;

        // consider second data point
        /*
         * if (!it.hasNext()) throw(new RuntimeException("Trying to cluster a
         * data set with one data point")); IVector v_i = it.next();
         * assignedClusters.add(0); minDistances.add(distanceMetric.measure(v_i,
         * v_0));
         */

        // first iteration: go over data, calculate distance to first center and assign first center
        System.out.println("KCenter: iteration 1/" + numberOfClusters + ".");
        IDoubleArray v_i = null;
        double maxMinDistance = 0;
        int count = 0;
        while (it.hasNext())
        {
            IDoubleArray p_j = it.next().copy();

            // calculate distance to cluster 0
            double d = distanceMetric.distance(p_j, v_0);
            minDistances.add(d);

            // is this the largest distance so far? Then memorize as next center candidate
            if (d >= maxMinDistance)
            {
                count++;
                maxMinDistance = d;
                v_i = p_j;
            }

            // assign every data point p_j to cluster 0
            assignedClusters.add(0);
        }


        // for all other clusters, add the most distance point as center and the reassign
        // for k clusters do k-2 further passes to the data
        for (int i = 1; i < numberOfClusters; i++)
        {
            System.out.println("KCenter: iteration " + (i + 1) + "/" + numberOfClusters + ".");

            // add most distance point as a new center
            clusters.add(v_i);

            int j = 0;
            maxMinDistance = 0;
            IDoubleArray v_next = null;
            for (it = data.iterator(); it.hasNext(); j++) //for (int j = 0; j < dataPoints.size(); j++)
            {
                IDoubleArray p_j = it.next().copy();

                // get minimal distance of p_j to cluster centers
                double currentDistance = minDistances.get(j);

                // calculate new distance of p_j to the possible new cluster center
                double newDistance = distanceMetric.distance(p_j, v_i);

                // if new cluster center closer, then reassign
                if (newDistance < currentDistance)
                {
                    assignedClusters.set(j, i);
                    minDistances.set(j, newDistance);

                }

                if (minDistances.get(j) >= maxMinDistance)
                {
                    maxMinDistance = minDistances.get(j);
                    v_next = p_j;
                }
            }

            v_i = v_next;
        }
    }

    @Override
    public void setClusterInput(Iterable<IDoubleArray> datapoints, int _size)
    {
        this.data = datapoints;
        this.size = _size;
        assignedClusters = new IntArrayList();
    }

    @Override
    public void setClusterInput(IDataSequence datapoints)
    {
        this.data = datapoints;
        this.size = datapoints.size();
        //this.dimension = datapoints.dimension();
        assignedClusters = new IntArrayList();
    }

    @Override
    public void setMetric(IMetric<IDoubleArray> metric)
    {
        this.distanceMetric = metric;
    }

    public void setNumberOfClusters(int numberOfClusters)
    {

        // initialize variables
        this.numberOfClusters = numberOfClusters;
        //this.clusterCenterIndexes = new int[numberOfClusters];
    }

    @Override
    public int getNumberOfClusters()
    {
        return numberOfClusters;
    }

    //@Override
    /*
     * public int getClusterIndex(int i) { return pointPBelongsToCluster[i];
    }
     */
    //@Override
    /*
     * public IDoubleArray getMembership(int i) {
     *
     * // here we need a factory for vector creation IDoubleArray membership =
     * Doubles.create.array(numberOfClusters);
     *
     * for (int j = 0; j < membership.size(); j++) { membership.set(j, 0.0d); }
     *
     * membership.set(pointPBelongsToCluster[i], 1.0d);
     *
     * return membership;
    }
     */
    /**
     * Sets the random seed for the pseudo random number generator in the
     * initialization. Need for reproducibility of the results.
     *
     * @param randomSeed The random seed for the number generator
     */
    public void setRandomSeed(long randomSeed)
    {
        this.randomSeed = randomSeed;
    }

    @Override
    public IDiscretization getClusterAssignment()
    {
        return this;
    }

    /**
     * Returns index of that cluster which is closest to the given point p.
     *
     * @param p poiint which is assigned to one of the clusters of
     *
     * @return index of cluster
     */
    @Override
    public int assign(IDoubleArray p)
    {
        double minimalDistance = Double.MAX_VALUE;
        int indexOfNearestCluster = -1;

        for (int i = 0; i < numberOfClusters; i++)
        {
            IDoubleArray v_i = clusters.get(i);

            // calculate distance
            double currentDistance = distanceMetric.distance(p, v_i);

            if (currentDistance < minimalDistance)
            {
                minimalDistance = currentDistance;
                indexOfNearestCluster = i;
            }
        }

        if (indexOfNearestCluster == -1)
        {
            throw new RuntimeException("Can not assign cluster.");
        }
        else
        {
            return indexOfNearestCluster;
        }
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray p)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray p)
    {
        return clusters.get(assign(p));
    }

    //@Override
    public String getDescriptiveName()
    {
        return "kcenter";
    }

    //@Override
    /*
     * public IDoubleArray getClusterCenter(int i) { return
     * dataPoints.get(clusterCenterIndexes[i]);
    }
     */
    @Override
    public Iterator<IDoubleArray> clusterCenterIterator()
    {
        return (clusters.iterator());
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        assignedClusters.trim();
        return (Ints.create.arrayFrom(assignedClusters.elements()));
    }

    @Override
    public IDataSequence getClusterCenters()
    {
        return clusters;
    }
}
