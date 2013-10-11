/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface IGriddedFunction
    extends IGrid, IDiscretizedFunction
{
    public double f(int... indexes);
    
    public double f(IIntArray indexes);
}
