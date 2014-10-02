

package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author cwehmeyer
 */
public interface IDeltaGDistribution
{
    /**
     * accepts or rejects a transition matrix sample
     * @param stationary distribution, random number
     * @return
     */
    public boolean accept( IDoubleArray pi, double randomNumber );
}