/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.tpt;

import static stallone.api.API.*;

import java.util.*;
import java.math.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleElement;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.graph.IIntGraph;

import cern.colt.matrix.impl.*;
import stallone.api.graph.Graph;


public class PathwayDecomposition
{
    SparseObjectMatrix2D F;
    BigDecimal[] influxes = null;
    BigDecimal[] outfluxes = null;

    private double[] Q = null;
    private int[] A = null, B = null;
    private boolean[] inA = null, inB = null;

    private int[] currentPathway = null;
    private BigDecimal currentFlux = BigDecimal.ZERO;

    /**
       @param _F The net fluxes
       @param Q The committor
       @param R The set of representatives;
     */

    public PathwayDecomposition(IDoubleArray _F, double[] _Q, int[] _A, int[] _B)
    {
        this.Q = _Q;
        this.A = _A;
        this.B = _B;

        IIntGraph g = graphNew.intMatrixGraph(_F);

        this.inA = new boolean[_F.rows()];
        for (int i = 0; i < A.length; i++)
            inA[A[i]] = true;
        
        inB = new boolean[_F.rows()];
        for (int i = 0; i < B.length; i++)
            inB[B[i]] = true;

        // convert double matrix into Big Decimals
        this.F = new SparseObjectMatrix2D(_F.rows(), _F.rows());
        for (int i=0; i<_F.rows(); i++)
            for (int j=0; j<_F.rows(); j++)
                if (_F.get(i,j) != 0)
                    F.set(i,j, new BigDecimal(_F.get(i,j)));

        // collect in- and outflux of every node
        this.influxes = new BigDecimal[_F.rows()];
        this.outfluxes = new BigDecimal[_F.rows()];
        for (int i=0; i<influxes.length; i++)
            {
                influxes[i] = BigDecimal.ZERO;
                outfluxes[i] = BigDecimal.ZERO;
            }

        for (int i=0; i<F.rows(); i++)
            for (int j=0; j<F.rows(); j++)
                {
                    if (F.get(i,j) != null)
                        {
                            influxes[j] = influxes[j].add((BigDecimal)F.get(i,j));
                            outfluxes[i] = outfluxes[i].add((BigDecimal)F.get(i,j));
                        }
                }

        // correct for rounding errors by iterating nodes with increasing committor.
        int[] IQ = doubleArrays.sortedIndexes(Q);
        for (int i=0; i<IQ.length; i++)
            {
                int s = IQ[i]; // this is the current node

                if (influxes[s].equals(BigDecimal.ZERO) ||
                    outfluxes[s].equals(BigDecimal.ZERO))
                    continue;

                BigDecimal err = outfluxes[s].subtract(influxes[s]); // difference between in and out

                // correct outflux.
                int[] neighbors = g.getNeighbors(s).getArray();

                int ilargest = 0;
                double flargest = _F.get(s,neighbors[0]);
                for (int j=1; j<neighbors.length; j++)
                    if (_F.get(s, neighbors[j]) > flargest)
                        {
                            ilargest = j;
                            flargest = _F.get(s, neighbors[j]);
                        }

                F.set(s, neighbors[ilargest],
                      ((BigDecimal)F.get(s, neighbors[ilargest])).subtract(err));
                outfluxes[s] = outfluxes[s].subtract(err);
                influxes[neighbors[ilargest]] = influxes[neighbors[ilargest]].subtract(err);
            }
    }

    public int[][] removeEdge(int[][] set, int[] edge)
    {
	int[][] res = new int[set.length-1][];
	for (int i=0, k=0; i<set.length; i++)
	    if (!(set[i][0] == edge[0] && set[i][1] == edge[1]))
		res[k++] = set[i];
	return(res);
    }

    public int[][] findGap(int[][] pathway, int[] S1, int[] S2)
    {
	if (pathway.length == 0)
	    return(new int[][]{S1,S2});

	if (!intArrays.contains(S1, pathway[0][0]))
	    return(new int[][]{S1,new int[]{pathway[0][0]}});
	
	if (!intArrays.contains(S2, pathway[pathway.length-1][1]))
	    return(new int[][]{new int[]{pathway[pathway.length-1][1]},S2});

	for (int i=0; i<pathway.length-1; i++)
	    if (pathway[i][1] != pathway[i+1][0])
		return(new int[][]{new int[]{pathway[i][1]},
				   new int[]{pathway[i+1][0]}});

	return(null);
    }

    public int[][] insertIntoPathway(int[][] pathway, int[] b)
    {
        // insert by committor
        for (int i=0; i<pathway.length; i++)
        {
            if (Q[b[1]] <= Q[pathway[i][0]] )
            {
        	int[][] res = new int[0][2];
                if (i>0)
                     res = intArrays.subarray(pathway,0,i-1);
                res = intArrays.concat(res, b);
                res = intArrays.concat(res, intArrays.subarray(pathway,i,pathway.length));
                return res;
            }
        }
        
        int[][] res = intArrays.concat(pathway,b);
        return res;

            /*
        if (pathway.length == 0)
            return new int[][]{b};
        
        for (int i=0; i<pathway.length; i++)
        {
            if (pathway[i][0] == b[1])
            {
        	int[][] res = intArrays.concat(intArrays.subarray(pathway,0,i),b);
        	res = intArrays.concat(res, intArrays.subarray(pathway,i,pathway.length));
                return res;
            }
        }
        
        // append at end
        if (pathway[pathway.length-1][1] == b[1])
        {
            int[][] res = intArrays.concat(pathway,b);
            return res;
        }
        else
        {
            throw new RuntimeException("Cannot insert new edge as it does not fit into pathway");
        }*/
    }

    public int[] edges2vertices(int[][] path)
    {
	int[] res = new int[path.length+1];
	for (int i=0; i<path.length; i++)
	    res[i] = path[i][0];
	res[res.length-1] = path[path.length-1][1];
	return(res);
    }

    public BigDecimal computeCurrentFlux()
    {
	BigDecimal res = (BigDecimal)F.get(this.currentPathway[0],
					   this.currentPathway[1]);

	for (int i=1; i<this.currentPathway.length-1; i++)
	    {
		BigDecimal w = (BigDecimal)F.get(this.currentPathway[i],
						 this.currentPathway[i+1]);
		if (w.compareTo(res) < 1)
		    res = w;
	    }
	this.currentFlux = res;
	return(res);
    }

    public void subtractCurrentPath()
    {
	for (int i=0; i<this.currentPathway.length-1; i++)
	    F.set(this.currentPathway[i], this.currentPathway[i+1],
		  ((BigDecimal)F.get(this.currentPathway[i],this.currentPathway[i+1])).subtract(this.currentFlux));
    }

    /**
       Gets the next largest A->B pathway. Returns null if no more pathway is
       available.
     */
    public int[] nextPathway()
    {
        // get current set of edges
        List<int[]> Elist = new ArrayList<int[]>();
        for (int i=0; i<F.rows(); i++)
            for (int j=0; j<F.rows(); j++)
                if (F.get(i,j) != null)
                    if (((BigDecimal)F.get(i,j)).compareTo(BigDecimal.ZERO) > 0)
                        Elist.add(new int[]{i,j});
        int[][] E = intArrays.List2Array2(Elist);

	int[][] pathway = new int[0][];
	int[][] gap = null;
        //System.out.println("NEW PATH");
	while((gap = findGap(pathway, A, B)) != null)
	    {
		// bisection
		Bisection bi = new Bisection(F, E, gap[0], gap[1]);
		//System.out.println(" computing gap: "+intArrays.toString(gap));
		int[] b = bi.bottleneck();

                if (b == null)
                    return null;
                
		//System.out.println(" ["+gap[0][0]+"-"+gap[1][0]+"] bottleneck: "+intArrays.toString(b)+   "w = "+F.get(b[0],b[1]));
		E = removeEdge(E, b);
		pathway = insertIntoPathway(pathway, b);
	    }

	this.currentPathway = edges2vertices(pathway);

	this.computeCurrentFlux();
	this.subtractCurrentPath();

	return(this.currentPathway);
    }

    public int[] getCurrentPathway()
    {
	return(this.currentPathway);
    }

    public BigDecimal getCurrentFlux()
    {
	return(this.currentFlux);
    }

}
class Bisection
{
    int nV = 0;
    int[][] E;
    int[] order;
    int[][] Esorted;

    //int[][] Eleft;
    //int[][] Eright;

    int[] A = null, B = null;
    boolean[] inA = null, inB = null;

    public Bisection(SparseObjectMatrix2D currentF, int[][] _E, int[] _A, int[] _B)
    {
	this.nV = currentF.rows();
	this.E = _E;
	this.A = _A;
	this.B = _B;

	this.inA = new boolean[currentF.rows()];
	for (int i=0; i<A.length; i++)
	    inA[A[i]] = true;
	this.inB = new boolean[currentF.rows()];
	for (int i=0; i<B.length; i++)
	    inB[B[i]] = true;

	// sorts edges
	double[] W = new double[E.length];
	for (int i=0; i<W.length; i++)
	    W[i] = ((BigDecimal)currentF.get(E[i][0],E[i][1])).doubleValue();
	this.order = doubleArrays.sortedIndexes(W);
        
        // reorders edges
        Esorted = new int[E.length][];
        for (int i=0; i<order.length; i++)
        {
            Esorted[i] = E[order[i]];
        }
    }

    /**
       Checks if the current graph structure has a A->B reaction pathway
     */
    public boolean hasConnection(int[][] _E)
    {
	IIntGraph g = graphNew.intListGraph(_E);
	for (int i=0; i<A.length; i++)
	    {
                int[] found = graph.bfs(g, A[i]);
		for (int j=0; j<found.length; j++)
		    if (inB[found[j]])
			return(true);
	    }

	return(false);
    }

    /**
       Gets the bottleneck edge of the flow graph given by E 
       @return [[b1,b2]
     */
    public int[] bottleneck()
    {
        if (Esorted.length == 0)
            return null;
        
	// check fattest edge.
	int[] ec = (int[])(Esorted[Esorted.length-1]);
	if (inA[ec[0]] && inB[ec[1]])
	    return(ec);

	int l = 0, r = Esorted.length-1;
	while (r-l > 1)
	    {
		int m = ((r+l)/2);
		int[][] Esub = intArrays.subarray(Esorted, m, Esorted.length);
		if (hasConnection(Esub))
		    l = m;
		else
		    r = m;
	    }

	ec = (int[])(Esorted[l]);
	return(ec);
    }

    

}