/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

/**
 *
 * @author noe
 */
public interface IDynamicIntGraph extends IIntGraph
{
    public void addNode(int node);

    public void removeNode(int node);

    public boolean addEdge(int node1, int node2);

    public boolean hasEdge(int node1, int node2);

    public void removeEdge(int node1, int node2);

    public void removeEdges(int node1);

    public void removeAllEdges();
}
