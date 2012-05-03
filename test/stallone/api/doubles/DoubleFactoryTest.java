/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import java.io.IOException;
import org.junit.*;
import static org.junit.Assert.*;

import static stallone.doubles.DoubleArrayTest.*;

/**
 *
 * @author noe
 */
public class DoubleFactoryTest
{
    
    public DoubleFactoryTest()
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
     * Test of denseColumn method, of class DoubleFactory.
     */
    @Test
    public void testDenseColumn()
    {
        System.out.println("denseColumn");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.denseColumn(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of denseRow method, of class DoubleFactory.
     */
    @Test
    public void testDenseRow()
    {
        System.out.println("denseRow");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.denseRow(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sparseColumn method, of class DoubleFactory.
     */
    @Test
    public void testSparseColumn()
    {
        System.out.println("sparseColumn");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.sparseColumn(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sparseRow method, of class DoubleFactory.
     */
    @Test
    public void testSparseRow()
    {
        System.out.println("sparseRow");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.sparseRow(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of column method, of class DoubleFactory.
     */
    @Test
    public void testColumn()
    {
        System.out.println("column");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.column(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of row method, of class DoubleFactory.
     */
    @Test
    public void testRow()
    {
        System.out.println("row");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.row(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_int()
    {
        System.out.println("array");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_doubleArr()
    {
        System.out.println("array");
        double[] init = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(init);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of denseMatrix method, of class DoubleFactory.
     */
    @Test
    public void testDenseMatrix()
    {
        System.out.println("denseMatrix");
        int nrows = 0;
        int ncols = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.denseMatrix(nrows, ncols);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of sparseMatrix method, of class DoubleFactory.
     */
    @Test
    public void testSparseMatrix()
    {
        System.out.println("sparseMatrix");
        int nrows = 0;
        int ncols = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.sparseMatrix(nrows, ncols);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_doubleArrArr()
    {
        System.out.println("array");
        double[][] init = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(init);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_int_int()
    {
        System.out.println("array");
        int rows = 0;
        int cols = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(rows, cols);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public final static String inputMatrixDense = "test/stallone/api/doubles/inputfile-denseMatrix-2x2.dat";
    public final static String inputMatrixSparse = "test/stallone/api/doubles/inputfile-sparseMatrix-2x2.dat";
    
    /**
     * Test of fromFile method, of class DoubleFactory.
     */
    @Test
    public void testFromFile()
    {
        System.out.println("fromFile");

        try
        {
            IDoubleArray M1 = Doubles.create.fromFile(inputMatrixDense);
            IDoubleArray M2 = Doubles.create.fromFile(inputMatrixSparse);

            assertEqual(M1, M2, 1e-8);
        }
        catch(IOException e)
        {
            System.out.println("IOException: ");
            e.printStackTrace();
            System.exit(0);
        }
        
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_int_double()
    {
        System.out.println("array");
        int size = 0;
        double value = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(size, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_String()
    {
        System.out.println("array");
        String from = "";
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(from);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_double()
    {
        System.out.println("arrayFrom");
        double d = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayFrom(d);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_double_doubleArr()
    {
        System.out.println("arrayFrom");
        double d1 = 0.0;
        double[] d2 = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayFrom(d1, d2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_doubleArr()
    {
        System.out.println("arrayFrom");
        double[] arr = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayFrom(arr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_intArr()
    {
        System.out.println("arrayFrom");
        int[] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_floatArr()
    {
        System.out.println("arrayFrom");
        float[] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayRandom method, of class DoubleFactory.
     */
    @Test
    public void testArrayRandom()
    {
        System.out.println("arrayRandom");
        int n = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayRandom(n);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of arrayRange method, of class DoubleFactory.
     */
    @Test
    public void testArrayRange()
    {
        System.out.println("arrayRange");
        double start = 0.0;
        double end = 0.0;
        double step = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.arrayRange(start, end, step);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrix method, of class DoubleFactory.
     */
    @Test
    public void testMatrix_int_int()
    {
        System.out.println("matrix");
        int nrows = 0;
        int ncols = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrix(nrows, ncols);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrix method, of class DoubleFactory.
     */
    @Test
    public void testMatrix_3args()
    {
        System.out.println("matrix");
        int nrows = 0;
        int ncols = 0;
        double value = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrix(nrows, ncols, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrix method, of class DoubleFactory.
     */
    @Test
    public void testMatrix_doubleArrArr()
    {
        System.out.println("matrix");
        double[][] res = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrix(res);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrix method, of class DoubleFactory.
     */
    @Test
    public void testMatrix_String()
    {
        System.out.println("matrix");
        String from = "";
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrix(from);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrixFrom method, of class DoubleFactory.
     */
    @Test
    public void testMatrixFrom_floatArrArr()
    {
        System.out.println("matrixFrom");
        float[][] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrixFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrixFrom method, of class DoubleFactory.
     */
    @Test
    public void testMatrixFrom_intArrArr()
    {
        System.out.println("matrixFrom");
        int[][] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrixFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of matrixReshape method, of class DoubleFactory.
     */
    @Test
    public void testMatrixReshape()
    {
        System.out.println("matrixReshape");
        IDoubleArray arr = null;
        int d1 = 0;
        int d2 = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.matrixReshape(arr, d1, d2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of diag method, of class DoubleFactory.
     */
    @Test
    public void testDiag_int_double()
    {
        System.out.println("diag");
        int size = 0;
        double value = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.diag(size, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of diag method, of class DoubleFactory.
     */
    @Test
    public void testDiag_doubleArr()
    {
        System.out.println("diag");
        double[] values = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.diag(values);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of diag method, of class DoubleFactory.
     */
    @Test
    public void testDiag_IDoubleArray()
    {
        System.out.println("diag");
        IDoubleArray values = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.diag(values);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of symmetric method, of class DoubleFactory.
     */
    @Test
    public void testSymmetric()
    {
        System.out.println("symmetric");
        IDoubleArray matrix = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.symmetric(matrix);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of symmetricReal method, of class DoubleFactory.
     */
    @Test
    public void testSymmetricReal()
    {
        System.out.println("symmetricReal");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.symmetricReal(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of identity method, of class DoubleFactory.
     */
    @Test
    public void testIdentity()
    {
        System.out.println("identity");
        int dim = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleArray expResult = null;
        IDoubleArray result = instance.identity(dim);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class DoubleFactory.
     */
    @Test
    public void testList_int()
    {
        System.out.println("list");
        int size = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.list(size);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class DoubleFactory.
     */
    @Test
    public void testList_int_double()
    {
        System.out.println("list");
        int size = 0;
        double value = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.list(size, value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class DoubleFactory.
     */
    @Test
    public void testList_IDoubleArray()
    {
        System.out.println("list");
        IDoubleArray arr = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.list(arr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFrom method, of class DoubleFactory.
     */
    @Test
    public void testListFrom_double()
    {
        System.out.println("listFrom");
        double d = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listFrom(d);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFrom method, of class DoubleFactory.
     */
    @Test
    public void testListFrom_double_doubleArr()
    {
        System.out.println("listFrom");
        double d1 = 0.0;
        double[] d2 = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listFrom(d1, d2);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFrom method, of class DoubleFactory.
     */
    @Test
    public void testListFrom_doubleArr()
    {
        System.out.println("listFrom");
        double[] arr = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listFrom(arr);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFrom method, of class DoubleFactory.
     */
    @Test
    public void testListFrom_intArr()
    {
        System.out.println("listFrom");
        int[] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listFrom method, of class DoubleFactory.
     */
    @Test
    public void testListFrom_floatArr()
    {
        System.out.println("listFrom");
        float[] a = null;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listFrom(a);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listRandom method, of class DoubleFactory.
     */
    @Test
    public void testListRandom()
    {
        System.out.println("listRandom");
        int n = 0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listRandom(n);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of listRange method, of class DoubleFactory.
     */
    @Test
    public void testListRange()
    {
        System.out.println("listRange");
        double start = 0.0;
        double end = 0.0;
        double step = 0.0;
        DoubleFactory instance = new DoubleFactory();
        IDoubleList expResult = null;
        IDoubleList result = instance.listRange(start, end, step);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
