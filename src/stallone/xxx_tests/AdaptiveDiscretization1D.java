/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_tests;

import java.io.IOException;
import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.datasequence.*;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.*;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;

/**
 *
 * TODO: - Mark centers in dtraj - For a given f region that should be resolved,
 * select from any transition paths between centers
 *
 * @author noe
 */
public class AdaptiveDiscretization1D
{

    public static IIntArray selectRandomIndexesWithin(IIntArray dtraj, IDoubleArray f, double fmin, double fmax, int nselect)
    {
        IIntList res = intsNew.list(0);
        for (int i = 0; i < dtraj.size(); i++)
        {
            double fi = f.get(dtraj.get(i));
            if (fi >= fmin && fi <= fmax)
            {
                res.append(i);
            }
        }
        IIntList sel = intsNew.listRandomIndexes(res.size(), nselect);
        return ints.subToNew(res, sel);
    }

    /*
     * public static int selectRandomIndex(IIntArray dtraj, int state) { int n =
     * ints.count(dtraj, state); int j = MathTools.randomInt(0, n); int k = -1;
     * for (int i = 0; i < dtraj.size(); i++) { if (dtraj.get(i) == state) {
     * k++; } if (k == j) { return i; } } return -1;
    }
     */
    public static int windowState(IIntArray dtraj, IDoubleArray f, double fmin, double fmax, int i)
    {
        if (f.get(dtraj.get(i)) < fmin)
        {
            return -1;
        }
        if (f.get(dtraj.get(i)) > fmax)
        {
            return 1;
        }
        return 0;
    }

    public static int[] nextTransitionPath(IIntArray dtraj, IDoubleArray f, double fmin, double fmax, int start)
    {
        int i = start;

        while (true)
        {
            // move to first in
            while (!(windowState(dtraj, f, fmin, fmax, i) != 0 && windowState(dtraj, f, fmin, fmax, i + 1) == 0))
            {
                i++;
                if (i == dtraj.size() - 1)
                {
                    return null;
                }
            }
            int windowStart = i + 1;

            // move to next out
            while (!(windowState(dtraj, f, fmin, fmax, i) == 0 && windowState(dtraj, f, fmin, fmax, i + 1) != 0))
            {
                i++;
                if (i == dtraj.size() - 1)
                {
                    return null;
                }
            }
            int windowEnd = i;

            if (windowState(dtraj, f, fmin, fmax, windowStart) != windowState(dtraj, f, fmin, fmax, windowEnd))
            {
                return new int[]
                        {
                            windowStart, windowEnd
                        };
            }
        }
    }

    public static IIntArray selectRandomIndexexOnPaths(IIntArray dtraj, IDoubleArray f, double fmin, double fmax, int n)
    {
        int i = 0;
        IIntList indexes = intsNew.list(0);
        int[] window = null;
        while ((window = nextTransitionPath(dtraj, f, fmin, fmax, i)) != null)
        {
            indexes.appendAll(intsNew.arrayRange(window[0], window[1] + 1));
        };

        IIntArray sel = intsNew.arrayRandomIndexes(indexes.size(), n);
        return ints.subToNew(indexes, sel);
    }

    /**
     * Assigns the trajectory to the current cluster centers, producing dtraj
     *
     * @return
     */
    public void assign()
    {
        IDataList centers = dataNew.createDatalist();
        for (int i = 0; i < centerIndexes.size(); i++)
        {
            centers.add(traj.get(centerIndexes.get(i)));
        }
        IDiscretization voronoiDiscretization = discNew.voronoiDiscretization(centers);
        dtraj = cluster.discretize(traj, voronoiDiscretization);
    }

    public void evBin()
    {
        // estimate
        IDoubleArray C = msm.estimateC(dtraj, tau);
        IDoubleArray T = msm.estimateTrev(C);
        IEigenvalueDecomposition evd = alg.evd(T);
        ev2 = evd.getRightEigenvector(1).viewReal();

        //System.out.println("Eval = " + evd.getEval().get(1));
        //System.out.println("EV 2 = " + evd.getRightEigenvector(1).viewReal());

        // bin EV's
        double min = doubles.min(ev2);
        double max = doubles.max(ev2);
        evGrid = doublesNew.arrayGrid(min, max, evGridSize);
        evGridCount = stat.histogram(ev2, evGrid);
    }

    public void printBinning()
    {
        System.out.println("binning:");
        for (int i = 0; i < evGrid.size(); i++)
        {
            System.out.println(evGrid.get(i) + "\t" + evGridCount.get(i));
        }
    }

    public void addNewCenters(IIntArray newCenterIndexes)
    {
        for (int i = 0; i < newCenterIndexes.size(); i++)
        {
            int c = newCenterIndexes.get(i);
            if (!ints.contains(centerIndexes, c))
            {
                centerIndexes.append(c);
            }
        }
    }

    public void addToExtremes()
    {
        // add to extremes until we have enough there.
        if (evGridCount.get(0) < nperbin)
        {
            IIntArray newcounts = selectRandomIndexesWithin(dtraj, ev2, Double.NEGATIVE_INFINITY, 0.5 * (evGrid.get(0) + evGrid.get(1)), nperbin - evGridCount.get(0));
            addNewCenters(newcounts);
        }

        int ilast = evGridCount.size() - 1;
        if (evGridCount.get(ilast) < nperbin)
        {
            IIntArray newcounts = selectRandomIndexesWithin(dtraj, ev2, 0.5 * (evGrid.get(ilast - 1) + evGrid.get(ilast)), Double.POSITIVE_INFINITY, nperbin - evGridCount.get(0));
            addNewCenters(newcounts);
        }
    }
    
    
    // parameters
    private int nclusters = 10;
    private int nperbin = 10;
    private int tau = 10;
    private int evGridSize = 10;
    // trajectory objects
    private IDataSequence traj;
    private IIntList centerIndexes = intsNew.list(0);
    private IIntArray dtraj;
    // ev objects
    private IDoubleArray ev2;
    private IDoubleArray evGrid;
    private IIntArray evGridCount;

    public static void main(String[] args) throws IOException
    {
        AdaptiveDiscretization1D ad = new AdaptiveDiscretization1D();

        // load trajectory
        ad.traj = dataNew.dataSequenceLoader(args[0]).load();

        // initial clustering
        ad.centerIndexes = intsNew.listRandomIndexes(ad.traj.size(), ad.nclusters);

        // assign
        ad.assign();

        // compute eigenvector and bin
        ad.evBin();
        ad.printBinning();

        while (ad.evGridCount.get(0) < ad.nperbin || ad.evGridCount.get(ad.evGridCount.size()-1) < ad.nperbin)
        {
            // add to extremes
            ad.addToExtremes();

            // assign
            ad.assign();

            // compute eigenvector and bin
            ad.evBin();
            ad.printBinning();
        }
        
        
    }
}
