/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import java.io.FileNotFoundException;
import java.io.IOException;

import stallone.api.strings.Strings;
import stallone.doubles.DoubleArrayList_FastUtilWrapper;
import stallone.doubles.DoubleIO;
import stallone.doubles.PrimitiveDoubleArray;
import stallone.doubles.PrimitiveDoubleTable;
import stallone.doubles.PrimitiveDoubleTools;
import stallone.doubles.SparseRealMatrix;
import stallone.doubles.SparseRealVector;
import stallone.doubles.SymmetricMatrix;

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
        return (new PrimitiveDoubleArray(new double[size]));
    }

    //TODO: create Row and Column Version of 1D-Arrays.
    public IDoubleArray denseRow(int size)
    {
        return (new PrimitiveDoubleTable(1,size));
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
        return (new PrimitiveDoubleArray(init));
    }    
    
    // TODO: 
    // create from lists
    // create primitive types from IDoubleArray, IDoubleMatrix
    public IDoubleArray denseMatrix(int nrows, int ncols)
    {
        return (new PrimitiveDoubleTable(new double[nrows][ncols]));
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
        return (new PrimitiveDoubleTable(init));
    }
    
    /**
     * Generic array creator. Creates a dense vector or matrix when rows and cols are less than 5000, otherwise sparse
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
        double[] arr = PrimitiveDoubleTools.getDoubleArray(d);
        return (array(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     * (this one is necessary because it does not overlap with create(double[])
     */
    public IDoubleArray arrayFrom(double d1, double... d2)
    {
        double[] arr = PrimitiveDoubleTools.concat(PrimitiveDoubleTools.getDoubleArray(d1), d2);
        return (array(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IDoubleArray arrayFrom(double[] arr)
    {
        return (array(arr));
    }

    public IDoubleArray arrayFrom(int[] a)
    {
        double[] res = PrimitiveDoubleTools.from(a);
        return (array(res));
    }

    public IDoubleArray arrayFrom(float[] a)
    {
        double[] res = PrimitiveDoubleTools.from(a);
        return (array(res));
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
        double[][] arr = new double[nrows][ncols];
        return (array(arr));
    }

    public IDoubleArray matrix(int nrows, int ncols, double value)
    {
        double[][] arr = new double[nrows][ncols];
        for (int i = 0; i < nrows; i++)
        {
            java.util.Arrays.fill(arr[i], value);
        }
        return (array(arr));
    }

    public IDoubleArray matrix(double[][] res)
    {
        return (array(res));
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
        double[][] res = new double[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = new double[a[i].length];
            for (int j = 0; j < a[i].length; j++)
            {
                res[i][j] = a[i][j];
            }
        }
        return (array(res));
    }

    public IDoubleArray matrixFrom(int[][] a)
    {
        double[][] res = new double[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = new double[a[i].length];
            for (int j = 0; j < a[i].length; j++)
            {
                res[i][j] = a[i][j];
            }
        }
        return (array(res));
    }

    /**
    reshapes the given 1-dimensional double array into a two-dimensional
    double array of size d1*d2
     */
    public IDoubleArray matrixReshape(IDoubleArray arr, int d1, int d2)
    {
        if (arr.size() != d1 * d2)
        {
            throw (new IllegalArgumentException("Illegal array size"));
        }
        IDoubleArray res = array(d1, d2);
        for (int i = 0; i < d1; i++)
        {
            for (int j = 0; j < d2; j++)
            {
                res.set(i, j, arr.get(i * d2 + j));
            }
        }
        return (res);
    }    
    
    public IDoubleArray diag(int size, double value)
    {
        IDoubleArray M = matrix(size, size);
        for (int i = 0; i < size; i++)
        {
            M.set(i, i, value);
        }
        return (M);
    }

    public IDoubleArray diag(double... values)
    {
        IDoubleArray M = matrix(values.length, values.length);
        for (int i = 0; i < values.length; i++)
        {
            M.set(i, i, values[i]);
        }
        return (M);
    }
    
    public IDoubleArray diag(IDoubleArray values)
    {
        IDoubleArray M = matrix(values.size(), values.size());
        for (int i = 0; i < values.size(); i++)
        {
            M.set(i, i, values.get(i));
        }
        return (M);
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
