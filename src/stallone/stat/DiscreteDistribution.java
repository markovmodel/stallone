/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DiscreteDistribution
{

    private double[] p, pinc;

    public DiscreteDistribution(double[] _p)
    {
        p = _p;

        pinc = new double[_p.length];
        pinc[0] = _p[0];
        for (int j = 1; j < pinc.length; j++)
        {
            pinc[j] = pinc[j - 1] + _p[j];
        }
    }
    
    public DiscreteDistribution(IDoubleArray arr)
    {
        this(arr.getArray());
    }

    /**
     *
     * @param c current state
     * @return next state
     */
    public int sample()
    {
        double r = Math.random();
        int to = 0;
        for (; to < pinc.length && pinc[to] <= r; to++);
        return (to);
    }
}
