/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

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
import stallone.api.hmm.IHMM;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.hmm.pmm.NinjaEstimator;
import stallone.hmm.pmm.NinjaUtilities;

/**
 *
 * @author noe
 */
public class NinjaEstimatorCmd
{
    // input data
    private List<String> inputFiles;
    private List<IIntArray> discreteTrajectories;
    // parameters
    private int nhidden;
    // initialization
    private IDoubleArray init_T = null;
    private IDoubleArray init_Chi = null;
    // option single-point estimate
    private boolean singlePointEstimate = false;
    private int tau = 1, timeshift = 1;
    // option multi-start estimate
    private boolean multiStartEstimate = false;
    private double[] metastabilities;
    // option multi-timescales estimate
    private boolean hmmTimescalesEstimate = false;
    private int taumin = 1, taumax = 1000;
    private double taumult = 1.2;
    private int maxAverageWindow = 10;
    // hmm convergence options
    private double hmmDecTol = 0;
    private int hmmNIter = 1000;
    // option direct
    private boolean direct = false;
    // output directory
    private String outdir;

    
    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringArrayCommand("i", true);
        // mandatory parameters
        parser.addIntCommand("nhidden", true);
        // optional initialization
        parser.addCommand("init", false);
        parser.addStringArgument("init", true); // T
        parser.addStringArgument("init", true); // Chi
        // option estimate
        parser.addCommand("estimate", false);
        parser.addIntArgument("estimate", true); // tau
        parser.addIntArgument("estimate", true); // average window
        // option hmm timescales
        parser.addCommand("hmmtimescales", false);
        parser.addIntArgument("hmmtimescales", true); // tau-min
        parser.addIntArgument("hmmtimescales", true); // tau-max
        parser.addDoubleArgument("hmmtimescales", true); // tau-mult
        parser.addIntArgument("hmmtimescales", true); // max-avg-window
        // hmm convergence options
        parser.addCommand("hmmconv",false);
        parser.addDoubleArgument("hmmconv", true, -0.1, Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY); // dectol
        parser.addIntArgument("hmmconv", true, 1000, 0, Integer.MAX_VALUE); // niter
        // option
        parser.addCommand("direct", false);
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
        
        if (parser.hasCommand("init"))
        {
            String fileT = parser.getString("init",0);
            init_T = doublesNew.fromFile(fileT);
            String fileChi = parser.getString("init",1);
            init_Chi = doublesNew.fromFile(fileChi);
        }
        
        // read command option
        if (parser.hasCommand("estimate"))
        {
            singlePointEstimate = true;
            tau = parser.getInt("estimate",0);
            timeshift = parser.getInt("estimate",1);
        }
        else if (parser.hasCommand("estimatemult"))
        {
            multiStartEstimate = true;
            tau = parser.getInt("estimatemult",0);
            timeshift = parser.getInt("estimatemult",1);
            metastabilities = parser.getDoubleArray("metastabilities");
        }
        else if (parser.hasCommand("hmmtimescales"))
        {
            hmmTimescalesEstimate = true;
            taumin = parser.getInt("hmmtimescales",0);
            taumax = parser.getInt("hmmtimescales",1);
            taumult = parser.getDouble("hmmtimescales",2);
            timeshift = parser.getInt("hmmtimescales",3);
        }
        
        direct = parser.hasCommand("direct");
        
        if (parser.hasCommand("hmmconv"))
        {
            hmmDecTol = parser.getDouble("hmmconv",0);
            hmmNIter = parser.getInt("hmmconv",1);
        }
        
        outdir = parser.getString("o");

        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " NinjaEstimator"
                + "\n"
                + "=======================================\n"
                + "Usage: " + "\n"
                + "\n"
                + "Mandatory input and output options: " + "\n"
                + " -i <discrete trajectory>+\n"
                + " -nhidden <number of hidden states>" + "\n"
                + "\n"
                + " -o <out-dir>" + "\n"
                + "\n"
                + "Any of: " + "\n"
                + " -estimate <lag time> <average-window>" + "\n"
                + "\n"
                + " [-init <T-init> <chi-init>]"
                + "\n"
                + " -hmmtimescales <min-lag> <max-lag> <lag-mult> <timeshift>" + "\n"
                + " [-direct]" + "\n"
                + "  enforce direct estimate at each lagtime rather than using the previous result to initialize the next lagtime\n"
                + " [-hmmconv <decrease-tolerance> <niter>]" + "\n"
                + "  hmm convergence options\n"
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

        NinjaEstimatorCmd cmd = new NinjaEstimatorCmd();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        if (cmd.singlePointEstimate)
        {
            IHMM pmm = hmm.pmm(cmd.discreteTrajectories, cmd.nhidden, cmd.tau, cmd.timeshift, cmd.hmmNIter, cmd.hmmDecTol, null, null);
            
            IDoubleArray hmmTC = pmm.getTransitionMatrix();
            io.writeString(cmd.outdir+"/hmmTc.dat", doubles.toString(hmmTC,"\t","\n"));
            IDoubleArray hmmChi = pmm.getOutputParameters();
            io.writeString(cmd.outdir+"/hmmChi.dat", doubles.toString(hmmChi,"\t","\n"));
        }
        else if (cmd.hmmTimescalesEstimate)
        {
            IIntList lagtimes = intsNew.list(0);
            lagtimes.append(cmd.taumin);
            for (double tau = cmd.taumin; tau <= cmd.taumax; tau *= cmd.taumult)
            {
                int lag = (int)tau;
                if (lag != lagtimes.get(lagtimes.size()-1))
                    lagtimes.append(lag);
            }

            PrintStream itsout = new PrintStream(cmd.outdir+"/hmm-its.dat");
            
            IDoubleArray lastTC = cmd.init_T;
            IDoubleArray lastChi = cmd.init_Chi;
            
            for (int i=0; i<lagtimes.size(); i++)
            {
                //set lag and timeshift
                int lag = lagtimes.get(i);
                System.out.println("\ntau = "+lag+"\n");

                // in direct mode, erase the results from last lag. Otherwise use as initialization.
                if (cmd.direct)
                {
                    lastTC = null;
                    lastChi = null;
                }

                System.out.println("Hidden state: "+cmd.nhidden);
                IHMM pmm = hmm.pmm(cmd.discreteTrajectories, cmd.nhidden, lag, cmd.timeshift, cmd.hmmNIter, cmd.hmmDecTol, lastTC, lastChi);
                
                // remember estimation results and use them as a next initializer.
                lastTC = pmm.getTransitionMatrix();
                lastChi = pmm.getOutputParameters();
                //double[] logL = cmd.ninja.getHMMLikelihoodHistory();
                double lastLogL = pmm.getLogLikelihood();

                // output timescales
                IDoubleArray hmmTimescales = msm.timescales(lastTC, lag);
                itsout.println(lag+"\t"+lastLogL+"\t"+doubles.toString(hmmTimescales,""," "));
                
                // output hidden matrix
                PrintStream TCout = new PrintStream(cmd.outdir+"/hmm-TC-lag"+lag+".dat");
                TCout.print(doubles.toString(lastTC,"\t","\n"));
                TCout.close();
                
                // output hidden matrix
                PrintStream Chiout = new PrintStream(cmd.outdir+"/hmm-Chi-lag"+lag+".dat");
                Chiout.print(doubles.toString(lastChi,"\t","\n"));
                Chiout.close();
            }
            
            itsout.close();
        }
    }
}
