/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.stat;

import static stallone.api.API.*;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.algebra.Algebra;
import stallone.api.doubles.IDoubleList;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.stat.modelselection.ExitTimeSplitter;

/**
 *
 * @author noe
 */
public class StatisticsUtilities
{    
    /**
    @return the mean (or average) value of the array
     */
    public double mean(IDoubleArray arr)
    {
        return (Doubles.util.sum(arr) / (double)arr.size());
    }
    
    /**
        @return the variance from the mean. Returns 0 for single-value arrays
     */
    public double variance(IDoubleArray arr)
    {
        if (arr.size() == 1)
        {
            return (0);
        }
        double meanval = mean(arr);
        double sum = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            double err = arr.get(i) - meanval;
            sum += err * err;
        }
        return (sum / (double)(arr.size() - 1));
    }

    /**
    @return the std dev from the mean
     */
    public double stdDev(IDoubleArray arr)
    {
        return (Math.sqrt(variance(arr)));
    }
    

    /**
    @return the arithmetic mean row
     */
    public IDoubleArray meanRow(IDoubleArray arr)
    {
        int nrows = arr.rows();
        int ncols = arr.columns();

        IDoubleArray res = Doubles.create.array(ncols);
        for (int i = 0; i < nrows; i++)
        {
            Algebra.util.addTo(res, arr.viewRow(i));
        }
        Algebra.util.scale(1.0 / (double) nrows, res);
        
        return(res);
    }
    
    /**
    @return the arithmetic mean row
     */
    public IDoubleArray meanColumn(IDoubleArray arr)
    {
        int nrows = arr.rows();
        int ncols = arr.columns();

        IDoubleArray res = Doubles.create.array(nrows);
        for (int i = 0; i < ncols; i++)
        {
            Algebra.util.addTo(res, arr.viewColumn(i));
        }
        Algebra.util.scale(1.0 / (double) ncols, res);
        
        return(res);
    }

    /** 
     * Bins the data
     * @param data
     * @param grid grid points. data is assigned to closest grid point
     * @return the histogram counts related to the specified grid
     */
    public IIntArray histogram(IDoubleArray data, IDoubleArray grid)
    {
        if (!doubles.isSorted(grid))
            throw new IllegalArgumentException("Grid passed to histogram must be sorted");
        
        int[] res = new int[grid.size()];
        
        for (int i=0; i<data.size(); i++)
        {
            res[doubles.findClosest(grid, data.get(i))]++;
        }
        
        return intsNew.arrayFrom(res);
    }
    
    
    /**
     * Returns the gaussian density at x
     * @param mean mean of the gaussian
     * @param stddev standard deviation of the gaussian
     * @param x
     * @return 
     */
    public double gaussianDensity(double mean, double variance, double x)
    {
	double dev = (mean - x);
	double f = 1/(Math.sqrt(2*Math.PI*variance)) *
	    Math.exp(-(dev*dev) / (2*variance));
	return(f);
    }    
    

    /**
     * Splits states with nonexponential lifetime distributions.
     * @param states for each event the state index of that event
     * @param lifetimes for each event its lifetime
     * @return the new state index assignment
     */
    public int[] splitNonexponentialLifetimes(IIntList states, IDoubleList lifetimes)
    {
        ExitTimeSplitter splitter = new ExitTimeSplitter();
        for (int i=0; i<states.size(); i++)
            splitter.add(states.get(i), lifetimes.get(i));
        splitter.run();
        return splitter.getNewStateAssignment();
    }

    /**
     * Splits states with nonexponential lifetime distributions.
     * @param states for each event the state index of that event
     * @param lifetimes for each event its lifetime
     * @return the new state index assignment
     */
    public int[] splitNonexponentialLifetimes(int[] states, double[] lifetimes)
    {
        ExitTimeSplitter splitter = new ExitTimeSplitter();
        for (int i=0; i<states.length; i++)
            splitter.add(states[i], lifetimes[i]);
        splitter.run();
        return splitter.getNewStateAssignment();
    }
}
