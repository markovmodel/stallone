package stallone.cluster;

import java.util.Iterator;
import stallone.discretization.VoronoiDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataSequence;
import stallone.datasequence.DataList;

/**
 * Clusters data objects in such a way, that cluster centers are at least in distance of dmin to eachother according to
 * the given metric. The assignment of data objects to cluster centers is performed by Voronoi paritioning. That means,
 * that a data object is assigned to that clusters center, which has the least distance.
 *
 * @param <T> data (whatever kind) to cluster
 * @author  Martin Senne
 */
public class RegularSpatialClustering
 extends AbstractRegularClustering
 implements IClustering
{

    /** Minimum distance which the cluster centers have to be away from eachother. */
    private double dmin;

    /**
     * Standard default constructor.
     */
    public RegularSpatialClustering()
    {
        super();
    }

    /**
     * Constructor.
     *
     * @param  metric
     * @param  data
     * @param  dmin
     */
    public RegularSpatialClustering(IMetric<IDoubleArray> metric, IDataSequence data, double dmin)
    {
        this.metric = metric;
        this.data = data;
        this.dmin = dmin;

        this.resultsAvailable = false;
        this.clusterCenters = null;
        this.voronoiPartitioning = null;
    }

    /**
     * @param  dmin  the dmin to set
     */
    public void setDmin(double dmin)
    {
        this.dmin = dmin;
        this.resultsAvailable = false;
    }

    /**
     * Executes the clustering algorithm. Use setters or constructor to prepare input data.
     */
    @Override
    public void perform()
    {
        DataList centroids = new DataList();

        Iterator<IDoubleArray> it = data.iterator();
        
        if (datasize > 0)
        {
            // take first element as centroid
            centroids.add(it.next());
        }
        else
        {
            throw new RuntimeException("No first element .... aborting.");
        }


        IDoubleArray current;

        while(it.hasNext())
        {
            current = it.next();

            double[] d = calculateDistances(centroids, current, metric);
            int minIndex = minIndex(d);

            if (d[minIndex] >= dmin)
            {
                centroids.add(current);
                System.out.println(d[minIndex] + "\t" + centroids.size());
            }
        }

        this.clusterCenters = centroids;
        this.voronoiPartitioning = new VoronoiDiscretization(this.clusterCenters, this.metric);
        this.resultsAvailable = true;
    }

    /**
     * Utility method to copy from java collection List interface to phaseprofiler ICollectionVector structure.
     *
     * @param   <U>     type parameter for List and ICollectionVector
     * @param   source  is the source list to copy
     *
     * @return  an instance of ICollectionVector which contains all elements of source
     */
    /* public static <U> ICollectionVector<U> copy( List<U> source ) {
     *  int n = source.size();
     *  ICollectionVector<U> target = new Vector<U>( n );
     *  int i = 0;
     *  for ( Iterator<U> it = source.iterator(); it.hasNext(); ) {
     *      target.setEntry( i, it.next() );
     *      i++;
     *  }
     *  return target;
     *}*/
    private static double[] calculateDistances(IDataSequence xs, IDoubleArray y, IMetric<IDoubleArray> metric)
    {
        int n = xs.size();
        double[] distances = new double[n];

        for (int i = 0; i < n; i++)
        {
            distances[i] = metric.distance(xs.get(i), y);
        }

        return distances;
    }

    public static int minIndex(double[] arr)
    {
        double min = Double.POSITIVE_INFINITY;
        int minIndex = 0;

        for (int i = 0; i < arr.length; i++)
        {

            if (arr[i] < min)
            {
                min = arr[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    //@Override
    public String getDescriptiveName()
    {
        return "regularspatialspaced";
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        return(Ints.create.arrayRange(super.getNumberOfClusters()));
    }
}
