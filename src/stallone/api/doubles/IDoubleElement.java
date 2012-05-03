/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

/**
 *
 * @author noe
 */
public interface IDoubleElement
{
    public int index();
    
    public int row();
    
    public int column();
    
    public double get();
    
    public void set(double x);
}
