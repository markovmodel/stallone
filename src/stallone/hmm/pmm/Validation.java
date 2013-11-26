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
import static stallone.api.API.*;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.util.CommandLineParser;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class Validation
{
    // input data
    private List<String> inputFiles;
    private List<IIntArray> data;
    private int dataMaxTime=0;
    private int nObservableStates=0;
    // input model
    private IDoubleArray T;
    private IDoubleArray pout;
    private int lagModel;
    // output
    private String outdir;
    // flags
    private boolean computeAutocorrelations = true;
    private boolean computeTimescales = false;
    
    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringCommand("iT", true);
        parser.addStringCommand("ipout", true);
        parser.addStringArrayCommand("idata", true);
        parser.addIntCommand("lag", true);

        // output
        parser.addStringCommand("o", true);
        
        // switch
        parser.addCommand("autocorrelations", false);
        parser.addCommand("timescales", false);

        if (!parser.parse(args))
            return false;

        
        String[] ifiles = parser.getStringArray("idata");
        inputFiles = new ArrayList();
        for (int i=0; i<ifiles.length; i++)
            inputFiles.add(ifiles[i]);
        
        IDataSequenceLoader loader = dataNew.dataSequenceLoader(inputFiles);
        data = intseq.loadIntSequences(inputFiles);
        for (int i=0; i<data.size(); i++)
        {
            dataMaxTime = Math.max(dataMaxTime, data.get(i).size());
            nObservableStates = Math.max(nObservableStates, ints.max(data.get(i))+1);
        }
        
        T = doublesNew.fromFile(parser.getString("iT"));
        pout = doublesNew.fromFile(parser.getString("ipout"));
        lagModel = parser.getInt("lag");
        
        outdir = parser.getString("o");
        
        if (parser.hasCommand("autocorrelations"))
        {
            computeAutocorrelations = true;
            computeTimescales = false;
        }

        if (parser.hasCommand("timescales"))
        {
            computeAutocorrelations = false;
            computeTimescales = true;
        }
        
        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " Validation"
                + "\n"
                + "=======================================\n"
                + "Usage: "
                + "\n"
                + "Mandatory input and output options: "
                + " -iT <T-macro> +\n"
                + " -ipout <output-matrix> +\n"
                + " -idata  <discrete trajectory>+\n"
                + " -lag <lag of model>" + "\n"
                + "[-o [<output-directory>]]\n"
                + "\n"
                + "Mode selection (either of): "
                + "[-autocorrelations]" + "\n"
                + "[-timescales]" + "\n"
                + "\n"
        ;
    }
    
    
    public static void main(String[] args)
            throws FileNotFoundException, IOException
    {
        Validation cmd = new Validation();

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

        // input correct. Let's play...
        
        
        IDoubleArray poutT = alg.transposeToNew(cmd.pout);
        
        String prefix = "validate-corr-";
        int nout = cmd.nObservableStates;
        if (cmd.computeTimescales)
        {
            nout = cmd.T.rows()-1;
            prefix = "validate-its-";
        }
        PrintStream[] out = new PrintStream[nout];
        for (int i=0; i<nout; i++)
        {
            out[i] = new PrintStream(cmd.outdir+"/"+prefix+""+(i+1)+".out");
            out[i].println("# data\tmodel");
        }

        // determine maximum lag
        int maxlag = cmd.dataMaxTime / (2 * cmd.lagModel);        
        // iterate lagtimes
        for (int tauModel=1; tauModel<=maxlag; tauModel++)
        {
            int tauData = cmd.lagModel * tauModel;
            // Data - countable properties
            // compute data count matrix
            IDoubleArray Cdata = msm.estimateC(cmd.data, tauData);
            // compute data transition matrix
            IDoubleArray Tdata = msm.estimateT(Cdata);
            
            // Model transition matrix
            IDoubleArray Tn = alg.power(cmd.T, tauModel);
            // Model transition matrix
            IDoubleArray pimodel = msm.stationaryDistribution(Tn);
            IDoubleArray corrn = alg.product(doublesNew.diag(pimodel), Tn);
            // compute data correlation matrix
            IDoubleArray corrmodel = alg.product(alg.product(poutT, corrn), cmd.pout);
            
            if (cmd.computeAutocorrelations)
            {
                // compute data stationary probability
                IDoubleArray pidata = msm.stationaryDistribution(Tdata);
                // Model correlation matrix
                IDoubleArray corrdata = alg.product(doublesNew.diag(pidata), Tdata);
                
                for (int i=0; i<nout; i++)
                    out[i].println(corrdata.get(i,i)+"\t"+corrmodel.get(i,i));
            }
            else if (cmd.computeTimescales)
            {
                IDoubleArray timescalesData = msm.timescales(Tdata, tauData);
                
                IDoubleArray TmodelApparent = msm.estimateT(corrmodel);                
                IDoubleArray timescalesModel = msm.timescales(TmodelApparent, tauData);
                for (int i=0; i<nout; i++)
                    out[i].println(timescalesData.get(i)+"\t"+timescalesModel.get(i));
            }
        }
        
        for (int i=0; i<nout; i++)
            out[i].close();
    }
}
