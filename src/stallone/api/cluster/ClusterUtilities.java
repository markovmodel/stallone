/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IMetric;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class ClusterUtilities
{
    private IClustering perform(IClustering clustering)
    {
        clustering.perform();
        return clustering;
    }
    
    public IClustering kmeans(IDataSequence data, IMetric metric, int k, int maxIter)
    {
        return (perform(Clustering.create.createKmeans(data,metric,k,maxIter)));
    }

    public IClustering kmeans(Iterable<IDoubleArray> data, int size, IMetric metric, int k, int maxIter)
    {
        return (perform(Clustering.create.createKmeans(data,size,metric,k,maxIter)));
    }
    
    public IClustering kmeans(IDataSequence data, int k, int maxIter)
    {
        return (perform(Clustering.create.createKmeans(data,k,maxIter)));
    }
    
    public IClustering kmeans(Iterable<IDoubleArray> data, int size, int k, int maxIter)
    {
        return (perform(Clustering.create.createKmeans(data,size,k,maxIter)));
    }
    
    public IClustering kmeans(IDataSequence data, int k)
    {
        return (perform(Clustering.create.createKmeans(data,k)));
    }
    
    public IClustering kmeans(Iterable<IDoubleArray> data, int size, int k)
    {
        return (perform(Clustering.create.createKmeans(data,size,k)));
    }
    
    public IClustering kcenter(IDataSequence data, IMetric metric, int k)
    {
        return (perform(Clustering.create.createKcenter(data,k)));
    }

    public IClustering kcenter(Iterable<IDoubleArray> data, int size, IMetric metric, int k)
    {
        return (perform(Clustering.create.createKcenter(data,size,k)));
    }
    
    public IClustering createRegularSpatial(IDataSequence data, IMetric metric, double dmin)
    {
        return (perform(Clustering.create.createRegularSpatial(data,metric,dmin)));
    }

    public IClustering createRegularSpatial(Iterable<IDoubleArray> data, int size, IMetric metric, double dmin)
    {
        return (perform(Clustering.create.createRegularSpatial(data,size,metric,dmin)));
    }
    
    public IIntArray discretize(IDataSequence data, IDiscretization disc)
    {
        IIntArray res = Ints.create.array(data.size());
        for (int i=0; i<data.size(); i++)
            res.set(i, disc.assign(data.get(i)));
        return(res);
    }
}
