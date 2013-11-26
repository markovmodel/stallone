/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import static stallone.api.API.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import stallone.api.API;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.doubles.IDoubleArray;
import stallone.util.CommandLineParser;

/**
 *
 * @author noe
 */
public class ExpandEigenvectorsCmd
{
    private String inDir, outDir;
    
    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringCommand("i", true);
        // input
        parser.addStringCommand("o", true);

        if (!parser.parse(args))
        {
            return false;
        }

        inDir = parser.getString("i");
        outDir = parser.getString("o");

        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " ExpandEigenvectorsCmd"
                + "\n"
                + "=======================================\n"
                + "Usage: " + "\n"
                + "\n"
                + "Mandatory input and output options: " + "\n"
                + " -i <input-directory>+\n"
                + "\n"
                + " -o <out-dir>" + "\n"
                ;
        
    }    
    
    public static void main(String[] args) 
            throws FileNotFoundException, IOException
    {
        // if no input, print usage String
        if (args.length == 0)
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        ExpandEigenvectorsCmd cmd = new ExpandEigenvectorsCmd();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }
        
        // read lag times
        IDoubleArray lagFile = doublesNew.fromFile(cmd.inDir+"/hmm-its.dat");
        int[] lagtimes = intArrays.from(lagFile.getColumn(0));

        // read matrices and Chi
        for (int tau: lagtimes)
        {
            IDoubleArray TC = doublesNew.fromFile(cmd.inDir+"/hmm-TC-lag"+tau+".dat");            
            IDoubleArray Pi = doublesNew.diag(msm.stationaryDistribution(TC));
            IDoubleArray Chi = doublesNew.fromFile(cmd.inDir+"/hmm-Chi-lag"+tau+".dat");            

            // diagonalize TC
            IEigenvalueDecomposition evd = alg.evd(TC);
            IDoubleArray R = evd.R().viewReal();
            IDoubleArray L = alg.inverse(R);

            // pi
            IDoubleArray Lbig = alg.product(L, alg.transposeToNew(Chi));
            IDoubleArray pibig = Lbig.viewRow(0);
            alg.normalize(pibig, 1);
            IDoubleArray PibigInv = doublesNew.diag(pibig.size(), 1);
            for (int i=0; i<pibig.size(); i++)
                PibigInv.set(i,i,1.0/pibig.get(i));
            
            // Rbig
            IDoubleArray Rbig = alg.product(alg.product(PibigInv, Chi), alg.product(Pi, R));
            

            // output
            io.writeString(cmd.outDir+"/hmm-pibig-lag"+tau+".dat", doubles.toString(pibig,"\n","\n"));
            io.writeString(cmd.outDir+"/hmm-Rbig-lag"+tau+".dat", doubles.toString(Rbig,"\t","\n"));
        }
        
    }
}
