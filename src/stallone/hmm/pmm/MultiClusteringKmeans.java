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
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.util.CommandLineParser;

/**
 *
 * @author noe
 */
public class MultiClusteringKmeans
{
    private List<String> inputFiles;
    private List<IDataSequence> data;
    private int nclusters, nsplit;
    private String outPrefix;
    
    private MultiClustering mc;
        
    public MultiClusteringKmeans()
    {
    }
    
    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringArrayCommand("i", true);
        parser.addIntCommand("ncluster", true);
        parser.addIntCommand("nsplit", true);
        parser.addStringCommand("o", true);

        if (!parser.parse(args))
            return false;

        
        String[] ifiles = parser.getStringArray("i");
        inputFiles = new ArrayList();
        for (int i=0; i<ifiles.length; i++)
            inputFiles.add(ifiles[i]);
        
        IDataSequenceLoader loader = dataNew.dataSequenceLoader(inputFiles);
        data = loader.loadAll();

        nclusters = parser.getInt("ncluster");
        nsplit = parser.getInt("nsplit");
        
        outPrefix = parser.getString("o");
        
        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " MultiClusteringKmeans"
                + "\n"
                + "=======================================\n"
                + "Usage: "
                + "\n"
                + "Mandatory input and output options: "
                + " -i  <discrete trajectory>+\n"
                + " -ncluster <number of initial clusters>" + "\n"
                + " -nsplit <number of new clusters per split>" + "\n"
                + "\n"
                + " -o <out-prefix>" + "\n"
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

        MultiClusteringKmeans cmd = new MultiClusteringKmeans();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        IClustering clustering1 = clusterNew.createKmeans(cmd.nclusters, 10);
        IClustering clustering2 = clusterNew.createKmeans(cmd.nsplit, 10);

        // split only on first trajectory:
        cmd.mc = new MultiClustering(cmd.data.get(0), clustering1, clustering2);
        cmd.mc.split(0);
        cmd.mc.split(0);
        cmd.mc.split(0);
        cmd.mc.split(0);
        cmd.mc.split(0);
        cmd.mc.split(0);

        // output
        ArrayList<IIntArray> leaves = cmd.mc.getLeafIndexes();
        PrintStream[] out = new PrintStream[leaves.size()];
        for (int i=0; i<out.length; i++)
        {
            out[i] = new PrintStream(cmd.outPrefix+"_cluster-"+(i+1)+".dat");
            IIntArray leaf = leaves.get(i);
            System.out.println("leaf size = "+leaf.size());
            for (int j=0; j<leaf.size(); j++)
            {
                IDoubleArray data = cmd.data.get(0).get(leaf.get(j));
                out[i].println(data.get(0));
            }
            out[i].close();
        }
        
    }
}
