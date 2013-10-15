package stallone.api.mc.tpt;

import stallone.api.doubles.IDoubleArray;

public interface ICommittor
{
    public IDoubleArray backwardCommittor();
    public IDoubleArray forwardCommittor();
    public void setRateMatrix(IDoubleArray K);
    public void setStationaryDistribution(IDoubleArray pi);
    public void setTransitionMatrix(IDoubleArray T);
}
