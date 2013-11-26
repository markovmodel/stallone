/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import java.util.ArrayList;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.cluster.IClustering;

import stallone.api.datasequence.IDataSequence;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.api.ints.IntsPrimitive;
import stallone.datasequence.DataSequenceSubset;

/**
 *
 * TEST - split and merge structure with accept / reject option for every step
 * 
 * Data structure for representing a multi-level clustering of user-specified data 
 * based on user-specified clustering algorithm.
 * 
 * This class represents a multi-level clustering where data is split into clusters at the first level
 * and (some of these) clusters are further split in the lower levels. 
 * Clustering is always done by performing a Voronoi decomposition of the subset of clustered data.
 * As a result, the set of leaves of the splitting tree represent a full and nonoverlapping state space
 * decomposition. 
 * 
 * @author noe
 */
public class MultiClusteringSplitMerge
{
    private IDataSequence data;
    private IMetric metric;
    //private IClustering clusterMethodFull;
    private IClustering clusterMethodLeaves;
    
    // currently accepted clustering results
    private IIntArray micro2macro;
    private ArrayList<Leaf> leaves = new ArrayList();
    // new considered clustering resuts
    private IIntArray micro2macroNew;
    private ArrayList<Leaf> leavesNew = new ArrayList();
    
    /**
     * 
     * @param _data the data to be clustered
     * @param _clusterMethodFull a pre-configured instance of the clustering method to be used for the first cluster level
     * @param _clusterMethodLeaves a pre-configured instance of the clustering method to be used for the leaves
     */
    public MultiClusteringSplitMerge(IDataSequence _data, IClustering _clusterMethodFull, IClustering _clusterMethodLeaves)
    {
        // set input
        this.data = _data;
        //this.clusterMethodFull = _clusterMethodFull;        
        this.clusterMethodLeaves = _clusterMethodLeaves;        
        
        // first clustering
        _clusterMethodFull.setClusterInput(_data);
        _clusterMethodFull.perform();
        IDataSequence centers = _clusterMethodFull.getClusterCenters();
        // assignment 
        IDiscretization assignment = _clusterMethodFull.getClusterAssignment();
        micro2macro = cluster.discretize(data, assignment);
        // define leaves
        int nClusters = _clusterMethodFull.getNumberOfClusters();
        for (int i=0; i<nClusters; i++)
        {
            IDoubleArray leafCenter = centers.get(i);
            IIntArray leafIndexes = ints.findAll(micro2macro, i);
            leaves.add(new Leaf(leafIndexes));
        }
    }
    
    /**
     * 
     * @param _data the data to be clustered
     * @param _initialDiscretization initial discrete trajectory
     * @param _clusterMethodLeaves a pre-configured instance of the clustering method to be used for the leaves
     */
    public MultiClusteringSplitMerge(IDataSequence _data, IDataSequence _initcenters, IMetric _metric, IClustering _clusterMethodLeaves)
    {
        // set input
        this.data = _data;
        this.clusterMethodLeaves = _clusterMethodLeaves;  
        this.metric = _metric;
        // first clustering
        IDiscretization voronoiDiscretization = discNew.voronoiDiscretization(_initcenters, _metric);
        micro2macro = cluster.discretize(_data, voronoiDiscretization);
        int nInitialClusters = _initcenters.size();
        IIntList[] leafIndexes = new IIntList[nInitialClusters];
        for (int i=0; i<leafIndexes.length; i++)
            leafIndexes[i] = intsNew.list(0);
        for (int i=0; i<micro2macro.size(); i++)
        {
            int s = micro2macro.get(i);
            leafIndexes[s].append(i);
        }
        for (int i=0; i<leafIndexes.length; i++)
            leaves.add(new Leaf(leafIndexes[i]));        
    }
    
    
    /**
     * 
     * @param leafIndex the leaf to be split
     * @return true - splitting successful. false - the cluster method did not permit further splitting of this leaf. Nothing was done.
     */
    private boolean split(int leafIndex)
    {        
        int nClustersBeforeSplitting = leavesNew.size();
        // cluster leaf
        Leaf leaf = leavesNew.get(leafIndex);
        DataSequenceSubset subset = new DataSequenceSubset(data, leaf.indexes);
        this.clusterMethodLeaves.setClusterInput(subset);
        this.clusterMethodLeaves.perform();
        int nPieces = this.clusterMethodLeaves.getNumberOfClusters();        
        if (nPieces == 1)
            return false; // no splitting could be done, return unsuccessful.
        // reassignment table (new index -> global index)
        int[] reassign = new int[this.clusterMethodLeaves.getNumberOfClusters()];
        reassign[0] = leafIndex; // first new cluster replaces old cluster
        for (int i=1; i<reassign.length; i++)
            reassign[i] = nClustersBeforeSplitting+(i-1);

        //System.out.println("N clusters before splitting: "+nClustersBeforeSplitting);
        //System.out.println("New clusters: "+nPieces);
        //System.out.println("Reassignment table: "+IntArrays.toString(reassign));

        // assignment 
        IDiscretization assignment = this.clusterMethodLeaves.getClusterAssignment();
        IDataSequence centers = this.clusterMethodLeaves.getClusterCenters();
        IIntArray micro2macroLeaf = cluster.discretize(subset, assignment);
        // update global assignment
        for (int i=0; i<leaf.indexes.size(); i++)
        {
            int globalIndex = leaf.indexes.get(i);
            int newClusterIndex = reassign[micro2macroLeaf.get(i)];
            micro2macroNew.set(globalIndex, newClusterIndex);
        }
        // create new leaves
        IIntArray[] newLeaveIndexes = new IIntArray[nPieces];
        for (int i=0; i<nPieces; i++)
        {
            newLeaveIndexes[i] = ints.findAll(micro2macroLeaf, i);
            // change local to global index
            for (int j=0; j<newLeaveIndexes[i].size(); j++)
                newLeaveIndexes[i].set(j, leaf.indexes.get(newLeaveIndexes[i].get(j)));
        }        
        // add other leaves to end
        leavesNew.set(leafIndex, new Leaf(newLeaveIndexes[0]));
        for (int i=1; i<nPieces; i++)
        {
            leavesNew.add(new Leaf(newLeaveIndexes[i]));
        }        
        
        System.out.print(
                " splitted indexes: "+leafIndex+" -> ("+IntsPrimitive.util.toString(reassign,",")+")"+
                "\n  nstates: "+leaf.indexes.size()+" -> (");
        for (int i=0; i<newLeaveIndexes.length; i++)
            System.out.print(newLeaveIndexes[i].size()+",");
                System.out.println(")\n  now we have "+leavesNew.size()+" states");
        
        //System.out.println("Nr leaves after splitting: "+leaves.size());
        return true;
    }
    
    public boolean considerSplit(IIntArray leafIndexes)
    {
        // working copy
        micro2macroNew = micro2macro.copy();
        leavesNew = (ArrayList<Leaf>)leaves.clone();

        // start splitting
        boolean couldsplit = false;
        IIntArray I = ints.sortedIndexes(leafIndexes);
        for (int i=I.size()-1; i>=0; i--)
        {
            int splitIndex = leafIndexes.get(I.get(i));
            if (split(splitIndex))
                couldsplit = true;
        }
        return couldsplit;
    }
    
    private void merge(List<Leaf> leavesToMerge)
    {        
        // Create a merged leaf
        IIntList mergedIndexes = intsNew.list(0);
        for (Leaf l : leavesToMerge)
        {
            mergedIndexes.appendAll(l.indexes);
        }
        Leaf newLeaf = new Leaf(mergedIndexes);

        // remove old leaves and add new leaf
        leavesNew.removeAll(leavesToMerge);
        leavesNew.add(newLeaf);

        // refresh assignment
        for (int s=0; s<leavesNew.size(); s++)
        {
            IIntArray I = leavesNew.get(s).indexes;
            for (int i=0; i<I.size(); i++)
                micro2macroNew.set(I.get(i), s);
        }
    }
    
    public void considerMerge(List<IIntArray> mergeGroups)
    {
        // working copy
        micro2macroNew = micro2macro.copy();
        leavesNew = (ArrayList<Leaf>)leaves.clone();
        
        // collect leaf groups
        ArrayList allLeavesToMerge = new ArrayList();        
        for (IIntArray mergeGroup: mergeGroups)
        {
            List<Leaf> leavesToMerge = new ArrayList();
            for (int i=0; i<mergeGroup.size(); i++)
            {
                Leaf l = leavesNew.get(mergeGroup.get(i));
                leavesToMerge.add(l);
            }
            allLeavesToMerge.add(leavesToMerge);
        }
        
        for (int i=0; i<allLeavesToMerge.size(); i++)
        {
            merge((List<Leaf>)allLeavesToMerge.get(i));
        }
    }
    
    /**
     * 
     * @return the set of leaves of the clustering tree.
     */
    public ArrayList<IIntArray> getCurrentLeafIndexes()
    {
        ArrayList<IIntArray> res = new ArrayList();
        for (Leaf l : leaves)
            res.add(l.indexes);
        return res;
    }

    /**
     * 
     * @return the set of leaves of the clustering tree.
     */
    public ArrayList<IIntArray> getNewLeafIndexes()
    {
        ArrayList<IIntArray> res = new ArrayList();
        for (Leaf l : leavesNew)
            res.add(l.indexes);
        return res;
    }
    
    public IIntArray getCurrentDiscreteTrajectory()
    {
        return micro2macro;
    }

    public IIntArray getNewDiscreteTrajectory()
    {
        return micro2macroNew;
    }
    
    public void accept()
    {
        if (micro2macroNew == null)
            return; // nothing to do
        
        micro2macro = micro2macroNew;
        leaves = leavesNew;
    }
    
    
    class Leaf
    {
        public IIntArray indexes;
        
        public Leaf(IIntArray _indexes)
        {
            this.indexes = _indexes;
        }
    }
}

