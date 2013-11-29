/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import static stallone.api.API.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import stallone.api.datasequence.IDataReader;
import stallone.api.doubles.IDoubleArray;
import stallone.io.CachedAsciiFileReader;
import stallone.mc.tpt.PathwayDecomposition;
import stallone.util.Arguments;

/**
 *
 * @author noe
 */
public class MSM_Flux
{
    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        if (args.length == 0)
        {
            System.out.println("MSM_Flux -cg -i <flux> <sets> -oflux <file> -onetflux <file>");
            System.out.println("Coarse-grains TPT fluxes");
            System.out.println();
            System.out.println("MSM_Flux -pathways <flux> <q+> <A> <B>");
            System.out.println("Pathway decomposition");
            System.exit(0);
        }
        Arguments arg = new Arguments(args);
        
        if (arg.hasCommand("cg"))
        {
            IDoubleArray f = doublesNew.fromFile(arg.getArgument("i",0));
            int[][] I = io.readIntMatrix(arg.getArgument("i",1));
            IDoubleArray F = doublesNew.matrix(I.length, I.length);

            // sum up fluxes
            for (int I1=0; I1<I.length; I1++)
            {
                for (int I2=0; I2<I.length; I2++)
                {
                    for (int i=0; i<I[I1].length; i++)
                    {
                        for (int j=0; j<I[I2].length; j++)
                        {
                            F.set(I1,I2, F.get(I1,I2) + f.get(I[I1][i],I[I2][j]));
                        }
                    }
                }
            }
        
            // net flux
            IDoubleArray Fnet = doublesNew.matrix(I.length, I.length);
            for (int i=0; i<Fnet.rows(); i++)
            {
                Fnet.set(i,i,0);
                for (int j=i+1; j<Fnet.columns(); j++)
                {
                    if (F.get(i,j) > F.get(j,i))
                        Fnet.set(i,j, F.get(i,j) - F.get(j,i));
                    else
                        Fnet.set(j,i, F.get(j,i) - F.get(i,j));
                }
            }

            if (arg.hasCommand("oflux"))
                doubles.writeMatrixSparse(F, new PrintStream(arg.getArgument("oflux")));
            if (arg.hasCommand("onetflux"))
                doubles.writeMatrixSparse(Fnet, new PrintStream(arg.getArgument("onetflux")));
        }
        if(arg.hasCommand("pathways"))
        {
            IDoubleArray F = doublesNew.fromFile(arg.getArgument("pathways",0));
            double[] Q = io.readDoubleColumn(arg.getArgument("pathways",1),0);
            int[] A = str.toIntArray(arg.getArgument("pathways",2));
            int[] B = str.toIntArray(arg.getArgument("pathways",3));

            PathwayDecomposition decomp = new PathwayDecomposition(F, Q, A, B);
            List<int[]> allPaths = new ArrayList();
            List<Double> allFluxes = new ArrayList();
            int[] path = null;
            double totalFlux = 0;
            do
            {
                path = decomp.nextPathway();
                if (path != null)
                {
                    allPaths.add(path);
                    allFluxes.add(decomp.getCurrentFlux().doubleValue());
                    totalFlux += decomp.getCurrentFlux().doubleValue();
                }
            }
            while (path != null);
            
            System.out.println("path\tflux\tcum. flux\tcum. fraction");
            double cumulativeFlux = 0;
            for (int i=0; i<allPaths.size(); i++)
            {
                cumulativeFlux += allFluxes.get(i);
                
                System.out.print(intArrays.toString(allPaths.get(i)));
                System.out.print("\t");
                System.out.print(allFluxes.get(i));
                System.out.print("\t");
                System.out.print(cumulativeFlux);
                System.out.print("\t");
                System.out.print(cumulativeFlux/totalFlux);
                System.out.println();
            }
            
        }
    }
}
