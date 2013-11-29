/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.graph;

import java.util.ArrayList;
import static stallone.api.API.*;

import java.util.Iterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.graph.IIntEdge;
import stallone.api.graph.IIntGraph;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import stallone.api.ints.IIntList;
import stallone.api.ints.Ints;
import stallone.ints.IntArrayIterator;

/**
 *
 * @author noe
 */
public class ListIntGraph implements IIntGraph
{
    private IIntList[] neighbors;

    public ListIntGraph(int nNodes)
    {
        this.neighbors = new IIntList[nNodes];
        for (int i=0; i<neighbors.length; i++)
            neighbors[i] = intsNew.list(5);
    }
    
    public ListIntGraph(IDoubleArray matrix)
    {
        this(matrix.rows());
        for (IDoubleIterator it = matrix.nonzeroIterator(); it.hasNext();)
        {
            addEdge(it.row(), it.column());
        }
    }

    public ListIntGraph(int[][] edges)
    {
        this(intArrays.max(intArrays.flatten(edges))+1);
        for (int[] e : edges)
        {
            addEdge(e[0],e[1]);
        }
    }
    
    private void addEdge(int i, int j)
    {
        if (!ints.contains(this.neighbors[i], j))
            this.neighbors[i].append(j);
    }

    @Override
    public boolean contains(int vertex)
    {
        return (0 <= vertex) && (vertex < neighbors.length);
    }

    @Override
    public boolean contains(int vertex1, int vertex2)
    {
        return ints.contains(this.neighbors[vertex1], vertex2);
    }

    @Override
    public IIntIterator nodeIterator()
    {
        IntArrayIterator it = new IntArrayIterator(Ints.create.arrayRange(neighbors.length));
        return(it);
    }

    @Override
    public IIntIterator neighborIterator(int node)
    {
        return neighbors[node].iterator();
    }

    @Override
    public Iterator<IIntEdge> edgeIterator()
    {
        ArrayList<IIntEdge> edges = new ArrayList();
    
        for (int i=0; i<neighbors.length; i++)
        {
            for (int j=0; j<neighbors[i].size(); j++)
            {
                edges.add(new IntEdge(i, neighbors[i].get(j)));
            }
        }
        
        return edges.iterator();
    }

    @Override
    public int numberOfNodes()
    {
        return neighbors.length;
    }

    @Override
    public int numberOfArcs()
    {
        int n = 0;
        for (int i=0; i<neighbors.length; i++)
            n += neighbors[i].size();
        return(n);
    }

    @Override
    public IIntArray getNodes()
    {
        return(Ints.create.arrayRange(neighbors.length));
    }
    
    public IIntArray getNeighbors(int i)
    {
        return neighbors[i];
    }

}    
