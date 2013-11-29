/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface IHMM
{
    /**
     * Returns the HMM parameters
     * @return 
     */
    public IHMMParameters getParameters();

    /**
     * Short-cut method to obtain the hidden transition matrix
     * @return 
     */
    public IDoubleArray getTransitionMatrix();

    /**
     * Short-cut method to obtain the output model parameter matrix. 
     * For a discrete HMM, these are simply the discrete probability distributions.
     * @return 
     */
    public IDoubleArray getOutputParameters();
    
    /**
     * Returns the Hidden variables (alpha, beta, gamma, likelihood)
     * @return 
     */
    public IHMMHiddenVariables getHidden(int itraj);

    /**
     * Returns the viterbi path
     * @return 
     */
    public List<IIntArray> viterbi();

    /**
     * Short-cut to the log-likelihood
     * @return 
     */
    public double getLogLikelihood();
}
