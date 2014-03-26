

package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author cwehmeyer
 */
public abstract class DeltaGDistributionAbstract
{
	public boolean accept( IDoubleArray pi, double randomNumber )
	{
		return true;
	}
}