/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.doubles.IDoubleArray;
import stallone.mc.pcca.PCCA;

/**
 *
 * @author noe
 */
public class NinjaUtilities
{    
    public static IDoubleArray tuneMetastability(IDoubleArray T, double timescaleFactor)
    {
        IDoubleArray Tnew = T.copy();
        //IDoubleArray Pi = doublesNew.diag(msm.stationaryDistribution(T));
        for (int i=0; i<Tnew.rows(); i++)
        {
            // scale diagonal
            Tnew.set(i,i, Math.pow(Tnew.get(i,i), 1.0/timescaleFactor));
            
            // compute off-diagonal scaling factor
            double cOffdiag = (1.0 - Tnew.get(i,i)) / (alg.sum(Tnew.viewRow(i)) - Tnew.get(i,i));
            
            for (int j=0; j<Tnew.columns(); j++)
            {
                if (j != i)
                    Tnew.set(i, j, cOffdiag * Tnew.get(i,j));
            }
        }

        return Tnew;
    }   
    
    public static void main(String[] args)
    {
        IDoubleArray T = doublesNew.array(new double[][]{
            {0.8, 0.2 , 0,    0   },
            {0.2, 0.75, 0.05, 0   },
            {0  , 0.05, 0.75, 0.2},
            {0  , 0   , 0.2 , 0.8 }});
        IDoubleArray ts1 = msm.timescales(T, 1);

        IEigenvalueDecomposition evd = alg.evd(T);
        IDoubleArray Lambda = evd.getDiagonalMatrix();
        System.out.println("old timescales: "+ts1);
        System.out.println("Lambda = \n"+Lambda+"\n");

        int nhidden = 2;
        PCCA pcca = msmNew.createPCCA(T, nhidden);
        IDoubleArray R = evd.getRightEigenvectorMatrix().viewReal();
        IDoubleArray Rsub = R.viewBlock(0, 0, T.rows(), nhidden);
        pcca.setEigenvectors(Rsub);
        pcca.perform();
        IDoubleArray M = pcca.getFuzzy();
        
        System.out.println("M\n"+M);

        IDoubleArray pi = msm.stationaryDistribution(T);
        IDoubleArray Pi = doublesNew.diag(pi);
        IDoubleArray piC = alg.product(alg.transposeToNew(M),pi);
        IDoubleArray PiC = doublesNew.diag(piC);
        
        System.out.println("Pi = \n"+Pi+"\n");
        System.out.println("PiC = \n"+PiC+"\n");
        
        IDoubleArray Res = alg.transposeToNew(M);
        IDoubleArray Int = alg.product(alg.product(Pi,M),alg.inverse(PiC));

        System.out.println("R = \n"+Res+"\n");
        System.out.println("I = \n"+Int+"\n");
        
        IDoubleArray RItinv = alg.transposeToNew(alg.inverse(alg.product(Res,Int)));
        IDoubleArray IntT = alg.transposeToNew(Int);
        IDoubleArray ResT = alg.transposeToNew(Res);
        
        IDoubleArray TC = alg.product(alg.product(alg.product(RItinv,IntT),T),ResT);
        System.out.println("TC = \n"+TC+"\n");

        IEigenvalueDecomposition evdC = alg.evd(TC);
        IDoubleArray LambdaC = evdC.getDiagonalMatrix();
        System.out.println("LambdaC = \n"+LambdaC+"\n");
        
        System.exit(0);
        /*
        IDoubleArray pi = msm.stationaryDistribution(T);
        IDoubleArray pichi = M.copy();
        for (int i=0; i<pichi.rows(); i++)
            for (int j=0; j<pichi.columns(); j++)
                pichi.set(i,j, pi.get(i)*M.get(i,j));
        
        IDoubleArray pHidden = doublesNew.array(M.columns());
        for (int j=0; j<pHidden.size(); j++)
            pHidden.set(j, doubles.sum(pichi.viewColumn(j)));
        
        //System.out.println("pHidden: "+pHidden);

        IDoubleArray chi = M.copy();
        for (int i=0; i<chi.rows(); i++)
            for (int j=0; j<chi.columns(); j++)
                chi.set(i,j, pichi.get(i,j) / pHidden.get(j));

        System.out.println("chi: \n"+chi);

        // low-rank approx to T
        IDoubleArray D2 = doublesNew.diag(1.0, evd.getEvalNorm(1));
        IDoubleArray Rinv = alg.inverse(R);
        IDoubleArray Lsub = Rinv.viewBlock(0, 0, nhidden, T.columns());

        IDoubleArray T2 = alg.product(alg.product(Rsub, D2), Lsub);
        
        System.out.println("T-low-rank:\n"+T2);
        
        IDoubleArray Tc = computeHiddenTransitionMatrix(T, chi);
        System.out.println("Tc\n"+Tc);

        IDoubleArray Tc2 = computeHiddenTransitionMatrix(T2, chi);
        System.out.println("Tc2\n"+Tc2);
        IDoubleArray ts2 = msm.timescales(Tc, 1);
        System.out.println("new timescales: "+ts2);
        */

    }
}
