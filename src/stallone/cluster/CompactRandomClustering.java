/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import java.io.IOException;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.ints.IIntArray;
import stallone.doubles.EuclideanDistance;

/**
 *
 * @author noe
 */
public class CompactRandomClustering extends AbstractRegularClustering
{
    private RandomClustering bestClustering;
    private int nclusters = 2;
    private int nrepeat = 0;

    public CompactRandomClustering(int _nClusters, int _nrepeat)
    {
        nclusters = _nClusters;
        if (_nrepeat > 1)
            this.nrepeat = _nrepeat;
    }

    @Override
    public void perform()
    {
        bestClustering = new RandomClustering(nclusters);
        bestClustering.setMetric(metric);
        bestClustering.setClusterInput(data, datasize);
        bestClustering.perform();
        double DB = cluster.clusterIndexDaviesBouldin(data, bestClustering.getClusterCenters(), metric, bestClustering.getClusterIndexes());
        //double DB = cluster.clusterNoncompactness(data, bestClustering.getClusterCenters(), metric, bestClustering.getClusterIndexes());
        //double DB = cluster.clusterIndexSizeImbalance(bestClustering.getClusterIndexes());

        for (int i=1; i<nrepeat; i++)
        {
            RandomClustering rcnew = new RandomClustering(nclusters);
            rcnew.setMetric(metric);
            rcnew.setClusterInput(data, datasize);
            rcnew.perform();

            double DBnew = cluster.clusterIndexDaviesBouldin(data, rcnew.getClusterCenters(), metric, rcnew.getClusterIndexes());
            //double DBnew = cluster.clusterNoncompactness(data, rcnew.getClusterCenters(), metric, rcnew.getClusterIndexes());
            //double DBnew = cluster.clusterIndexSizeImbalance(rcnew.getClusterIndexes());
            if (DBnew < DB)
            {
                DB = DBnew;
                bestClustering = rcnew;
            }
        }

        System.out.println("Choose clustering with DB = "+DB);

        super.clusterCenters = bestClustering.getClusterCenters();
        super.voronoiPartitioning = bestClustering.voronoiPartitioning;
        super.resultsAvailable = true;
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        return bestClustering.getClusterIndexes();
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0)
        {
            System.out.println("CompactRandomClustering <datafile> <nclusters> <nrepeats>");
            System.exit(0);
        }

        IDataSequence data = dataNew.dataSequenceLoader(args[0]).load();
        int nclusters = str.toInt(args[1]);
        int nrepeats = str.toInt(args[2]);

        CompactRandomClustering C = new CompactRandomClustering(nclusters, nrepeats);
        C.setClusterInput(data);
        C.setMetric(new EuclideanDistance());
        C.perform();

        IIntArray assign = C.getClusterIndexes();
        ints.print(assign,"","\n");
    }
}
