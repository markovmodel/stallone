/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;

import stallone.api.doubles.IDoubleArray;
import stallone.api.potential.IEnergyModel;


/**
 *
 * @author noe
 */
public interface IIntegrator
{
    public IIntegrator copy();

    public void setX(IDoubleArray x0);

    public void setV(IDoubleArray v0);

    public void setEnergyModel(IEnergyModel model);

    public void setMasses(IDoubleArray masses);

    public void setStepLength(double dt);

    /**
     *
     * @return true if step was successful, false otherwise
     */
    public boolean step();

    public IDoubleArray getX();

    public IDoubleArray getV();

    public IDoubleArray getA();

    public double getStepLength();

    public double potentialEnergy();

    public double kineticEnergy();

    public double totalEnergy();
}
