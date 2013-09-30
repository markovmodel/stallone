/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dynamics;

/**
 *
 * @author noe
 */
public interface IIntegratorThermostatted extends IIntegrator
{
    public void setkT(double kT);
}
