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

import stallone.doubles.mtj.WrappedMTJMatrix;
import stallone.doubles.mtj.WrappedMTJVector;
import java.util.logging.Level;
import java.util.logging.Logger;

import no.uib.cipr.matrix.sparse.DefaultIterationMonitor;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;

import stallone.api.algebra.*;
import stallone.api.doubles.IDoubleArray;



/**
 * General solution of a real linear system via means of LU or QR decomposition.
 *
 * @author  Martin Senne
 */
public class RealSparseLinearSystem implements ILinearSystem {

    // http://www.netlib.org/templates/index.html
    // http://people.sc.fsu.edu/~jburkardt/m_src/templates/templates.html


//    Preconditioner("AMG", "Algebraic multigrid preconditioner. Uses the smoothed aggregation method described by Vanek, Mandel, and Brezina (1996)" );
//    Preconditioner("DiagonalPreconditioner","Uses the inverse of the diagonal as preconditioner.");
//    Preconditioner("ICC","Incomplete Cholesky preconditioner without fill-in using a compressed row matrix as internal storage");
//    Preconditioner("ILU","ILU(0) preconditioner using a compressed row matrix as internal storage");
//    Preconditioner("ILUT","ILU preconditioner with fill-in. Uses the dual threshold approach of Saad.");
//    Preconditioner("SSOR","SSOR preconditioner. Uses symmetrical sucessive overrelaxation as a preconditioner. Meant for symmetrical, positive definite matrices." +
//                   "For best performance, omega must be carefully chosen (between 0 and 2).");
//    Preconditioner("","");
//
//    Solver("BiCGstab","BiCGstab solves the unsymmetric linear system Ax = b using the Preconditioned BiConjugate Gradient Stabilized method");
//    Solver("BiCG", "BiCG solves the unsymmetric linear system Ax = b using the Preconditioned BiConjugate Gradient method");
//    Solver("CG","CG solves the symmetric positive definite linear system Ax=b using the Conjugate Gradient method.");
//    Solver("CGS","CGS solves the unsymmetric linear system Ax = b using the Conjugate Gradient Squared method");
//    Solver("Chebyshev","Chebyshev solver. Solves the symmetric positive definite linear system Ax = b using the Preconditioned Chebyshev Method." +
//                       "Chebyshev requires an acurate estimate on the bounds of the spectrum of the matrix.");
//    Solver("GMRES","GMRES solves the unsymmetric linear system Ax = b using the Generalized Minimum Residual method." +
//                   "The GMRES iteration is restarted after a given number of iterations. " +
//                   "By default it is restarted after 30 iterations.");
//    Solver("IR","Iterative Refinement. IR solves the unsymmetric linear system Ax = b using Iterative Refinement (preconditioned Richardson iteration).");
//    Solver("QMR","Quasi-Minimal Residual method. QMR solves the unsymmetric linear system Ax = b using the Quasi-Minimal Residual method. " +
//                 "QMR uses two preconditioners, and by default these are the same preconditioner.");
//    Solver("","");
    
    public static final int BiCGstab = 1;
    public static final int BiCG = 2;
    public static final int CG = 3;
    public static final int CGS = 4;

    private IDoubleArray wrappedLeftSideMatrix;
    private IDoubleArray wrappedCoefficientVector;
    private IDoubleArray wrappedSolutionVector;

    private no.uib.cipr.matrix.sparse.AbstractIterativeSolver solver;
    
    private no.uib.cipr.matrix.Matrix leftSideMatrix;
    private no.uib.cipr.matrix.Vector coefficientVector;
    private no.uib.cipr.matrix.Vector solutionVector;

    private final int solverType;

    public RealSparseLinearSystem( int solverType ) {
        this.solverType = solverType;
    }
    
    /**
     * Matrix leftSideMatrix should be sparse.
     * 
     * @param leftSideMatrix
     */
    @Override
    public void setMatrix(final IDoubleArray A) {
        leftSideMatrix = new no.uib.cipr.matrix.sparse.FlexCompColMatrix( A.rows(), A.columns());
        wrappedLeftSideMatrix = new WrappedMTJMatrix( leftSideMatrix );
        wrappedLeftSideMatrix.copyFrom(A); // copy values to wrapped mtj matrix leftSideMatrix
    }

    @Override
    public void setCoefficientVector(IDoubleArray b) {
        coefficientVector = new no.uib.cipr.matrix.sparse.SparseVector(b.size());
        wrappedCoefficientVector = new WrappedMTJVector(coefficientVector);
        wrappedCoefficientVector.copyFrom(b);

        solutionVector = new no.uib.cipr.matrix.sparse.SparseVector(b.size());
        wrappedSolutionVector = new WrappedMTJVector(solutionVector);
    }

    @Override
    public void perform() {
        try {
            if (solverType == CG) {
                solver = new no.uib.cipr.matrix.sparse.CG(solutionVector);
            } else if (solverType == BiCG) {
                solver = new no.uib.cipr.matrix.sparse.BiCG(solutionVector);
            }

            DefaultIterationMonitor monitor = new DefaultIterationMonitor();
            monitor.setMaxIterations(1000000);

            solver.setIterationMonitor( monitor );
            
            // A, b, x
            solver.solve(leftSideMatrix, coefficientVector, solutionVector);
        } catch (IterativeSolverNotConvergedException ex) {
            System.out.println("Iteration did not converge!!!");
            Logger.getLogger(RealSparseLinearSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public IDoubleArray getSolutionVector() {
        return wrappedSolutionVector;
    }
}
