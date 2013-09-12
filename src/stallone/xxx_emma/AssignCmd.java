/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_emma;

import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.discretization.*;
import stallone.api.doubles.*;
import stallone.api.intsequence.*;
import stallone.api.io.IO;

import stallone.doubles.EuclideanDistance;
import stallone.doubles.MinimalRMSDistance3D;

import stallone.util.CommandLineParser;
import stallone.util.StringTools;

/**
 *
 * @author Antonia Mey, Frank Noe
 */
public class AssignCmd
{
    private String[] v_inputtraj;
    private String v_inputformat;
    private String v_inputcFormat;
    private String v_inputcluster;
    private String v_outputfolder;
    private String v_metric;
    private boolean timeColumnOption;
    private int dimension;

    public boolean parseArguments(String[] args)
    {
        CommandLineParser parser = new CommandLineParser();
        parser.addStringArrayCommand("i", true);
        parser.addStringCommand("iformat", false, "auto", new String[]
                {
                    "auto", "dcd", "ascii", "xtc", "ensemble"
                });
        parser.addStringCommand("ic", true);
        parser.addStringCommand("icformat", false, "auto", new String[]
                {
                    "auto", "dcd", "ascii", "xtc", "ensemble"
                });
        parser.addStringCommand("metric", false, "euclidean", new String[]
                {
                    "minrmsd", "euclidean"
                });
        parser.addCommand("timecolumn", false);
        parser.addStringCommand("o", false, "./", null);

        if (!parser.parse(args))
            return false;
        
        v_inputtraj = parser.getStringArray("i");
        v_inputformat = parser.getString("iformat");
        v_inputcluster = parser.getString("ic");
        v_inputcFormat = parser.getString("icformat");
        v_metric = parser.getString("metric");
        timeColumnOption = parser.hasCommand("timecolumn");
        v_outputfolder = parser.getString("o");
        
        System.out.println("input traj: "+StringTools.toString(v_inputtraj));
        System.out.println("input format: "+v_inputformat);
        System.out.println("input cluster: "+v_inputcluster);
        System.out.println("cluster format: "+v_inputcFormat);
        System.out.println("metric: "+v_metric);
        System.out.println("timeColumn: "+timeColumnOption);
        System.out.println("out folder: "+v_outputfolder);
        System.exit(0);
        
        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + "mm_assign"
                + " assigns the simulation data (-i) to microstates (-ic) that have been previously generated/ or user supplied. The assignment is done such that each frame of the input data is assigned to the number of the nearest cluster center (Voronoi parition)."
                + "\n"
                + "=======================================\n"
                + "Usage: "
                + "\n"
                + "Mandatory input and output options: "
                + " -i (<string:filename|filenamepattern>)+\n"
                + " -ic (<string:filename of clustercenteres|filenamepattern>+)\n "
                + "[-o [<string:output_directory_for_discretized_trajectories>]]\n"
                + "\n"
                + "Selection of the distance metric (use same as for clustering): "
                + "[-m [ {euclidean} | minrmsd ]]"
                + "\n"
                + "Selection of the input format: "
                + "[-iformat [ xtc | dcd | ascii | ensembleascii | {auto} ]]\n"
                + "[-icformat [ xtc | dcd | ascii | ensembleascii | {auto} ]]\n"
                + "[-timecolumn]\n";
    }

    public boolean checkInput()
    {
        // gather filenames from search pattern v_inputtraj
        List<File> trajectoryFiles = IO.util.listFiles(v_inputtraj);

        if (trajectoryFiles.isEmpty())
        {
            System.out.println("No trajectories found. Either path is incorrect, or no trajectories match the wildcard.");
            System.exit(1);
        }
        
        // state which trajectories to use for input
        System.out.println("Using the following " + trajectoryFiles.size() + " trajectories for input:");
        List<String> fileNames = new ArrayList<String>();
        for (File file : trajectoryFiles)
        {
            System.out.println("  " + file.getPath());
            fileNames.add(file.getPath());
        } 
        
        // cluster input
        File clusterFile = new File (v_inputcluster);
        System.out.println("Cluster file input file is: " + v_inputcluster);
        if (!clusterFile.exists())
        {
            System.out.println("Cluster input file "+v_inputcluster+" does not exist! Aborting.");
            System.exit(1);
        }        
        
        // check output directory
        if (!(new File(v_outputfolder).isDirectory()))
        {
            System.out.println("The specified outputfolder '" + v_outputfolder + "' must be a directory");
            System.exit(-1);
        }
        
        System.out.println("Input parameters are valid.");
        
        return true;
    }
    
    public void run()
    {        
        // reading cluster center data from file
        System.out.println("Reading Cluster Centers ... ");
        IDataSequence clusterCenters = DataSequence.util.readDataSequence(v_inputcluster);
        this.dimension = clusterCenters.dimension();
        System.out.println(" Read "+clusterCenters.size()+" cluster centers of dimension "+this.dimension+" from file "+v_inputcluster);

        // construct Voronoi discretization
        IMetric metric = constructMetric();
        IDiscretization disc = Discretization.create.voronoiDiscretization(clusterCenters, metric);
        System.out.println("Voronoi discretization with metric: "+v_metric);

        //Generates a list of output files
        String[] outputFiles = getOutputMap(v_inputtraj, v_outputfolder);

        try
        {
            assignClusters(v_inputtraj, disc, outputFiles);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(AssignCmd.class.getName()).log(Level.SEVERE, null, ex);
        }


        System.out.println("Discrete clusters have been assigned and written to file.");

    }    

    private IMetric<?> constructMetric()
    {
        if (v_metric.equals("minrmsd"))
        {
            return new MinimalRMSDistance3D(dimension / 3);
        }
        else if (v_metric.equals("euclidean"))
        {
            return new EuclideanDistance();
        }
        else
        {
            throw new RuntimeException("No metric.");
        }
    }    

    private void assignClusters(String[] inputfiles, IDiscretization disc, String[] outputfiles)
            throws IOException
    {
        int count = 0;
        for (int i = 0; i < inputfiles.length; i++)
        {
            // input
            String inputfile = inputfiles[i];
            IDataReader loader = DataSequence.create.dataSequenceLoader(inputfile);
            if (loader.dimension() != dimension)
            {
                IO.util.error("The dimension of the input vector (" + loader.dimension() + ") and the cluster center " + dimension + " are not the same...Aborting");
            }

            // output
            IIntWriter writer = IntSequence.create.intSequenceWriter(outputfiles[i]);

            for (IDoubleArray v : loader)
            {
                if (count % 50000 == 0 && count > 0)
                {
                    System.out.println(" assignment step: " + count);
                }

                writer.add(disc.assign(v));

                count++;
            }
            writer.close();
        }
    }    
    
    private String[] getOutputMap(String[] input, String outputdir)
    {
        String[] out = new String[input.length];
        for (int i = 0; i < input.length; i++)
        {
            String base = IO.util.getBasename(input[i]);
            out[i] = outputdir + "/" + base + ".disctraj";
        }

        System.out.println("Discrete trajectories will be written to: ");
        System.out.println(out[0]);
        System.out.println(" ... ");
        System.out.println(out[out.length-1]);
        
        return out;
    }

    public static void main(String[] args)
    {
        AssignCmd cmd = new AssignCmd();

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
    }
}
