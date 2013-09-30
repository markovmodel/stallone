/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import org.junit.*;
import static org.junit.Assert.*;
import stallone.api.algebra.Algebra;

/**
 *
 * @author noe
 */
public class IDoubleArrayTest
{
    public static final IDoubleArray array = Doubles.create.arrayFrom(1,2,3,4,5,6,7,8,9);    
    public static final IDoubleArray matrix = Doubles.create.array(new double[][]{
        {1,2,3},
        {4,5,6},
        {7,8,9}
    });
    public static final IDoubleArray sparseMatrix = Doubles.create.array(new double[][]{
        {1,0,0},
        {0,0,6},
        {7,0,9}
    });
    public static final int sparseMatrixNonzero = 4;
    
    public IDoubleArrayTest()
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
     * Test of size method, of class IDoubleArray.
     */
    @Test
    public void testSize()
    {
    }

    /**
     * Test of order method, of class IDoubleArray.
     */
    @Test
    public void testOrder()
    {
    }

    /**
     * Test of rows method, of class IDoubleArray.
     */
    @Test
    public void testRows()
    {
    }

    /**
     * Test of columns method, of class IDoubleArray.
     */
    @Test
    public void testColumns()
    {
    }

    /**
     * Test of get method, of class IDoubleArray.
     */
    @Test
    public void testGet_int()
    {
    }

    /**
     * Test of get method, of class IDoubleArray.
     */
    @Test
    public void testGet_int_int()
    {
    }

    /**
     * Test of set method, of class IDoubleArray.
     */
    @Test
    public void testSet_int_double()
    {
    }

    /**
     * Test of set method, of class IDoubleArray.
     */
    @Test
    public void testSet_3args()
    {
    }

    /**
     * Test of zero method, of class IDoubleArray.
     */
    @Test
    public void testZero()
    {
    }

    /**
     * Test of getArray method, of class IDoubleArray.
     */
    @Test
    public void testGetArray()
    {
    }

    /**
     * Test of getTable method, of class IDoubleArray.
     */
    @Test
    public void testGetTable()
    {
    }

    /**
     * Test of getRow method, of class IDoubleArray.
     */
    @Test
    public void testGetRow()
    {
    }

    /**
     * Test of getColumn method, of class IDoubleArray.
     */
    @Test
    public void testGetColumn()
    {

    }

    /**
     * Test of iterator method, of class IDoubleArray.
     */
    @Test
    public void testIterator()
    {
        double sum = 0;
        int nelements = 0;
        for (IDoubleElement e : array)
        {
            sum += e.get();
            nelements++;
        }        
        assertEquals(nelements, array.size());
        assertEquals((int)sum, (int)Algebra.util.sum(array));

        sum = 0;
        nelements = 0;
        for (IDoubleElement e : matrix)
        {
            sum += e.get();
            nelements++;
        }        
        assertEquals(nelements, matrix.size());
        assertEquals((int)sum, (int)Algebra.util.sum(matrix));
    }

    /**
     * Test of nonzeroIterator method, of class IDoubleArray.
     */
    @Test
    public void testNonzeroIterator()
    {
        double sum = 0;
        int nelements = 0;
        for (IDoubleIterator it = sparseMatrix.nonzeroIterator(); it.hasNext(); it.advance())
        {
            sum += it.get();
            nelements++;
        }        
        assertEquals(nelements, sparseMatrixNonzero);
        assertEquals((int)sum, (int)Algebra.util.sum(sparseMatrix));
    }

    /**
     * Test of copy method, of class IDoubleArray.
     */
    @Test
    public void testCopy()
    {

    }

    /**
     * Test of copyFrom method, of class IDoubleArray.
     */
    @Test
    public void testCopyFrom()
    {

    }

    /**
     * Test of copyInto method, of class IDoubleArray.
     */
    @Test
    public void testCopyInto()
    {

    }

    /**
     * Test of create method, of class IDoubleArray.
     */
    @Test
    public void testCreate_int()
    {

    }

    /**
     * Test of create method, of class IDoubleArray.
     */
    @Test
    public void testCreate_int_int()
    {
    }

    /**
     * Test of viewRow method, of class IDoubleArray.
     */
    @Test
    public void testViewRow()
    {

    }

    /**
     * Test of viewColumn method, of class IDoubleArray.
     */
    @Test
    public void testViewColumn()
    {

    }

    /**
     * Test of viewBlock method, of class IDoubleArray.
     */
    @Test
    public void testViewBlock()
    {
    }

    /**
     * Test of view method, of class IDoubleArray.
     */
    @Test
    public void testView()
    {

    }

}
