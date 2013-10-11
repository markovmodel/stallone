/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface ICountMatrixEstimator
{
    public void addInput(IIntArray traj);
    
    public void addInput(Iterable<IIntArray> traj);
    
    public void setLag(int lag);
    
    public IDoubleArray estimate();
    
    public IDoubleArray getCountMatrix();
}
