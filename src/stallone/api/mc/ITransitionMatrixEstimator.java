/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface ITransitionMatrixEstimator
{
    /**
     * Sets the posterior count matrix (observed + prior counts)
     * @param C posterior count matrix
     */
    public void setCounts(IDoubleArray C);

    /**
     * Sets the maximum number of iterations that will be performed.
     * Has no effect for non-iterative estimators
     * @param nmax 
     */
    public void setMaxIter(int nmax);

    
    /**
     * Sets the convergence criterion. Convergence accepted when the likelihood has not changed more than 1
     * for nIterPer1 Consecutive steps.
     * Has no effect for non-iterative estimators
     * @param nIterPer1
     */
    public void setConvergence(int niter);
    
    
    /**
     * Runs the estimator.
     */
    public void estimate();

    
    /**
     * Returns the transition matrix.
     * @return 
     */
    public IDoubleArray getTransitionMatrix();
    
    
    /**
     * Returns the likelihood sequence
     * @return 
     */
    public double[] getLikelihoodHistory();

    
}
