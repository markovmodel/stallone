/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import stallone.api.hmm.IHMM;
import stallone.api.hmm.IHMMHiddenVariables;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.api.ints.Ints;
import stallone.api.ints.IntsPrimitive;

/**
 *
 * @author noe
 */
public class IntSequenceUtilities
{
    public List<IIntArray> loadIntSequences(List<String> files)
            throws IOException
    {
        return IntSequence.create.intSequenceLoader(files).loadAll();
    }    

    public void writeIntSequences(List<IIntArray> data, List<String> files)
            throws IOException
    {
        if (data.size() != files.size())
            throw new IllegalArgumentException("Number of sequences is different from number of target files");
        
        for (int itraj=0; itraj<data.size(); itraj++)
        {
            PrintStream ps = new PrintStream(files.get(itraj));
            for (int i=0; i<data.get(itraj).size(); i++)
                ps.println(data.get(itraj).get(i));
            ps.close();
        }
    }    

    public int max(Iterable<IIntArray> paths)
    {
        int n = 0;
        for (IIntArray p : paths)
        {
            System.out.println("path length "+p.size());
            n = Math.max(n, Ints.util.max(p));
        }
        return n;
    }
    
    
    /**
     * Returns an array of [index, lifetime] for each contiguous path piece
     * @param path a sequence of integers
     * @return a list of state indexes and corresponding dwell times.
     */
    public List<int[]> lifetimesByEvent(IIntArray path)
    {
        List<int[]> res = new ArrayList();

        int begin = 0;
        for (int t = 1; t < path.size(); t++)
        {
            if (path.get(t) != path.get(begin))
            {
                int state = path.get(begin);
                int time = t - begin;
                res.add(new int[]{state,time});
                begin = t;
            }
        }

        // add last event.
        int state = path.get(begin);
        int time = path.size() - begin;
        res.add(new int[]{state,time});
        
        return res;
    }
    /**
     * Returns a list of times each integer value was continuously seen in the given path
     * @param path a sequence of integers
     * @return a list of dwell times for each occurring integer value.
     */
    public IIntList[] lifetimesByState(IIntArray path)
    {
        int nstates = Ints.util.max(path)+1;
        IIntList[] lists = new IIntList[nstates];
        for (int i = 0; i < lists.length; i++)
        {
            lists[i] = Ints.create.list(0);
        }

        int begin = 0;
        for (int t = 1; t < path.size(); t++)
        {
            if (path.get(t) != path.get(begin))
            {
                lists[path.get(begin)].append(t - begin);
                begin = t;
            }
        }
        
        return lists;
    }

    /**
     * Returns a list of times each integer value was continuously seen in the given path
     * @param path a sequence of integers
     * @return a list of dwell times for each occurring integer value.
     */
    public IIntList[] lifetimesByState(List<IIntArray> paths)
    {
        IIntList[] res = new IIntList[max(paths)+1];
        for (int i=0; i<res.length; i++)
            res[i] = Ints.create.list(0);

        for (IIntArray path : paths)
        {
            IIntList[] ttimes = lifetimesByState(path);
            for (int s = 0; s < ttimes.length; s++)
            {
                res[s].appendAll(ttimes[s]);
            }
        }

        return (res);
    }

    /**
     * 
     * @param em
     * @param nstates
     * @param ntraj
     * @return 
     */
    public double[][] cumulativeLifetimeDistribution(List<IIntArray> paths)
    {
        // get all dwell times from all trajectories
        IIntList[] times = lifetimesByState(paths);
        int nstates = times.length;

        // find the largest lifetime
        int maxtime = 0;
        for (IIntList l : times)
            maxtime = Math.max(maxtime, Ints.util.max(l));

        // get cum dists by state
        double[][] cumdist = new double[maxtime+1][nstates];
        for (int s = 0; s < nstates; s++)
        {
            // sort times descendingly
            Ints.util.sort(times[s]);
            Ints.util.mirror(times[s]);
            
            // count occurances longer than t
            int n = 0;
            
            for (int t = cumdist.length-1; t>=0; t--)
            {
                // count occurances longer than t
                while (times[s].get(n) >= t && n < times[s].size()-1)
                    n++;
                cumdist[t][s] = (double)n / (double)(times[s].size());
            }            
        }
                
        return (cumdist);
    }
    
    
    

}
