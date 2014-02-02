/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.function;

import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IFunction;
import stallone.api.function.IProductFunction;

/**
 *
 * @author noe
 */
public class ProductFunction implements IProductFunction
{
    private IFunction[] factors;
    
    public ProductFunction(IFunction... _factors)
    {
        this.factors = _factors;
    }

    @Override
    public IFunction[] factors()
    {
        return factors;
    }

    @Override
    public int getNumberOfVariables()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double f(double... x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double f(IDoubleArray x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
}
