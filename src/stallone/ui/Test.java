/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.algebra.IEigenvalueSolver;
import stallone.api.doubles.IDoubleArray;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class Test
{
    public static void main(String[] args) 
            throws FileNotFoundException, IOException
    {        
        List<String> solvernames = algNew.queryEigenvalueDecompositionNames();
        System.out.println(solvernames);

        int N = 20000;
        int radius = 20;
        double D = 0.5;
        IDoubleArray T = doublesNew.sparseMatrix(N, N);
        for (int i=0; i<N; i++)
        {
            double sum = 0;
            
            int jmin = Math.max(0, i-radius);
            int jmax = Math.min(i+radius, N-1);
            for (int j=jmin; j<=jmax; j++)
            {
                T.set(i,j,D * Math.random());
            }
            T.set(i,i,1);
            //System.out.println(T.viewRow(i));
            //System.out.println(i+"\t"+sum+"\t"+alg.norm(T.viewRow(i), 1));
        }
        
        alg.normalizeRows(T, 1);
        
        //IDoubleArray M = doublesNew.fromFile("/Users/noe/data/msms/Anton-BPTI/tica-lag10000-dim2_regspace0.3/msm_lag1_rev/T.dat");
        
        int nev = 5;
        IEigenvalueSolver solverSparse = algNew.eigensolverSparse(T, nev);
        long t1 = System.currentTimeMillis();
        solverSparse.perform();
        long t2 = System.currentTimeMillis();
        IEigenvalueDecomposition evdSparse = solverSparse.getResult();
        IDoubleArray evalSparse = evdSparse.getEvalNorm();
        System.out.println("SPARSE time: "+(t2-t1));
        
        /*
        IEigenvalueSolver solverDense = algNew.eigensolverDense(T);
        //solverDense.setNumberOfRequestedEigenvalues(5);
        long t3 = System.currentTimeMillis();
        solverDense.perform();
        long t4 = System.currentTimeMillis();
        IEigenvalueDecomposition evdDense = solverDense.getResult();
        IDoubleArray evalDense = evdDense.getEvalNorm();
        System.out.println("DENSE time:  "+(t4-t3));*/

        for (int i=0; i<nev; i++)
            System.out.println(evalSparse.get(i));//+"\t"+evalDense.get(i));
        
    }
}
