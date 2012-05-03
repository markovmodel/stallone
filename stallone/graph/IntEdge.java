/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.graph;

import stallone.api.graph.IIntEdge;

/**
 *
 * @author noe
 */
public class IntEdge implements IIntEdge
{
    private int v1,v2;
    private double w;
    
    public IntEdge(int node1, int node2)
    {
        this.v1 = node1;
        this.v2 = node2;
    }

    public IntEdge(int node1, int node2, double weight)
    {
        this.v1 = node1;
        this.v2 = node2;
        this.w = weight;
    }
    
    @Override
    public int getV1()
    {
        return(v1);
    }

    @Override
    public int getV2()
    {
        return(v2);
    }

    @Override
    public void setV1(int node1)
    {
        v1 = node1;
    }

    @Override
    public void setV2(int node2)
    {
        v2 = node2;
    }
    
    @Override
    public Integer getNode1()
    {
        return(v1);
    }

    @Override
    public Integer getNode2()
    {
        return(v2);
    }

    @Override
    public double getWeight()
    {
        return(w);
    }

    @Override
    public void setWeight(double weight)
    {
        this.w = weight;
    }
    
}
