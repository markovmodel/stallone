/*
 *  File:
 *  System:
 *  Module:
 *  Author:
 *  Copyright:
 *  Source:              $HeadURL: $
 *  Last modified by:    $Author: $
 *  Date:                $Date: $
 *  Version:             $Revision: $
 *  Description:
 *  Preconditions:
 */
package stallone.algebra.extern;

import stallone.api.complex.IComplexArray;
import org.netlib.arpack.ARPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;
import stallone.algebra.EigenvalueDecomposition;

import stallone.complex.ComplexNumber;
import stallone.api.algebra.*;
import stallone.api.complex.Complex;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

public class SparseArpackEigenvalueDecomposition implements IEigenvalueSolver
{
    protected IDoubleArray matrix;
    private int nev;
    private IComplexArray eigenvalues;
    private IComplexArray rightEigenvectors;
    private boolean bRightComputation = true;
    
    private IEigenvalueDecomposition result;

    public SparseArpackEigenvalueDecomposition(final IDoubleArray matrix)
    {
        this.matrix = matrix;
    }

    @Override
    public void perform()
    {

        if (!bRightComputation)
        {
            return;
        }

        // if (matrix.isComplex())
        // throw new UnsupportedOperationException("Not supported yet.");

        // Set up parameters
        final int n = matrix.columns();
        final IDoubleArray x;
        final IDoubleArray y;
        x = Doubles.create.array(n);
        y = Doubles.create.array(n);

        final String bmat = "I";
        final String which = "LM";
        final intW nev = new intW(n - 2);
        final int ncv = n;

        // length of
        final int lworkl = (3 * (int) Math.pow(ncv, 2)) + (6 * ncv);

        // the residual
        final double[] resid = new double[n];

        // workspace
        final double[] workd = new double[3 * n];

        // workspace
        final double[] workl = new double[lworkl];

        // final set of arnoldi basis vectors
        final double[] v = new double[n * ncv];

        // info==0 => initialise resid with random numbers
        final intW info = new intW(0);

        // stopping criterion
        final doubleW tol = new doubleW(0.0);

        // ISHIFT = 1: exact shifts with respect to the current Hessenberg
        // matrix H
        final int ishfts = 1;

        // maximum number of Arnoldi update iterations allowed.
        final int maxitr = 300;

        // Type of eigenwert problem to solve
        final int mode1 = 1;

        // iparam is always an array of length 11
        final int[] iparam =
        {
            ishfts, 0, maxitr, 0, 0, 0, mode1, 0, 0, 0, 0
        };

        // ipntr is always an array of length 14
        final int[] ipntr = new int[14];

        final intW ido = new intW(0);

        do
        {

            // Call into ARPACK
            ARPACK.getInstance().dnaupd(ido, bmat, n, which, nev.val, tol, resid, ncv, v, n, iparam, ipntr, workd,
                    workl, lworkl, info);

            // The algorithm is done => skip the rest of the loop
            if (ido.val == 99)
            {
                break;
            }

            // write the ARPACK output into x
            for (int pos = ipntr[0] - 1; pos < (ipntr[0] - 1 + n); pos++)
            {
                x.set(pos - (ipntr[0] - 1), workd[pos]);
            }

            // y=matrix*x
            Algebra.util.product(matrix, x, y);

            // Give arpack the requested subsection of the result
            for (int pos = ipntr[1] - 1; pos < (ipntr[1] - 1 + n); pos++)
            {
                workd[pos] = (float) y.get(pos - (ipntr[1] - 1));
            }
        }
        while ((ido.val == -1) || (ido.val == 1));

        if (info.val < 0)
        {
            throw new RuntimeException("ARPACK error: snaupd(1) returned with info = " + info.val);
        }
        else
        {
            final boolean rvec = true;
            final float sigmar = 0.0f;
            final float sigmai = 0.0f;

            // Returned error code
            final intW ierr = new intW(0);

            // In this mode used as additional workspace
            final boolean[] select = new boolean[ncv];

            // Real part of the ouput
            final double[] dReal = new double[nev.val + 2];

            // Imaginay part of the outpu
            final double[] dImg = new double[nev.val + 2];

            // Eigenvectors
            final double[] z = new double[n * (nev.val + 1)];

            // workspace
            final double[] workev = new double[3 * ncv];

            // Call into ARPACK
            ARPACK.getInstance().dneupd(rvec, "A", select, dReal, dImg, z, n, sigmar, sigmai, workev, bmat, n, which,
                    nev, tol.val, resid, ncv, v, n, iparam, ipntr, workd, workl, lworkl, ierr);

            // Process the result
            if ((ierr.val != 0))
            {
                throw new RuntimeException("ARPACK error: sneupd(2) returned with info = " + info.val);
            }
            else
            {
                eigenvalues = Complex.create.array(nev.val);
                rightEigenvectors = Complex.create.array(n, nev.val);

                for (int i = 0; i < nev.val; i++)
                {
                    eigenvalues.set(i, dReal[i], dImg[i]);

                    int dest = 0;

                    // Copy real Eigenvector
                    if (dImg[i] == 0)
                    {

                        for (int source = i * n; source < ((i * n) + n); source++)
                        {
                            rightEigenvectors.set(dest++, i, z[source]);
                        }
                    } // Copy complex Eigenvector
                    else
                    {
                        // Note: Complex Eigenvectors always appear in pairs that only differ in the sign of the
                        // imaginary part. Thus ARPACK stores those pairs of almost identical vector in two consecutive
                        // columns of z. Extracting this properly is handled by the following code.

                        // Version of the Eigenvector with positive imaginary part
                        if (dImg[i] > 0)
                        {

                            for (int source = i * n; source < ((i * n) + n); source++)
                            {
                                rightEigenvectors.set(dest++, i, z[source], z[source + n]);
                            }
                        } // Version of the Eigenvector with negative imaginary part
                        else
                        {

                            for (int source = i * n; source < ((i * n) + n); source++)
                            {
                                rightEigenvectors.set(dest++, i, z[source - n], -z[source]);
                            }
                        }
                    } // end if-else
                } // end for
            } // end if-else
        } // end if-else
        
        result = new EigenvalueDecomposition(null, eigenvalues, rightEigenvectors);
    }

    public IEigenvalueDecomposition getResult()
    {
        return result;
    }
    
    @Override
    public void setMatrix(final IDoubleArray m)
    {
        this.matrix = m;
    }

    //@Override
    public int getNumberOfAvailableEigenvectors()
    {
        return matrix.columns() - 2;
    }

    @Override
    public void setPerformRightComputation(final boolean right)
    {
        bRightComputation = right;
    }

    //@Override
    public IComplexNumber getEigenvalue(final int i)
    {
        return new ComplexNumber(eigenvalues.getRe(i), eigenvalues.getIm(i));
    }

    //@Override
    public IComplexArray getEigenvalues()
    {
        return eigenvalues;
    }

    //@Override
    public IComplexArray getRightEigenvectorMatrix()
    {
        return rightEigenvectors;
    }

    //@Override
    public IComplexArray getRightEigenvector(final int i)
    {
        return rightEigenvectors.viewColumn(i);
    }

    //@Override
    public IComplexArray getLeftEigenvectorMatrix()
    {

        // ARPACK can't do left eigenvectors
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPerformLeftComputation(final boolean left)
    {

        if (left == true)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    //@Override
    public IComplexArray getLeftEigenvector(final int i)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNumberOfRequestedEigenvalues(int _nev)
    {
        this.nev = _nev;
    }
}
