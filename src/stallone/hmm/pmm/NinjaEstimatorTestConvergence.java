/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataInput;
import stallone.api.hmm.ParameterEstimationException;
import stallone.util.CommandLineParser;

import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;

/**
 *
 * @author noe
 */
public class NinjaEstimatorTestConvergence
{
    // input data
    private List<String> inputFiles;
    private List<IIntArray> discreteTrajectories;
    // parameters
    private int nhidden;
    // option single-point estimate
    private int tau = 1, timeshift = 1;
    // n hmm conv
    private int[] nHMMConv = new int[]{1,2,3,4,5,7,9,11,13,15,18,21,24,27,30,35,40,45,50,60,70,80,90,100,120,140,160,180,200,250,300,350,400,450,500,600,700,800,900,1000};
    // output directory
    private String outfile;

    
    private NinjaEstimator ninja;
    
    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringArrayCommand("i", true);
        // mandatory parameters
        parser.addIntCommand("nhidden", true);
        // number of conv steps
        parser.addIntArrayCommand("nhmmconv", false);
        // option estimate
        parser.addCommand("estimate", false);
        parser.addIntArgument("estimate", true); // tau
        parser.addIntArgument("estimate", true); // average window
        // output
        parser.addStringCommand("o", true);

        if (!parser.parse(args))
        {
            return false;
        }

        // read input and construct NINJA
        String[] ifiles = parser.getStringArray("i");
        inputFiles = new ArrayList();
        for (int i = 0; i < ifiles.length; i++)
        {
            inputFiles.add(ifiles[i]);
        }
        discreteTrajectories = intseqNew.intSequenceLoader(inputFiles).loadAll();

        // mandatory input
        nhidden = parser.getInt("nhidden");
        
        if (parser.hasCommand("nhmmconv"))
            nHMMConv = parser.getIntArray("nhmmconv");
        
        // read command option
        if (parser.hasCommand("estimate"))
        {
            tau = parser.getInt("estimate",0);
            timeshift = parser.getInt("estimate",1);
        }
        
        outfile = parser.getString("o");

        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " NinjaEstimatorTestConvergence"
                + "\n"
                + "=======================================\n"
                + "Usage: " + "\n"
                + "\n"
                + "Mandatory input and output options: " + "\n"
                + " -i <discrete trajectory>+\n"
                + " -nhidden <number of hidden states>" + "\n"
                + " -nhmmconv <sequence of convergence steps>" + "\n"
                + "\n"
                + " -o <out-file>" + "\n"
                + "\n"
                + " -estimate <tau> <timeshift>" + "\n"
                + "\n"
                ;
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException, ParameterEstimationException
    {
        // if no input, print usage String
        if (args.length == 0)
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        NinjaEstimatorTestConvergence cmd = new NinjaEstimatorTestConvergence();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        // initialize NINJA
        cmd.ninja = new NinjaEstimator(cmd.discreteTrajectories);
        cmd.ninja.setNHiddenStates(cmd.nhidden);
        cmd.ninja.setTau(cmd.tau);
        cmd.ninja.setTimeshift(cmd.timeshift);
        cmd.ninja.setHMMLikelihoodMaxIncrease(10);
        
        PrintStream out = new PrintStream(cmd.outfile);
        
        for (int i=0; i<cmd.nHMMConv.length; i++)
        {
            int nconv = cmd.nHMMConv[i];
            cmd.ninja.setNIterHMMMax(nconv);
            cmd.ninja.estimate();

            IDoubleArray hmmTimescales = cmd.ninja.getHMMTimescales();
            double[] hmmLikelihoodHistory = cmd.ninja.getHMMLikelihoodHistory();
            double lastLikelihood = hmmLikelihoodHistory[hmmLikelihoodHistory.length-1];
            
            out.println(nconv+"\t"+lastLikelihood+"\t"+doubles.toString(hmmTimescales));
        }
    }
}
