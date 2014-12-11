package stallone.graph;

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import stallone.api.ints.IIntElement;
import stallone.ints.IntArrayIterator;
import stallone.ints.IntElement;
import stallone.api.graph.IIntGraph;
import stallone.api.graph.IIntEdge;
import java.util.Iterator;
import stallone.api.doubles.IDoubleElement;
import stallone.api.ints.*;




/**
 *
 * @author  Frank Noe
 */
public class MatrixIntGraph implements IIntGraph
{
    private IDoubleArray matrix;

    public MatrixIntGraph(IDoubleArray matrix)
    {
        this.matrix = matrix;
        if (matrix.rows() != matrix.columns())
        {
            throw new IllegalArgumentException("Supplied matrix is not square (n x n).");
        }
    }

    @Override
    public boolean contains(int vertex)
    {
        return (0 <= vertex) && (vertex < matrix.rows());
    }

    @Override
    public boolean contains(int vertex1, int vertex2)
    {
        return(matrix.get(vertex1,vertex2) > 0);
    }

    @Override
    public IIntIterator nodeIterator()
    {
        IntArrayIterator it = new IntArrayIterator(Ints.create.arrayRange(matrix.rows()));
        return(it);
    }

    @Override
    public IIntIterator neighborIterator(int node)
    {
        IDoubleArray row = matrix.viewRow(node);
        return(new MatrixGraphNeighborIterator(node, row.nonzeroIterator()));
    }

    @Override
    public Iterator<IIntEdge> edgeIterator()
    {
        return(new MatrixGraphEdgeIterator(matrix, matrix.nonzeroIterator()));
    }

    @Override
    public int numberOfNodes()
    {
        return(matrix.rows());
    }

    @Override
    public int numberOfArcs()
    {
        int n = 0;
        for (IDoubleIterator it = matrix.nonzeroIterator(); it.hasNext(); it.advance())
            n++;
        return(n);
    }

    @Override
    public IIntArray getNodes()
    {
        return(Ints.create.arrayRange(matrix.rows()));
    }

    @Override
    public IIntArray getNeighbors(int node)
    {
        IIntList res = intsNew.list(5);
        for (int i=0; i<matrix.columns(); i++)
            if (matrix.get(node,i) != 0)
                res.append(i);
        return res;
    }
}

class MatrixGraphNeighborIterator implements IIntIterator
{
    private int myself;
    private IDoubleIterator it;
    
    private IDoubleElement next = null;

    public MatrixGraphNeighborIterator(int _myself, IDoubleIterator _it)
    {
        this.myself = _myself;
        this.it = _it;
        
        advance();
    }

    @Override
    public boolean hasNext()
    {
        return(next != null);
    }

    @Override
    public IIntElement next()
    {
        IIntElement ie = new IntElement(next.row(), next.column(), next.index(), next.index());
        advance();
        return ie;
    }

    @Override
    public int get()
    {
        return(next.index());
    }

    @Override
    public void reset()
    {
        it.reset();
        advance();
    }

    @Override
    public void advance()
    {
        if (this.it.hasNext())
        {
            this.next = this.it.next();
            // skip self
            if (this.next.index() == myself)
            {
                if (this.it.hasNext())
                    this.next = this.it.next();
                else
                    this.next = null;
            }
        }
        else
            this.next = null;
    }

    @Override
    public int getIndex()
    {
        return(next.index());
    }

    @Override
    public int row()
    {
        return(next.row());
    }

    @Override
    public int column()
    {
        return(next.column());
    }

    @Override
    public void set(int x)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


//class MatrixGraphNeighborIterator implements IIntIterator
//{
//    private int myself;
//    private IDoubleIterator it;
//
//    public MatrixGraphNeighborIterator(int _myself, IDoubleIterator _it)
//    {
//        this.myself = _myself;
//        this.it = _it;
//        skipmyself();
//    }
//
//    @Override
//    public boolean hasNext()
//    {
//        return(it.hasNext());
//    }
//
//    @Override
//    public IIntElement next()
//    {
//        it.advance();
//        return(new IntElement(it.row(), it.column(), it.getIndex(), it.getIndex()));
//    }
//
//    @Override
//    public int get()
//    {
//        return(it.getIndex());
//    }
//
//    @Override
//    public void reset()
//    {
//        it.reset();
//    }
//
//    private void skipmyself()
//    {
//        if (it.getIndex() == myself)
//            it.advance();
//    }
//
//    @Override
//    public void advance()
//    {
//        it.advance();
//        skipmyself();
//    }
//
//    @Override
//    public int getIndex()
//    {
//        return(it.getIndex());
//    }
//
//    @Override
//    public int row()
//    {
//        return(it.row());
//    }
//
//    @Override
//    public int column()
//    {
//        return(it.column());
//    }
//
//    @Override
//    public void set(int x)
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    @Override
//    public void remove()
//    {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//}

class MatrixGraphEdgeIterator implements Iterator<IIntEdge>
{
    private IDoubleArray M;
    private IDoubleIterator it;
    private IIntEdge e;

    public MatrixGraphEdgeIterator(IDoubleArray M, IDoubleIterator _it)
    {
        this.it = _it;
        e = new IntEdge(-1,-1);
    }

    @Override
    public boolean hasNext()
    {
        return(it.hasNext());
    }

    @Override
    public IIntEdge next()
    {
        it.advance();
        e.setV1(it.row());
        e.setV2(it.column());
        e.setWeight(it.get());
        return(e);
    }

    @Override
    public void remove()
    {
        M.set(e.getV1(),e.getV2(),0);
    }

}