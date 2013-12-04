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

        int N = 10;
        int n = 4;
        IDoubleArray T = doublesNew.sparseMatrix(N, N);
        for (int i=0; i<N; i++)
        {
            double sum = 0;
            T.set(i,i,1);
            for (int k=0; k<n; k++)
            {
                int j = MathTools.randomInt(0, N);
                T.set(i,j,Math.random());
            }
            System.out.println(T.viewRow(i));
            System.out.println(i+"\t"+sum+"\t"+alg.norm(T.viewRow(i), 1));
        }
        
        System.out.println("NORMALIZING...");
        alg.normalizeRows(T, 1);
        
        for (int i=0; i<N; i++)
        {
            double sum = 0;
            for (int j=0; j<N; j++)
            {
                sum += T.get(i,j);
            }
            System.out.println(T.viewRow(i));
            System.out.println(i+"\t"+sum+"\t"+alg.norm(T.viewRow(i), 1));
        }
            

        //IDoubleArray M = doublesNew.fromFile("/Users/noe/data/msms/Anton-BPTI/tica-lag10000-dim2_regspace0.3/msm_lag1_rev/T.dat");
        
        IEigenvalueSolver solver = algNew.eigenSolver(T, "SPARSE_ARPACK");
        solver.setNumberOfRequestedEigenvalues(2);
        solver.perform();
        IEigenvalueDecomposition evd = solver.getResult();
        
        System.out.println("eval = "+evd.getEvalNorm());
    }
}
