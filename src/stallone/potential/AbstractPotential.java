/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.potential.IEnergyModel;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IFunctionC1;
import stallone.doubles.PrimitiveDoubleArray;

/**
 *
 * @author noe
 */
public abstract class AbstractPotential
    implements IEnergyModel, IFunctionC1
{
    PrimitiveDoubleArray tmp_x;

    @Override
    public double f(IDoubleArray x)
    {
        setCoordinates(x);
        calculate();
        return(getEnergy());
    }

    @Override
    public double f(double... x)
    {
        if (tmp_x == null)
            tmp_x = new PrimitiveDoubleArray(x);
        else
            tmp_x.set(x);
        return(f(tmp_x));
    }

    @Override
    public IDoubleArray grad(IDoubleArray x)
    {
        setCoordinates(x);
        calculate();
        return(getGradient());
    }

    @Override
    public IDoubleArray grad(double... x)
    {
        if (tmp_x == null)
            tmp_x = new PrimitiveDoubleArray(x);
        else
            tmp_x.set(x);
        return(grad(tmp_x));

    }

}
