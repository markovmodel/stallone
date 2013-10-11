/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import stallone.api.datasequence.DataSequence;

/**
 *
 * @author noe
 */
public class DensityBasedClusteringSimpleN extends DensityBasedClusteringSimple
{
    // parameters
    private int N; // nclusters

    public DensityBasedClusteringSimpleN(int _N)
    {
        super();
        this.N = _N;
    }

    @Override
    public void perform()
    {
        // fix minpts
        super.minpts = (int) Math.max(1, datasequence.size() / (N*100));

        double dmin = 0;
        double dmax = DataSequence.util.rmax(datasequence);
        
        System.out.println("Determined data radius: "+dmax);

        // iterative search for the right eps
        for (int i=0; i<20; i++)
        {
            double d = 0.5*(dmin + dmax);
            
            super.eps = d;

            System.out.println("Running density based clustering with dist = "+super.eps+" and minpts = "+super.minpts);
            
            super.perform();

            int nfound = super.getNumberOfClusters();

            System.out.println("Found "+nfound+" clusters");
            
            if (nfound == N)
                return;
            
            if (nfound < N)
                dmax = d;
            if (nfound > N)
                dmin = d;
        }
        
        // did not find N clusters
        throw new RuntimeException("Failed to find "+N+" clusters. Aborting!");
    }

}
