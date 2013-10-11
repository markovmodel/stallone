package stallone.graph.connectivity;

import stallone.doubles.fastutils.IntSortedSet;
import stallone.doubles.fastutils.IntAVLTreeSet;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import java.util.*;
import stallone.api.graph.*;

/**
 *
 * @author  Martin Senne
 */
public class IntStrongConnectivity implements IIntConnectivity
{
    private IIntGraph graph;

    private HashMap<Integer,IntVertexInfo> vertex2info = new HashMap<Integer, IntVertexInfo>();
    
    // private List<ArrayList<N>> components;
    private List<IntSortedSet> internalComponents;
    private List<IIntArray> components;

    private int index;
    
    private Stack<IntVertexInfo> S;

    public IntStrongConnectivity(IIntGraph graph)
    {
        setGraph(graph);
    }
    
    @Override
    public final void setGraph(IIntGraph graph)
    {
        this.graph = graph;

        for (IIntIterator it = graph.nodeIterator(); it.hasNext(); it.advance())
        {
            IntVertexInfo ivi = new IntVertexInfo(it.get());
            vertex2info.put(it.get(), ivi);
        }
                
        internalComponents = new ArrayList<IntSortedSet>();
        components = new ArrayList<IIntArray>();

        index = 0;
        S = new Stack<IntVertexInfo>();    
    }

    /**
     * executes Tarjan's algorithm of the supplied graph
     *
     * @return A list of lists of node which belong to the same component
     */
    @Override
    public void perform()
    {
        List<IntVertexInfo> tmp = new ArrayList<IntVertexInfo>(vertex2info.values());
        for (int i = 0; i < tmp.size(); i++)
        {
            if (tmp.get(i).notNumbered())
            {
                perform(tmp.get(i).node);
            }
        }
        
        // clean up component lists:
        for (IntSortedSet iss: internalComponents)
        {
            components.add(Ints.create.arrayFrom(iss.toArray(new int[iss.size()])));
        }
    }

    private void perform(int n)
    {
        tarjan(vertex2info.get(n));
    }

    private void tarjan(IntVertexInfo v)
    {
        v.index = index;
        v.lowlink = index;
        index++;
        S.push(v);

        // List<A> arcs = graph.getArcs(v.node);
        // for (A arc : arcs) {
        //    VertexInfo<N> vp = verticies.get( graph.getTarget( arc ));

        for (IIntIterator it = graph.neighborIterator(v.node); it.hasNext(); it.advance())
        {
            int node = it.get();
            
            IntVertexInfo vp = vertex2info.get(node);

            if (vp.index == -1)
            {
                tarjan(vp);
                v.lowlink = Math.min(v.lowlink, vp.lowlink);
            }
            else if (this.S.contains(vp))
            {
                v.lowlink = Math.min(v.lowlink, vp.index);
            }
        }

        if (v.lowlink == v.index)
        {
            IntVertexInfo j;
            // ArrayList<N> component = new ArrayList<N>();
            IntSortedSet component = new IntAVLTreeSet();
            do
            {
                j = this.S.pop();
                component.add(j.node);
            }
            while (j != v);

            internalComponents.add(component);
        }
    }

    @Override
    public List<IIntArray> getStrongComponents()
    {
        return components;
    }

    public class IntVertexInfo
    {

        public int node;
        public int index;
        public int lowlink;

        /**
         * @param n
         */
        public IntVertexInfo(int n)
        {
            index = -1;
            lowlink = -1;
            node = n;
        }

        public boolean notNumbered()
        {
            if (index == -1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        @Override
        public String toString()
        {
            return "vertex [index=" + index + ", lowlink=" + lowlink
                    + ", node=" + node + "]";
        }
    }
}
