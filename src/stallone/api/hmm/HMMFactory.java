/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import java.util.List;
import stallone.api.algebra.Algebra;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.hmm.EM;
import stallone.hmm.HMMParameters;
import stallone.stat.GaussianUnivariate;

/**
 *
 * @author noe
 */
public class HMMFactory
{
    private IHMMParameters initialParametersGaussian1D(List<IDataSequence> _obs)
    {
        return null;
    }

    public IHMMParameters initialParametersGaussian(List<IDataSequence> _obs)
    {
        int dim = _obs.get(0).dimension();
        if (dim <= 1)
            throw(new IllegalArgumentException("Data dimension is <= 0 but must be 1 at least"));
        if (dim == 1)
            return initialParametersGaussian1D(_obs);
        else
        {
            throw new java.lang.UnsupportedOperationException("N-dimensional Gaussian HMM not yet implemented");
        }
    }
    
    public EM createGaussianHmm(List<IDataSequence> _obs, IHMMParameters initialParameters)
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
    
    public EM createGaussianHmm(List<IDataSequence> _obs, int nstates)
    {
        // check if the data is event-based
        boolean eventBased = true;
        
        // generate initial paramters

        // initial T
        IDoubleArray T = Algebra.util.add(Doubles.create.diag(nstates, 0.9), Doubles.create.matrix(nstates, nstates, 0.1/(double)nstates));
        // initial p0
        IDoubleArray p0 = Doubles.create.array(nstates, 1.0/(double)nstates);
        // initial output parameters
        IDoubleArray[] parOut = new IDoubleArray[nstates];
        for (int i=0; i<parOut.length; i++)
        {
            //parOut[i] = Doubles.create.arrayFrom((2.0*Math.random())-1.0, 1.0);
            parOut[i] = Doubles.create.arrayFrom(i*2 -1, 1.0);
            
        }
        // initial parameters
        IHMMParameters initialParameters = new HMMParameters(T, p0, parOut, false, true);

        return createGaussianHmm(_obs, initialParameters);
    }
}
