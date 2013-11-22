/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

/**
 *
 * @author noe
 */
public class RunningAverage
{
    private double mean=0, variance=0;
    private int n=0;

    public RunningAverage()
    {
    }

    /**
     * Adds a data point
     * @param x
     */
    public void add(double x)
    {
	double newmean = mean + (x-mean)/(n+1);
	double newvar  = 0;
	if (n>0)
	    newvar = ((n-1)*variance + (x-newmean)*(x-mean))/(n);

        mean = newmean;
        variance = newvar;
        n++;
    }

    public double getMean()
    {
        return(mean);
    }

    public double getStandardDeviation()
    {
        return(Math.sqrt(variance));
    }

    public double getVariance()
    {
        return(variance);
    }

    public int getN()
    {
        return(n);
    }


}
