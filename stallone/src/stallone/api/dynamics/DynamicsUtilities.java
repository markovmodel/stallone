/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;

import stallone.api.doubles.IDoubleArray;
import stallone.api.datasequence.IDataWriter;
import stallone.dynamics.IIntegrator;

/**
 *
 * @author noe
 */
public class DynamicsUtilities
{
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
}
