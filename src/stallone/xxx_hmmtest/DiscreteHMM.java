/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IExpectationMaximization;
import stallone.api.hmm.IHMM;
import stallone.api.hmm.IHMMParameters;
import stallone.api.hmm.ParameterEstimationException;
import stallone.doubles.DoubleIO;
import stallone.doubles.io.SparseDoubleArrayAsciiWriter;
import stallone.util.CommandLineParser;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class DiscreteHMM
{
    private List<String> inputFiles;
    private int nhiddenStates;
    private int nsteps = 10;
    private double maxIncrease = 0.1;
    private String outDir;
    
    public boolean parseArguments(String[] args)
    {
        CommandLineParser parser = new CommandLineParser();
        parser.addStringArrayCommand("i", true);

        parser.addCommand("conv", false);
        parser.addIntArgument("conv", true);
        parser.addDoubleArgument("conv", true);

        parser.addStringCommand("o", true);

        parser.addIntCommand("nstates", true);
        
        parser.parse(args);
        
        
        String[] ifiles = parser.getStringArray("i");
        inputFiles = new ArrayList();
        for (int i=0; i<ifiles.length; i++)
            inputFiles.add(ifiles[i]);
        
        nsteps = parser.getInt("conv", 0);
        nhiddenStates =parser.getInt("nstates");
        maxIncrease = parser.getDouble("conv", 1);
        outDir = parser.getString("o");
        
        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + "DiscreteHMM"
                + "\n"
                + "=======================================\n"
                + "Usage: "
                + "\n"
                + " -i <input-file>+\n"
                + "-o <output-directory>\n"
                + "\n"
                + " [-conv <nsteps> <max-increase>]\n"
                + "\n"
                ;
    }    
    
    public static void main(String[] args)
            throws IOException, ParameterEstimationException
    {
        DiscreteHMM cmd = new DiscreteHMM();

        // if no input, print usage String
        if (args.length == 0)
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }
        
        // read input
        IDataSequenceLoader loader = dataNew.dataSequenceLoader(cmd.inputFiles);
        List<IDataSequence> observations = loader.loadAll();
        int nObservableStates = observations.get(0).dimension();
        
        // initial parameters
        IHMMParameters par0 = hmmNew.parameters(cmd.nhiddenStates, true, true);
        IDoubleArray Test0 = doublesNew.matrix(cmd.nhiddenStates, cmd.nhiddenStates, 1.0/(double)cmd.nhiddenStates);
        par0.setTransitionMatrix(Test0);
        for (int i=0; i<cmd.nhiddenStates; i++)
        {
            IDoubleArray poutEst = doublesNew.arrayRandom(nObservableStates);
            alg.normalize(poutEst, 1);
            par0.setOutputParameters(i, poutEst);
        }
        
        // Estimate
        double[] uniformPrior = new double[nObservableStates];
        java.util.Arrays.fill(uniformPrior, 1.0/(double)uniformPrior.length);
        IExpectationMaximization EM = hmmNew.emDiscrete(observations, par0, uniformPrior);
        EM.setMaximumNumberOfStep(10);
        EM.setLikelihoodDecreaseTolerance(0.1);
        EM.run();
        
        // HMM
        IHMM hmmEst = EM.getHMM();
        IHMMParameters parEst = hmmEst.getParameters();
        // output
        io.writeString(cmd.outDir+"/likelihoods.out", doubleArrays.toString(EM.getLogLikelihoodHistory(),"\n"));
        // transition matrix
        SparseDoubleArrayAsciiWriter writer = new SparseDoubleArrayAsciiWriter(parEst.getTransitionMatrix(), cmd.outDir+"/T.dat");
        writer.perform();
        // output probabilities
        for (int i=0; i<parEst.getNStates(); i++)
            io.writeString(cmd.outDir+"/pout-"+i+".dat", doubles.toString(parEst.getOutputParameters(i),"\n"));
        
    }
}
