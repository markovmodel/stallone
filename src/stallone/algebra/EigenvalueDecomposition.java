/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.algebra;

import stallone.api.algebra.IComplexNumber;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.complex.Complex;
import stallone.api.complex.IComplexArray;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.complex.ComplexNumber;

/**
 *
 * Data container for eigenvalue decomposition of a matrix
 *
 * @author noe
 */
public class EigenvalueDecomposition implements IEigenvalueDecomposition
{

    private IComplexArray L, eval, R;
    private int fullRank = 0, availableEigenvalues = 0;
    private IIntArray order;
    // precomputed stuff
    private int[] fullRankEnum;

    public EigenvalueDecomposition(IComplexArray _L, IComplexArray _eval, IComplexArray _R)
    {
        L = _L;
        eval = _eval;
        R = _R;

        // determine rank and available eigenvalues.
        if (_L != null)
        {
            availableEigenvalues = _L.rows();
            fullRank = _L.columns();
            // check consistency.
            if (_R != null)
                if (_R.rows() != fullRank || _R.columns() != availableEigenvalues)
            {
                throw new IllegalArgumentException("Inconsistent sizes of eigenvector matrices or eigenvalues: (" + L.rows() + "x" + L.columns() + "  (" + eval.size() + ")  " + R.rows() + "x" + R.columns());
            }
        }

        // determine rank and available eigenvalues.
        if (_R != null)
        {
            availableEigenvalues = _R.columns();
            fullRank = _R.rows();
            // check consistency.
            if (_L != null)
                if (_L.columns() != fullRank || _L.rows() != availableEigenvalues)
            {
                throw new IllegalArgumentException("Inconsistent sizes of eigenvector matrices or eigenvalues: (" + L.rows() + "x" + L.columns() + "  (" + eval.size() + ")  " + R.rows() + "x" + R.columns());
            }
        }

        if (_L == null && _R == null)
        {
            availableEigenvalues = _eval.size();
            fullRank = _eval.size();
        }
        // check consistency.
        if (_eval.size() != availableEigenvalues || _eval.order() != 1)
        {
            throw new IllegalArgumentException("Inconsistent sizes of eigenvalue matrix: (" + eval.size() + ")");
        }

        // sort
        order = Ints.create.arrayRange(availableEigenvalues);
        sortNormDescending();

        // precompute stuff
        fullRankEnum = Ints.create.arrayRange(fullRank).getArray();
    }

    @Override
    public int availableEigenpairs()
    {
        return availableEigenvalues;
    }

    @Override
    public int fullRank()
    {
        return fullRank;
    }

    @Override
    public final void sortNormAscending()
    {
        IDoubleArray evalNorm = getEvalNormUnsorted();
        order = Doubles.util.sortedIndexes(evalNorm);
    }

    @Override
    public final void sortNormDescending()
    {
        IDoubleArray evalNorm = getEvalNormUnsorted();
        order = Doubles.util.sortedIndexes(evalNorm);
        Ints.util.mirror(order);
    }

    @Override
    public final void sortRealAscending()
    {
        IDoubleArray evalNorm = eval.viewReal();
        order = Doubles.util.sortedIndexes(evalNorm);
    }

    @Override
    public final void sortRealDescending()
    {
        IDoubleArray evalNorm = eval.viewReal();
        order = Doubles.util.sortedIndexes(evalNorm);
        Ints.util.mirror(order);
    }

    @Override
    public IComplexArray getRightEigenvectorMatrix()
    {
        return R.view(fullRankEnum, order.getArray());
    }

    @Override
    public IComplexArray getRightEigenvector(int i)
    {
        return R.viewColumn(order.get(i));
    }

    @Override
    public IComplexArray getLeftEigenvectorMatrix()
    {
        return L.view(order.getArray(), fullRankEnum);
    }

    @Override
    public IComplexArray getLeftEigenvector(int i)
    {
        return L.viewRow(order.get(i));
    }

    @Override
    public IComplexArray getDiagonalMatrix()
    {
        return Complex.create.diag(eval.view(order.getArray(), new int[]
                {
                    0
                }));
    }

    @Override
    public IComplexArray getEval()
    {
        return eval.view(order.getArray(), new int[]
                {
                    0
                });
    }

    @Override
    public IComplexNumber getEval(int i)
    {
        return new ComplexNumber(eval.getRe(order.get(i)), eval.getIm(order.get(i)));
    }

    @Override
    public IDoubleArray getEvalRe()
    {
        return getEval().viewReal();
    }

    @Override
    public double getEvalRe(int i)
    {
        return eval.getRe(order.get(i));
    }

    @Override
    public IDoubleArray getEvalIm()
    {
        return getEval().viewImaginary();
    }

    @Override
    public double getEvalIm(int i)
    {
        return eval.getIm(order.get(i));
    }

    @Override
    public IComplexArray R()
    {
        return getRightEigenvectorMatrix();
    }

    @Override
    public IComplexArray L()
    {
        return getLeftEigenvectorMatrix();
    }

    @Override
    public IComplexArray D()
    {
        return getDiagonalMatrix();
    }

    private IDoubleArray getEvalNormUnsorted()
    {
        IDoubleArray res = Doubles.create.array(availableEigenvalues);
        for (int i = 0; i < availableEigenvalues; i++)
        {
            double re = eval.getRe(i);
            double im = eval.getIm(i);
            res.set(i, Math.sqrt(re * re + im * im));
        }
        return res;
    }

    @Override
    public IDoubleArray getEvalNorm()
    {
        IDoubleArray res = Doubles.create.array(availableEigenvalues);
        for (int i = 0; i < availableEigenvalues; i++)
        {
            double re = eval.getRe(order.get(i));
            double im = eval.getIm(order.get(i));
            res.set(i, Math.sqrt(re * re + im * im));
        }
        return res;
    }

    @Override
    public double getEvalNorm(int i)
    {
        double re = eval.getRe(order.get(i));
        double im = eval.getIm(order.get(i));
        return Math.sqrt(re * re + im * im);
    }

    @Override
    public boolean hasLeftEigenvectors()
    {
        return (L != null);
    }

    @Override
    public boolean hasRightEigenvectors()
    {
        return (R != null);
    }
}
