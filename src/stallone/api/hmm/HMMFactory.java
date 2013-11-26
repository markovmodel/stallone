/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import static stallone.api.API.*;

import java.util.List;
import stallone.api.algebra.Algebra;
import stallone.api.cluster.Clustering;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.ints.IIntArray;
import stallone.api.intsequence.IntSequence;
import stallone.api.stat.IParameterEstimator;
import stallone.api.stat.Statistics;
import stallone.hmm.EM;
import stallone.hmm.EMHierarchical;
import stallone.hmm.EMMultiStart;
import stallone.hmm.HMMParameters;
import stallone.stat.GaussianUnivariate;

import stallone.hmm.pmm.NinjaEstimator;
import stallone.stat.DiscreteDistribution;

/**
 *
 * @author noe
 */
public class HMMFactory
{
    private IDataSequence concat(List<IDataSequence> _obs, int maxsize)
    {
        IDataSequence res = null;
        int size = DataSequence.util.size(_obs);
        int interleaf = (int)Math.max(1, size / maxsize);
        return DataSequence.util.concat(_obs, interleaf);
    }

    private IDoubleArray[] initialParametersGaussian1D(List<IDataSequence> _obs, int nstates)
    {
        // cluster data
        IDataSequence obscat = concat(_obs, nstates*1000);

        System.out.println("Data concatenated to size "+obscat.size());

        System.out.println("Clustering...");

        IClustering cluster = Clustering.util.densityBased(obscat, nstates);

        System.out.println("done.");

        IIntArray ci = cluster.getClusterIndexes();
        IDoubleArray[] res = new IDoubleArray[nstates];
        for (int state=0; state<nstates; state++)
        {
            IParameterEstimator estimator = Statistics.create.parameterEstimatorGaussian1D();
            res[state] = estimator.estimate(obscat, Clustering.util.membershipToState(cluster, state));
        }
        return res;
    }

    public IDoubleArray[] initialParametersGaussian(List<IDataSequence> _obs, int nstates)
    {
        int dim = _obs.get(0).dimension();
        if (dim < 1)
            throw(new IllegalArgumentException("Data dimension is <= 0 but must be 1 at least"));
        if (dim == 1)
            return initialParametersGaussian1D(_obs, nstates);
        else
        {
            throw new java.lang.UnsupportedOperationException("N-dimensional Gaussian HMM not yet implemented");
        }
    }

    public IHMMParameters parameters(int nstates, boolean _isReversible, boolean _isStationary)
    {
        IHMMParameters par = new HMMParameters(nstates, _isReversible, _isStationary);
        // initialize transition matrix
        IDoubleArray T0 = doublesNew.matrix(nstates,nstates);
        doubles.fill(T0, 1.0/(double)nstates);
        par.setTransitionMatrix(T0);
        return par;
    }

    public IHMMParameters parameters(int nstates, boolean _isReversible, boolean _isStationary, IParametricFunction outputModel)
    {
        IHMMParameters par = parameters(nstates, _isReversible, _isStationary);
        // initialize parameters
        int npar = outputModel.getParameters().size();
        for (int i=0; i<nstates; i++)
            par.setOutputParameters(i, doublesNew.array(npar));
        return par;
    }

    public IExpectationMaximization em(List<IDataSequence> _obs, IParametricFunction outputModel, IParameterEstimator outputEstimator, IHMMParameters initialParameters)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        boolean saveMemory = false;

        IExpectationMaximization em = new EM(_obs, eventBased, initialParameters.getNStates(), initialParameters.isReversible(), outputModel, outputEstimator, saveMemory);
        em.setInitialParameters(initialParameters);

        return em;
    }

    public IExpectationMaximization em(List<IDataSequence> _obs, IParametricFunction outputModel, IParameterEstimator outputEstimator, List<IIntArray> initialPaths, boolean reversible)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        boolean saveMemory = false;

        // count states:
        int nstates = IntSequence.util.max(initialPaths)+1;

        // construct em:
        IExpectationMaximization em = new EM(_obs, eventBased, nstates, reversible, outputModel, outputEstimator, saveMemory);
        // default parameters:
        IHMMParameters par0 = parameters(nstates, reversible, true, outputModel);
        em.setInitialParameters(par0);

        em.setInitialPaths(initialPaths);

        return em;
    }

    public IHMMOptimizer emMultiStart(List<IDataSequence> _obs, IParametricFunction outputModel, IParameterEstimator outputEstimator, IHMMParameters[] initialParameters,
            int nscansteps, int nscans, int nconvsteps, double dectol)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        boolean saveMemory = false;

        EMMultiStart em = new EMMultiStart(_obs, outputModel, outputEstimator, initialParameters);
        em.setNumberOfScanningSteps(nscansteps);
        em.setNumberOfConvergenceSteps(nconvsteps);
        em.setLikelihoodDecreaseTolerance(dectol);

        return em;
    }

    public IHMMOptimizer emHierarchical(List<IDataSequence> _obs, IParametricFunction outputModel, IParameterEstimator outputEstimator,
            IHMMParameters[] initialParameters, int nInitialSteps, double dectol, boolean saveMemory)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        //boolean saveMemory = false;

        EMHierarchical em = new EMHierarchical(_obs, outputModel, outputEstimator);
        em.setInitialParameters(initialParameters);
        em.setInitialNumberOfSteps(nInitialSteps);
        em.setLikelihoodDecreaseTolerance(dectol);

        return em;
    }

    /**
     *
     * @param _obs
     * @param initialParameters
     * @param prior prior counts for the output probabilities
     * @return
     */
    public IExpectationMaximization emDiscrete(List<IDataSequence> _obs, IHMMParameters initialParameters, double[] prior)
    {
        // check if the data is event-based
        boolean eventBased = false;

        // save memory?
        boolean saveMemory = false;

        // output model and parametrizer
        DiscreteDistribution dd = new DiscreteDistribution(initialParameters.getOutputParameters(0));
        dd.setPrior(prior);
        EM em = new EM(_obs, eventBased, initialParameters.getNStates(), initialParameters.isReversible(), dd, dd, saveMemory);
        em.setInitialParameters(initialParameters);

        return em;
    }

    public IExpectationMaximization emDiscrete(List<IDataSequence> _obs, IHMMParameters initialParameters)
    {
        // check if the data is event-based
        boolean eventBased = false;

        // save memory?
        boolean saveMemory = false;

        // output model and parametrizer
        DiscreteDistribution dd = new DiscreteDistribution(initialParameters.getOutputParameters(0));
        EM em = new EM(_obs, eventBased, initialParameters.getNStates(), initialParameters.isReversible(), dd, dd, saveMemory);
        em.setInitialParameters(initialParameters);

        return em;
    }


    public IExpectationMaximization emGaussian(List<IDataSequence> _obs, IHMMParameters initialParameters)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        boolean saveMemory = false;
        // output model and parametrizer
        GaussianUnivariate gauss = new GaussianUnivariate(0,1);
        EM em = new EM(_obs, eventBased, initialParameters.getNStates(), initialParameters.isReversible(), gauss, gauss, saveMemory);
        em.setInitialParameters(initialParameters);

        return em;
    }

    public IExpectationMaximization emGaussian(List<IDataSequence> _obs, List<IIntArray> initialPaths, boolean reversible)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // save memory?
        boolean saveMemory = false;
        // output model and parametrizer
        GaussianUnivariate gauss = new GaussianUnivariate(0,1);

        // count states:
        int nstates = IntSequence.util.max(initialPaths)+1;

        EM em = new EM(_obs, eventBased, nstates, reversible, gauss, gauss, saveMemory);
        em.setInitialPaths(initialPaths);

        return em;
    }

    public IExpectationMaximization emGaussian(List<IDataSequence> _obs, int nstates)
    {
        // check if the data is event-based
        boolean eventBased = true;

        // initial T
        IDoubleArray T = Algebra.util.add(Doubles.create.diag(nstates, 0.9), Doubles.create.matrix(nstates, nstates, 0.1/(double)nstates));
        // initial p0
        IDoubleArray p0 = Doubles.create.array(nstates, 1.0/(double)nstates);
        // initial output parameters
        IDoubleArray[] parOut = initialParametersGaussian(_obs, nstates);
        // initial parameters
        IHMMParameters initialParameters = new HMMParameters(T, p0, parOut, false, true);

        return emGaussian(_obs, initialParameters);
    }
    
    
    /**
     * 
     * @param _dtrajs List of discrete trajectories
     */
    public IExpectationMaximization pmm(List<IIntArray> _dtrajs, 
            int nHiddenStates, int lag, int timeshift,
            int nconvsteps, double dectol, IDoubleArray TCinit, IDoubleArray chiInit)
    {
        NinjaEstimator ninja = new NinjaEstimator(_dtrajs);
        ninja.setNHiddenStates(nHiddenStates);
        ninja.setNIterHMMMax(nconvsteps);
        ninja.setHMMLikelihoodMaxIncrease(dectol);
        
        ninja.setTau(lag);
        ninja.setTimeshift(timeshift);

        // initialization from last lag
        if (TCinit != null && chiInit != null)
            ninja.setInit(TCinit, chiInit);
        
        return ninja;
    }

    /**
     * 
     * @param _dtrajs List of discrete trajectories
     */
    public IExpectationMaximization pmm(List<IIntArray> _dtrajs, 
            int nHiddenStates, int lag, int timeshift,
            int nconvsteps)
    {
        return pmm(_dtrajs, nHiddenStates, lag, timeshift, nconvsteps, 0.1, null, null);
    }    
}
