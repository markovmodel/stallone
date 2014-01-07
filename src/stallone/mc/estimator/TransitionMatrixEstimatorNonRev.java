/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.mc.estimator;

import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.mc.ITransitionMatrixEstimator;

/**
 *
 * @author noe
 */
public class TransitionMatrixEstimatorNonRev implements ITransitionMatrixEstimator
{
    private IDoubleArray C;
    private IDoubleArray T;

    public TransitionMatrixEstimatorNonRev(IDoubleArray _C)
    {
        this.C = _C;
    }

    public TransitionMatrixEstimatorNonRev()
    {
    }

    @Override
    public void setCounts(IDoubleArray _C)
    {
        this.C = _C;
    }

    @Override
    public void estimate()
    {
        this.T = C.create(C.rows(), C.columns());

        // row counts
        double[] rowsums = new double[T.rows()];

        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            rowsums[it.row()] += it.get();
        }

        // divide
        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            T.set(it.row(), it.column(), it.get()/rowsums[it.row()]);
        }
    }

    @Override
    public IDoubleArray getTransitionMatrix()
    {
        return(T);
    }

    @Override
    public void setMaxIter(int nmax)
    {
    }

    @Override
    public void setConvergence(int niter)
    {
    }

    @Override
    public double[] getLikelihoodHistory()
    {
        return new double[]{msm.logLikelihood(T, C)};
    }

}
