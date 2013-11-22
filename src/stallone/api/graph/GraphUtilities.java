/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

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
        IIntConnectivity res = Graph.create.connectivityChecker(Graph.create.intGraph(m));
        List<IIntArray> strongComponents = res.getStrongComponents();
        return(strongComponents);
    }

}
