/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat.modelselection;

import static stallone.api.API.*;
import stallone.api.doubles.IDoubleList;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;

/**
 *
 * @author noe
 */
public class ExitTimeSplitter
{

    public static boolean VERBOSE = false;
    private double splitAt = 1.5;

    private int nburnin = 1000, nsample = 100000;
    // input
    private IIntList states = intsNew.list(0);
    private IDoubleList exitTimes = doublesNew.list(0);
    // reformatted input
    private double[][] exitTimesByState;
    private int[][] exitTimeIndexesByState;
    // Splitting algorithm
    private ExitTimes[] et;
    // Splitting result
    private double[] populatedStates;
    private boolean[] isStateSplit;
    private int[] newStates;
    private double[][] splittingParameters;

    public ExitTimeSplitter(int _nburnin, int _nsample)
    {
        nburnin = _nburnin;
        nsample = _nsample;
    }

    public ExitTimeSplitter()
    {
    }

    public void add(int state, double lifetime)
    {
        states.append(state);
        exitTimes.append(lifetime);
    }

    public void setSplitAt(double _splitAt)
    {
        splitAt = _splitAt;
    }

    public int getNsamples(int i)
    {
        if (exitTimesByState[i].length > 10000)
            return 10000;
        if (exitTimesByState[i].length > 1000)
            return 100000;
        return 1000000;
    }

    public void run()
    {
        // count states
        int nstates = ints.max(states) + 1;
        populatedStates = new double[nstates];
        exitTimesByState = new double[nstates][];
        exitTimeIndexesByState = new int[nstates][];
        isStateSplit = new boolean[nstates];
        splittingParameters = new double[nstates][3];
        et = new ExitTimes[nstates];
        newStates = states.copy().getArray();

        // rearrange lifetimes by states
        int nstatesAfterSplit = nstates;
        for (int i = 0; i < nstates; i++)
        {
            IIntArray I = ints.findAll(states, i);

            exitTimeIndexesByState[i] = I.getArray();

            exitTimesByState[i] = doubles.subToNew(exitTimes, I).getArray();

            // if there is more than one event, try splitting
            if (exitTimesByState[i].length > 1)
            {
                et[i] = new ExitTimes(exitTimesByState[i]);
                et[i].run(nburnin, getNsamples(i));

                // is split?
                isStateSplit[i] = (et[i].getNumberOfStates() > splitAt);
                populatedStates[i] = et[i].getNumberOfStates();

                if (isStateSplit[i])
                {
                    int state1 = i;
                    int state2 = nstatesAfterSplit;

                    double[] par = et[i].getMeanK2();
                    double a = par[0];
                    splittingParameters[i][0] = a;
                    double k1 = par[1];
                    splittingParameters[i][1] = k1;
                    double k2 = par[2];
                    splittingParameters[i][2] = k2;
                    for (int j = 0; j < exitTimesByState[i].length; j++)
                    {
                        if (a * k1 * Math.exp(-k1 * exitTimesByState[i][j]) < (1 - a) * k2 * Math.exp(-k2 * exitTimesByState[i][j]))
                        {
                            newStates[exitTimeIndexesByState[i][j]] = state1;
                        }
                        else
                        {
                            newStates[exitTimeIndexesByState[i][j]] = state2;
                        }
                    }

                    nstatesAfterSplit++;
                }
                else
                {
                    splittingParameters[i][0] = 1;
                    splittingParameters[i][1] = et[i].getMeanK();
                }
            }
        }

        if (VERBOSE)
        {
            printResult();
        }
    }

    public boolean hasAnySplits()
    {
        boolean res = false;
        for (int i = 0; i < isStateSplit.length; i++)
        {
            if (isStateSplit[i])
            {
                res = true;
            }
        }
        return res;
    }

    public boolean isSplit(int oldStateIndex)
    {
        return isStateSplit[oldStateIndex];
    }

    public int[] getNewStateAssignment()
    {
        return newStates;
    }

    /**
     * Returns the split parameters for the requested state
     * @param state
     * @return if not split: (1, k, 0), where k is the exit rate
     * if split: (a, k1, k2), where the exit time model is a*exp(-k1 t) + (1-a)*exp(-k2 t)
     */
    public double[] getSplittingParameters(int state)
    {
        return splittingParameters[state];
    }

    public void printResult()
    {
        System.out.println("state\tevents\tsamples\tnstates\tsplit?\tamplitude\trate k1\trate k2");
        for (int i = 0; i < isStateSplit.length; i++)
        {
            System.out.println(i
                    + "\t" + exitTimesByState[i].length
                    + "\t" + getNsamples(i)
                    + "\t" + populatedStates[i]
                    + "\t" + isStateSplit[i]
                    + "\t" + splittingParameters[i][0] + "\t" + splittingParameters[i][1] + "\t" + splittingParameters[i][2]);
        }
    }
}
