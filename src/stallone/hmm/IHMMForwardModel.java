/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IHMMParameters;

/**
 * This interface needs to be implemented by the HMM user in order to provide the
 * transition and output probabilities for his specific model
 * @author noe
 */
public interface IHMMForwardModel
{
    /**
     * Returns the number of states
     * @return the number of states
     */
    public int getNStates();

    /**
     * Returns the number of observation trajectories
     * @return the number of observation trajectories
     */
    public int getNObs();
        
    /**
     * Returns the number of observations at trajectory traj
     * @return the number of observations in this trajectory
     */
    public int getNObs(int traj);
    
    /**
     * Returns initial state probability
     * @param traj trajectory index
     * @param state state index
     * @return
     */
    public double getP0(int traj, int state);

    /**
     * Calculates the state-to-state transition probability
     * @param traj The trajectory index
     * @param timeindex1 time index 1
     * @param state1 source state index 1
     * @param timeindex2 time index 2
     * @param state2 target state index 2
     * @return the transition probability 
     */
    public double getPtrans(int traj, int timeindex1, int state1, int state2);

    /**
     * Calculates the output probability of the observation at time t
     * @param traj trajectory index
     * @param timeindex time index
     * @param state state index
     * @return the output probability
     */
    public double getPout(int traj, int timeindex, int state);


    /**
     * Sets the estimated transition counts.
     * @param C a nxn count matrix where n is the number of states
     */
    public void setTransitionCounts(IDoubleArray C);
    
    /**
     * Sets the parameters for the output model of the given state
     * @param state the state index
     * @param par the parameter list
     */
    public void setOutputParameters(int state, IDoubleArray par);
    
    public IHMMParameters getParameters();
    
}
