/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.estimator;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;

/**
 *
 * @author noe
 */
public class TransitionMatrixLikelihood
{
    /**
     * Returns whether the given transition matrix has a nonzero probability given the counts C
     * @param T
     * @param C
     * @return 
     */
    public static boolean isFeasible(IDoubleArray T, IDoubleArray C)
    {
        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            if (T.get(it.row(),it.column()) == 0)
                return(false);
        }
        return(true);
    }
    
    /**
     * Returns the log-likelihood of C given T, i.e. 
     * log p(C|T) = sum_ij c_ij log T_ij.
     * Will return -INFINITY if p(C|T)=0.
     * @param T
     * @param C
     * @return 
     */
    public static double logLikelihood(IDoubleArray T, IDoubleArray C)
    {
        double l = 0;
        
        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            if (T.get(it.row(),it.column()) == 0)
            {
                return(Double.NEGATIVE_INFINITY);
            }
            int i = it.row();
            int j = it.column();
            l += C.get(i,j) * Math.log(T.get(i, j));            
        }        
        
        return(l);
    }
    
    public static double logLikelihoodCorrelationMatrix(IDoubleArray corr, IDoubleArray C)
    {
        double l = 0;
        
        double[] pi = new double[corr.rows()];
        for (int i=0; i<pi.length; i++)
            pi[i] = Doubles.util.sumRow(corr, i);
        
        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            if (corr.get(it.row(),it.column()) == 0 || pi[it.row()] == 0)
            {
                return(Double.NEGATIVE_INFINITY);
            }
            int i = it.row();
            int j = it.column();
            l += C.get(i,j) * Math.log(corr.get(i, j) / pi[i]);            
        }        
        
        return(l);
    }

}
