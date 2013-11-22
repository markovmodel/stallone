/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import java.util.Iterator;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;

/**
 *
 * @author  Martin Senne
 */
public interface IIntGraph
{
    public int numberOfNodes();

    public int numberOfArcs();

    /**
     * Returns true if vertex is contained
     * @param vertex
     * @return
     */
    public boolean contains(int vertex);

    /**
     * Returns true if edge is contained
     * @param vertex1
     * @param vertex2
     * @return
     */
    public boolean contains(int vertex1, int vertex2);

    public IIntArray getNodes();

    public IIntIterator nodeIterator();

    public IIntIterator neighborIterator(int node);

    public Iterator<IIntEdge> edgeIterator();
}
