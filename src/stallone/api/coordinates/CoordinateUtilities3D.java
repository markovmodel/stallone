/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.util.Arguments;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class CoordinateUtilities3D
{

    static long t;

    /** copies the contents of v1 into v2
     */
    public static void copy(double[] v1, double[] v2)
    {
        v2[0] = v1[0];
        v2[1] = v1[1];
        v2[2] = v1[2];
    }

    /** adds two vectors
     */
    public static double[] add(double[] v1, double[] v2)
    {
        double[] v = new double[3];
        v[0] = v1[0] + v2[0];
        v[1] = v1[1] + v2[1];
        v[2] = v1[2] + v2[2];
        return (v);
    }

    /** increments vector v1 by v2
     */
    public static void increment(double[] v1, double[] v2)
    {
        v1[0] += v2[0];
        v1[1] += v2[1];
        v1[2] += v2[2];
    }

    /** decrements vector v1 by v2
     */
    public static void decrement(double[] v1, double[] v2)
    {
        v1[0] -= v2[0];
        v1[1] -= v2[1];
        v1[2] -= v2[2];
    }

    /** computes the vector a*v1 + b*v2
     */
    public static double[] addweighted(double a, double[] v1,
            double b, double[] v2)
    {
        double[] v = new double[3];
        v[0] = a * v1[0] + b * v2[0];
        v[1] = a * v1[1] + b * v2[1];
        v[2] = a * v1[2] + b * v2[2];
        return (v);
    }

    /** subtracts two vectors v1-v2
     */
    public static double[] subtract(double[] v1, double[] v2)
    {
        double[] v = new double[3];
        v[0] = v1[0] - v2[0];
        v[1] = v1[1] - v2[1];
        v[2] = v1[2] - v2[2];
        return (v);
    }

    /** subtracts two vectors v1-v2, writes into res*/
    public static void subtract2(double[] v1, double[] v2, double[] res)
    {
        res[0] = v1[0] - v2[0];
        res[1] = v1[1] - v2[1];
        res[2] = v1[2] - v2[2];
    }

    /** returns a*v, where v is a 3-dimensional vector*/
    public static double[] stretch(double a, double[] v)
    {
        double[] v1 = new double[3];
        v1[0] = a * v[0];
        v1[1] = a * v[1];
        v1[2] = a * v[2];
        return (v1);
    }

    /** computes the dot product v1*v2.*/
    public static double dotprod(double[] v1, double[] v2)
    {
        return (v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2]);
    }

    public static double overlap(double[][] V1, double[][] V2)
    {
        double o = 0;
        for (int i = 0; i < V1.length; i++)
        {
            for (int j = 0; j < V2.length; j++)
            {
                o += Math.pow(doubleArrays.dot(normalize(V1[i]),
                        normalize(V2[j])), 2.0);
            }
        }
        return (o);
    }

    /**
    Determines the dimensionality of overlap between two spaces spanned
    by the vectors V1 and V2, compared to the larger of the two dimensions
    1/max(|V1|,|V2|) * sum_V1 sum_V2 dot(v1,v2)
    which is 0 for orthogonal and 1 for coplanar spaces.
     */
    public static double subspaceOverlap(double[][] V1, double[][] V2)
    {
        return (overlap(V1, V2) / Math.max(V1.length, V2.length));
    }

    /**
    Determines the dimensionality of overlap between two spaces spanned
    by the vectors V1 and V2, compared to the smaller of the two dimensions
    1/min(|V1|,|V2|) * sum_V1 sum_V2 dot(v1,v2)
    which is 0 for orthogonal and 1 for coplanar spaces.
     */
    public static double superspaceOverlap(double[][] V1, double[][] V2)
    {
        return (overlap(V1, V2) / Math.min(V1.length, V2.length));
    }

    /** general cross product function for vectors in three dimensions
     */
    public static double[] crossprod(double[] v1, double[] v2)
    {
        double[] v = new double[3];
        v[0] = v1[1] * v2[2] - v1[2] * v2[1];
        v[1] = v1[2] * v2[0] - v1[0] * v2[2];
        v[2] = v1[0] * v2[1] - v1[1] * v2[0];
        return (v);
    }

    /**
    computes the unit normal vector on the plane p1p2p3 as
    normalize(p2p1 x p2p3)
     */
    public static double[] normal0(double[] p1, double[] p2, double[] p3)
    {
        double[] v21 = subtract(p1, p2);
        double[] v23 = subtract(p3, p2);
        return (normalize(crossprod(v21, v23)));
    }

    /** returns distance between two points*/
    public static double squaredistance(double[] v1, double[] v2)
    {
        double dx = v1[0] - v2[0];
        double dy = v1[1] - v2[1];
        double dz = v1[2] - v2[2];
        return (dx * dx + dy * dy + dz * dz);
    }

    /** returns distance between two points*/
    public static double distance(double[] v1, double[] v2)
    {
        double dx = v1[0] - v2[0];
        double dy = v1[1] - v2[1];
        double dz = v1[2] - v2[2];
        return (Math.sqrt(dx * dx + dy * dy + dz * dz));
    }

    public static double[] distances(double[][] arr, int[][] partners)
    {
        double[] res = new double[partners.length];
        for (int i = 0; i < res.length; i++)
        {
            res[i] = distance(arr[partners[i][0]], arr[partners[i][1]]);
        }
        return (res);
    }

    public static double distancePointSurface(double[] o, double[] surfv1, double[] surfv2, double[] p)
    {
        double[] n = crossprod(surfv1, surfv2);
        double[][] M =
        {
            doubleArrays.normalize(surfv1),
            doubleArrays.normalize(surfv2),
            doubleArrays.normalize(n)
        };
        M = doubleArrays.transpose(M);
        IDoubleArray arrM = doublesNew.array(M);
        double[] o_new = alg.solve(arrM, doublesNew.array(o)).getArray();
        double[] p_new = alg.solve(arrM, doublesNew.array(p)).getArray();
        double[] x = doubleArrays.subtract(p_new, o_new);
        return (Math.abs(x[2]));
    }

    /** general function to compute angle between two vectors*/
    public static double angleRad(double[] v1, double[] v2)
    {
        double sqnorm = Math.sqrt(dotprod(v1, v1) * dotprod(v2, v2));
        if (sqnorm == 0)
        {
            return (0.0);
        }
        else
        {
            return (MathTools.acos(MathTools.bound(dotprod(v1, v2) / sqnorm, -1, 1)));
        }
    }


    /** general function to compute angle between two vectors in degrees
     */
    public static double angleDeg(double[] v1, double[] v2)
    {
        double a = MathTools.acos(dotprod(v1, v2) / (norm(v1) * norm(v2)));
        return (180.0 * a / Math.PI);
    }

    /** compute angle between three points p1,p2,p3
    as angle between p2->p1 and p2->p3*/
    public static double angleRad(double[] p1, double[] p2, double[] p3)
    {
        double[] v1 =
        {
            p1[0] - p2[0], p1[1] - p2[1], p1[2] - p2[2]
        };
        double[] v2 =
        {
            p3[0] - p2[0], p3[1] - p2[1], p3[2] - p2[2]
        };
        double result = angleRad(v1, v2);
        return (result);
    }

    /** compute angle between three points p1,p2,p3
    as angle between p2->p1 and p2->p3*/
    public static double angleDeg(double[] p1, double[] p2, double[] p3)
    {
        double[] v1 =
        {
            p1[0] - p2[0], p1[1] - p2[1], p1[2] - p2[2]
        };
        double[] v2 =
        {
            p3[0] - p2[0], p3[1] - p2[1], p3[2] - p2[2]
        };
        double result = angleDeg(v1, v2);
        return (result);
    }

    /** torsion angle by 3-dimensional coordinates in radians!*/
    public static double torsionRad(double[] p1, double[] p2,
            double[] p3, double[] p4)
    {
        double[] v21 = subtract(p1, p2);
        double[] v23 = subtract(p3, p2);
        double[] cv1 = crossprod(v21, v23);
        double[] v32 = subtract(p2, p3);
        double[] v34 = subtract(p4, p3);
        double[] cv2 = crossprod(v32, v34);
        double angle = angleRad(cv1, cv2);
        if (dotprod(v34, cv1) > 0)
        {
            angle = -angle;
        }
        return (angle);
    }

    /** torsion angle by 3-dimensional coordinates in degrees!*/
    public static double torsionDeg(double[] p1, double[] p2,
            double[] p3, double[] p4)
    {
        return (180.0 * torsionRad(p1, p2, p3, p4) / Math.PI);
    }

    public static double torsionDeg(double[][] crd,
            int i1, int i2, int i3, int i4)
    {
        return (torsionDeg(crd[i1], crd[i2], crd[i3], crd[i4]));
    }

    public static double torsionDeg(double[][] crd, int[] indexes)
    {
        return (torsionDeg(crd[indexes[0]], crd[indexes[1]],
                crd[indexes[2]], crd[indexes[3]]));
    }

    public static double[] torsionsDeg(double[][] crd, int[][] indexes)
    {
        double[] res = new double[indexes.length];
        for (int i = 0; i < res.length; i++)
        {
            res[i] = torsionDeg(crd[indexes[i][0]], crd[indexes[i][1]],
                    crd[indexes[i][2]], crd[indexes[i][3]]);
        }
        return (res);
    }

    /**
    For a planar triangle 1,2,3, computes d13, based on d12,d23 and
    theta23 = angle(d12,d23) using the cosine theorem, i.e.:
    d13^2 = d12^2 + d23^2 + d12 d23 cos(theta23)
    @param theta23 the angle between d12, d23 in radians.
    @return c^2 (not c !!!!)
     */
    public static double cosineTheorem(double d12, double d23, double theta123)
    {
        return (d12 * d12 + d23 * d23 - 2 * d12 * d23 * Math.cos(theta123));
    }

    /**
    Computes the 1-4 distance in an arbitrary (nonplanar) quadrangle,
    given d12, d13, d14, theta123 = angle(1,2,3), theta234 = angle(2,3,4)
    and phi = angle(plane(1,2,3), plane(2,3,4)).
    @param theta123 the angle between 1,2,3 in radians.
    @param theta234 the angle between 2,3,4 in radians.
    @param phi the angle between the planes (1,2,3) and (2,3,4), in radians
    @return d14
     */
    public static double dist14(double d12, double d23, double d34,
            double theta123, double theta234, double phi)
    {
        double alpha123 = Math.PI - theta123;
        double alpha234 = Math.PI - theta234;
        double a1 = Math.cos(alpha123) * d12;
        double a2 = Math.sin(alpha123) * d12;
        double b1 = Math.cos(alpha234) * d34;
        double b2 = Math.sin(alpha234) * d34;
        double c = a1 + d23 + b1;
        double e_sq = cosineTheorem(a2, b2, phi);
        return (Math.sqrt(c * c + e_sq));
    }

    /** returns the norm of the vector*/
    public static double norm(double[] v)
    {
        return (Math.sqrt(dotprod(v, v)));
    }

    /** returns vector v normalized */
    public static double[] normalize(double[] v)
    {
        double[] v0 = new double[3];
        double n = norm(v);
        v0[0] = v[0] / n;
        v0[1] = v[1] / n;
        v0[2] = v[2] / n;
        return (v0);
    }

    /** Translate the coordinate set by [x,y,z]. */
    public static void translate(double[][] crds,
            double x, double y, double z)
    {
        for (int i = 0; i < crds.length; i++)
        {
            crds[i][0] = crds[i][0] + x;
            crds[i][1] = crds[i][1] + y;
            crds[i][2] = crds[i][2] + z;
        }
    }

    /** Translate some selected coordinates out of crds by [x,y,z]
    crds: the coordinate array
    select: a list with the point indexes which are to be translated
     */
    public static void translate(double[][] crds, int[] select,
            double x, double y, double z)
    {
        for (int i = 0; i < select.length; i++)
        {
            crds[select[i]][0] = crds[select[i]][0] + x;
            crds[select[i]][1] = crds[select[i]][1] + y;
            crds[select[i]][2] = crds[select[i]][2] + z;
        }
    }

    /** Computes a 3x3 rotation matrix for a rotation around the
    3D-vector r clockwise by an angle psi, which is returned
    explicitly in the matrix expression.
    psi: angle in radians
     */
    public static double[][] rotationMatrix(double[] r, double psi)
    {
        double[] r0 = normalize(r);
        double[][] Mr = new double[3][3];
        double cospsi = Math.cos(psi);
        double sinpsi = Math.sin(psi);
        double one_cospsi = 1 - cospsi;

        Mr[0][0] = r0[0] * r0[0] * one_cospsi + cospsi;
        Mr[0][1] = r0[0] * r0[1] * one_cospsi - r0[2] * sinpsi;
        Mr[0][2] = r0[0] * r0[2] * one_cospsi + r0[1] * sinpsi;
        Mr[1][0] = r0[0] * r0[1] * one_cospsi + r0[2] * sinpsi;
        Mr[1][1] = r0[1] * r0[1] * one_cospsi + cospsi;
        Mr[1][2] = r0[1] * r0[2] * one_cospsi - r0[0] * sinpsi;
        Mr[2][0] = r0[0] * r0[2] * one_cospsi - r0[1] * sinpsi;
        Mr[2][1] = r0[1] * r0[2] * one_cospsi + r0[0] * sinpsi;
        Mr[2][2] = r0[2] * r0[2] * one_cospsi + cospsi;
        return (Mr);
    }

    /**
    multiplies two 3x3 - matrixes
    NOT TESTED!!
     */
    public static double[][] matrixMultiply(double[][] M1, double[][] M2)
    {
        double[][] M = new double[3][3];
        M[0][0] = M1[0][0] * M2[0][0] + M1[0][1] * M2[1][0] + M1[0][2] * M2[2][0];
        M[0][1] = M1[0][0] * M2[0][1] + M1[0][1] * M2[1][1] + M1[0][2] * M2[2][1];
        M[0][2] = M1[0][0] * M2[0][2] + M1[0][1] * M2[1][2] + M1[0][2] * M2[2][2];
        M[1][0] = M1[1][0] * M2[0][0] + M1[1][1] * M2[1][0] + M1[1][2] * M2[2][0];
        M[1][1] = M1[1][0] * M2[0][1] + M1[1][1] * M2[1][1] + M1[1][2] * M2[2][1];
        M[1][2] = M1[1][0] * M2[0][2] + M1[1][1] * M2[1][2] + M1[1][2] * M2[2][2];
        M[2][0] = M1[2][0] * M2[0][0] + M1[2][1] * M2[1][0] + M1[2][2] * M2[2][0];
        M[2][1] = M1[2][0] * M2[0][1] + M1[2][1] * M2[1][1] + M1[2][2] * M2[2][1];
        M[2][2] = M1[2][0] * M2[0][2] + M1[2][1] * M2[1][2] + M1[2][2] * M2[2][2];
        return (M);
    }

    /**
    computes the 3x3 matrix determinant
    NOT TESTED!!
     */
    public static double det(double[][] M)
    {
        double res =
                M[0][0] * M[1][1] * M[2][2]
                + M[0][1] * M[1][2] * M[2][0]
                + M[0][2] * M[1][0] * M[2][1]
                - M[0][2] * M[1][1] * M[2][0]
                - M[0][1] * M[1][0] * M[2][2]
                - M[0][0] * M[1][2] * M[2][1];
        return (res);
    }

    /**
    applies the matrix M to all selected coordinates
    NOT TESTED!!
     */
    public static void applyMatrix(double[][] crds, double[][] M,
            int[] selection)
    {
        for (int i = 0; i < selection.length; i++)
        {
            crds[selection[i]][0] =
                    crds[selection[i]][0] * M[0][0]
                    + crds[selection[i]][1] * M[0][1]
                    + crds[selection[i]][2] * M[0][2];
            crds[selection[i]][1] =
                    crds[selection[i]][0] * M[1][0]
                    + crds[selection[i]][1] * M[1][1]
                    + crds[selection[i]][2] * M[1][2];
            crds[selection[i]][2] =
                    crds[selection[i]][0] * M[2][0]
                    + crds[selection[i]][1] * M[2][1]
                    + crds[selection[i]][2] * M[2][2];
        }
    }

    /** rotates selected points out of a coordinate set rightwise
    by angle psi around axis r1->r2.
    crds: a 3xN coordinate matrix
    movingAtoms: an array of indexes referring to crds.
    Specifies the points which are moved in the rotation
    nAtoms: the number of Atoms in the list movingAtoms
    r1,r2: rotation axis r1->r2
    psi: angle in radians
     */
    public static void rotatePointsRad(double[][] crds, int[] movingAtoms, int nMoving,
            double[] r1, double[] r2, double psiRad)
    {
        double[][] Mr = rotationMatrix(subtract(r2, r1), psiRad);
        double rx, ry, rz;
        int a;

        for (int i = 0; i < nMoving; i++)
        {
            a = movingAtoms[i];
            // translate:
            crds[a][0] = crds[a][0] - r2[0];
            crds[a][1] = crds[a][1] - r2[1];
            crds[a][2] = crds[a][2] - r2[2];
            // rotate:
            rx = Mr[0][0] * crds[a][0] + Mr[0][1] * crds[a][1]
                    + Mr[0][2] * crds[a][2];
            ry = Mr[1][0] * crds[a][0] + Mr[1][1] * crds[a][1]
                    + Mr[1][2] * crds[a][2];
            rz = Mr[2][0] * crds[a][0] + Mr[2][1] * crds[a][1]
                    + Mr[2][2] * crds[a][2];
            // translate back:
            crds[a][0] = rx + r2[0];
            crds[a][1] = ry + r2[1];
            crds[a][2] = rz + r2[2];
        }
    }

    /** same as above, rotates all moving Atoms */
    public static void rotatePointsRad(double[][] crds, int[] movingAtoms,
            double[] r1, double[] r2, double psiRad)
    {
        rotatePointsRad(crds, movingAtoms, movingAtoms.length,
                r1, r2, psiRad);
    }

    /** same as above only one atom
     */
    public static void rotateRad(double[] crd, double[] r1, double[] r2, double psiRad)
    {
        double[][] crds =
        {
            crd
        };
        int[] movingAtoms =
        {
            0
        };
        rotatePointsRad(crds, movingAtoms, 1, r1, r2, psiRad);
    }

    public static void rotateDeg(double[] crd, double[] r1, double[] r2, double psiDeg)
    {
        rotateRad(crd, r1, r2, Math.PI * psiDeg / 180.0);
    }

    /** same as above, but psi is given in degrees
     */
    public static void rotatePointsDeg(double[][] crds, int[] movingAtoms, int nMoving,
            double[] r1, double[] r2, double psiDeg)
    {
        rotatePointsRad(crds, movingAtoms, nMoving,
                r1, r2, Math.PI * psiDeg / 180.0);
    }

    /** same as above, but psi is given in degrees
     */
    public static void rotatePointsDeg(double[][] crds, int[] movingAtoms,
            double[] r1, double[] r2, double psiDeg)
    {
        rotatePointsRad(crds, movingAtoms, movingAtoms.length,
                r1, r2, Math.PI * psiDeg / 180.0);
    }

    /** returns the possible solutions for a point p whose distances
    d1,d2,d3 to three points p1,p2,p3 is given.
    d1,d2,d3: scalar distances
    p1,p2,p3: three-dimensional vectors
    s1,s2: two 3-dimensional double arrays for the solutions
    returns true, if successful, false otherwise
     */
    public static boolean triangulate(double[] p1, double[] p2, double[] p3,
            double d1, double d2, double d3,
            double[] s1, double[] s2)
    {
        double[] v12;
        double[] v120;
        double[] v13;
        double[] v23;
        double[] pF;
        double[] vi;
        double[] vi0;
        double[] vFF;
        double[] vF3;
        double[] pF_2;
        double[] pF_3;
        double[] vN;
        double[] vN0;
        double d12sq, d12, d13sq, d13, d23sq, d23, arg, dF, r, dF_2;
        double dp3flat, dFFsq, dF3, beta, d3v12, dF_3, r_3;
        // compute some values
        v12 = subtract(p2, p1);
        d12sq = dotprod(v12, v12);
        d12 = Math.sqrt(d12sq);
        v120 = stretch(1 / d12, v12);
        v13 = subtract(p3, p1);
        d13sq = dotprod(v13, v13);
        d13 = Math.sqrt(d13sq);
        v23 = subtract(p3, p2);
        d23sq = dotprod(v23, v23);
        d23 = Math.sqrt(d23sq);
        // Winkel zwischen p und v12
        arg = (-d2 * d2 + d1 * d1 + d12sq) / (2 * d1 * d12);
        if (arg < -1 || arg > 1)
        {
            return (false);
        }
        // Abstand und Fusspunkt zu v12
        dF = d1 * arg;
        r = d1 * Math.sin(MathTools.acos(arg));
        pF = addweighted(1, p1, dF, v120);
        // Vektor, der senkrecht auf v12 steht und nach p3 zeigt:
        dF_2 = (-d13sq + d12sq + d23sq) / (2 * d12);
        pF_2 = addweighted(1, p2, -dF_2, v120);
        vi = subtract(p3, pF_2);
        vi0 = normalize(vi);
        vFF = subtract(pF_2, pF);
        dFFsq = dotprod(vFF, vFF);
        // Abstand von p auf p3 in der vi-ebene
        arg = d3 * d3 - dFFsq;
        if (arg < 0)
        {
            return (false);
        }
        dp3flat = Math.sqrt(arg);
        vF3 = subtract(p3, pF);
        dF3 = norm(vF3);
        beta = angleRad(vi, vF3);
        d3v12 = Math.cos(beta) * dF3;
        // Winkel zwischen vi und p
        arg = (-dp3flat * dp3flat + r * r + d3v12 * d3v12) / (2 * r * d3v12);
        if (arg < -1 || arg > 1)
        {
            return (false);
        }
        dF_3 = r * arg;
        r_3 = r * Math.sin(MathTools.acos(arg));
        pF_3 = addweighted(1, pF, dF_3, vi0);
        // Senkrechte auf p1-p2-p3-Ebene und p berechnen
        vN = crossprod(v12, v13);
        vN0 = normalize(vN);
        s1[0] = pF_3[0] + r_3 * vN0[0];
        s1[1] = pF_3[1] + r_3 * vN0[1];
        s1[2] = pF_3[2] + r_3 * vN0[2];
        s2[0] = pF_3[0] - r_3 * vN0[0];
        s2[1] = pF_3[1] - r_3 * vN0[1];
        s2[2] = pF_3[2] - r_3 * vN0[2];
        return (true);
    }

    /**
    @return the indexes of all points which are equal in both arrays
    up to a given tolerance.
     */
    public static int[] equalPoints(double[][] arr1, double[][] arr2, double tol)
    {
        if (arr1.length != arr2.length)
        {
            throw (new IllegalArgumentException("incompatible arrays"));
        }
        int[] ep = new int[arr1.length];
        int k = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            if (distance(arr1[i], arr2[i]) <= tol)
            {
                ep[k++] = i;
            }
        }
        return (intArrays.subarray(ep, 0, k));
    }


    /* obtain, from a set of points the index of the one which is
    closest to a reference point
    p the reference points
    points the set of points*/
    public static int closestPoint(double[] p, double[][] points,
            int nPoints)
    {
        int si = 0, i = 0;
        double[] d = subtract(p, points[0]);
        double ssd = dotprod(d, d);
        double sd;

        for (i = 1; i < nPoints; i++)
        {
            d = subtract(p, points[i]);
            sd = dotprod(d, d);
            if (sd < ssd)
            {
                ssd = sd;
                si = i;
            }
        }

        return (si);
    }

    /*
    Returns an array with upper and lower bounds for the x, y, and
    z-direction of the coordinate set.
    Form: [xmin, xmax, ymin, ymax, zmin, zmax]
     */
    public static double[] bounds(double[][] crds)
    {
        double[] b =
        {
            crds[0][0], crds[0][0],
            crds[0][1], crds[0][1],
            crds[0][2], crds[0][2]
        };

        for (int i = 1; i < crds.length; i++)
        {
            if (crds[i][0] < b[0])
            {
                b[0] = crds[i][0];
            }
            if (crds[i][0] > b[1])
            {
                b[1] = crds[i][0];
            }
            if (crds[i][1] < b[2])
            {
                b[2] = crds[i][1];
            }
            if (crds[i][1] > b[3])
            {
                b[3] = crds[i][1];
            }
            if (crds[i][2] < b[4])
            {
                b[4] = crds[i][2];
            }
            if (crds[i][2] > b[5])
            {
                b[5] = crds[i][2];
            }
        }

        return (b);
    }

    /**
    ERROR: May overestimate distance for segments as it does not consider
    that the nearest points may be within the segments.
     */
    /*private static double lineDistance
    (double[] a1, double[] a2, double[] b1, double[] b2,
    boolean segmentsOnly)
    {
    double[] a12 = subtract(a2,a1);
    double[] b12 = subtract(b2,b1);
    double[] a1b1 = subtract(b1,a1);
    double A = - dotprod(a12,a12);
    double B = dotprod(b12,a12);
    double C = - dotprod(a12,a1b1);
    double D = - dotprod(a12,b12);
    double E = dotprod(b12,b12);
    double F = - dotprod(b12,a1b1);
    double[][] M = {{A,B},{D,E}};
    double[][] b = {{C},{F}};
    DenseDoubleMatrix2D matM = new DenseDoubleMatrix2D(M);
    Algebra alg = new Algebra();

    double[][] X = {{0},{0}};

    if (alg.det(matM) == 0) // vectors parallel. choose X[0][0] = 0
    X[1][0] = - dotprod(a12,a1b1)/dotprod(a12,b12);
    else
    {
    DenseDoubleMatrix2D matb = new DenseDoubleMatrix2D(b);
    LUDecomposition lud = new LUDecomposition(matM);
    X = (lud.solve(matb).toArray());
    }

    if (segmentsOnly &&
    ((X[0][0] < 0.0) || (X[0][0] > 1.0) ||
    (X[1][0] < 0.0) || (X[1][0] > 1.0)))
    {
    double[] possible = {distance(a1,b1), distance(a2,b2),
    distance(a1,b2), distance(a2,b1),
    pointSegmentDistance(a1,b1,b12),
    pointSegmentDistance(a2,b1,b12),
    pointSegmentDistance(b1,a1,a12),
    pointSegmentDistance(b2,a1,a12)};
    return(doubleArrays.min(possible));
    }
    else
    {
    double[] L1 = addweighted(1.0, a1, X[0][0], a12);
    double[] L2 = addweighted(1.0, b1, X[1][0], b12);
    return(distance(L1, L2));
    }
    }*/
    public static double pointLineDistance(double[] p, double[] a, double[] va)
    {
        double h = dotprod(va, subtract(p, a)) / dotprod(va, va);
        double[] L = addweighted(1.0, a, h, va);
        return (distance(L, p));
    }

    public static double pointSegmentDistance(double[] p, double[] a, double[] va)
    {
        double h = dotprod(va, subtract(p, a)) / dotprod(va, va);
        h = Math.min(h, 1.0);
        h = Math.max(h, 0.0);
        double[] L = addweighted(1.0, a, h, va);
        return (distance(L, p));
    }

    /**
    returns -1, 0, 1 if the lines specified by a1->a2, b1->b2 are "below"
    "in" or "above" the plane normal to the vector connecting their nearest
    points
     */
    private static int lineOrientation(double[] a1, double[] a2, double[] b1, double[] b2)
    {
        double[] a12 = subtract(a2, a1);
        double[] b12 = subtract(b2, b1);
        double[] a1b1 = subtract(b1, a1);
        double[] n = crossprod(a12, b12);
        return (MathTools.sign(dotprod(n, a1b1)));
    }

    /*public static double lineDistance
    (double[] a1, double[] a2, double[] b1, double[] b2)
    {return(lineDistance(a1,a2,b1,b2,false));}

    public static double segmentDistance
    (double[] a1, double[] a2, double[] b1, double[] b2)
    {return(lineDistance(a1,a2,b1,b2,true));}
     *
     */

    /*
    private static boolean lineCrossing
    (double[] a11, double[] a21, double[] b11, double[] b21,
    double[] a12, double[] a22, double[] b12, double[] b22,
    boolean segmentsOnly)
    {
    int[] orient = {lineOrientation(a11,a21,b11,b21),
    lineOrientation(a12,a22,b12,b22)};
    if (orient[0] == orient[1])
    return(false); // lines do not cross - no distance minimum within
    if (!segmentsOnly)
    return(true); // lines cross somewhere

    double[] va1 = subtract(a12,a11);
    double[] va2 = subtract(a22,a21);
    double[] vb1 = subtract(b12,b11);
    double[] vb2 = subtract(b22,b21);

    // determine distance minimum by bracketing the line orientation
    double tolSeg = 0.01;
    double tolLin = 1e-4;
    double[] T = {0,1};
    double t = 0;
    double d = lineDistance(a11,a21,b11,b21);
    double ds = segmentDistance(a11,a21,b11,b21);
    if (ds < tolSeg)
    return(true);

    for (int i=0; (i<1000) && (d > tolLin); i++)
    {
    t = 0.5*(T[1]+T[0]);
    double[] a1t = addweighted(1,a11,t,va1);
    double[] a2t = addweighted(1,a21,t,va2);
    double[] b1t = addweighted(1,b11,t,vb1);
    double[] b2t = addweighted(1,b21,t,vb2);
    int o = lineOrientation(a1t,a2t,b1t,b2t);
    double dnew = lineDistance(a1t,a2t,b1t,b2t);
    if (o == orient[0])
    T[0] = t;
    else
    T[1] = t;
    if (dnew < d)
    d = dnew;

    //System.out.println(t+"\t"+d+"\t"+segmentDistance(a1t,a2t,b1t,b2t));

    if (d <= tolLin)
    ds = segmentDistance(a1t,a2t,b1t,b2t);
    }


    if (ds < tolSeg)
    return(true); // yes, crossing in segment!
    else
    return(false); // no, crosses somewhere else
    }

    public static boolean lineCrossing
    (double[] a11, double[] a12, double[] a21, double[] a22,
    double[] b11, double[] b12, double[] b21, double[] b22)
    {return(lineCrossing(a11,a12,a21,a22,b11,b12,b21,b22,false));}

    public static boolean segmentCrossing
    (double[] a11, double[] a12, double[] a21, double[] a22,
    double[] b11, double[] b12, double[] b21, double[] b22)
    {return(lineCrossing(a11,a12,a21,a22,b11,b12,b21,b22,true));}
     */
    /**
    Transforms the selected (sel) group of points in referenceCrds in the same way
    as the transformation of the rigid body (i1-i2-i3) describes from
    referenceCrds to buildCrds and puts the resulting coordinates in buildCrds.
     */
    public static void transformRigid(double[][] buildCrds, double[][] referenceCrds,
            int i1, int i2, int i3, int[] sel)
    {
        if (i1 < 0 || i2 < 0 || i3 < 0)
        {
            throw (new RuntimeException("Trying to build transform nonexisting pos."));
        }

        for (int i = 0; i < sel.length; i++)
        {
            copy(referenceCrds[sel[i]], buildCrds[sel[i]]);
        }

        double[] p10 = buildCrds[i1];
        double[] p1r = referenceCrds[i1];
        double[] p20 = buildCrds[i2];
        double[] p2r = referenceCrds[i2];
        double[] p30 = buildCrds[i3];
        double[] p3r = referenceCrds[i3];
        double[] p1copy = doubleArrays.copy(p1r);

        double[] t = CoordinateUtilities3D.subtract(p20, p2r);
        CoordinateUtilities3D.translate(buildCrds, sel, t[0], t[1], t[2]);
        p1copy = CoordinateUtilities3D.add(p1copy, t);

        double[] n0 = CoordinateUtilities3D.crossprod(CoordinateUtilities3D.subtract(p20, p10),
                CoordinateUtilities3D.subtract(p20, p30));
        double[] vCaNr = CoordinateUtilities3D.subtract(p2r, p3r);
        double[] nr = CoordinateUtilities3D.crossprod(CoordinateUtilities3D.subtract(p2r, p1r),
                vCaNr);
        double[] vrot1 = CoordinateUtilities3D.crossprod(nr, n0);
        double[] virt1 = CoordinateUtilities3D.add(p20, vrot1);
        double arot1 = CoordinateUtilities3D.angleRad(nr, n0);

        if (arot1 > 0)
        {
            // rotate into new 1-2-3 plane
            CoordinateUtilities3D.rotatePointsRad(buildCrds, sel, p20, virt1, arot1);
            CoordinateUtilities3D.rotateRad(p1copy, p20, CoordinateUtilities3D.add(p20, vrot1), arot1);
        }

        double arot2 = CoordinateUtilities3D.angleRad(CoordinateUtilities3D.subtract(p1copy, p20),
                CoordinateUtilities3D.subtract(p10, p20));
        if (arot2 > 0)
        {
            double[] n2 = CoordinateUtilities3D.crossprod(CoordinateUtilities3D.subtract(p1copy, p20),
                    CoordinateUtilities3D.subtract(p10, p20));

            // rotate within 1-2-3 plane
            double[] virt2 = CoordinateUtilities3D.add(p20, n2);
            CoordinateUtilities3D.rotatePointsRad(buildCrds, sel, p20, virt2, arot2);
            CoordinateUtilities3D.rotateRad(p1copy, p20, virt2, arot2);
        }
    }

    /**
    Diagonalizes the matrix Mcov, writes out the eigenvectors and eigenvalues,
    sorted from the largest to the smallest
     */
    /*
    public static void largestEVanalysis(double[][] Mcov,
    String evecOut, String evalOut)
    {
    Cern ED = new Cern(new DenseDoubleMatrix2D(Mcov));
    double[][] evec = ED.getEigenvectors();
    double[] eval = ED.getEigenvalues();

    // reorder eigenvectors according to eigenvalues;
    double[][] evecT = doubleArrays.transpose(evec);
    double[][] evecTsort = new double[evecT.length][evecT[0].length];
    int[] order = doubleArrays.sortedIndexes(eval);
    // descending order:
    order = IntArrays.add(IntArrays.multiply(-1,order), order.length-1);
    for (int i=0; i<order.length; i++)
    evecTsort[order[i]] = evecT[i];

    double[][] pcBase = (doubleArrays.transpose(evecTsort));
    double[] evalsort = doubleArrays.copy(eval);
    java.util.Arrays.sort(evalsort);
    double[] eigenvalues = doubleArrays.mirror(evalsort);

    doubleArrays.save(pcBase, evecOut);
    doubleArrays.save(eigenvalues, evalOut);
    }

    public static double[] powerMethod(SparseDoubleMatrix2D M)
    {
    Algebra algebra = new Algebra();
    DoubleMatrix2D p = new DenseDoubleMatrix2D(1,M.rows());
    for (int i=0; i<p.columns(); i++)
    p.set(0, i, Math.random());
    double norm = doubleArrays.norm(p.toArray()[0]);
    for (int i=0; i<p.size(); i++)
    p.set(0, i, p.get(0,i)/norm);

    double err = Double.POSITIVE_INFINITY;
    for (int i=0; i<100000; i++)
    {
    DoubleMatrix2D pnew = p.zMult(M, null);
    double[][] vnew = pnew.toArray();
    double sum = doubleArrays.sum(vnew[0]);
    double lambda = doubleArrays.norm(vnew[0]);
    for (int j=0; j<p.size(); j++)
    p.set(0, j, vnew[0][j]/lambda);
    //p = pnew;
    System.out.println(i+"\t"+lambda+"\t"+sum);
    }

    return(null);
    }
     */
    public static void DEBUG_printCrd(double[] crd)
    {
        System.out.println(crd[0] + ", " + crd[1] + ", " + crd[2]);
    }

    public static void DEBUG_printCrdMatrix(double[][] crd)
    {
        for (int i = 0; i < crd.length; i++)
        {
            System.out.print(crd[i][0]);
            System.out.print(crd[i][1]);
            System.out.println(crd[i][2]);
        }
    }

    public static void timer(String msg)
    {
        if (msg == null)
        {
            t = System.currentTimeMillis();
        }
        else
        {
            System.out.println(msg + "\t" + (double) (System.currentTimeMillis() - t) / 1000);
        }
    }

    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("usage: LinalgTools [mode]\n"
                    + "          -subspaceOverlap <V1> <V2>\n"
                    + "          -superspaceOverlap <V1> <V2>\n"
                    + "          -largestEVanalysis (-ibin <matrix-bin> | -itxt <matrix-bin>) (-o <evecOut> <evalOut>)\n"
                    + "          -powerMethod <matrix>");
            System.out.println("          -eigenvalues <matrix>");
            System.exit(0);
        }
        Arguments arg = new Arguments(args);

        if (arg.hasCommand("subspaceOverlap"))
        {
            double[][] V1 = doubleArrays.loadMatrix(arg.getArgument("subspaceOverlap", 0));
            double[][] V2 = doubleArrays.loadMatrix(arg.getArgument("subspaceOverlap", 1));
            System.out.println(CoordinateUtilities3D.subspaceOverlap(V1, V2));
        }
        if (arg.hasCommand("superspaceOverlap"))
        {
            double[][] V1 = doubleArrays.loadMatrix(arg.getArgument("superspaceOverlap", 0));
            double[][] V2 = doubleArrays.loadMatrix(arg.getArgument("superspaceOverlap", 1));
            System.out.println(CoordinateUtilities3D.superspaceOverlap(V1, V2));
        }


    }
}
