/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import java.io.IOException;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataWriter;

import stallone.api.doubles.IDoubleArray;
import stallone.coordinates.MinimalRMSDistance3D;

/**
 *
 * @author noe
 */
public class CoordinateUtilities
{
    MinimalRMSDistance3D minrmsd = null;

    /**
     * Computes the minimal root mean square distance between x1 and x2.
     * I.e. the result is minrmsd(x1,x2) = sqrt(|x1-x2'|^2 / N), 
     * where x2' is a x2 aligned to x1, such minrmsd(x1,x2) is minimal.
     * Based on: Douglas L. Theobald Rapid calculation of RMSDs using a 
     * quaternion-based characteristic polynomial Acta Crystallographica Section A, 
     * Foundations of Crystallography ISSN 0108-7673 Department of Chemistry
     * and Biochemistry, University of Colorado at Boulder, 
     * Boulder, CO 80309-0215, USA. Correspondence e-mail: theobal@colorado.edu
     * @param x1 coordinate set 1, a Nx3 matrix
     * @param x2 coordinate set 2, a Nx3 matrix
     * @return minrmsd(x1,x2)
     */
    public double minRMSD(IDoubleArray x1, IDoubleArray x2)
    {
        int N = x1.rows();
        if (minrmsd == null)
            minrmsd = new MinimalRMSDistance3D(N);
        if (minrmsd.getN() != N)
            minrmsd = new MinimalRMSDistance3D(N);
        return minrmsd.distance(x1, x2);
    }

    
    /**
     * calls distanceMatrix(x,set) on every data set in the reader and writes it to the writer
     * @param in a data sequence, e.g. a IDataReader on a file, or a data sequence in memory
     * @param set a n-sized index set
     * @param out a data writer
     */
    public void convertToDistances(String infile, String outfile, int[] set) 
            throws IOException
    {
        IDataReader reader = dataNew.dataSequenceLoader(infile);
        int N = reader.size();
        int dout = (set.length*(set.length-1))/2;
        IDataWriter writer = dataNew.createDataWriter(outfile, N, dout);
        for (IDoubleArray x : reader)
        {
            IDoubleArray y = distanceMatrix(x, set);
            writer.add(y);
        }
        writer.close();
        reader.close();
    }

    /**
     * calls distanceMatrix(x,set) on every data set in the reader and writes it to the writer
     * @param in a data sequence, e.g. a IDataReader on a file, or a data sequence in memory
     * @param set a n-sized index set
     * @param out a data writer
     */
    public void convertToDistances(String infile, String outfile, int[][] set) 
            throws IOException
    {
        IDataReader reader = dataNew.dataSequenceLoader(infile);
        int N = reader.size();
        int dout = (set.length*(set.length-1))/2;
        IDataWriter writer = dataNew.createDataWriter(outfile, N, dout);
        for (IDoubleArray x : reader)
        {
            IDoubleArray y = distanceMatrix(x, set);
            writer.add(y);
        }
        writer.close();
        reader.close();
    }

    /**
     * calls distanceMatrix(x,set1,set2) on every data set in the reader and writes it to the writer
     * @param in a data sequence, e.g. a IDataReader on a file, or a data sequence in memory
     * @param set a n-sized index set
     * @param out a data writer
     */
    public void convertToDistances(String infile, String outfile, int[] set1, int[] set2) 
            throws IOException
    {
        IDataReader reader = dataNew.dataSequenceLoader(infile);
        int N = reader.size();
        int dout = set1.length*set2.length;
        IDataWriter writer = dataNew.createDataWriter(outfile, N, dout);
        for (IDoubleArray x : reader)
        {
            IDoubleArray y = distanceMatrix(x, set1, set2);
            writer.add(y);
        }
        writer.close();
        reader.close();
    }
    

    /**
     * calls distanceMatrix(x,set1,set2) on every data set in the reader and writes it to the writer
     * @param in a data sequence, e.g. a IDataReader on a file, or a data sequence in memory
     * @param set a n-sized index set
     * @param out a data writer
     */
    public void convertToDistances(String infile, String outfile, int[][] set1, int[][] set2) 
            throws IOException
    {
        IDataReader reader = dataNew.dataSequenceLoader(infile);
        int N = reader.size();
        int dout = set1.length*set2.length;
        IDataWriter writer = dataNew.createDataWriter(outfile, N, dout);
        for (IDoubleArray x : reader)
        {
            IDoubleArray y = distanceMatrix(x, set1, set2);
            writer.add(y);
        }
        writer.close();
        reader.close();
    }
    
    
    /**
     * computes the upper half of a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @returns the linearized upper triangle of the distance matrix, i.e.
     * (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public IDoubleArray distances(IDoubleArray x, int[] set)
    {
        double[] res = new double[(set.length*(set.length-1))/2];
        distances(x.getTable(), set, res);
        return doublesNew.array(res);
    }
    
    /**
     * computes a upper half of a (nxn) minimal distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set n indes sets
     * @returns the linearized upper triangle of the distance matrix, i.e.
     * (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public IDoubleArray distances(IDoubleArray x, int[][] set)
    {
        double[] res = new double[(set.length*(set.length-1))/2];
        distances(x.getTable(), set, res);
        return doublesNew.array(res);
    }
    

    /**
     * computes a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @return nxn array with all distance pairs. 
     */
    public IDoubleArray distanceMatrix(IDoubleArray x, int[] set)
    {
        double[][] res = new double[set.length][set.length];
        distanceMatrix(x.getTable(), set, res);
        return doublesNew.array(res);
    }
    
    
    /**
     * computes a (nxn) minimal distance matrix between the n index sets given.
     * d_i,j = min_k,l {dist(x[i][k], x[j][l])}  with i,j in set
     * @param x a Nx3 coordinate set
     * @param set a nx* index array with n distance sets
     * @return nxn array with all minimum distance pairs.
     */
    public IDoubleArray distanceMatrix(IDoubleArray x, int[][] set)
    {
        double[][] res = new double[set.length][set.length];
        distanceMatrix(x.getTable(), set, res);
        return doublesNew.array(res);
    }

    
    /**
     * computes a (mxn) distance matrix between the m-sized distance group 1 and
     * the n-sized distance group 2.
     * @param x a Nx3 coordinate set
     * @param set1 a m-sized distance group
     * @param set1 a n-sized distance group
     * @return mxn array with all distance pairs. If distanceGroup1 and 
     * distanceGroup2 have overlap in their indexes, this will have both
     * d_ij and d_ji and self-distances that are 0. I.e. the distance groups
     * are not analyzed for possible redundancies before computation.
     */
    public IDoubleArray distanceMatrix(IDoubleArray x, int[] set1, int[] set2)
    {
        double[][] res = new double[set1.length][set2.length];
        distanceMatrix(x.getTable(), set1, set2, res);
        return doublesNew.array(res);
    }

    
    /**
     * computes a (mxn) minimal distance matrix between the n index sets given.
     * d_i,j = min_k,l {dist(x[i][k], x[j][l])}  with i in set1 and j in set2
     * @param x a Nx3 coordinate set
     * @param set1 a mx* index array with n distance sets
     * @param set2 a nx* index array with n distance sets
     * @return nxn array with all minimum distance pairs.
     */
    public IDoubleArray distanceMatrix(IDoubleArray x, int[][] set1, int[][] set2)
    {
        double[][] res = new double[set1.length][set2.length];
        distanceMatrix(x.getTable(), set1, set2, res);
        return doublesNew.array(res);
    }
    
    
    /**
     * computes the upper half of a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param target the linearized upper triangle of the distance matrix, i.e.
     * (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public void distances(double[][] x, int[] set, double[] target)
    {
        int n = set.length;
        if (target.length != (n*n-1)/2)
            throw new IllegalArgumentException("target array has illegal size "+target.length+". requiring "+((n*n-1)/2));
        int p = 0;
        for (int i=0; i<n-1; i++)
            for (int j=i+1; j<n; j++)
            {
                target[p++] = doubleArrays.distance(x[set[i]], x[set[j]]);
            }
    }

    /**
     * computes a upper half of a (nxn) minimal distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set n indes sets
     * @param target the linearized upper triangle of the distance matrix, i.e.
     * (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public void distances(double[][] x, int[][] set, double[] target)
    {
        int n = set.length;
        if (target.length != (n*n-1)/2)
            throw new IllegalArgumentException("target array has illegal size "+target.length+". requiring "+((n*n-1)/2));
        int p = 0;
        for (int i=0; i<n-1; i++)
        {
            for (int j=i+1; j<n; j++)
            {
                double dmin = Double.POSITIVE_INFINITY;
                int ni = set[i].length;
                int nj = set[j].length;
                for (int k=0; k<ni; k++)
                {
                    for (int l=0; l<nj; k++)
                    {
                        double d = doubleArrays.distance(x[set[i][k]], x[set[j][l]]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                target[p++] = dmin;
            }
        }
    }
    
    
    /**
     * computes a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param target a nxn array to be filled with the result
     */
    public void distanceMatrix(double[][] x, int[] set, double[][] target)
    {
        int n = set.length;
        if (target.length != n && target[0].length != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.length+","+target[0].length+"). requiring ("+n+","+n+")");
        for (int i=0; i<n-1; i++)
            for (int j=i+1; j<n; j++)
            {
                double dij = doubleArrays.distance(x[set[i]], x[set[j]]);
                target[i][j] = dij;
                target[j][i] = dij;
            }
    }

    /**
     * computes a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param target a nxn array to be filled with the result
     */
    public void distanceMatrix(double[][] x, int[][] set, double[][] target)
    {
        int n = set.length;
        if (target.length != n && target[0].length != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.length+","+target[0].length+"). requiring ("+n+","+n+")");
        for (int i=0; i<n-1; i++)
        {
            for (int j=i+1; j<n; j++)
            {
                double dmin = Double.POSITIVE_INFINITY;
                int ni = set[i].length;
                int nj = set[j].length;
                for (int k=0; k<ni; k++)
                {
                    for (int l=0; l<nj; k++)
                    {
                        double d = doubleArrays.distance(x[set[i][k]], x[set[j][l]]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                target[i][j] = dmin;
                target[j][i] = dmin;
            }
        }
    }
    
    /**
     * computes a (mxn) distance matrix between the m-sized distance group 1 and
     * the n-sized distance group 2.
     * @param x a Nx3 coordinate set
     * @param set1 a m-sized distance group
     * @param set2 a n-sized distance group
     * @return mxn array with all distance pairs. If distanceGroup1 and 
     * distanceGroup2 have overlap in their indexes, this will have both
     * d_ij and d_ji and self-distances that are 0. I.e. the distance groups
     * are not analyzed for possible redundancies before computation.
     */
    public void distanceMatrix(double[][] x, int[] set1, int[] set2, double[][] target)
    {
        int m = set1.length;
        int n = set2.length;
        if (target.length != m && target[0].length != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.length+","+target[0].length+"). requiring ("+m+","+n+")");
        for (int i=0; i<m; i++)
            for (int j=0; j<n; j++)
                target[i][j] = doubleArrays.distance(x[set1[i]], x[set2[i]]);
    }

    /**
     * computes a (mxn) distance matrix between the indexes of the 
     * m-sized distance set 1 and the n-sized distance set 2.
     * @param x a Nx3 coordinate set
     * @param set1 a m-sized index set
     * @param set2 a n-sized index set
     * @param target a nxn array to be filled with the result
     */
    public void distanceMatrix(double[][] x, int[][] set1, int[][] set2, double[][] target)
    {
        int m = set1.length;
        int n = set2.length;
        if (target.length != m && target[0].length != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.length+","+target[0].length+"). requiring ("+m+","+n+")");
        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {
                double dmin = Double.POSITIVE_INFINITY;
                int ni = set1[i].length;
                int nj = set2[j].length;
                for (int k=0; k<ni; k++)
                {
                    for (int l=0; l<nj; k++)
                    {
                        double d = doubleArrays.distance(x[set1[i][k]], x[set2[j][l]]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                target[i][j] = dmin;
            }
        }
    }
    
}
