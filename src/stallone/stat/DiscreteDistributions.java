/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import stallone.api.doubles.IDoubleArray;

/**
 * Samples from a stochastic matrix
 * @author noe
 */
public class DiscreteDistributions
{
    DiscreteDistribution_Old[] dd;

    public DiscreteDistributions(double[][] p)
    {
        dd = new DiscreteDistribution_Old[p.length];
        for (int i=0; i<dd.length; i++)
            dd[i] = new DiscreteDistribution_Old(p[i]);
    }

    public DiscreteDistributions(IDoubleArray p)
    {
        this(p.getTable());
    }

    public int sample(int s)
    {
        return(dd[s].sample());
    }
}
