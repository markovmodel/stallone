/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class FixedClustering extends AbstractRegularClustering
{
    IIntArray data2cluster;

    public FixedClustering(IDataSequence _clusterCenters)
    {
        this.clusterCenters = _clusterCenters;
    }

    @Override
    public void perform()
    {
        // assign data
        this.voronoiPartitioning = discNew.voronoiDiscretization(clusterCenters);
        this.data2cluster = intsNew.array(datasize);
        int k=0;
        for (IDoubleArray y : data)
            this.data2cluster.set(k++, this.voronoiPartitioning.assign(y));

        for (int i=0; i<this.clusterCenters.size(); i++)
            System.out.print(ints.count(data2cluster, i)+", ");

        // done
        this.resultsAvailable = true;    }

    @Override
    public IIntArray getClusterIndexes()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
