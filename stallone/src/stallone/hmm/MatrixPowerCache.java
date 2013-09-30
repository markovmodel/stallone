/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class MatrixPowerCache
{
    IDoubleArray T1;
    IDoubleArray[] Tn;
    
    /**
     * 
     * @param nmaxCache the maximum power kept in the cache. Powers greater than nmaxCache will be calculated every time requested.
     */
    public MatrixPowerCache(int nmaxCache)
    {
        if (nmaxCache > 1000)
            throw(new IllegalArgumentException("Trying to construct a transition matrix power cache with n > 1000. This is probably not be a good idea. Stopping."));
        
        Tn = new IDoubleArray[nmaxCache+1];
    }
    
    public MatrixPowerCache()
    {
        this(100);
    }
    
    private void computePowers()
    {
        Tn[0] = Doubles.create.identity(T1.rows());
        Tn[1] = T1.copy();
        for (int i=2; i<Tn.length; i++)
            Tn[i] = Algebra.util.product(Tn[i-1], T1);
    }
    
    public IDoubleArray getPower(IDoubleArray _T1, int n)
    {
        if (T1 != _T1)
        {
            T1 = _T1;
            computePowers();
        }
        
        if (n < Tn.length)
            return(Tn[n]);
        else
        {
            return(Algebra.util.power(_T1,n));
        }
    }
    
    public double getPowerElement(IDoubleArray _T1, int n, int i, int j)
    {
        return(getPower(_T1,n).get(i,j));
    }
}
