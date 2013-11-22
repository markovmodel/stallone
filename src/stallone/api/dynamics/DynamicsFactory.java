/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;

import stallone.api.doubles.IDoubleArray;
import stallone.api.potential.*;
import stallone.dynamics.BrownianDynamicsEuler;
import stallone.dynamics.IIntegrator;
import stallone.dynamics.IIntegratorThermostatted;
import stallone.dynamics.LangevinLeapFrog;
import stallone.dynamics.VelocityVerlet;

/**
 *
 * @author noe
 */
public class DynamicsFactory
{
    public IIntegratorThermostatted brownianDynamicsEuler(IEnergyModel model, IDoubleArray masses, double dt, double gamma, double kT)
    {
        IIntegratorThermostatted res = new BrownianDynamicsEuler(dt, gamma, kT);
        res.setEnergyModel(model);
        res.setMasses(masses);
        return(res);
    }

    public IIntegratorThermostatted langevinLeapFrog(IEnergyModel model, IDoubleArray masses, double dt, double gamma, double kT)
    {
        IIntegratorThermostatted res = new LangevinLeapFrog(dt, gamma, kT);
        res.setEnergyModel(model);
        res.setMasses(masses);
        return(res);
    }

    public IIntegrator velocityVerlet(IEnergyModel model, IDoubleArray masses, double dt)
    {
        IIntegrator res = new VelocityVerlet(dt);
        res.setEnergyModel(model);
        res.setMasses(masses);
        return(res);
    }
}
