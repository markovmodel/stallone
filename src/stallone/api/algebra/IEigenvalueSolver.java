package stallone.api.algebra;

import stallone.api.doubles.IDoubleArray;



/**
 * Basic interface for the eigenvalue decomposition of a real valued n x n matrix.
 *
 * @author  Martin Senne
 */
public interface IEigenvalueSolver
{

    /**
     * Set matrix to do eigenvalue decomposition for.
     *
     * @param  m  is the matrix to set
     */
    public void setMatrix(IDoubleArray m);

    /**
     * Set whether to compute left eigenvectors.
     *
     * @param  left  true, if to compute left eigenvectors.
     */
    public void setPerformLeftComputation(boolean left);

    /**
     * Set whether to compute right eigenvectors.
     *
     * @param  right  true, if to compute right eigenvectors.
     */
    public void setPerformRightComputation(boolean right);

    public void setNumberOfRequestedEigenvalues(int nev);

    /**
     * Execute eigenvalue decomposition.
     */
    public void perform();

    /**
     * Gets the right complex eigenvector matrix, if available.
     */
    public IEigenvalueDecomposition getResult();
}
