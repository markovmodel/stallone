package stallone.api.mc;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.mc.tpt.ITPTFlux;
import stallone.mc.tpt.PathwayDecomposition;
import stallone.mc.tpt.TPTFlux;

public class TestPathwayDecomposition
{

    IDoubleArray F;
    IDoubleArray Q;
    IIntArray A, B;
    
    @Before
    public void setUp() throws Exception
    {
        IDoubleArray C = API.doublesNew.matrix(new double[][] 
                {{6000, 3, 0, 0, 0, 0},
                 {3, 1000, 3, 0, 0, 0},
                 {0, 3, 1000, 3, 0, 0},
                 {0, 0, 3, 1000, 3, 0},
                 {0, 0, 0, 3, 1000, 3},
                 {0, 0, 0, 0, 3, 90000}});
        
        for (int i = 0; i < C.rows(); i++)
        {
            double[] row = C.getRow(i);
            double sum = 0;
            for (int j = 0; j < row.length; j++) {
                sum += row[j];
            }
            
            for (int j = 0; j < row.length; j++)
            {
                row[j] /= sum;
            }
        }
        
        A = API.intsNew.array(1, 0);
        B = API.intsNew.array(1, 5);
//        A = API.intsNew.arrayFrom(new int[] {1,2,3});
//        B = API.intsNew.arrayFrom(new int[] {5});
        
        ITPTFlux flux = API.msmNew.createTPT(C, A, B);
        F = flux.getFlux();
        Q = flux.getForwardCommittor();
    }

    @Test
    public void test()
    {
        PathwayDecomposition decomp = new PathwayDecomposition(F, Q.getArray(),
                                                A.getArray(), B.getArray());
        // FIXME: why is this null in the first call? did we miss a function call before this?
        int[] nextPathway = decomp.nextPathway();
        if (nextPathway != null) {
            BigDecimal computeCurrentFlux = decomp.computeCurrentFlux();
        } else
        {
            System.out.println("no more pathways");
        }
    }

}
