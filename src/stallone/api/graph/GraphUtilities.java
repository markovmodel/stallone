/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import static stallone.api.API.*;

import java.util.LinkedList;
import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;

/**
 *
 * @author noe
 */
public class GraphUtilities
{

    public List<IIntArray> connectedComponents(IIntGraph g)
    {
        IIntConnectivity res = Graph.create.connectivityChecker(g);
        List<IIntArray> strongComponents = res.getStrongComponents();
        return(strongComponents);
    }

    public List<IIntArray> connectedComponents(IDoubleArray m)
    {
        IIntConnectivity res = Graph.create.connectivityChecker(Graph.create.intMatrixGraph(m));
        List<IIntArray> strongComponents = res.getStrongComponents();
        return(strongComponents);
    }

    /**
    Conducts a Breadth-First search through the graph, starting from vertex s.
    @param adjList the adjacency list to use
    @param _visited a boolean array which has true for each vertex that was
    already visited and false for each vertex that wasn't
    @param distances an integer array which will contain the distance (number of
    edges on shortest path) from s to any vertex
    @param predecessors the predecessor of each vertex on the shortest path from s
    predecessors[s] is set to -1.
    @param s the starting vertex.
    @return all the vertices found (the component accessible from s)
     */
    private int[] bfs(IIntGraph g, boolean[] _visited, int[] distances, int[] predecessors, int s)
    {
        boolean[] visited = _visited;
        if (_visited == null)
        {
            visited = new boolean[g.numberOfNodes()];
        }

        java.util.Arrays.fill(distances, -1);
        distances[s] = 0;
        predecessors[s] = -1;

        LinkedList found = new LinkedList();
        LinkedList todo = new LinkedList();
        todo.add(new Integer(s));
        visited[s] = true;

        while (todo.size() > 0)
        {
            int v = ((Integer) todo.remove(0)).intValue();
            IIntArray neighbors = g.getNeighbors(v);
            for (int i = 0; i < neighbors.size(); i++)
            {
                int neighbor = neighbors.get(i);
                //System.out.println(v+":"+distances[v]+" -> "+adjList[v][i]+":"+distances[adjList[v][i]]);
                if (!visited[neighbor])
                {
                    distances[neighbor] = distances[v] + 1;
                    predecessors[neighbor] = v;
                    todo.add(new Integer(neighbor));
                    visited[neighbor] = true;
                }
            }
            found.add(new Integer(v));
        }

        return (intArrays.List2Array(found));
    }

    /**
    Conducts a Breadth-First search through the graph, starting from vertex s.
    @param adjList the adjacency list to use
    @param s the starting vertex.
    @return all the vertices found (the component accessible from s)
     */
    public int[] bfs(IIntGraph g, int s)
    {
        int n = g.numberOfNodes();
        boolean[] visited = new boolean[n];
        int[] distances = new int[n];
        int[] predecessors = new int[n];
        java.util.Arrays.fill(distances, -1);
        return (bfs(g, visited, distances, predecessors, s));
    }

    /**
    Repeated BFS to find all vertices. Each vertex set (strong component) is returned
    independently
    @param adjList the adjacency list to use
     */
    public int[][] bfsAll(IIntGraph g)
    {
        int n = g.numberOfNodes();
        boolean[] visited = new boolean[n];
        int[] distances = new int[n];
        int[] predecessors = new int[n];
        LinkedList found = new LinkedList();
        for (int i = 0; i < visited.length; i++)
        {
            if (!visited[i])
            {
                found.add(bfs(g, visited, distances, predecessors, i));
            }
        }

        return (intArrays.List2Array2(found));
    }
    
}
