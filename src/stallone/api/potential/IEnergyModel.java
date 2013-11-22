/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.potential;

import stallone.api.doubles.IDoubleArray;


/**
 *
 * @author noe
 */
public interface IEnergyModel
{
    public void setCoordinates(IDoubleArray coordinates);

    /**
     *
     * @return true when energy and gradient were evaluated successfully. false otherwise
     */
    public boolean calculate();

    public int getNDimensions();

    public double getEnergy();

    public IDoubleArray getGradient();

    public IDoubleArray getCoordinates();
}
