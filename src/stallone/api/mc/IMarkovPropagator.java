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
public interface IMarkovPropagator
{
    /**
     * Sets the basic propagator
     * @param _P the propagator
     */
    public void set(IDoubleArray _P);

    /**
     * 
     * @param t the time
     * @return the propagator for time t
     */
    public IDoubleArray propagate(double t);    
}
