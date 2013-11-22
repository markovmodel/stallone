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
public interface IFunction
{
    public int getNumberOfVariables();

    public double f(double... x);

    public double f(IDoubleArray x);
}
