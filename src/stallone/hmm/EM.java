/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import java.util.List;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.function.IParametricFunction;
import stallone.api.hmm.IHMMHiddenVariables;
import stallone.api.hmm.IHMMOptimizer;
import stallone.api.hmm.IHMMParameters;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.io.IO;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class EM implements IHMMOptimizer
{
    private List<IDataSequence> obs;
    private HMMForwardModel model;
    private HMMCountMatrixEstimator countMatrixEstimator;
    private IParameterEstimator[] outputModelEstimators;
    private ForwardBackward trajEstimator;
    double  logLikelihood = Double.NEGATIVE_INFINITY;

    // hidden variables are stored when saveMemory mode is off
    private HMMHiddenVariables[] hidden = null;
    private boolean saveMemory = false;

    /**
     * @param _obs the observation data
     * @param eventBased if the data is stored event-based (irregular time intervals)
     * @param initialParameters the initial HMM parameters
     * @param _fOut the output model
     * @param _outputModelEstimator the estimator for the output model
     * @param _saveMemory if the hidden trajectories should be always constructed on the fly.
     */
    public EM(List<IDataSequence> _obs, boolean eventBased, 
              IHMMParameters initialParameters, IParametricFunction _fOut, IParameterEstimator _outputModelEstimator, 
              boolean _saveMemory)
    {
        this.obs = _obs;
        this.model = new HMMForwardModel(_obs, eventBased, initialParameters, _fOut);
        this.saveMemory = _saveMemory;

        // init E-estimators
        trajEstimator = new ForwardBackward(model);
        if (saveMemory)
        {
            hidden = new HMMHiddenVariables[1];
            int maxsize = 0;
            for (int i = 0; i < hidden.length; i++)
            {
                if (obs.get(i).size() > maxsize)
                {
                    maxsize = obs.get(i).size();
                }
            }
            hidden[0] = new HMMHiddenVariables(maxsize, model.getNStates());
        }
        else
        {
            hidden = new HMMHiddenVariables[obs.size()];
            for (int i = 0; i < hidden.length; i++)
            {
                hidden[i] = new HMMHiddenVariables(obs.get(i).size(), model.getNStates());
            }
        }


        // init M-estimators
        this.countMatrixEstimator = new HMMCountMatrixEstimator(eventBased, model);
        this.outputModelEstimators = new IParameterEstimator[model.getNStates()];
        for (int i = 0; i < outputModelEstimators.length; i++)
        {
            outputModelEstimators[i] = _outputModelEstimator.copy();
        }
    }

    /**
     * If false, all hidden pathways will be kept in memory. This is the faster
     * option If true, each hidden pathway will be regenerated whenever needed.
     * This is the memory-saving option.
     *
     * @param sm
     */
    public void setSaveMemory(boolean sm)
    {
        saveMemory = sm;
    }
    
    /**
     * @param mode MODE_MAXPATH = 1, MODE_VITERBI = 2, MODE_BAUMWELCH = 3;
     * default is: MODE_BAUMWELCH
     */
    public void setCountMode(int mode)
    {
        this.countMatrixEstimator.setCountMode(mode);
    }

    public double[] run(int nsteps, double dectol)
            throws ParameterEstimationException
    {
        double[] res = new double[nsteps];
        logLikelihood = Double.NEGATIVE_INFINITY;

        for (int n = 0; n < nsteps; n++)
        {
            // Initialize maximization step
            //IHMMParameters curpar = par.copy(); // working parameters
            countMatrixEstimator.initialize();
            for (int s = 0; s < outputModelEstimators.length; s++)
            {
                outputModelEstimators[s].initialize();
            }

            for (int i = 0; i < obs.size(); i++)
            {
                // Estimation step
                HMMHiddenVariables hiddenCur = null;
                if (saveMemory)
                {
                    hidden[0].setLength(obs.get(i).size());
                    hiddenCur = hidden[0];
                }
                else
                {
                    hiddenCur = hidden[i];
                }

                trajEstimator.computePath(i, hiddenCur);

                res[n] += hiddenCur.logLikelihood();
                if (Double.isNaN(res[n]))
                {
                    break;
                }

                // Update maximization step
                countMatrixEstimator.addToEstimate(obs.get(i), i, hiddenCur);
                for (int s = 0; s < outputModelEstimators.length; s++)
                {
                    outputModelEstimators[s].addToEstimate(obs.get(i), hiddenCur.getGammaByState(s));
                }
            }

            if (res[n] < logLikelihood - dectol)
            {
                System.out.println(" next likelihood = " + res[n] + " exiting.");
                return (DoublesPrimitive.util.subarray(res, 0, n));
            }
            if (res[n] > logLikelihood)
            {
                logLikelihood = res[n];
            }

            if (Double.isNaN(res[n]))
            {
                System.out.println("NaN in likelihood from E-Step");
                java.util.Arrays.fill(res, Double.NEGATIVE_INFINITY);
                return (res);
            }

            // complete maximization step
            this.model.setTransitionCounts(countMatrixEstimator.getEstimate());
            for (int s = 0; s < outputModelEstimators.length; s++)
            {
                this.model.setOutputParameters(s, outputModelEstimators[s].getEstimate());
            }
        }

        return (res);

    }

    public IHMMHiddenVariables getHidden(int itraj)
    {
        if (saveMemory)
        {
            hidden[0].setLength(obs.get(itraj).size());
            try
            {
                trajEstimator.computePath(itraj, hidden[0]);
            } catch (ParameterEstimationException ex)
            {
                IO.util.error(ex.getMessage());
            }
            return hidden[0];
        }
        else
        {
            return (hidden[itraj]);
        }
    }
    
    public IHMMParameters getParameters()
    {
        return model.getParameters();
    }
}
