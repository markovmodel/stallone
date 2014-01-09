/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dynamics;


/**
 *
 * @author noe
 */
public interface IIntegratorThermostatted extends IIntegrator
{
    public void setkT(double kT);
    public double getkT();
}
