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
public interface IFunctionC1 extends IFunction
{
    public IDoubleArray grad(double... x);

    public IDoubleArray grad(IDoubleArray x);
}
