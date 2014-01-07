package stallone.api.cluster;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IMetric;

public class TestKMeans
{
    IClustering algo;
    
    @Before
    public void setUp() throws IOException {
        String files = "/home/marscher/md_simulation_data/trajall_500K.xtc";
        IDataReader loader = API.dataNew.reader(files);
        IDataSequence data = loader.load();
        
        IMetric metric = API.clusterNew.metric(0, 0);
        
        this.algo = API.clusterNew.kmeans(data, metric, 10, 100);
    }
    @Test
    public void test()
    {
        algo.perform();
        System.out.println(algo.getClusterCenters());
    }

}
