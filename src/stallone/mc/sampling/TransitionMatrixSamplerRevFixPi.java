/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IReversibleSamplingStep;
import stallone.api.mc.MarkovModel;

/**
 *
 * Gibbs sampler for reversible transition matrices.
 * By default uses MCMC sampling for element quadruples and a row Gibbs sampling step:
 * 1) MCMC sampling of element quadruples accordint to Noe JCP 2008
 * 2) Reversible row shift by beta distribution sampling (Trendelkamp-Schroer, Wu, Noe - preprint)
 *
 * @author noe, trendelkamp
 */
public class TransitionMatrixSamplerRevFixPi extends TransitionMatrixSamplerAbstract
{
    protected final IDoubleArray piFixed;

    private IReversibleSamplingStep step_quad;


    public TransitionMatrixSamplerRevFixPi(IDoubleArray counts, IDoubleArray piFixed)
    {
        this.piFixed=piFixed;
        init(counts);
    }

    public TransitionMatrixSamplerRevFixPi(IDoubleArray counts, IDoubleArray Tinit, IDoubleArray piFixed)
    {
        this.piFixed=piFixed;
        init(counts, Tinit);
    }

    @Override
    public final void init(IDoubleArray _C, IDoubleArray Tinit)
    {
        this.C = _C;

        if (Tinit == null)
        {
            this.T = MarkovModel.util.estimateTrev(eraseNegatives(_C));
        }
        else
        {
            this.T = Tinit;
        }
        this.logLikelihood = MarkovModel.util.logLikelihood(T, C);

        // initialize steps
        this.step_quad = new Step_Rev_Quad_Trendelkamp();
        this.step_quad.init(C, T, piFixed);
    }

    public static TransitionMatrixSamplerRevFixPi create(IDoubleArray _C, IDoubleArray Tinit, IDoubleArray piFixed, IReversibleSamplingStep _step_quad)
    {
        TransitionMatrixSamplerRevFixPi res = new TransitionMatrixSamplerRevFixPi(_C, Tinit, piFixed);
        res.step_quad = _step_quad;
        res.step_quad.init(_C, res.T, piFixed);

        return res;
    }

    public static TransitionMatrixSamplerRevFixPi create(IDoubleArray counts, IDoubleArray piFixed, Step_Rev_Quad_MC _step_quad)
    {
        return create(counts, null, piFixed, _step_quad);
    }

    @Override
    protected boolean step()
    {
        return step_quad.step();
    }
}
