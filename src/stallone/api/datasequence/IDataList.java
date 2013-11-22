/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IDataList
    extends IDataSequence
{
    public boolean add(IDoubleArray x);

    public boolean remove(IDoubleArray x);

    public IDoubleArray remove(int i);

    public void set(int i, IDoubleArray x);
}
