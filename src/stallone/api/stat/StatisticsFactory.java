/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.stat;

import stallone.stat.*;

/**
 *
 * @author noe
 */
public class StatisticsFactory
{
    public RunningAverage runningAverage()
    {
        return(new RunningAverage());
    }    
    
    public IParameterEstimator parameterEstimatorGaussian1D()
    {
        return new GaussianUnivariate(0,0);
    }
}
