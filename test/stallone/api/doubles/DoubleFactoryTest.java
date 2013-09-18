/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static stallone.doubles.DoubleArrayTest.assertEqual;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author noe
 */
public class DoubleFactoryTest
{
	/**
	 * delta used to compare floats for equality
	 */
	static final double delta = 1e-10;
	
	DoubleFactory instance;
	
	
	final static String inputMatrixDense = "test/stallone/api/doubles/inputfile-denseMatrix-2x2.dat";
	final static String inputMatrixSparse = "test/stallone/api/doubles/inputfile-sparseMatrix-2x2.dat";
	
    
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
    	// every test case will use a new factory!
    	instance = new DoubleFactory();
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
        int size = 100;
        double delta = 1e-10;

        IDoubleArray expected = instance.array(new double[size]);
        IDoubleArray result = instance.denseColumn(size);
        assertArrayEquals(expected.getArray(), result.getArray(), delta);
    }

    /**
     * Test of denseRow method, of class DoubleFactory.
     */
    @Test
    public void testDenseRow()
    {
        int size = 100;
       
        IDoubleArray expected = instance.array(new double[size]);
        IDoubleArray result = instance.denseRow(size);
        assertArrayEquals(expected.getArray(), result.getArray(), delta);
    }

    /**
     * Test of sparseColumn method, of class DoubleFactory.
     * TODO do a multiplication and test for equality
     */
    @Test
    public void testSparseColumn()
    {
    	int size = 10;
        
        IDoubleArray expResult = instance.column(size);
        expResult.set(4, 1.0f);
        expResult.set(8, 1.0f);
        
        IDoubleArray result = instance.sparseColumn(size);
        
        // FIXME: leads to array out of bounds exception
        result.set(4, 1.0f);
        result.set(8, 1.0f);
        
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of sparseRow method, of class DoubleFactory.
     */
    @Test
    public void testSparseRow()
    {
    	int size = 10;
        
        IDoubleArray expResult = instance.row(size);
        expResult.set(4, 1.0f);
        expResult.set(9, 1.0f);
        
        IDoubleArray result = instance.sparseRow(size);
        
        // FIXME: leads to array out of bounds exception
        result.set(4, 1.0f);
        result.set(9, 1.0f);
        
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of column method, of class DoubleFactory.
     */
    @Test
    public void testColumn()
    {
        int size = 10;
        IDoubleArray expResult = instance.array(new double[size][size]);
        IDoubleArray result = instance.column(size);
        assertArrayEquals(expResult.getColumn(0), result.getArray(), delta);
    }

    /**
     * Test of row method, of class DoubleFactory.
     */
    @Test
    public void testRow() {
        int size = 10;
        IDoubleArray expResult = instance.array(new double[size][size]);
        IDoubleArray result = instance.column(size);
        assertArrayEquals(expResult.getColumn(0), result.getArray(), delta);
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_int()
    {
        int size = 0;
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
        double[] init = null;
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
        int nrows = 10;
        int ncols = 10;
        IDoubleArray expResult = instance.array(new double [ncols][nrows]);
        IDoubleArray result = instance.denseMatrix(nrows, ncols);
        
        assertEquals(expResult.columns(), result.columns());
        assertEquals(expResult.rows(), result.rows());
        
        for(int i = 0; i < ncols; ++i)
            assertArrayEquals(expResult.getColumn(i), result.getColumn(i), delta);
    }

    /**
     * Test of sparseMatrix method, of class DoubleFactory.
     */
    @Test
    public void testSparseMatrix()
    {
        int nrows = 10;
        int ncols = 10;
        IDoubleArray expResult = instance.array(new double [ncols][nrows]);
        IDoubleArray result = instance.sparseMatrix(nrows, ncols);
        
        expResult.set(3, 4, 1.f);
        // FIXME: array out of bounds
        result.set(3, 4, 1.f);
        
        assertEquals(expResult.columns(), result.columns());
        assertEquals(expResult.rows(), result.rows());
        
        for(int i = 0; i < ncols; ++i)
            assertArrayEquals(expResult.getColumn(i), result.getColumn(i), delta);
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_doubleArrArr()
    {
        double[][] init = null;
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
        int rows = 0;
        int cols = 0;
        IDoubleArray expResult = null;
        IDoubleArray result = instance.array(rows, cols);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of fromFile method, of class DoubleFactory.
     */
    @Test
    public void testFromFile()
    {
        try
        {
            IDoubleArray M1 = Doubles.create.fromFile(inputMatrixDense);
            IDoubleArray M2 = Doubles.create.fromFile(inputMatrixSparse);

            assertEqual(M1, M2, 1e-8);
        }
        catch(IOException e)
        {
            fail("failed to read from input files.");
        }
    }

    /**
     * Test of array method, of class DoubleFactory.
     */
    @Test
    public void testArray_int_double()
    {
        int size = 0;
        double value = 0.0;

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
        String from = "1,2,3";

        IDoubleArray expResult = instance.array(new double[] {1,2,3});
        IDoubleArray result = instance.array(from);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_double()
    {
        double d = 0.0;

        IDoubleArray expResult = instance.array(new double[1]);
        IDoubleArray result = instance.arrayFrom(d);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_double_doubleArr()
    {
        double d1 = 42;
        double[] d2 = new double[0];

        IDoubleArray expResult = instance.array(new double[] {d1});
        IDoubleArray result = instance.arrayFrom(d1, d2);
        
        assertEquals(expResult.size(), result.size());
        
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     * TODO: this is trivial?
     */
    @Test
    public void testArrayFrom_doubleArr()
    {
        double[] arr = {1,2,3};

        IDoubleArray expResult = instance.array(arr);
        IDoubleArray result = instance.arrayFrom(arr);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_intArr()
    {
        int[] a = {1,2,3};
        double[] b = new double[a.length];

        // cast integer to double
        for (int i = 0; i < b.length; i++) {
            b[i] = a[i];
        }
        
        IDoubleArray expResult = instance.arrayFrom(b);
        IDoubleArray result = instance.arrayFrom(a);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayFrom method, of class DoubleFactory.
     */
    @Test
    public void testArrayFrom_floatArr()
    {
        float[] a = {1f,2f,3f};
        double[] b = new double[a.length];

        for (int i = 0; i < b.length; i++) {
            b[i] = a[i];
        }

        IDoubleArray expResult = instance.arrayFrom(b);
        IDoubleArray result = instance.arrayFrom(a);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of arrayRandom method, of class DoubleFactory.
     */
    @Test
    public void testArrayRandom()
    {
        int n = 100;

        IDoubleArray result = instance.arrayRandom(n);
        
        // check random elements are in bounds of [0,1[
        // TODO: is this check correct?!
        for(int i = 0; i < result.size(); ++i) {
            assertTrue( result.get(i) < 1);
            assertTrue( result.get(i) >= 0);
        }
    }

    /**
     * Test of arrayRange method, of class DoubleFactory.
     */
    @Test
    public void testArrayRange()
    {
        double start = 0.0;
        double end = 10;
        double step = 1.5;

        IDoubleArray expResult = instance.array(new double[]{0.0, 1.5, 3.0, 4.5, 6.0, 7.5, 9});
        IDoubleArray result = instance.arrayRange(start, end, step);
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }
    
    /**
     * Test of arrayRange method, of class DoubleFactory.
     */
    @Test
    public void testArrayRangeNeg()
    {
        double start = 9.0;
        double end = 0.0;
        double step = -1.5;
        
        double[] arr = {9.0, 7.5, 6.0, 4.5, 3.0, 1.5};
        
        IDoubleArray expResult = instance.array(arr);
        IDoubleArray result = instance.arrayRange(start, end, step);
        
        assertEquals(expResult.size(), result.size());
        assertArrayEquals(expResult.getArray(), result.getArray(), delta);
    }

    /**
     * Test of matrix method, of class DoubleFactory.
     */
    @Test
    public void testMatrix_int_int()
    {
        
        int nrows = 0;
        int ncols = 0;

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
        int nrows = 0;
        int ncols = 0;
        double value = 0.0;

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
        double[][] res = null;

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
        String from = "";

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
        float[][] a = null;

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
        int[][] a = null;

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
        IDoubleArray arr = null;
        int d1 = 0;
        int d2 = 0;

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
        int size = 10;
        double value = 1f;

        IDoubleArray expResult = instance.matrix(size, size);
        
        for(int i = 0; i < size; i++) {
            expResult.set(i, i, value);
        }
        
        IDoubleArray result = instance.diag(size, value);
        
        for(int i = 0; i < size; i++) {
            assertArrayEquals(expResult.getRow(i), result.getRow(i), delta);
        }
    }

    /**
     * Test of diag method, of class DoubleFactory.
     */
    @Test
    public void testDiag_doubleArr()
    {
        double[] values = {1,2,3,4,5,6,7};
        int size = values.length;

        IDoubleArray expResult = instance.matrix(size, size);
        
        for(int i = 0; i < size; i++) {
            expResult.set(i, i, values[i]);
        }
        
        IDoubleArray result = instance.diag(values);
        
        for(int i = 0; i < size; i++) {
            assertArrayEquals(expResult.getRow(i), result.getRow(i), delta);
        }
    }

    /**
     * Test of diag method, of class DoubleFactory.
     */
    @Test
    public void testDiag_IDoubleArray()
    {
        int size = 3;
        IDoubleArray values = instance.array(new double[] {1,2,3});

        IDoubleArray expResult = instance.matrix(3, 3);
        expResult.set(0, 0, 1);
        expResult.set(1, 1, 2);
        expResult.set(2, 2, 3);
        
        
        IDoubleArray result = instance.diag(values);
        
        
        for(int i = 0; i < size; i++) {
            assertArrayEquals(expResult.getRow(i), result.getRow(i), delta);
        }
    }

    /**
     * Test of symmetric method, of class DoubleFactory.
     */
    @Test
    public void testSymmetric()
    {
        IDoubleArray matrix = null;

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
        int size = 0;

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
        int dim = 0;

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
        int size = 0;

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
        int size = 0;
        double value = 0.0;

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
        IDoubleArray arr = null;

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
        double d = 0.0;

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
        double d1 = 0.0;
        double[] d2 = null;

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
        double[] arr = null;

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
        int[] a = null;

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
        float[] a = null;

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
        
        int n = 0;

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
        
        double start = 0.0;
        double end = 0.0;
        double step = 0.0;

        IDoubleList expResult = null;
        IDoubleList result = instance.listRange(start, end, step);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
