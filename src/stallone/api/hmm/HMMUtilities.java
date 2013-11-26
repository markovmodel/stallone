/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import static stallone.api.API.*;

import java.util.List;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class HMMUtilities
{
    /**
     * 
     * @param _dtrajs List of discrete trajectories
     */
    public IHMM pmm(List<IIntArray> _dtrajs, int nHiddenStates, int lag, int timeshift, int nconvsteps)
    {
        IExpectationMaximization em = hmmNew.pmm(_dtrajs, nHiddenStates, lag, timeshift, nconvsteps);
        return em.getHMM();
    }

}
