/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.algebra;

import static stallone.api.API.*;

import stallone.algebra.RealCholeskyDecomposition;
import stallone.algebra.RealLUDecomposition;
import stallone.algebra.RealLinearSystem;
import stallone.algebra.RealQRDecomposition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import stallone.algebra.extern.*;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class AlgebraFactory
{

    /////////////////////////////////////////////////////////////////////////////
    //
    // Algorithms
    //
    /////////////////////////////////////////////////////////////////////////////
    public ILinearSystem linearSolver(final IDoubleArray A, final IDoubleArray b)
    {
        final ILinearSystem system = new RealLinearSystem();
        system.setCoefficientVector(b);
        system.setMatrix(A);

        return system;
    }

    public List<String> queryLinearSolverNames()
    {
        return getNames(LinearSystemEnum.values());
    }

    public ILinearMatrixSystem linearMatrixSolver(final IDoubleArray A, final IDoubleArray B)
    {
        final ILinearMatrixSystem system = new RealLinearSystem();
        system.setCoefficientMatrix(B);
        system.setMatrix(A);

        return system;
    }

    public ILinearSystem linearSolver(IDoubleArray A, IDoubleArray b, String algoName)
    {
        ILinearSystem linearSystem = null;
        if (algoName.equals(LinearSystemEnum.DENSE_QR.name()))
        {
            linearSystem = new RealLinearSystem();
        }
        else if (algoName.equals(LinearSystemEnum.SPARSE_MTJ_CG.name()))
        {
            linearSystem = new RealSparseLinearSystem(RealSparseLinearSystem.CG);
        }
        else if (algoName.equals(LinearSystemEnum.SPARSE_MTJ_BiCG.name()))
        {
            linearSystem = new RealSparseLinearSystem(RealSparseLinearSystem.BiCG);
        }

        // no solver available
        if (linearSystem == null)
        {
            String message = "Requested linear solver '" + algoName + "' is not available, "
                    + "only " + concatenate(queryLinearSolverNames(), ", ") + " available.";
            throw new UnsupportedOperationException(message);
        }

        linearSystem.setMatrix(A);
        linearSystem.setCoefficientVector(b);
        return linearSystem;
    }

    public ILUDecomposition LUSolver(final IDoubleArray A)
    {
        final ILUDecomposition decomposition = new RealLUDecomposition();
        decomposition.setMatrix(A);

        return decomposition;

    }

    public IQRDecomposition QRSolver(final IDoubleArray A)
    {
        final IQRDecomposition decomposition = new RealQRDecomposition();
        decomposition.setMatrix(A);

        return decomposition;

    }
    
    public IEigenvalueSolver eigensolver(final IDoubleArray matrix, int nev)
    {
        if (matrix.isSparse() && 10*nev < matrix.size())
            return eigensolverSparse(matrix, nev);
        else
            return eigensolverDense(matrix);
    }
    

    public IEigenvalueSolver eigensolverSparse(final IDoubleArray matrix, int n)
    {
        SparseArpackEigenvalueDecomposition solver = new SparseArpackEigenvalueDecomposition(matrix);
        solver.setNumberOfRequestedEigenvalues(n);
        return solver;
    }
    
    public IEigenvalueSolver eigensolverDense(final IDoubleArray matrix)
    {
        LapackEigenvalueDecomposition blas = new LapackEigenvalueDecomposition();
        blas.setMatrix(matrix);
        return (blas);
    }

    public IEigenvalueSolver eigensolverDense(final IDoubleArray matrix, boolean computeLeftEV, boolean computeRightEV)
    {
        LapackEigenvalueDecomposition blas = new LapackEigenvalueDecomposition(computeLeftEV, computeRightEV);
        blas.setMatrix(matrix);
        return (blas);
    }


    public IEigenvalueSolver eigensolver(IDoubleArray matrix, String algoName)
    {
        if (algoName.equals(EigenvalueDecompositionEnum.DENSE_BLAS.name()))
        {
            return new LapackEigenvalueDecomposition();
        }
        if (algoName.equals(EigenvalueDecompositionEnum.SPARSE_ARPACK.name()))
        {
            return new SparseArpackEigenvalueDecomposition(matrix);
        }

        String message = "Requested eigenvalue decomposition '" + algoName + "' is not available, "
                + "only " + concatenate(queryEigenvalueDecompositionNames(), ", ") + " available.";
        throw new UnsupportedOperationException(message);
    }

    public List<String> queryEigenvalueDecompositionNames()
    {
        return getNames(EigenvalueDecompositionEnum.values());
    }

    public static List<String> getNames(Enum<?>[] enums)
    {
        List<String> names = new ArrayList<String>();
        for (Enum<?> enumeration : enums)
        {
            names.add(enumeration.name());
        }
        return names;
    }

    protected static String concatenate(List<String> strings, String delim)
    {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> it = strings.iterator(); it.hasNext();)
        {
            String string = it.next();
            builder.append(string);
            if (it.hasNext())
            {
                builder.append(delim);
            }
        }
        return builder.toString();
    }

    /**
     * Specifies algorithm to use for solving eigenvalue system.
     */
    public enum EigenvalueDecompositionEnum
    {

        /** Dense eigenvalue decomposition done by blas (fortran wrapper) */
        DENSE_BLAS(true),
        /** Sparse eigenvalue decomposition using Arnoldi sparse solver */
        SPARSE_ARPACK(false);
        protected boolean dense;

        private EigenvalueDecompositionEnum(boolean dense)
        {
            this.dense = dense;
        }

        /**
         * Get the value of dense
         *
         * @return the value of dense
         */
        public boolean isDense()
        {
            return dense;
        }
    }

    /**
     * Specifies algorithm to use for solving linear system.
     */
    public enum LinearSystemEnum
    {

        /** Dense QR decomposition or LU (if matrix is quadrativ) decomposition to solve linear system */
        DENSE_QR(true),
        SPARSE_MTJ_CG(false),
        SPARSE_MTJ_BiCG(false);
        protected boolean dense;

        private LinearSystemEnum(boolean dense)
        {
            this.dense = dense;
        }

        /**
         * Get the value of dense
         *
         * @return the value of dense
         */
        public boolean isDense()
        {
            return dense;
        }
    }

    public ICholeskyDecomposition createCholeskyDecomposition(IDoubleArray matrix)
    {
        return new RealCholeskyDecomposition(matrix);
    }
}
