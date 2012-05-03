/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.ints.PrimitiveIntArray;
import stallone.stat.DiscreteDistributions;

/**
Generates a time- and space-discrete trajectory
@author noe
 */
public class MarkovChain
{
    private IDoubleArray T;
    private DiscreteDistributions dd;

    protected MarkovChain()
    {}
    
    public MarkovChain(IDoubleArray _T)
    {
        init(_T);
    }
    
    protected final void init(IDoubleArray _T)
    {
        this.T = _T;
        dd = new DiscreteDistributions(_T);
    }
    
    public IDoubleArray getTransitionMatrix()
    {
        return (T);
    }

    /**
     * @param s starting state
     * @param N total trajectory length
     * @param nskip number of steps between saved transitions
     * @return random trajectory of length N/nskip + 1
     */
    public IIntArray randomTrajectory(int s, int N, int nskip)
    {

        int[] res = new int[(N / nskip)];
        int c = s;
        int k = 0;
        for (int i = 0; i < N; i++)
        {
            c = dd.sample(c);

            if (i % nskip == 0)
            {
                res[k++] = c;
            }
        }
        return (new PrimitiveIntArray(res));
    }

    public IIntArray randomTrajectory(int s, int N)
    {
        return (randomTrajectory(s, N, 1));
    }

    /**
    Generates a random Trajectory of length N starting from s using the transfer operator
     */
    public void printRandomTrajectory(int s, int N)
    {
        int c = s;
        System.out.println(c);
        for (int i = 0; i < N; i++)
        {
            c = dd.sample(c);
            System.out.println(c);
        }
    }
}
