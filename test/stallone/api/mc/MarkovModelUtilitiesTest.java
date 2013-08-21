/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import java.io.IOException;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import stallone.api.algebra.Algebra;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.complex.Complex;
import stallone.api.datasequence.DataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.api.io.IO;
import stallone.doubles.DoubleArrayTest;

import static stallone.doubles.DoubleArrayTest.*;

/**
 *
 * @author noe
 */
public class MarkovModelUtilitiesTest
{
    public static final String inputT1 = "./test/stallone/api/mc/inputfile-T-uniform.dat";
    public static final String inputT1_pi = "./test/stallone/api/mc/inputfile-T-uniform-pi.dat";
    
    
    public MarkovModelUtilitiesTest()
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
    
    //////////////////////////////////////////////////////////////////////
    //
    // Connected component tests
    // 
    //////////////////////////////////////////////////////////////////////
    
    private static final IDoubleArray P1 = Doubles.create.matrix(new double[][]
    {{0,1,0},
     {0,0,1},
     {1,0,0}}
            );
    private static final Set<IIntArray> C1 = new HashSet<IIntArray>();
    static
    {
        C1.add(Ints.create.arrayFrom(0,1,2));
    }
    IIntArray giant1 = Ints.create.arrayFrom(0,1,2);

    private static final IDoubleArray P2 = Doubles.create.matrix(new double[][]
    {{1,1,0,0},
     {1,1,0,0},
     {0,0,1,1},
     {0,0,1,1}}
            );
    private static final Set<IIntArray> C2 = new HashSet<IIntArray>();
    static
    {
        C2.add(Ints.create.arrayFrom(0,1));
        C2.add(Ints.create.arrayFrom(2,3));
    }
    IIntArray giant2 = Ints.create.arrayFrom(0,1);

    private static final IDoubleArray P3 = Doubles.create.matrix(new double[][]
    {{0,1,0,0},
     {0,1,1,0},
     {0,1,1,0},
     {0,0,1,0}}
            );
    private static final Set<IIntArray> C3 = new HashSet<IIntArray>();
    static
    {
        C3.add(Ints.create.arrayFrom(0));
        C3.add(Ints.create.arrayFrom(1,2));
        C3.add(Ints.create.arrayFrom(3));
    }
    IIntArray giant3 = Ints.create.arrayFrom(1,2);
    
    /**
     * Test of isConnected method, of class MarkovModelUtilities.
     */
    @Test
    public void testIsConnected()
    {
        System.out.println("isConnected");

        assertEquals(MarkovModel.util.isConnected(P1), true);
        assertEquals(MarkovModel.util.isConnected(P2), false);
        assertEquals(MarkovModel.util.isConnected(P3), false);
    }

    /**
     * Test of connectedComponents method, of class MarkovModelUtilities.
     */
    @Test
    public void testConnectedComponents()
    {
        System.out.println("connectedComponents");
        
        Collection<IIntArray> cc1 = MarkovModel.util.connectedComponents(P1);
        assertTrue(cc1.containsAll(C1));
        assertEquals(cc1.size(), C1.size());

        Collection<IIntArray> cc2 = MarkovModel.util.connectedComponents(P2);
        assertTrue(cc2.containsAll(C2));
        assertEquals(cc2.size(), C2.size());

        Collection<IIntArray> cc3 = MarkovModel.util.connectedComponents(P3);
        assertTrue(cc3.containsAll(C3));
        assertEquals(cc3.size(), C3.size());        
    }

    /**
     * Test of giantComponent method, of class MarkovModelUtilities.
     */
    @Test
    public void testGiantComponent()
    {
        System.out.println("giantComponent");

        assertEquals(MarkovModel.util.giantComponent(P1), giant1);
        assertEquals(MarkovModel.util.giantComponent(P2), giant2);
        assertEquals(MarkovModel.util.giantComponent(P3), giant3);
    }
    
    /**
     * Test of estimateC method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateC_Iterable_int()
    {
        System.out.println("estimateC");
        Iterable<IIntArray> trajs = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateC(trajs, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCmilestoning method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCmilestoning_3args_1()
    {
        System.out.println("estimateCmilestoning");
        Iterable<IIntArray> trajs = null;
        Iterable<IIntArray> cores = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCmilestoning(trajs, cores, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCmilestoning method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCmilestoning_Iterable_int()
    {
        System.out.println("estimateCmilestoning");
        Iterable<IIntArray> trajs = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCmilestoning(trajs, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateC method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateC_IIntArray_int()
    {
        System.out.println("estimateC");
        IIntArray traj = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateC(traj, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCmilestoning method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCmilestoning_3args_2()
    {
        System.out.println("estimateCmilestoning");
        IIntArray traj = null;
        Iterable<IIntArray> cores = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCmilestoning(traj, cores, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCmilestoning method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCmilestoning_IIntArray_int()
    {
        System.out.println("estimateCmilestoning");
        IIntArray traj = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCmilestoning(traj, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCstepping method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCstepping_Iterable_int()
    {
        System.out.println("estimateCstepping");
        Iterable<IIntArray> trajs = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCstepping(trajs, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateCstepping method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateCstepping_IIntArray_int()
    {
        System.out.println("estimateCstepping");
        IIntArray traj = null;
        int lag = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateCstepping(traj, lag);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of logLikelihood method, of class MarkovModelUtilities.
     */
    @Test
    public void testLogLikelihood()
    {
        System.out.println("logLikelihood");
        IDoubleArray T = null;
        IDoubleArray C = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        double expResult = 0.0;
        double result = instance.logLikelihood(T, C);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of logLikelihoodCorrelationMatrix method, of class MarkovModelUtilities.
     */
    @Test
    public void testLogLikelihoodCorrelationMatrix()
    {
        System.out.println("logLikelihoodCorrelationMatrix");
        IDoubleArray corr = null;
        IDoubleArray C = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        double expResult = 0.0;
        double result = instance.logLikelihoodCorrelationMatrix(corr, C);
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isTransitionMatrix method, of class MarkovModelUtilities.
     */
    @Test
    public void testIsTransitionMatrix()
    {
        System.out.println("isTransitionMatrix");
        IDoubleArray T = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        boolean expResult = false;
        boolean result = instance.isTransitionMatrix(T);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isRateMatrix method, of class MarkovModelUtilities.
     */
    @Test
    public void testIsRateMatrix()
    {
        System.out.println("isRateMatrix");
        IDoubleArray K = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        boolean expResult = false;
        boolean result = instance.isRateMatrix(K);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isReversible method, of class MarkovModelUtilities.
     */
    @Test
    public void testIsReversible_IDoubleArray()
    {
        System.out.println("isReversible");
        IDoubleArray T = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        boolean expResult = false;
        boolean result = instance.isReversible(T);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isReversible method, of class MarkovModelUtilities.
     */
    @Test
    public void testIsReversible_IDoubleArray_IDoubleArray()
    {
        System.out.println("isReversible");
        IDoubleArray T = null;
        IDoubleArray pi = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        boolean expResult = false;
        boolean result = instance.isReversible(T, pi);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateT method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateT()
    {
        System.out.println("estimateT");
        IDoubleArray counts = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateT(counts);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of estimateTrev method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateTrev_IDoubleArray()
    {
        System.out.println("estimateTrev");
        double[][] C = {
            {10,5,1},
            {1,7,4},
            {5,7,25}};
        double[][] Tref = {
            {0.625,               0.23689343769530435, 0.13810656230469573},
            {0.18414208307292756, 0.5833333333333334,  0.23252458359373912},
            {0.10244040548986129, 0.22188391883446298, 0.6756756756756757}};
        
        IDoubleArray counts = Doubles.create.array(C);
        IDoubleArray expResult = Doubles.create.array(Tref);
        IDoubleArray result = MarkovModel.util.estimateTrev(counts);
        DoubleArrayTest.assertEqual(result, expResult, 1e-4);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of estimateTrev method, of class MarkovModelUtilities.
     */
    @Test
    public void testEstimateTrev_IDoubleArray_IDoubleArray()
    {
        System.out.println("estimateTrev");
        IDoubleArray counts = null;
        IDoubleArray piFixed = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.estimateTrev(counts, piFixed);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of stationaryDistribution method, of class MarkovModelUtilities.
     */
    @Test
    public void testStationaryDistribution()
    {
        System.out.println("stationaryDistribution");

        try
        {
            IDoubleArray T = Doubles.create.fromFile(inputT1);
            IDoubleArray expResult = Doubles.create.fromFile(inputT1_pi);            
            IDoubleArray pi = MarkovModel.util.stationaryDistribution(T);
            assertEqual(pi, expResult, 1e-5);
        }
        catch(IOException e)
        {
            System.out.println("Exception: "+e);
            e.printStackTrace();
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of stationaryDistributionRevQuick method, of class MarkovModelUtilities.
     */
    @Test
    public void testStationaryDistributionRevQuick()
    {
        System.out.println("stationaryDistributionRevQuick");
        IDoubleArray T = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.stationaryDistributionRevQuick(T);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of timescales method, of class MarkovModelUtilities.
     */
    @Test
    public void testTimescales_IDoubleArray_double()
    {
        System.out.println("timescales");
        IDoubleArray T = null;
        double tau = 0.0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.timescales(T, tau);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of timescales method, of class MarkovModelUtilities.
     */
    @Test
    public void testTimescales_5args()
    {
        System.out.println("timescales");
        Iterable<IIntArray> dtraj = null;
        ICountMatrixEstimator Cest = null;
        ITransitionMatrixEstimator Test = null;
        int ntimescales = 0;
        IIntArray lagtimes = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.timescales(dtraj, Cest, Test, ntimescales, lagtimes);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of metastableStates method, of class MarkovModelUtilities.
     */
    @Test
    public void testMetastableStates()
    {
        System.out.println("metastableStates");
        IDoubleArray M = null;
        int nstates = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IIntArray expResult = null;
        IIntArray result = instance.metastableStates(M, nstates);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of metastableMemberships method, of class MarkovModelUtilities.
     */
    @Test
    public void testMetastableMemberships()
    {
        System.out.println("metastableMemberships");
        IDoubleArray M = null;
        int nstates = 0;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.metastableMemberships(M, nstates);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of forwardCommittor method, of class MarkovModelUtilities.
     */
    @Test
    public void testForwardCommittor()
    {
        System.out.println("forwardCommittor");
        IDoubleArray M = null;
        IIntArray A = null;
        IIntArray B = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.forwardCommittor(M, A, B);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of backwardCommittor method, of class MarkovModelUtilities.
     */
    @Test
    public void testBackwardCommittor()
    {
        System.out.println("backwardCommittor");
        IDoubleArray M = null;
        IIntArray A = null;
        IIntArray B = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.backwardCommittor(M, A, B);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of autocorrelation method, of class MarkovModelUtilities.
     */
    @Test
    public void testAutocorrelation()
    {
        System.out.println("autocorrelation");
        IDoubleArray M = null;
        IDoubleArray observable = null;
        IDoubleArray timepoints = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.autocorrelation(M, observable, timepoints);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of correlation method, of class MarkovModelUtilities.
     */
    @Test
    public void testCorrelation()
    {
        System.out.println("correlation");
        IDoubleArray M = null;
        IDoubleArray observable1 = null;
        IDoubleArray observable2 = null;
        IDoubleArray timepoints = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.correlation(M, observable1, observable2, timepoints);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of perturbationExpectation method, of class MarkovModelUtilities.
     */
    @Test
    public void testPerturbationExpectation()
    {
        System.out.println("perturbationExpectation");
        IDoubleArray M = null;
        IDoubleArray pi0 = null;
        IDoubleArray observable = null;
        IDoubleArray timepoints = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.perturbationExpectation(M, pi0, observable, timepoints);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fingerprintAutocorrelation method, of class MarkovModelUtilities.
     */
    @Test
    public void testFingerprintAutocorrelation()
    {
        System.out.println("fingerprintAutocorrelation");
        IDoubleArray M = null;
        IDoubleArray observable = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.fingerprintAutocorrelation(M, observable);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fingerprintCorrelation method, of class MarkovModelUtilities.
     */
    @Test
    public void testFingerprintCorrelation()
    {
        System.out.println("fingerprintCorrelation");
        IDoubleArray M = null;
        IDoubleArray observable1 = null;
        IDoubleArray observable2 = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.fingerprintCorrelation(M, observable1, observable2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fingerprintPerturbation method, of class MarkovModelUtilities.
     */
    @Test
    public void testFingerprintPerturbation()
    {
        System.out.println("fingerprintPerturbation");
        IDoubleArray M = null;
        IDoubleArray p0 = null;
        IDoubleArray observable = null;
        MarkovModelUtilities instance = new MarkovModelUtilities();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.fingerprintPerturbation(M, p0, observable);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
