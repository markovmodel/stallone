/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dynamics;

import stallone.api.doubles.IDoubleArray;
import stallone.api.dynamics.IIntegrator;
import stallone.api.potential.IEnergyModel;


/**
 *
 * @author noe
 */
public class VelocityVerlet implements IIntegrator
{
    private IEnergyModel model;
    private double dt;
    private IDoubleArray x, v, a;
    private IDoubleArray masses;

    public VelocityVerlet(double _dt)
    {
        this.dt = _dt;
    }

    @Override
    public IIntegrator copy()
    {
        VelocityVerlet res = new VelocityVerlet(dt);
        res.setEnergyModel(model);
        res.setMasses(masses.copy());
        res.setX(x.copy());
        res.setV(v.copy());
        return(res);
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
        this.a = (IDoubleArray)v.copy();
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
        for (int i = 0; i < x.size(); i++)
        {
            // update coordinates
            x.set(i, x.get(i) + dt * v.get(i) + 0.5 * dt * dt * a.get(i));
            // update velocities to half-step
            v.set(i, v.get(i) + 0.5 * dt * a.get(i));
        }

        // update acceleration
        model.setCoordinates(x);
        boolean success = model.calculate();
        if (!success)
        {
            return (false);
        }

        IDoubleArray gradient = model.getGradient();

        for (int i = 0; i < x.size(); i++)
        {
            // calculate acceleration from forces
            a.set(i, -gradient.get(i) / masses.get(i));
            // update velocities to full step
            v.set(i, v.get(i) + 0.5 * dt * a.get(i));
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
    public double getStepLength()
    {
        return (dt);
    }
}
