/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.correlations;

import stallone.mc.*;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IMarkovPropagator;
import stallone.api.mc.MarkovModel;
import stallone.api.mc.IDynamicalExpectations;


/**
 * Calculates expectations and correlations of functions of states
 *
 * @author noe
 */
public class DynamicalExpectations implements IDynamicalExpectations
{
    // propagator
    private IMarkovPropagator prop;

    // stationary distribution
    private StationaryDistribution statdist = new StationaryDistribution();
    private IDoubleArray pi;

    public DynamicalExpectations(IDoubleArray M)
    {
        if (MarkovModel.util.isTransitionMatrix(M))
        {
            setT(M);
        }
        else if (MarkovModel.util.isRateMatrix(M))
        {
            setK(M);
        }
        else
        {
            throw(new IllegalArgumentException("Trying to construct DynamicalExpectationsSpectral with a Matrix that is neither a transition nor a rate matrix"));
        }

        pi = Doubles.create.array(M.rows());
    }

    public DynamicalExpectations()
    {
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#setT(stallone.api.doubles.IDoubleArray)
     */
    public final void setT(IDoubleArray _T)
    {
        prop = new TransitionMatrixPropagator(_T);
        statdist.setT(_T);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#setK(stallone.api.doubles.IDoubleArray)
     */
    public final void setK(IDoubleArray _K)
    {
        prop = new RateMatrixPropagator(_K);
        statdist.setK(_K);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#setStationaryDistribution(stallone.api.doubles.IDoubleArray)
     */
    public void setStationaryDistribution(IDoubleArray _pi)
    {
        pi = Doubles.create.array(_pi.getArray());
    }


    private void calculateAlgebra()
    {
        if (prop==null)
            throw(new RuntimeException("Trying to calculate dynamical expectations before setting T or K."));

        // calculates the stationary distribution if not given
        if (pi == null)
        {
            pi = statdist.calculate();
        }
    }


    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#calculatePerturbationExpectation(stallone.api.doubles.IDoubleArray, stallone.api.doubles.IDoubleArray, double)
     */
    public double calculatePerturbationExpectation(IDoubleArray p0, IDoubleArray a, double t)
    {
        if (pi == null)
            calculateAlgebra();

        IDoubleArray P = prop.propagate(t);

        double res = 0;
        for (int i=0; i<P.rows(); i++)
        {
            for (int j=0; j<P.columns(); j++)
            {
                res += p0.get(i) * P.get(i,j) * a.get(j);
            }
        }

        return(res);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#calculateAutocorrelation(stallone.api.doubles.IDoubleArray, double)
     */
    public double calculateAutocorrelation(IDoubleArray a, double t)
    {
        return(calculateCorrelation(a,a,t));
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectations#calculateCorrelation(stallone.api.doubles.IDoubleArray, stallone.api.doubles.IDoubleArray, double)
     */
    public double calculateCorrelation(IDoubleArray a, IDoubleArray b, double t)
    {
        if (pi == null)
            calculateAlgebra();

        IDoubleArray P = prop.propagate(t);

        double res = 0;
        for (int i=0; i<P.rows(); i++)
        {
            for (int j=0; j<P.columns(); j++)
            {
                res += a.get(i) * pi.get(i) * P.get(i,j) * b.get(j);
            }
        }

        return(res);
    }

}
