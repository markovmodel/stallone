/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import java.util.*;
import stallone.api.algebra.*;
import stallone.api.graph.IIntGraph;
import stallone.graph.MatrixGraph;
import stallone.graph.connectivity.IntStrongConnectivity;

public class StationaryDistribution
{
    IDoubleArray T = null;
    boolean reversible = false;
    IDoubleArray pi = null;

    List<IIntArray> components = null;
    List<IDoubleArray> componentsPi = null;

    public void setT(IDoubleArray _T)
    {
        this.T = _T;
    }

    /**
     * Calculates stationary distribution using  rate matrix
     * @param _K
     */
    public void setK(IDoubleArray _K)
    {
        this.T = _K.copy();
        for (int i=0; i<T.size(); i++)
            T.set(i,i, T.get(i,i)+1);
    }

    /**
     * If set true, a fast algorithm for calculating the reversible stationary distribution
     * will be employed. This only works if the transition matrix used is really reversible!
     * If not, the result will be nonsense
     * @param rev
     */
    public void setReversible(boolean rev)
    {
        this.reversible = rev;
    }

    private boolean componentIsClosed(IIntArray C)
    {
        for (int i=0; i<C.size(); i++)
        {
            int s = C.get(i);
            for (int j=0; j<T.columns(); j++)
            {
                if (T.get(s,j) > 0)
                {
                    if (!Ints.util.contains(C, j))
                        return(false);
                }
            }
        }
        return(true);
    }

    /**
     * if there exists one component that has an outgoing edge into another component,
     * these components are merged. Only one pair of components is merged in this way.
     * @param C component set to inspect
     * @return false if nothing needs to be done, otherwise the new component set
     */
    private boolean mergeZeroComponent(List<IIntList> C)
    {
        int cfrom = -1, cto = -1;
        for (int c = 0; c < C.size(); c++)
        {
            for (int i = 0; i < C.get(c).size(); i++)
            {
                int s1 = C.get(c).get(i);
                for (int s2 = 0; s2 < T.columns(); s2++)
                {
                    if (T.get(s1,s2) > 0 && !Ints.util.contains(C.get(c), s2))
                    {
                        cfrom = c;
                        // we have found a target state s2 out of C[c]. Which set is it in?
                        for (int c2 = 0; c2 < C.size(); c2++)
                        {
                            if (Ints.util.contains(C.get(c2), s2))
                            {
                                cto = c2;
                            }
                        }
                        break;
                    }
                    if (cfrom > -1)
                    {
                        break;
                    }
                }
                if (cfrom > -1)
                {
                    break;
                }
            }
        }

        if (cfrom == -1)
        {
            return (false);
        }

        C.get(cto).appendAll(C.get(cfrom));
        C.remove(cfrom);

        return (true);
    }

    private void calculateComponents()
    {
        IIntGraph g = new MatrixGraph(T);
        IntStrongConnectivity connectivity = new IntStrongConnectivity(g);
        connectivity.perform();

        List<IIntArray> C = connectivity.getStrongComponents();
//        int[][] C = g.BFS_mult();

        IIntArray complengths = Ints.create.array(C.size());
        for (int i=0; i<complengths.size(); i++)
            complengths.set(i, C.get(i).size());
        IIntArray I = Ints.util.sortedIndexes(complengths);
        Ints.util.mirror(I);
        C = Ints.util.subset(C, I);

        this.components = C;

        this.componentsPi = new ArrayList<IDoubleArray>(this.components.size());
        for (int i=0; i<C.size(); i++)
            this.componentsPi.add(null);

        //System.out.println("Component lengths: "+IntArrays.toString(IntArrays.lengths(C)));

        /*
        Graph g = Graph.fromWeightMatrix(T);
        int[][] C = g.strongComponents();

        // merge components that have outfluxes into one of their targets (arbitrary which one is chosen)
        int[][] Cmerged = null;
        while ((Cmerged = mergeZeroComponent(C)) != null)
        {
            C = Cmerged;
        }
        this.components = C;
        this.componentsPi = new double[this.components.length][T.length];
         *
         */
    }

    public IDoubleArray calculate()
    {
        pi = Doubles.create.array(T.rows());

        // check transition matrix structure and determine components
        calculateComponents();

        for (int c=0; c<components.size(); c++)
        {
            IDoubleArray Tsub = T.view(components.get(c).getArray(),components.get(c).getArray()).copy();
            for (int i=0; i<Tsub.rows(); i++)
            {
                IDoubleArray row = Tsub.viewRow(i);
                Algebra.util.scale(1.0/Doubles.util.sum(row), row);
            }

            //System.out.println(c+" "+componentIsClosed(components[c]));

            if (!componentIsClosed(components.get(c)))
                continue; // no probability in this set.

            IDoubleArray pisub = calculateSub(Tsub);

            /*if (c == 0)
            {
            System.out.println("Submatrix: "+Tsub.length);
            for (int j=0; j<Tsub.length; j++)
                System.out.println(j+" "+DoubleArrays.sum(Tsub[j]));

            Graph g = Graph.fromWeightMatrix(Tsub);
            int[][] C = g.strongComponents();
            System.out.println("Number of components: "+C.length);

            System.out.println("PI: ");
            DoubleArrays.print(pisub,"\n");

            System.out.println("T sub:");
            AlgebraPrimitive.writeMatrixSparse(Tsub,System.out);

                System.exit(0);
            }
             *
             */

            this.componentsPi.set(c, pisub);

            for (int i=0; i<pisub.size(); i++)
            {
                int s = components.get(c).get(i);
                pi.set(s, pisub.get(i));
                //this.componentsPi.set(c).set(s, pisub.get(i));
                //this.componentsPi.get(c).set(s, pisub.get(i));
                //System.out.println("trying to get from component "+c+" with size "+components.get(c));
                //System.out.println("trying to get from pi comp with size "+componentsPi.get(c));

            }
        }
        return (pi);
    }

    private IDoubleArray calculateSub(IDoubleArray Tsub)
    {
        IDoubleArray pisub = null;
        if (!reversible)
        {
            pisub = calculateSubGeneral(Tsub);
        }
        else
        {
            pisub = calculateSubReversible(Tsub);
        }

        return (pisub);
    }


    private IDoubleArray calculateSubGeneral(IDoubleArray Tsub)
    {
        IEigenvalueDecomposition evd = Algebra.util.evd(Tsub, true, false);
        evd.sortNormDescending();

        IDoubleArray l1 = evd.getLeftEigenvector(0).copy();

        Algebra.util.scale(1.0 / Doubles.util.sum(l1), l1);

        return(l1);
    }

    private IDoubleArray calculateSubReversible(IDoubleArray Tsubrev)
    {
        IDoubleArray pisub = Doubles.create.array(Tsubrev.rows());
        pisub.set(0,1);

        boolean[] done = new boolean[Tsubrev.rows()];
        done[0] = true;

        LinkedList<Integer> todo = new LinkedList<Integer>();
        todo.add(0);

        while (todo.size() > 0)
        {
            int i = todo.get(0);
            for (int j = 0; j < Tsubrev.columns(); j++)
            {
                if (i != j && Tsubrev.get(i,j) > 0 && !done[j])
                {
                    pi.set(j, pi.get(i) * Tsubrev.get(i,j) / Tsubrev.get(j,i));
                    todo.add(j);
                    done[j] = true;
                }
            }
            todo.remove(0);
        }

        Algebra.util.scale(1.0 / Doubles.util.sum(pisub), pisub);
        return (pisub);
    }

    /**
     * Returns whether the calculated stationary distribution is unique.
     * @return
     */
    public boolean isUnique()
    {
        if (pi == null)
        {
            throw (new RuntimeException("Trying to find out whether stationary distribution is unique before having calculated it."));
        }
        return (components.size() == 1);
    }

    public IDoubleArray getPi()
    {
        return(pi);
    }

    public List<IIntArray> getComponents()
    {
        return(components);
    }

    public List<IDoubleArray> getComponentsPi()
    {
        return(componentsPi);
    }

    public static IDoubleArray calculate(IDoubleArray T)
    {
        StationaryDistribution statdist = new StationaryDistribution();
        statdist.setT(T);
        statdist.setReversible(false);
        return(statdist.calculate());
    }

    public static IDoubleArray calculateReversible(IDoubleArray T)
    {
        StationaryDistribution statdist = new StationaryDistribution();
        statdist.setT(T);
        statdist.setReversible(true);
        return(statdist.calculate());
    }
}
