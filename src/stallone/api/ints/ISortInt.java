/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface ISortInt
{
    public void setData(IIntArray data, IIntArray target);

    public void setData(IIntArray data);

    public IIntArray sort();

    public IIntArray getSortedIndexes();

    public IIntArray getSortedData();
}
