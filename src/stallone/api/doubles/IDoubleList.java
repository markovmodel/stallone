/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

/**
 *
 * @author noe
 */
public interface IDoubleList extends IDoubleArray
{
    public void append(double value);
    
    public void appendAll(IDoubleArray values);
    
    public void insert(int index, double value);    

    public void insertAll(int index, IDoubleArray values);
    
    public void remove(int index);
    
    /**
     * 
     * @param from
     * @param to exclusive end index
     */
    public void removeRange(int from, int to);
    
    public void removeByValue(double value);
}
