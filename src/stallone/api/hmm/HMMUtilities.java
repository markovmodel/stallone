/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import static stallone.api.API.*;

import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class HMMUtilities
{
    /**
     * Estimates a HMM approximation to a PMM
     * @param _dtrajs List of discrete trajectories
     * @param nHiddenStates number of hidden states
     * @param lag lag time
     * @param timeshift The time shift unit for generating multiple trajectories for the estimation when lag > 1. 
     * For example, when lag = 10 and timeshift = 2, five trajectories will be used for the estimation, having the 
     * time indexes [0,10,...,N-10], [2,12,...,N-8], [4,14,...,N-6], [6,16,...,N-4], [8,18,...,N-2].
     * The optimal setting is timeshift = 1, but will compute slowest. 
     * The fastest setting is timeshift = lag. Settings timeshift > lag have the same effect as timeshift = lag.
     * @param nconvsteps the maximal number of EM steps before the method returns
     * @param dectol the tolerance by which a decrease of the log-likelihood is accepted. 
     * The likelihood should normally only increase, but close to the optimum numerical fluctuations are possible.
     * If you want to use it as a normal convergence criterion, set to a small negative value, such as -0.1
     * @param TCinit the initial hidden transition matrix to start EM with
     * @param chiInit the initial hidden output probability matrix to start EM with
     */
    public IHMM pmm(List<IIntArray> _dtrajs, int nHiddenStates, int lag, int timeshift, int nconvsteps, double dectol, IDoubleArray TCinit, IDoubleArray chiInit)
            throws ParameterEstimationException
    {
        IExpectationMaximization em = hmmNew.pmm(_dtrajs, nHiddenStates, lag, timeshift, nconvsteps, dectol, TCinit, chiInit);
        em.run();
        return em.getHMM();
    }

    
    /**
     * Estimates a HMM approximation to a PMM
     * @param _dtrajs List of discrete trajectories
     * @param nHiddenStates number of hidden states
     * @param lag lag time
     */
    public IHMM pmm(List<IIntArray> _dtrajs, int nHiddenStates, int lag)
            throws ParameterEstimationException
    {
        IExpectationMaximization em = hmmNew.pmm(_dtrajs, nHiddenStates, lag);
        em.run();
        return em.getHMM();
    }

}
