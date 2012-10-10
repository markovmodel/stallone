/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.*;

/**
 *
 * @author noe
 */
public class GaussianHMMTest
{
    public static void main(String[] args) throws ParameterEstimationException
    {
        
        // prepare observation
        Random rand = new Random();
        IDataList seq = DataSequence.create.createDatalist();
        for (int i=0; i<10000; i++)
        {
            IDoubleArray x;
            if ((i/1000) % 2 == 0)
                 x = Doubles.create.arrayFrom(rand.nextGaussian()-1);
            else
                 x = Doubles.create.arrayFrom(rand.nextGaussian()+1);
            seq.add(x);
        }
        
        List<IDataSequence> obs = new ArrayList();
        obs.add(seq);

        IExpectationMaximization em = HMM.create.emGaussian(obs, 2);
        em.setMaximumNumberOfStep(100);
        em.setLikelihoodDecreaseTolerance(1);
        
        // print initial parameters
        System.out.println("Initial Parameters: "+em.getHMM().getParameters());
        
        em.run();
        double[] L = em.getLikelihoodHistory();
        
        DoublesPrimitive.util.print(L,"\n");
        
        // print hidden trace
        IHMMHiddenVariables hidden = em.getHMM().getHidden(0);
        for (int i=0; i<hidden.size(); i++)
            System.out.println(i+ "\t" + hidden.mostProbableState(i));
        
        // print parameters
        System.out.println("Parameters: ");
        IHMMParameters par = em.getHMM().getParameters();
        System.out.println(par);
    }
}
