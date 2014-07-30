

package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;
import java.lang.Math;

/**
 *
 * @author cwehmeyer
 */
public abstract class DeltaGGaussian extends DeltaGDistributionAbstract
{

	private double mu;
	private double sigma;

	private int stateA;
	private int stateB;

	public DeltaGGaussian( double _mu, double _sigma, int _A, int _B )
	{
		mu = _mu;
		sigma = _sigma;
		stateA = _A;
		stateB = _B;
	}

	@Override
	public boolean accept( IDoubleArray pi, double randomNumber )
	{
		double diff = Math.log( pi[stateA] / pi[stateB] ) - mu;
		double dbs = diff / sigma;
		double model = Math.exp( -0.5 * dbs * dbs );
		if ( randomNumber < model )
			return true;
		return false;
	}
	
}