package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;

public interface IDynamicalExpectationsSpectral
{

    public void setT(IDoubleArray _T);

    public void setK(IDoubleArray _K);

    /**
     * Optional. 
     */
    public void setStationaryDistribution(IDoubleArray _pi);

    /**
     * Calculates the expectation value of a when the ensemble starts a p0 and relaxes towards the stationary distribution
     * @param a
     */
    public void calculatePerturbationExpectation(IDoubleArray p0,
            IDoubleArray a);

    /**
     * Calculates the stationary autocorrelation of a
     * @param a 
     */
    public void calculateAutocorrelation(IDoubleArray a);

    /**
     * 
     * Calculates the stationary cross-correlation of a and b
     * @param a
     * @param b 
     */
    public void calculateCorrelation(IDoubleArray a, IDoubleArray b);

    public IDoubleArray getAmplitudes();

    public IDoubleArray getTimescales();

    public double getValue(double t);

}