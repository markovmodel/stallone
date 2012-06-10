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
public class Merger
{
    private double[][] exitTimes;
    private double[][] numberOfStates;
    
    public Merger(double[][] _exitTimes, int _nburnin, int _nsample)
    {
        exitTimes = _exitTimes;
        run(_nburnin, _nsample);
    }

    public Merger(double[][] _exitTimes)
    {
        exitTimes = _exitTimes;
        run(1000, 10000);
    }
    
    private void run(int _nburnin, int _nsample)
    {
        for (int i=1; i<exitTimes.length; i++)
        {
            for (int j=i+1; j<exitTimes.length; j++)
            {
                double[] timesij = doubleArrays.concat(exitTimes[i],exitTimes[j]);
                ExitTimes et = new ExitTimes(timesij);
                et.run(_nburnin, _nsample);
                numberOfStates[i][j] = et.getNumberOfStates();
                numberOfStates[j][i] = et.getNumberOfStates();
            }
        }
    }
    
    /**
     * Returns true if any merge has occurred
     * @return 
     */
    public boolean hasMerged()
    {
        for (int i=1; i<numberOfStates.length; i++)
        {
            for (int j=i+1; j<numberOfStates.length; j++)
            {
                if (numberOfStates[i][j] < 1.5)
                    return true;
            }
        }
        return false;
    }
    
    private int findSet(int[][] sets, int state)
    {
        for (int i=0; i<sets.length; i++)
            if (intArrays.contains(sets[i], state))
                return i;
        return -1;
    }
    
    /**
     * Returns a vector of the 
     * @return 
     */
    public int[][] getMerge()
    {
        // collect nstates
        double[] nstatesLin = new double[(numberOfStates.length*(numberOfStates.length-1))/2];
        int k=0;
        for (int i=1; i<numberOfStates.length; i++)
        {
            for (int j=i+1; j<numberOfStates.length; j++)
            {
                nstatesLin[k++] = numberOfStates[i][j];
            }
        }
        // sort
        int[] I = doubleArrays.sortedIndexes(nstatesLin);
        
        int[][] sets = new int[numberOfStates.length][1];
        for (int i=0; i<sets.length; i++)
            sets[i][0] = i;

        // try merging
        for (int i=0; i<I.length; i++)
        {
            if (nstatesLin[i] >= 1.5)
                return sets;

            // merge these two states:
            int state1 = i/numberOfStates.length;
            int state2 = i%numberOfStates.length;
            
            // get indexes of sets
            int set1 = findSet(sets, state1);
            int set2 = findSet(sets, state2);
            
            // check if all states of these sets are pairwise mergeable
            boolean merge = true;
            for (int j1=0; j1<sets[set1].length; j1++)
                for (int j2=0; j2<sets[set2].length; j2++)
                {
                    if (numberOfStates[sets[set1][j1]][sets[set2][j2]] >= 1.5)
                        merge = false;
                }
            
            // merge
            if (merge)
            {
                sets[set1] = intArrays.concat(sets[set1], sets[set2]);
                sets = intArrays.removeByIndex(sets, set2);
            }
        }
        
        return sets;
    }
    
}
