package stallone.api.graph;

import java.util.Iterator;

/**
 *
 * @author  Martin Senne
 */
public interface IGraph<NodeType, EdgeType extends IEdge<?>>
{
    boolean contains(NodeType vertex);

    boolean contains(EdgeType edge);
    
    public Iterator<NodeType> nodeIterator();

    public Iterator<NodeType> neighborIterator(NodeType node);

    public Iterator<EdgeType> edgeIterator();
    
}
