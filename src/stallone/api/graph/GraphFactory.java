/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import stallone.api.doubles.IDoubleArray;
import stallone.graph.MatrixGraph;
import stallone.graph.connectivity.IntStrongConnectivity;

/**
 *
 * @author noe
 */
public class GraphFactory
{
    public IIntGraph intGraph(IDoubleArray M)
    {
        return(new MatrixGraph(M));
    }

    public IIntConnectivity connectivityChecker(IIntGraph g)
    {
        IIntConnectivity res = new IntStrongConnectivity(g);
        return(res);
    }
}
