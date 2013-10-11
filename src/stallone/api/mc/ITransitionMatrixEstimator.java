/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.mc;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface ITransitionMatrixEstimator
{
    /**
     * Sets the posterior count matrix (observed + prior counts)
     * @param C posterior count matrix
     */
    public void setCounts(IDoubleArray C);

    public void estimate();

    public IDoubleArray getTransitionMatrix();
}
