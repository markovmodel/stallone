/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;

/**
 *
 * @author noe
 */
public interface IIntList extends IIntArray
{
    public void append(int value);

    public void appendAll(IIntArray values);

    public void insert(int index, int value);

    public void insertAll(int index, IIntArray values);

    public void remove(int index);

    /**
     *
     * @param from
     * @param to exclusive end index
     */
    public void removeRange(int from, int to);

    public void removeByValue(int value);

    public void clear();
}
