/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import java.util.ArrayList;
import java.util.List;
import stallone.api.algebra.Algebra;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.algebra.IEigenvalueSolver;
import stallone.api.graph.IIntGraph;
import stallone.cluster.MilestoningFilter;
import stallone.graph.MatrixGraph;
import stallone.graph.connectivity.IntStrongConnectivity;
import stallone.mc.MarkovChain;
import stallone.mc.StationaryDistribution;
import stallone.mc.correlations.DynamicalExpectations;
import stallone.mc.correlations.DynamicalExpectationsSpectral;
import stallone.mc.estimator.TransitionMatrixLikelihood;
import stallone.mc.pcca.PCCA;
import stallone.mc.tpt.Committor;

/**
 *
 * @author noe
 */
public class MarkovModelUtilities
{
    /**
     * 
     * @param P A count, transition or rate matrix
     */
    public boolean isConnected(IDoubleArray P)
    {
        return (connectedComponents(P).size() == 1);
    }
    
    public List<IIntArray> connectedComponents(IDoubleArray P)
    {
        IIntGraph g = new MatrixGraph(P);
        IntStrongConnectivity connectivity = new IntStrongConnectivity(g);
        connectivity.perform();

        List<IIntArray> C = connectivity.getStrongComponents();
        return C;
    }

    public IIntArray giantComponent(IDoubleArray P)
    {
        List<IIntArray> C = connectedComponents(P);
        IIntArray largest = C.get(0);
        int size = C.get(0).size();
        for (int i=1; i<C.size(); i++)
        {
            if (C.get(i).size() > size)
            {
                largest = C.get(i);
                size = largest.size();
            }
        }
        return largest;
    }
    
    /**
     * Standard count matrix estimation using a sliding window with given lag time. 
     * This is useful for the maximum likelihood estimate of T
     * @param traj
     * @param lag
     * @return 
     */    
    public IDoubleArray estimateC(Iterable<IIntArray> trajs, int lag)
    {
        ICountMatrixEstimator est = MarkovModel.create.createCountMatrixEstimatorSliding(trajs, lag);
        return(est.estimate());
    }

    public IDoubleArray estimateCmilestoning(Iterable<IIntArray> trajs, Iterable<IIntArray> cores, int lag)
    {
        MilestoningFilter filter = new MilestoningFilter(cores);
        ArrayList<IIntArray> filteredList = new ArrayList<IIntArray>();
        for (IIntArray traj:trajs)
            filteredList.add(filter.filter(traj));
        return estimateC(filteredList, lag);
    }

    /**
     * Implicit assumption that cores are 0 1 2 ... n, while non-cores are negative numbers
     * @param trajs
     * @param cores
     * @param lag
     * @return 
     */
    public IDoubleArray estimateCmilestoning(Iterable<IIntArray> trajs, int lag)
    {
        // find maximum
        int max = 0;
        for (IIntArray traj : trajs)
        {
            max = Math.max(max, Ints.util.max(traj));
        }
        
        // build cores
        ArrayList<IIntArray> cores = new ArrayList<IIntArray>();
        for (int i=0; i<max+1; i++)
            cores.add(Ints.create.arrayFrom(i));
            
        return(estimateCmilestoning(trajs, cores, lag));
    }
    
    /**
     * Standard count matrix estimation using a sliding window with given lag time. 
     * This is useful for the maximum likelihood estimate of T
     * @param traj
     * @param lag
     * @return 
     */    
    public IDoubleArray estimateC(IIntArray traj, int lag)
    {
        ICountMatrixEstimator est = MarkovModel.create.createCountMatrixEstimatorSliding(traj, lag);
        return(est.estimate());
    }
    
    public IDoubleArray estimateCmilestoning(IIntArray traj, Iterable<IIntArray> cores, int lag)
    {
        MilestoningFilter filter = new MilestoningFilter(cores);
        IIntArray filteredTraj = filter.filter(traj);
        return estimateC(filteredTraj, lag);
    }

    public IDoubleArray estimateCmilestoning(IIntArray traj, int lag)
    {
        // find maximum
        int max = Ints.util.max(traj);
        
        // build cores
        ArrayList<IIntArray> cores = new ArrayList<IIntArray>();
        for (int i=0; i<max+1; i++)
            cores.add(Ints.create.arrayFrom(i));
            
        return(estimateCmilestoning(traj, cores, lag));
    }
    
    /**
     * Count matrix estimation where the input trajectory is sampled every lag steps. This is
     * useful for estimating the uncertainties of the transition matrix
     * @param traj
     * @param lag
     * @return 
     */    
    public IDoubleArray estimateCstepping(Iterable<IIntArray> trajs, int lag)
    {
        ICountMatrixEstimator est = MarkovModel.create.createCountMatrixEstimatorStepping(trajs, lag);
        return(est.estimate());
    }

    /**
     * Count matrix estimation where the input trajectory is sampled every lag steps. This is
     * useful for estimating the uncertainties of the transition matrix
     * @param traj
     * @param lag
     * @return 
     */    
    public IDoubleArray estimateCstepping(IIntArray traj, int lag)
    {
        ICountMatrixEstimator est = MarkovModel.create.createCountMatrixEstimatorStepping(traj, lag);
        return(est.estimate());
    }
    
    public double logLikelihood(IDoubleArray T, IDoubleArray C)
    {
        return(TransitionMatrixLikelihood.logLikelihood(T, C));
    }

    public double logLikelihoodCorrelationMatrix(IDoubleArray corr, IDoubleArray C)
    {
        return(TransitionMatrixLikelihood.logLikelihoodCorrelationMatrix(corr, C));
    }
    
    public boolean isTransitionMatrix(IDoubleArray T)
    {
        // check elements
        for (IDoubleIterator it = T.nonzeroIterator(); it.hasNext(); it.advance())
        {
            double Tij = it.get();
            if (Tij < 0 || Tij > 1)
            {
                System.out.println("Invalid Element: "+it.row()+", "+it.column());
                return(false);
            }
        }
        
        // check row sums
        for (int i=0; i<T.rows(); i++)
        {
            double diff = Math.abs(Doubles.util.sum(T.viewRow(i)) - 1);
            if (diff > 1e-6)
            {
                System.out.println("Invalid Row sum difference from 1 in row "+i+": "+diff);
                return(false);
            }
        }
        return(true);
    }
    
    public boolean isRateMatrix(IDoubleArray K)
    {
        // check elements
        for (IDoubleIterator it = K.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            double kij = it.get();
            if (i == j && kij > 0)
                return(false);
            if (i != j && kij < 0)
                return(false);
        } 
        
        // check row sums
        for (int i=0; i<K.rows(); i++)
        {
            if (Math.abs(Doubles.util.sum(K.viewRow(i))) > 1e-6)
                return(false);
        }
        return(true);
    }
    
    public boolean isReversible(IDoubleArray T)
    {
        return(isReversible(T, this.stationaryDistribution(T)));
    }
    
    /**
     * Tests whether the given transition matrix T is reversible with respect to the provided 
     * stationary distribution pi
     * @param T
     * @param pi
     * @return 
     */
    public boolean isReversible(IDoubleArray T, IDoubleArray pi)
    {
        for (IDoubleIterator it = T.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            double fij = pi.get(i)*it.get();
            double fji = pi.get(j)*T.get(j,i);
            if ((fij+fji) > 1e-10)
                if(Math.abs((fij-fji)/(fij+fji)) > 1e-6)
                {
                    return(false);
                }
        }
        return(true);
    }
    
    /**
     * Maximum likelihood estimate of T (generally nonreversible)
     * @param counts
     * @param piFixed
     * @return 
     */
    public IDoubleArray estimateT(IDoubleArray counts)
    {
        ITransitionMatrixEstimator est = MarkovModel.create.createTransitionMatrixEstimatorNonrev();
        est.setCounts(counts);
        est.estimate();
        return(est.getTransitionMatrix());
    }

    /**
     * Reversible maximum likelihood estimate of T 
     * @param counts
     * @param piFixed
     * @return 
     */
    public IDoubleArray estimateTrev(IDoubleArray counts)
    {
        ITransitionMatrixEstimator est = MarkovModel.create.createTransitionMatrixEstimatorRev();
        est.setCounts(counts);
        est.estimate();
        return(est.getTransitionMatrix());
    }

    /**
     * Reversible maximum likelihood estimate of T given a fixed stationary distribution
     * @param counts
     * @param piFixed
     * @return 
     */
    public IDoubleArray estimateTrev(IDoubleArray counts, IDoubleArray piFixed)
    {
        ITransitionMatrixEstimator est = MarkovModel.create.createTransitionMatrixEstimatorRev(piFixed);
        est.setCounts(counts);
        est.estimate();
        return(est.getTransitionMatrix());
    }
    
    public IDoubleArray stationaryDistribution(IDoubleArray T)
    {
        return(StationaryDistribution.calculate(T));
    }

    /**
     * Quick and dirty calculation of the stationary distribution when T is a strictly reversible matrix.
     * Warning: when T is not reversible, the result will be non sense.
     * @param T
     * @return 
     */
    public IDoubleArray stationaryDistributionRevQuick(IDoubleArray T)
    {
        return(StationaryDistribution.calculateReversible(T));
    }

    /**
     * Calculates the relaxation timescale of a single transition matrix
     * @param T
     * @param tau
     * @return 
     */
    public IDoubleArray timescales(IDoubleArray T, double tau)
    {
        if (!this.isTransitionMatrix(T))
            throw(new IllegalArgumentException("Trying to calculate timescales of a matrix that is not a transition matrix"));
        
        IEigenvalueDecomposition evd = Algebra.util.evd(T);
        IDoubleArray ev = evd.getEvalNorm();

        IDoubleArray timescales = Doubles.create.array(ev.size()-1);
        for (int i=0; i<timescales.size(); i++)
            timescales.set(i, -tau/Math.log(ev.get(i+1)));
        
        return(timescales);
    }
    
    /**
     * Calculates the relaxation timescales of a single estimation
     * @return a double matrix with implied timescales in the columns and lag times in the rows.
     */
    public IDoubleArray timescales(Iterable<IIntArray> dtraj, ICountMatrixEstimator Cest, ITransitionMatrixEstimator Test, int ntimescales, IIntArray lagtimes)
    {
        double[][] res = new double[lagtimes.size()][];
        
        Cest.addInput(dtraj);
        
        for (int i=0; i<lagtimes.size(); i++)
        {
            int tau = lagtimes.get(i);
            Cest.setLag(lagtimes.get(i));
            IDoubleArray C = Cest.estimate();
            Test.setCounts(C);
            Test.estimate();
            IDoubleArray T = Test.getTransitionMatrix();
            res[i] = timescales(T, tau).getArray();
        }

        return(Doubles.create.matrix(res));        
    }
    
    /**
     * Finds the metastable states of a Markov Model given the transition matrix or its eigenvectors
     * @param M
     * @param nstates
     * @return 
     */
    public IIntArray metastableStates(IDoubleArray M, int nstates)
    {
        PCCA pcca = MarkovModel.create.createPCCA(M, nstates);
        return(pcca.getClusters());
    }

    /**
     * Finds the memberships to metastable states of a Markov Model given the transition matrix or its eigenvectors
     * @param M
     * @param nstates
     * @return 
     */
    public IDoubleArray metastableMemberships(IDoubleArray M, int nstates)
    {
        PCCA pcca = MarkovModel.create.createPCCA(M, nstates);
        return(pcca.getFuzzy());
    }

    public IDoubleArray forwardCommittor(IDoubleArray M, IIntArray A, IIntArray B)
    {
        Committor comm = MarkovModel.create.createCommittor(M, A, B);
        return(comm.forwardCommittor());
    }

    public IDoubleArray backwardCommittor(IDoubleArray M, IIntArray A, IIntArray B)
    {
        Committor comm = MarkovModel.create.createCommittor(M, A, B);
        return(comm.backwardCommittor());
    }
    
    
    /**
     * Calculates the autocorrelation function of the given observable under the dynamical action of M at the given timepoints
     * @param M a rate or transition matrix
     * @param observable
     * @param timepoints
     * @return 
     */
    public IDoubleArray autocorrelation(IDoubleArray M, IDoubleArray observable, IDoubleArray timepoints)
    {
        return(correlation(M,observable,observable,timepoints));
    }
    
    /**
     * Calculates the crosscorrelation function of the given observables under the dynamical action of M at the given timepoints
     * @param M a rate or transition matrix
     * @param observable
     * @param timepoints
     * @return 
     */
    public IDoubleArray correlation(IDoubleArray M, IDoubleArray observable1, IDoubleArray observable2, IDoubleArray timepoints)
    {
        DynamicalExpectations dexp = MarkovModel.create.createDynamicalExpectations(M);
        IDoubleArray res = Doubles.create.array(timepoints.size());
        for (int i=0; i<res.size(); i++)
            res.set(i, dexp.calculateCorrelation(observable1, observable2, timepoints.get(i)));
        return(res);
    }
    
    /**
     * Calculates the crosscorrelation function of the given observables under the dynamical action of M at the given timepoints
     * @param M a rate or transition matrix
     * @param observable
     * @param timepoints
     * @return 
     */
    public IDoubleArray perturbationExpectation(IDoubleArray M, IDoubleArray pi0, IDoubleArray observable, IDoubleArray timepoints)
    {
        DynamicalExpectations dexp = MarkovModel.create.createDynamicalExpectations(M);
        IDoubleArray res = Doubles.create.array(timepoints.size());
        for (int i=0; i<res.size(); i++)
            res.set(i, dexp.calculatePerturbationExpectation(pi0, observable, timepoints.get(i)));
        return(res);
    }    

    /**
     * Calculates the dynamical fingerprint (timescale amplitude spectrum) of the autocorrelation of the given observable
     * under the action of the dynamics M
     * @param M a rate or transition matrix
     * @param observable
     * @return 
     */
    public IDoubleArray fingerprintAutocorrelation(IDoubleArray M, IDoubleArray observable)
    {
        return(fingerprintCorrelation(M, observable, observable));
    }

    /**
     * Calculates the dynamical fingerprint (timescale amplitude spectrum) of the correlation of the given observables
     * under the action of the dynamics M
     * @param M a rate or transition matrix
     * @param observable
     * @return 
     */
    public IDoubleArray fingerprintCorrelation(IDoubleArray M, IDoubleArray observable1, IDoubleArray observable2)
    {
        DynamicalExpectationsSpectral dexp = MarkovModel.create.createDynamicalFingerprint(M);
        dexp.calculateCorrelation(observable1, observable2);
        IDoubleArray res = Doubles.util.mergeColumns(dexp.getTimescales(), dexp.getAmplitudes());
        return(res);        
    }

    /**
     * Calculates the dynamical fingerprint (timescale amplitude spectrum) of the autocorrelation of the given observable
     * under the action of the dynamics M
     * @param M a rate or transition matrix
     * @param observable
     * @return 
     */
    public IDoubleArray fingerprintPerturbation(IDoubleArray M, IDoubleArray p0, IDoubleArray observable)
    {
        DynamicalExpectationsSpectral dexp = MarkovModel.create.createDynamicalFingerprint(M);
        dexp.calculatePerturbationExpectation(p0, observable);
        IDoubleArray res = Doubles.util.mergeColumns(dexp.getTimescales(), dexp.getAmplitudes());
        return(res);        
    }
    
    
    /**
     * Creates a stochastic realization of the chain
     */
    public IIntArray trajectory(IDoubleArray T, int s, int length)
    {
        MarkovChain mc = new MarkovChain(T);
        mc.setStartingState(s);
        return mc.randomTrajectory(length);        
    }

    /**
     * Creates a stochastic realization of the chain
     */
    public IIntArray trajectoryToState(IDoubleArray T, int s, int[] endStates)
    {
        MarkovChain mc = new MarkovChain(T);
        mc.setStartingState(s);
        return mc.randomTrajectoryToState(endStates);        
    }    
}
