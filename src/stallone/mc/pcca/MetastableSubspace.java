/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.pcca;

import static stallone.api.API.*;

import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * Computes the dominant eigenvectors of a transition matrix and offers computations on this subspace,
 * such as PCCA and transition matrix coarse-graining
 * 
 * @author noe
 */
public class MetastableSubspace
{
    ////////////////////////////////////////////////////////////////////////////
    // CONSTANTS:
    ////////////////////////////////////////////////////////////////////////////
    private static double MINIMAL_MEMBERSHIP = 1e-4;
    private static boolean ENFORCE_REVERSIBILITY = true;
    
    ////////////////////////////////////////////////////////////////////////////
    // GENERIC QUANTITIES:
    ////////////////////////////////////////////////////////////////////////////
    // transition matrix
    private IDoubleArray T;
    // Eigenvalue decomposition of t
    private IEigenvalueDecomposition evd;
    // stationary distribution
    private IDoubleArray pi = null;
    // Diagonal matrix with stationary distribution
    private IDoubleArray Pi = null;

    
    ////////////////////////////////////////////////////////////////////////////
    // SUBSPACE-SPECIFIC QUANTITIES:
    ////////////////////////////////////////////////////////////////////////////
    // size of subspace
    private int subspaceSize;
    // pcca
    private PCCA pcca;
    // metastable state memberships
    private IDoubleArray M;
    // coarse-grained stationary distribution
    private IDoubleArray piC = null;
    // coarse-grained stationary distribution diagonal matrix
    private IDoubleArray PiC;
    // probability matrix to observe a microstate given a metastable state
    private IDoubleArray PObs;    
    // coarse-grained transition matrix
    private IDoubleArray Tcoarse;
    
    
    /**
     * 
     * @param T the transition matrix
     * @param nEigenvectors the number of dominant eigenvectors defining the metastable subspace
     */
    public MetastableSubspace(IDoubleArray _T)
    {
        this.T = _T;
        // Eigenvalue decomposition
        this.evd = alg.evd(T);
        // compute stationary distribution
        this.pi = msm.stationaryDistribution(T);
        this.Pi = doublesNew.diag(pi);
    }

    private void enforceMembershipBounds(IDoubleArray M)
    {
        for (int i = 0; i < M.size(); i++)
        {
            if (M.get(i) < MINIMAL_MEMBERSHIP)
            {
                M.set(i, MINIMAL_MEMBERSHIP);
            }
        }
        alg.normalizeRows(M, 1);
    }
    
    private IDoubleArray symmetrize(IDoubleArray C)
    {
        return alg.addWeightedToNew(0.5, C, 0.5, alg.transposeToNew(C));
    }

    private void ensureNonnegativeElements(IDoubleArray C)
    {
        // make sure correlation matrix is nonnegative
        for (int i = 0; i < C.rows(); i++)
        {
            for (int j = 0; j < C.columns(); j++)
            {
                if (C.get(i, j) < 0)
                {
                    C.set(i, j, 0);
                }
            }
        }
    }
    
    public void coarseGrain(int nStates)
    {
        this.subspaceSize = nStates;

        // do PCCA
        this.pcca = new PCCA();
        IDoubleArray evec = this.evd.getRightEigenvectorMatrix().viewReal();
        IDoubleArray evecSub = evec.viewBlock(0, 0, T.rows(), nStates);
        pcca.setEigenvectors(evecSub);
        pcca.perform();

        // PCCA memberships
        this.M = pcca.getFuzzy();
        enforceMembershipBounds(M);

        // coarse-grain stationary distribution
        this.piC = alg.product(alg.transposeToNew(M),pi);
        this.PiC = doublesNew.diag(piC);

        // restriction and interpolation operators
        IDoubleArray Res = alg.transposeToNew(M);
        IDoubleArray Int = alg.product(alg.product(Pi,M),alg.inverse(PiC));
        
        // compute observation probability
        this.PObs = Int;
        
        // compute coarse-grained correlation matrix
        IDoubleArray RItinv = alg.transposeToNew(alg.inverse(alg.product(Res,Int)));
        IDoubleArray IntT = alg.transposeToNew(Int);
        IDoubleArray ResT = alg.transposeToNew(Res);        
        IDoubleArray TC = alg.product(alg.product(alg.product(RItinv,IntT),T),ResT);
        IDoubleArray CC = alg.product(PiC, TC);

        // symmetrize if requested
        if (ENFORCE_REVERSIBILITY)
            CC = symmetrize(CC);
        
        // make sure matrix is nonnegative
        ensureNonnegativeElements(CC);
        
        // compute coarse-grained transition matrix
        this.Tcoarse = CC.copy();
        alg.normalizeRows(this.Tcoarse, 1);
    }

    public IDoubleArray getMemberships()
    {
        return M;
    }

    public IDoubleArray getObservationProbabilities()
    {
        return this.PObs;
    }
    
    public IDoubleArray getCoarseGrainedStationaryDistribution()
    {
        return this.piC;
    }

    public IDoubleArray getCoarseGrainedTransitionMatrix()
    {
        return this.Tcoarse;
    }    
}
