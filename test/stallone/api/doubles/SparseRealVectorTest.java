package stallone.api.doubles;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
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
        vec.set(5, 23);
        vec.set(8, 42);
        vec.set(0, 7);
        
        Assert.assertEquals(7, vec.get(0), tolerance);
        Assert.assertEquals(23, vec.get(5), tolerance);
        Assert.assertEquals(42, vec.get(8), tolerance);

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
        
        // modify copy and ensure vec is not beeing modified.
        copy.set(0, 42);
        Assert.assertEquals(23, vec.get(0), tolerance);
        
        copy.zero();
        Assert.assertEquals(23, vec.get(0), tolerance);
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
        class result {
            public result(double v, int i, int j) {
                this.v=v; this.i=i;this.j=j;
            }
            double v;
            int i, j;
            
            public boolean equals(Object obj) {
                if (obj instanceof result)
                {
                    result r = (result) obj;
                    return i == r.i && j == r.j && (Math.abs(v - r.v) < tolerance);
                }
                return false;
            }
            
            public String toString() {
                return "("+ i + "," + j + ") = " + v;
            }
        }
        
        List<result> results = new ArrayList<result>();
        List<result> expected = new ArrayList<result>();
        
        // note that the indices have to be sorted, since the iterator moves
        // from beginning to end.
        vec.set(0, 7);
        vec.set(5, 23);
        vec.set(8, 42);
        
        expected.add(new result(7f, 0, 0));
        expected.add(new result(23f, 5, 0));
        expected.add(new result(42f, 8, 0));
        
        for (IDoubleIterator it = vec.nonzeroIterator(); it.hasNext();) {
            IDoubleElement e = it.next();
            results.add(new result(e.get(), e.row(), e.column()));
        }
        
        Assert.assertEquals(expected, results);
    }
}
