/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import java.util.ArrayList;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IExpectationMaximization;
import stallone.api.hmm.IHMM;
import stallone.api.hmm.IHMMParameters;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.ints.IIntArray;
import stallone.stat.DiscreteDistribution_Old;

/**
 *
 * @author noe
 */
public class DiscreteHMMTest
{
    public static void main(String[] args) 
            throws ParameterEstimationException
    {
        double[][] Tarr = {
        {0.9, 0.1},
        {0.05, 0.95}};
        IDoubleArray T = doublesNew.matrix(Tarr);
        IIntArray hiddenTraj = msm.trajectory(T, 0, 10000);

        double[] poutArr1 = {0.0, 0.1, 0.2, 0.4, 0.2, 0.1, 0.0, 0.0, 0.0};
        DiscreteDistribution_Old pout1 = new DiscreteDistribution_Old(poutArr1);        
        double[] poutArr2 = {0.0, 0.0, 0.0, 0.1, 0.2, 0.4, 0.2, 0.1, 0.0};
        DiscreteDistribution_Old pout2 = new DiscreteDistribution_Old(poutArr2);
        int nSamplePerTimestep = 5;

        // create observation
        IDataList obs = dataNew.createDatalist();
        for (int i=0; i<hiddenTraj.size(); i++)
        {
            IDoubleArray oi = doublesNew.array(poutArr1.length);
            for (int k=0; k<nSamplePerTimestep; k++)
            {
                int s = 0;
                if (hiddenTraj.get(i) == 0)
                    s = pout1.sample();
                else
                    s = pout2.sample();
                oi.set(s, oi.get(s)+1);
            }
            obs.add(oi);
        }
        List<IDataSequence> obsList = new ArrayList();
        obsList.add(obs);
        
        /*for (int i=0; i<obs.size(); i++)
        {
            System.out.println(obs.get(i));
        }
        System.exit(0);*/
        
        // initial parameters
        IHMMParameters par0 = hmmNew.parameters(2, true, true);
        double[][] Testarr0 = {{0.5,0.5},{0.5,0.5}};
        IDoubleArray Test0 = doublesNew.matrix(Testarr0);
        par0.setTransitionMatrix(Test0);
        
        IDoubleArray poutEst1 = doublesNew.arrayRandom(poutArr1.length);
        alg.normalize(poutEst1, 1);
        par0.setOutputParameters(0, poutEst1);
        IDoubleArray poutEst2 = doublesNew.arrayRandom(poutArr1.length);
        alg.normalize(poutEst2, 1);
        par0.setOutputParameters(1, poutEst2);
        
        // Estimate
        double[] uniformPrior = new double[poutArr1.length];
        java.util.Arrays.fill(uniformPrior, 1.0/(double)uniformPrior.length);
        IExpectationMaximization EM = hmmNew.emDiscrete(obsList, par0, uniformPrior);
        EM.setMaximumNumberOfStep(10);
        EM.setLikelihoodDecreaseTolerance(0.1);
        EM.run();
        
        double[] likelihoods = EM.getLikelihoodHistory();
        System.out.println("likelihoods:\n"+doubleArrays.toString(likelihoods,"\n"));
        System.out.println();
        IHMM hmmEst = EM.getHMM();
        IHMMParameters parEst = hmmEst.getParameters();
        System.out.println(parEst.getTransitionMatrix());
        System.out.println();
        System.out.println(parEst.getOutputParameters(0));
        System.out.println();
        System.out.println(parEst.getOutputParameters(1));
        System.out.println();
    }
}
