/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;

import static stallone.api.API.*;

import java.util.Random;

import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.datasequence.IDataWriter;

/**
 *
 * @author noe
 */
public class DynamicsUtilities
{
    private static Random random = new Random();
    public static final double kB_kJmolK = 0.008314472616;
    
    /**
     *
     * @param kB Boltzmann constant
     * @param temperature temperature
     * @return
     */
    public IDoubleArray maxwellBoltzmannVelocities(IDoubleArray masses, double kT)
    {
        // draw from a Gaussian with sigma^2 = kT / m
        // = 0.00911837293 * T[K] / m[u].
        IDoubleArray v = masses.copy();

        for (int i = 0; i < v.size(); i++)
        {
            double sigma = Math.sqrt(kT / masses.get(i));
            v.set(i, sigma * random.nextGaussian());
        }

        return(v);
    }    

    
    /**
     *
     * @param x0 the initial coordinate set
     * @param masses the masses
     * @param integrator a readily-usable integrator having a reference to the dynamical model
     * @param crdOutput reference to the output where the trajectory will be written
     * @param nsteps the total number of integration steps done
     * @param nsave the number of integration steps before writing out coordinates
     */
    public void run(IDoubleArray x0, IIntegrator integrator, IDataWriter crdOutput, int nsteps, int nsave)
    {
        integrator.setX(x0);

        for (int i=0; i<nsteps; i++)
        {
            integrator.step();

            if (i % nsave == 0)
                crdOutput.add(integrator.getX());
        }
    }
    
    /**
     *
     * @param x0 the initial coordinate set
     * @param masses the masses
     * @param integrator a readily-usable integrator having a reference to the dynamical model
     * @param crdOutput reference to the output where the trajectory will be written
     * @param nsteps the total number of integration steps done
     * @param nsave the number of integration steps before writing out coordinates
     */
    public IDataSequence run(IDoubleArray x0, IIntegrator integrator, int nsteps, int nsave)
    {
        IDataList out = dataNew.list();
        integrator.setX(x0);

        for (int i=0; i<nsteps; i++)
        {
            integrator.step();

            if (i % nsave == 0)
                out.add(integrator.getX().copy());
        }
        return out;
    }
    
}
