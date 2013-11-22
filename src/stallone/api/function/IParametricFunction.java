/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

import stallone.api.ICopyable;
import stallone.api.doubles.IDoubleArray;


/**
 *
 * @author noe
 */
public interface IParametricFunction extends IFunction, ICopyable<IParametricFunction>
{
    public IDoubleArray getParameters();

    public void setParameters(IDoubleArray par);
}
