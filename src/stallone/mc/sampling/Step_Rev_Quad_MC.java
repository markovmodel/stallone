/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleArray;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class Step_Rev_Quad_MC implements IReversibleSamplingStep
{
    private int n;
    private IDoubleArray C;
    
    private IDoubleArray T;
    private IDoubleArray mu;
    
    private double Tii_backup, Tij_backup, Tji_backup, Tjj_backup;
    
    public Step_Rev_Quad_MC()
    {
    }
    
    @Override
    public void init(IDoubleArray _C, IDoubleArray _T, IDoubleArray _mu)
    {
        this.n = _C.rows();
        this.C = _C;
        
        this.T = _T;
        this.mu = _mu;
    }
    
    /**
     * Conducts a single reversible edge shift MC step @returns true if the step
     * has been accepted.
     */
    public boolean sampleQuad(int i, int j)
    {
        //double[] pic = TransitionMatrix.distribution(new DenseDoubleMatrix2D(T));
        double q = mu.get(j) / mu.get(i);
        double dmin = Math.max(-T.get(i, i), -q * T.get(j, j));
        double dmax = Math.min(T.get(i, j), q * T.get(j, i));

        if (dmin == dmax)
        {
            return (false);
        }

        if (dmin > dmax)
        {
            throw (new RuntimeException("Error during reversible edge shift in Transition Matrix Sampling: "
                    + "Have reached an inconsistency between elements. dmin > dmax with:"
                    + "dmin = Math.max(-T.get(i, i), -q * T.get(j, j))"
                    + dmin + " = Math.max(-" + T.get(i, i) + ", -" + q + " * " + T.get(j, j) + ")"
                    + "dmax = Math.min(T.get(i, j), q * T.get(j, i))"
                    + dmax + " = Math.min(" + T.get(i, j) + ", " + q + " * " + T.get(j, i) + ")"
                    + "at i = " + i + "   j = " + j));
        }

        double d1 = 0, d2 = 0;

        d1 = MathTools.randomDouble(dmin, dmax);
        d2 = d1 / q;

        double prop = Math.sqrt((((T.get(i, j) - d1) * (T.get(i, j) - d1)) + ((T.get(j, i) - d2) * (T.get(j, i) - d2)))
                / ((T.get(i, j) * T.get(i, j)) + (T.get(j, i) * T.get(j, i))));

        double pacc = prop
                * Math.pow((T.get(i, i) + d1) / T.get(i, i), C.get(i, i))
                * Math.pow((T.get(i, j) - d1) / T.get(i, j), C.get(i, j))
                * Math.pow((T.get(j, j) + d2) / T.get(j, j), C.get(j, j))
                * Math.pow((T.get(j, i) - d2) / T.get(j, i), C.get(j, i));

        boolean accept = Math.random() <= pacc;

        if (accept)
        {
            Tii_backup = T.get(i,i);
            Tij_backup = T.get(i,j);
            Tji_backup = T.get(j,i);
            Tjj_backup = T.get(j,j);
            
            T.set(i, j, T.get(i, j) + -d1);
            T.set(i, i, T.get(i, i) + (1.0 - doubles.sumRow(T, i)));
            T.set(j, i, T.get(i, j) / q);
            T.set(j, j, T.get(j, j) + (1.0 - doubles.sumRow(T, j)));

            // revert step if illegal
            if (!TransitionMatrixSamplingTools.isElementIn01(T,i,i) 
                    || !TransitionMatrixSamplingTools.isElementIn01(T,i,j) 
                    || !TransitionMatrixSamplingTools.isElementIn01(T,j,i) 
                    || !TransitionMatrixSamplingTools.isElementIn01(T,j,j))
            {
                T.set(i,i, Tii_backup);
                T.set(i,j, Tij_backup);
                T.set(j,j, Tjj_backup);
                T.set(j,i, Tji_backup);
                accept = false;
            }
        }

        return (accept);
    }    
    
    @Override
    public boolean step()
    {
        int i, j;
        do
        {
            i = MathTools.randomInt(0, n);
            j = MathTools.randomInt(0, n);
        }
        while (i == j);
        
        return sampleQuad(i,j);
    }
        
}
