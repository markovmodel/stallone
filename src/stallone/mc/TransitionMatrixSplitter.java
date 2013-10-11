/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import java.io.FileNotFoundException;
import java.io.IOException;
import static stallone.api.API.*;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class TransitionMatrixSplitter
{
    private IDoubleArray T_old;
    
    public TransitionMatrixSplitter(IDoubleArray _Ta)
    {
        this.T_old = _Ta;
    }
    
    public IDoubleArray splitForLifetimes(int state, double p1, double k1, double k2)
    {
        // test input!
        //System.out.println("Splitting state "+state+"\t to probability "+p1+"\tk1 "+k1+"\tk2 "+k2);
        
        // compute correlation matrix
        IDoubleArray pi_old = msm.stationaryDistribution(T_old);
        IDoubleArray Pi_old = doublesNew.diag(pi_old);
        IDoubleArray C_old = alg.product(Pi_old, T_old);
        // useful stuff
        int n = T_old.rows();
        double pi_state_old = pi_old.get(state);
        
        //System.out.println("C_old = \n"+C_old);
        //System.out.println();
        //System.out.println("pi_old = \n"+pi_old);
        
        // fill unaffected states
        IDoubleArray C_split = doublesNew.matrix(n+1,n+1);
        IDoubleArray pi_split = doublesNew.array(n+1);
        for (int i=0; i<n; i++)
            for (int j=0; j<n; j++)
                if (i != state && j != state)
                {
                    C_split.set(i,j, C_old.get(i,j));
                    pi_split.set(i, pi_old.get(i));
                }
        
        // compute stationary probabilities of the new states
        double pi_split_1 = p1 * pi_old.get(state);
        double pi_split_2 = pi_old.get(state) - pi_split_1;
        pi_split.set(state, pi_split_1);
        pi_split.set(n, pi_split_2);

        //System.out.println("pi_split_1 = "+pi_split_1);
        //System.out.println("pi_split_2 = "+pi_split_2);
        
        // compute diagonal elements
        double d1 = pi_split_1 * Math.exp(-k1);
        double d2 = pi_split_2 * Math.exp(-k2);
        C_split.set(state,state,d1);
        C_split.set(n,n,d2);
        //System.out.println("d1 = "+d1);
        //System.out.println("d2 = "+d2);
        
        // set transition elements in the splitting block
        double split_block_trans = 0.5 * (C_old.get(state,state) - d1 - d2);
        //System.out.println("initial st: "+split_block_trans);
        
        if (split_block_trans < 0 || split_block_trans > Math.min(pi_split_1 - d1, pi_split_2 - d2))
        {
            split_block_trans = Math.min(pi_split_1 - d1, pi_split_2 - d2) / (double)(10*n+1);
            //System.out.println("correcting to: "+split_block_trans);
        }
        C_split.set(state, n, split_block_trans);
        C_split.set(n, state, split_block_trans);

        //System.out.println("trans_el = "+split_block_trans);
        
        // set remaining elements
        double sum_old = pi_state_old - C_old.get(state,state);
        double rest1 = pi_split_1 - d1 - split_block_trans;
        double rest2 = pi_split_2 - d2 - split_block_trans;
        //System.out.println("sum_old = "+sum_old);
        //System.out.println("rest1 = "+rest1);
        //System.out.println("rest2 = "+rest2);
        for (int i=0; i<n; i++)
        {
            if (i != state)
            {
                C_split.set(state, i, rest1 * C_old.get(state,i)/sum_old);
                C_split.set(i, state, C_split.get(state,i));
                C_split.set(n, i, rest2 * C_old.get(state,i)/sum_old);
                C_split.set(i, n, C_split.get(n,i));
            }
        }

        //System.out.println("C_split = \n"+C_split);
        
        // normalize
        IDoubleArray T_split = C_split.copy();
        alg.normalizeRows(T_split, 1);
        
        //System.out.println("T_split = \n"+T_split);

        IDoubleArray pi_res = msm.stationaryDistribution(T_split);
        double err = alg.distance(pi_res, pi_split);
        //System.out.println("pi error = "+err);
        
        double p1_res = pi_res.get(state) / (pi_res.get(state)+pi_res.get(n));
        double k1_res = -Math.log(T_split.get(state,state));
        double k2_res = -Math.log(T_split.get(n,n));
        //System.out.println("Have obtained parameters: p1 "+p1_res+"\tk1 "+k1_res+"\tk2 "+k2_res);
        //System.out.println("Compared to reference:    p1 "+p1+"\tk1 "+k1+"\tk2 "+k2);

        
        return T_split;
    }
    
    public static void main(String[] args)
            throws FileNotFoundException, IOException
    {
        if (args.length == 0)
        {
            System.out.println("Usage: TransitionMatrixSplitter <T-matrix> <split-state> <p1> <k1> <k2>");
        }
        
        IDoubleArray T_old = doublesNew.fromFile(args[0]);
        int split_state = str.toInt(args[1]);
        double p1 = str.toDouble(args[2]);
        double k1 = str.toDouble(args[3]);
        double k2 = str.toDouble(args[4]);
        
        TransitionMatrixSplitter tms = new TransitionMatrixSplitter(T_old);
        IDoubleArray T_split = tms.splitForLifetimes(split_state, p1, k1, k2);
    }
}
