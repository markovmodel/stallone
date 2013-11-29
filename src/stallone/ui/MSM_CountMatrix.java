/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import static stallone.api.API.*;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import java.io.IOException;
import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.mc.ICountMatrixEstimator;
import stallone.util.Arguments;


/**
 *
 * @author noe
 */
public class MSM_CountMatrix
{

    public static void main(String[] args) throws IOException
    {
        if (args.length == 0)
        {
            System.out.println("CountMatrix\n"
                    + "-estimate <dtraj(s)> [-lag <lag>] [-sampleLag] [-countrev] [-subset <states>] \n\n"
                    + "-submatrix <C> <states>\n\n"
                    + "-populousSet <C> <minCount> <minIn> <minOut>"
                    );
            System.exit(0);
        }
        Arguments arg = new Arguments(args);

        if (arg.hasCommand("estimate"))
        {
            List<String> inputfiles = io.listFileNames(arg.getArgument("estimate"));
            List<IIntArray> dtrajs = intseq.loadIntSequences(inputfiles);

            int lag = 1;
            if (arg.hasCommand("lag"))
            {
                lag = arg.getIntArgument("lag");
            }
            
            IDoubleArray C = null;
            if (arg.hasCommand("sampleLag"))
            {
                C = msm.estimateCstepping(dtrajs, lag);
            }
            else
            {
                C = msm.estimateC(dtrajs, lag);
            }
            
            if (arg.hasCommand("countrev"))
            {
                C = alg.addWeightedToNew(0.5, C, 0.5, alg.transposeToNew(C));
            }

            if (arg.hasCommand("subset"))
            {
                IIntArray S = intseq.loadIntSequence(arg.getArgument("subset"));
                C = C.view(S.getArray(), S.getArray());
            }

            doubles.writeMatrixSparse(C, System.out);
        }
        if (arg.hasCommand("submatrix"))
        {
            IDoubleArray C = doublesNew.fromFile(arg.getArgument("submatrix", 0));
            IIntArray S = intseq.loadIntSequence(arg.getArgument("submatrix", 1));
            C = C.view(S.getArray(), S.getArray());
            doubles.writeMatrixSparse(C, System.out);
        }
        if (arg.hasCommand("populousSet"))
        {
            IDoubleArray C = doublesNew.fromFile(arg.getArgument("populousSet", 0));
            int n = C.rows();
            double minCount = arg.getIntArgument("populousSet",1);
            double minIn = arg.getIntArgument("populousSet",2);
            double minOut = arg.getIntArgument("populousSet",3);

            IDoubleArray counts = alg.rowSums(C);
            IDoubleArray ins = alg.columnSums(C);
            IDoubleArray outs = alg.rowSums(C);
            for (int i=0; i<n; i++)
            {
                ins.set(i,i, ins.get(i,i)-C.get(i,i));
                outs.set(i,i, outs.get(i,i)-C.get(i,i));
            }

            IIntArray iC = doubles.largeValueIndexes(counts, minCount);
            IIntArray iI = doubles.largeValueIndexes(ins, minIn);
            IIntArray iO = doubles.largeValueIndexes(outs, minOut);
            IIntArray I = ints.intersectionToNew(ints.intersectionToNew(iC, iI), iO);
            ints.print(I,"\n");
        }
    }
}
