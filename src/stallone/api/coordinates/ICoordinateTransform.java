/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * Defines a map from one coordinate set to another coordinate set. 
 * Coordinate transforms include coordinate selection, linear projection (e.g. PCA),
 * calculation of nonlinear parameters (distances, angles, etc), projection onto
 * a basis set, etc.
 * 
 * @author noe
 */
public interface ICoordinateTransform
{
    /**
     * maps a single coordinate set using the current coordinate transform.
     * Default behavior: will call compute() if compute() has not yet been called, 
     * or if data has been added after the last compute().
     * @param c a coordinate set
     * @return the transformed coordinate set
     */
    public IDoubleArray transform(IDoubleArray c);

    /**
     * Same as transform(IDoubleArray), but writes the result into the provided
     * output array
     * @param in
     * @param out 
     */
    public void transform(IDoubleArray in, IDoubleArray out);
    
    /**
     * Returns the output dimension of this transform
     * @return 
     */
    public int dimension();
}
