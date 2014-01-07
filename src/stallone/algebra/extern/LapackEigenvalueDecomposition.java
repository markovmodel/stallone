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

import stallone.api.doubles.IDoubleArray;
import stallone.complex.ComplexNumber;
import stallone.complex.AbstractComplexArray;
import stallone.complex.DenseComplexArray;
import stallone.api.complex.IComplexArray;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;

import stallone.api.algebra.*;
import stallone.algebra.*;
import stallone.api.complex.ImaginaryView;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * BlasEigenvalueDecomposition computes eigenvalue decomposition of a general quadratic (real valued) matrices.
 *
 * @author  Martin Senne, Frank Noe
 */
public class LapackEigenvalueDecomposition implements IEigenvalueSolver
{

    /** An empty double. */
    private static double[] emptyDouble = new double[0];
    /** Double work array. */
    private double[] workSpace;
    /** Size of the matrix. */
    private int size;
    /** Real valued matrix, for which we want to compute EVD,. */
    private IDoubleArray matrix;
    /** Job to do on the left and right eigenvectors. */
    private boolean jobLeft;
    private boolean jobRight;
    // number of requested eigenvalues. -1: compute all
    private int nev = -1;
    /**
     * Job left and job right as strings. Can be "N" or "V". "N" is: do not calculate eigenvectors. "V" is: calculate
     * eigenvectors.
     */
    private String jobLeftString;
    private String jobRightString;
    /** Contains the real parts of the eigenvalues. */
    private double[] wRealValues;
    /** Contains the imaginary parts of the eigenvalues. */
    private double[] wImagValues;
    /** Contains the left eigenvectors. */
    private double[] vLeftEigenvectors;
    /** Contains the right eigenvectors. */
    private double[] vRightEigenvectors;
    // ==================================================
    // Methods for full analysis
    // Mainly done by wrappers to existing double arrays.
    // ==================================================

    private IEigenvalueDecomposition result;
    /** full system matrix of left eigenvectors. */
    //private IComplexArray leftEigenvectorMatrix;
    /** full system matrix of right eigenvectors. */
    //private IComplexArray rightEigenvectorMatrix;
    /** full system eigenvalues. */
    //private IComplexArray eigenvalues;

    /**
     * Creates a new blas eigenvalue decomposition where neither left nor right eigenvectors, so only eigenvalues are
     * computed.
     */
    public LapackEigenvalueDecomposition()
    {
        this(false, true);
    }

    /**
     * Creates a new blas eigenvalue decomposition.
     *
     * @param  left   whether to compute the left eigenvectors or not
     * @param  right  whether to compute the right eigenvectors or not
     * @param  nev    number of requested eigenvalues
     */
    public LapackEigenvalueDecomposition(final boolean left, final boolean right)
    {
        setPerformLeftComputation(left);
        setPerformRightComputation(right);

        workSpace = null;
    }

    @Override
    public final void setPerformLeftComputation(final boolean left)
    {
        jobLeft = left;
        jobLeftString = "N"; // do not calculate left eigenvectors

        if (jobLeft)
        {
            jobLeftString = "V";
        }
    }

    @Override
    public final void setPerformRightComputation(final boolean right)
    {
        jobRight = right;
        jobRightString = "N"; // do not calculate right eigenvectors

        if (jobRight)
        {
            jobRightString = "V";
        }
    }

    @Override
    public void setMatrix(final IDoubleArray m)
    {
        assertSquare(m);
        this.matrix = m;
        this.size = m.rows();

            // Allocate space for the decomposition
            wRealValues = new double[size];
            wImagValues = new double[size];

            if (jobLeft)
            {
                vLeftEigenvectors = new double[size * size];
            }
            else
            {
                vLeftEigenvectors = null;
            }

            if (jobRight)
            {
                vRightEigenvectors = new double[size * size];
            }
            else
            {
                vRightEigenvectors = null;
            }

            workSpace = new double[getSizeOfWorkspace()];
    }

    @Override
    public void setNumberOfRequestedEigenvalues (int nev)
    {
    }

    /**
     * Computes the eigenvalue decomposition of the given matrix.
     */
    @Override
    public void perform()
    {

        final intW info = new intW(0);

        // create a dense working copy
        final double[] matrixDataColumnMajor = new double[size * size];

        for (int i = 0; i < size; i++)
        {

            for (int j = 0; j < size; j++)
            {
                // important: column major is
                // / 1 2 3 \
                // | 4 5 6 |
                // \ 7 8 9 /
                //
                // 1 4 7 2 5 8 3 6 9  in memory
                matrixDataColumnMajor[i + (j * size)] = matrix.get(i, j);
            }
        }

        double[] leftEigenVectorData = emptyDouble;

        if (jobLeft)
        {
            leftEigenVectorData = vLeftEigenvectors;
            // leftEigenVectorData = Vl.getData();
        }

        double[] rightEigenVectorData = emptyDouble;

        if (jobRight)
        {
            rightEigenVectorData = vRightEigenvectors;
            // rightEigenVectorData = Vr.getData();
        }

        LAPACK.getInstance().dgeev(jobLeftString, jobRightString, size, matrixDataColumnMajor,
                // n, Wr.getData(), Wi.getData(), leftEigenVectorData,
                size, wRealValues, wImagValues, leftEigenVectorData, size, rightEigenVectorData, size, workSpace,
                workSpace.length, info);

        if (info.val > 0)
        {
            throw new RuntimeException("EVD did not converge.");
        }
        else if (info.val < 0)
        {
            throw new IllegalArgumentException();
        }

        // copy result
        IComplexArray L = null;
        if (jobLeft)
        {
            L = new MatrixOfEigenvectors(vLeftEigenvectors).copy();
            Algebra.util.transpose(L);
        }
        IComplexArray R = null;
        if (jobRight)
        {
            R = new MatrixOfEigenvectors(vRightEigenvectors);
        }
        IComplexArray eval = new VectorOfEigenvalues();
        result = new EigenvalueDecomposition(L, eval, R);
    }

    @Override
    public IEigenvalueDecomposition getResult()
    {
        return result;
    }


    /**
     * Calculate required size of workspace. Used internally.
     *
     * @return  required size for Blas.
     */
    private int getSizeOfWorkspace()
    {

        // Find the needed workspace
        final double[] worksize = new double[1];
        final intW info = new intW(0);

        LAPACK.getInstance().dgeev(jobLeftString, jobRightString, size, emptyDouble, size, emptyDouble, emptyDouble,
                emptyDouble, size, emptyDouble, size, worksize, -1, info);

        // Allocate workspace
        int workSpaceSize = 0;

        if (info.val != 0)
        {

            if (jobLeft && jobRight)
            {
                workSpaceSize = 4 * size;
            }
            else
            {
                workSpaceSize = 3 * size;
            }
        }
        else
        {
            workSpaceSize = (int) worksize[0];
        }

        workSpaceSize = Math.max(1, workSpaceSize);

        return workSpaceSize;
    }

    /**
     * Matrix view which provides efficient access to calculated matrix of left and right eigenvectors. The internal
     * format of blas eigenvalue decomposition requires such a view.
     */
    private class MatrixOfEigenvectors extends AbstractComplexArray
    {
        private final int rows,cols;
        private final double[] data;
        private final int[] firstOfPair;
        private final boolean[] isComplex;
        private final int n;

        private MatrixOfEigenvectors(final double[] data)
       {
            rows = LapackEigenvalueDecomposition.this.size;
            cols = LapackEigenvalueDecomposition.this.size;
            this.data = data;
            this.n = LapackEigenvalueDecomposition.this.size;
            this.firstOfPair = new int[n];
            this.isComplex = new boolean[n];

            for (int i = 0; i < n; i++)
            {

                if (wImagValues[i] == 0.0d)
                { // real eigenvalue
                    firstOfPair[i] = i;
                    isComplex[i] = false;
                }
                else
                { // complex eigenvalue
                    firstOfPair[i] = i;
                    firstOfPair[i + 1] = i;
                    isComplex[i] = true;
                    isComplex[i + 1] = true;
                    i += 1;
                }
            }
        }

        /**
         * Copy constructor.
         *
         * @param  m  source
         */
        private MatrixOfEigenvectors(final MatrixOfEigenvectors m)
        {
            this(m.data);
        }

        //@Override
        public IComplexNumber getScalar(final int i, final int j)
        {
            final int f = firstOfPair[j];

            // f th column contains real values
            // f+1 th columns contains imag values ( (f+1)*n + i )
            final int idx = (f * n) + i;

            if (f == j)
            { // either first of complex conjugate pair or real

                if (isComplex[f])
                {
                    return new ComplexNumber(data[idx], data[idx + n]);
                }
                else
                {
                    return new ComplexNumber(data[idx], 0.0d);
                }
            }
            else
            {
                return new ComplexNumber(data[idx], -data[idx + n]);
            }
        }

        //@Override
        public IComplexNumber getScalar(final int i, final int j, final IComplexNumber target)
        {
            final int f = firstOfPair[j];

            // f th column contains real values
            // f+1 th columns contains imag values ( (f+1)*n + i )
            final int idx = (f * n) + i;

            if (f == j)
            { // either first of complex conjugate pair or real

                if (isComplex[f])
                {
                    target.setComplex(data[idx], data[idx + n]);
                }
                else
                {
                    target.setComplex(data[idx], 0.0d);
                }
            }
            else
            {
                target.setComplex(data[idx], -data[idx + n]);
            }

            return target;
        }

        @Override
        public double getRe(final int i, final int j)
        {
            final int f = firstOfPair[j];

            // f th column contains real values
            // f+1 th columns contains imag values ( (f+1)*n + i )
            final int idx = (f * n) + i;

            return data[idx];
        }

        @Override
        public double getIm(final int i, final int j)
        {
            final int f = firstOfPair[j];

            // f th column contains real values
            // f+1 th columns contains imag values ( (f+1)*n + i )
            if (isComplex[f])
            {
                final int idx = (f * n) + i;

                return data[idx + n];
            }
            else
            {
                return 0.0d;
            }
        }

        @Override
        public int rows()
        {
            return n;
        }

        @Override
        public int columns()
        {
            return n;
        }

        @Override
        public void setRe(final int row, final int column, final double value)
        {
            throw new UnsupportedOperationException("Writing not supported.");
        }

        @Override
        public void setIm(final int row, final int column, final double value)
        {
            throw new UnsupportedOperationException("Writing not supported.");
        }

        @Override
        public void zero()
        {
            throw new UnsupportedOperationException("Writing not supported.");
        }

        @Override
        public void copyInto(final IComplexArray target)
        {

            for (int j = 0; j < n; j++)
            { // columns

                final int f = firstOfPair[j];
                // f th column contains real values
                // f+1 th columns contains imag values ( (f+1)*n + i )

                if (f == j)
                { // either first of complex conjugate pair or real

                    if (isComplex[f])
                    {

                        for (int i = 0; i < n; i++)
                        { // rows

                            final int idx = (f * n) + i;
                            target.set(i, j, data[idx], data[idx + n]);
                        }
                    }
                    else
                    {

                        for (int i = 0; i < n; i++)
                        { // rows

                            final int idx = (f * n) + i;
                            target.set(i, j, data[idx], 0.0d);
                        }
                    }
                }
                else
                {

                    for (int i = 0; i < n; i++)
                    { // rows

                        final int idx = (f * n) + i;
                        target.set(i, j, data[idx], -data[idx + n]);
                    }
                } // end if-else
            } // end for
        }

        @Override
        public IComplexArray create(int _rows, int _cols)
        {
            return (new DenseComplexArray(_rows, _cols));
        }

        @Override
        public IComplexArray create(int size)
        {
            return (new DenseComplexArray(size, 1));
        }

        @Override
        public IComplexArray copy()
        {
            IComplexArray res = new DenseComplexArray(rows,cols);
            res.copyFrom(this);
            return(res);
        }

        @Override
        public IDoubleArray viewReal()
        {
            return this;
        }

        @Override
        public IDoubleArray viewImaginary()
        {
            return new ImaginaryView(this);
        }

        @Override
        public boolean isSparse()
        {
            // TODO Auto-generated method stub
            return false;
        }
    }

    /**
     * View for eigenvalues. View the set of eigenvalues as vector.
     */
    private class VectorOfEigenvalues extends AbstractComplexArray
    {
        private int size;

        /**
         * Vector of eigenvalues delivers performant access for eigenvalues.
         */
        private VectorOfEigenvalues()
        {
            size = LapackEigenvalueDecomposition.this.size;
        }

        @Override
        public int rows()
        {
            return size;
        }

        @Override
        public int columns()
        {
            return 1;
        }

        @Override
        public double getRe(final int i, final int j)
        {
            if (j!=0)
                throw(new ArrayIndexOutOfBoundsException("Trying to access column "+j+" of a column vector"));

            return wRealValues[i];
        }

        @Override
        public double getIm(final int i, final int j)
        {
            if (j!=0)
                throw(new ArrayIndexOutOfBoundsException("Trying to access column "+j+" of a column vector"));

            return wImagValues[i];
        }

        @Override
        public double getRe(final int i)
        {
            return wRealValues[i];
        }

        @Override
        public double getIm(final int i)
        {
            return wImagValues[i];
        }

        /*
        @Override
        public IScalar getScalar(final int i)
        {
            return new ComplexScalar(wRealValues[i], wImagValues[i]);
        }

        @Override
        public IScalar getScalar(final int i, final IScalar target)
        {
            target.setComplex(wRealValues[i], wImagValues[i]);

            return target;
        }
*/
        @Override
        public void setRe(final int i, final int j, final double value)
        {
            throw new UnsupportedOperationException("Read only vector.");
        }

        @Override
        public void setIm(final int i, final int j, final double value)
        {
            throw new UnsupportedOperationException("Read only vector.");
        }

        @Override
        public void set(final int i, final int j, final double real, final double imaginary)
        {
            throw new UnsupportedOperationException("Read only vector.");
        }
/*
        @Override
        public void setScalar(final int index, final IScalar val)
        {
            throw new UnsupportedOperationException("Read only vector.");
        }
*/
        @Override
        public IComplexArray copy()
        {
            return(new DenseComplexArray(this));
        }

        @Override
        public void copyFrom(final IComplexArray other)
        {
            throw new UnsupportedOperationException("Read only vector.");
        }

        @Override
        public void zero()
        {
            throw new UnsupportedOperationException("Read only vector.");
        }

        @Override
        public IComplexArray create(int size)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public IComplexArray create(int rows, int cols)
        {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public IDoubleArray viewReal()
        {
            return this;
        }

        @Override
        public IDoubleArray viewImaginary()
        {
            return new ImaginaryView(this);
        }

        @Override
        public boolean isSparse()
        {
            // TODO Auto-generated method stub
            return false;
        }
    }
}
// from jblas - simpleblas.java
/*
 * public static int geev( char jobvl, char jobvr, DoubleMatrix A, DoubleMatrix WR, DoubleMatrix WI, DoubleMatrix VL,
 * DoubleMatrix VR ) {
 *
 * int info = NativeBlas.dgeev( jobvl, jobvr, A.rows, A.data, 0, A.rows, WR.data, 0, WI.data, 0, VL.data, 0, VL.rows,
 * VR.data, 0, VR.rows ); if ( info > 0 ) { throw new LapackConvergenceException( "DGEEV", "First " + info +
 * " eigenvalues have not converged." ); } return info; }
 */
// from jblas - nativeblas.java
/*
 * public static native int dgeev(char jobvl, char jobvr, int n, double[] a, int aIdx, int lda, double[] wr, int wrIdx,
 * double[] wi, int wiIdx, double[] vl, int vlIdx, int ldvl, double[] vr, int vrIdx, int ldvr, double[] work, int
 * workIdx,
 * int lwork);
 */
// from jblas - nativeblas.java
/*
 * public static int dgeev(char jobvl, char jobvr, int n, double[] a, int aIdx, int lda, double[] wr, int wrIdx,
 * double[] wi,
 * int wiIdx, double[] vl, int vlIdx, int ldvl, double[] vr, int vrIdx, int ldvr) { int info; double[] work = new
 * double[1];
 * int lwork; info = dgeev(jobvl, jobvr, n, doubleDummy, 0, lda, doubleDummy, 0, doubleDummy, 0, doubleDummy, 0, ldvl,
 * doubleDummy, 0, ldvr, work, 0, -1); if (info != 0) { return info; } lwork = (int) work[0]; work = new double[lwork];
 * info
 * = dgeev(jobvl, jobvr, n, a, aIdx, lda, wr, wrIdx, wi, wiIdx, vl, vlIdx, ldvl, vr, vrIdx, ldvr, work, 0, lwork);
 * return
 * info; }
 */
