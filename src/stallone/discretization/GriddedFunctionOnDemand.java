/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.discretization;

import static stallone.api.API.*;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.datasequence.IDataSequence;
import stallone.api.function.*;
import java.util.Iterator;

/**
 * A grid discretization which calculates functional values on demand and
 * does not store anything.
 * @author noe
 */
public class GriddedFunctionOnDemand
    implements IGriddedFunction
{
    private IFunction F;
    private IGrid grid;

    /**
     *
     * @param _F an n-variate function to be discretized
     * @param bounds A n x 2 table containing lower and upper bounds for the discretization range
     * @param boxsize The size of a single discretization box in each dimension
     */
    public GriddedFunctionOnDemand(IFunction _F, IDoubleArray bounds, double boxsize)
    {
        this.F = _F;
        // check bounds
        if (bounds.rows() != _F.getNumberOfVariables())
            throw(new IllegalArgumentException("Number of variables in function is different from number of discretization bounds provided"));

        this.grid = new Grid(bounds, boxsize);
    }

    public GriddedFunctionOnDemand(IFunction _F, IDoubleArray griddef)
    {
        this.F = _F;

//        int dof = _F.getNumberOfVariables();

        this.grid = new Grid(griddef);
    }

    @Override
    public double f(int... indexes)
    {
        if (indexes.length != F.getNumberOfVariables())
            throw new IllegalArgumentException("Wrong number of variables.");

        IDoubleArray x = grid.getPoint(indexes);
        return(F.f(x));
    }

    @Override
    public double f(IIntArray indexes)
    {
        return(f(indexes.getArray()));
    }

    @Override
    public int getNumberOfGridPoints()
    {
        return(grid.getNumberOfGridPoints());
    }

    @Override
    public int getNumberOfGridPoints(int dimension)
    {
        return(grid.getNumberOfGridPoints(dimension));
    }

    @Override
    public IIntArray getNeighborIndexes(int index)
    {
        return(grid.getNeighborIndexes(index));
    }

    @Override
    public IDataSequence getNeighbors(int index)
    {
        return(grid.getNeighbors(index));
    }

    @Override
    public int assign(IDoubleArray x)
    {
        return(grid.assign(x));
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray x)
    {
        return(grid.assignFuzzy(x));
    }

    @Override
    public IDoubleArray getRepresentative(IDoubleArray x)
    {
        return(grid.getRepresentative(x));
    }

    @Override
    public int getIndex(IIntArray indexes)
    {
        return(grid.getIndex(indexes));
    }

    @Override
    public IDoubleArray getPoint(IIntArray indexes)
    {
        return(grid.getPoint(indexes));
    }

    @Override
    public int getIndex(int... indexes)
    {
        return(getIndex(indexes));
    }

    @Override
    public IDoubleArray getPoint(int... indexes)
    {
        return(getPoint(indexes));
    }

    @Override
    public int size()
    {
        return(grid.size());
    }

    @Override
    public int dimension()
    {
        return grid.dimension();
    }

    @Override
    public IDoubleArray get(int i)
    {
        return(grid.get(i));
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return(grid.iterator());
    }

    @Override
    public double f(IDoubleArray x)
    {
        IDoubleArray y = grid.getRepresentative(x);
        return(F.f(y));
    }

    @Override
    public double f(double... x)
    {
        return(f(doublesNew.array(x)));
    }

    @Override
    public int getNumberOfVariables()
    {
        return(F.getNumberOfVariables());
    }

    @Override
    public IIntArray nearestMultiIndex(IDoubleArray x)
    {
        return(grid.nearestMultiIndex(x));
    }

    @Override
    public IIntArray getNeighborMultiIndexes(int... indexes)
    {
        return(grid.getNeighborMultiIndexes(indexes));
    }

    @Override
    public IDoubleArray getView(int i)
    {
        return(get(i));
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
