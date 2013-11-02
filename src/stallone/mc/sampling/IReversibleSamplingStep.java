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
public interface IReversibleSamplingStep
{
    public void init(IDoubleArray _C, IDoubleArray _T, IDoubleArray _mu);
    
    public boolean step();
}
