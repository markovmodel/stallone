/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat.modelselection;

import static stallone.api.API.*;
/**
 *
 * @author noe
 */
public class Splitter
{
    private double[] exitTimes;
    private ExitTimes et;

    public Splitter(double[] _exitTimes, int _nburnin, int _nsample)
    {
        exitTimes = _exitTimes;
        et = new ExitTimes(_exitTimes);
        et.run(_nburnin, _nsample);
    }

    public Splitter(double[] _exitTimes)
    {
        exitTimes = _exitTimes;
        et = new ExitTimes(_exitTimes);
        et.run(1000, 10000);
    }

    public boolean split()
    {
        return(et.getNumberOfStates() > 1.5);
    }

    /**
     * Returns a vector of 0's and 1's, depending to which state the corresponding exit time is assigned.
     * Return a vector of all 0's if only one state is found.
     * @return
     */
    public int[] getAssignment()
    {
        int[] res = new int[exitTimes.length];

        if (et.getNumberOfStates() <= 1.5)
        {
            return res;
        }
        else
        {
            double[] par = et.getMeanK2();
            double a = par[0];
            double k1 = par[1];
            double k2 = par[2];

            for (int i=0; i<res.length; i++)
            {
                if (a*k1*Math.exp(-k1*exitTimes[i]) < (1-a)*k2*Math.exp(-k2*exitTimes[i]))
                    res[i] = 0;
                else
                    res[i] = 1;
            }
            return res;
        }
    }

    /**
     * Get array of exit time arrays (either one or two arrays)
     * @return
     */
    public double[][] getExitTimes()
    {
        if (split())
        {
            int[] assign = getAssignment();
            int[] I0 = intArrays.findAll(assign, 0);
            int[] I1 = intArrays.findAll(assign, 1);
            double[] times1 = doubleArrays.subarray(exitTimes, I0);
            double[] times2 = doubleArrays.subarray(exitTimes, I1);
            return new double[][]{times1, times2};
        }
        else
            return new double[][]{exitTimes};
    }

}
