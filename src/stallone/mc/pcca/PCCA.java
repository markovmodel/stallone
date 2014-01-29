package stallone.mc.pcca;

import static stallone.api.API.*;
import stallone.api.IAlgorithm;
import stallone.api.algebra.Algebra;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.cluster.IClustering;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.ints.PrimitiveIntArray;

/**
 * A java implementation of the Inner Simplex Algorithm (ISA) from Marcus Weber. For details refer to:<br>
 * <i>Marcus Weber: Improved Perron Cluster Analysis, ZIB-Report 03-04.<br>
 * Marcus Weber and Tobias Galliat: Charakterization of Transition States in Conformational Dynamics using Fuzzy States,
 * ZIB-Report 02-12.</i><br>
 * See <tt><A HREF="www.zib.de/bib/pub/index.en.html"><CODE>www.zib.de/bib/pub/index.en.html</CODE></A></tt>.
 *
 * <p>A typical application is the computation of meta stable sets of a Markov chain represented by a given reversible
 * transition matrix. This requires the following steps:<br>
 * 1) Compute a reversible transition matrix, say <b>P</b>.<br>
 * 2) Determine the number of meta stable sets, for instance via the size of the Perron Cluster, say <i>k</i>.<br>
 * 3) Compute the <i>k</i> eigenvectors belonging to the <i>k</i> eigenvalues closest to 1.<br>
 * 4) Use the eigenvectors as input data to the cluster algorithm.<br>
 * 5) With {@link #getClusters} you get an array containing the allocation of each indices to a cluster, which is a
 * number between 0,...,k-1.<br>
 * 6) Use {@link #getPermutationArray} to obtain a permutation array. With this array you can permute the state space of
 * <b>P</b>, such that states belonging to the same cluster are neighbours.</p>
 *
 * @author  meerbach@math.fu-berlin.de
 */
public final class PCCA implements IAlgorithm
{

    /**
     * The condition of the transformation matrix. The transformation matrix transforms a given dataset with
     * simplex-like structure to a dataset with standard simplex-like structure. Large condition indicates an ill
     * conditioned cluster problem.
     */
    //public double COND_TRANS;
    /**
     * An indikator for the deviation of the data-points from simplex structure. Is this indikator negative, than there
     * are data points outside the computed simplex, so this indikator should be close to zero.
     */
    public double INDIKATOR;
    private int[] CLUSTER;
    private int[] SORTARRAY;
    private int[] CLUSTER_SIZE;
    private IDoubleArray FUZZY;
    private IDoubleArray eigenvectors;

    /**
     * Creates a new instance of ClusterByIsa and computes a clustering of a data set with the Isa-algorithm. The
     * created instance will be immutable. The results will be:<br>
     * <i>Cluster allocation</i>: An integer array containing the allocation of the given data points to the clusters:
     * <code>CLUSTER[i]</code> is the cluster of data point <i>i</i>.<br>
     * <i>Fuzzy allocations</i>: A (number of data points times number of clusters) -matrix containing the fuzzy
     * allocation for each data point.<br>
     * <i>Simplex indikator</i>: An indikator for the deviation of the shape of the set of data-points from a simplex
     * structure.<br>
     * <i>Condition indikator</i>: Large condition indicates an ill conditioned cluster problem.<br>
     * Use the {@link #allData()} method to display all results and the {@link #getClusters()}, resp. {@link #getFuzzy}
     * method to achieve them. The simplex and the condition indikator are public fields.
     *
     * @param   dataSet  the data points in a <code>DoubleMatrix2D</code>. <code>dataSet.rows()</code> is the number of
     *                   data points, while <code>dataSet.columns()</code> is the number of cluster.
     *
     * @throws  IllegalArgumentException  if there are more clusters than data points.
     */
    public PCCA()
    {
    }

    public void setEigenvectors(IDoubleArray dataSet)
    {
        this.eigenvectors = dataSet;
    }

    public void perform()
    {

        if (eigenvectors == null)
        {
            throw new RuntimeException("No eigenvectors set. Aborting.");
        }

        /* initialisation */
        int noOfClusters = eigenvectors.columns();
        int noOfPoints = eigenvectors.rows();
        IDoubleArray fuzzyAllocation;
        IDoubleArray transMatrix;
        double condTrans;
        double indikator = 0;
        int[] clusterAllocation = new int[noOfPoints];
        int[] counter;

        /* Special cases: */
        /* a) less than two clusters. */
        if (noOfClusters < 2)
        {
            fuzzyAllocation = Doubles.create.matrix(noOfPoints, 1, 1);
            transMatrix = Doubles.create.matrix(1, 1, 1 / eigenvectors.get(0, 0));
            condTrans = 0;

            for (int i = 0; i < noOfPoints; i++)
            {
                clusterAllocation[i] = 0;
            }

            counter = new int[2];
            counter[0] = 0;
            counter[1] = noOfPoints;
        }
        /* b) more cluster than states. */
        else if (noOfClusters > noOfPoints)
        {
            throw new IllegalArgumentException("There are more clusters than points given!");
        }
        else if (noOfClusters == noOfPoints)
        {
            fuzzyAllocation = Doubles.create.array(noOfPoints,noOfPoints);
            for (int i=0; i<fuzzyAllocation.rows(); i++)
                fuzzyAllocation.set(i,i,1);
            transMatrix = Algebra.util.inverse(eigenvectors);

            // currently, we do not calculate the condition number because we lack the method
            //condTrans = A.cond(transMatrix);

            counter = new int[noOfPoints + 1];

            for (int i = 0; i < noOfPoints; i++)
            {
                clusterAllocation[i] = i;
                counter[i + 1] = 1;
            }
        }
        /* Start of the Isa-algorithm. */
        else
        {
            double skalar = 0;
            double maxDist = 0;
            double comp = 0;
            double entry = 0;
            int[] index = new int[noOfClusters];
            int[] indexAll = Ints.create.arrayRange(0, noOfClusters).getArray();
            IDoubleArray orthoSys = eigenvectors.copy();
            IDoubleArray dummy;

            /* Compute the two data-points with the largest distance
             *(quantified by the 2-norm)*/
            for (int i = 0; i < noOfPoints; i++)
            {
                for (int j = i + 1; j < noOfPoints; j++)
                {
                    double dij = Algebra.util.distance(eigenvectors.viewRow(i), eigenvectors.viewRow(j));

                    if (dij > maxDist)
                    {
                        maxDist = dij;
                        index[0] = i;
                        index[1] = j;
                    }
                }
            }

            /* compute the other representatives by modified gram-schmidt
             * (i.e. the data points with the
             *largest distance to them computed before).*/
            for (int i = 0; i < noOfPoints; i++)
            {
                for (int j=0; j<orthoSys.columns(); j++)
                    orthoSys.set(i, j, orthoSys.get(i,j)-eigenvectors.get(0, j));
            }

            // divide by norm of index 1.
            double d = Algebra.util.norm(orthoSys.viewRow(index[1]));
            Algebra.util.scale(1.0/d, orthoSys);

            // ??
            for (int i = 2; i < noOfClusters; i++)
            {
                maxDist = 0;
                dummy = orthoSys.viewRow(index[i - 1]).copy();

                for (int j = 0; j < noOfPoints; j++)
                {
                    skalar = Algebra.util.dot(dummy, orthoSys.viewRow(j));
                    for (int k = 0; k<dummy.size(); k++)
                    {
                        orthoSys.set(j, k, orthoSys.get(j,k) - (dummy.get(k) * skalar));
                    }
                    comp = Algebra.util.norm(orthoSys.viewRow(j));

                    if (comp > maxDist)
                    {
                        maxDist = comp;
                        index[i] = j;
                    }
                }

                double normi = Algebra.util.norm(orthoSys.viewRow(index[i]));
                Algebra.util.scale(1.0/normi, orthoSys);
            }

            /* Use the index-array with the representatives to compute the
             * transformation matrix, i.e., the matrix that maps the
             * representative to the edges of the standard simplex.*/
            transMatrix = Algebra.util.inverse(eigenvectors.view(index, indexAll));

            // currently cannot compute condition number
            //condTrans = A.cond(transMatrix);

            /* Transform the data set to a (hopefully) nearly standard simplex
             * structure, by mapping all data points with the computed
             * transformation matrix. */
            fuzzyAllocation = Doubles.create.matrix(Algebra.util.product(eigenvectors, transMatrix).getTable());

            /* Extract from soft (fuzzy) allocation the sharp allocation vektor.*/
            counter = new int[noOfClusters + 1];

            for (int i = 0; i < noOfPoints; i++)
            {
                comp = 0;

                for (int j = 0; j < noOfClusters; j++)
                {
                    entry = fuzzyAllocation.get(i, j);

                    if (entry < indikator)
                    {
                        indikator = entry;
                    }

                    if (entry > comp)
                    {
                        clusterAllocation[i] = j;
                        comp = entry;
                    }
                }

                counter[clusterAllocation[i] + 1]++;
            }
        } // end if-else

        /* Compute a sorting array and an array with the number of data points
         *in each cluster*/
        int[] counter2 = new int[noOfClusters];
        int[] sortarray = new int[noOfPoints];

        for (int i = 1; i < counter.length; i++)
        {
            counter[i] += counter[i - 1];
        }

        for (int i = 0; i < sortarray.length; i++)
        {
            sortarray[counter[clusterAllocation[i]] + counter2[clusterAllocation[i]]] = i;
            counter2[clusterAllocation[i]]++;
        }

        /* define fields */
        INDIKATOR = indikator;
        FUZZY = fuzzyAllocation;
        //COND_TRANS = condTrans;
        CLUSTER = clusterAllocation;
        SORTARRAY = sortarray;
        CLUSTER_SIZE = counter2;
    }

    /**
     * Returns an array with cluster allocations. <code>int[i]</code> is the cluster to which data point i was
     * allocated.
     */
    public IIntArray getClusters()
    {
        return(new PrimitiveIntArray(CLUSTER));
    }

    /**
     * Returns an array containing the number of data points in each cluster.
     */
    public IIntArray getClusterSize()
    {
        return(new PrimitiveIntArray(CLUSTER_SIZE));
    }

    public IDoubleArray getFuzzy()
    {
        return(FUZZY);
    }

    /**
     * Returns the permutation which is needed to arrange the data points according to their cluster.
     */
    public IIntArray getPermutationArray()
    {
        return(new PrimitiveIntArray(SORTARRAY));
    }
}
