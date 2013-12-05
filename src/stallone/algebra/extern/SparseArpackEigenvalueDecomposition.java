/*
 *  File:
 *  System:
 *  Module:
 *  Author: Martin Senne, Martin Scherer, Frank Noe
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
    
    /**
     * number of eigenvalues to calculate:
     * 0 < NEV < N-1, where n is the amount of columns of matrix
     */
    private int nev;
    
    private double tolerance = 1e-15;
    
    private int maxIter = 100;
    
    private IComplexArray eigenvalues;
    private IComplexArray rightEigenvectors;
    private boolean bRightComputation = true;

    private IEigenvalueDecomposition result;

    public SparseArpackEigenvalueDecomposition(final IDoubleArray matrix) 
            throws IllegalArgumentException
    {
        if(! matrix.isSparse())
            throw new IllegalArgumentException("matrix is not sparse. " +
                    "One should use dense solver instead.");
        this.matrix = matrix;
    }

    @Override
    public void perform() throws RuntimeException
    {

        if (!bRightComputation)
        {
            return;
        }

        // if (matrix.isComplex())
        // throw new UnsupportedOperationException("Not supported yet.");

        // Set up parameters
        // # columns
        final int n = matrix.columns();
        final IDoubleArray x;
        final IDoubleArray y;
        x = Doubles.create.array(n);
        y = Doubles.create.array(n);

        final String bmat = "I";
        final String which = "LM";
        //final intW nev = new intW(n - 2);
        final int ncv = Math.max(2 + nev, 3 * nev);

        System.out.println("NEV = "+nev);
        System.out.println("NCV = "+ncv);
        System.out.println("NCV-NEV = " + (ncv-nev));
        System.out.println("n = " + n);
        
        // the residual
        final double[] resid = new double[n];

        // workspace
        final double[] workd = new double[3 * n];

        // workspace
        final int lworkl = (3 * (int) Math.pow(ncv, 2)) + (6 * ncv);
        final double[] workl = new double[lworkl];

        System.out.println("WORKL length = "+lworkl);
        
        // final set of arnoldi basis vectors
        final double[] v = new double[n * ncv];

        System.out.println("Arnoldi basis length = "+v.length);
        
        // info==0 => initialize resid with random numbers
        final intW info = new intW(0);

        // stopping criterion
        final doubleW tol = new doubleW(this.tolerance);

        // ISHIFT = 1: exact shifts with respect to the current Hessenberg
        // matrix H
        final int ishfts = 1;

        // maximum number of Arnoldi update iterations allowed.
        final int maxitr = Math.max(n, this.maxIter);

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

        calculate(n, x, y, bmat, which, ncv, resid, workd, lworkl, workl, v,
                info, tol, iparam, ipntr, ido);

        postProcess(n, bmat, which, ncv, resid, workd, lworkl, workl, v, info,
                tol, iparam, ipntr);

        result = new EigenvalueDecomposition(null, eigenvalues, rightEigenvectors);
    }

    /**
     * @param n
     * @param x
     * @param y
     * @param bmat
     * @param which
     * @param ncv
     * @param resid
     * @param workd
     * @param lworkl
     * @param workl
     * @param v
     * @param info
     * @param tol
     * @param iparam
     * @param ipntr
     * @param ido
     */
    private void calculate(final int n, final IDoubleArray x,
            final IDoubleArray y, final String bmat, final String which,
            final int ncv, final double[] resid, final double[] workd,
            final int lworkl, final double[] workl, final double[] v,
            final intW info, final doubleW tol, final int[] iparam,
            final int[] ipntr, final intW ido)
    {
        // Call into ARPACK
        /*
        c  Reverse communication interface for the Implicitly Restarted Arnoldi
        c  iteration. This subroutine computes approximations to a few eigenpairs 
        c  of a linear operator "OP" with respect to a semi-inner product defined by 
        c  a symmetric positive semi-definite real matrix B. B may be the identity 
        c  matrix. NOTE: If the linear operator "OP" is real and symmetric 
        c  with respect to the real positive semi-definite symmetric matrix B, 
        c  i.e. B*OP = (OP')*B, then subroutine ssaupd should be used instead.
        c
        c  The computed approximate eigenvalues are called Ritz values and
        c  the corresponding approximate eigenvectors are called Ritz vectors.
        c
        c  dnaupd is usually called iteratively to solve one of the 
        c  following problems:
        c
        c  Mode 1:  A*x = lambda*x.
        c           ===> OP = A  and  B = I.
        c
        c  Mode 2:  A*x = lambda*M*x, M symmetric positive definite
        c           ===> OP = inv[M]*A  and  B = M.
        c           ===> (If M can be factored see remark 3 below)
        c
        c  Mode 3:  A*x = lambda*M*x, M symmetric semi-definite
        c           ===> OP = Real_Part{ inv[A - sigma*M]*M }  and  B = M. 
        c           ===> shift-and-invert mode (in real arithmetic)
        c           If OP*x = amu*x, then 
        c           amu = 1/2 * [ 1/(lambda-sigma) + 1/(lambda-conjg(sigma)) ].
        c           Note: If sigma is real, i.e. imaginary part of sigma is zero;
        c                 Real_Part{ inv[A - sigma*M]*M } == inv[A - sigma*M]*M 
        c                 amu == 1/(lambda-sigma). 
        c  
        c  Mode 4:  A*x = lambda*M*x, M symmetric semi-definite
        c           ===> OP = Imaginary_Part{ inv[A - sigma*M]*M }  and  B = M. 
        c           ===> shift-and-invert mode (in real arithmetic)
        c           If OP*x = amu*x, then 
        c           amu = 1/2i * [ 1/(lambda-sigma) - 1/(lambda-conjg(sigma)) ].
        c
        c  Both mode 3 and 4 give the same enhancement to eigenvalues close to
        c  the (complex) shift sigma.  However, as lambda goes to infinity,
        c  the operator OP in mode 4 dampens the eigenvalues more strongly than
        c  does OP defined in mode 3.
        c
        c  NOTE: The action of w <- inv[A - sigma*M]*v or w <- inv[M]*v
        c        should be accomplished either by a direct method
        c        using a sparse matrix factorization and solving
        c
        c           [A - sigma*M]*w = v  or M*w = v,
        c
        c        or through an iterative method for solving these
        c        systems.  If an iterative method is used, the
        c        convergence test must be more stringent than
        c        the accuracy requirements for the eigenvalue
        c        approximations.
        c
        c\Usage:
        c  call dnaupd
        c     ( IDO, BMAT, N, WHICH, NEV, TOL, RESID, NCV, V, LDV, IPARAM,
        c       IPNTR, WORKD, WORKL, LWORKL, INFO )
        c
        c\Arguments
        c  IDO     Integer.  (INPUT/OUTPUT)
        c          Reverse communication flag.  IDO must be zero on the first 
        c          call to dnaupd.  IDO will be set internally to
        c          indicate the type of operation to be performed.  Control is
        c          then given back to the calling routine which has the
        c          responsibility to carry out the requested operation and call
        c          dnaupd with the result.  The operand is given in
        c          WORKD(IPNTR(1)), the result must be put in WORKD(IPNTR(2)).
        c          -------------------------------------------------------------
        c          IDO =  0: first call to the reverse communication interface
        c          IDO = -1: compute  Y = OP * X  where
        c                    IPNTR(1) is the pointer into WORKD for X,
        c                    IPNTR(2) is the pointer into WORKD for Y.
        c                    This is for the initialization phase to force the
        c                    starting vector into the range of OP.
        c          IDO =  1: compute  Y = OP * Z  and Z = B * X where
        c                    IPNTR(1) is the pointer into WORKD for X,
        c                    IPNTR(2) is the pointer into WORKD for Y,
        c                    IPNTR(3) is the pointer into WORKD for Z.
        c          IDO =  2: compute  Y = B * X  where
        c                    IPNTR(1) is the pointer into WORKD for X,
        c                    IPNTR(2) is the pointer into WORKD for Y.
        c          IDO =  3: compute the IPARAM(8) real and imaginary parts 
        c                    of the shifts where INPTR(14) is the pointer
        c                    into WORKL for placing the shifts. See Remark
        c                    5 below.
        c          IDO =  4: compute Z = OP * X
        c          IDO = 99: done
        c          -------------------------------------------------------------
        c          After the initialization phase, when the routine is used in 
        c          the "shift-and-invert" mode, the vector B * X is already 
        c          available and does not need to be recomputed in forming OP*X.
        c             
        c  BMAT    Character*1.  (INPUT)
        c          BMAT specifies the type of the matrix B that defines the
        c          semi-inner product for the operator OP.
        c          BMAT = 'I' -> standard eigenvalue problem A*x = lambda*x
        c          BMAT = 'G' -> generalized eigenvalue problem A*x = lambda*B*x
        c
        c  N       Integer.  (INPUT)
        c          Dimension of the eigenproblem.
        c
        c  WHICH   Character*2.  (INPUT)
        c          'LM' -> want the NEV eigenvalues of largest magnitude.
        c          'SM' -> want the NEV eigenvalues of smallest magnitude.
        c          'LR' -> want the NEV eigenvalues of largest real part.
        c          'SR' -> want the NEV eigenvalues of smallest real part.
        c          'LI' -> want the NEV eigenvalues of largest imaginary part.
        c          'SI' -> want the NEV eigenvalues of smallest imaginary part.
        c
        c  NEV     Integer.  (INPUT)
        c          Number of eigenvalues of OP to be computed. 0 < NEV < N-1.
        c
        c  TOL     Double precision scalar.  (INPUT)
        c          Stopping criterion: the relative accuracy of the Ritz value 
        c          is considered acceptable if BOUNDS(I) .LE. TOL*ABS(RITZ(I))
        c          where ABS(RITZ(I)) is the magnitude when RITZ(I) is complex.
        c          DEFAULT = DLAMCH('EPS')  (machine precision as computed
        c                    by the LAPACK auxiliary subroutine DLAMCH).
        c
        c  RESID   Double precision array of length N.  (INPUT/OUTPUT)
        c          On INPUT: 
        c          If INFO .EQ. 0, a random initial residual vector is used.
        c          If INFO .NE. 0, RESID contains the initial residual vector,
        c                          possibly from a previous run.
        c          On OUTPUT:
        c          RESID contains the final residual vector.
        c
        c  NCV     Integer.  (INPUT)
        c          Number of columns of the matrix V. NCV must satisfy the two
        c          inequalities 2 <= NCV-NEV and NCV <= N.
        c          This will indicate how many Arnoldi vectors are generated 
        c          at each iteration.  After the startup phase in which NEV 
        c          Arnoldi vectors are generated, the algorithm generates 
        c          approximately NCV-NEV Arnoldi vectors at each subsequent update 
        c          iteration. Most of the cost in generating each Arnoldi vector is 
        c          in the matrix-vector operation OP*x. 
        c          NOTE: 2 <= NCV-NEV in order that complex conjugate pairs of Ritz 
        c          values are kept together. (See remark 4 below)
        c
        c  V       Double precision array N by NCV.  (OUTPUT)
        c          Contains the final set of Arnoldi basis vectors. 
        c
        c  LDV     Integer.  (INPUT)
        c          Leading dimension of V exactly as declared in the calling program.
        c
        c  IPARAM  Integer array of length 11.  (INPUT/OUTPUT)
        c          IPARAM(1) = ISHIFT: method for selecting the implicit shifts.
        c          The shifts selected at each iteration are used to restart
        c          the Arnoldi iteration in an implicit fashion.
        c          -------------------------------------------------------------
        c          ISHIFT = 0: the shifts are provided by the user via
        c                      reverse communication.  The real and imaginary
        c                      parts of the NCV eigenvalues of the Hessenberg
        c                      matrix H are returned in the part of the WORKL 
        c                      array corresponding to RITZR and RITZI. See remark 
        c                      5 below.
        c          ISHIFT = 1: exact shifts with respect to the current
        c                      Hessenberg matrix H.  This is equivalent to 
        c                      restarting the iteration with a starting vector
        c                      that is a linear combination of approximate Schur
        c                      vectors associated with the "wanted" Ritz values.
        c          -------------------------------------------------------------
        c
        c          IPARAM(2) = No longer referenced.
        c
        c          IPARAM(3) = MXITER
        c          On INPUT:  maximum number of Arnoldi update iterations allowed. 
        c          On OUTPUT: actual number of Arnoldi update iterations taken. 
        c
        c          IPARAM(4) = NB: blocksize to be used in the recurrence.
        c          The code currently works only for NB = 1.
        c
        c          IPARAM(5) = NCONV: number of "converged" Ritz values.
        c          This represents the number of Ritz values that satisfy
        c          the convergence criterion.
        c
        c          IPARAM(6) = IUPD
        c          No longer referenced. Implicit restarting is ALWAYS used.  
        c
        c          IPARAM(7) = MODE
        c          On INPUT determines what type of eigenproblem is being solved.
        c          Must be 1,2,3,4; See under \Description of dnaupd for the 
        c          four modes available.
        c
        c          IPARAM(8) = NP
        c          When ido = 3 and the user provides shifts through reverse
        c          communication (IPARAM(1)=0), dnaupd returns NP, the number
        c          of shifts the user is to provide. 0 < NP <=NCV-NEV. See Remark
        c          5 below.
        c
        c          IPARAM(9) = NUMOP, IPARAM(10) = NUMOPB, IPARAM(11) = NUMREO,
        c          OUTPUT: NUMOP  = total number of OP*x operations,
        c                  NUMOPB = total number of B*x operations if BMAT='G',
        c                  NUMREO = total number of steps of re-orthogonalization.        
        c
        c  IPNTR   Integer array of length 14.  (OUTPUT)
        c          Pointer to mark the starting locations in the WORKD and WORKL
        c          arrays for matrices/vectors used by the Arnoldi iteration.
        c          -------------------------------------------------------------
        c          IPNTR(1): pointer to the current operand vector X in WORKD.
        c          IPNTR(2): pointer to the current result vector Y in WORKD.
        c          IPNTR(3): pointer to the vector B * X in WORKD when used in 
        c                    the shift-and-invert mode.
        c          IPNTR(4): pointer to the next available location in WORKL
        c                    that is untouched by the program.
        c          IPNTR(5): pointer to the NCV by NCV upper Hessenberg matrix
        c                    H in WORKL.
        c          IPNTR(6): pointer to the real part of the ritz value array 
        c                    RITZR in WORKL.
        c          IPNTR(7): pointer to the imaginary part of the ritz value array
        c                    RITZI in WORKL.
        c          IPNTR(8): pointer to the Ritz estimates in array WORKL associated
        c                    with the Ritz values located in RITZR and RITZI in WORKL.
        c
        c          Note: IPNTR(9:13) is only referenced by dneupd. See Remark 2 below.
        c
        c          IPNTR(9): pointer to the real part of the NCV RITZ values of the 
        c                    original system.
        c          IPNTR(10): pointer to the imaginary part of the NCV RITZ values of 
        c                     the original system.
        c          IPNTR(11): pointer to the NCV corresponding error bounds.
        c          IPNTR(12): pointer to the NCV by NCV upper quasi-triangular
        c                     Schur matrix for H.
        c          IPNTR(13): pointer to the NCV by NCV matrix of eigenvectors
        c                     of the upper Hessenberg matrix H. Only referenced by
        c                     dneupd if RVEC = .TRUE. See Remark 2 below.
        c          Note: IPNTR(9:13) is only referenced by dneupd. See Remark 2 below.
        c          IPNTR(14): pointer to the NP shifts in WORKL. See Remark 5 below.
        c          -------------------------------------------------------------
        c          
        c  WORKD   Double precision work array of length 3*N.  (REVERSE COMMUNICATION)
        c          Distributed array to be used in the basic Arnoldi iteration
        c          for reverse communication.  The user should not use WORKD 
        c          as temporary workspace during the iteration. Upon termination
        c          WORKD(1:N) contains B*RESID(1:N). If an invariant subspace
        c          associated with the converged Ritz values is desired, see remark
        c          2 below, subroutine dneupd uses this output.
        c          See Data Distribution Note below.  
        c
        c  WORKL   Double precision work array of length LWORKL.  (OUTPUT/WORKSPACE)
        c          Private (replicated) array on each PE or array allocated on
        c          the front end.  See Data Distribution Note below.
        c
        c  LWORKL  Integer.  (INPUT)
        c          LWORKL must be at least 3*NCV**2 + 6*NCV.
        c
        c  INFO    Integer.  (INPUT/OUTPUT)
        c          If INFO .EQ. 0, a randomly initial residual vector is used.
        c          If INFO .NE. 0, RESID contains the initial residual vector,
        c                          possibly from a previous run.
        c          Error flag on output.
        c          =  0: Normal exit.
        c          =  1: Maximum number of iterations taken.
        c                All possible eigenvalues of OP has been found. IPARAM(5)  
        c                returns the number of wanted converged Ritz values.
        c          =  2: No longer an informational error. Deprecated starting
        c                with release 2 of ARPACK.
        c          =  3: No shifts could be applied during a cycle of the 
        c                Implicitly restarted Arnoldi iteration. One possibility 
        c                is to increase the size of NCV relative to NEV. 
        c                See remark 4 below.
        c          = -1: N must be positive.
        c          = -2: NEV must be positive.
        c          = -3: NCV-NEV >= 2 and less than or equal to N.
        c          = -4: The maximum number of Arnoldi update iteration 
        c                must be greater than zero.
        c          = -5: WHICH must be one of 'LM', 'SM', 'LR', 'SR', 'LI', 'SI'
        c          = -6: BMAT must be one of 'I' or 'G'.
        c          = -7: Length of private work array is not sufficient.
        c          = -8: Error return from LAPACK eigenvalue calculation;
        c          = -9: Starting vector is zero.
        c          = -10: IPARAM(7) must be 1,2,3,4.
        c          = -11: IPARAM(7) = 1 and BMAT = 'G' are incompatable.
        c          = -12: IPARAM(1) must be equal to 0 or 1.
        c          = -9999: Could not build an Arnoldi factorization.
        c                   IPARAM(5) returns the size of the current Arnoldi
        c                   factorization.
        c
        c\Remarks
        c  1. The computed Ritz values are approximate eigenvalues of OP. The
        c     selection of WHICH should be made with this in mind when
        c     Mode = 3 and 4.  After convergence, approximate eigenvalues of the
        c     original problem may be obtained with the ARPACK subroutine dneupd.
        c
        c  2. If a basis for the invariant subspace corresponding to the converged Ritz 
        c     values is needed, the user must call dneupd immediately following 
        c     completion of dnaupd. This is new starting with release 2 of ARPACK.
        c
        c  3. If M can be factored into a Cholesky factorization M = LL'
        c     then Mode = 2 should not be selected.  Instead one should use
        c     Mode = 1 with  OP = inv(L)*A*inv(L').  Appropriate triangular 
        c     linear systems should be solved with L and L' rather
        c     than computing inverses.  After convergence, an approximate
        c     eigenvector z of the original problem is recovered by solving
        c     L'z = x  where x is a Ritz vector of OP.
        c
        c  4. At present there is no a-priori analysis to guide the selection of NCV 
        c     relative to NEV.  The only formal requirement is that NCV > NEV + 2.
        c     However, it is recommended that NCV .ge. 2*NEV+1.  If many problems of
        c     the same type are to be solved, one should experiment with increasing
        c     NCV while keeping NEV fixed for a given test problem.  This will 
        c     usually decrease the required number of OP*x operations but it
        c     also increases the work and storage required to maintain the orthogonal
        c     basis vectors.  The optimal "cross-over" with respect to CPU time
        c     is problem dependent and must be determined empirically. 
        c     See Chapter 8 of Reference 2 for further information.
        c
        c  5. When IPARAM(1) = 0, and IDO = 3, the user needs to provide the 
        c     NP = IPARAM(8) real and imaginary parts of the shifts in locations 
        c         real part                  imaginary part
        c         -----------------------    --------------
        c     1   WORKL(IPNTR(14))           WORKL(IPNTR(14)+NP)
        c     2   WORKL(IPNTR(14)+1)         WORKL(IPNTR(14)+NP+1)
        c                        .                          .
        c                        .                          .
        c                        .                          .
        c     NP  WORKL(IPNTR(14)+NP-1)      WORKL(IPNTR(14)+2*NP-1).
        c
        c     Only complex conjugate pairs of shifts may be applied and the pairs 
        c     must be placed in consecutive locations. The real part of the 
        c     eigenvalues of the current upper Hessenberg matrix are located in 
        c     WORKL(IPNTR(6)) through WORKL(IPNTR(6)+NCV-1) and the imaginary part 
        c     in WORKL(IPNTR(7)) through WORKL(IPNTR(7)+NCV-1). They are ordered
        c     according to the order defined by WHICH. The complex conjugate
        c     pairs are kept together and the associated Ritz estimates are located in
        c     WORKL(IPNTR(8)), WORKL(IPNTR(8)+1), ... , WORKL(IPNTR(8)+NCV-1).
        c
        c-----------------------------------------------------------------------
         */
        do
        {
            ARPACK.getInstance().dnaupd(ido, bmat, n, which, nev, tol, resid, ncv, v, n, iparam, ipntr, workd,
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
    }

    /**
     * @param n
     * @param bmat
     * @param which
     * @param ncv
     * @param resid
     * @param workd
     * @param lworkl
     * @param workl
     * @param v
     * @param info
     * @param tol
     * @param iparam
     * @param ipntr
     * @throws RuntimeException
     */
    private void postProcess(final int n, final String bmat,
            final String which, final int ncv, final double[] resid,
            final double[] workd, final int lworkl, final double[] workl,
            final double[] v, final intW info, final doubleW tol,
            final int[] iparam, final int[] ipntr) throws RuntimeException
    {
        // POST-PROCESSing
        /*
            On the final return from dnaupd (indicated by ido = 99), the error flag info must be checked. 
            If info = 0, then no fatal errors have occurred and it is time to post-process using dneupd  
            to get eigenvalues of the original problem and the corresponding eigenvectors if desired. 
            In the case shown here (shift-invert and generalized), there are some subtleties to recovering 
            eigenvectors when ${\bf M}$ is ill-conditioned. This process is called eigenvector purification. 
            It prevents eigenvectors from   being corrupted with noise due to the presence of eigenvectors 
            corresponding to near infinite eigenvalues (See Chapter 4). These operations are completely 
            transparent to the user. The general calling sequence for dseupd is shown below:


           c
        c        %-----------------------------------------------%
        c        | No fatal errors occurred.                     |
        c        | Postprocess using DNEUPD.                     |
        c        |                                               |
        c        | Computed eigenvalues may be extracted.        |  
        c        |                                               |
        c        | Eigenvectors may also be computed now if      |
        c        | desired.  (indicated by rvec = .true.)        |
        c        |                                               |
        c        | The real part of the eigenvalue is returned   |
        c        | in the first column of the two-dimensional    |
        c        | array D, and the IMAGINARY part is returned   |
        c        | in the second column of D.  The corresponding |  
        c        | eigenvectors are returned in the first NEV    |
        c        | columns of the two-dimensional array V if     |
        c        | requested.  Otherwise, an orthogonal basis    |
        c        | for the invariant subspace corresponding to   |
        c        | the eigenvalues in D is returned in V.        |
        c        %-----------------------------------------------%
        c
                 rvec = .true. 
                 call dneupd ( rvec, 'A', select, d, d(1,2), v, ldv,
                    &        sigmar, sigmai, workev, bmat, n, which, nev, tol,
                    &        resid, ncv, v, ldv, iparam, ipntr, workd,
                    &        workl, lworkl, ierr )
        c    


        The input parameters bmat, n info are precisely the same parameters that 
        appear in the calling sequence of dnaupd. It is extremely IMPORTANT that 
        none of these parameters are altered between the final return from dsaupd 
        and the subsequent call to dneupd.

        The approximate eigenvalues of the original problem are returned with 
        real part array dr and imaginary part in the array di. Since the problem 
        is real, complex eigenvalues must come in complex conjugate pairs. 
        There is negligible additional cost to obtain eigenvectors. An orthonormal 
        (Schur) basis for the invariant subspace corresponding to the converged 
        approximate eigenvalues is always computed. In the above example, this 
        basis is overwritten with the eigenvectors in the array v. When the 
        eigenvectors corresponding to a complex conjugate   pair of eigenvaues 
        are computed, the vector corresponding to the eigenvalue with positive 
        imaginary part is stored with real and imaginary parts in consecutive 
        columns of v.   The eigenvector corresponding to the conjugate eigenvalue 
        is, of course, the conjugate of this vector. Both basis sets may be 
        obtained if desired but there is an additional storage cost of if both 
        are requested (in this case a separate n by nev array z must be supplied). 
        In some cases it may be desirable to have both basis sets.

        In the non-Hermitian case, the eigenvector basis is potentially 
        ill-conditioned and may not even exist. While, eigenvectors may have 
        physical meaning, they are generally not the best basis to use. If a 
        basis for a selected invariant subspace is required, then it is generally 
        better to compute a Schur basis. This will provide an orthogonal, hence 
        well conditioned, basis for the subspace. The sensitivity of a given 
        subspace to perturbations (such as roundoff error) is another question. 
        See § 4.6 for a brief discussion.

        If it is desirable to retain the Schur basis in v and storage is an issue, 
        the user may elect to call this routine once for each desired eigenvector 
        and store it peripherally. There is also the option of computing a selected 
        set of these vectors with a single call.

        The input parameters that must be specified are

        The logical variable rvec = .true. if eigenvectors are requested .false. 
        if only eigenvalues are desired.
        The character*1 parameter howmny specifies how many eigenvectors are desired. 
        howmny = 'A': compute nev eigenvectors; howmny = 'P': Compute nev Schur vectors; 
        howmny = 'S': compute some of the eigenvectors, specified by the logical array select.
        sigmar, sigmai should contain the real and imaginary portions, respectively, 
        of the shift that was used if iparam(7) = 3 or 4. Neither is referenced if iparam(7) = 1 or 2.

        When requested, the eigenvectors returned by dneupd are normalized   
        to have unit length with respect to the semi-inner product that was used. 
        Thus, if they will have unit length in the standard 2-norm. In general, 
        a computed eigenvector will satisfy .Eigenvectors corresponding to a 
        complex conjugate pair of eigenvalues with are returned with stored 
        in the j-th column and stored in the (j+1)-st column of the eigenvector 
        matrix when and are the j-th and (j+1)-st eigenvalues. This is the same 
        storage convention as the one used for LAPACK. Note that implies .         
        */
        
        if (info.val < 0)
        {
            String err = null;
            if (info.val == 1)
                err= "The Schur form computed by LAPACK routine dlahqr"
                        + "could not be reordered by LAPACK routine dtrsen."
                        + "Re-enter subroutine DNEUPD with IPARAM(5)=NCV and"
                        + "increase the size of the arrays DR and DI to have"
                        + "dimension at least dimension NCV and allocate at least NCV"
                        + "columns for Z. NOTE: Not necessary if Z and V share"
                        + "the same space. Please notify the authors if this error"
                        + "occurs.";
            else if (info.val == -1)
                err = "N must be positive.";
            else if (info.val == -2)
                err = "NEV must be positive.";
            else if (info.val == -3)
                err = "NCV-NEV >= 2 and less than or equal to N.";
            else if (info.val == -5)
                err = "WHICH must be one of 'LM', 'SM', 'LR', 'SR', 'LI', 'SI'";
            else if (info.val == -6)
                err = "BMAT must be one of 'I' or 'G'.";
            else if (info.val == -7)
                err = "Length of private work WORKL array is not sufficient.";
            else if (info.val == -8)
                err = "Error return from calculation of a real Schur form."
                        + "Informational error from LAPACK routine dlahqr.";
            else if (info.val == -9)
                err = "Error return from calculation of eigenvectors."
                        + "Informational error from LAPACK routine dtrevc.";
            else if (info.val == -10)
                err = "IPARAM(7) must be 1,2,3,4.";
            else if (info.val == -11)
                err = "IPARAM(7) = 1 and BMAT = 'G' are incompatible.";
            else if (info.val == -12)
                err = "HOWMNY = 'S' not yet implemented";
            else if (info.val == -13)
                err = "HOWMNY must be one of 'A' or 'P' if RVEC = .true.";
            else if (info.val == -14)
                err = "DNAUPD did not find any eigenvalues to sufficient accuracy";
            throw new RuntimeException("ARPACK error: snaupd(1) returned with info = " + info.val+ "\n"+err);
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
            final double[] dReal = new double[nev + 2];

            // Imaginay part of the output
            final double[] dImg = new double[nev + 2];

            // Eigenvectors
            final double[] z = new double[n * (nev + 1)];

            // workspace
            final double[] workev = new double[3 * ncv];

            // Call into ARPACK
            ARPACK.getInstance().dneupd(rvec, "A", select, dReal, dImg, z, n, sigmar, sigmai, workev, bmat, n, which,
                    new intW(nev), tol.val, resid, ncv, v, n, iparam, ipntr, workd, workl, lworkl, ierr);

            // Process the result
            if ((ierr.val != 0))
            {
                throw new RuntimeException("ARPACK error: dneupd returned with info = " + ierr.val);
            }
            else
            {
                eigenvalues = Complex.create.array(nev);
                rightEigenvectors = Complex.create.array(n, nev);

                for (int i = 0; i < nev; i++)
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

    public IComplexArray getLeftEigenvector(final int i)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNumberOfRequestedEigenvalues(int _nev)
    {
        if(_nev > this.matrix.columns())
            throw new IllegalArgumentException("there cannot be more eigenvalues than columns of matrix.");
        this.nev = _nev;
    }
}
