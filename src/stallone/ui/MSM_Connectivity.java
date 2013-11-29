/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

import stallone.util.Arguments;

/**
 *
 * @author noe
 */
public class MSM_Connectivity
{
    public static void main(String[] args) 
            throws FileNotFoundException, IOException
    {
        if (args.length == 0)
        {
            System.out.println("MSM_Connectivity -iC <count-matrix> [-oGiant <file>]");
            System.out.println();
            System.out.println("Calculates the connectivity (strong components) of the specified count matrix. -oGiant writes out the giant component");
            System.exit(0);
        }

        Arguments arg = new Arguments(args);

        IDoubleArray C = doublesNew.fromFile(arg.getArgument("iC"));
        List<IIntArray> components = graph.connectedComponents(C);
        int[] I = intArrays.sortedIndexes(intseq.lengths(components));

        System.out.println("Strong Components: "+I.length);
        System.out.println("Lengths: "+intArrays.toString(I,", "));
        System.out.println("members: ");
        for (int i=0; i<I.length; i++)
        {
            IIntArray c = components.get(I[i]);
            System.out.println(ints.toString(c)+" ");
        }
        
        if (arg.hasCommand("oGiant"))
        {
            io.writeString(arg.getArgument("oGiant"), ints.toString(components.get(I[0]),"\n"));
        }
    }
}
