/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.mc.tpt.ICommittor;
import stallone.api.mc.tpt.ITPTFlux;
import stallone.api.algebra.*;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.function.IGriddedFunction;
import stallone.mc.DiscretePotentialMetropolisMarkovChain;
import stallone.api.mc.IMarkovChain;
import stallone.mc.MarkovChain;
import stallone.mc.PosteriorCountMatrix;
import stallone.mc.correlations.*;
import stallone.mc.estimator.*;
import stallone.mc.pcca.PCCA;
import stallone.mc.sampling.*;
import stallone.mc.tpt.Committor;
import stallone.mc.tpt.TPTFlux;

/**
 *
 * @author noe
 */
public class MarkovModelFactory
{
    // ************************************************************************
    //
    // PRIORS
    //
    // ************************************************************************

    /**
     * Adds a constant prior count to all elements where c_ij + c_ji > 0.
     * @param observedCounts
     * @param prior
     * @return 
     */
    public IDoubleArray createPosteriorCountsNeighbor(IDoubleArray observedCounts, double prior)
    {
        IDoubleArray Cprior = observedCounts.create(observedCounts.rows(), observedCounts.columns());
        for (IDoubleIterator it = observedCounts.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int row = it.row();
            int col = it.column();
            if (observedCounts.get(row,col)+observedCounts.get(col,row) > 0)
            {
                Cprior.set(row,col,prior);
                Cprior.set(col,row,prior);
            }
        }
        return(new PosteriorCountMatrix(Cprior, observedCounts));
    }

    // ************************************************************************
    //
    // Specific Markov models
    //
    // ************************************************************************

    /**
     * Creates a Markov chain from a Metropolis Monte Carlo jump between neighbors of the gridded function.
     * Jumps with equal probability to a neighbor and accepts with min(1,exp(-dE/kT)), where dE = E_target - E_current.
     * @param f
     * @param kT the thermal energy used to calculate jump probabilities
     * @return 
     */
    public IDoubleArray metropolisMC(IGriddedFunction f, double kT)
    {
        DiscretePotentialMetropolisMarkovChain mc = new DiscretePotentialMetropolisMarkovChain(f, kT);
        return(mc.getTransitionMatrix());
    }    
    
    
    // ************************************************************************
    //
    // COUNT MATRIX ESTIMATORS
    //
    // ************************************************************************

    public ICountMatrixEstimator createCountMatrixEstimatorSliding(Iterable<IIntArray> trajs, int lag)
    {
        ICountMatrixEstimator cme = new CountMatrixEstimatorSliding(trajs);
        cme.setLag(lag);
        return(cme);
    }

    public ICountMatrixEstimator createCountMatrixEstimatorSliding(IIntArray traj, int lag)
    {
        ICountMatrixEstimator cme = new CountMatrixEstimatorSliding(traj);
        cme.setLag(lag);
        return(cme);
    }

    public ICountMatrixEstimator createCountMatrixEstimatorStepping(Iterable<IIntArray> trajs, int lag)
    {
        ICountMatrixEstimator cme = new CountMatrixEstimatorStepping(trajs);
        cme.setLag(lag);
        return(cme);
    }

    public ICountMatrixEstimator createCountMatrixEstimatorStepping(IIntArray traj, int lag)
    {
        ICountMatrixEstimator cme = new CountMatrixEstimatorStepping(traj);
        cme.setLag(lag);
        return(cme);
    }
    
    // ************************************************************************
    //
    // TRANSITION MATRIX ESTIMATORS
    //
    // ************************************************************************
    
    public ITransitionMatrixEstimator createTransitionMatrixEstimatorNonrev()
    {
        return(new TransitionMatrixEstimatorNonRev());
    }

    public ITransitionMatrixEstimator createTransitionMatrixEstimatorRev()
    {
        return(new TransitionMatrixEstimatorRev());
    }
    
    public ITransitionMatrixEstimator createTransitionMatrixEstimatorRev(IDoubleArray pifix)
    {
        return(new TransitionMatrixEstimatorRevFixPi(pifix));
    }
    
    /**
     * Creates a nonreversible transition matrix sampler
     * @param counts the posterior count matrix
     * @return 
     */
    public ITransitionMatrixSampler createTransitionMatrixSamplerNonrev(IDoubleArray counts)
    {
        ITransitionMatrixSampler sampler = new TransitionMatrixSamplerNonrev(counts);
        return(sampler);
    }
    
    /**
     * Creates a reversible transition matrix sampler
     * @param counts the posterior count matrix
     * @return 
     */
    public ITransitionMatrixSampler createTransitionMatrixSamplerRev(IDoubleArray counts)
    {
        ITransitionMatrixSampler sampler = new TransitionMatrixSamplerRev(counts);
        return(sampler);
    }

        /**
     * Creates a reversible transition matrix sampler with a fixed stationary distribution
     * @param counts the posterior count matrix
     * @param piFix the fixed stationary distribution
     * @return 
     */
    public ITransitionMatrixSampler createTransitionMatrixSamplerRev(IDoubleArray counts, IDoubleArray piFix)
    {
        ITransitionMatrixSampler sampler = new TransitionMatrixSamplerRevFixPi(counts, piFix);
        
        return(sampler);
    }

    public PCCA createPCCA(IDoubleArray M, int nstates)
    {
        PCCA pcca = new PCCA();
        
        IDoubleArray evec = M;
        
        if (MarkovModel.util.isTransitionMatrix(M))
        {
            IEigenvalueDecomposition evd = Algebra.util.evd(M,nstates);
            evec = evd.getRightEigenvectorMatrix();
            evec = evec.viewBlock(0,0,M.rows(),nstates);
        }
        else
        {
            if (evec.columns() < nstates)
                throw(new IllegalArgumentException("Attempting to create PCCA decomposition into "+nstates+" states."+
                                                    "but only "+evec.columns()+" eigenvectors were provided"));
        }
        
        pcca.setEigenvectors(evec);
        pcca.perform();
        
        return(pcca);
    }
    
    public ICommittor createCommittor(IDoubleArray T, IIntArray A, IIntArray B)
    {
        return(createCommittor(T, MarkovModel.util.stationaryDistribution(T), A, B));
    }

    public ICommittor createCommittor(IDoubleArray T, IDoubleArray pi, IIntArray A, IIntArray B)
    {
        ICommittor comm = new Committor(T,A,B);
        comm.setStationaryDistribution(pi);
        return(comm);
    }
    
    public ITPTFlux createTPT(IDoubleArray T, IIntArray A, IIntArray B)
    {
        return(createTPT(T, MarkovModel.util.stationaryDistribution(T), A, B));
    }

    public ITPTFlux createTPT(IDoubleArray T, IDoubleArray initialDistribution, IIntArray A, IIntArray B)
    {
        TPTFlux tpt = new TPTFlux(T,A,B);

        tpt.setStationaryDistribution(initialDistribution);
        tpt.calculate();

        return(tpt);
    }
    
    public IDynamicalExpectations createDynamicalExpectations(IDoubleArray T)
    {
        // FIXME: calculate and set pi
        IDynamicalExpectations dexp = new DynamicalExpectations(T);
        //dexp.setStationaryDistribution(pi);
        return dexp;
    }

    public IDynamicalExpectations createDynamicalExpectations(IDoubleArray T, IDoubleArray pi)
    {
        IDynamicalExpectations dexp = new DynamicalExpectations(T);
        dexp.setStationaryDistribution(pi);
        return(dexp);
    }

    public IDynamicalExpectationsSpectral createDynamicalFingerprint(IDoubleArray T)
    {
        return(createDynamicalFingerprint(T));
    }

    public IDynamicalExpectationsSpectral createDynamicalFingerprint(IDoubleArray T, IDoubleArray pi)
    {
        IDynamicalExpectationsSpectral dexp = new DynamicalExpectationsSpectral(T);
        dexp.setStationaryDistribution(pi);
        return(dexp);
    }
    
    public IMarkovChain markovChain(IDoubleArray T)
    {
        return new MarkovChain(T);
    }
    
    public IMarkovChain markovChain(IDoubleArray startingDistribution, IDoubleArray T)
    {
        return new MarkovChain(startingDistribution, T);
    }
    
}
