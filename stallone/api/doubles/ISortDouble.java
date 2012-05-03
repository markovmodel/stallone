/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface ISortDouble
{
    public void setData(IDoubleArray data, IDoubleArray target);

    public void setData(IDoubleArray data);
    
    public IDoubleArray sort();
    
    public IIntArray getSortedIndexes();
    
    public IDoubleArray getSortedData();
}
