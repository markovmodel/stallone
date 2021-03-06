/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.correlations;

import stallone.api.algebra.Algebra;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IDynamicalExpectationsSpectral;
import stallone.api.mc.MarkovModel;
import stallone.mc.StationaryDistribution;


/**
 * Calculates expectations and correlations of functions of states
 *
 * @author noe
 */
public class DynamicalExpectationsSpectral implements IDynamicalExpectationsSpectral
{
    // input
    private IDoubleArray T, K;
    private StationaryDistribution statdist = new StationaryDistribution();

    // stationary distribution
    private IDoubleArray pi;

    // spectral components
    private IDoubleArray timescales;
    private IDoubleArray R; // eigenvectors, normalized with sqrt()

    // result
    private IDoubleArray amplitudes;

    public DynamicalExpectationsSpectral(IDoubleArray M)
    {
        if (MarkovModel.util.isTransitionMatrix(M))
        {
            setT(M);
        }
        else if (MarkovModel.util.isTransitionMatrix(M))
        {
            setK(M);
        }
        else
        {
            throw(new IllegalArgumentException("Trying to construct DynamicalExpectationsSpectral with a Matrix that is neither a transition nor a rate matrix"));
        }
    }

    public DynamicalExpectationsSpectral()
    {
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#setT(stallone.api.doubles.IDoubleArray)
     */
    public final void setT(IDoubleArray _T)
    {
        this.K = null;
        this.T = _T;
        statdist.setT(_T);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#setK(stallone.api.doubles.IDoubleArray)
     */
    public final void setK(IDoubleArray _K)
    {
        this.K = _K;
        this.T = null;
        statdist.setK(_K);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#setStationaryDistribution(stallone.api.doubles.IDoubleArray)
     */
    public void setStationaryDistribution(IDoubleArray _pi)
    {
        pi = Doubles.create.array(_pi.getArray());
    }


    private void calculateAlgebra()
    {
        if (K==null && T==null)
            throw(new RuntimeException("Trying to calculate dynamical expectations before setting T or K."));

        // calculates the stationary distribution if not given
        if (pi == null)
        {
            pi = Doubles.create.array(statdist.calculate().getArray());
        }

        // eigenvalues and timescales
        if (T != null)
        {
            IEigenvalueDecomposition evd = Algebra.util.evd(T);
            IDoubleArray evalT = evd.getEval();
            timescales = Doubles.create.array(evalT.size());
            for (int i=0; i<timescales.size(); i++)
                timescales.set(i, -1/Math.log(Math.abs(evalT.get(i))));

            R = evd.getRightEigenvectorMatrix();
        }
        else
        {
            IEigenvalueDecomposition evd = Algebra.util.evd(K);
            IDoubleArray evalK = evd.getEval();
            timescales = Doubles.create.array(evalK.size());
            for (int i=0; i<timescales.size(); i++)
                timescales.set(i, -1/Math.abs(evalK.get(i)));

            R = evd.getRightEigenvectorMatrix();
        }

        // normalize eigenvectors
        for (int i=0; i<R.columns(); i++)
        {
            IDoubleArray ri = R.viewColumn(i);
            double s = Math.sqrt(Algebra.util.dot(ri, ri, pi));

            for (int j=0; j<R.size(); j++)
                R.set(j,i, R.get(j,i) / s);
        }
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#calculatePerturbationExpectation(stallone.api.doubles.IDoubleArray, stallone.api.doubles.IDoubleArray)
     */
    public void calculatePerturbationExpectation(IDoubleArray p0, IDoubleArray a)
    {
        if (pi == null || timescales == null || R == null)
            calculateAlgebra();

        amplitudes = Doubles.create.array(timescales.size());
        for (int i=0; i<amplitudes.size(); i++)
        {
            double a1 = Algebra.util.dot(p0, R.viewColumn(i));
            double a2 = Algebra.util.dot(a, R.viewColumn(i), pi);
            amplitudes.set(i, a1*a2);
        }
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#calculateAutocorrelation(stallone.api.doubles.IDoubleArray)
     */
    public void calculateAutocorrelation(IDoubleArray a)
    {
        if (pi == null || timescales == null || R == null)
            calculateAlgebra();

        amplitudes = Doubles.create.array(timescales.size());
        for (int i=0; i<amplitudes.size(); i++)
        {
            double a1 = Algebra.util.dot(a, R.viewColumn(i), pi);
            amplitudes.set(i, a1*a1);
        }
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#calculateCorrelation(stallone.api.doubles.IDoubleArray, stallone.api.doubles.IDoubleArray)
     */
    public void calculateCorrelation(IDoubleArray a, IDoubleArray b)
    {
        if (pi == null || timescales == null || R == null)
            calculateAlgebra();

        amplitudes = Doubles.create.array(timescales.size());
        for (int i=0; i<amplitudes.size(); i++)
        {
            double a1 = Algebra.util.dot(a, R.viewColumn(i), pi);
            double a2 = Algebra.util.dot(b, R.viewColumn(i), pi);
            amplitudes.set(i, a1*a2);
        }
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#getAmplitudes()
     */
    public IDoubleArray getAmplitudes()
    {
        return(amplitudes);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#getTimescales()
     */
    public IDoubleArray getTimescales()
    {
        return(timescales);
    }

    /* (non-Javadoc)
     * @see stallone.mc.correlations.IDynamicalExpectationsSpectral#getValue(double)
     */
    public double getValue(double t)
    {
        double res = 0;
        for (int i=0; i<timescales.size(); i++)
        {
            res += amplitudes.get(i) * Math.exp(-t/timescales.get(i));
        }
        return(res);
    }

}
