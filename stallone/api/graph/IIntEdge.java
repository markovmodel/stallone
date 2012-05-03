/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.graph;

/**
 *
 * @author noe
 */
public interface IIntEdge extends IEdge<Integer>
{
    public int getV1();
    public int getV2();
    public void setV1(int node1);
    public void setV2(int node2);
    public void setWeight(double w);
}
