package stallone.doubles;

import java.util.Arrays;

import stallone.api.doubles.IDoubleArray;

/**
 * Sparse diagonal matrix
 *
 * @author marscher
 *
 */
public class DiagonalMatrix extends AbstractDoubleArray
{
    private double[] diagonal;

    /**
     * directly creates a sparse diagonal matrix out of the given diagonal
     * vector
     *
     * @param diagonal
     */
    public DiagonalMatrix(double[] diagonal)
    {
        this.diagonal = diagonal;
    }

    /**
     * creates a sparse diagonal matrix out of the diagonal of the given matrix
     *
     * @param matrixOrVector
     */
    public DiagonalMatrix(IDoubleArray matrixOrVector)
    {
        switch (matrixOrVector.order())
        {
        case 1:
            diagonal = matrixOrVector.getArray();
            break;
        case 2:
        {
            if (matrixOrVector.rows() != matrixOrVector.columns())
                throw new IllegalArgumentException("no symetric matrix given");

            diagonal = new double[matrixOrVector.rows()];

            for (int i = 0; i < matrixOrVector.rows(); i++)
            {
                diagonal[i] = matrixOrVector.get(i, i);
            }
        }
        default:
            throw new IllegalArgumentException("tensors not impled.");
        }
    }

    @Override
    public int rows()
    {
        return diagonal.length;
    }

    @Override
    public int columns()
    {
        return diagonal.length;
    }

    @Override
    public double get(int i, int j)
    {
        assert (i < diagonal.length && j < diagonal.length);

        if (i == j)
            return diagonal[i];

        return 0;
    }

    @Override
    public void set(int i, int j, double x)
    {
        assert (i < diagonal.length && j < diagonal.length);
        if (i == j)
            diagonal[i] = x;
        else
            throw new IllegalArgumentException(
                    "trying to set off diagonal element" + " of sparse matrix");
    }

    @Override
    public IDoubleArray copy()
    {
        double[] diag = Arrays.copyOf(this.diagonal, this.diagonal.length);
        return new DiagonalMatrix(diag);
    }

    @Override
    public IDoubleArray create(int size)
    {
        return new DiagonalMatrix(new double[size]);
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        return new DiagonalMatrix(new double[rows]);
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

}
