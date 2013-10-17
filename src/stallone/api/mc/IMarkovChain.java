package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

public interface IMarkovChain
{

    public abstract void setStartingState(int _s);

    public abstract void setStartingDistribution(IDoubleArray _p0);

    public abstract IDoubleArray getTransitionMatrix();

    /**
     * @param s starting state
     * @param N total trajectory length
     * @param nskip number of steps between saved transitions
     * @return random trajectory of length N/nskip + 1
     */
    public abstract IIntArray randomTrajectory(int N);

    /**
     * @param startingState
     * @param terminalState
     * @param nskip number of steps between saved transitions
     * @return random trajectory of length N/nskip + 1
     */
    public abstract IIntArray randomTrajectoryToState(int[] endStates);

    /**
     * Generates a random Trajectory of length N starting from s using the
     * transfer operator
     */
    public abstract void printRandomTrajectory(int N);

}