/*
 *  File:
 *  System:
 *  Module:
 *  Author:
 *  Copyright:
 *  Source:              $HeadURL: $
 *  Last modified by:    $Author: $
 *  Date:                $Date: $
 *  Version:             $Revision: $
 *  Description:
 *  Preconditions:
 */
package stallone.api.algebra;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.algebra.MatrixProduct;
import stallone.algebra.InnerProduct;
import stallone.algebra.ArrayNumericalEquality;
import stallone.algebra.ArrayDifference;
import stallone.algebra.ArrayElementDivide;
import stallone.algebra.ArrayNorm;
import stallone.algebra.ArrayElementProduct;
import stallone.algebra.ArrayScale;
import stallone.algebra.ArrayTranspose;
import stallone.algebra.ArraySum;
import stallone.algebra.ScalarNumericalEquality;
import stallone.complex.ComplexNumber;
import stallone.api.complex.*;
import static stallone.doubles.DoubleArrayTest.*;

/**
 * Classes for elementary algebra operations on matrices and vectors.
 *
 * @author  Martin Senne, Tomaso Frigato, Christoph Thöns
 */
public class AlgebraUtilities
{

    private INorm norm = new ArrayNorm();
    private ArraySum vsum = new ArraySum();
    private ArrayDifference vdiff = new ArrayDifference();
    private ArrayScale vscale = new ArrayScale();
    private InnerProduct vdot = new InnerProduct(true);
    private ArrayTranspose trans = new ArrayTranspose();
    private MatrixProduct mprod = new MatrixProduct();
    private ArrayElementProduct elprod = new ArrayElementProduct();
    private ArrayElementDivide eldiv = new ArrayElementDivide();
    private ArrayNumericalEquality arrequal = new ArrayNumericalEquality();
    private ScalarNumericalEquality scalarequal = new ScalarNumericalEquality();

    // **********************************************************************
    //
    // Vector operations
    //
    // **********************************************************************
    public boolean isSquare(IDoubleArray matrix)
    {
        return matrix.rows() == matrix.columns();
    }

    /**
     * Check whether given {@code IDoubleArray} represents a symmetric matrix, i.e.
     * matrix is square and for each tuple i,j with i in 1:rows, j in 1:columns
     *
     * <p>
     * matrix.get(i,j) == matrix.get(j,i) is satisfied.
     * </p>
     *
     * @param matrix The matrix to be checked.
     * @return True if the matrix is symmetric, false otherwise.
     */
    public boolean isSymmetric(IDoubleArray matrix)
    {
        boolean result = isSquare(matrix);

        if (result)
        {
            int dimension = matrix.rows();
            for (int i = 0; i < dimension; i++)
            {
                for (int j = i; j < dimension; j++)
                {
                    if (matrix.get(i, j) != matrix.get(j, i))
                    {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * The entry-wise 2-norm. If v is a vector this is the Euclidean norm, if v is a Matrix
     * this is the Frobenius norm
     * @param v
     * @return
     */
    public double norm(IDoubleArray v)
    {
        return (norm.norm(v));
    }

    /**
     * The entry-wise p-norm.
     * @param v the vector or matrix
     * @param p the order of the norm.
     * @return
     */
    public double norm(IDoubleArray v, int p)
    {
        return (norm.norm(v, p));
    }

    public double distance(IDoubleArray v1, IDoubleArray v2)
    {
        return(norm(subtract(v1,v2)));
    }

    public void addTo(final IComplexArray v1, final IComplexArray v2)
    {
        add(v1, v2, v1);
    }

    public void addTo(final IComplexArray v1, IComplexNumber c)
    {
        for (IComplexIterator it = v1.complexIterator(); it.hasNext(); it.advance())
        {
            it.set(it.getRe() + c.getRe(), it.getIm() + c.getIm());
        }
    }

    public IComplexArray add(IComplexArray v1, IComplexArray v2)
    {
        IComplexArray target = Complex.create.array(v1.size(), v2.size());
        return add(v1, v2, target);
    }

    public IComplexArray add(final IComplexArray v1, final IComplexArray v2, final IComplexArray target)
    {
        vsum.sumDense(v1, v2, target);
        return (target);
    }

    public IComplexArray addWeightedToNew(final double a1, final IComplexArray v1, final double a2, final IComplexArray v2)
    {
        IComplexArray h1 = scaleToNew(a1, v1);
        IComplexArray h2 = scaleToNew(a2, v2);
        addTo(h1, h2);
        return h1;
    }

    public void addTo(final IDoubleArray v1, final IDoubleArray v2)
    {
        add(v1, v2, v1);
    }

    public void addTo(final IDoubleArray v1, double c)
    {
        for (IDoubleIterator it = v1.iterator(); it.hasNext(); it.advance())
        {
            it.set(it.get() + c);
        }
    }

    public IDoubleArray add(IDoubleArray v1, IDoubleArray v2)
    {
        IDoubleArray target = Doubles.create.array(v1.rows(), v1.columns());
        return add(v1, v2, target);
    }

    public IDoubleArray add(final IDoubleArray v1, final IDoubleArray v2, final IDoubleArray target)
    {
        vsum.sumDense(v1, v2, target);
        return (target);
    }

    public IDoubleArray addWeightedToNew(final double a1, final IDoubleArray v1, final double a2, final IDoubleArray v2)
    {
        IDoubleArray h1 = scaleToNew(a1, v1);
        IDoubleArray h2 = scaleToNew(a2, v2);
        addTo(h1, h2);
        return h1;
    }

    public IDoubleArray subtract(final IDoubleArray v1, final IDoubleArray v2)
    {
        return subtract(v1, v2, v1.copy());
    }

    public IDoubleArray subtract(final IDoubleArray v1, final IDoubleArray v2, final IDoubleArray target)
    {
        // Execute the algorithm
        vdiff.subtractDense(v1, v2, target);
        return target;
    }

    public IComplexArray multiplyElementsToNew(IComplexArray arr1, IComplexArray arr2)
    {
        return elprod.multiplyToNewDense(arr1, arr2);
    }

    public IDoubleArray multiplyElementsToNew(IDoubleArray arr1, IDoubleArray arr2)
    {
        if(arr1.isSparse() || arr2.isSparse())
            return elprod.multiplyToNewSparse(arr1, arr2);
        
        return elprod.multiplyToNewDense(arr1, arr2);
    }

    public IDoubleArray divideElementsToNew(IDoubleArray arr1, IDoubleArray arr2)
    {
        return eldiv.divideToNewDense(arr1, arr2);
    }

    public IComplexNumber dotComplex(final IComplexArray v1, final IComplexArray v2)
    {
        return (dotComplexWeighted(v1, v2, null));
    }

    public IComplexNumber dotComplex(final IComplexArray v1, final IComplexArray v2, IComplexNumber target)
    {
        return (dotComplexWeighted(v1, v2, null, target));
    }

    public IComplexNumber dotComplexWeighted(final IComplexArray v1, final IComplexArray v2, final IDoubleArray w, IComplexNumber target)
    {
        return (vdot.innerProduct(v1, v2, w, target));
    }

    public IComplexNumber dotComplexWeighted(final IComplexArray v1, final IComplexArray v2, final IDoubleArray w)
    {
        IComplexNumber target = new ComplexNumber(0, 0);
        vdot.innerProduct(v1, v2, w, target);
        return target;
    }

    public double dot(final IDoubleArray v1, final IDoubleArray v2)
    {
        return (vdot.innerProduct(v1, v2));
    }

    public double dot(final IDoubleArray v1, final IDoubleArray v2, final IDoubleArray w)
    {
        return (vdot.innerProduct(v1, v2, w));
    }

    public void negate(IComplexArray arr)
    {
        scale(-1, arr);
    }

    public void negate(IDoubleArray arr)
    {
        scale(-1, arr);
    }

    public void invertElements(IDoubleArray arr)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            arr.set(i, 1.0 / arr.get(i));
        }
    }

    public void square(IDoubleArray arr)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            arr.set(i, arr.get(i) * arr.get(i));
        }
    }

    public double sum(IDoubleArray arr)
    {
        double res = 0;
        for (IDoubleIterator it = arr.nonzeroIterator(); it.hasNext(); it.advance())
        {
            res += it.get();
        }
        return (res);
    }

    public IDoubleArray rowSums(IDoubleArray arr)
    {
        double[] rowsums = new double[arr.rows()];
        for (IDoubleIterator it = arr.nonzeroIterator(); it.hasNext(); it.advance())
        {
            rowsums[it.row()] += it.get();
        }
        return Doubles.create.array(rowsums);
    }

    public IDoubleArray columnSums(IDoubleArray arr)
    {
        double[] colsums = new double[arr.columns()];
        for (IDoubleIterator it = arr.nonzeroIterator(); it.hasNext(); it.advance())
        {
            colsums[it.column()] += it.get();
        }
        return Doubles.create.array(colsums);
    }

    public IComplexNumber sum(IComplexArray arr)
    {
        double re = 0, im = 0;
        for (IComplexIterator it = arr.nonzeroComplexIterator(); it.hasNext(); it.advance())
        {
            re += it.getRe();
            im += it.getIm();
        }
        return (new ComplexNumber(re, im));
    }

    public IComplexArray scaleToNew(final IComplexNumber s, final IComplexArray v, final IDoubleArray target)
    {
        IComplexArray res = v.copy();
        vscale.scale(v, s, res);
        return res;
    }

    public IComplexArray scaleToNew(final IComplexNumber s, final IComplexArray v)
    {
        return scaleToNew(s, v, v.copy());
    }

    public IComplexArray scaleToNew( final double s, final IComplexArray v)
    {
        return scaleToNew(new ComplexNumber(s, 0), v, v.copy());
    }

    public void scale(IComplexNumber s, IComplexArray v)
    {
        vscale.scale(v, s);
    }

    public void scale(double s, IComplexArray v)
    {
        vscale.scale(v, new ComplexNumber(s, 0));
    }

    public IDoubleArray scaleToNew(final double s, final IDoubleArray v)
    {
        IDoubleArray res = v.copy();
        vscale.scale(v, s, res);
        return res;
    }

    public void scale(double s, IDoubleArray v)
    {
        vscale.scale(v, s);
    }

    /**
     * Scales the rows such that each of them sum up to 1
     */
    public void normalizeRows(final IDoubleArray M, int p)
    {
        double[] rownorms = new double[M.rows()];
        for (int i=0; i<rownorms.length; i++)
            rownorms[i] = norm(M.viewRow(i), p);
        for (IDoubleIterator it = M.nonzeroIterator(); it.hasNext(); it.advance())
            it.set(it.get() / rownorms[it.row()]);
    }

    public void normalize(final IDoubleArray v)
    {
        scale(1.0 / norm(v), v);
    }

    public void normalize(final IDoubleArray v, int p)
    {
        scale(1.0 / norm(v, p), v);
    }

    public IDoubleArray createNormalized(final IDoubleArray v)
    {
        return (scaleToNew(1.0 / norm(v), v));
    }

    public IDoubleArray createNormalized(final IDoubleArray v, int p)
    {
        return (scaleToNew(1.0 / norm(v, p), v));
    }

    // **********************************************************************
    //
    // Matrix-Vector operations
    //
    // **********************************************************************
    public IDoubleArray product(final IComplexArray v, final IComplexArray m)
    {
        return mprod.multiplyToNew(v, m);
    }

    public IComplexArray product(final IComplexArray v, final IComplexArray m, final IComplexArray target)
    {
        mprod.multiply(v, m, target);
        return target;
    }

    public IDoubleArray product(final IDoubleArray v, final IDoubleArray m)
    {
        return mprod.multiplyToNew(v, m);
    }

    public IDoubleArray product(final IDoubleArray v, final IDoubleArray m, final IDoubleArray target)
    {
        mprod.multiply(v, m, target);
        return target;
    }

    /**
     * Calculates M^p explicitly
     * @param m
     * @param p
     * @return
     */
    public IDoubleArray power(final IDoubleArray M, final int p)
    {
        if (p < 0)
        {
            throw (new IllegalArgumentException("Trying to raise matrix to a negative power. Use Matrix inverse explicitly if desired"));
        }
        if (p == 0)
        {
            return (Doubles.create.diag(M.rows(), 1));
        }

        IDoubleArray res = M.copy();
        for (int i = 1; i < p; i++)
        {
            res = product(res, M);
        }

        return (res);
    }

    public IComplexArray transposeToNew(final IComplexArray m)
    {
        return trans.conjugateTransposeToNew(m);
    }

    public void transpose(final IComplexArray m)
    {
        trans.conjugateTranspose(m);
    }

    public IDoubleArray transposeToNew(final IDoubleArray m)
    {
        return trans.transposeToNew(m);
    }

    public void transpose(final IDoubleArray m)
    {
        trans.transpose(m);
    }

    public IDoubleArray inverse(final IDoubleArray m)
    {
        final ILinearMatrixSystem matrixSystem;

        // check that source matrix is square
        if (m.columns() != m.rows())
        {
            throw new IllegalArgumentException("Matrix must be square.");
        }

        final IDoubleArray identity = Doubles.create.identity(m.columns());
        matrixSystem = Algebra.create.linearMatrixSolver(m, identity);

        // calculate and return the inverse matrix
        matrixSystem.perform();

        return matrixSystem.getSolutionMatrix();
    }

    public double det(final IDoubleArray m)
    {
        // perform LU decomposition
        final ILUDecomposition decomposition = Algebra.create.LUSolver(m);
        decomposition.perform();

        // compute determinant and store it in a new IScalar object
        return decomposition.det();
    }

    public double trace(final IDoubleArray M)
    {
        double tr = 0;
        for (int i=0; i<M.rows(); i++)
            tr += M.get(i,i);
        return tr;
    }

    public IEigenvalueDecomposition evd(final IDoubleArray matrix)
    {
        IEigenvalueSolver solver = Algebra.create.eigensolverDense(matrix);
        solver.perform();
        return (solver.getResult());
    }

    public IEigenvalueDecomposition evd(final IDoubleArray matrix, boolean computeLeftEV, boolean computeRightEV)
    {
        IEigenvalueSolver solver = Algebra.create.eigensolverDense(matrix, computeLeftEV, computeRightEV);
        solver.perform();
        return (solver.getResult());
    }

    public IEigenvalueDecomposition evdSparse(final IDoubleArray matrix, int nev)
    {
        IEigenvalueSolver solver = Algebra.create.eigensolverSparse(matrix, nev);
        solver.perform();
        return (solver.getResult());
    }

    public IEigenvalueDecomposition evd(final IDoubleArray matrix, int nev)
    {
        IEigenvalueSolver solver = Algebra.create.eigensolver(matrix, nev);
        solver.perform();
        return (solver.getResult());
    }

    public IEigenvalueDecomposition evd(final IDoubleArray matrix, String algoName)
    {
        IEigenvalueSolver solver = Algebra.create.eigensolver(matrix, algoName);
        solver.perform();
        return (solver.getResult());
    }

    public IDoubleArray solve(final IDoubleArray A, final IDoubleArray b)
    {
        IDoubleArray res = null;
        if (b.order() == 1)
        {
            ILinearSystem solver = Algebra.create.linearSolver(A, b);
            solver.perform();
            res = solver.getSolutionVector();
        }
        else
        {
            ILinearMatrixSystem solver = Algebra.create.linearMatrixSolver(A, b);
            solver.perform();
            res = solver.getSolutionMatrix();
        }
        return (res);
    }

    public IDoubleArray solve(final IDoubleArray A, final IDoubleArray b, String algoName)
    {
        assertOrder(b, 1);

        ILinearSystem solver = Algebra.create.linearSolver(A, b, algoName);
        solver.perform();
        IDoubleArray res = solver.getSolutionVector();
        return (res);
    }

    public boolean numericallyEquals(final IDoubleArray o1, final IDoubleArray o2, final double precision)
    {
        return arrequal.numericallyEqual(o1, o2, precision);
    }

    public boolean numericallyEquals(final IComplexNumber o1, final IComplexNumber o2, final double precision)
    {
        return scalarequal.numericallyEqual(o1, o2, precision);
    }
}
