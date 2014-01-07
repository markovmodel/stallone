package stallone.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import stallone.api.algebra.Algebra;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.discretization.Discretization;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.doubles.EuclideanDistance;
import stallone.doubles.fastutils.IntArrayList;

/**
 * Perform k-means Clustering.
 *
 * @author  Martin Senne
 */
public class KMeansClustering implements IClustering
{

    /** undefined init mode. */
    private static final int INIT_UNDEFINED = 1;
    /** init cluster centers by random. */
    private static final int INIT_RANDOM = 2;
    /** init cluster centers by given indices to data. */
    private static final int INIT_BY_INDICES = 3;
    /** Data to be clustered by k-means; */
    /** Data to be clustered by k-means; */
    private Iterable<IDoubleArray> data;
    private int size = -1;
    /**
     * Metric used for distance measuring between data objects. Make sure, this metric conforms with vector algebra: in
     * order to compute new cluster center, the center of mass is constructed via
     *
     * <pre>
    1/n * sum_i=1^n (x_i)    with vectors  x_i
     *   </pre>
     */
    private IMetric<IDoubleArray> metric;
    /** Assignment from {@code numberOfClusters} data objects / points to cluster centers. */
    private IntArrayList assignments;
    /** Current list of cluster centers. */
    private IDataList clusterCenters;
    /** Number of clusters used. */
    private int numberOfClusters;
    /** Mode used for initialization of cluster centers. */
    private int initMode;
    /** maximum number of iterations. Is zero if no limitiations are set. */
    private int maxIterations;
    /** Only used internally during initialisation. */
    private IIntArray indices;
    private List<Integer> emptyCenterIndices = new ArrayList<Integer>();

    private IDiscretization voronoiDiscretization;


    public KMeansClustering()
    {
        initMode = INIT_UNDEFINED;
        maxIterations = 0;
    }

    public void setInitialClusterCentersByRandom(int numberOfClusters)
    {
        initMode = INIT_RANDOM;
        this.numberOfClusters = numberOfClusters;
    }

    public void setInitialClusterCenters(IIntArray indices)
    {
        initMode = INIT_BY_INDICES;
        numberOfClusters = indices.size();
        this.indices = indices;
    }

    private void initialize()
    {
        this.clusterCenters = DataSequence.create.list();
        this.assignments = new IntArrayList();

        if (initMode == INIT_RANDOM)
        {
            this.indices = Ints.create.arrayRandomIndexes(size, numberOfClusters);
        }

        int[] sortedIndexes = Arrays.copyOf(indices.getArray(), indices.size());
        Arrays.sort(sortedIndexes);

        // select Cluster centers from data
        int i = 0;
        for (Iterator<IDoubleArray> it = data.iterator(); it.hasNext();)
        {
            IDoubleArray current = it.next();

            // do we have this index?
            if (Arrays.binarySearch(sortedIndexes, i) >= 0)
            {
                clusterCenters.add(current.copy());
            }


            this.assignments.add(-1);

            i++;
        }
    }

    @Override
    public void setInput(IDataSequence _data)
    {
        this.data = _data;
        this.size = _data.size();
        assignments = new IntArrayList(_data.size());
    }

    @Override
    public void setInput(IDataInput _data)
    {
        this.data = _data.singles();
        this.size = _data.size();
        assignments = new IntArrayList(size);
    }

    @Override
    public void setMetric(IMetric<IDoubleArray> metric)
    {
        this.metric = metric;

        if (!(metric instanceof EuclideanDistance))
        {
            System.out.println("Warning. Not using euclidian metric. This may produce nonsensical results.");
        }
    }

    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    @Override
    public void perform()
    {
        System.out.println("Starting k-means.");

        // initialize
        initialize();

        boolean clustersChanged;
        boolean doMoreIterations;
        int loopIdx = 0;

        // the main loop
        do
        {
            // check if we need to do more iterations
            // max iterations == 0 means an infinite number of iterations
            if ((loopIdx < maxIterations) || (maxIterations == 0))
            {
                doMoreIterations = true;
            }
            else
            {
                doMoreIterations = false;
            }

            loopIdx++;
            System.out.println("Iteration step: " + loopIdx);

            clustersChanged = assign();
            if (clustersChanged)
            {
                updateCenters();
            }
        }
        while (clustersChanged && doMoreIterations);

        // final cluster center assignment
        this.voronoiDiscretization = Discretization.create.voronoiDiscretization(clusterCenters, metric);
    }

    private boolean assign()
    {
        // reset flags
        boolean clustersChanged = false;

        // make the assignments
        //for (int i = 0; i < n; i++)
        int i = 0;
        for (Iterator<IDoubleArray> it = data.iterator(); it.hasNext();)
        {
            IDoubleArray current = it.next();

            // find the closest center
            int closest = 0;
            double closestDistance = metric.distance(current, clusterCenters.get(0));

            // double closestDistance = distanceMeasure
            // .value(set.get(i), clusterCenters[0]);
            for (int j = 1; j < this.numberOfClusters; j++)
            {

                // double distance = distanceMeasure
                // .value(set.get(i), clusterCenters[j]);
                double distance = metric.distance(current, clusterCenters.get(j));

                if (distance < closestDistance)
                {
                    closestDistance = distance;
                    closest = j;
                }
            }

            if (assignments.get(i) != closest)
            {
                clustersChanged = true;
            }

            assignments.set(i, closest);

            i++;
        } // end for

        return (clustersChanged);
    }

    private void updateCenters()
    {
        // set all cluster centers to zero
        for (int i = 0; i < numberOfClusters; i++)
        {
            IDoubleArray clusterCenter = clusterCenters.get(i);
            clusterCenter.zero();
        }

        // sum up all data points per cluster they are assigned to
        double[] assignmentWeight = new double[this.numberOfClusters];

        int j = 0;
        for (Iterator<IDoubleArray> it = data.iterator(); it.hasNext();)
        //for (int j = 0; j < data.size(); j++)
        {

            IDoubleArray currentVector = it.next();

            int assignedTo = assignments.get(j);
            assignmentWeight[assignedTo] += 1;

            IDoubleArray clusterCenter = clusterCenters.get(assignedTo);
            Algebra.util.addTo(clusterCenter, currentVector);

            j++;
        }

        // normalize

        emptyCenterIndices.clear();
        
        for (int i = 0; i < numberOfClusters; i++)
        {
            //Store indices of empty cluster centers
            if(assignmentWeight[i]==0.0d){
                emptyCenterIndices.add(i);
            }
            Algebra.util.scale(1.0d / assignmentWeight[i], clusterCenters.get(i));
        }

        //Remove empty cluster centers
        if(!emptyCenterIndices.isEmpty()){
            if(emptyCenterIndices.size()>=numberOfClusters){
                System.out.println("Fatal error. All cluster centers empty. -> System.exit(1)!");
                System.exit(1);
            }
            else{
                System.out.printf("Removing %d empty cluster centers.", emptyCenterIndices.size());
                for(int i : emptyCenterIndices){
                    clusterCenters.remove(i);
                    numberOfClusters--;
                }
            }
        }

    }

    @Override
    public int getNumberOfClusters()
    {
        return this.numberOfClusters;
    }

    //@Override
    public int getClusterIndex(int i)
    {
        return assignments.get(i);
    }

    //@Override
    public IDoubleArray getMembership(int i)
    {
        IDoubleArray membership = Doubles.create.array(numberOfClusters);
        membership.set(i, 1.0d);

        return membership;
    }

    @Override
    public IDiscretization getClusterAssignment()
    {
        return(voronoiDiscretization);
    }

    //@Override
    public String getDescriptiveName()
    {
        return "kmeans";
    }


    //@Override
    public IDoubleArray getClusterCenter(int i)
    {
        return clusterCenters.get(i);
    }

    @Override
    public int assign(IDoubleArray data)
    {
        return(voronoiDiscretization.assign(data));
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray p)
    {
        return clusterCenters.get(assign(p));
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray data)
    {
        return(voronoiDiscretization.assignFuzzy(data));
    }

    @Override
    public Iterator<IDoubleArray> clusterCenterIterator()
    {
        return(clusterCenters.iterator());
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        assignments.trim();
        return Ints.create.arrayFrom(assignments.elements());
    }


    @Override
    public IDataSequence getClusterCenters()
    {
        return clusterCenters;
    }
}
