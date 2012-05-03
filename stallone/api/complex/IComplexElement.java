/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.complex;

import stallone.api.doubles.IDoubleElement;

/**
 *
 * @author noe
 */
public interface IComplexElement extends IDoubleElement
{
    public double re();

    public double im();

    public void setRe(double x);
    
    public void setIm(double x);
}
