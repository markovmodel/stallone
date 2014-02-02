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
import stallone.doubles.DenseDoubleArray;
import stallone.util.MathTools;

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

    /**
     * Apply a coordinate transform to all coordinates in a file and write the
     * result to an output file.
     * @param infile
     * @param T
     * @param outfile
     * @throws IOException 
     */
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
    
    
    private double dotprod(IDoubleArray X, int i, int j)
    {
        return (X.get(i,0) * X.get(j,0) + X.get(i,1) * X.get(j,1) + X.get(i,2) * X.get(j,2));
    }

    // for internal use
    private double[] v1 = {0,0,0};
    private double[] v2 = {0,0,0};
    
    
    /**
     * Computes the connection vector from atom i to atom j v_ij
     * @param X Nx3 coordinate set
     * @param i atom index i
     * @param j atom index j
     * @param v length 3 array
     * @return 
     */
    public void vector(IDoubleArray X, int i, int j, double[] v)
    {
        v[0] = X.get(j,0) - X.get(i,0);
        v[1] = X.get(j,1) - X.get(i,1);
        v[2] = X.get(j,2) - X.get(i,2);
    }

    /**
     * Computes the connection vector from atom i to atom j v_ij
     * @param X Nx3 coordinate set
     * @param i atom index i
     * @param j atom index j
     * @return 
     */
    public double[] vector(IDoubleArray X, int i, int j)
    {
        double[] v = new double[3];
        vector(X,i,j,v);
        return v;
    }
    
    /** returns distance between two points*/
    public double squaredistance(IDoubleArray X, int i, int j)
    {
        vector(X,i,j,v1);
        return v1[0]*v1[0] + v1[1]*v1[1] + v1[2]*v1[2];
    }

    /** returns distance between two points*/
    public double distance(IDoubleArray X, int i, int j)
    {
        return(Math.sqrt(squaredistance(X,i,j)));
    }
    
    /** compute angle between three points p1,p2,p3
    as angle between p2->p1 and p2->p3*/
    public double angleRad(IDoubleArray X, int i, int j, int k)
    {
        vector(X,j,i,v1);
        vector(X,j,k,v2);
        return coor3d.angleRad(v1, v2);
    }

    private double rad2deg = 180.0 / Math.PI;
    /** compute angle between three points p1,p2,p3
    as angle between p2->p1 and p2->p3*/
    public double angleDeg(IDoubleArray X, int i, int j, int k)
    {
        return rad2deg * angleRad(X,i,j,k);
    }

    /** torsion angle by 3-dimensional coordinates in radians!*/
    public double torsionRad(IDoubleArray X, int p1, int p2, int p3, int p4)
    {
        vector(X,p2,p1,v1);
        vector(X,p2,p3,v2);
        double[] cv1 = coor3d.crossprod(v1, v2);
        vector(X,p3,p2,v1);
        vector(X,p3,p4,v2);
        double[] cv2 = coor3d.crossprod(v1, v2);
        double angle = coor3d.angleRad(cv1, cv2);
        if (coor3d.dotprod(v2, cv1) > 0)
        {
            angle = -angle;
        }
        return (angle);
    }

    /** torsion angle by 3-dimensional coordinates in degrees!*/
    public double torsionDeg(IDoubleArray X, int p1, int p2, int p3, int p4)
    {
        return (rad2deg*torsionRad(X,p1, p2, p3, p4));
    }
    
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
        IDoubleArray res = doublesNew.array((set.length*(set.length-1))/2);
        distances(x, set, res);
        return res;
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
        int n = set.length;
        if (out.size() != (n*(n-1))/2)
            throw new IllegalArgumentException("target array has illegal size "+out.size()+". requiring "+((n*n-1)/2));
        int p = 0;
        for (int i=0; i<n-1; i++)
            for (int j=i+1; j<n; j++)
            {
                out.set(p++, distance(x, set[i], set[j]));
            }
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
        IDoubleArray res = doublesNew.array((set.length*(set.length-1))/2);
        distances(x, set, res);
        return res;
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
        int n = set.length;
        if (out.size() != (n*(n-1))/2)
            throw new IllegalArgumentException("target array has illegal size "+out.size()+". requiring "+((n*n-1)/2));
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
                        double d = distance(x, set[i][k], set[j][l]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                out.set(p++, dmin);
            }
        }
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
        IDoubleArray res = doublesNew.array(set.length,set.length);
        distanceMatrix(x, set, res);
        return res;
    }

    /**
     * computes a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param target a nxn array to be filled with the result
     */
    public void distanceMatrix(IDoubleArray x, int[] set, IDoubleArray target)
    {
        int n = set.length;
        if (target.rows() != n && target.columns() != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.rows()+","+target.columns()+"). requiring ("+n+","+n+")");
        for (int i=0; i<n-1; i++)
            for (int j=i+1; j<n; j++)
            {
                double dij = distance(x, set[i], set[j]);
                target.set(i,j,dij);
                target.set(j,i,dij);
            }
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
        IDoubleArray res = doublesNew.array(set.length, set.length);
        distanceMatrix(x, set, res);
        return res;
    }

    
    /**
     * computes a (nxn) distance matrix between the indexes of the 
     * n-sized distance set.
     * @param x a Nx3 coordinate set
     * @param set a n-sized index set
     * @param target a nxn array to be filled with the result
     */
    public void distanceMatrix(IDoubleArray x, int[][] set, IDoubleArray target)
    {
        int n = set.length;
        if (target.rows() != n && target.columns() != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.rows()+","+target.columns()+"). requiring ("+n+","+n+")");
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
                        double d = distance(x,set[i][k], set[j][l]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                target.set(i,j,dmin);
                target.set(j,i,dmin);
            }
        }
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
        IDoubleArray res = doublesNew.array(set1.length,set2.length);
        distanceMatrix(x, set1, set2, res);
        return res;
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
    public void distanceMatrix(IDoubleArray x, int[] set1, int[] set2, IDoubleArray target)
    {
        int m = set1.length;
        int n = set2.length;
        if (target.rows() != m && target.columns() != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.rows()+","+target.columns()+"). requiring ("+m+","+n+")");
        for (int i=0; i<m; i++)
            for (int j=0; j<n; j++)
                target.set(i,j,distance(x, set1[i], set2[j]));
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
        IDoubleArray res = doublesNew.array(set1.length,set2.length);
        distanceMatrix(x, set1, set2, res);
        return res;
    }
    

    /**
     * computes a (mxn) minimal distance matrix between the n index sets given.
     * d_i,j = min_k,l {dist(x[i][k], x[j][l])}  with i in set1 and j in set2
     * @param x a Nx3 coordinate set
     * @param set1 a mx* index array with n distance sets
     * @param set2 a nx* index array with n distance sets
     * @param out mxn array with all minimum distance pairs.
     */
    public void distanceMatrix(IDoubleArray x, int[][] set1, int[][] set2, IDoubleArray target)
    {
        int m = set1.length;
        int n = set2.length;
        if (target.rows() != m && target.columns() != n)
            throw new IllegalArgumentException("target array has illegal size ("+target.rows()+","+target.columns()+"). requiring ("+m+","+n+")");
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
                        double d = distance(x, set1[i][k], set2[j][l]);
                        if (d < dmin)
                            dmin = d;
                    }
                }
                target.set(i,j, dmin);
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
        IDoubleArray res = doublesNew.array(selection.length);
        angles(x, selection, res);
        return res;
    }
    
    /**
     * Computes a set of angles or dihedral angles.
     * @param x the coordinate set
     * @param selection a sequence of triples or quadruples. For each triple,
     * the corresponding angle will be computed. For each quadruple, the 
     * corresponding dihedral (torsion) angle will be computed.
     * @param out the target array into which angles will be written.
     */
    public void angles(IDoubleArray x, int[][] selection, IDoubleArray out)
    {
        for (int i=0; i<selection.length; i++)
        {
            if (selection[i].length == 3)
            {
                if (degrees)
                    out.set(i, angleDeg(x, selection[i][0], selection[i][1], selection[i][2]));
                else
                    out.set(i, angleRad(x, selection[i][0], selection[i][1], selection[i][2]));
            }
            else if (selection[i].length == 4)
            {
                if (degrees)
                    out.set(i, torsionDeg(x, selection[i][0], selection[i][1], selection[i][2], selection[i][3]));
                else
                    out.set(i, torsionRad(x, selection[i][0], selection[i][1], selection[i][2], selection[i][3]));
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
