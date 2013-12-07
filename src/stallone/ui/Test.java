/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.algebra.IEigenvalueSolver;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataReader;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.mc.sampling.ITransitionMatrixSampler;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class Test
{
    public static void main(String[] args) 
            throws FileNotFoundException, IOException
    {        
        String infile = "/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/long_md/tmp_tics.dat";
        String outfile = "/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/long_md/tmp.dat";
        /*
        IDataReader reader = dataNew.dataSequenceLoader();
        reader.scan();
        int N = reader.size();
        int d = reader.dimension();
        System.out.println("Input shape = "+N+" x "+d);*/
        List<String> names = new ArrayList();
        names.add(infile);
        IDataInput loader = dataNew.dataSequenceLoader(names);
        loader.scan();
        Iterable<IDoubleArray> it = loader.getSingleDataLoader();
        
        IClustering clustering = clusterNew.createRegularSpatial(it, loader.size(), 1.0);
        clustering.perform();
        IDiscretization assign = clustering.getClusterAssignment();

        System.out.println("number of clusters: "+clustering.getNumberOfClusters());
        //clustercenters = self.clustering.getClusterCenters();
        //writer = API.dataNew.createASCIIDataWriter(self.file_clustercenters, 0, ' ', '\n')
        //writer.addAll(clustercenters)
        //writer.close()
        System.out.println("Scanning input again:");
        IDataReader loader2 = dataNew.dataSequenceLoader(infile);
        
        System.out.println("size = "+loader2.size());
        System.out.println("dimension = "+loader2.dimension());
        
        Iterator<IDoubleArray> it2 = loader2.iterator();
        while (it2.hasNext())
        {
            IDoubleArray x = it2.next();
            int i = assign.assign(x);
            System.out.println(i);
        }
        

    }
}
