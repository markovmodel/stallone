/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import org.junit.*;
import static org.junit.Assert.*;
import stallone.api.doubles.IDoubleElement;
import stallone.api.doubles.IDoubleIterator;

/**
 *
 * @author noe
 */
public class SparseRealMatrixTest
{
    
    public SparseRealMatrixTest()
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
     * Test of zero method, of class SparseRealMatrix.
     */
    @Test
    public void testZero()
    {
    }

    /**
     * Test of rows method, of class SparseRealMatrix.
     */
    @Test
    public void testRows()
    {
    }

    /**
     * Test of columns method, of class SparseRealMatrix.
     */
    @Test
    public void testColumns()
    {
    }

    /**
     * Test of get method, of class SparseRealMatrix.
     */
    @Test
    public void testGet()
    {
    }

    /**
     * Test of set method, of class SparseRealMatrix.
     */
    @Test
    public void testSet()
    {
    }

    /**
     * Test of copyFrom method, of class SparseRealMatrix.
     */
    @Test
    public void testCopyFrom()
    {
    }

    /**
     * Test of copyInto method, of class SparseRealMatrix.
     */
    @Test
    public void testCopyInto()
    {
    }

    /**
     * Test of copy method, of class SparseRealMatrix.
     */
    @Test
    public void testCopy()
    {
    }

    /**
     * Test of create method, of class SparseRealMatrix.
     */
    @Test
    public void testCreate_int_int()
    {
    }

    /**
     * Test of create method, of class SparseRealMatrix.
     */
    @Test
    public void testCreate_int()
    {
    }

    /**
     * Test of viewRow method, of class SparseRealMatrix.
     */
    @Test
    public void testViewRow()
    {
    }

    /**
     * Test of isSparse method, of class SparseRealMatrix.
     */
    @Test
    public void testIsSparse()
    {
    }

    /**
     * Test of nonzeroIterator method, of class SparseRealMatrix.
     */
    @Test
    public void testNonzeroIterator()
    {
        SparseRealMatrix M = new SparseRealMatrix(10,10);
        int[][] data = {
            {0,0,5},
            {1,1,1},
            {1,2,2},
            {3,1,3},
            {4,6,4},
            {9,3,6},
        };

        for (int i=0; i<data.length; i++)
            M.set(data[i][0], data[i][1], data[i][2]);
        
        IDoubleIterator it = M.nonzeroIterator(); 
        IDoubleElement de = null;
        
        for (int i=0; i<data.length; i++)
        {
            Assert.assertEquals(it.hasNext(), true);
            de = it.next();
            Assert.assertEquals(de.row(), data[i][0]);
            Assert.assertEquals(de.column(), data[i][1]);
            Assert.assertEquals((int)de.get(), data[i][2]);
        }
        Assert.assertEquals(it.hasNext(), false);
        
    }

    /**
     * Test of main method, of class SparseRealMatrix.
     */
    @Test
    public void testMain()
    {
    }
}
