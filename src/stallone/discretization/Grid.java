/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.discretization;

import static stallone.api.API.*;

import stallone.doubles.DoubleArrayView;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.ints.IntArrayView;
import stallone.ints.PrimitiveIntArray;
import stallone.ints.PrimitiveIntTable;
import stallone.api.datasequence.IDataSequence;
import stallone.datasequence.DataSequenceResizable;
import java.util.Iterator;
import stallone.api.function.IGrid;
import stallone.util.Counter;



/**
 *
 * @author noe
 */
public class Grid implements IGrid
{
    private int dim;
    private double[] left, step, right, width;
    private IDoubleArray[] pointPositions;
    // pre-computed sizes of the grid in each dimension
    private int[] sizes;
    // pre-computed significance numbers that are used to calculate single indexes
    private int[] numbers;

    // pre-constructed holder for neighbor indexes
    private IntArrayView preNeighborIndexes;
    // pre-constructed holder for neighbor indexes
    private IntArrayView preMultIndex;
    // pre-constructed holder for a data point
    private DoubleArrayView prePoint;
    // pre-constructed neighbor index holder
    private IntArrayView preNeighborMultIndexes;
    // pre-constructed neighbor holder
    private DataSequenceResizable preNeighbors;
    // pre-constructed index holder
    private int[] multIndex;

    public Grid(IDoubleArray griddef)
    {
        init(griddef);
    }

    public Grid(IDoubleArray bounds, double _step)
    {
        IDoubleArray griddef = doublesNew.matrix(bounds.rows(), 3);
        for (int i=0; i<griddef.rows(); i++)
        {
            griddef.set(i,0,bounds.get(i,0));
            griddef.set(i,1,_step);
            griddef.set(i,2,bounds.get(i,1));
        }
        init(griddef);
    }

    /**
     * Constructs grid from [xmin, dx, xmax], [ymin, dy, ymax], ...
     * @param griddef
     */
    private void init(IDoubleArray griddef)
    {
        this.dim = griddef.rows();
        left = new double[dim];
        step = new double[dim];
        right = new double[dim];
        width = new double[dim];
        pointPositions = new IDoubleArray[dim];
        sizes = new int[dim];
        numbers = new int[dim];
        multIndex = new int[dim];
        for (int i=0; i<dim; i++)
        {
            left[i] = griddef.get(i,0);
            step[i] = griddef.get(i,1);
            right[i] = griddef.get(i,2);
            width[i] = right[i]-left[i];
            pointPositions[i] = Doubles.create.arrayRange(left[i]+(step[i]/2), right[i]-(step[i]/2)+(1e-6*step[i]), step[i]);
            sizes[i] = pointPositions[i].size();
        }

        numbers[dim-1] = 1;
        for (int i=(dim-2); i>=0; i--)
            numbers[i] = numbers[i+1] * sizes[i];
        preNeighborIndexes = new IntArrayView(new PrimitiveIntArray(dim*2), 0, dim*2);
        preMultIndex = new IntArrayView(new PrimitiveIntArray(dim), 0, dim);
        prePoint = new DoubleArrayView(doublesNew.array(dim),0, dim);
        preNeighborMultIndexes = new IntArrayView(new PrimitiveIntTable(dim*2,dim), 0, 0, dim*2, dim);
    }

    @Override
    public int getNumberOfGridPoints()
    {
        int n = 1;
        for (int i=0; i<pointPositions.length; i++)
            n *= pointPositions[i].size();
        return(n);
    }

    @Override
    public int getNumberOfGridPoints(int dimension)
    {
        return(pointPositions[dimension].size());
    }

    private int[] getMultiIndex(int index)
    {
        int rest = index;
        for (int i=0; i<multIndex.length; i++)
        {
            multIndex[i] = rest / numbers[i];
            rest = rest % numbers[i];
        }
        return(multIndex);
    }

    @Override
    public int getIndex(int... indexes)
    {
        int res = 0;

        for (int i=0; i<multIndex.length; i++)
            res += indexes[i]*numbers[i];
        return(res);
    }


    @Override
    public int getIndex(IIntArray indexes)
    {
        return(getIndex(indexes.getArray()));
    }

    @Override
    public IDoubleArray getPoint(IIntArray indexes)
    {
        for (int i=0; i<indexes.size(); i++)
            prePoint.set(i, pointPositions[i].get(indexes.get(i)));
        return(prePoint);
    }

    @Override
    public IDoubleArray getPoint(int... indexes)
    {
        for (int i=0; i<indexes.length; i++)
            prePoint.set(i, pointPositions[i].get(indexes[i]));
        return(prePoint);
    }

    @Override
    public IIntArray nearestMultiIndex(IDoubleArray x)
    {
        int size = x.size();
        for (int i=0; i<size; i++)
        {
            double xc = x.get(i);

            int ic = (int)((xc-left[i])/step[i]);
            if (ic < 0)
                ic = 0;
            if (ic > sizes[i]-1)
                ic = sizes[i]-1;

            preMultIndex.set(i, ic);
        }

        return(preMultIndex);
    }

    @Override
    public int assign(IDoubleArray x)
    {
        return(getIndex(nearestMultiIndex(x)));
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray x)
    {
        return(getPoint(nearestMultiIndex(x)));
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray x)
    {
        IDoubleArray res = Doubles.create.sparseColumn(size());
        res.set(assign(x), 1.0);
        return res;
    }

    @Override
    public int size()
    {
        int res = 1;
        for (int i=0; i<sizes.length; i++)
            res *= sizes[i];
        return(res);
    }

    @Override
    public int dimension()
    {
        return dim;
    }

    @Override
    public IDoubleArray get(int i)
    {
        return(getPoint(getMultiIndex(i)));
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return(get(i));
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(new GridPointIterator(sizes, pointPositions));
    }

    
    @Override
    public IIntArray getNeighborIndexes(int index)
    {
        IIntArray neighborMultiIndexes = getNeighborMultiIndexes(getMultiIndex(index));
        preNeighborIndexes.setView(0, neighborMultiIndexes.rows());
        for (int i=0; i<neighborMultiIndexes.rows(); i++)
            preNeighborIndexes.set(i, getIndex(neighborMultiIndexes.getRow(i)));
        return(preNeighborIndexes);
    }

    @Override
    public IIntArray getNeighborMultiIndexes(int... indexes)
    {
        int nneighbors = 0;
        for (int i=0; i<dim; i++)
        {
            // left neighbor
            if (indexes[i] > 0)
            {
                indexes[i] -= 1;
                for (int j=0; j<dim; j++)
                {
                    preNeighborMultIndexes.set(nneighbors, j, indexes[j]);
                }
                nneighbors ++;
                indexes[i] += 1;
            }
            // right neighbor
            if (indexes[i] < sizes[i]-1)
            {
                indexes[i] += 1;
                for (int j=0; j<dim; j++)
                {
                    preNeighborMultIndexes.set(nneighbors, j, indexes[j]);
                }
                nneighbors ++;
                indexes[i] -= 1;
            }
        }

        preNeighborMultIndexes.setView(0, 0, nneighbors, dim);

        return(preNeighborMultIndexes);
    }

    @Override
    public IDataSequence getNeighbors(int index)
    {
        IIntArray neighborMultiIndexes = getNeighborMultiIndexes(getMultiIndex(index));
        preNeighbors.setSize(neighborMultiIndexes.size());
        for (int i=0; i<preNeighbors.size(); i++)
            preNeighbors.set(i, getPoint(neighborMultiIndexes.get(i)));
        return(preNeighbors);
    }

    @Override
    public double getTime(int i)
    {
        return i;
    }

    @Override
    public Iterator<IDoubleArray[]> pairIterator(int spacing)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterable<IDoubleArray[]> pairs(int spacing)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
class GridPointIterator implements Iterator<IDoubleArray>
{
    Counter count;
    boolean overflow = false;
    IDoubleArray[] pointPositions;

    int dim;
    IDoubleArray prePoint;

    public GridPointIterator(int[] sizes, IDoubleArray[] pointPositions)
    {
         count = new Counter(Ints.create.arrayFrom(sizes));
         dim = sizes.length;
         prePoint = Doubles.create.array(sizes.length);
    }

    @Override
    public boolean hasNext()
    {
        return(!overflow);
    }

    @Override
    public IDoubleArray next()
    {
        IIntArray c = count.get();

        for (int i=0; i<dim; i++)
            prePoint.set(i, pointPositions[i].get(c.get(i)));

        overflow = count.inc();

        return(prePoint);
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
