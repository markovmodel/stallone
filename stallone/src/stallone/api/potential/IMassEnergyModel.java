/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.api.potential;

import stallone.api.potential.IEnergyModel;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IMassEnergyModel extends IEnergyModel
{
	public IDoubleArray getMasses();
}
