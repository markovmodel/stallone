package stallone.doubles;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;

/**
 * Class for a fast calculation of the minimal RMSD between two structures. Based on the fortran implementation of John
 * Chodera and based on the paper Douglas L. Theobald Rapid calculation of RMSDs using a quaternion-based characteristic
 * polynomial Acta Crystallographica Section A, Foundations of Crystallography ISSN 0108-7673 Department of Chemistry
 * and Biochemistry, University of Colorado at Boulder, Boulder, CO 80309-0215, USA. Correspondence e-mail:
 * theobal@colorado.edu
 *
 * @author   Martin Senne - mail@martin-Senne.de
 * @version  1.0
 */
public class MinimalRMSDistance3D implements IMetric<IDoubleArray>
{

    public static final int DIM = 3;
    public static final int MAX_ITERATIONS = 1000;
    public static final double TOLERANCE = 10e-8;
    private double[][] M;
    private double[][] K;
    private double[] p_centroid_k;
    private double[][] x_nk;
    private double[][] y_nk;
    private int N;

    /**
     * @param  N  number of atoms
     */
    public MinimalRMSDistance3D(int N)
    {
        this.N = N;

        this.M = new double[DIM][DIM];
        this.K = new double[4][4];

        this.p_centroid_k = new double[DIM];
        this.x_nk = new double[N][DIM];
        this.y_nk = new double[N][DIM];
    }

    protected double calculateMinRMSD()
    {

        double G_x = 0.0;
        double G_y = 0.0;

        for (int i = 0; i < N; i++)
        {

            for (int j = 0; j < DIM; j++)
            {
                G_x += x_nk[i][j] * x_nk[i][j];
                G_y += y_nk[i][j] * y_nk[i][j];
            }
        }

        for (int i = 0; i < DIM; i++)
        {

            for (int j = 0; j < DIM; j++)
            {
                M[i][j] = 0.0;

                for (int k = 0; k < N; k++)
                {
                    M[i][j] += x_nk[k][i] * y_nk[k][j];
                }
            }
        }

        K[0][0] = M[0][0] + M[1][1] + M[2][2];
        K[0][1] = M[1][2] - M[2][1];
        K[0][2] = M[2][0] - M[0][2];
        K[0][3] = M[0][1] - M[1][0];
        K[1][0] = K[0][1];
        K[1][1] = M[0][0] - M[1][1] - M[2][2];
        K[1][2] = M[0][1] + M[1][0];
        K[1][3] = M[2][0] + M[0][2];
        K[2][0] = K[0][2];
        K[2][1] = K[1][2];
        K[2][2] = -M[0][0] + M[1][1] - M[2][2];
        K[2][3] = M[1][2] + M[2][1];
        K[3][0] = K[0][3];
        K[3][1] = K[1][3];
        K[3][2] = K[2][3];
        K[3][3] = -M[0][0] - M[1][1] + M[2][2];

        // Compute coefficients of the characteristic polynomial.
        double C_4 = 1.0;
        double C_3 = 0.0;
        double C_2 = 0.0;

        for (int i = 0; i < 3; i++)
        {

            for (int j = 0; j < 3; j++)
            {
                C_2 -= 2.0 * M[i][j] * M[i][j];
            }
        }

        double C_1 = -8.0 * det(M);
        double C_0 = det(K);

        // Construct inital guess at lambda using upper bound.
        double lambda = (G_x + G_y) / 2.0;

        double lambda_old;

        double lambda2;
        double b;
        double a;

        for (int i = 0; i < MAX_ITERATIONS; i++)
        {
            lambda_old = lambda;
            lambda2 = lambda_old * lambda_old;
            b = (lambda2 + C_2) * lambda_old;
            a = b + C_1;
            lambda = lambda_old - (((a * lambda_old) + C_0) / ((2.0 * lambda2 * lambda_old) + b + a));

            if (Math.abs(lambda - lambda_old) < Math.abs(TOLERANCE * lambda))
            {
                break;
            }
        }

        double rmsd2 = (G_x + G_y - (2.0 * lambda)) / ((double) N);

        double result = 0.0;

        if (rmsd2 > 0)
        {
            result = Math.sqrt(rmsd2);
        }

        return result;

    }

    /**
     * Utility function to move positions to common center.
     *
     * @param  p_orig_nk     are the data
     * @param  p_shifted_nk  are the shifted data
     */
    private void shiftToCentroid(double[][] p_orig_nk, double[][] p_shifted_nk)
    {
        for (int j = 0; j < DIM; j++)
        {
            p_centroid_k[j] = 0.0d;

            for (int i = 0; i < N; i++)
            {
                p_centroid_k[j] += p_orig_nk[i][j];
            }

            p_centroid_k[j] /= ((double) N);
        }

        for (int i = 0; i < N; i++)
        {
            for (int j = 0; j < DIM; j++)
            {
                p_shifted_nk[i][j] = p_orig_nk[i][j] - p_centroid_k[j];
            }
        }
    }

    /**
     * Calculate determinant of matrix M.
     *
     * @param   M  is the matrix of which to calculated the determinant
     *
     * @return  calculated determinant
     */
    public static double det(double[][] M)
    {
        int n = M.length;

        if (n == 1)
        {
            return M[0][0];

        }
        else if (n == 2)
        {
            return (M[0][0] * M[1][1]) - (M[0][1] * M[1][0]);

        }
        else if (n == 3)
        {
            return (M[0][0] * ((M[1][1] * M[2][2]) - (M[1][2] * M[2][1])))
                    - (M[1][0] * ((M[0][1] * M[2][2]) - (M[0][2] * M[2][1])))
                    + (M[2][0] * ((M[0][1] * M[1][2]) - (M[0][2] * M[1][1])));

        }
        else if (n == 4)
        {
            return (M[0][0]
                    * ((M[1][1] * ((M[2][2] * M[3][3]) - (M[2][3] * M[3][2])))
                    - (M[2][1] * ((M[1][2] * M[3][3]) - (M[1][3] * M[3][2])))
                    + (M[3][1] * ((M[1][2] * M[2][3]) - (M[1][3] * M[2][2])))))
                    - (M[1][0]
                    * ((M[0][1] * ((M[2][2] * M[3][3]) - (M[2][3] * M[3][2])))
                    - (M[2][1] * ((M[0][2] * M[3][3]) - (M[0][3] * M[3][2])))
                    + (M[3][1] * ((M[0][2] * M[2][3]) - (M[0][3] * M[2][2])))))
                    + (M[2][0]
                    * ((M[0][1] * ((M[1][2] * M[3][3]) - (M[1][3] * M[3][2])))
                    - (M[1][1] * ((M[0][2] * M[3][3]) - (M[0][3] * M[3][2])))
                    + (M[3][1] * ((M[0][2] * M[1][3]) - (M[0][3] * M[1][2])))))
                    - (M[3][0]
                    * ((M[0][1] * ((M[1][2] * M[2][3]) - (M[1][3] * M[2][2])))
                    - (M[1][1] * ((M[0][2] * M[2][3]) - (M[0][3] * M[2][2])))
                    + (M[2][1] * ((M[0][2] * M[1][3]) - (M[0][3] * M[1][2])))));
        }
        else
        {
            double result = 0.0;

            for (int i = 0; i < M[0].length; i++)
            {
                double[][] temp = new double[M.length - 1][M[0].length - 1];

                for (int j = 1; j < M.length; j++)
                {

                    for (int k = 0; k < M[0].length; k++)
                    {

                        if (k < i)
                        {
                            temp[j - 1][k] = M[j][k];
                        }
                        else if (k > i)
                        {
                            temp[j - 1][k - 1] = M[j][k];
                        }

                    }
                }

                result += M[0][i] * Math.pow(-1, (double) i) * det(temp);
            }

            return result;
        } // end if-else
    }

    /**
     * Convert from frame format { P1_x, P1_y, P1_z, P2_x, P2_y, P2_z, ... } => { { P1_x, P1_y, P1_z }, { P2_x, P2_y,
     * P2_z }, ... }
     *
     * @param   frame
     *
     * @return  converted frame
     */
    private static double[][] convertFromFrame(IDoubleArray frame)
    {
        int n = frame.size() / DIM;

        if ((frame.size() % DIM) != 0)
        {
            throw new RuntimeException("Critical error ..... length not dividable by " + DIM + ".");
        }
        else
        {
            double[][] structure = new double[n][DIM];

            for (int i = 0; i < n; i++)
            {

                for (int j = 0; j < DIM; j++)
                {
                    structure[i][j] = frame.get((i * DIM) + j);
                }
            }

            return structure;
        }
    }

    @Override
    public double distance(IDoubleArray p1, IDoubleArray p2)
    {
        if (p1.size() == p2.size())
        {
            if (p1.rows() == N && p1.columns() == DIM && p2.rows() == N && p2.columns() == DIM)
            {
                this.shiftToCentroid(p1.getTable(), x_nk);
                this.shiftToCentroid(p2.getTable(), y_nk);

                return calculateMinRMSD();
            }
            else
            {
                throw new RuntimeException("Wrong dimension. (" + N + " x " + DIM + ") expected, "
                        + "but input vectors have (" + p1.rows() + " x "+ p1.columns() + ") and (" + p2.rows() + " x " + p2.columns() + ").");
            }
        }
        else
        {
            throw new RuntimeException("Can't calculate minRMSD, vectors do not have save length.");
        }
    }
}
