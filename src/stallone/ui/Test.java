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
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.datasequence.IDataReader;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.mc.ITransitionMatrixSampler;
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
        /*IDataReader reader = dataNew.createASCIIDataReader("/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/new_model_test/model/tics/long2.dat");
        reader.scan();
        for (int i=0; i<100; i++)
        {
            IDoubleArray x = reader.get(i);           
            System.out.println(x.size());
        }
        System.exit(0);*/
        
        
        //String outfile = "/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/long_md/tmp.dat";
        /*
        IDataReader reader = dataNew.dataSequenceLoader();
        reader.scan();
        int N = reader.size();
        int d = reader.dimension();
        System.out.println("Input shape = "+N+" x "+d);*/
        List<String> names = new ArrayList();
        names.add("/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/new_model_test/model/tics/long1.dat");
        names.add("/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/new_model_test/model/tics/long2.dat");
        names.add("/Users/noe/data/open_projects/adaptive_sampling_local/data/TrypsinBenzamidine/new_model_test/model/tics/long3.dat");
        IDataSequenceLoader loader = dataNew.multiSequenceLoader(names);
        loader.scan();
        
        IClustering clustering = clusterNew.regspace(loader, 0.5);
        clustering.perform();
        
        /*
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
        
*/
    }
}
