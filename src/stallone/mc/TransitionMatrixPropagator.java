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
public final class TransitionMatrixPropagator implements IMarkovPropagator
{
    // input T
    private IDoubleArray T;
    private boolean reversible;
    
    private IDoubleArray R,L;
    private IDoubleArray evalReal;

    public TransitionMatrixPropagator(IDoubleArray _T)
    {
        set(_T);
    }
    
    /**
     * Sets the basic propagator
     * @param _P the propagator
     */
    @Override
    public void set(IDoubleArray _P)
    {
        this.T = _P;
        
        IEigenvalueDecomposition evd = Algebra.util.evd(_P);
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
            if (((int)t - t) != 0)
                throw(new IllegalArgumentException("Can only use integer times for propagating transition matrices. Attempted t = "+t));
            
            res =   Algebra.util.power(T, (int)t);
        }
        
        return(res);
    }
}
