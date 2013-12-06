/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.algebra;

import static stallone.api.API.*;

import org.junit.*;
import static org.junit.Assert.*;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class InnerProductTest
{
    
    public InnerProductTest()
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
     * Test of innerProduct method, of class InnerProduct.
     */
    @Test
    public void testInnerProduct_IDoubleArray_IDoubleArray()
    {
    }

    /**
     * Test of innerProductSparseSparse method, of class InnerProduct.
     */
    @Test
    public void testInnerProductSparseSparse_IDoubleArray_IDoubleArray()
    {
    }

    /**
     * Test of innerProductSparseDense method, of class InnerProduct.
     */
    @Test
    public void testInnerProductSparseDense_IDoubleArray_IDoubleArray()
    {
        IDoubleArray a = doublesNew.sparseRow(100);
        for (int i=0; i<10; i++)
            a.set(i, Math.random());
        IDoubleArray b = doublesNew.row(100);
        doubles.fill(b, 1);

        InnerProduct ip = new InnerProduct(true);
        double abSparse = ip.innerProductSparseDense(a,b);
        double abDense = ip.innerProduct(a,b);
        
        Assert.assertTrue(Math.abs(abSparse-abDense) < 1e-8);
    }

    /**
     * Test of innerProduct method, of class InnerProduct.
     */
    @Test
    public void testInnerProduct_3args_1()
    {
    }

    /**
     * Test of innerProductSparseDense method, of class InnerProduct.
     */
    @Test
    public void testInnerProductSparseDense_3args()
    {
    }

    /**
     * Test of innerProductSparseSparse method, of class InnerProduct.
     */
    @Test
    public void testInnerProductSparseSparse_3args()
    {
    }

    /**
     * Test of innerProduct method, of class InnerProduct.
     */
    @Test
    public void testInnerProduct_4args()
    {
    }

    /**
     * Test of innerProduct method, of class InnerProduct.
     */
    @Test
    public void testInnerProduct_3args_2()
    {
    }
}
