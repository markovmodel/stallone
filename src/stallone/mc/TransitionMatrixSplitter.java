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
        System.out.println("Splitting state "+state);
        
        // useful stuff
        int n = T_old.rows();
        
        // compute correlation matrix
        IDoubleArray pi_old = msm.stationaryDistribution(T_old);
        IDoubleArray Pi_old = doublesNew.diag(pi_old);
        IDoubleArray C_old = alg.product(Pi_old, T_old);
        
        System.out.println("C_old = \n"+C_old);
        System.out.println();
        System.out.println("pi_old = \n"+pi_old);
        
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

        System.out.println("pi_split_1 = "+pi_split_1);
        System.out.println("pi_split_2 = "+pi_split_2);
        
        // compute diagonal elements
        double d1 = pi_split_1 * Math.exp(-k1);
        double d2 = pi_split_2 * Math.exp(-k2);
        double epsilon = 1e-4;
        double diag_el_1 = Math.min(d1,d1*C_old.get(state,state)/(d1+d2) - epsilon);
        double diag_el_2 = Math.min(d2,d2*C_old.get(state,state)/(d1+d2) - epsilon);
        C_split.set(state,state,diag_el_1);
        C_split.set(n,n,diag_el_2);

        System.out.println("diag_el_1 = "+diag_el_1);
        System.out.println("diag_el_2 = "+diag_el_2);
        
        // compute transition elements in splitting block
        double split_block_trans = 0.5*(C_old.get(state,state) - diag_el_1 - diag_el_2);
        C_split.set(state, n, split_block_trans);
        C_split.set(n, state, split_block_trans);

        System.out.println("trans_el = "+split_block_trans);
        System.out.println("checking sums : "+(C_split.get(state,state)+C_split.get(n,n)+2*split_block_trans)+" = "+C_old.get(state,state));        
        
        // set remaining elements
        for (int i=0; i<n; i++)
        {
            if (i != state)
            {
                C_split.set(state, i, C_old.get(state,i) * pi_split.get(state)/pi_old.get(state));
                C_split.set(i, state, C_split.get(state,i));
                C_split.set(n, i, C_old.get(state,i) - C_split.get(state, i));
                C_split.set(i, n, C_split.get(n,i));
            }
        }

        System.out.println("C_split = \n"+C_split);
        
        // normalize
        IDoubleArray T_split = C_split.copy();
        alg.normalizeRows(T_split, 1);
        
        System.out.println("T_split = \n"+T_split);

        double err = alg.distance(msm.stationaryDistribution(T_split), pi_split);
        System.out.println("pi error = "+err);
        
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
