/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.algebra.Algebra;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.MarkovModel;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class TransitionMatrixSamplerRevFixPi extends TransitionMatrixSamplerRev
{

    public TransitionMatrixSamplerRevFixPi(IDoubleArray counts, IDoubleArray piFixed)
    {
        super(counts, MarkovModel.util.estimateTrev(eraseNegatives(counts), piFixed));
        super.pi = piFixed;
    }
    
    @Override
    protected boolean step()
    {
        return (stepReversibleEdgeShiftFixPi());
    }

    public boolean stepReversibleEdgeShiftFixPi()
    {
        int i, j;
        do
        {
            i = MathTools.randomInt(0, T.rows());
            j = MathTools.randomInt(0, T.rows());
        }
        while (i == j);

        //double[] pic = TransitionMatrix.distribution(new DenseDoubleMatrix2D(T));
        double q = pi.get(j) / pi.get(i);
        double dmin = Math.max(-T.get(i, i), -q * T.get(j, j));
        double dmax = Math.min(T.get(i, j), q * T.get(j, i));

        double d1 = MathTools.randomDouble(dmin, dmax);
        double d2 = d1 / q;
        
        double pacc =
                Math.pow((T.get(i, i) + d1) / T.get(i, i), C.get(i, i))
                * Math.pow((T.get(i, j) - d1) / T.get(i, j), C.get(i, j))
                * Math.pow((T.get(j, j) + d2) / T.get(j, j), C.get(j, j))
                * Math.pow((T.get(j, i) - d2) / T.get(j, i), C.get(j, i));

        if (Math.random() <= pacc)
        {
            T.set(i, i, T.get(i,i)+d1);
            T.set(i, j, T.get(i,j)-d1);
            T.set(j, j, T.get(j,j)+d2);
            T.set(j, i, T.get(j,i)-d2);
            
            // numerical corrections:
            validateElement(i, j);
            validateElement(i, i);
            validateElement(j, i);
            validateElement(j, j);
            if (Math.random() < 0.0001) // do a row rescaling every 10000 steps
            {
                validateRow(i);
                validateRow(j);
            }
            
            return (true);
        }
        else
        {
            return (false);
        }
    }
}
