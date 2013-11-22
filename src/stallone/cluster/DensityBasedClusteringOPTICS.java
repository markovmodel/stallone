/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import java.util.Comparator;
import java.util.PriorityQueue;
import stallone.api.cluster.INeighborSearch;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.doubles.EuclideanDistance;

/**
 * Density-based clustering algorithm OPTICS:
 *
 * Mihael Ankerst, Markus M. Breunig, Hans-Peter Kriegel, Jörg Sander (1999).
 * "OPTICS: Ordering Points To Identify the Clustering Structure". ACM SIGMOD
 * international conference on Management of data. ACM Press. pp. 49–60.
 *
 * @author noe
 */
public class DensityBasedClusteringOPTICS extends AbstractRegularClustering
{
    private IDataSequence datasequence;
    private INeighborSearch neighborSearch;

    // working data
    private boolean[] processed;
    private int[] numberOfNeighbors; // number of neighbors to point i within distance epsilon
    private double[] reachability_distance;
    private int[] orderedPoints;
    private int[] clusterIndexes;


    // parameters
    private double eps; // distance
    private int minpts; // minpoints
    private int nclusters; // number of clusters

    public DensityBasedClusteringOPTICS(double _epsilon, int _minpoints, int _nclusters)
    {
        this.eps = _epsilon;
        this.minpts = _minpoints;
        this.nclusters = _nclusters;

        neighborSearch = new NeighborSearchTrivial(null,new EuclideanDistance());
    }

    @Override
    public void setClusterInput(IDataSequence data)
    {
        super.setClusterInput(data);
        this.datasequence = data;
        neighborSearch.setData(data);

        this.reachability_distance = new double[data.size()];
        java.util.Arrays.fill(this.reachability_distance, -1);

        processed = new boolean[data.size()];
        java.util.Arrays.fill(processed, false);

        orderedPoints = new int[data.size()];
    }

    @Override
    public void setClusterInput(Iterable<IDoubleArray> data, int size)
    {
        throw new RuntimeException("Must use setClusterInput(IDataSequence)");
    }

    @Override
    public void setMetric(IMetric<IDoubleArray> metric)
    {
        super.setMetric(metric);
        neighborSearch.setMetric(metric);
    }

    @Override
    public void perform()
    {
        // initialize clusters:
        super.clusterCenters = DataSequence.create.createDatalist();

        optics();

        for (int p : orderedPoints)
            System.out.println(p+"\t"+reachability_distance[p]);
        System.exit(0);

        selectClusters();
    }

    private double coreDistance(int i)
    {
        int[] neighbors = neighborSearch.neighbors(i, eps);
        if (neighbors.length < minpts)
        {
            return -1;
        }
        else
        {
            double[] distances = new double[neighbors.length];
            for (int j = 0; j < distances.length; j++)
            {
                distances[j] = metric.distance(datasequence.get(i), datasequence.get(neighbors[j]));
            }
            DoublesPrimitive.util.sort(distances);
            return distances[minpts - 1];
        }
    }

    public double reachabilityDistance(int i, int j)
    {
        double cd = coreDistance(j);
        if (cd == -1)
        {
            return -1;
        }
        else
        {
            return Math.max(cd, metric.distance(datasequence.get(i), datasequence.get(j)));
        }
    }

    private void optics()
    {
        // iterate
        int nprocessed = 0;
        for (int i = 0; i < processed.length; i++)
        {
            System.out.println("Processing "+i);

            // skip if done
            if (processed[i])
            {
                continue;
            }

            int[] N = neighborSearch.neighbors(i, eps);
            processed[i] = true;
            System.out.println(" Neighbors: "+Ints.util.toString(Ints.create.arrayFrom(N)));

            // point p done;
            orderedPoints[nprocessed] = i;
            nprocessed++;

            PriorityQueue<Integer> seeds = new PriorityQueue<Integer>();

            double coredist_i = coreDistance(i);
            System.out.println(" core distance: "+coredist_i);
            if (coredist_i != -1)
            {
                update(N, i, seeds);
                while (!seeds.isEmpty())
                {
                    int q = seeds.poll();

                    System.out.println("  polling "+q);

                    int[] N2 = neighborSearch.neighbors(q, eps);

                    // point q done.
                    processed[q] = true;
                    orderedPoints[nprocessed] = q;
                    nprocessed++;

                    if (coreDistance(q) != -1)
                    {
                        update(N2, q, seeds);
                    }
                }
            }
        }
    }

    private void update(int[] neighbors, int p, PriorityQueue<Integer> seeds)
    {
        double coredist = coreDistance(p);

        for (int o : neighbors)
        {
            if (processed[o])
            {
                continue;
            }

            double newReachDist = Math.max(coredist, metric.distance(datasequence.get(o), datasequence.get(p)));
            if (reachability_distance[o] == -1)
            {
                reachability_distance[o] = newReachDist;
                seeds.add(o);
            }
            else               // o in Seeds, check for improvement
            {
                if (newReachDist < reachability_distance[o])
                {
                    reachability_distance[o] = newReachDist;
                    seeds.remove(o);
                    seeds.add(o);
                }
            }
        }
    }

    /**
     * Finds the right number of clusters.
     */
    private void selectClusters()
    {
        double rmin = 0;
        double rmax = DoublesPrimitive.util.max(reachability_distance);
        double r = rmax/2.0;

        int nclusters = 0;
        boolean inACluster = false;
        for (int p : orderedPoints)
        {
            if (r < reachability_distance[p])
            {
                if (inACluster)
                    clusterIndexes[p] = (nclusters-1);
                else
                {
                    nclusters++;
                    inACluster = true;
                    clusterIndexes[p] = (nclusters-1);
                }
            }
            else
            {

            }
        }

    }

    class OrderedPointComparator implements Comparator<Integer>
    {

        @Override
        public int compare(Integer p1, Integer p2)
        {
            if (reachability_distance[p1] < reachability_distance[p2])
            {
                return -1;
            }
            if (reachability_distance[p1] > reachability_distance[p2])
            {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args)
    {
        IDataList data = DataSequence.create.createDatalist();
        data.add(Doubles.create.arrayFrom(-1.4));
        data.add(Doubles.create.arrayFrom(-1));
        data.add(Doubles.create.arrayFrom(1));
        data.add(Doubles.create.arrayFrom(1.1));
        data.add(Doubles.create.arrayFrom(1.2));
        data.add(Doubles.create.arrayFrom(-1.3));
        data.add(Doubles.create.arrayFrom(-1.2));
        data.add(Doubles.create.arrayFrom(-1.1));
        data.add(Doubles.create.arrayFrom(1.3));
        data.add(Doubles.create.arrayFrom(1.4));

        DensityBasedClusteringOPTICS clustering = new DensityBasedClusteringOPTICS(0.5, 2, 2);
        clustering.setClusterInput(data);
        clustering.setMetric(new EuclideanDistance());
        clustering.perform();
    }
}
