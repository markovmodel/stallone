/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat.modelselection;

import static stallone.api.API.*;
import stallone.util.MathTools;

/**
 *
 * Samples from one- and two-state exit time distributions in order to distinguish single- and biexponential distributions
 * 
 * @author noe
 */
public class ExitTimes
{
    private double[] exitTimes;
    
    // switch
    private int nExp = 1;

    // single-state parameter
    private double k;
    // average
    private int n1=0;
    private double ksum=0;

    // two-state parameters
    private double a, k1, k2;
    // average
    private int n2=0;
    private double asum=0, k1sum=0, k2sum=0;
    
    
    
    public ExitTimes(double[] _exitTimes)
    {
        this.exitTimes = _exitTimes;
        nExp = 1;
        k = doubleArrays.mean(_exitTimes);
    }

    /**
     * p(O|k) = prod_i k exp(-k t_i).
     * Log p(O|k) = sum_i (log k - k t_i).
     * @return 
     */
    private double logLikelihood1(double _k)
    {
        double res = exitTimes.length * Math.log(_k);
        for (double ti : exitTimes)
            res -= _k*ti;
        return res;
    }
    
    private double logPrior1(double _k)
    {
        return -Math.log(_k);
    }

    /**
     * p(O|a,k1,k2) = prod_i Z^-1 (a*Exp[-k1*t] + (1 - a)*Exp[-k2*t])/(a/k1 + (1 - a)/k2)
     * Log p(O|a,k1,k2) = sum_i Z^-1 log (a*Exp[-k1*t] + (1 - a)*Exp[-k2*t]) - log (a/k1 + (1 - a)/k2)
     * @return 
     */
    private double logLikelihood2(double _a, double _k1, double _k2)
    {
        double res = -exitTimes.length * Math.log((_a/_k1 + (1 - _a)/_k2));
        for (double ti : exitTimes)
            res += Math.log(_a*Math.exp(-_k1*ti) + (1 - _a)*Math.exp(-_k2*ti));
        return res;
    }

    private double logPrior2(double _a, double _k1, double _k2)
    {
        return -Math.log(a*_k1+(1-a)*_k2);
    }
    
    /**
     * MCMC step within the single-state case
     */
    private void step11()
    {
        double r = Math.random()+0.5;
        double k_try = r*k;
        
        double pAcc = r*Math.exp(logPrior1(k_try)+logLikelihood1(k_try) -logPrior1(k)-logLikelihood1(k));

        if (Math.random() < pAcc)
        {
            k = k_try;
        }
    }
    
    /**
     * MCMC step within the single-state case
     */
    private void step22()
    {
        int sel = MathTools.randomInt(0,3);
        double a_try = a;
        double k1_try = k1;
        double k2_try = k2;
        double r = 1;
        
        if (sel == 0)
        {
            a_try = Math.random();
        }
        else if (sel == 1)
        {
            r = Math.random()+0.5;
            k1_try = r*k1;
        }        
        else
        {
            r = Math.random()+0.5;
            k2_try = r*k2;
        }        

        double pAcc = r*Math.exp(logPrior2(a_try,k1_try,k2_try)+logLikelihood2(a_try,k1_try,k2_try) -logPrior2(a,k1,k2)-logLikelihood2(a,k1,k2));

        if (Math.random() < pAcc)
        {
            a = a_try;
            k1 = Math.min(k1_try,k2_try);
            k2 = Math.max(k1_try,k2_try);
        }
    }
    
    /**
     * split step
     */
    private void stepSplit()
    {
        double a_try = Math.random();
        double b = Math.random();
        
        double k1_try = b*k;
        double k2_try = (1-a_try*b)/(1-a_try) * k;
        
        //double logPProp = 3;
        double logPProp = Math.log((1-b)/((k2_try-k1_try)*(k2_try-k1_try)));
        
        double pAcc = Math.exp(- logPProp + logPrior2(a_try,k1_try,k2_try) + logLikelihood2(a_try,k1_try,k2_try) - logPrior1(k) - logLikelihood1(k));
        
        if (Math.random() < pAcc)
        {
            nExp = 2;
            a = a_try;
            k1 = k1_try;
            k2 = k2_try;
        }
    }
    
    /**
     * split step
     */
    private void stepMerge()
    {
        double k_try = a*k1 + (1-a)*k2;
        
        double logPProp = Math.log((1-(k1/k_try))/((k2-k1)*(k2-k1)));
        //double logPProp = 3;
        
        double pAcc = Math.exp(+ logPProp + logPrior1(k_try) + logLikelihood1(k_try) - logPrior2(a,k1,k2) - logLikelihood2(a,k1,k2) );
        //System.out.println("p_merge = "+pAcc+"\t"+logPProp +"\t"+ logPrior1(k_try) + "\t"+ logLikelihood1(k_try) + "\t-" + logPrior2(a,k1,k2) +"\t-" + logLikelihood2(a,k1,k2));
        
        if (Math.random() < pAcc)
        {
            nExp = 1;
            k = k_try;
        }
    }
    
    private void step()
    {
        if (nExp == 1)
        {
            if (Math.random() < 0.5)
                step11();
            else
                stepSplit();
        }
        else
        {
            if (Math.random() < 0.5)
                step22();
            else
                stepMerge();
        }
    }
        
    public void run(int nburnin, int nsample)
    {
        for (int i=0; i<nburnin; i++)
        {
            step();
        }

        for (int i=0; i<nsample; i++)
        {
            step();
            
            if (nExp == 1)
            {
                n1++;
                ksum += k;
            }
            else
            {
                n2++;
                asum += a;
                k1sum += k1;
                k2sum += k2;
            }
        }        
    }
    
    public double getNumberOfStates()
    {
        return((double)(n1+2*n2)/(double)(n1+n2));
    }
    
    public double getMeanK()
    {
        return(ksum / (double)n1);
    }

    /**
     * 
     * @return {a,k1,k2}
     */
    public double[] getMeanK2()
    {
        return(new double[]{asum / (double)n2, k1sum / (double)n2, k2sum / (double)n2});
    }        
}
