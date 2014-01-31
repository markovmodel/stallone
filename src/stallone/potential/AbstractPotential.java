/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import static stallone.api.API.*;

import stallone.api.potential.IEnergyModel;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IFunctionC1;
import stallone.doubles.DenseDoubleArray;

/**
 *
 * @author noe
 */
public abstract class AbstractPotential
    implements IEnergyModel, IFunctionC1
{
    IDoubleArray tmp_x;

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
            tmp_x = doublesNew.array(x);
        else
            doubles.set(tmp_x,x);
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
            tmp_x = doublesNew.array(x);
        else
            doubles.set(tmp_x,x);
        return(grad(tmp_x));

    }

}
