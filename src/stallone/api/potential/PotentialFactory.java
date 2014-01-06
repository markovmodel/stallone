/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.potential;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.potential.IEnergyModel;
import stallone.function.JEPFunctionC1;
import stallone.potential.*;

/**
 *
 * @author noe
 */
public class PotentialFactory
{
    public IEnergyModel multivariateFromExpression(String[] variables, String expression, String... derivatives)
    {
        return(new GenericPotential(new JEPFunctionC1(variables, expression, derivatives)));
    }

    public IEnergyModel univariateFromExpression(String expression, String... derivatives)
    {
        return(new GenericPotential(new JEPFunctionC1(expression, derivatives)));
    }

    public IEnergyModel sumOfTerms(IEnergyModel... terms)
    {
        CompositeEnergyModel res = new CompositeEnergyModel();
        for (IEnergyModel term: terms)
            res.add(term);
        return(res);
    }

    /**
     * Symmetric bistable potential f(x) = k4 (x-d0)^4 - k2 (x-d0)^2
     * @param _k2
     * @param _k4
     * @param _d0
     * @return
     */
    public IEnergyModel bistable(double k2, double k4, double d0)
    {
        return(new BistablePotential(k2, k4, d0));
    }

    /**
     * Asymmetric bistable potential f(x) = k4 (x-d0)^4 - k2 (x-d0)^2 + k1 (x-d0)
     * @param k1
     * @param k2
     * @param k4
     * @param d0
     * @return
     */
    public IEnergyModel bistableAssymetric(double k1, double k2, double k4, double d0)
    {
        return(new AssymetricBistablePotential(k1, k2, k4, d0));
    }


    /** Folding model with
     * d < df:  U = -kf/2 (d-df)^2
     * d > du:  U = ku/2 (d-du)^2
     * else     U = 0
     */
    public IEnergyModel foldingModel(double df, double kf, double du, double ku, int ndim)
    {
        return(new FoldingModelSimple(df, kf, du, ku, ndim));
    }

    public IEnergyModel coulomb(IDoubleArray charges, double epsilonr)
    {
        return(new CoulombSystem(charges, epsilonr));
    }

    public IEnergyModel coulomb(IDoubleArray charges, IIntArray bonded, double epsilonr)
    {
        return(new CoulombSystem(charges, bonded, epsilonr));
    }

    public IEnergyModel harmonicNetwork(int natoms)
    {
        return(new HarmonicNetwork(natoms));
    }

    public IEnergyModel harmonicOscillator(double k)
    {
        return(new HarmonicOscillator(k));
    }

    public IEnergyModel lennardJones(int natoms, IDoubleArray radii, IDoubleArray epsilons)
    {
        return(new LennardJonesSystem(natoms, radii, epsilons));
    }

    public IEnergyModel lennardJones(int natoms, IDoubleArray radii, IDoubleArray epsilons, IIntArray bonded)
    {
        return(new LennardJonesSystem(natoms, radii, epsilons, bonded));
    }
}
