/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * Transforms discrete trajectories into coarse-grained discrete Trajectories by
 * milestoning count
 *
 * @author noe
 */
public class MilestoningFilter
{
    private Iterable<IIntArray> cores;
    private HashMap<Integer,Integer> state2core;

    public MilestoningFilter(Iterable<IIntArray> _cores)
    {
        this.cores = _cores;

        state2core = new HashMap<Integer,Integer>();
        int c = 0;
        for (IIntArray core: _cores)
        {
            for (int i=0; i<core.size(); i++)
            {
                state2core.put(core.get(i), c);
            }
            c++;
        }
    }

    public MilestoningFilter(IIntArray... _cores)
    {
        this(Arrays.asList(_cores));
    }

        /**
         * Filters the trajectory using milestoning count
         * @param traj
         * @return the filtered trajectory. Might be an empty array if there is nothing left after filtering
         */
    public IIntArray filter(IIntArray traj)
    {
        // go to the first index that is in a core
        int ifirst = 0;
        for (; ifirst < traj.size(); ifirst++)
        {
            if (state2core.containsKey(traj.get(ifirst)))
                break;
        }

        // no core found -> result trajectory is empty.
        if (ifirst >= traj.size())
            return(Ints.create.array(0));

        // create an array of the right size of the remaining trajectory
        IIntArray res = Ints.create.array(traj.size()-ifirst);
        int currentCore = state2core.get(traj.get(ifirst));
        for (int i=ifirst; i<traj.size(); i++)
        {
            // switch core?
            if (state2core.containsKey(traj.get(i)))
                currentCore = state2core.get(traj.get(i));

            res.set(i-ifirst, currentCore);
        }

        return(res);
    }

}
