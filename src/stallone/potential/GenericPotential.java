/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IFunctionC1;

/**
 *
 * @author noe
 */
public class GenericPotential extends AbstractPotential
{
    private IFunctionC1 function;
    private IDoubleArray x;
    private double f;
    private IDoubleArray grad;

    public GenericPotential(IFunctionC1 _function)
    {
        this.function = _function;
    }

    @Override
    public void setCoordinates(IDoubleArray coordinates)
    {
        this.x = coordinates;
    }

    @Override
    public boolean calculate()
    {
        f = function.f(x);
        grad = function.grad(x);
        return(true);
    }

    @Override
    public int getNDimensions()
    {
        return(function.getNumberOfVariables());
    }

    @Override
    public double getEnergy()
    {
        return(f);
    }

    @Override
    public IDoubleArray getGradient()
    {
        return(grad);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return(x);
    }

    @Override
    public int getNumberOfVariables()
    {
        return(function.getNumberOfVariables());
    }

}
