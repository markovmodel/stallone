/*
 * To change this template, choose Tools | Templates
 * and writeHeader the template in the editor.
 */
package stallone.xxx_emma;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import stallone.api.cluster.Clustering;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.*;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.io.IO;
import stallone.api.strings.Strings;
import stallone.doubles.EuclideanDistance;
import stallone.doubles.MinimalRMSDistance3D;
import stallone.util.CommandLineParser;
import stallone.util.MemUtil;

import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;

/**
 *
 * @author trendelkamp, Frank Noe
 */
public class ClusterCmd //extends AbstractCmd 
{
    // primary input

    private String[] v_inputtraj;
    private String v_inputformat;
    private String v_algorithm;
    private String v_metric;
    private int v_clustercenters;
    private double v_dmin;
    private int v_subsample;
    private int v_max_interations;
    private String v_output;
    private boolean hasTimeColumn;

    // secondary input
    public String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput() +
                "\n"
                + "=======================================\n"
                + "mm_cluster"
                + "clusters the simulation (MD) data to microstate generators. These generators are structures or points in state space that shall subsequently be used to discretize the trajectories using a Voronoi (nearest-neighbor) parititon."
                + "\n"
                + "=======================================\n"
                + "Usage: "
                + "\n"
                + "Input selection:\n"
                + "\n"
                + " -i (<string:filename|filenamepattern>)\n"
                + "[-iformat [ xtc | dcd | ascii | ensembleascii | {auto} ]]\n"
                + "[-timecolumn]\n"
                + "\n"
                + "Cluster algorithm selection:\n"
                + "\n"
                + "[-subsample {int:stepwidth{1}}]\n"
                + "[  -kmeans <int:clustercenters> [<int:maxiterations>]\n"
                + " | -kcenters <int:clustercenters>\n"
                + " | -regularspatial <double:dmin>\n"
                + " | -regulartemporal <int:spacing>\n"
                + "]\n"
                + "[-metric [ minrmsd | euclidean ]]\n"
                + "\n"
                + "Output selection:\n"
                + "\n"
                + "-o (<string:filename_for_cluster_centers>)\n";
    }

    private boolean parseArguments(String[] args)
    {
        CommandLineParser parser = new CommandLineParser();

        parser.addStringArrayCommand("i", true);
        parser.addStringCommand("iformat", false, "auto", new String[]
                {
                    "auto", "dcd", "ascii", "xtc", "ensemble"
                });
        parser.addStringCommand("timecolumn", false);
        parser.addIntArgument("subsample", false, 1, 1, Integer.MAX_VALUE);
        parser.addStringCommand("metric", false, "euclidean", new String[]
                {
                    "minrmsd", "euclidean"
                });
        parser.addStringCommand("o", true);

        // cluster algos
        String[] clusterAlgos =
        {
            "kmeans", "kcenters", "regularspatial", "regulartemporal"
        };
        parser.requireExactlyOneOf(clusterAlgos);
        parser.addStringCommand("kmeans", false);
        parser.addIntArgument("kmeans", true);  // n clusters
        parser.addIntArgument("kmeans", false, 20, 1, Integer.MAX_VALUE); // number of iterations
        parser.addStringCommand("kcenters", false);
        parser.addIntArgument("kmeans", true);  // n clusters
        parser.addStringCommand("regularspatial", false);
        parser.addDoubleArgument("regularspatial", true);  // distance
        parser.addStringCommand("regulartemporal", false);
        parser.addIntArgument("regulartemporal", true);  // spacing

        v_inputtraj = parser.getStringArray("i");
        v_inputformat = parser.getString("iformat");
        hasTimeColumn = parser.hasCommand("timecolumn");
        v_subsample = parser.getInt("subsample");
        v_metric = parser.getString("metric");
        v_output = parser.getString("o");

        // set algorithm and check if input is meaningful
        if (parser.hasCommand("kmeans"))
        {
            v_algorithm = "kmeans";
            v_clustercenters = parser.getInt("kmeans",0);
            v_max_interations = parser.getInt("kmeans",1);
            // check if metric is meaningful
            if (parser.hasCommand("metric") && v_metric.equals("minrmsd"))
            {
                IO.util.error("Cannot use k-means with metric minrmsd. Use euclidean");
            }
        }
        else if (parser.hasCommand("kcenters"))
        {
            v_algorithm = "kcenters";
            v_clustercenters = parser.getInt("kcenters");
        }
        else if (parser.hasCommand("regularspatial"))
        {
            v_algorithm = "regularspatial";
            v_dmin = parser.getDouble("regularspatial");
        }
        else if (parser.hasCommand("regulartemporal"))
        {
            v_algorithm = "regulartemporal";
            if (parser.hasCommand("subsample"))
            {
                System.out.println("Warning: specifying '-subsample' with regular temporal clustering is ineffective. Will use spacing "+parser.getInt("regulartemporal")+" given as regulartemporal argument as time spacing");
            }
            v_subsample = parser.getInt("regulartemporal");
        }

        return true;
    }

    private void run()
    {
        //======================================================================
        //Prepare and check input and output
        //======================================================================

        // construct input loader
        List<String> trajectoryList = Strings.util.toList(v_inputtraj);
        IDataSequenceLoader loader = null;
        try
        {
            // create loader
            loader = DataSequence.create.dataSequenceLoader(trajectoryList);
            // scan in order to check file consistency, equal dimensionality, etc.
            loader.scan();

            if (loader.numberOfSequences() == 0)
            {
                IO.util.error("No trajectories found. Either path is incorrect, or no trajectories match the wildcard.");
            }

            // state which trajectories to use for input
            System.out.println("Using " + loader.numberOfSequences() + " trajectories for input:");
            System.out.println(" " + trajectoryList.get(0));
            System.out.println(" ... ");
            System.out.println(" " + trajectoryList.get(trajectoryList.size() - 1));
        } 
        catch (IOException e)
        {
            System.out.println("IO error when trying to read input trajectories: \n");
            e.printStackTrace();
            System.exit(-1);
        }

        // check output file
        if (!IO.util.canCreateFile(v_output))
        {
            IO.util.error("Cannot create output file " + v_output + ". Check if path exists and you have write permissions");
        }

        //======================================================================
        //Decide whether to cluster in memory or via disk access
        //======================================================================

        //Memory usage: Count total number of frames needed and get total memory.
        int nFramesUsed = (int) Math.ceil((double) loader.size() / (double) v_subsample);
        int dimension = loader.dimension();
        long memRequiredForAll = nFramesUsed * 8;
        long memAvailable = MemUtil.getMaxMemAvailable();

        //Get iterable over trajectory frames that respects stepwidth
        Iterable<IDoubleArray> frameIterable = DataSequence.create.interleavedDataIterable(loader, v_clustercenters);

        //Branching between memory sensitive (k_means, k-centers) and memory 
        //insensitive (reg.-spatial, reg.-time) clustering algorithms.
        boolean isMemorySensitive = false;
        List<String> memorySensitiveAlgorithms = new ArrayList<String>();
        memorySensitiveAlgorithms.add("kmeans");
        memorySensitiveAlgorithms.add("kcenters");
        if (memorySensitiveAlgorithms.contains(v_algorithm))
        {
            isMemorySensitive = true;
        }

        if (isMemorySensitive)
        {
            if (memRequiredForAll < memAvailable)
            {
                frameIterable = load(frameIterable, dimension);
            }
            else if (memRequiredForAll > memAvailable)
            {
                System.out.print("Careful! Not enough memory available!\n\n"
                        + "You are trying to perform clustering "
                        + "using a memory sensitive algorithm with"
                        + " insufficient available memory.\n");
                System.out.printf("%.1f MB of free memory for this JVM.\n",
                        (float) memAvailable / (float) 1048576);
                System.out.printf("%.1f MB of memory required for clustering.\n",
                        (float) memRequiredForAll / (float) 1048576);
            }
        }

        //======================================================================
        //Cluster
        //======================================================================

        IMetric metric = constructMetric(loader.dimension());
        IDataSequence clusterCenters = cluster(frameIterable, nFramesUsed, dimension, metric);

        //=====================================================================
        //Write clusterCenters to file using the specified output mode.
        //=====================================================================

        // Simply use extension of first file in trajectory file list to 
        // determine if output is ascii or dcd.
        String extension = IO.util.getExtension(trajectoryList.get(0));

        //Check if extension of the first trajectory file matches one of the binary extensions.
        boolean isascii = true;
        if (extension.equals("dcd") || extension.equals("xtc"))
        {
            isascii = false;
        }

        // Depending on the input trajectory format write cluster centers to ascii or to dcd files.
        try
        {
            DataSequence.util.writeData(clusterCenters, v_output);
        }
        catch(IOException e)
        {
            IO.util.error("Exception while trying to write output file "+v_output+"\n: e\nCheck pathname, disk space, write permissions etc.");
        }
    }

    /*
     * Method performs clustering in tow steps. Loading all trajectory frames
     * into an ArrayList<IVector> and passing this array list to the clustering
     * algorithm
     */
    private IDataSequence load(Iterable<IDoubleArray> trajectoryFrames, int dimension)
    {
        System.out.println("Copying trajectories to memory.");
        IDataList res = DataSequence.create.createDatalist();
        for (IDoubleArray v : trajectoryFrames)
        {
            res.add(v.copy());
        }
        return res;
    }

    private IMetric constructMetric(int dim)
    {

        if (v_metric.equals("minrmsd"))
        {
            return new MinimalRMSDistance3D(dim / 3);
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

    private IDataSequence cluster(Iterable<IDoubleArray> data, int size, int dimension, IMetric metric)
    {
        IClustering clustering = null;
        IDataSequence res = null;
        if (v_algorithm.equals("kcenters"))
        {
            System.out.println("Using Cluster method: k-Center.");
            clustering = Clustering.util.kcenter(data, size, metric, v_clustercenters);
            res = clustering.getClusterCenters();
        }
        else if (v_algorithm.equals("kmeans"))
        {
            System.out.println("Using Cluster method: k-Means.");
            clustering = Clustering.util.kmeans(data, size, metric, v_clustercenters, v_max_interations);
            res = clustering.getClusterCenters();
        }
        else if (v_algorithm.equals("regularspatial"))
        {
            System.out.println("Using Cluster method: regular spatial.");
            clustering = Clustering.util.regularSpatial(data, size, metric, v_dmin);
            res = clustering.getClusterCenters();
        }
        else if (v_algorithm.equals("regulartemporal"))
        {
            System.out.println("Using Cluster method: regular temporal.");
            // data is already readuced to cluster centers through subsampling. Just need to load the centers
            return load(data, dimension);
        }
        else
        {
            throw new RuntimeException("No clustering algorithm.");
        }
        return res;
    }



    public static void main(String[] args)
    {
    }
}
