/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.stat;

import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleList;
import stallone.api.ints.IIntList;
import stallone.stat.*;
import stallone.stat.modelselection.ExitTimeSplitter;

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
    
    public ExitTimeSplitter exitTimeSplitter(IIntList states, IDoubleList lifetimes)
    {
        ExitTimeSplitter splitter = new ExitTimeSplitter();
        for (int i=0; i<states.size(); i++)
            splitter.add(states.get(i), lifetimes.get(i));
        
        return splitter;
    }    
    
    public IDiscreteDistribution discreteDistribution(IDoubleArray probabilities)
    {
        return new DiscreteDistribution_Old(probabilities);
    }
}
