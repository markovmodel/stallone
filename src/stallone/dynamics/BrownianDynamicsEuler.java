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
public class BrownianDynamicsEuler implements IIntegratorThermostatted
{
    private double dt = 0;
    private double gamma = 0;
    private double kT = 1;

    private IDoubleArray x;

    private IEnergyModel model;
    private IDoubleArray masses;
    private Random random = new Random();

    public BrownianDynamicsEuler(double _dt, double _gamma, double _kT)
    {
        dt = _dt;
        gamma = _gamma;
        kT = _kT;
    }

    @Override
    public IIntegrator copy()
    {
        BrownianDynamicsEuler res = new BrownianDynamicsEuler(dt,gamma,kT);
        res.setX(x.copy());
        res.setEnergyModel(model);
        res.setMasses(masses.copy());
        return(res);
    }

    @Override
    public void setX(IDoubleArray x0)
    {
        x = x0.copy();
    }

    @Override
    public void setV(IDoubleArray v0)
    {
        throw new UnsupportedOperationException("Brownian Dynamics has no velocities.");
    }

    @Override
    public void setEnergyModel(IEnergyModel _model)
    {
        model = _model;
    }

    @Override
    public void setMasses(IDoubleArray _masses)
    {
        masses = _masses;
    }

    @Override
    public void setStepLength(double _dt)
    {
        dt = _dt;
    }

    @Override
    public boolean step()
    {
        model.setCoordinates(x);
        model.calculate();
        IDoubleArray grad = model.getGradient();

        //System.out.println("step, d="+DoubleArrays.norm(x.getAll()));

        for (int i=0; i<x.size(); i++)
        {
            double gm = (gamma*masses.get(i));
            double r = random.nextGaussian();
            //System.out.println(" grad "+i+" "+grad.get(i));
            double dx = -dt * grad.get(i) / gm  + Math.sqrt(2*dt*kT/gm)*r;
            //System.out.println(" dx "+i+" "+dx);
            x.set(i, x.get(i) + dx);
        }

        return(true);
    }

    @Override
    public IDoubleArray getX()
    {
        return(x.copy());
    }

    @Override
    public IDoubleArray getV()
    {
        throw new UnsupportedOperationException("Brownian Dynamics has no velocities.");
    }

    @Override
    public IDoubleArray getA()
    {
        throw new UnsupportedOperationException("Brownian Dynamics has no accelerations.");
    }

    @Override
    public double getStepLength()
    {
        return(dt);
    }

    @Override
    public double potentialEnergy()
    {
        return(model.getEnergy());
    }

    @Override
    public double kineticEnergy()
    {
        throw new UnsupportedOperationException("Brownian Dynamics has no kinetic energy.");
    }

    @Override
    public double totalEnergy()
    {
        return(model.getEnergy());
    }

    @Override
    public void setkT(double kT)
    {
        this.kT = kT;
    }

    @Override
    public double getkT()
    {
        return this.kT;
    }

}
