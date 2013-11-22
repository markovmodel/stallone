/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.stat;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IParameterEstimator
{
    public IParameterEstimator copy();

    public IDoubleArray estimate(IDataSequence data);

    public IDoubleArray estimate(IDataSequence data, IDoubleArray weights);

    public void initialize();

    public void initialize(IDoubleArray initPar);

    public void addToEstimate(IDataSequence data);

    public void addToEstimate(IDataSequence data, IDoubleArray weights);

    public IDoubleArray getEstimate();
}
