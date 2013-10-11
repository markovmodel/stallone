/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.cluster;

import static stallone.api.API.*;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IMetric;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
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
    
    public IClustering kmeans(IDataSequence data, IMetric<?> metric, int k, int maxIter)
    {
        return (perform(Clustering.create.createKmeans(data,metric,k,maxIter)));
    }

    public IClustering kmeans(Iterable<IDoubleArray> data, int size, IMetric<?> metric, int k, int maxIter)
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
    
    public IClustering kcenter(IDataSequence data, IMetric<?> metric, int k)
    {
        return (perform(Clustering.create.createKcenter(data,k)));
    }

    public IClustering kcenter(Iterable<IDoubleArray> data, int size, IMetric<?> metric, int k)
    {
        return (perform(Clustering.create.createKcenter(data,size,k)));
    }
    
    public IClustering regularSpatial(IDataSequence data, IMetric<IDoubleArray> metric, double dmin)
    {
        return (perform(Clustering.create.createRegularSpatial(data, metric, dmin)));
    }

    public IClustering regularSpatial(Iterable<IDoubleArray> data, int size,
    		IMetric<IDoubleArray> metric, double dmin)
    {
        return (perform(Clustering.create.createRegularSpatial(data,size,metric,dmin)));
    }

    public IClustering densityBased(IDataSequence data, IMetric metric, double dmin, int minpts)
    {
        return (perform(Clustering.create.createDensityBased(data,metric,dmin,minpts)));
    }

        
    public IClustering densityBased(IDataSequence data, double dmin, int minpts)
    {
        return (perform(Clustering.create.createDensityBased(data,dmin,minpts)));
    }

    public IClustering densityBased(IDataSequence data, int N)
    {
        return (perform(Clustering.create.createDensityBased(data,N)));
    }
    
    public IIntArray discretize(IDataSequence data, IDiscretization disc)
    {
        IIntArray res = Ints.create.array(data.size());
        for (int i=0; i<data.size(); i++)
        {
            res.set(i, disc.assign(data.get(i)));
        }
        return(res);
    }

    
    public IDoubleArray clusterSizes(Iterable<IDoubleArray> data, IDataSequence centers, IMetric<IDoubleArray> metric, IIntArray assignment)
    {
        double[] res = new double[centers.size()];
        double[] counts = new double[centers.size()];
        int k=0;
        for (IDoubleArray x : data)
        {
            int c = assignment.get(k);
            counts[c] += 1;
            double d = metric.distance(x, centers.get(c));
            res[c] += d*d;
            k++;
        }
        for (int i=0; i<res.length; i++)
            res[i] = Math.sqrt(res[i]/counts[i]);
        return doublesNew.array(res);
    }

    public double clusterIndexSizeImbalance(IIntArray assignment)
    {
        double[] sizes = new double[ints.max(assignment)+1];
        for (int i=0; i<assignment.size(); i++)
            sizes[assignment.get(i)] += 1;
        double meansize = doubleArrays.mean(sizes);
        for (int i=0; i<sizes.length; i++)
            sizes[i] -= meansize;
        return doubleArrays.norm(sizes);
    }    
    
    /**
     * Computes the noncompactness measure
     * C = || D ||
     * where D is the vector of cluster diameters
     * @param clustering
     * @return 
     */
    public double clusterNoncompactness(Iterable<IDoubleArray> data, IDataSequence centers, IMetric<IDoubleArray> metric, IIntArray assignment)
    {
        double res = 0;
        int k=0;
        for (IDoubleArray x : data)
        {
            int c = assignment.get(k);
            double d = metric.distance(x, centers.get(c));
            res += d*d;
            //res += d;
            k++;
        }
        //return res/(double)k;
        return Math.sqrt(res/(double)k);
    }
    
    /**
     * Computes the Davies-Bouldin clustering index, defined by
     * DB = (1/n) sum_i^n max{i!=j} (d_i + d_j)/(d_ij)
     * where d_i, d_j are the sizes of the two clusters and d_ij is the distance between the clusters
     * @param clustering
     * @return 
     */
    public double clusterIndexDaviesBouldin(Iterable<IDoubleArray> data, IDataSequence centers, IMetric<IDoubleArray> metric, IIntArray assignment)
    {
        IDoubleArray D = clusterSizes(data, centers, metric, assignment);
        double[][] DBdist = new double[centers.size()][centers.size()];
        for (int i=0; i<centers.size()-1; i++)
        {
            for (int j=i+1; j<centers.size(); j++)
            {
                DBdist[i][j] = (D.get(i)+D.get(j))/(metric.distance(centers.get(i), centers.get(j)));
                System.out.println("d_i = "+D.get(i));
                System.out.println("d_j = "+D.get(j));
                System.out.println("d_ij = "+metric.distance(centers.get(i), centers.get(j)));
                System.out.println("DB = "+DBdist[i][j]);
                System.out.println();
            }
        }
        //System.exit(0);
        double res = 0;
        for (int i=0; i<DBdist.length; i++)
            res += doubleArrays.max(DBdist[i]);
        return (res/(double)centers.size());
    }
    
    public IDoubleArray membershipToState(IClustering crisp, int state)
    {
        IIntArray clusterIndexes = crisp.getClusterIndexes();
        IDoubleArray res = Doubles.create.array(clusterIndexes.size());
        for (int i=0; i<res.size(); i++)
        {
            if (clusterIndexes.get(i) == state)
                res.set(i, 1);
            else
                res.set(i, 0);
        }
        
        return res;
    }
}
