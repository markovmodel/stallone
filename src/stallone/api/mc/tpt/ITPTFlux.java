package stallone.api.mc.tpt;

import stallone.api.doubles.IDoubleArray;

public interface ITPTFlux
{
    public void calculate();
    public IDoubleArray getBackwardCommittor();
    public IDoubleArray getFlux();
    public IDoubleArray getForwardCommittor();
    public IDoubleArray getNetFlux();
    public double getRate();
    public IDoubleArray getStationaryDistribution();
    public double getTotalFlux();
    public void setRateMatrix(IDoubleArray K);
    public void setStationaryDistribution(IDoubleArray pi);
    public void setTransitionMatrix(IDoubleArray T);
}
