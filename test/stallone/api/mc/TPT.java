package stallone.api.mc;

import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.mc.tpt.ITPTFlux;

public class TPT
{
    private IDoubleArray T;
    private IIntArray A, B;
    private ITPTFlux tptflux;
    
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
        
        IIntArray A = API.intsNew.arrayRange(1);
        IIntArray B = API.intsNew.arrayRange(1);
        
        this.A = A;
        this.B = B;
        this.T = C;
    }

    @Test
    public void testCreationAndCalculation()
    {
        this.tptflux = API.msmNew.createTPT(T, A, B);
        IDoubleArray backwardCommitor = tptflux.getBackwardCommittor();
        IDoubleArray forwardCommitor = tptflux.getForwardCommittor();
        IDoubleArray flux = tptflux.getFlux();
        IDoubleArray netFlux = tptflux.getNetFlux();
        double totalFlux = tptflux.getTotalFlux();
        double rate = tptflux.getRate();

        System.out.println("backward c: " + backwardCommitor + "\n"
                + "forward c: " + forwardCommitor + "\n" + "flux: " + flux
                + "\n" + "netflux: " + netFlux + "\n" + "totalflux: "
                + totalFlux + "\n" + "rate: " + rate);

    }

}
