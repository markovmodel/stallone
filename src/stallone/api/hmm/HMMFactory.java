/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

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
import stallone.api.stat.IParameterEstimator;
import stallone.api.stat.Statistics;
import stallone.hmm.EM;
import stallone.hmm.HMMParameters;
import stallone.stat.GaussianUnivariate;

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
        return par;
    }
    
    public IHMMOptimizer createHmm(List<IDataSequence> _obs, IParametricFunction outputModel, IParameterEstimator outputEstimator, IHMMParameters initialParameters)
    {
        // check if the data is event-based
        boolean eventBased = true;
        
        // save memory?
        boolean saveMemory = false;
        
        EM em = new EM(_obs, eventBased, initialParameters, outputModel, outputEstimator, saveMemory);
        
        return em;
    }    
    
    public IHMMOptimizer createGaussianHmm(List<IDataSequence> _obs, IHMMParameters initialParameters)
    {
        // check if the data is event-based
        boolean eventBased = true;
        
        // save memory?
        boolean saveMemory = false;
        // output model and parametrizer
        GaussianUnivariate gauss = new GaussianUnivariate(0,1);
        
        EM em = new EM(_obs, eventBased, initialParameters, gauss, gauss, saveMemory);
        
        return em;
    }    
    
    public IHMMOptimizer createGaussianHmm(List<IDataSequence> _obs, int nstates)
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

        return createGaussianHmm(_obs, initialParameters);
    }
}
