/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import stallone.api.doubles.IDoubleArray;
import stallone.coordinates.MinimalRMSDistance3D;

/**
 *
 * @author noe
 */
public class CoordinateUtilities
{
    MinimalRMSDistance3D minrmsd = null;

    /**
     * Computes the minimal root mean square distance between x1 and x2.
     * I.e. the result is minrmsd(x1,x2) = sqrt(|x1-x2'|^2 / N), 
     * where x2' is a x2 aligned to x1, such minrmsd(x1,x2) is minimal.
     * Based on: Douglas L. Theobald Rapid calculation of RMSDs using a 
     * quaternion-based characteristic polynomial Acta Crystallographica Section A, 
     * Foundations of Crystallography ISSN 0108-7673 Department of Chemistry
     * and Biochemistry, University of Colorado at Boulder, 
     * Boulder, CO 80309-0215, USA. Correspondence e-mail: theobal@colorado.edu
     * @param x1 coordinate set 1, a Nx3 matrix
     * @param x2 coordinate set 2, a Nx3 matrix
     * @return minrmsd(x1,x2)
     */
    public double minRMSD(IDoubleArray x1, IDoubleArray x2)
    {
        int N = x1.rows();
        if (minrmsd == null)
            minrmsd = new MinimalRMSDistance3D(N);
        if (minrmsd.getN() != N)
            minrmsd = new MinimalRMSDistance3D(N);
        return minrmsd.distance(x1, x2);
    }
}
