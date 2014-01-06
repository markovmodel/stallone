/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.coordinates;

import static stallone.api.API.*;

import stallone.api.coordinates.ICoordinateTransform;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public abstract class AbstractCoordinateTransform implements ICoordinateTransform
{
    private int dimension;
    private int order = 1;
    private int dim1, dim2 = 0;
    
    /**
     * Creates 1-dimensional transform with given dimension
     * @param _dimension 
     */
    public AbstractCoordinateTransform(int _dimension)
    {
        this.dimension = _dimension;
        this.dim1 = _dimension;
    }

    /**
     * Creates 2-dimensional transform with given dimensions
     * @param _dim1
     * @param _dim2
     */
    public AbstractCoordinateTransform(int _dim1, int _dim2)
    {
        order = 2;
        this.dim1 = _dim1;
        this.dim2 = _dim2;
        this.dimension = _dim1*_dim2;
    }
    
    @Override
    public IDoubleArray transform(IDoubleArray c)
    {
        IDoubleArray res;
        if (order == 1)
            res = doublesNew.array(dimension);
        else
            res = doublesNew.matrix(dim1,dim2);
        transform(c, res);
        return res;
    }

    @Override
    public int dimension()
    {
        return dimension;
    }
    
}
