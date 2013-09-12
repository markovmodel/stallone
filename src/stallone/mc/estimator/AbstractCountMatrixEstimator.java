/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.estimator;

import java.util.LinkedList;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.api.mc.ICountMatrixEstimator;

/**
 *
 * @author noe
 */
public abstract class AbstractCountMatrixEstimator implements ICountMatrixEstimator
{
    // input
    protected LinkedList<IIntArray> input = new LinkedList<IIntArray>();
    protected int lag = 1;

    // output
    protected IDoubleArray C = null;

    public AbstractCountMatrixEstimator(IIntArray traj)
    {
        input.add(traj);
    }
    
    public AbstractCountMatrixEstimator(Iterable<IIntArray> trajs)
    {
        for (IIntArray traj:trajs)
            input.add(traj);
    }
        
    @Override
    public void setLag(int _lag)
    {
        this.lag = _lag;
    }
    
    @Override
    public void addInput(IIntArray traj)
    {
        input.add(traj);
    }

    @Override
    public void addInput(Iterable<IIntArray> trajs)
    {
        for (IIntArray traj:trajs)
            input.add(traj);
    }
    
    @Override
    public IDoubleArray estimate()
    {
        // determine number of states
        int n = 0;
        for (IIntArray traj:input)
        {
            n = Math.max(Ints.util.max(traj)+1,n);
        }
        
        // initialize C
        C = Doubles.create.array(n, n);

        this.count();
        
        return(C);
    }

    /**
     * constructs counts from input. Count matrix must be available
     */
    protected abstract void count();
    
    @Override
    public IDoubleArray getCountMatrix()
    {
        return(C);
    }
    
}
