/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.datasequence.IDataSequence;
import stallone.api.discretization.IDiscretization;

/**
 *
 * @author noe
 */
public interface IGrid
    extends IDiscretization, IDataSequence
{
    public int getNumberOfGridPoints();
    
    public int getNumberOfGridPoints(int dimension);
    
    /**
     * Yields the index of the nearest data point
     * @param x
     * @return 
     */
    public IIntArray nearestMultiIndex(IDoubleArray x);
    
    public IIntArray getNeighborIndexes(int index);
    
    public IDataSequence getNeighbors(int index);    

    public IIntArray getNeighborMultiIndexes(int... indexes);    
    
    /**
     * Yields the index of the given multi index
     * @param x
     * @return 
     */
    public int getIndex(IIntArray indexes);

    /**
     * Yields the nearest data point
     * @param x
     * @return 
     */
    public IDoubleArray getPoint(IIntArray indexes);
    
    /**
     * Yields the index of the nearest data point
     * @param x
     * @return 
     */
    public int getIndex(int... indexes);

    /**
     * Yields the nearest data point
     * @param x
     * @return 
     */
    public IDoubleArray getPoint(int... indexes);
    
    
    
    
}
