/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.complex.IComplexArray;

import stallone.api.doubles.IDoubleArray;
import stallone.util.Arguments;

/**
 *
 * @author noe
 */
public class Tools_Matrix
{

    public static void main(String[] args) 
            throws FileNotFoundException, IOException
    {
        if (args.length == 0)
        {
            System.out.println("Tools_Matrix \n"
                    + "-sparse2dense <M>\n"
                    + "-dense2sparse <M>\n"
                    + "-eigenvalues <M> [-norm | -complex]\n"
                    + "-eigenvectors <M> <neig> [-left]\n"
                    + "");
            System.exit(0);
        }
        Arguments arg = new Arguments(args);

        if (arg.hasCommand("sparse2dense"))
        {
            IDoubleArray T = doublesNew.fromFile(arg.getArgument("sparse2dense"));
            doubles.writeMatrixDense(T, System.out);
        }
        if (arg.hasCommand("dense2sparse"))
        {
            IDoubleArray T = doublesNew.fromFile(arg.getArgument("dense2sparse"));
            doubles.writeMatrixSparse(T, System.out);
        }
        if (arg.hasCommand("eigenvalues"))
        {
            IDoubleArray T = doublesNew.fromFile(arg.getArgument("eigenvalues"));
            IEigenvalueDecomposition evd = alg.evd(T);

            if (arg.hasCommand("norm"))
            {
                doubles.print(evd.getEvalNorm(), "\n");
            }
            else if (arg.hasCommand("complex"))
            {
                IComplexArray eval = evd.getEval();
                for (int i=0; i<eval.size(); i++)
                    System.out.println(eval.getRe(i)+" + "+eval.getIm(i)+"i");
            }
            else
            {
                doubles.print(evd.getEvalRe(), "\n");
            }
        }
        if (arg.hasCommand("eigenvectors"))
        {
            IDoubleArray T = doublesNew.fromFile(arg.getArgument("eigenvectors"));
            if (arg.hasCommand("left"))
                alg.transpose(T);
            int n = arg.getIntArgument("eigenvectors", 1);

            IDoubleArray EV = alg.evd(T).R();
            EV = EV.viewBlock(0, EV.rows(), 0, n);
            doubles.print(EV, " ", "\n");
        }

    }
}
