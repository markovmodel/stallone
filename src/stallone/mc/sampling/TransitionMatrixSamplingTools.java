/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class TransitionMatrixSamplingTools
{
    /**
    Checks whether the given element is still within [0,1] or else puts it back to that
    value.
     */
    public static void ensureValidElement(IDoubleArray T, int i, int j)
    {
        if (T.get(i, j) < 0)
        {
            T.set(i, j, 0);
        }
        if (T.get(i, j) > 1)
        {
            T.set(i, j, 1);
        }
    }

    public static boolean isElementIn01(IDoubleArray T, int i, int j)
    {
        if (T.get(i, j) < 0)
        {
            return false;
        }
        if (T.get(i, j) > 1)
        {
            return false;
        }
        return true;
    }

    public static boolean isRowIn01(IDoubleArray T, int i)
    {
        for (int j=0; j<T.columns(); j++)
        {
            if (T.get(i, j) < 0)
            {
                return false;
            }
            if (T.get(i, j) > 1)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 
     * @param mu invariant density
     * @return 
     */
    public static double computeDetailedBalanceError(IDoubleArray T, IDoubleArray mu)
    {
        double err = 0;
        for (int i=0; i<T.rows(); i++)
        {
            for (int j=0; j<T.columns(); j++)
            {
                err += Math.abs(mu.get(i)*T.get(i,j) - mu.get(j)*T.get(j,i));
            }
        }
        return err;
    }
    
    /**
    Makes sure that the row still sums up to 1.
     */
    public static void ensureValidRow(IDoubleArray T, int i)
    {
        IDoubleArray r = T.viewRow(i);
        Algebra.util.scale(1.0 / Doubles.util.sum(r), r);
    }
    
}
