/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

import stallone.api.datasequence.IDataInput;
import stallone.cluster.CompactRandomClustering;
import stallone.cluster.FixedClustering;
import stallone.cluster.DensityBasedClusteringSimpleN;
import stallone.cluster.DensityBasedClusteringSimple;
import stallone.cluster.RandomClustering;
import stallone.cluster.RegularSpatialClustering;
import stallone.cluster.KMeansClustering;
import stallone.cluster.KCenterClustering;
import stallone.doubles.EuclideanDistance;
import stallone.api.doubles.IMetric;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

import stallone.coordinates.MinimalRMSDistance3D;

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

    private void kmeans(KMeansClustering clustering,
    		IDataInput data, IMetric<IDoubleArray> metric,
    		int k, int maxIter)
    {
        clustering.setInitialClusterCentersByRandom(k);
        clustering.setMaxIterations(maxIter);
        clustering.setMetric(metric);
        clustering.setInput(data);
    }

    private void kmeans(KMeansClustering clustering,
    		IMetric<IDoubleArray> metric, int k, int maxIter)
    {
        clustering.setInitialClusterCentersByRandom(k);
        clustering.setMaxIterations(maxIter);
        clustering.setMetric(metric);
    }

    public IClustering kmeans(IMetric metric, int k, int maxIter)
    {
        KMeansClustering clustering = new KMeansClustering();
        kmeans(clustering, metric, k, maxIter);
        return (clustering);
    }

    public IClustering kmeans(int k, int maxIter)
    {
        KMeansClustering clustering = new KMeansClustering();
        kmeans(clustering, new EuclideanDistance(), k, maxIter);
        return (clustering);
    }

    public IClustering kmeans(int k)
    {
        KMeansClustering clustering = new KMeansClustering();
        kmeans(clustering, new EuclideanDistance(), k, 1000);
        return (clustering);
    }

    public IClustering kmeans(IDataSequence data, IMetric<?> metric, int k, int maxIter)
    {
        return kmeans(data, metric, k, maxIter);
    }

    public IClustering kmeans(IDataInput data, IMetric metric, int k, int maxIter)
    {
        KMeansClustering clustering = new KMeansClustering();
        kmeans(clustering, data, metric, k, maxIter);
        return (clustering);
    }

    public IClustering kmeans(IDataInput data, int k, int maxIter)
    {
        return (kmeans(data, new EuclideanDistance(), k, maxIter));
    }

    public IClustering kmeans(IDataSequence data, int k, int maxIter)
    {
        return (kmeans(data, new EuclideanDistance(), k, maxIter));
    }

    public IClustering kmeans(IDataInput data, int k)
    {
        return (kmeans(data, new EuclideanDistance(), k, 1000));
    }

    public IClustering kmeans(IDataSequence data, int k)
    {
        return (kmeans(data, new EuclideanDistance(), k, 1000));
    }


    public IClustering kcenter(IMetric metric, int k)
    {
        KCenterClustering clustering = new KCenterClustering();
        clustering.setNumberOfClusters(k);
        clustering.setMetric(metric);
        return (clustering);
    }

    public IClustering kcenter(int k)
    {
        KCenterClustering clustering = new KCenterClustering();
        clustering.setNumberOfClusters(k);
        return (clustering);
    }

    public IClustering kcenter(IDataInput data, IMetric metric, int k)
    {
        KCenterClustering clustering = new KCenterClustering();
        clustering.setNumberOfClusters(k);
        clustering.setMetric(metric);
        clustering.setInput(data);
        return (clustering);
    }

    public IClustering kcenter(IDataSequence data, IMetric metric, int k)
    {
        return (kcenter(data, metric, k));
    }

    public IClustering kcenter(IDataInput data, int k)
    {
        return (kcenter(data, new EuclideanDistance(), k));
    }

    public IClustering kcenter(IDataSequence data, int k)
    {
        return (kcenter(data, new EuclideanDistance(), k));
    }



    public IClustering regspace(IDataInput data, IMetric<IDoubleArray> metric, double dmin)
    {
        RegularSpatialClustering clustering = new RegularSpatialClustering();
        clustering.setDmin(dmin);
        clustering.setMetric(metric);
        clustering.setInput(data);
        return (clustering);
    }

    public IClustering regspace(IDataSequence data,
    		IMetric<IDoubleArray> metric, double dmin)
    {
        return regspace(data, metric, dmin);
    }

    public IClustering regspace(IDataInput data, double dmin)
    {
        return (regspace(data, new EuclideanDistance(), dmin));
    }

    public IClustering regspace(IDataSequence data, double dmin)
    {
        return (regspace(data, new EuclideanDistance(), dmin));
    }

    public IClustering densitybased(IDataSequence data, IMetric<IDoubleArray> metric, double dmin, int minpts)
    {
        DensityBasedClusteringSimple clustering = new DensityBasedClusteringSimple(dmin, minpts);
        clustering.setMetric(metric);
        clustering.setInput(data);
        return (clustering);
    }

    public IClustering densitybased(IDataSequence data, double dmin, int minpts)
    {
        return densitybased(data, new EuclideanDistance(), dmin, minpts);
    }

    public IClustering densitybased(IDataSequence data, IMetric metric, int N)
    {
        DensityBasedClusteringSimpleN clustering = new DensityBasedClusteringSimpleN(N);
        clustering.setMetric(metric);
        clustering.setInput(data);
        return (clustering);
    }

    public IClustering densitybased(IDataSequence data, int N)
    {
        return densitybased(data, new EuclideanDistance(), N);
    }

    public IClustering random(int N)
    {
        IClustering clustering = new RandomClustering(N);
        return clustering;
    }

    public IClustering random(IDataSequence data, int N)
    {
        IClustering rc = random(N);
        rc.setInput(data);
        return rc;
    }

    public IClustering randomCompact(int N, int nrepeat)
    {
        IClustering clustering = new CompactRandomClustering(N,nrepeat);
        return clustering;
    }

    public IClustering randomCompact(IDataSequence data, int N, int nrepeat)
    {
        IClustering rc = randomCompact(N,nrepeat);
        rc.setInput(data);
        return rc;
    }

    public IClustering fixed(IDataSequence clusters)
    {
        return new FixedClustering(clusters);
    }

    public IClustering fixed(IDataSequence data, IDataSequence clusters)
    {
        IClustering fc = new FixedClustering(clusters);
        fc.setInput(data);
        return fc;
    }

}
