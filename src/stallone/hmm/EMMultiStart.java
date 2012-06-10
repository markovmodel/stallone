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
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class EMMultiStart implements IHMMOptimizer
{
    // fixed parameters
    private List<IDataSequence> obs;
    private IParametricFunction outputModel;
    private IParameterEstimator outputEstimator;
    private IHMMParameters initialParameters;
    private int nstates;
    
    // optimization parameters
    private int nscansteps;
    private int nscans;
    private int nconvsteps;
    private double dectol;
    
    // result
    private IHMM hmmBest = null;
    
    public EMMultiStart(List<IDataSequence> _obs, IParametricFunction _outputModel, IParameterEstimator _outputEstimator, IHMMParameters _initialParameters)
    {
        this.obs = _obs;
        this.outputModel = _outputModel;
        this.outputEstimator = _outputEstimator;
        this.initialParameters = _initialParameters;
    }

    public void setNumberOfScanningSteps(int steps)
    {
        this.nscansteps = steps;
    }

    public void setNumberOfScans(int n)
    {
        this.nscans = n;
    }

    public void setNumberOfConvergenceSteps(int steps)
    {
        this.nconvsteps = steps;
    }

    public void setLikelihoodDecreaseTolerance(double tol)
    {
        this.dectol = tol;
    }
    
    public void run()
            throws ParameterEstimationException
    {
        double likelihoodBest = Double.NEGATIVE_INFINITY;

        IExpectationMaximization emBest = null;

        for (int i = 0; i < nscans; i++)
        {
            System.out.println("SCAN " + i);

            // construct HMM
            IExpectationMaximization em = HMM.create.em(obs, outputModel, outputEstimator, initialParameters);

            double[] likelihoods = null;
            try
            {
                em.setMaximumNumberOfStep(nscansteps);
                em.setLikelihoodDecreaseTolerance(dectol);
                em.run();
                likelihoods = em.getLikelihoodHistory();

                System.out.println("Likelihood history: ");
                DoublesPrimitive.util.print(likelihoods, "\n");
                System.out.println();

                //System.out.println(" last E = "+DoubleArrays.toString(((HMMParametersFretBinned)em.getParameters()).getEfficiency()));
                //System.out.println(" last likelihood check: "+em.getHidden(0).logLikelihood());

                if (likelihoods[likelihoods.length - 1] > likelihoodBest)
                {
                    likelihoodBest = likelihoods[likelihoods.length - 1];
                    emBest = em;
                    //System.out.println(" updating best. Now best likelihood: "+emBest.getHidden(0).logLikelihood());                
                }
            } catch (ParameterEstimationException e)
            {
                System.out.println(" CAUGHT exception in EMmult (see below). Will continue with another try\n" + e);
            }

            Runtime.getRuntime().gc();
        }

        if (emBest == null)
        {
            throw (new RuntimeException("Could not optimize a single model. It seems all attempts have failed. STOPPING!"));
        }
        System.out.println("Refining the best model with log L = " + likelihoodBest);
        System.out.println(" and Parameters = " + emBest.getHMM().getParameters());
        System.out.println(" and likelihood = " + emBest.getHMM().getLogLikelihood());

        /*
         * HMMModelFretBinned modelfine = new HMMModelFretBinned(photons,
         * nstates, bg); HMMParameterEstimatorFretBinned parEstimatorfine = new
         * HMMParameterEstimatorFretBinned(photons, bg, modelfine, obs); EM
         * emfine = new EM(obs, modelfine, parEstimatorfine, false);
         * emfine.setSaveMemory(false);
         * emfine.setParameters(emBest.getParameters()); double[] l2 =
         * emfine.run(1, 1);
         *
         * System.out.println(" 1 likelihood = "+l2[0]); System.out.println(" 1
         * likelihood = "+emBest.getHidden(0).logLikelihood());
         */

        emBest.setMaximumNumberOfStep(nconvsteps);
        emBest.setLikelihoodDecreaseTolerance(dectol);
        emBest.run();
        double[] likelihoods = emBest.getLikelihoodHistory();

        DoublesPrimitive.util.print(likelihoods, "\n");
        hmmBest = emBest.getHMM();
    }    
    
    public IHMM getHMM()
    {
        return (hmmBest);
    }
}
