/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import stallone.api.algebra.*;
import stallone.api.complex.IComplexArray;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IMarkovPropagator;


/**
 *
 * @author noe
 */
public final class RateMatrixPropagator implements IMarkovPropagator
{
    // input T
    private IDoubleArray K;
    private boolean reversible;

    private IDoubleArray R,L;
    private IDoubleArray evalReal;

    public RateMatrixPropagator(IDoubleArray _K)
    {
        set(_K);
    }

    /**
     * Sets the basic propagator
     * @param _P the propagator
     */
    @Override
    public void set(IDoubleArray _K)
    {
        this.K = _K;

        IEigenvalueDecomposition evd = Algebra.util.evd(_K);
        IComplexArray evComplex = evd.getEval();
        if (evComplex.isReal())
        {
            evalReal = evComplex;
            L = evd.getLeftEigenvectorMatrix();
            R = evd.getRightEigenvectorMatrix();
        }
        else
            reversible = false;
    }

    @Override
    public IDoubleArray propagate(double t)
    {
        IDoubleArray res = null;

        if (reversible)
        {
            IDoubleArray evalPower = Doubles.create.array(evalReal.size());
            for (int i=0; i<evalPower.size(); i++)
                evalPower.set(i, Math.pow(evalReal.get(i),t));

            IDoubleArray D = Doubles.create.diag(evalPower);
            res = Algebra.util.product(R, Algebra.util.product(D, L));
        }
        else
        {
            throw(new IllegalArgumentException("Trying to use a nonreversible rate matrix propagator. This is not implemented yet."));
        }

        return(res);
    }
}
