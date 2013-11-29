/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import stallone.api.doubles.IDoubleArray;
import stallone.graph.ListIntGraph;
import stallone.graph.MatrixIntGraph;
import stallone.graph.connectivity.IntStrongConnectivity;

/**
 *
 * @author noe
 */
public class GraphFactory
{
    public IIntGraph intMatrixGraph(IDoubleArray M)
    {
        return(new MatrixIntGraph(M));
    }

    public IIntGraph intListGraph(IDoubleArray M)
    {
        return(new ListIntGraph(M));
    }

    public IIntGraph intListGraph(int[][] edges)
    {
        return(new ListIntGraph(edges));
    }
    
    public IIntConnectivity connectivityChecker(IIntGraph g)
    {
        IIntConnectivity res = new IntStrongConnectivity(g);
        return(res);
    }
}
