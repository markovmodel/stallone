/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import static stallone.api.API.msm;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.api.ints.Ints;
import stallone.api.mc.IMarkovChain;
import stallone.ints.PrimitiveIntArray;
import stallone.ints.PrimitiveIntTools;
import stallone.stat.DiscreteDistribution_Old;
import stallone.stat.DiscreteDistributions;

/**
 * Generates a time- and space-discrete trajectory
 *
 * @author noe
 */
public class MarkovChain implements IMarkovChain
{
	/**
	 *  starting distribution p0. If not specified, the starting distribution
	 *  is the stationary distribution
	 */
	protected IDoubleArray p0;
    protected IDoubleArray T;
    protected DiscreteDistributions dd;
    
    // fixed starting state?
    private boolean fixedStartingState = false;
    private int s = 0;
    // starting distribution
    private DiscreteDistribution_Old p0dist;

    //skip
    private int nskip = 1;
    
    protected MarkovChain()
    {
    }

    public MarkovChain(IDoubleArray _T)
    {
        this.T = _T;
        dd = new DiscreteDistributions(_T);
    }

    public MarkovChain(IDoubleArray _startingDistribution, IDoubleArray _T)
    {
        this.T = _T;
        dd = new DiscreteDistributions(_T);
        this.p0 = _startingDistribution;
        p0dist = new DiscreteDistribution_Old(p0);
    }    
    
    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#setStartingState(int)
     */
    public void setStartingState(int _s)
    {
        fixedStartingState = true;
        s = _s;
    }
    
    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#setStartingDistribution(stallone.api.doubles.IDoubleArray)
     */
    public void setStartingDistribution(IDoubleArray _p0)
    {
        fixedStartingState = false;
        p0 = _p0;
        p0dist = new DiscreteDistribution_Old(p0);
    }

    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#getTransitionMatrix()
     */
    public IDoubleArray getTransitionMatrix()
    {
        return (T);
    }
    
    private int startingState()
    {
        if (fixedStartingState)
            return s;
        else
        {
            if (p0 == null)
            {
                p0 = msm.stationaryDistribution(T);
                p0dist = new DiscreteDistribution_Old(p0);
            }
            return p0dist.sample();
        }
    }
    
    /**
     * Sets the number of steps to be skipped in the output
     */
    private void setNSkip(int _nskip)
    {
        this.nskip = _nskip;
    }

    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#randomTrajectory(int)
     */
    public IIntArray randomTrajectory(int N)
    {
        int[] res = new int[(N / nskip)];
        int c = startingState();
        res[0] = c;
        int k = 1;
        for (int i = 1; i < N; i++)
        {
            c = dd.sample(c);

            if (i % nskip == 0)
            {
                res[k++] = c;
            }
        }
        return (new PrimitiveIntArray(res));
    }

    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#randomTrajectoryToState(int[])
     */
    public IIntArray randomTrajectoryToState(int[] endStates)
    {
        IIntList res = Ints.create.list(0);
        int c = startingState();
        res.append(c);

        int i = 0;
        while (!PrimitiveIntTools.contains(endStates, c))
        {
            c = dd.sample(c);

            if (i % nskip == 0)
            {
                res.append(c);
            }
            i++;
        }

        return (res);
    }

    /* (non-Javadoc)
     * @see stallone.mc.IMarkovChain#printRandomTrajectory(int)
     */
    public void printRandomTrajectory(int N)
    {
        int c = startingState();
        System.out.println(c);
        for (int i = 0; i < N; i++)
        {
            c = dd.sample(c);
            System.out.println(c);
        }
    }
}
