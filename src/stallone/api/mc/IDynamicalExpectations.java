package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;

public interface IDynamicalExpectations
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
    public double calculatePerturbationExpectation(IDoubleArray p0,
            IDoubleArray a, double t);

    /**
     * Calculates the stationary autocorrelation of a
     * @param a
     */
    public double calculateAutocorrelation(IDoubleArray a, double t);

    /**
     *
     * Calculates the stationary cross-correlation of a and b
     * @param a
     * @param b
     */
    public double calculateCorrelation(IDoubleArray a, IDoubleArray b,
            double t);

}
