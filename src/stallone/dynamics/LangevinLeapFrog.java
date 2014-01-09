/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dynamics;

import java.util.Random;

import stallone.api.doubles.IDoubleArray;
import stallone.api.dynamics.IIntegrator;
import stallone.api.dynamics.IIntegratorThermostatted;
import stallone.api.potential.IEnergyModel;

/**
 *
 * @author noe
 */
public class LangevinLeapFrog implements IIntegratorThermostatted
{

    private Random random = new Random();
    private IEnergyModel model;
    private double dt, gamma, kT;
    private IDoubleArray x, v, a;
    private IDoubleArray grad, rand;
    private IDoubleArray masses;
    // for fail-safeness
    private int failsafe = 0, failures = 0;
    private IDoubleArray xBackup = null, vBackup = null;

    /**
     *
     * @param _dt time step
     * @param gamma collision rate
     */
    public LangevinLeapFrog(double _dt, double _gamma, double _kT)
    {
        this.dt = _dt;
        this.gamma = _gamma;
        this.kT = _kT;
    }

    @Override
    public IIntegrator copy()
    {
        LangevinLeapFrog res = new LangevinLeapFrog(dt,gamma,kT);
        res.setEnergyModel(model);
        res.setFailsafe(failures);
        res.setMasses(masses.copy());
        res.setV(v.copy());

        return(res);
    }

    /**
     * Sets whether the integrator can deal with failures to calculate energy or gradient
     * by the underlying energy model. Upon a failure to calculate energy or gradient
     * the integrator will generate new random numbers and try again for up to *level* times
     * per time step. The default is 0, i.e. by default the integrator is not failsafe.
     * Attention: Failsafeness induces in principle an undesirable bias.
     * It is equivalent to rejecting structures that the underlying energy model cannot deal with.
     * It is a much better idea to fix the underlying energy model than using failsafeness here.
     * @param level
     */
    public void setFailsafe(int level)
    {
        this.failsafe = level;
    }

    @Override
    public void setX(IDoubleArray x0)
    {
        this.x = x0;
    }

    @Override
    public void setV(IDoubleArray v0)
    {
        this.v = v0;
    }

    @Override
    public void setEnergyModel(IEnergyModel _model)
    {
        this.model = _model;
    }

    @Override
    public void setMasses(IDoubleArray _masses)
    {
        this.masses = _masses;
    }

    @Override
    public void setStepLength(double _dt)
    {
        this.dt = _dt;
    }

    @Override
    public boolean step()
    {
        double ca = Math.exp(-gamma * dt / 2.0);
        double cb = (1 - ca) / (gamma * dt);
        double cc = Math.sqrt(1 - ca);

        if (grad == null)
        {
            model.setCoordinates(x);
            model.calculate();
            grad = model.getGradient();

            rand = x.copy();
            for (int i = 0; i < rand.size(); i++)
            {
                rand.set(i, Math.sqrt(kT) * random.nextGaussian());
            }
        }

        // failsafe
        if (failsafe > 0)
        {
            xBackup = (IDoubleArray)x.copy();
            vBackup = (IDoubleArray)v.copy();
        }

        int attempts = 0;
        boolean success = false;
        while (!(success || attempts > failsafe))
        {
            for (int i = 0; i < v.size(); i++)
            {
                // update all velocities to half-step
                v.set(i, ca * v.get(i) - cb * dt * grad.get(i) / masses.get(i) + cc * rand.get(i) / Math.sqrt(masses.get(i)));

                // update all coordinates
                x.set(i, x.get(i) + dt * v.get(i));
            }

            model.setCoordinates(x);
            success = model.calculate();
            attempts++;
            if ((!success) && attempts <= failsafe)
                System.out.println(" WARNING: Energy or gradient calculation has failed "+attempts+" times. Failsafe-Level allows for another attempt");
        }
        if (!success)
        {
            System.out.println(" WARNING: Energy or gradient calculation has failed. Giving up.");
            return (false);
        }

        grad = model.getGradient();

        // update all velocities to full-step
        for (int i = 0; i < v.size(); i++)
        {
            // next random
            rand.set(i, Math.sqrt(kT) * random.nextGaussian());

            v.set(i, ca * v.get(i) - cb * dt * grad.get(i) / masses.get(i) + cc * rand.get(i) / Math.sqrt(masses.get(i)));
        }

        return (true);
    }

    @Override
    public IDoubleArray getX()
    {
        return (x);
    }

    @Override
    public IDoubleArray getV()
    {
        return (v);
    }

    @Override
    public IDoubleArray getA()
    {
        return (a);
    }

    @Override
    public double getStepLength()
    {
        return (dt);
    }

    @Override
    public double potentialEnergy()
    {
        return (model.getEnergy());
    }

    @Override
    public double kineticEnergy()
    {
        double ekin = 0;

        for (int i = 0; i < masses.size(); i++)
        {
            ekin += 0.5 * masses.get(i) * v.get(i) * v.get(i);
        }

        return (ekin);
    }

    @Override
    public double totalEnergy()
    {
        return (model.getEnergy() + kineticEnergy());
    }

    @Override
    public void setkT(double _kT)
    {
        this.kT = _kT;
    }

    @Override
    public double getkT()
    {
        return this.kT;
    }
}
