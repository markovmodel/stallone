/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import java.util.Random;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.stat.GaussianUnivariate;

/**
 *
 * @author noe
 */
public class GaussianTest
{
    public static void main(String[] args)
    {
        Random rand = new Random();
        // Gaussian fit test
        IDataList seq = DataSequence.create.createDatalist();
        for (int i=0; i<10000; i++)
        {
            IDoubleArray x = Doubles.create.arrayFrom(rand.nextGaussian());
            seq.add(x);
        }
        
        GaussianUnivariate gu = new GaussianUnivariate(0,1);
        gu.initialize();
        gu.addToEstimate(seq);
        IDoubleArray par = gu.getEstimate();
        
        System.out.println(par.order());
        System.out.println(par.rows());
        System.out.println(par.columns());
        
        System.out.println(par);
    }
}
