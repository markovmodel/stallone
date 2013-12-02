package stallone.api.doubles;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.doubles.IDoubleArray;
import stallone.doubles.SparseRealVector;

public class SparseRealVectorTest
{

    IDoubleArray vec;
    int size = 10;
    double tolerance = 1e-9;
    
    private int getNNZ(IDoubleArray a) {
        return ((SparseRealVector)vec).getNumberOfNonzero();
    }

    @Before
    public void setUp() throws Exception
    {
        vec = API.doublesNew.sparseColumn(size);
        Assert.assertTrue("size have to match", vec.size() == size);
    }

    @Test
    public void testSet()
    {
        vec.set(0, 23);
        Assert.assertEquals(23, vec.get(0), tolerance);
    }
    
    @Test
    public void testGetNumberNNZ() {
        vec.set(0, 23);
        SparseRealVector casted = (SparseRealVector) vec;
        int nnz = casted.getNumberOfNonzero();
        Assert.assertEquals(nnz, 1);
        
        vec.set(5, 23);
        vec.set(7, 23);
        
        nnz = casted.getNumberOfNonzero();
        Assert.assertEquals(nnz, 3);
    }

    @Test
    public void testMultiply()
    {
        vec.set(0, 23);
        IDoubleArray b = API.doublesNew.sparseColumn(size);
        b.set(0, 1);
        IDoubleArray result = API.alg.multiplyElementsToNew(vec, b);
        Assert.assertEquals(23f, result.get(0), tolerance);
    }

    /**
     * test if vector can be properly converted to a dense raw double array.
     */
    @Test
    public void testGetArray()
    {
        double[] expected = new double[] { 0, 0, 0, 23, 0, 0, 0, 0, 0, 0 };
        vec.set(3, 23.);
        Assert.assertArrayEquals(expected, vec.getArray(), tolerance);
    }
    
    @Test
    public void testCopy() {
        vec.set(0, 23);
        IDoubleArray copy = vec.copy();
        // vec.nnz == copy.nnz
        Assert.assertEquals(getNNZ(vec), getNNZ(copy));
        // vec[0] == copy[0]
        Assert.assertEquals(vec.get(0), copy.get(0), tolerance);
    }
    
    @Test
    public void testZero() {
        vec.set(0, 23);
        vec.zero();
        
        Assert.assertEquals(0, vec.get(0), tolerance);
        Assert.assertEquals(0, getNNZ(vec));
    }
    
    @Test 
    public void testNonzeroIterator() {
        vec.set(5, 23);
        for (IDoubleIterator it = vec.nonzeroIterator(); it.hasNext(); it.advance()) {
            int i = it.row();
            int j = it.column();
            System.out.println("i:  " + i + "; j : " + j);
            double v = it.get();
            Assert.assertEquals(23f, v, tolerance);
        }
    }
}
