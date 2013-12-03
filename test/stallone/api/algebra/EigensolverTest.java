package stallone.api.algebra;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import stallone.algebra.extern.SparseArpackEigenvalueDecomposition;
import stallone.api.API;
import stallone.api.doubles.IDoubleArray;
import stallone.doubles.SparseRealMatrix;

public class EigensolverTest
{
    double tolerance = 1e-9;
    
    IEigenvalueSolver solver;
    @Before
    public void setUp() throws Exception
    {
        IDoubleArray m = new SparseRealMatrix(4, 4);
        m.set(0, 0, -16);
        m.set(0, 1, 9);

        m.set(1, 0, -12);
        m.set(1, 1, 5);

        m.set(2, 2, 6);
        m.set(2, 3, -2);

        m.set(3, 3, 4);
        
        System.out.println(m);
        
        solver = new SparseArpackEigenvalueDecomposition(m);
    }

    @Test
    public void test()
    {
        // TODO: this number is ignored.
        solver.setNumberOfRequestedEigenvalues(4);
        solver.setPerformRightComputation(true);
        solver.perform();
        IEigenvalueDecomposition result = solver.getResult();
        System.out.println("available pairs: " + result.availableEigenpairs());
        
        IDoubleArray ev_expected = API.doublesNew.arrayFrom(
                new double[]{-7, 6, 4,-4});
        IDoubleArray ev = API.doublesNew.array(4);
        System.out.println(result.getEval());
        result.getEval().copyInto(ev);
        API.doubles.sort(ev);
        
        assertArrayEquals(ev_expected.getArray(), ev.getArray(), tolerance);
        
        System.out.println("R:\n"+result.getRightEigenvectorMatrix());

    }

}
