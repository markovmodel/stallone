/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import stallone.api.cluster.Clustering;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataList;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;

/**
 *
 * @author noe
 */
public class MatrixDensityClustering
{

    public static void main(String[] args)
            throws FileNotFoundException, IOException
    {
        /*if (args.length == 0)
        {
            System.out.println("USage: MatrixDensityClustering <matrix-file> <dmin> <minpts>");
            System.exit(0);
        }

        String matrixfile = args[0];
        double dmin = Strings.util.toDouble(args[1]);
        int minpts = Strings.util.toInt(args[2]);
        */
        String matrixfile = "/Users/noe/data/temp/francesca_matrix_clustering/matrix_rmsd1.dat";
        double dmin = .7;
        int minpts = 2;
        
        // read density matrix
        IDoubleArray distanceMatrix = Doubles.create.fromFile(matrixfile);

        // construct metric which measures matrix elements.
        IMetric<?> metric = new MatrixMetric(distanceMatrix);

        // construct fake data sequence
        IDataList dataSequence = DataSequence.create.createDatalist();
        for (int i=0; i<distanceMatrix.rows(); i++)
            dataSequence.add(Doubles.create.arrayFrom(i));
        
        IClustering clustering = Clustering.util.densityBased(dataSequence, metric, dmin, minpts);
        
        System.out.println("Number of clusters: ");
        int nclusters = clustering.getNumberOfClusters();
        System.out.println(nclusters);
        
        IIntArray clusterIndexes = clustering.getClusterIndexes();
        //System.out.println(clustering.getClusterIndexes());
        
        System.out.println("Assignment: ");
        for (int i=0; i<nclusters; i++)
        {
            IIntArray members = Ints.util.findAll(clusterIndexes, i);
            System.out.print(members.size()+"\t");
            Ints.util.print(members, ",", "");
            System.out.println();
        }
    }
}

class MatrixMetric implements IMetric
{

    private IDoubleArray distanceMatrix;

    public MatrixMetric(IDoubleArray _distanceMatrix)
    {
        distanceMatrix = _distanceMatrix;
    }

    @Override
    public double distance(IDoubleArray x, IDoubleArray y)
    {
        int i = (int) x.get(0);
        int j = (int) y.get(0);
        return distanceMatrix.get(i, j);
    }
}