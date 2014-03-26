package stallone.mc.sampling;


import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IReversibleSamplingStep;
import stallone.api.mc.MarkovModel;
import stallone.api.mi.IDeltaGDistribution;


/**
 *
 * @author cwehmeyer
 */
public class TransitionMatrixSamplerRevDeltaG extends TransitionMatrixSamplerAbstract
{
	protected IDoubleArray mu;

    private double p_step_row;

    private IReversibleSamplingStep step_row;
    private IReversibleSamplingStep step_quad;

    private IDeltaGDistribution deltaG;

    public TransitionMatrixSamplerRevDeltaG(IDoubleArray counts)
    {
        super(counts);
    }

    public TransitionMatrixSamplerRevDeltaG(IDoubleArray counts, IDoubleArray Tinit)
    {
        super(counts, Tinit);
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

        // stationary distribution
        mu = msm.stationaryDistribution(T);

        // initialize steps
        this.step_row = new Step_Rev_Row_Beta();
        this.step_row.init(C, T, mu);
        this.step_quad = new Step_Rev_Quad_MC();
        this.step_quad.init(C, T, mu);

        // weights
        int dofQuad=0, dofRow=0;
        for (int i=0; i<C.rows(); i++)
        {
            if (C.get(i,i) > -1 && doubles.sumRow(C, i) > - C.columns())
                dofRow++;
            for (int j=i+1; j<C.columns(); j++)
            {
                if (C.get(i,j)+C.get(j,i) > -2)
                    dofQuad++;
            }
        }
        this.p_step_row = (double)dofRow / (double)(dofQuad + dofRow);
    }

    public static TransitionMatrixSamplerRevDeltaG create(IDoubleArray _C, IDoubleArray Tinit, IReversibleSamplingStep _step_row, IReversibleSamplingStep _step_quad)
    {
        TransitionMatrixSamplerRevDeltaG res = new TransitionMatrixSamplerRevDeltaG(_C, Tinit);

        res.step_row = _step_row;
        res.step_row.init(_C, res.T, res.mu);

        res.step_quad = _step_quad;
        res.step_quad.init(_C, res.T, res.mu);

        return res;
    }

    public static TransitionMatrixSamplerRevDeltaG create(IDoubleArray counts, IReversibleSamplingStep _step_row, IReversibleSamplingStep _step_quad)
    {
        return create(counts, null, _step_row, _step_quad);
    }

    public IDoubleArray getInvariantDensity()
    {
        return mu;
    }

    public double detailedBalanceError()
    {
        return super.computeDetailedBalanceError(mu);
    }

    @Override
    protected boolean step()
    {
        if (Math.random() < this.p_step_row) // Reversible edge shift
        {
            return step_row.step();
        }
        else
        {
            return step_quad.step();
        }
    }
}