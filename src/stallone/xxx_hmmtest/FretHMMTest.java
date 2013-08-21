/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.*;
import stallone.api.ints.IIntArray;
import stallone.api.mc.MarkovModel;

/**
 *
 * @author noe
 */
public class FretHMMTest
{
    public static IExpectationMaximization emFRET(List<IDataSequence> obs, IHMMParameters par)
    {
            BinnedFretEfficiencyOutputModel_tmp model = new BinnedFretEfficiencyOutputModel_tmp(0);
            return HMM.create.em(obs, model, model, par);
    }    
    
    public static void main(String[] args)
    {
        // true parameters
        IDoubleArray T = Doubles.create.array(new double[][]{{0.99, 0.01},{0.01, 0.99}});
        double[] E = new double[]{0.2,0.7};
        double k = 0.1; // intensity

        // hidden trajectory
        IIntArray S = MarkovModel.util.trajectory(T, 0, 10000);
        
        // generate Observation
        IDataList obs1 = DataSequence.create.createDatalist();
        for (int i=0; i<S.size(); i++)
        {
            double e = E[S.get(i)];
            IDoubleArray x = Doubles.create.array(2);
            if (Math.random() < k)
            {
                if (Math.random() < e)
                    x.set(1,1);
                else
                    x.set(0,1);
            }
            obs1.add(x);
        }
        List<IDataSequence> obs = new ArrayList<IDataSequence>();
        obs.add(obs1);

        // initial parameters
        IHMMParameters par0 = HMM.create.parameters(2, true, true);
        IDoubleArray T0 = Doubles.create.array(new double[][]{{0.97, 0.03},{0.05, 0.95}});
        par0.setTransitionMatrix(T0);
        par0.setOutputParameters(0, Doubles.create.arrayFrom(0.2));
        par0.setOutputParameters(1, Doubles.create.arrayFrom(0.8));

        // EM
        IExpectationMaximization em = emFRET(obs, par0);
        try
        {
            // optimize
            em.setMaximumNumberOfStep(100);
            em.setLikelihoodDecreaseTolerance(0.1);
            em.run();
            double[] logHistory = em.getLogLikelihoodHistory();
            
            /*
            System.out.println("path: ");
            IHMMHiddenVariables hidden = em.getHidden(0);
            for (int t=0; t<hidden.size(); t++)
                System.out.println(t+"\t"+S.get(t)+"\t"+obs1.get(t) +"\t"+hidden.mostProbableState(t));
            
            System.out.println("Likelihood history:");
            DoublesPrimitive.util.print(logHistory,"\n");
            System.out.println();*/
        } 
        catch (ParameterEstimationException ex)
        {
            //Logger.getLogger(FretFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        IHMMParameters par = em.getHMM().getParameters();
        /*
        System.out.println("final parameters: ");
        System.out.println(par);
        System.out.println();*/
        
        // assert correctness
        /*assertEqual(par.getTransitionMatrix(), T, 0.05);
        assertTrue(Math.abs(par.getOutputParameters(0).get(0) - E[0]) < 0.1);
        assertTrue(Math.abs(par.getOutputParameters(1).get(0) - E[1]) < 0.1);*/
    }
}
