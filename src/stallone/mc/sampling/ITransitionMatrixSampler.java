/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface ITransitionMatrixSampler
{
    /**
     * Sets the count matrix used for sampling. Assumes that the prior (if desired) is included
     * @param counts
     */
    public void init(IDoubleArray counts);

    /**
     * Sets the count matrix used for sampling. Assumes that the prior (if desired) is included
     * @param counts
     */
    public void init(IDoubleArray counts, IDoubleArray Tinit);
    
    /**
     * Generates a new sample
     * @param steps the number of sampling steps taken before the next sample is returned
     * @return 
     */
    public IDoubleArray sample(int steps);
    
    /**
     * Returns the log-likelihood of the current sample
     * @return 
     */
    public double logLikelihood();
}
