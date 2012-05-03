/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.api.hmm;

import stallone.api.doubles.IDoubleArray;

/**
 * So far this is a Marker interface that should be overwritten in order to
 * allow for proper data handling in the HMM
 * @author noe
 */
public interface IHMMParameters
{
    /**
     * The user must provide a deep (completely independent) copy of this object.
     * @return deep copy of this parameter instance.
     */
    public IHMMParameters copy();
    
    /**
     * Returns the number of states
     * @return 
     */
    public int getNStates();
    
    /**
     * Returns the parameters of the specified output model.
     * @param state the state index
     * @return 
     */
    public IDoubleArray getOutputParameters(int state);

    /**
     * Sets the parameters of the specified output model
     * @param state
     * @param par 
     */
    public void setOutputParameters(int state, IDoubleArray par);
    
    /**
     * Returns the transition matrix
     * @return 
     */
    public IDoubleArray getTransitionMatrix();
    
    /**
     * Sets the transition matrix
     * @param T 
     */
    public void setTransitionMatrix(IDoubleArray T);
    
    /**
     * Returns the initial state distribution.
     * @return 
     */
    public IDoubleArray getInitialDistribution();

    /**
     * Sets the initial state distribution.
     */
    public void setInitialDistribution(IDoubleArray p0);
    
    public boolean isReversible();
    
    public boolean isStationary();
    
}
