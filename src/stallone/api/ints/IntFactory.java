/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;

import java.nio.ByteBuffer;

import stallone.ints.ForeignBufferIntArray;
import stallone.ints.IntArrayList_FastUtilWrapper;
import stallone.ints.PrimitiveIntArray;
import stallone.ints.PrimitiveIntTable;
import stallone.ints.PrimitiveIntTools;

/**
 *
 * @author noe
 */
public class IntFactory
{
    // ***********************************************************************
    //
    // General Vector operations
    //
    // ***********************************************************************

    /**
     * Creates an empty double sequence of the same type and size as arr
     * Currently just copies the array, but this should be replaced by a pure
     * data allocation.
     * @param arr the template array
     * @return
     */
    public IIntArray like(IIntArray arr, int size)
    {
        return(arr.create(size));
    }

    // ***********************************************************************
    //
    // Array factories
    //
    // ***********************************************************************

    public IIntArray array(int size)
    {
        return (new PrimitiveIntArray(new int[size]));
    }

    public IIntArray array(int size, int value)
    {
        return (new PrimitiveIntArray(PrimitiveIntTools.createInitialized(size, value)));
    }

    public IIntArray array(IIntArray arr)
    {
        return (new PrimitiveIntArray(arr.getArray()));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IIntArray arrayFrom(int d)
    {
        int[] arr = PrimitiveIntTools.getIntArray(d);
        return (new PrimitiveIntArray(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     * (this one is necessary because it does not overlap with create(double[])
     */
    public IIntArray arrayFrom(int d1, int... d2)
    {
        int[] arr = PrimitiveIntTools.concat(PrimitiveIntTools.getIntArray(d1),d2);
        return (new PrimitiveIntArray(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IIntArray arrayFrom(int[] arr)
    {
        return (new PrimitiveIntArray(arr));
    }


    public IIntArray arrayFrom(double[] a)
    {
        int[] res = PrimitiveIntTools.from(a);
        return (new PrimitiveIntArray(res));
    }


    public IIntArray arrayFrom(float[] a)
    {
        int[] res = PrimitiveIntTools.from(a);
        return (new PrimitiveIntArray(res));
    }
    
    /**
     * wraps a bytebuffer in an integer array interface. Note that this buffer 
     * may lay outside of jvm memory (direct buffer).
     * @param bb
     * @param rows
     * @param cols
     * @return IIntArray
     */
    public IIntArray arrayFrom(ByteBuffer bb, int rows, int cols) {
        return new ForeignBufferIntArray(bb, rows, cols);
    }


    /**
       @return a list of distinct random numbers in the range 0...N-1. If n >= N, [0,...N-1]
       is returned.
    */
    public IIntArray arrayRandomIndexes(int N, int n)
    {
        int[] res = PrimitiveIntTools.randomIndexes(N,n);
        return (new PrimitiveIntArray(res));
    }


    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntArray arrayRange(int start, int end, int step)
    {
        int[] res = PrimitiveIntTools.range(start, end, step);
        return (new PrimitiveIntArray(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntArray arrayRange(int start, int end)
    {
        int[] res = PrimitiveIntTools.range(start, end);
        return (new PrimitiveIntArray(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntArray arrayRange(int end)
    {
        int[] res = PrimitiveIntTools.range(end);
        return (new PrimitiveIntArray(res));
    }


    // ***********************************************************************
    //
    // List factories
    //
    // ***********************************************************************

    public IIntList list(int size)
    {
        return (new IntArrayList_FastUtilWrapper(new int[size]));
    }

    public IIntList list(int size, int value)
    {
        return (new IntArrayList_FastUtilWrapper(PrimitiveIntTools.createInitialized(size, value)));
    }

    public IIntList list(IIntArray arr)
    {
        return (new IntArrayList_FastUtilWrapper(arr.getArray()));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IIntList listFrom(int d)
    {
        int[] arr = PrimitiveIntTools.getIntArray(d);
        return (new IntArrayList_FastUtilWrapper(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     * (this one is necessary because it does not overlap with create(double[])
     */
    public IIntList listFrom(int d1, int... d2)
    {
        int[] arr = PrimitiveIntTools.concat(PrimitiveIntTools.getIntArray(d1),d2);
        return (new IntArrayList_FastUtilWrapper(arr));
    }

    /**
     * Convenience constructor for primitive arrays
     */
    public IIntList listFrom(int[] arr)
    {
        return (new IntArrayList_FastUtilWrapper(arr));
    }


    public IIntList listFrom(double[] a)
    {
        int[] res = PrimitiveIntTools.from(a);
        return (new IntArrayList_FastUtilWrapper(res));
    }


    public IIntList listFrom(float[] a)
    {
        int[] res = PrimitiveIntTools.from(a);
        return (new IntArrayList_FastUtilWrapper(res));
    }

    /**
       @return a list of distinct random numbers in the range 0...N-1. If n >= N, [0,...N-1]
       is returned.
    */
    public IIntList listRandomIndexes(int N, int n)
    {
        int[] res = PrimitiveIntTools.randomIndexes(N,n);
        return (new IntArrayList_FastUtilWrapper(res));
    }


    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntList listRange(int start, int end, int step)
    {
        int[] res = PrimitiveIntTools.range(start, end, step);
        return (new IntArrayList_FastUtilWrapper(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntList listRange(int start, int end)
    {
        int[] res = PrimitiveIntTools.range(start, end);
        return (new IntArrayList_FastUtilWrapper(res));
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public IIntList listRange(int end)
    {
        int[] res = PrimitiveIntTools.range(end);
        return (new IntArrayList_FastUtilWrapper(res));
    }



    // *****************************************************************************
    //
    // Table constructors
    //
    // *****************************************************************************

    public IIntArray table(int[][] a) 
    {
        return new PrimitiveIntTable(a);
    }
    
    public IIntArray table(int nrows, int ncols)
    {
        return (new PrimitiveIntTable(new int[nrows][ncols]));
    }

    public IIntArray table(int nrows, int ncols, int value)
    {
        int[][] arr = new int[nrows][ncols];
        for (int i = 0; i < nrows; i++)
        {
            java.util.Arrays.fill(arr[i], value);
        }
        return (new PrimitiveIntTable(arr));
    }

    public IIntArray tableFrom(float[][] a)
    {
        int[][] res = new int[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = new int[a[i].length];
            for (int j = 0; j < a[i].length; j++)
            {
                res[i][j] = (int) a[i][j];
            }
        }
        return (new PrimitiveIntTable(res));
    }

    public IIntArray tableFrom(double[][] a)
    {
        int[][] res = new int[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = new int[a[i].length];
            for (int j = 0; j < a[i].length; j++)
            {
                res[i][j] = (int) a[i][j];
            }
        }
        return (new PrimitiveIntTable(res));
    }

        /**
    reshapes the given 1-dimensional double array into a two-dimensional
    double array of size d1*d2
     */
    public IIntArray tableReshape(IIntArray arr, int d1, int d2)
    {
        if (arr.size() != d1 * d2)
        {
            throw (new IllegalArgumentException("Illegal array size"));
        }
        IIntArray res = table(d1,d2);
        for (int i = 0; i < d1; i++)
        {
            for (int j = 0; j < d2; j++)
            {
                res.set(i,j, arr.get(i * d2 + j));
            }
        }
        return (res);
    }

}
