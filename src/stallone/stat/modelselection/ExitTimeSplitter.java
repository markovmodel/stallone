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
    private int nburnin = 1000, nsample = 10000;

    // input
    private IIntList states=intsNew.list(0);
    private IDoubleList exitTimes=doublesNew.list(0);
    
    // reformatted input
    private double[][] exitTimesByState;
    private int[][] exitTimeIndexesByState;
    
    // Splitting algorithm
    private ExitTimes[] et;
    
    // Splitting result
    private boolean[] isStateSplit;
    private int[] newStates;
    
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

    public void run()
    {
        // count states
        int nstates = ints.max(states)+1;
        exitTimesByState = new double[nstates][];
        exitTimeIndexesByState = new int[nstates][];
        isStateSplit = new boolean[nstates];
        et = new ExitTimes[nstates];
        newStates = states.copy().getArray();
        
        // rearrange lifetimes by states
        int nstatesAfterSplit = nstates;
        for (int i=0; i<nstates; i++)
        {
            IIntArray I = ints.findAll(states, i);
            
            exitTimeIndexesByState[i] = I.getArray();
            
            exitTimesByState[i] = doubles.subToNew(exitTimes, I).getArray();
            
            // if there is more than one event, try splitting
            if (exitTimesByState[i].length > 1)
            {
                et[i] = new ExitTimes(exitTimesByState[i]);
                et[i].run(nburnin, nsample);

                // is split?
                isStateSplit[i] = (et[i].getNumberOfStates() > 1.5);
                
                if (isStateSplit[i])
                {
                    int state1 = i;
                    int state2 = nstatesAfterSplit;
                    
                    double[] par = et[i].getMeanK2();
                    double a = par[0];
                    double k1 = par[1];
                    double k2 = par[2];
                    for (int j=0; j<exitTimesByState[i].length; j++)
                    {
                        if (a*k1*Math.exp(-k1*exitTimesByState[i][j]) < (1-a)*k2*Math.exp(-k2*exitTimesByState[i][j]))
                            newStates[exitTimeIndexesByState[i][j]] = state1;
                        else
                            newStates[exitTimeIndexesByState[i][j]] = state2;
                    }
                    
                    nstatesAfterSplit++;
                }
            }
        }
        
    }
    
    public boolean hasAnySplits()
    {
        boolean res = false;
        for (int i=0; i<isStateSplit.length; i++)
            if (isStateSplit[i])
                res = true;
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
    
    

}
