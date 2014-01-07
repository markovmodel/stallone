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
import stallone.api.doubles.Doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.coordinates.MinimalRMSDistance3D;
import stallone.datasequence.io.AsciiDataSequenceWriter;

/**
 *
 * @author noe
 */
public class CoordinateUtilities
{
    private MinimalRMSDistance3D minrmsd = null;
    private boolean fixedOutputPrecision = false;
    private int outputPrecisionPre = 5;
    private int outputPrecisionPost = 3;

    public IDataSequence transform_data(IDataSequence X, ICoordinateTransform T)
    {
        IDoubleArray[] res = new IDoubleArray[X.size()];
        for (int i=0; i<res.length; i++)
            res[i] = T.transform(X.get(i));
        return dataNew.array(res);
    }

    public void fixOutputPrecision(int pre, int post)
    {
        fixedOutputPrecision = true;
        outputPrecisionPre = pre;
        outputPrecisionPost = post;
    }

    public void fixOutputPrecision()
    {
        fixedOutputPrecision = false;
    }
    
    public void transform_file(String infile, ICoordinateTransform T, String outfile) 
            throws IOException
    {
        IDataReader reader = dataNew.reader(infile);
        int N = reader.size();
        IDataWriter writer = dataNew.writer(outfile, N, T.dimension());
        if (writer instanceof AsciiDataSequenceWriter && fixedOutputPrecision)
            ((AsciiDataSequenceWriter)writer).setFixedPrecision(outputPrecisionPre, outputPrecisionPost);
        for (IDoubleArray X : reader)
            writer.add(T.transform(X));
        writer.close();
    }
    
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
     * computes the upper half of a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param out will receive the linearized upper triangle of the distance 
     * matrix, i.e. (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public void distances(IDoubleArray x, int[] set, IDoubleArray out)
    {
        distances(x.getTable(), set, out.getArray());
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
     * computes a upper half of a (nxn) minimal distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set n indes sets
     * @param out will receive the linearized upper triangle of the distance 
     * matrix, i.e. (d11,...,d1n,d21,...,d2n-1,...,dnn-1)
     */
    public void distances(IDoubleArray x, int[][] set, IDoubleArray out)
    {
        distances(x.getTable(), set, out.getArray());
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
     * computes a (mxn) distance matrix between the m-sized distance group 1 and
     * the n-sized distance group 2.
     * @param x a Nx3 coordinate set
     * @param set1 a m-sized distance group
     * @param set1 a n-sized distance group
     * @param out mxn array to receive all distance pairs. If distanceGroup1 and 
     * distanceGroup2 have overlap in their indexes, this will have both
     * d_ij and d_ji and self-distances that are 0. I.e. the distance groups
     * are not analyzed for possible redundancies before computation.
     */
    public void distanceMatrix(IDoubleArray x, int[] set1, int[] set2, IDoubleArray out)
    {
        distanceMatrix(x.getTable(), set1, set2, out.getTable());
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
     * computes a (mxn) minimal distance matrix between the n index sets given.
     * d_i,j = min_k,l {dist(x[i][k], x[j][l])}  with i in set1 and j in set2
     * @param x a Nx3 coordinate set
     * @param set1 a mx* index array with n distance sets
     * @param set2 a nx* index array with n distance sets
     * @param out mxn array with all minimum distance pairs.
     */
    public void distanceMatrix(IDoubleArray x, int[][] set1, int[][] set2, IDoubleArray out)
    {
        distanceMatrix(x.getTable(), set1, set2, out.getTable());
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
        if (target.length != (n*(n-1))/2)
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
        if (target.length != (n*(n-1))/2)
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
                    for (int l=0; l<nj; l++)
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
                    for (int l=0; l<nj; l++)
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
                target[i][j] = doubleArrays.distance(x[set1[i]], x[set2[j]]);
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
        for (int i=0; i<m; i++)
        {
            for (int j=0; j<n; j++)
            {
                double dmin = Double.POSITIVE_INFINITY;
                int ni = set1[i].length;
                int nj = set2[j].length;
                for (int k=0; k<ni; k++)
                {
                    for (int l=0; l<nj; l++)
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
    
    private boolean degrees = true;
    
    /**
     * When called, subsequent angle computations will be made in degrees
     * (default).
     */
    public void anglesInDegrees()
    {
        degrees = true;
    }

    /**
     * When called, subsequent angle computations will be made in radians.
     */
    public void anglesInRadians()
    {
        degrees = false;
    }
    
    /**
     * Computes a set of angles or dihedral angles.
     * @param x the coordinate set
     * @param selection a sequence of triples or quadruples. For each triple,
     * the corresponding angle will be computed. For each quadruple, the 
     * corresponding dihedral (torsion) angle will be computed.
     */
    public IDoubleArray angles(IDoubleArray x, int[][] selection)
    {
        double[] res = new double[selection.length];
        angles(x.getTable(), selection, res);
        return doublesNew.array(res);
    }
    
    /**
     * Computes a set of angles or dihedral angles.
     * @param x the coordinate set
     * @param selection a sequence of triples or quadruples. For each triple,
     * the corresponding angle will be computed. For each quadruple, the 
     * corresponding dihedral (torsion) angle will be computed.
     * @param out the target array into which angles will be written.
     */
    public void angles(double[][] x, int[][] selection, double[] out)
    {
        for (int i=0; i<selection.length; i++)
        {
            if (selection[i].length == 3)
            {
                if (degrees)
                    out[i] = coor3d.angleDeg(x[selection[i][0]], x[selection[i][1]], x[selection[i][2]]);
                else
                    out[i] = coor3d.angleRad(x[selection[i][0]], x[selection[i][1]], x[selection[i][2]]);
            }
            else if (selection[i].length == 4)
            {
                if (degrees)
                    out[i] = coor3d.torsionDeg(x[selection[i][0]], x[selection[i][1]], x[selection[i][2]], x[selection[i][3]]);
                else
                    out[i] = coor3d.torsionRad(x[selection[i][0]], x[selection[i][1]], x[selection[i][2]], x[selection[i][3]]);
            }
            else
                throw new IllegalArgumentException("found a selection with "+selection[i].length+" elements. Can only handle 3 (angles) and 4 (torsions)");
        }
    }

    public void select(IDoubleArray in, int[] selection, IDoubleArray out)
    {
        for (int i=0; i<selection.length; i++)
        {
            for (int j=0; j<in.columns(); j++)
                out.set(i,j, in.get(selection[i],j));
        }
    }

    
}
