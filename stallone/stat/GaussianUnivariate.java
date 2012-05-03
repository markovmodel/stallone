/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class GaussianUnivariate implements IParametricFunction, IParameterEstimator
{
    private IDoubleArray parameters; // mean and variance
    
    // pre-calculated for speed-up
    private double a,b;

    public GaussianUnivariate(IDoubleArray _parameters)
    {
        parameters = _parameters;
        precalc();
    }
    
    public GaussianUnivariate(double mean, double variance)
    {
        parameters = Doubles.create.arrayFrom(mean, variance);
        precalc();
    }
    
    final private void precalc()
    {
        a = Math.sqrt(1.0/(parameters.get(1)*2*Math.PI));
        b = - 1.0 / (2.0 * parameters.get(1));
    }
    
    @Override
    public IDoubleArray getParameters()
    {
        return parameters;
    }

    @Override
    public void setParameters(IDoubleArray par)
    {
        parameters = par;
        precalc();
    }

    @Override
    public int getNumberOfVariables()
    {
        return(1);
    }

    @Override
    public double f(double... x)
    {
        double dx = x[0]-parameters.get(0);
        return (a * Math.exp(b*dx*dx));
    }

    @Override
    public double f(IDoubleArray x)
    {
        double dx = x.get(0)-parameters.get(0);
        return (a * Math.exp(b*dx*dx));
    }

    @Override
    public IDoubleArray estimate(IDataSequence data)
    {
        IDoubleArray par = Doubles.create.array(2);

        // mean
        double mean = 0;
        for (IDoubleArray a : data)
            mean += a.get(0);
        mean /= (double)data.size();

        // var
        double var = 0;
        for (IDoubleArray a : data)
        {
            double d = a.get(0) - mean;
            var += d*d;
        }
        var /= (double)(data.size()-1);
        
        par.set(0, mean);
        par.set(1, var);
        
        return par;        
    }

    @Override
    public IDoubleArray estimate(IDataSequence data, IDoubleArray weights)
    {
        IDoubleArray par = Doubles.create.array(2);
        double W = 0;
        
        // mean
        double mean = 0;
        for (int i=0; i<data.size(); i++)
        {
            mean += weights.get(i) * data.get(i).get(0);
            W += weights.get(i);
        }
        mean /= W;

        // var
        double var = 0;
        for (int i=0; i<data.size(); i++)
        {
            double d = data.get(i).get(0) - mean;
            var += weights.get(i) * d*d;
        }
        var /= W-1;
        
        par.set(0, mean);
        par.set(1, var);
        
        return par;        
    }

    private double sumRunning, sumSquaredRunning, totalWeightRunning;
    
    @Override
    public void initialize()
    {
        sumRunning = 0;
        sumSquaredRunning = 0;
        totalWeightRunning = 0;
    }

    @Override
    public void initialize(IDoubleArray initPar)
    {
        sumRunning = 0;
        sumSquaredRunning = 0;
        totalWeightRunning = 0;
    }

    @Override
    public void addToEstimate(IDataSequence data)
    {
        for (IDoubleArray arr : data)
        {
            double x = arr.get(0);
            sumRunning += x;
            sumSquaredRunning += x*x;
            totalWeightRunning += 1.0;
        }
    }

    @Override
    public void addToEstimate(IDataSequence data, IDoubleArray weights)
    {
        for (int i=0; i<data.size(); i++)
        {
            double x = data.get(i).get(0);
            double w = weights.get(i);
            sumRunning += w*x;
            sumSquaredRunning += w*x*x;
            totalWeightRunning += w;
        }
    }

    @Override
    public IDoubleArray getEstimate()
    {
        double mean = sumRunning / totalWeightRunning;
        double var = sumSquaredRunning / totalWeightRunning - mean*mean;
        
        return Doubles.create.arrayFrom(mean, var);
    }

    @Override
    public GaussianUnivariate copy()
    {
        return(new GaussianUnivariate(parameters.get(0), parameters.get(1)));
    }
}
