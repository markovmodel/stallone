/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import java.util.*;
import stallone.api.cluster.INeighborSearch;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IntsPrimitive;
import stallone.doubles.EuclideanDistance;

/**
 * Simple Density-based clustering algorithm 
 *
 * @author noe
 */
public class DensityBasedClusteringSimple extends AbstractRegularClustering
{
    protected IDataSequence datasequence;
    protected INeighborSearch neighborSearch = new NeighborSearchTrivial(null,new EuclideanDistance());

    // working data
    private int[] nneighbors;
    private int[] cluster;
    private int nClusters;
    private boolean[] done;

    // parameters
    protected double eps; // distance
    protected int minpts; // minpoints

        protected DensityBasedClusteringSimple()
        {}

    public DensityBasedClusteringSimple(double _epsilon, int _minpoints)
    {
        this.eps = _epsilon;
        this.minpts = _minpoints;
    }

    @Override
    public void setInput(IDataSequence data)
    {
        super.setInput(data);
        this.datasequence = data;
        neighborSearch.setData(data);

        this.nneighbors = new int[data.size()];

        this.cluster = new int[data.size()];

        done = new boolean[data.size()];
    }

    @Override
    public void setInput(IDataInput data)
    {
        throw new RuntimeException("Currently not implemented");
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
        // initialize all:
        super.clusterCenters = DataSequence.create.list();
        java.util.Arrays.fill(this.nneighbors, -1);
        java.util.Arrays.fill(this.nneighbors, -1);
        java.util.Arrays.fill(done, false);

        // compute all densities (we can integrate this into the algorithm below, in order to avoid unnecessary doubling)
        for (int i=0; i<nneighbors.length; i++)
            nneighbors[i] = neighborSearch.neighbors(i, eps).length;

        // order by decreasing density
        int[] orderedPoints = IntsPrimitive.util.mirror(IntsPrimitive.util.sortedIndexes(nneighbors));

        int currentCluster = -1;
        for (int p : orderedPoints)
        {
            if (done[p])
                continue;

            // set cluster index
            currentCluster++;
            cluster[p] = currentCluster;
            done[p] = true;

            // consider the neighbors
            TreeSet<Integer> colleagues = new TreeSet<Integer>();
            int[] N = neighborSearch.neighbors(p, eps);
            for (int i : N)
            {
                colleagues.add(i);
            }

            while(!colleagues.isEmpty())
            {
                // pull first candidate
                int q = colleagues.pollFirst();

                if (nneighbors[q] > minpts)
                {
                    cluster[q] = currentCluster;
                    int[] N2 = neighborSearch.neighbors(q, eps);
                    for (int i : N2)
                    {
                        if (nneighbors[i] > minpts && !done[i])
                        {
                            colleagues.add(i);
                        }
                    }
                    done[q] = true;
                }
            }
        }

        nClusters = currentCluster+1;
        super.resultsAvailable = true;
    }




    @Override
    public IIntArray getClusterIndexes()
    {
        return Ints.create.arrayFrom(cluster);
    }

    @Override
    public int getNumberOfClusters()
    {
        return nClusters;
    }

    public static void main(String[] args)
    {
        IDataList data = DataSequence.create.list();
        data.add(Doubles.create.arrayFrom(-1.4));
        data.add(Doubles.create.arrayFrom(-1.3));
        data.add(Doubles.create.arrayFrom(-1.2));
        data.add(Doubles.create.arrayFrom(-1.1));
        data.add(Doubles.create.arrayFrom(-1));
        data.add(Doubles.create.arrayFrom(0.5));
        data.add(Doubles.create.arrayFrom(1));
        data.add(Doubles.create.arrayFrom(1.1));
        data.add(Doubles.create.arrayFrom(1.2));
        data.add(Doubles.create.arrayFrom(1.3));
        data.add(Doubles.create.arrayFrom(1.4));


        /*DensityBasedClusteringSimple clustering = new DensityBasedClusteringSimple(0.5, 3);
        clustering.setClusterInput(data);
        clustering.setMetric(new EuclideanDistance());
        clustering.perform();*/

        for (double d=0; d<4; d+=0.1)
        {
                DensityBasedClusteringSimple clustering = new DensityBasedClusteringSimple(d, 3);
                clustering.setInput(data);
                clustering.setMetric(new EuclideanDistance());
                clustering.perform();
                System.out.println(d+"\t"+clustering.getNumberOfClusters());
        }

    }
}
