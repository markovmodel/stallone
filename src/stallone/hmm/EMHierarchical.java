/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import java.util.List;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.function.IParametricFunction;
import stallone.api.hmm.*;
import stallone.api.ints.IntsPrimitive;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class EMHierarchical implements IHMMOptimizer
{
    // fixed parameters
    private List<IDataSequence> obs;
    private IParametricFunction outputModel;
    private IParameterEstimator outputEstimator;
    private int nstates;

    // optimization parameters
    private IHMMParameters[] parameters;
    private int nsteps;
    private double dectol;

    // result
    private IHMM hmmBest = null;


    public EMHierarchical(List<IDataSequence> _obs, IParametricFunction _outputModel, IParameterEstimator _outputEstimator)
    {
        this.obs = _obs;
        this.outputModel = _outputModel;
        this.outputEstimator = _outputEstimator;
    }

    public void setInitialParameters(IHMMParameters[] _parameters)
    {
        this.parameters = _parameters;
    }

    /**
     * Sets the number of optimization steps for level 1
     * @param n
     */
    public void setInitialNumberOfSteps(int n)
    {
        this.nsteps = n;
    }

    public void setLikelihoodDecreaseTolerance(double tol)
    {
        this.dectol = tol;
    }

    public void run()
            throws ParameterEstimationException
    {
        double[] likelihoods = new double[parameters.length];

        IExpectationMaximization emlast = null;

        int level = 1;
        int totaloptsteps = 0;
        while(true)
        {
            int optsteps = nsteps;
            if (level > 1)
            {
                optsteps = (int) Math.pow(nsteps, (level - 1));
            }

            System.out.println("#level " + level);

            for (int i = 0; i < likelihoods.length; i++)
            {
                System.out.println("# replica " + (i + 1));

                emlast = HMM.create.em(obs, outputModel, outputEstimator, parameters[i]);

                double[] likelihoodsRep = null;
                try
                {
                    emlast.setMaximumNumberOfStep(optsteps);
                    emlast.setLikelihoodDecreaseTolerance(dectol);
                    emlast.run();
                    likelihoodsRep = emlast.getLogLikelihoodHistory();
                    likelihoods[i] = likelihoodsRep[likelihoodsRep.length - 1];
                    parameters[i] = emlast.getHMM().getParameters().copy();

                    for (int k=0; k<likelihoodsRep.length; k++)
                        System.out.println((totaloptsteps+k+1)+"\t"+likelihoodsRep[k]);
                } catch (ParameterEstimationException e)
                {
                    System.out.println(" CAUGHT exception in EMHierarchical (see below). Will continue with another try\n" + e);
                }

                Runtime.getRuntime().gc();
            }

            totaloptsteps += optsteps;

            // if we only have one parameter set left, we're done.
            if (parameters.length == 1)
                break;

            // select the best
            int[] SI = IntsPrimitive.util.mirror(DoublesPrimitive.util.sortedIndexes(likelihoods));
            double[] newlikelihoods = new double[likelihoods.length / 2];
            IHMMParameters[] newparameters = new IHMMParameters[newlikelihoods.length];
            for (int i = 0; i < newlikelihoods.length; i++)
            {
                newlikelihoods[i] = likelihoods[SI[i]];
                newparameters[i] = parameters[SI[i]];
            }

            likelihoods = newlikelihoods;
            parameters = newparameters;
            level++;
        }

        hmmBest = emlast.getHMM();
    }

    public IHMM getHMM()
    {
        return hmmBest;
    }
}
