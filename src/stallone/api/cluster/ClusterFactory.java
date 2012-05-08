/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

import stallone.cluster.RegularSpatialClustering;
import stallone.cluster.KMeansClustering;
import stallone.cluster.KCenterClustering;
import stallone.doubles.EuclideanDistance;
import stallone.api.doubles.IMetric;
import stallone.api.datasequence.IDataSequence;
import stallone.api.cluster.*;
import stallone.api.doubles.IDoubleArray;
import stallone.cluster.*;

import stallone.doubles.MinimalRMSDistance3D;

/**
 *
 * @author noe
 */
public class ClusterFactory
{
    public static final int METRIC_EUCLIDEAN = 0;
    public static final int METRIC_MINRMSD = 1;

    public IMetric<IDoubleArray> metric(int metricID, int nRows)
    {
        if (metricID == METRIC_MINRMSD)
        {
            return new MinimalRMSDistance3D(nRows);
        }
        if (metricID == METRIC_EUCLIDEAN)
        {
            return new EuclideanDistance();
        }
        else
        {
            throw new RuntimeException("No metric.");
        }
    }    
    
    private void constructKmeans(KMeansClustering clustering, Iterable<IDoubleArray> data, int size, IMetric metric, int k, int maxIter)
    {
        clustering.setInitialClusterCentersByRandom(k);
        clustering.setMaxIterations(maxIter);
        clustering.setMetric(metric);
        clustering.setClusterInput(data, size);
    }

    public IClustering createKmeans(IDataSequence data, IMetric metric, int k, int maxIter)
    {
        return createKmeans(data, data.size(), metric, k, maxIter);
    }
    
    public IClustering createKmeans(Iterable<IDoubleArray> data, int size, IMetric metric, int k, int maxIter)
    {        
        KMeansClustering clustering = new KMeansClustering();
        constructKmeans(clustering, data, size, metric, k, maxIter);
        return (clustering);
    }

    public IClustering createKmeans(Iterable<IDoubleArray> data, int size, int k, int maxIter)
    {
        return (createKmeans(data, size, new EuclideanDistance(), k, maxIter));
    }
    
    public IClustering createKmeans(IDataSequence data, int k, int maxIter)
    {
        return (createKmeans(data, new EuclideanDistance(), k, maxIter));
    }

    public IClustering createKmeans(Iterable<IDoubleArray> data, int size, int k)
    {
        return (createKmeans(data, size, new EuclideanDistance(), k, 1000));
    }

    public IClustering createKmeans(IDataSequence data, int k)
    {
        return (createKmeans(data, new EuclideanDistance(), k, 1000));
    }

    
    public IClustering createKcenter(Iterable<IDoubleArray> data, int size, IMetric metric, int k)
    {
        KCenterClustering clustering = new KCenterClustering();
        clustering.setNumberOfClusters(k);
        clustering.setMetric(metric);
        clustering.setClusterInput(data, size);
        return (clustering);
    }

    public IClustering createKcenter(IDataSequence data, IMetric metric, int k)
    {
        return (createKcenter(data, new EuclideanDistance(), k));
    }

    public IClustering createKcenter(Iterable<IDoubleArray> data, int size, int k)
    {
        return (createKcenter(data, size, new EuclideanDistance(), k));
    }

    public IClustering createKcenter(IDataSequence data, int k)
    {
        return (createKcenter(data, new EuclideanDistance(), k));
    }

    
    
    public IClustering createRegularSpatial(Iterable<IDoubleArray> data, int size, IMetric metric, double dmin)
    {
        RegularSpatialClustering clustering = new RegularSpatialClustering();
        clustering.setDmin(dmin);
        clustering.setMetric(metric);
        clustering.setClusterInput(data,size);
        return (clustering);
    }
    
    public IClustering createRegularSpatial(IDataSequence data, IMetric metric, double dmin)
    {
        return createRegularSpatial(data, data.size(), metric, dmin);
    }

    public IClustering createRegularSpatial(Iterable<IDoubleArray> data, int size, double dmin)
    {
        return (createRegularSpatial(data, size, new EuclideanDistance(), dmin));
    }
    
    public IClustering createRegularSpatial(IDataSequence data, double dmin)
    {
        return (createRegularSpatial(data, new EuclideanDistance(), dmin));
    }

    public IClustering createDensityBased(IDataSequence data, IMetric metric, double dmin, int minpts)
    {
        DensityBasedClusteringSimple clustering = new DensityBasedClusteringSimple(dmin, minpts);
        clustering.setMetric(metric);
        clustering.setClusterInput(data);
        return (clustering);
    }
    
    public IClustering createDensityBased(IDataSequence data, double dmin, int minpts)
    {
        return createDensityBased(data, new EuclideanDistance(), dmin, minpts);
    }    
    
    public IClustering createDensityBased(IDataSequence data, IMetric metric, int N)
    {
        DensityBasedClusteringSimpleN clustering = new DensityBasedClusteringSimpleN(N);
        clustering.setMetric(metric);
        clustering.setClusterInput(data);
        return (clustering);
    }
    
    public IClustering createDensityBased(IDataSequence data, int N)
    {
        return createDensityBased(data, new EuclideanDistance(), N);
    }    
    
}
