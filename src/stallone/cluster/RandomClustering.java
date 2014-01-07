/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import java.util.Iterator;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;

/**
 *
 * @author noe
 */
public class RandomClustering extends AbstractRegularClustering
{
    protected int nClusters;
    protected IIntArray centerIndexes;
    protected IIntArray data2cluster;

    public RandomClustering(int _nClusters)
    {
        this.nClusters = _nClusters;
    }

    /**
     * Checks if the new center is different from the accepted centers
     * @param acceptedCenters
     * @param newCenter
     * @return
     */
    private boolean isNew(IDataSequence acceptedCenters, IDoubleArray newCenter)
    {
        for (int i=0; i<acceptedCenters.size(); i++)
            if (metric.distance(acceptedCenters.get(i), newCenter) == 0)
                return false;
        return true;
    }

    private IDataList select(IIntArray indexes)
    {
        IDataList res = dataNew.list();
        Iterator<IDoubleArray> dataIterator = data.iterator();
        IDoubleArray x;
        int k = 0;
        for (int i=0; i<datasize; i++)
        {
            if (! dataIterator.hasNext())
                throw(new RuntimeException("Data set is shorter than specified"));
            x = dataIterator.next();
            if (i == indexes.get(k))
            {
                res.add(x);
                k++;
            }
            if (k >= indexes.size())
                break;
        }
        return res;
    }

    @Override
    public void perform()
    {
        int nfound = 0;
        int nsought = nClusters;
        IIntList acceptedCenterIndexes = intsNew.list(0);
        IDataList acceptedCenters = dataNew.list();

        int nAttempts = 0;
        while (nfound < nClusters && nAttempts <= 10)
        {
            // decide which indexes should be centers
            IIntArray I = intsNew.arrayRandomIndexes(datasize, nsought);
            ints.sort(I);
            // get corresponding data points
            IDataList datapoints = select(I);

            // add new ones
            for (int i=0; i<I.size(); i++)
            {
                if (isNew(acceptedCenters, datapoints.get(i)))
                {
                    acceptedCenterIndexes.append(I.get(i));
                    acceptedCenters.add(datapoints.get(i));
                }
            }

            nfound = acceptedCenters.size();
            nAttempts++;

            nsought = nClusters - nfound;
        }

        this.centerIndexes = acceptedCenterIndexes;
        this.clusterCenters = acceptedCenters;
        //System.out.println("CLUSTERing to centers: "+centerIndexes);

        // assign data
        this.voronoiPartitioning = discNew.voronoiDiscretization(clusterCenters, this.metric);
        this.data2cluster = intsNew.array(datasize);
        int k=0;
        for (IDoubleArray y : data)
            this.data2cluster.set(k++, this.voronoiPartitioning.assign(y));

        /*System.out.print("ASSIGN: ");
        for (int i=0; i<nClusters; i++)
            System.out.print(ints.count(data2cluster, i)+", ");
        System.out.println();*/

        // done
        this.resultsAvailable = true;
    }

    @Override
    public IIntArray getClusterIndexes()
    {
        return data2cluster;
    }

}
