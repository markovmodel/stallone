package stallone.api.algebra;

import stallone.api.complex.IComplexArray;



/**
 * Interface IPartialEigensystem provides access to computed eigenvalues and eigenvectors.
 *
 * @author  Martin Senne
 */
public interface IPartialEigensystem {

    /**
     * Get left complex eigenvector with index <code>i</code>.
     *
     * @param   i  the index of the eigenvector to retrieve.
     *
     * @return  left eigenvector
     */
    public IComplexArray getLeftEigenvector(int i);

    /**
     * Get right eigenvector with index <code>i</code>.
     *
     * @param   i  the index of the eigenvector to retrieve.
     *
     * @return  right eigenvector
     */
    public IComplexArray getRightEigenvector(int i);

    /**
     * Get eigenvalue <code>i</code>.
     *
     * @param   i  determines the eigenvalue to retrieve.
     *
     * @return  eigenvalue.
     */
    public IComplexNumber getEigenvalue(int i);

    /**
     * Get number of availabke (computed) eigenvectors.
     *
     * @return  number of available eigenvector
     */
    public int getNumberOfAvailableEigenvectors();

}
