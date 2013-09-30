/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

/**
 *
 * @author noe
 */
public interface IEdge<V>
{
    public V getNode1();
    public V getNode2();
    public double getWeight();
}
