/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

import java.util.List;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface IIntConnectivity
{

    public void setGraph(IIntGraph graph);

    public void perform();

    public List<IIntArray> getStrongComponents();
}
