/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.potential.IMassEnergyModel;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class HarmonicOscillator  extends AbstractPotential
    implements IMassEnergyModel
{

    private IDoubleArray gradArr = Doubles.create.array(1), xArr = Doubles.create.array(1), massArr = Doubles.create.arrayFrom(1.0);
    private double k;
    private double x = 1, v, a;
    private double energy;
    private double gradient;

    public HarmonicOscillator(double _k)
    {
        this.k = _k;
    }

    @Override
    public void setCoordinates(IDoubleArray coordinates)
    {
        x = coordinates.get(0);
    }

    @Override
    public boolean calculate()
    {
        energy = 0.5 * k * x * x;
        gradient = k * x;
        return (true);
    }

    @Override
    public int getNDimensions()
    {
        return (1);
    }

    @Override
    public IDoubleArray getMasses()
    {
        return (massArr);
    }

    @Override
    public IDoubleArray getGradient()
    {
        gradArr.set(0, gradient);
        return (gradArr);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        xArr.set(0, x);
        return (xArr);
    }

    @Override
    public double getEnergy()
    {
        return (energy);
    }

    @Override
    public int getNumberOfVariables()
    {
        return(1);
    }

}
