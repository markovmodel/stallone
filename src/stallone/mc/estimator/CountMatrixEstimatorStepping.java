/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.estimator;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class CountMatrixEstimatorStepping extends AbstractCountMatrixEstimator
{
    public CountMatrixEstimatorStepping(IIntArray traj)
    {
        super(traj);
    }
    
    public CountMatrixEstimatorStepping(Iterable<IIntArray> trajs)
    {
        super(trajs);
    }

    /**
     * constructs counts from input. Count matrix must be available
     */
    @Override
    protected void count()
    {
        for (IIntArray traj : super.input)
        {
            for (int i=0; i<traj.size()-lag; i+=lag)
            {
                int k = traj.get(i);
                int l = traj.get(i+lag);
                super.C.set(k, l, super.C.get(k,l)+1);
            }
        }
    }    
}
