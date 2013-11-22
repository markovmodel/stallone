/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.algebra;

import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IEigenvalueDecomposition
{
    /**
     * Returns the number of eigenvalue / eigenvector pairs available. If the decomposition has full rank, size = dimension.
     * @return
     */
    public int availableEigenpairs();

    /**
     * Returns the size of the decomposed matrix.
     * @return
     */
    public int fullRank();

    /**
     * Sorts eigenpairs such that the eigenvalue norms are ascending.
     */
    public void sortNormAscending();

    /**
     * Sorts eigenpairs such that the eigenvalue norms are descending.
     */
    public void sortNormDescending();

    /**
     * Sorts eigenpairs such that the eigenvalue real parts are ascending.
     */
    public void sortRealAscending();

    /**
     * Sorts eigenpairs such that the eigenvalue real parts are descending.
     */
    public void sortRealDescending();

    public boolean hasLeftEigenvectors();
    public boolean hasRightEigenvectors();

    /**
     * Gets the right complex eigenvector matrix, if available.
     * By convention, this matrix has the eigenvectors in the columns.
     */
    public IComplexArray getRightEigenvectorMatrix();

    /**
     * Same as getLeftEigenvectorMatrix.
     */
    public IComplexArray R();

    public IComplexArray getRightEigenvector(int i);

    /**
     * Gets the left complex eigenvector matrix, if available.
     * By convention, this matrix has the eigenvectors in the rows.
     */
    public IComplexArray getLeftEigenvectorMatrix();

    /**
     * Same as getLeftEigenvectorMatrix.
     */
    public IComplexArray L();

    public IComplexArray getLeftEigenvector(int i);

    /**
     * Returns the diagonal matrix with eigenvalues on the diagonal.
     * @return
     */
    public IComplexArray getDiagonalMatrix();

    /**
     * Same as getDiagonalMatrix.
     */
    public IComplexArray D();

    /**
     * Get eigenvalues.
     *
     * @return  complex vector of eigenvalues
     */
    public IComplexArray getEval();

    public IComplexNumber getEval(int i);

    public IDoubleArray getEvalNorm();

    public double getEvalNorm(int i);

    public IDoubleArray getEvalRe();

    public double getEvalRe(int i);

    public IDoubleArray getEvalIm();

    public double getEvalIm(int i);
}
