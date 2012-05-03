/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.intsequence;

import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface IIntWriter
{
    public void add(int data);

    public void addAll(IIntArray data);    
    
    public void close();
}
