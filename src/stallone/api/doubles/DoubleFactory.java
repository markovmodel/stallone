/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import stallone.api.strings.Strings;
import stallone.doubles.*;

/**
 *
 * @author noe
 */
public class DoubleFactory
{
    // ***********************************************************************
    //
    // Generic array creation. Initializes to 0
    //
    // ***********************************************************************


    public IDoubleArray denseColumn(int size)
    {
        return (new DenseDoubleArray(new double[size]));
    }

    //TODO: create Row and Column Version of 1D-Arrays.
    public IDoubleArray denseRow(int size)
    {
        return (new DenseDoubleArray(1,size));
    }

    public IDoubleArray sparseColumn(int size)
    {
        return (new SparseRealVector(size));
    }

    public IDoubleArray sparseRow(int size)
    {
        return (new SparseRealMatrix(1,size));
    }

    public IDoubleArray column(int size)
    {
        if (size < 25000000)
            return denseColumn(size);
        else
            return sparseColumn(size);
    }

    public IDoubleArray row(int size)
    {
        if (size < 25000000)
            return denseRow(size);
        else
            return sparseRow(size);
    }

    public IDoubleArray array(int size)
    {
        return(column(size));
    }

    public IDoubleArray array(double[] init)
    {
        return (new DenseDoubleArray(init));
    }

    // TODO:
    // create from lists
    // create primitive types from IDoubleArray, IDoubleMatrix
    public IDoubleArray denseMatrix(int nrows, int ncols)
    {
        return (new DenseDoubleArray(nrows,ncols));
    }

    // TODO:
    // create from lists
    // create primitive types from IDoubleArray, IDoubleMatrix
    public IDoubleArray sparseMatrix(int nrows, int ncols)
    {
        return (new SparseRealMatrix(nrows, ncols));
    }

    public IDoubleArray array(double[][] init)
    {
        return (new DenseDoubleArray(init));
    }

    /**
     * Generic array creator. Creates a dense vector or matrix when rows and cols
     * are less than 5000, otherwise sparse
     * @param rows
     * @param cols
     * @return
     */
    public IDoubleArray array(int rows, int cols)
    {
        if (rows < 5000 && cols < 5000)
        {
            if (cols == 1)
            {
                return column(rows);
            }
            else if (rows == 1)
            {
                return row(cols);
            }
            else
            {
                return denseMatrix(rows, cols);
            }
        }
        else
        {
            if (cols == 1)
            {
                return sparseColumn(rows);
            }
            else if (rows == 1)
            {
                return sparseRow(cols);
            }
            else
            {
                return sparseMatrix(rows, cols);
            }
        }
    }

    /**
     * Read matrix from ascii file.
     *
     * @param   filename
     *
     * @return
     */
    public IDoubleArray fromFile(String filename)
            throws FileNotFoundException, IOException
    {
        return DoubleIO.readDoubleMatrix(filename);
    }

    // ***********************************************************************
    //
    // Convenience vector factories
    //
    // ***********************************************************************

    public IDoubleArray array(int size, double value)
    {
        IDoubleArray res = array(size);
        Doubles.util.fill(res, value);
        return res;
    }
    
    /**
     * creates array with memory allocated optionally outside the JVM
     * @param size number of elements
     * @param allocateInSysMem if true, allocate mem outside JVM
     * @return IDoubleArray(ForeignBufferDoubleArray)
     */
    public IDoubleArray array(int size, boolean allocateInSysMem) {
        IDoubleArray res;
        
        if(allocateInSysMem) {
            ByteBuffer bb = ByteBuffer.allocateDirect(size * Double.SIZE);
            res = new ForeignBufferDoubleArray(bb, 1, size);
        } else {
            res = array(size);
        }
        
        return res;
    }

    /**
     * Creates an array from a string representation
     * @param from Array delimited by whitespaces or ,
     * @return
     */
    public IDoubleArray array(String from)
    {
        IDoubleArray res = array(Strings.util.toDoubleArray(from));
        return res;
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IDoubleArray arrayFrom(double d)
    {
        IDoubleArray res = new DenseDoubleArray(1);
        res.set(0,d);
        return res;
    }

    /**
     * Convenience constructor for primitive arrays
     * (this one is necessary because it does not overlap with create(double[])
     */
    public IDoubleArray arrayFrom(double d1, double... d2)
    {
        IDoubleArray res = new DenseDoubleArray(1+d2.length);
        res.set(0,d1);
        for (int i=0; i<d2.length; i++)
            res.set(i+1,d2[i]);
        return res;
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IDoubleArray arrayFrom(double[] arr)
    {
        return (array(arr));
    }
    
    /**
     * create an IDoubleArray from an ByteBuffer, which may be allocated outside
     * the JVM. This buffer should contain doubles only.
     * @param b ByteBuffer either allocated on heap or directly 
     * @param rows
     * @param cols
     * @return a ForeignBufferDoubleArray
     */
    public IDoubleArray arrayFrom(ByteBuffer b, int rows, int cols) {
        return new ForeignBufferDoubleArray(b, rows, cols);
    }

    public IDoubleArray arrayFrom(int[] a)
    {
        IDoubleArray res = new DenseDoubleArray(a.length);
        for (int i=0; i<a.length; i++)
            res.set(i,a[i]);
        return res;
    }

    public IDoubleArray arrayFrom(float[] a)
    {
        IDoubleArray res = new DenseDoubleArray(a.length);
        for (int i=0; i<a.length; i++)
            res.set(i,a[i]);
        return res;
    }

    /**
    @return a array of size n filled with random number out of [0,1[
     */
    public IDoubleArray arrayRandom(int n)
    {
        double[] res = PrimitiveDoubleTools.randomArray(n);
        return (array(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IDoubleArray arrayRange(double start, double end, double step)
    {
        double[] res = PrimitiveDoubleTools.range(start, end, step);
        return (array(res));
    }

    public IDoubleArray arrayGrid(double min, double max, int ngridpoints)
    {
        IDoubleArray grid = Doubles.create.array(ngridpoints);
        double dg = (max-min)/((double)ngridpoints-1);
        grid.set(0, min);
        for (int i=1; i<grid.size(); i++)
            grid.set(i, grid.get(i-1)+dg);
        return grid;
    }

    // ***********************************************************************
    //
    // Matrix convenience operations
    //
    // ***********************************************************************

    public IDoubleArray matrix(int nrows, int ncols)
    {
        return new DenseDoubleArray(nrows,ncols);
    }

    public IDoubleArray matrix(int nrows, int ncols, double value)
    {
        DenseDoubleArray res = new DenseDoubleArray(nrows,ncols);
        for (int i=0; i<res.size(); i++)
            res.set(i,value);
        return res;
    }

    public IDoubleArray matrix(double[][] res)
    {
        return new DenseDoubleArray(res);
    }

    /**
     * Creates an array from a string representation
     * @param from Array delimited by whitespaces or ,
     * @return
     */
    public IDoubleArray matrix(String from)
    {
        return (array(Strings.util.toDoubleTable(from)));
    }

    public IDoubleArray matrixFrom(float[][] a)
    {
        DenseDoubleArray res = new DenseDoubleArray(a.length,a[0].length);
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                res.set(i,j, a[i][j]);
            }
        }
        return res;
    }

    public IDoubleArray matrixFrom(int[][] a)
    {
        DenseDoubleArray res = new DenseDoubleArray(a.length,a[0].length);
        for (int i = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                res.set(i,j, a[i][j]);
            }
        }
        return res;
    }

    public IDoubleArray diag(int size, double value)
    {
        double[] diag = new double[size];
        Arrays.fill(diag, value);
        return new DiagonalMatrix(diag);
    }

    public IDoubleArray diag(double... values)
    {
        return new DiagonalMatrix(values);
    }

    public IDoubleArray diag(IDoubleArray values)
    {
        return new DiagonalMatrix(values);
    }

    public IDoubleArray symmetric(final IDoubleArray matrix)
    {
        return new SymmetricMatrix(matrix);
    }

    public IDoubleArray symmetricReal(final int size)
    {
        return new SymmetricMatrix(array(size,size));
    }

    public IDoubleArray identity(final int dim)
    {
        return(diag(dim,1));
    }


    // ***********************************************************************
    //
    // List factories
    //
    // ***********************************************************************
    public IDoubleList list(int size)
    {
        return (new DoubleArrayList_FastUtilWrapper(size));
    }

    public IDoubleList list(int size, double value)
    {
        return (new DoubleArrayList_FastUtilWrapper(PrimitiveDoubleTools.createInitialized(size, value)));
    }

    public IDoubleList list(IDoubleArray arr)
    {
        return (new DoubleArrayList_FastUtilWrapper(arr.getArray()));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IDoubleList listFrom(double d)
    {
        double[] arr = PrimitiveDoubleTools.getDoubleArray(d);
        return (new DoubleArrayList_FastUtilWrapper(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     * (this one is necessary because it does not overlap with create(double[])
     */
    public IDoubleList listFrom(double d1, double... d2)
    {
        double[] arr = PrimitiveDoubleTools.concat(PrimitiveDoubleTools.getDoubleArray(d1), d2);
        return (new DoubleArrayList_FastUtilWrapper(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IDoubleList listFrom(double[] arr)
    {
        return (new DoubleArrayList_FastUtilWrapper(arr));
    }

    public IDoubleList listFrom(int[] a)
    {
        double[] res = PrimitiveDoubleTools.from(a);
        return (new DoubleArrayList_FastUtilWrapper(res));
    }

    public IDoubleList listFrom(float[] a)
    {
        double[] res = PrimitiveDoubleTools.from(a);
        return (new DoubleArrayList_FastUtilWrapper(res));
    }

    /**
    @return a array of size n filled with random number out of [0,1[
     */
    public IDoubleList listRandom(int n)
    {
        double[] res = PrimitiveDoubleTools.randomArray(n);
        return (new DoubleArrayList_FastUtilWrapper(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IDoubleList listRange(double start, double end, double step)
    {
        double[] res = PrimitiveDoubleTools.range(start, end, step);
        return (new DoubleArrayList_FastUtilWrapper(res));
    }


}
