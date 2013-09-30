/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IDiscretizedFunction extends IFunction
{
    @Override
    public double f(IDoubleArray x);
}
