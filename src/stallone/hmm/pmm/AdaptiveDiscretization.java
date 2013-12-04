package stallone.hmm.pmm;

import static stallone.api.API.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataWriter;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.hmm.IExpectationMaximization;
import stallone.api.hmm.IHMM;
import stallone.api.hmm.IHMMParameters;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.doubles.EuclideanDistance;
import stallone.coordinates.MinimalRMSDistance3D;
import stallone.util.CommandLineParser;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * Adaptive discretization via NINJA!
 *
 * Input: - N data Sequences
 * \mathbf{X}=\{\mathbf{x}_{t}^{(1)},....,\mathbf{x}_{t}^{(N)}\} - Cluster
 * Algorithm - ncluster - initial number of clusters - Metric - lag time \tau -
 * number of hidden states m
 *
 * 1. Cluster \mathbf{X} into n=ncluster clusters. 2. Estimate
 * \mathbf{T}(\tau)\in\mathbb{R}^{n\times n} from the discrete trajectory 3. Use
 * PCCA to decompose \mathbf{T}(\tau) into
 * \boldsymbol{\chi}\in\mathbb{R}^{m\times n} and
 * \mathbf{T}^{(m)}(\tau)\in\mathbb{R}^{m\times m} . 4. Use HMM to optimize
 * \boldsymbol{\chi}'\leftarrow\boldsymbol{\chi} and
 * \mathbf{T'}^{(m)}\leftarrow\mathbf{T}^{(m)} . 5. Estimate and output m
 * dominant timescales from \mathbf{T'}^{(m)} 6. If all states are fine, EXIT.
 * Else label states to be split. 7. Split states and recompute discrete
 * trajectory 8. Go to 2.
 *
 *
 * @author noe
 */
public class AdaptiveDiscretization
{
    // input data
    private List<String> inputFiles;
    private List<IDataSequence> data;
    // basic settings
    private int ninitclusters = 0;
    private IDataSequence initcenters;
    private IMetric metric;
    // parameters
    private int nsplit;
    private int tau = 1, timeshift = 1;
    private int nhidden;
    private int maxrefinementsteps = 100;
    private double requestedError = 0.01;
    // output directory
    private String outdir;
    //
    // clustering
    private MultiClusteringSplitMerge mc;
    // NINJA estimation
    NinjaEstimator ninja;
    // current MSM estimation data
    IDoubleArray msmC, msmT, msmpi, msmPi, msmCorr;
    IDoubleArray msmChi, msmTC, msmTimescales;
    // current HMM estimates
    IDoubleArray hmmChi, hmmTC, hmmpiC, hmmTimescales;
    // error estimates
    IDoubleArray errors;
    double etot;

    public AdaptiveDiscretization()
    {
    }


    private IDoubleArray getStateMixing(IDoubleArray chi, IDoubleArray piObs, IDoubleArray piHidden)
    {
        IDoubleArray mix = chi.copy();

        for (int i = 0; i < chi.rows(); i++)
        {
            for (int j = 0; j < chi.columns(); j++)
            {
                mix.set(i, j, chi.get(i, j) * piHidden.get(j) / piObs.get(i));
            }
        }

        // renormalize
        alg.normalizeRows(mix, 1);

        return mix;
    }

    private IIntArray statesWithOverlap(IDoubleArray mix, double requestedQuality)
    {
        IIntList res = intsNew.list(0);

        for (int i = 0; i < mix.rows(); i++)
        {
            IDoubleArray pfrom = mix.viewRow(i);
            if (doubles.max(pfrom) < requestedQuality)
            {
                res.append(i);
            }
        }

        return res;
    }

    private IIntArray selectStatesToSplit(IDoubleArray errors, double etot)
    {
        // select bad states.
        IIntList splitStates = intsNew.list(0);
        
        // error level reached? then return empty list
        if (etot <= requestedError)
            return splitStates;

        // else select the worst fraction
        int nselect = Math.max(1, (int)Math.sqrt(errors.size()));        
        IIntArray sortedIndexes = doubles.sortedIndexes(errors);
        return (ints.subToNew(sortedIndexes, sortedIndexes.size()-nselect, sortedIndexes.size()));
    }

    private void printStateQualities(IDoubleArray mix, IDoubleArray pi)
    {
        System.out.println("State qualities");
        for (int i = 0; i < mix.rows(); i++)
        {
            IDoubleArray pfrom = mix.viewRow(i);
            double q = doubles.max(pfrom);
            System.out.println(i + "\t" + q + "\t" + pi.get(i));
        }
    }
    
    private void estimate()
            throws ParameterEstimationException
    {
            //  get discrete trajectory      
            IIntArray dtraj = mc.getCurrentDiscreteTrajectory();
            List<IIntArray> dtrajs = new ArrayList();
            dtrajs.add(dtraj);
            
            // construct estimator
        ninja = new NinjaEstimator(dtrajs);
        ninja.setNHiddenStates(nhidden);
        ninja.setTau(tau);
        ninja.setTimeshift(timeshift);
        ninja.estimate();

            msmT = ninja.getMSMTransitionMatrix();
            msmpi = ninja.getMSMStationaryDistribution();
            msmPi = doublesNew.diag(msmpi);
            msmCorr = alg.product(msmPi, msmT);
            msmTimescales = ninja.getMSMTimescales();
            hmmTC = ninja.getHMMTransitionMatrix();
            hmmpiC = ninja.getHMMStationaryDistribution();
            hmmChi = ninja.getHMMOutputProbabilities();
            hmmTimescales = ninja.getHMMTimescales();
    }
    
    private void estimateErrorByPureness()
    {
        IDoubleArray chinorm = hmmChi.copy(); 
        alg.normalizeRows(chinorm,1); 
        
        errors = doublesNew.array(chinorm.rows());

        for (int i=0; i<chinorm.rows(); i++)
        {
            IDoubleArray row = chinorm.viewRow(i);
            int imax = doubles.maxIndex(row);
            row.set(imax, row.get(imax)-1);
            errors.set(i, msmPi.get(i)*alg.norm(row));
        }
        
        etot = doubles.sum(errors);
    }
    
    private void estimateErrorByDetectability()
    {
            // try next...
            errors = doublesNew.array(hmmChi.rows());
            double qtot = 0;
            for (int i = 0; i < errors.size(); i++)
            {
                double q1 = 0, q2 = 0;
                for (int j = 0; j < hmmChi.columns(); j++)
                {
                    q1 += Math.pow(hmmpiC.get(j) * hmmChi.get(i, j), 2);
                    q2 += hmmpiC.get(j) * hmmChi.get(i, j);
                }
                qtot += q1 / q2;
                errors.set(i, msmpi.get(i) * (1 - q1 / (q2 * msmpi.get(i))));
                //System.out.println(i + "\t" + (q1 / q2) + "\t" + msmpi.get(i) + "\t" + (q1 / (q2 * msmpi.get(i))) + "\t" + errors.get(i));
            }
            etot = 1.0-qtot;
            //System.out.println("n = " + msmpi.size() + "\t etot = " + (etot));            
    }
    
    private void estimateError()
    {
            // ERROR estimate
            // which are the bad states?
            /*
             * IDoubleArray XPX = alg.product(alg.product(chiopt,
             * doublesNew.diag(piCopt)), alg.transposeToNew(chiopt));
             * IDoubleArray E = alg.subtract(Pi,XPX); System.out.println("XPX
             * matrix: \n"+XPX); System.out.println("pi obs: \n"+pi);
             * System.out.println("Error matrix: \n"+E);
             * System.out.println("Error \n"+alg.trace(E));
            System.exit(0);
             */
            estimateErrorByDetectability();
            
            for (int i = 0; i < errors.size(); i++)
            {
                System.out.println(i + "\t" + msmpi.get(i) + "\t" + errors.get(i));
            }
            System.out.println("n = " + msmpi.size() + "\t etot = " + (etot));            

    }
    
    /**
     * Conducts a split move
     * @return 
     */
    private boolean split()
    {
            // select bad states.
            IIntArray badStates = selectStatesToSplit(errors, etot);

            //IDoubleArray mix = cmd.getStateMixing(chiopt, pi, piCopt);
            //IIntArray badStates = cmd.statesWithOverlap(mix, cmd.requestedStateQuality);

            //System.out.println("piObs: "+pi);
            //System.out.println("piHidden: "+piCopt);
            //System.out.println("mixing:\n"+mix);
            //System.out.println();

            //cmd.printStateQualities(mix, pi);
            //System.out.println();

            if (badStates.size() == 0)
            {
                System.out.println("No overlapping states left. DONE!");
                return false;
            }
            else
            {
                // otherwise we continue
                System.out.println("splitting states: " + badStates);
                System.out.println();

                boolean couldsplit = mc.considerSplit(badStates);
                if (!couldsplit)
                {
                    System.out.println("A split was requested but couldn not be executed. DONE!");
                    return false;
                }
                
                // execute split
                mc.accept();
                return true;
            }            
    }
    
    private ArrayList<IIntArray> mergeGroupsByPurity()
    {

        // select best states as merge candidates.
        
        /*
        IIntList goodStates = intsNew.list(0);
        int nselect = errors.size()/2;   
        IIntArray sortedIndexes = doubles.sortedIndexes(errors);
        IIntArray mergeCandidates = ints.subToNew(sortedIndexes, 0, nselect);
        */
        IIntArray mergeCandidates = intsNew.arrayRange(hmmChi.rows());
        
        // compute coming-from-probabilities
        /*
        IDoubleArray pComeFrom = hmmChi.copy();
        for (int i=0; i<pComeFrom.rows(); i++)
            for (int I=0; I<pComeFrom.columns(); I++)
                pComeFrom.set(i,I, hmmChi.get(i,I) * hmmpiC.get(I) / msmpi.get(i));
                */
        IDoubleArray pComeFrom = hmmChi.copy();
        alg.normalizeRows(pComeFrom, 1);

            
        System.out.println(" COME-FROM array:");
        doubles.print(pComeFrom,"\t","\n");
        
        // select optimal groups
        ArrayList<IIntArray> groups = new ArrayList();
        for (int i=0; i<nhidden; i++)
            groups.add(intsNew.list(0));
        
        for (int i=0; i<mergeCandidates.size(); i++)
        {
            int s = mergeCandidates.get(i);
            int g = doubleArrays.maxIndex(pComeFrom.getRow(s));
            if (pComeFrom.get(i,g) > 0.99)
                ((IIntList)groups.get(g)).append(s);
        }

        // remove empty groups
        for (int i=groups.size()-1; i>=0; i--)
        {
            if (groups.get(i).size() <= 1)
            {
                System.out.println("removing merge group "+i+" with size "+groups.get(i).size());
                groups.remove(i);
            }
            else
            {
                // output
                for (int j=0; j<groups.size(); j++)
                {
                    IIntArray group = groups.get(j);
                    System.out.println("- merge group "+j+": "+ints.toString(group,"",","));
                    IDoubleArray groupChi = pComeFrom.view(group.getArray(), intsNew.arrayRange(0, nhidden).getArray());
                    doubles.print(groupChi,"\t","\n");
                    System.out.println();
                }        
            }        
        }
        return groups;
    }
    
    private ArrayList<IIntArray> mergeGroupsBySimilarity()
    {
        double maxdist = 0.01;
        IIntArray mergeCandidates = intsNew.arrayRange(hmmChi.rows());
        
        // compute coming-from-probabilities
        IDoubleArray pComeFrom = hmmChi.copy();
        alg.normalizeRows(pComeFrom, 1);
            
        System.out.println(" COME-FROM array:");
        doubles.print(pComeFrom,"\t","\n");
        
        ArrayList<IIntList> groups = new ArrayList();
        groups.add(intsNew.listFrom(mergeCandidates.get(0)));
        
        for (int i=1; i<mergeCandidates.size(); i++)
        {
            int s = mergeCandidates.get(i);
            IDoubleArray scf = pComeFrom.viewRow(s);
            
            double[] distances = new double[groups.size()];
            for (int j=0; j<distances.length; j++)
            {
                IDoubleArray ocf = pComeFrom.viewRow(groups.get(j).get(0));                                
                distances[j] = alg.norm(alg.subtract(scf, ocf));
                
                System.out.println(i+" "+j+":");
                System.out.println(scf);
                System.out.println(ocf);
                System.out.println(distances[j]);
                
            }

            // test if s is very similar to an existing merge group
            if(doubleArrays.min(distances) < maxdist)
            {
                groups.get(doubleArrays.minIndex(distances)).append(s);
            }
            else // new group
            {
                groups.add(intsNew.listFrom(s));
            }
        }
        
        // remove empty groups
        for (int i=groups.size()-1; i>=0; i--)
        {
            if (groups.get(i).size() <= 1)
            {
                System.out.println("removing merge group "+i+" with size "+groups.get(i).size());
                groups.remove(i);
            }
            else
            {
                // output
                for (int j=0; j<groups.size(); j++)
                {
                    IIntArray group = groups.get(j);
                    System.out.println("- merge group "+j+": "+ints.toString(group,"",","));
                    IDoubleArray groupChi = pComeFrom.view(group.getArray(), intsNew.arrayRange(0, nhidden).getArray());
                    doubles.print(groupChi,"\t","\n");
                    System.out.println();
                }        
            }        
        }
        
        
        ArrayList<IIntArray> res = new ArrayList();
        res.addAll(groups);        
        
        return res;
    }
    
    private void merge()
    {
        System.out.println("\nMERGE step");

        ArrayList<IIntArray> groups = mergeGroupsBySimilarity();//mergeGroupsByPurity();
        
        if (groups.isEmpty())
        {
            System.out.println("MERGE: Nothing to do!\n");
            return;
        }
        else
        {
            mc.considerMerge(groups);
            mc.accept();
        }
    }

    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringArrayCommand("i", true);
        // initial discretization
        parser.addIntCommand("ninitcluster", false);
        parser.addStringCommand("initcenters", false);
        parser.addStringCommand("metric", false, "euclidean", new String[]
                {
                    "euclidean", "minrmsd"
                });
        // parameters
        parser.addIntCommand("nsplit", true);
        parser.addCommand("tau", true);
        parser.addIntArgument("tau", true); // tau
        parser.addIntArgument("tau", true); // average window
        parser.addIntCommand("nhidden", true);
        parser.addDoubleCommand("requestederror", false);
        parser.addIntCommand("maxrefinementsteps", false);
        // output
        parser.addStringCommand("o", true);

        if (!parser.parse(args))
        {
            return false;
        }


        String[] ifiles = parser.getStringArray("i");
        inputFiles = new ArrayList();
        for (int i = 0; i < ifiles.length; i++)
        {
            inputFiles.add(ifiles[i]);
        }

        System.out.println("reading input data ... ");
        IDataInput loader = dataNew.dataSequenceLoader(inputFiles);
        data = loader.loadAll();
        System.out.println(" done. size: " + data.get(0).size() + " x " + data.get(0).dimension());


        // data assignment metric
        String metricstring = parser.getString("metric");
        if (metricstring.equalsIgnoreCase("euclidean"))
        {
            metric = new EuclideanDistance();
        }
        if (metricstring.equalsIgnoreCase("minrmsd"))
        {
            metric = new MinimalRMSDistance3D(data.get(0).dimension() / 3);
        }

        // initial discretization
        if (parser.hasCommand("ninitcluster"))
        {
            ninitclusters = parser.getInt("ninitcluster");
        }
        else
        {
            String initcenterfile = parser.getString("initcenters");
            initcenters = dataNew.dataSequenceLoader(initcenterfile).load();
        }

        // parameters
        nsplit = parser.getInt("nsplit");
        tau = parser.getInt("tau", 0);
        timeshift = parser.getInt("tau", 1);
        nhidden = parser.getInt("nhidden");
        requestedError = parser.getDouble("requestederror");
        maxrefinementsteps = parser.getInt("maxrefinementsteps");

        outdir = parser.getString("o");
        System.out.println("read all input, continuing");

        return true;
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " AdaptiveClustering"
                + "\n"
                + "=======================================\n"
                + "Usage: " + "\n"
                + "\n"
                + "Mandatory input and output options: " + "\n"
                + " -i  <trajectory>+\n"
                + " -nsplit <number of new clusters per split>" + "\n"
                + " -tau <lag time> <timeshift>" + "\n"
                + " -nhidden <number of hidden states>" + "\n"
                + "\n"
                + " -o <out-dir>" + "\n"
                + "\n"
                + "Any of: " + "\n"
                + " -ninitcluster <number of initial clusters>" + "\n"
                + " -initcenters <initial centers>" + "\n"
                + " [-metric <minrmsd|euclidean>]" + "\n"
                + "\n"
                + "Optional: " + "\n"
                + " [-requestederror <error-threshold, default = 0.01>]" + "\n"
                + " [-maxrefinementsteps <number of maximum refinement steps>]" + "\n"
                + "\n";
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

        AdaptiveDiscretization cmd = new AdaptiveDiscretization();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        // create Multi-Level-Clustering 
        if (cmd.ninitclusters > 0)
        {
            // k-means initial clustering and random splitting
            IClustering clustering1 = clusterNew.createKmeans(cmd.ninitclusters, 10);
            clustering1.setMetric(cmd.metric);
            IClustering clustering2 = clusterNew.createRandom(cmd.nsplit);
            //IClustering clustering2 = clusterNew.createKcenter(cmd.nsplit);
            clustering2.setMetric(cmd.metric);
            // construct multiclustering
            //cmd.mc = new MultiClustering(cmd.data.get(0), clustering1, clustering2);
            cmd.mc = new MultiClusteringSplitMerge(cmd.data.get(0), clustering1, clustering2);
        }
        else
        {
            IClustering clustering2 = clusterNew.createRandomCompact(cmd.nsplit, 10);
            //IClustering clustering2 = clusterNew.createRandom(cmd.nsplit);
            //IClustering clustering2 = clusterNew.createKcenter(cmd.nsplit);
            clustering2.setMetric(cmd.metric);
            cmd.mc = new MultiClusteringSplitMerge(cmd.data.get(0), cmd.initcenters, cmd.metric, clustering2);
        }

        // fixed initial clustering
        /*
         * IDataList userDefinedCenters = dataNew.createDatalist();
         * userDefinedCenters.add(doublesNew.arrayFrom(10,15));
         * userDefinedCenters.add(doublesNew.arrayFrom(15,15));
         * userDefinedCenters.add(doublesNew.arrayFrom(20,15)); IClustering
         * clustering1 = clusterNew.createFixed(userDefinedCenters);
         *
         * //IClustering clustering1 = clusterNew.createKmeans(cmd.nclusters,
         * 10); IClustering clustering2 = clusterNew.createRandom(cmd.nsplit);
         */
        
        // initial NINJA estimation
        cmd.estimate();
        cmd.estimateError();

        
        for (int n=0; n<cmd.maxrefinementsteps && cmd.etot>cmd.requestedError; n++)
        {
            // split
            boolean couldSplit = cmd.split();

            // NINJA estimation
            cmd.estimate();
            cmd.estimateError();

            // merge
            cmd.merge();
            
            // NINJA estimation
            cmd.estimate();
            cmd.estimateError();
            
            if (n == cmd.maxrefinementsteps-1)
            {
                System.out.println("Number of iterations expired. DONE!");
            }
            if (cmd.etot<=cmd.requestedError)
            {
                System.out.println("Prescribed error level of "+cmd.requestedError+" reached. DONE!");
            }
        }


        // OUTPUT
        // write discrete trajectory
        IIntArray dtrajFinal = cmd.mc.getCurrentDiscreteTrajectory();
        intseq.writeIntSequence(dtrajFinal, cmd.outdir + "/dtraj.dat");
        //
        // write cluster centers
        /*ArrayList<IDoubleArray> centers = cmd.mc.getLeafCenters();
        String centersOutFile = cmd.outdir + "/centers." + io.getExtension(cmd.inputFiles.get(0));
        IDataWriter centerWriter = dataNew.createDataWriter(centersOutFile, centers.size(), centers.get(0).size());
        centerWriter.addAll(centers);
        centerWriter.close();*/
    }
}
