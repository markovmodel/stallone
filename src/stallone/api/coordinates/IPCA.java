/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IPCA extends ICoordinateTransform
{
    /**
     * Sets the dimensionality of the principal subspace. 
     * transform(IDoubleArray) will project onto a subspace of this size. When not
     * set (default value 0), no dimension reduction will be done.
     * @param d The dimension of the principal subspace
     */
    public void setDimension(int d);

    /**
     * Returns the eigenvalues of the covariance matrix, representing the variances
     * of the data along the principal components.
     * @return 
     */
    public IDoubleArray getEigenvalues();

    /**
     * Returns principal component i. The principal components are sorted by descending variance
     * @param i
     * @return 
     */
    public IDoubleArray getEigenvector(int i);

    /**
     * Returns the entire set of principal components. Can be used as a transformation matrix
     * @return 
     */
    public IDoubleArray getEigenvectorMatrix();

    @Override
    public IDoubleArray transform(IDoubleArray x);    
}
