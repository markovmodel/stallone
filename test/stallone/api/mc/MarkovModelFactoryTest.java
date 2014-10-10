/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import static stallone.api.API.*;

import org.junit.*;
import static org.junit.Assert.*;
import stallone.api.doubles.IDoubleArray;
import stallone.mc.sampling.*;

/**
 *
 * @author noe
 */
public class MarkovModelFactoryTest
{
    
    public MarkovModelFactoryTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown()
    {
    }

    /**
     * Test of createPosteriorCountsNeighbor method, of class MarkovModelFactory.
     */
    @Test
    public void testCreatePosteriorCountsNeighbor()
    {
    }

    /**
     * Test of metropolisMC method, of class MarkovModelFactory.
     */
    @Test
    public void testMetropolisMC()
    {
    }

    /**
     * Test of createCountMatrixEstimatorSliding method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCountMatrixEstimatorSliding_Iterable_int()
    {
    }

    /**
     * Test of createCountMatrixEstimatorSliding method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCountMatrixEstimatorSliding_IIntArray_int()
    {
    }

    /**
     * Test of createCountMatrixEstimatorStepping method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCountMatrixEstimatorStepping_Iterable_int()
    {
    }

    /**
     * Test of createCountMatrixEstimatorStepping method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCountMatrixEstimatorStepping_IIntArray_int()
    {
    }

    /**
     * Test of createTransitionMatrixEstimatorNonrev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixEstimatorNonrev()
    {
    }

    /**
     * Test of createTransitionMatrixEstimatorRev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixEstimatorRev_0args()
    {
    }

    /**
     * Test of createTransitionMatrixEstimatorRev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixEstimatorRev_IDoubleArray()
    {
    }

    
    private void testSampler2x2(ITransitionMatrixSampler sampler, IDoubleArray C, int nsample, double errtol)
    {
        double c1 = doubles.sumRow(C,0);
        double c2 = doubles.sumRow(C,1);
        int n = C.rows();

        IDoubleArray samplesT12 = doublesNew.array(nsample);
        IDoubleArray samplesT21 = doublesNew.array(nsample);
        for (int i=0; i<nsample; i++)
        {
            IDoubleArray T = sampler.sample(100);
            samplesT12.set(i, T.get(0,1));
            samplesT21.set(i, T.get(1,0));
        }
        
        double true_meanT12 = (double)(C.get(0,1)+1) / (double)(c1 + n);
        double sample_meanT12 = stat.mean(samplesT12);
        double err_T12 = Math.abs(true_meanT12-sample_meanT12)/true_meanT12;
        System.out.println(true_meanT12+" "+sample_meanT12+" -> "+err_T12);
        assert(err_T12 < errtol);

        double true_meanT21 = (double)(C.get(1,0)+1) / (double)(c2 + n);
        double sample_meanT21 = stat.mean(samplesT21);
        double err_T21 = Math.abs(true_meanT21-sample_meanT21)/true_meanT21;
        System.out.println(true_meanT21+" "+sample_meanT21+" -> "+err_T21);
        assert(err_T21 < errtol);
        
        double true_varT12 = true_meanT12 * (1.0-true_meanT12) / (double)(c1 + n + 1);
        double sample_varT12 = stat.variance(samplesT12);
        double err_varT12 = Math.abs(true_varT12-sample_varT12)/true_varT12;
        System.out.println(true_varT12+" "+sample_varT12+" -> "+err_varT12);
        assert(err_varT12 < errtol);

        double true_varT21 = true_meanT21 * (1.0-true_meanT21) / (double)(c2 + n + 1);
        double sample_varT21 = stat.variance(samplesT21);
        double err_varT21 = Math.abs(true_varT21-sample_varT21)/true_meanT21;
        System.out.println(true_varT21+" "+sample_varT21+" -> "+err_varT21);
        assert(err_varT21 < errtol);        
    }
    
    private void compareSamplers(ITransitionMatrixSampler sampler1, ITransitionMatrixSampler sampler2, IDoubleArray C, int nsample, double errtol)
    {
        // Index: i,j,time
        int n = C.rows();
        double[][][] samples1 = new double[n][n][nsample];
        double[][][] samples2 = new double[n][n][nsample];
        
        for (int t=0; t<nsample; t++)
        {
            IDoubleArray T1 = sampler1.sample(100);
            for (int i=0; i<n; i++)
                for (int j=0; j<n; j++)
                    samples1[i][j][t] = T1.get(i,j);
            
            IDoubleArray T2 = sampler1.sample(100);
            for (int i=0; i<n; i++)
                for (int j=0; j<n; j++)
                    samples2[i][j][t] = T2.get(i,j);
        }

        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {
                // compare means
                double m1 = stat.mean(doublesNew.array(samples1[i][j]));
                double m2 = stat.mean(doublesNew.array(samples2[i][j]));
                double errm = Math.abs(m1-m2)/(0.5*(m1+m2));
                System.out.println("mean T_"+(i+1)+","+(j+1)+"\t"+m1+" "+m2+" -> "+errm);
                assert(errm < errtol);                        

                // compare variances
                double v1 = stat.mean(doublesNew.array(samples1[i][j]));
                double v2 = stat.mean(doublesNew.array(samples2[i][j]));
                double errv = Math.abs(v1-v2)/(0.5*(v1+v2));
                System.out.println("var T_"+(i+1)+","+(j+1)+"\t"+v1+" "+v2+" -> "+errv);
                assert(errv < errtol);                        
            }
        }
    }
    
    
    /**
     * Test of createTransitionMatrixSamplerNonrev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixSamplerNonrev()
    {
    }

    /**
     * Test of createTransitionMatrixSamplerRev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixSamplerRev_IDoubleArray()
    {
        IDoubleArray C = doublesNew.array(new double[][]{
            {8, 2, 1},
            {2,10, 3},
            {2, 3, 6}});
        ITransitionMatrixSampler sampler1 = TransitionMatrixSamplerRev.create(C, new Step_Rev_Row_Beta(), new Step_Rev_Quad_MC());
        ITransitionMatrixSampler sampler2 = TransitionMatrixSamplerRev.create(C, new Step_Rev_Row_MC(), new Step_Rev_Quad_MC());
        int nsample = 100000;
        double errtol = 1e-2;
        compareSamplers(sampler1, sampler2, C, nsample, errtol);
    }

    /**
     * Test of createTransitionMatrixSamplerRevMCMC method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixSamplerRevMCMC()
    {
        IDoubleArray C = doublesNew.array(new double[][]{
            {5,2},
            {1,10}});
        Step_Rev_Row_Beta rowstep=new Step_Rev_Row_Beta();
        Step_Rev_Quad_MC quadstep=new Step_Rev_Quad_MC();
        ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, rowstep, quadstep);
        // ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, new Step_Rev_Row_Beta(), new Step_Rev_Quad_MC());
        testSampler2x2(sampler, C, 100000, 1e-2);
        
        int[] Nrow=rowstep.getStepCount();
        int[] Nquad=quadstep.getStepCount();        
        System.out.println("Rowsteps: "+Nrow[0]+" "+Nrow[1]);
        System.out.println("Quadsteps: "+Nquad[0]+" "+Nquad[1]);
    }

    /**
     * Test of createTransitionMatrixSamplerRevGibbs method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixSamplerRevGibbs()
    {
        Step_Rev_Row_MC rowstep=new Step_Rev_Row_MC();
        Step_Rev_Quad_MC quadstep=new Step_Rev_Quad_MC();
        IDoubleArray C = doublesNew.array(new double[][]{
            {5,2},
            {1,10}});
        ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, rowstep, quadstep);
        // ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, new Step_Rev_Row_MC(), new Step_Rev_Quad_MC());
        testSampler2x2(sampler, C, 100000, 1e-2);
        
        int[] Nrow=rowstep.getStepCount();
        int[] Nquad=quadstep.getStepCount();        
        System.out.println("Rowsteps: "+Nrow[0]+" "+Nrow[1]);
        System.out.println("Quadsteps: "+Nquad[0]+" "+Nquad[1]);
    }
    
    @Test
    public void testCreateTransitionMatrixSamplerRevGibbsGibbs()
    {
        IDoubleArray C = doublesNew.array(new double[][]{
            {5,2},
            {1,10}});
        Step_Rev_Row_Beta rowstep=new Step_Rev_Row_Beta();
        Step_Rev_Quad_Gibbs_MC quadstep=new Step_Rev_Quad_Gibbs_MC();
        ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, rowstep, quadstep);
        // ITransitionMatrixSampler sampler = TransitionMatrixSamplerRev.create(C, new Step_Rev_Row_Beta(), new Step_Rev_Quad_Gibbs_MC());
        testSampler2x2(sampler, C, 100000, 1e-2);
        
        int[] Nrow=rowstep.getStepCount();
        int[] Nquad=quadstep.getStepCount();        
        System.out.println("Rowsteps: "+Nrow[0]+" "+Nrow[1]);
        System.out.println("Quadsteps: "+Nquad[0]+" "+Nquad[1]);
    }

    /**
     * Test of createTransitionMatrixSamplerRev method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTransitionMatrixSamplerRev_IDoubleArray_IDoubleArray()
    {
    }

    /**
     * Test of createPCCA method, of class MarkovModelFactory.
     */
    @Test
    public void testCreatePCCA()
    {
    }

    /**
     * Test of createCommittor method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCommittor_3args()
    {
    }

    /**
     * Test of createCommittor method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateCommittor_4args()
    {
    }

    /**
     * Test of createTPT method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTPT_3args()
    {
    }

    /**
     * Test of createTPT method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateTPT_4args()
    {
    }

    /**
     * Test of createDynamicalExpectations method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateDynamicalExpectations_IDoubleArray()
    {
    }

    /**
     * Test of createDynamicalExpectations method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateDynamicalExpectations_IDoubleArray_IDoubleArray()
    {
    }

    /**
     * Test of createDynamicalFingerprint method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateDynamicalFingerprint_IDoubleArray()
    {
    }

    /**
     * Test of createDynamicalFingerprint method, of class MarkovModelFactory.
     */
    @Test
    public void testCreateDynamicalFingerprint_IDoubleArray_IDoubleArray()
    {
    }

    /**
     * Test of markovChain method, of class MarkovModelFactory.
     */
    @Test
    public void testMarkovChain_IDoubleArray()
    {
    }

    /**
     * Test of markovChain method, of class MarkovModelFactory.
     */
    @Test
    public void testMarkovChain_IDoubleArray_IDoubleArray()
    {
    }
}
