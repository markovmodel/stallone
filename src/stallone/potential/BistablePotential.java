/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;


/**
 * Atom pair with interaction energy E(d) = -k2*d^2 / 2 + k4*d^4 / 4
 * @author noe
 */
public class BistablePotential  extends AbstractPotential
{
    private IDoubleArray gradArr = Doubles.create.array(1), dArr = Doubles.create.array(1);
    private double k2, k4, d0;
    private double d, grad, energy;

    public BistablePotential(double _k2, double _k4, double _d0)
    {
        this.k2 = _k2;
        this.k4 = _k4;
        this.d0 = _d0;
    }

    @Override
    public void setCoordinates(IDoubleArray coordinates)
    {
        this.d = coordinates.get(0);
    }

    @Override
    public boolean calculate()
    {
        double dd1 = (d - d0);
        double dd2 = dd1 * dd1;
        double dd3 = dd1 * dd2;
        double dd4 = dd2 * dd2;
        energy = -k2 * dd2 / 2.0 + k4 * dd4 / 4.0;

        double g0 = -k2 * dd1 + k4 * dd3;
        grad = g0;

        return(true);
    }

    @Override
    public int getNDimensions()
    {
        return (1);
    }

    @Override
    public double getEnergy()
    {
        return (energy);
    }

    @Override
    public IDoubleArray getGradient()
    {
        gradArr.set(0, grad);
        return(gradArr);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        dArr.set(0, d);
        return(dArr);
    }

    @Override
    public int getNumberOfVariables()
    {
        return(1);
    }


}
