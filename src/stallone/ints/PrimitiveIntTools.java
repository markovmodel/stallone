/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import java.util.*;
import java.io.*;
import stallone.doubles.PrimitiveDoubleTools;

public class PrimitiveIntTools
{
    public static int[] createInitialized(int size, int d)
    {
        int[] res = new int[size];
        java.util.Arrays.fill(res, d);
        return(res);
    }

    public static int[] getIntArray(int... d)
    {
        int[] arr = new int[d.length];
        System.arraycopy(d, 0, arr, 0, d.length);
        return (arr);
    }

    public static int[] from(double[] a)
    {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = (int) a[i];
        }
        return (res);
    }

    public static int[][] from(double[][] a)
    {
        int[][] res = new int[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = from(a[i]);
        }
        return (res);
    }    
    public static int[] from(float[] a)
    {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = (int) a[i];
        }
        return (res);
    }

    public static int[][] from(float[][] a)
    {
        int[][] res = new int[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = from(a[i]);
        }
        return (res);
    }
    
    /**
    allocates the size for a two-dimensional double array with the same
    sizes as the given integer array.
     */
    public static int[][] alloc(int[][] arr)
    {
        int[][] res = new int[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = new int[arr[i].length];
        }
        return (res);
    }

    public static int[] lengths(int[][] arr)
    {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = arr[i].length;
        }
        return (res);
    }

    public static void fill(int[][] arr, int v)
    {
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                arr[i][j] = v;
            }
        }
    }

    public static int[] add(int[] arr1, int v)
    {
        int[] res = new int[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] + v;
        }
        return (res);
    }

    public static int[] add(int[] a1, int[] a2)
    {
        int[] res = new int[a1.length];
        for (int i = 0; i < res.length; i++)
        {
            res[i] = a1[i] + a2[i];
        }
        return (res);
    }

    public static int[][] add(int[][] arr1, int v)
    {
        int[][] res = new int[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = add(arr1[i], v);
        }
        return (res);
    }

    public static int[] multiply(int v, int[] arr1)
    {
        int[] res = new int[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = v * arr1[i];
        }
        return (res);
    }

    public static double[] toDouble(int[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = (double) (arr[i]);
        }
        return (res);
    }

    public static double[][] toDouble(int[][] arr)
    {
        double[][] res = new double[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = new double[arr[i].length];
            for (int j = 0; j < arr[i].length; j++)
            {
                res[i][j] = (double) (arr[i][j]);
            }
        }
        return (res);
    }

    public static int[] List2Array(Collection al)
    {
        int size = al.size();
        int[] res = new int[size];
        int k = 0;
        Iterator i = al.iterator();
        while (i.hasNext())
        {
            res[k++] = ((Integer) i.next()).intValue();
        }
        return (res);
    }

    public static int[][] List2Array2(Collection al)
    {
        int size = al.size();
        int[][] res = new int[size][];
        int k = 0;
        Iterator i = al.iterator();
        while (i.hasNext())
        {
            res[k++] = (int[]) i.next();
        }
        return (res);
    }

    public static int[][][] List2Array3(Collection al)
    {
        int size = al.size();
        int[][][] res = new int[size][][];
        int k = 0;
        Iterator i = al.iterator();
        while (i.hasNext())
        {
            res[k++] = (int[][]) i.next();
        }
        return (res);
    }

    public static int[] copy(int[] arr)
    {
        int[] res = new int[arr.length];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
        {
            res[j++] = arr[i];
        }
        return (res);
    }

    public static int[][] copy(int[][] arr)
    {
        int[][] res = new int[arr.length][];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
        {
            res[j++] = copy(arr[i]);
        }
        return (res);
    }

    /**
    Selects a random entry from the array
     */
    public static int randomSelection(int[] arr)
    {
        int i = (int) (arr.length * Math.random());
        return (arr[i]);
    }

    /**
    @return a list of distinct random numbers in the range 0...N-1. If n >= N, [0,...N-1]
    is returned.
     */
    public static int[] randomIndexes(int N, int n)
    {
        if (n >= N)
        {
            return (PrimitiveIntTools.range(0, N));
        }
        boolean[] included = new boolean[N];

        if (n < 0.5 * N) // include random elements
        {
            Arrays.fill(included, false);
            for (int i = 0; i < n;)
            {
                int v = (int) (N * Math.random());
                if (!included[v])
                {
                    included[v] = true;
                    i++;
                }
            }
        }
        else // exclude random elements
        {
            Arrays.fill(included, true);
            int nExclude = N - n;
            for (int i = 0; i < nExclude;)
            {
                int v = (int) (N * Math.random());
                if (included[v])
                {
                    included[v] = false;
                    i++;
                }
            }
        }

        int[] selected = new int[n];
        for (int k = 0, i = 0; i < included.length; i++)
        {
            if (included[i])
            {
                selected[k++] = i;
            }
        }
        return (selected);
    }

    /**
    @return a selection of n elements of the given array. if n > arr.length,
    the full array is returned. The returned array contains the entries in the
    same order as in the original array, that is
    arr = randomSelection(arr, arr.length)
     */
    public static int[] randomSelection(int[] arr, int n)
    {
        return (PrimitiveIntTools.subarray(arr, randomIndexes(arr.length, n)));
    }

    public static int[] randomPermutation(int[] arr)
    {
        double[] R = new double[arr.length];
        for (int i = 0; i < R.length; i++)
        {
            R[i] = Math.random();
        }
        int[] I = PrimitiveDoubleTools.sortedIndexes(R);
        int[] res = new int[I.length];
        for (int i = 0; i < res.length; i++)
        {
            res[i] = arr[I[i]];
        }
        return (res);
    }

    public static int[] fromLinkedList(LinkedList ll)
    {
        int[] res = new int[ll.size()];
        Iterator i = ll.iterator();
        int k = 0;
        while (i.hasNext())
        {
            res[k++] = ((Integer) i.next()).intValue();
        }
        return (res);
    }

    /**
    @return an int array with the indexes where ba is true.
     */
    public static int[] fromBooleanArray(boolean[] ba)
    {
        int k = 0;
        for (int i = 0; i < ba.length; i++)
        {
            if (ba[i])
            {
                k++;
            }
        }
        int[] res = new int[k];
        k = 0;
        for (int i = 0; i < ba.length; i++)
        {
            if (ba[i])
            {
                res[k++] = i;
            }
        }

        return (res);
    }

    /**
    reshapes the given 1-dimensional double array into a two-dimensional
    double array of size d1*d2
     */
    public static int[][] reshape(int[] arr, int d1, int d2)
    {
        if (arr.length != d1 * d2)
        {
            throw (new IllegalArgumentException("Illegal array size"));
        }
        int[][] res = new int[d1][d2];
        for (int i = 0; i < d1; i++)
        {
            for (int j = 0; j < d2; j++)
            {
                res[i][j] = arr[i * d2 + j];
            }
        }
        return (res);
    }

    /**
    flattens the two-dimensional array a by concatenating all lines.
     */
    public static int[] flatten(int[][] a)
    {
        int n = 0;
        for (int i = 0; i < a.length; i++)
        {
            n += a[i].length;
        }
        int[] res = new int[n];
        for (int i = 0, k = 0; i < a.length; i++)
        {
            for (int j = 0; j < a[i].length; j++)
            {
                res[k++] = a[i][j];
            }
        }
        return (res);
    }

    public static int[] concat(int[] a1, int[] a2)
    {
        int[] res = new int[a1.length + a2.length];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        for (int i = 0; i < a2.length; i++)
        {
            res[j++] = a2[i];
        }
        return (res);
    }

    public static int[][] concat(int[][] a1, int[][] a2)
    {
        int[][] res = new int[a1.length + a2.length][];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        for (int i = 0; i < a2.length; i++)
        {
            res[j++] = a2[i];
        }
        return (res);
    }

    public static int[] concat(int[] a1, int a2)
    {
        int[] res = new int[a1.length + 1];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        res[j] = a2;
        return (res);
    }

    public static int[][] concat(int[][] a1, int[] a2)
    {
        int[][] res = new int[a1.length + 1][];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        res[j] = a2;
        return (res);
    }

    public static int[] subarray(int[] a, int i1, int i2)
    {
        int[] res = new int[i2 - i1];
        for (int i = i1; i < i2; i++)
        {
            res[i - i1] = a[i];
        }
        return (res);
    }

    public static int[] subarray(int[] a, int[] sel)
    {
        int[] res = new int[sel.length];
        for (int i = 0; i < sel.length; i++)
        {
            res[i] = a[sel[i]];
        }
        return (res);
    }

    public static int[][] subarray(int[][] a, int i1, int i2)
    {
        int[][] res = new int[i2 - i1][];
        for (int i = i1; i < i2; i++)
        {
            res[i - i1] = a[i];
        }
        return (res);
    }

    public static int[][] subarray(int[][] a, int x1, int x2, int y1, int y2)
    {
        int[][] res = new int[x2 - x1][y2 - y1];
        for (int x = x1; x < x2; x++)
        {
            for (int y = y1; y < y2; y++)
            {
                res[x - x1][y - y1] = a[x][y];
            }
        }
        return (res);
    }

    public static int[][] subarray(int[][] a, int[] sel)
    {
        int[][] res = new int[sel.length][];
        for (int i = 0; i < sel.length; i++)
        {
            res[i] = a[sel[i]];
        }
        return (res);
    }

    public static boolean equal(int[] arr1, int[] arr2)
    {
        if (arr1 == arr2)
        {
            return (true);
        }
        if (arr1.length != arr2.length)
        {
            return (false);
        }
        for (int i = 0; i < arr1.length; i++)
        {
            if (arr1[i] != arr2[i])
            {
                return (false);
            }
        }
        return (true);
    }

    public static int[] getColumn(int[][] a, int c)
    {
        int[] res = new int[a.length];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = a[i][c];
        }
        return (res);
    }

    public static void set(int[] arr, int[] index, int val)
    {
        for (int i = 0; i < index.length; i++)
        {
            arr[index[i]] = val;
        }
    }

    public static int[] insert(int[] arr, int index, int v)
    {
        int[] res = new int[arr.length + 1];
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res[j++] = arr[i];
        }
        res[j++] = v;
        for (int i = index; i < arr.length; i++)
        {
            res[j++] = arr[i];
        }
        return (res);
    }

    /**
    Inserts the given number into the sorted array arr, if
    max(arr) < num. The array will maintain its size, therefore
    the largest array element is lost.
    Will only work if arr is sorted.
     */
    public static void insertFixed(int[] arr, int index, int num)
    {
        if (index < 0 || index > arr.length - 1)
        {
            throw (new ArrayIndexOutOfBoundsException("illegal index " + index));
        }
        for (int i = arr.length - 1; i > index; i--)
        {
            arr[i] = arr[i - 1];
        }
        arr[index] = num;
    }

    public static void insertFixed(int[][] arr, int index, int[] num)
    {
        if (index < 0 || index > arr.length - 1)
        {
            throw (new ArrayIndexOutOfBoundsException("illegal index " + index));
        }
        for (int i = arr.length - 1; i > index; i--)
        {
            arr[i] = arr[i - 1];
        }
        arr[index] = num;
    }

    public static int[] append(int[] arr, int v)
    {
        int[] res = new int[arr.length + 1];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
        {
            res[j++] = arr[i];
        }
        res[j++] = v;
        return (res);
    }

    public static void exchange(int[] arr, int i, int j)
    {
        int t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    public static int[] removeByIndex(int[] arr, int index)
    {
        int[] res = new int[arr.length - 1];
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res[j++] = arr[i];
        }
        for (int i = index + 1; i < arr.length; i++)
        {
            res[j++] = arr[i];
        }
        return (res);
    }

    public static int[] removeByIndex(int[] arr, int[] indexes)
    {
        boolean[] exclude = new boolean[arr.length];
        int nex = 0;
        for (int i = 0; i < indexes.length; i++)
        {
            if (!exclude[indexes[i]])
            {
                exclude[indexes[i]] = true;
                nex++;
            }
        }
        int[] res = new int[arr.length - nex];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (!exclude[i])
            {
                res[j++] = arr[i];
            }
        }
        return (res);
    }

    public static int[][] removeByIndex(int[][] arr, int index)
    {
        int[][] res = new int[arr.length - 1][];
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res[j++] = arr[i];
        }
        for (int i = index + 1; i < arr.length; i++)
        {
            res[j++] = arr[i];
        }
        return (res);
    }

    public static int[][] removeByIndex(int[][] arr, int[] indexes)
    {
        boolean[] exclude = new boolean[arr.length];
        int nex = 0;
        for (int i = 0; i < indexes.length; i++)
        {
            if (!exclude[indexes[i]])
            {
                exclude[indexes[i]] = true;
                nex++;
            }
        }
        int[][] res = new int[arr.length - nex][];
        int j = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (!exclude[i])
            {
                res[j++] = arr[i];
            }
        }
        return (res);
    }

    /**
    Returns arr1 with all values occuring in arr2 removed. Does not change
    the order of arr1.
     */
    public static int[] removeByValue(int[] arr1, int[] arr2)
    {
        int[] res = new int[arr1.length];
        int k = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            boolean cinsert = true;
            for (int j = 0; j < arr2.length; j++)
            {
                if (arr1[i] == arr2[j])
                {
                    cinsert = false;
                }
            }
            if (cinsert)
            {
                res[k++] = arr1[i];
            }
        }
        return (subarray(res, 0, k));
    }

    public static int[] removeByValue(int[] arr1, int a2)
    {
        int[] arr2 =
        {
            a2
        };
        return (removeByValue(arr1, arr2));
    }

    public static int[][] removeByValue(int[][] arr1, int[] v)
    {
        int[][] res = new int[arr1.length][];
        int k = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            if (!PrimitiveIntTools.equal(arr1[i], v))
            {
                res[k++] = arr1[i];
            }
        }
        return (subarray(res, 0, k));
    }

    /**
    returns a mirror image of the array, i.e. index 0 becomes index length-1
    and vice versa
     */
    public static int[] mirror(int[] arr)
    {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[arr.length - 1 - i] = arr[i];
        }
        return (res);
    }

    public static int[][] transpose(int[][] arr)
    {
        int nCol = arr.length;
        int nLin = arr[0].length;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i].length != nLin)
            {
                throw (new IllegalArgumentException("Trying to transpose a non-matrix-array"));
            }
        }

        int[][] res = new int[nLin][nCol];
        for (int i = 0; i < res.length; i++)
        {
            for (int j = 0; j < res[i].length; j++)
            {
                res[i][j] = arr[j][i];
            }
        }
        return (res);
    }

    /*
    Removes all redundance from the given int[]-array (multiple occurance)
    and sorts the array in ascending order
     */
    /*public static int[] clean(int[] uncleaned)
    {
    if (uncleaned.length == 0)
    return(uncleaned);
    Arrays.sort(uncleaned);
    int[] cleaned = new int[uncleaned.length];
    cleaned[0] = uncleaned[0];
    int k=1;
    for (int i=1; i<uncleaned.length; i++)
    if (uncleaned[i] != uncleaned[i-1])
    cleaned[k++] = uncleaned[i];
    return(subarray(cleaned,0,k));
    }*/
    /**
    Removes all redundance from the given int[]-array (multiple occurance)
    and sorts the array in ascending order
     */
    public static int[] clean(int[] uncleaned)
    {
        return (PrimitiveDoubleTools.toInt(PrimitiveDoubleTools.clean(toDouble(uncleaned))));
    }

    /**
    Counts the number of occurances of the given value in the given array.
     */
    public static int count(int[] arr, int val)
    {
        int c = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == val)
            {
                c++;
            }
        }
        return (c);
    }

    /**
    Counts the number of occurances for all values between mi and ma and
    returns them as a histogram, where each entry corresponds
    to one integer in the value range.
    Caution: If the value range is too large, this might blast
    the memory
     */
    public static int[] histogram(int[] arr, int mi, int ma)
    {
        int[] res = new int[ma - mi + 1];
        for (int i = 0; i < arr.length; i++)
        {
            res[arr[i] - mi]++;
        }
        return (res);
    }

    /**
    Counts the number of occurances for all values between min(arr) and
    max(arr) and returns them as a histogram, where each entry corresponds
    to one integer in the value range.
    Caution: If the value range of arr is too large, this might blast
    the memory
     */
    public static int[] histogram(int[] arr)
    {
        return (histogram(arr, min(arr), max(arr)));
    }

    public static boolean contains(int[] arr, int val)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == val)
            {
                return (true);
            }
        }
        return (false);
    }

    public static boolean containsAny(int[] arr, int[] val)
    {
        for (int i = 0; i < val.length; i++)
        {
            if (contains(arr, val[i]))
            {
                return (true);
            }
        }
        return (false);
    }

    public static boolean containsAll(int[] arr, int[] val)
    {
        for (int i = 0; i < val.length; i++)
        {
            if (!contains(arr, val[i]))
            {
                return (false);
            }
        }
        return (true);
    }

    /**
    @return true if arr contains the line sub
     */
    public static boolean contains(int[][] arr, int[] sub)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (PrimitiveIntTools.equal(arr[i], sub))
            {
                return (true);
            }
        }
        return (false);
    }

    public static int min(int[] arr)
    {
        int m = arr[0];
        for (int i = 1; i < arr.length; i++)
        {
            if (arr[i] < m)
            {
                m = arr[i];
            }
        }
        return (m);
    }

    public static int minIndex(int[] arr)
    {
        int m = arr[0];
        int mi = 0;
        for (int i = 1; i < arr.length; i++)
        {
            if (arr[i] < m)
            {
                m = arr[i];
                mi = i;
            }
        }
        return (mi);
    }

    public static int max(int[] arr)
    {
        int m = arr[0];
        for (int i = 1; i < arr.length; i++)
        {
            if (arr[i] > m)
            {
                m = arr[i];
            }
        }
        return (m);
    }

    public static int maxIndex(int[] arr)
    {
        int m = arr[0];
        int mi = 0;
        for (int i = 1; i < arr.length; i++)
        {
            if (arr[i] > m)
            {
                m = arr[i];
                mi = i;
            }
        }
        return (mi);
    }

    /**
    Returns the sum over all fields
     */
    public static int sum(int[] arr)
    {
        int sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            sum += arr[i];
        }
        return sum;
    }

    /**
    @return the mean (or average) value of the array
     */
    public static double mean(int[] arr)
    {
        return ((double) sum(arr) / (double) arr.length);
    }

    /**
    Returns the intersection of the two given arrays
     */
    public static int[] intersect(int[] arr1, int[] arr2)
    {
        int[] arr1c = clean(arr1);
        int[] arr2c = clean(arr2);
        int[] res = new int[Math.min(arr1c.length, arr2c.length)];
        int k = 0, k1 = 0, k2 = 0;
        while (k1 < arr1c.length && k2 < arr2c.length)
        {
            if (arr1c[k1] < arr2c[k2])
            {
                k1++;
            }
            if (k1 < arr1c.length && k2 < arr2c.length)
            {
                if (arr1c[k1] == arr2c[k2])
                {
                    res[k++] = arr1c[k1];
                    k1++;
                    k2++;
                }
            }
            if (k1 < arr1c.length && k2 < arr2c.length)
            {
                if (arr1c[k1] > arr2c[k2])
                {
                    k2++;
                }
            }
        }
        return (subarray(res, 0, k));
    }

    public static int[] union(int[] arr1, int[] arr2)
    {
        return (clean(concat(arr1, arr2)));
    }

    public static int[] union(int[] arr1, int arr2)
    {
        return (clean(concat(arr1, arr2)));
    }

    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public static int findForward(int[] arr, int val, int from)
    {
        for (int i = from; i < arr.length; i++)
        {
            if (arr[i] == val)
            {
                return i;
            }
        }
        return (-1);
    }

    public static int findForward(int[] arr, int val)
    {
        return (findForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public static int findBackwards(int[] arr, int val, int from)
    {
        for (int i = from; i >= 0; i--)
        {
            if (arr[i] == val)
            {
                return i;
            }
        }
        return (-1);
    }

    public static int findBackwards(int[] arr, int val)
    {
        return (findBackwards(arr, val, 0));
    }

    public static int[] findAll(int[] arr, int val)
    {
        LinkedList ll = new LinkedList();
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == val)
            {
                ll.add(new Integer(i));
            }
        }
        return (PrimitiveIntTools.List2Array(ll));
    }

    public static int[] findAll(int[] arr, int[] val)
    {
        LinkedList ll = new LinkedList();
        for (int i = 0; i < arr.length; i++)
        {
            if (PrimitiveIntTools.contains(val, arr[i]))
            {
                ll.add(new Integer(i));
            }
        }
        return (PrimitiveIntTools.List2Array(ll));
    }

    /**
    Sorts the array
     */
    public static int[] sort(int[] arr)
    {
        double[] darr = PrimitiveIntTools.toDouble(arr);
        PrimitiveDoubleTools.sort(darr);
        return (PrimitiveDoubleTools.toInt(darr));
    }

    /**
    Returns the index order of increasing value size
     */
    public static int[] sortedIndexes(int[] arr)
    {
        return (PrimitiveDoubleTools.sortedIndexes(PrimitiveIntTools.toDouble(arr)));
    }

    /**
    Locates, by binary search the index of the first value that is >= the
    given number.
    Returns arr.size, if all array numbers are smaller.
    Will only work if arr is sorted.
     */
    public static int locateSorted(int[] arr, double num)
    {
        int l = 0, m = arr.length / 2, r = arr.length;
        boolean found = false;
        while (!found)
        {
            if (num < arr[m])
            {
                r = m;
                m = (r + l) / 2;
            }
            else if (num > arr[m])
            {
                l = m;
                m = (r + l) / 2;
            }
            else
            {
                found = true;
            }

            if (m == l || m == r)
            {
                found = true;
            }
        }

        // move left if there are equals to the left
        found = false;
        while (!found)
        {
            if (m == 0)
            {
                found = true;
            }
            else if (arr[m - 1] != arr[m])
            {
                found = true;
            }
            else
            {
                m--;
            }
        }

        // move one right if arr[m] is smaller than num
        if (num > arr[m])
        {
            m++;
        }

        return (m);
    }

    /**
    Converts array to a string.
     */
    public static String toString(int[] arr, String del)
    {
        if (arr == null)
        {
            return ("null");
        }
        StringBuffer strbuf = new StringBuffer(arr.length * 10);
        for (int i = 0; i < arr.length; i++)
        {
            strbuf.append(String.valueOf(arr[i]));
            if ((i + 1) < arr.length)
            {
                strbuf.append(del);
            }
        }
        return (strbuf.toString());
    }

    public static String toString(int[] arr)
    {
        return (toString(arr, ","));
    }

    public static String toString(int[][] arr, String del1, String del2)
    {
        if (arr.length == 0)
        {
            return ("");
        }
        StringBuffer strbuf = new StringBuffer(arr.length * arr[0].length * 10);
        for (int i = 0; i < arr.length; i++)
        {
            strbuf.append(PrimitiveIntTools.toString(arr[i], del1) + del2);
        }
        return (strbuf.toString());
    }

    public static String toString(int[][] arr)
    {
        return (toString(arr, ",", "\n"));
    }

    public static void print(int[] arr, String del)
    {
        System.out.println(PrimitiveIntTools.toString(arr, del));
    }

    public static void print(int[] arr)
    {
        System.out.println(PrimitiveIntTools.toString(arr));
    }

    public static void print(int[][] arr, String del1, String del2)
    {
        System.out.print(PrimitiveIntTools.toString(arr, del1, del2));
    }

    public static void print(int[][] arr)
    {
        System.out.print(PrimitiveIntTools.toString(arr));
    }

    public static int[] fromString(String str)
    {
        StringTokenizer tok = new StringTokenizer(str, ",");
        int[] arr = new int[tok.countTokens()];
        int i = 0;
        while (tok.hasMoreTokens())
        {
            arr[i++] = (Integer.valueOf(tok.nextToken())).intValue();
        }
        return (arr);
    }

    /**
    Expands the given arr by copying random values until it has expandTo
    elements.
     */
    public static int[] expand(int[] arr, int expandTo)
    {
        Random rand = new Random();
        int additions = expandTo - arr.length;
        // add missing values
        int[] res = arr;
        for (int i = 0; i < additions; i++)
        {
            int copyIndex = rand.nextInt(res.length);
            res = PrimitiveIntTools.insert(res, copyIndex, res[copyIndex]);
        }
        return (res);
    }

    /**
    Returns an int-array with values in the range [start,end[
    with the given stepsize.
     */
    public static int[] range(int start, int end, int step)
    {
        if (start > end)
            throw(new IllegalArgumentException("Invalid range: ["+start+", "+end+", "+step+"]"));
        int[] res = new int[(int) Math.ceil((double)(end - start) / (double)step)];

        if (res.length > 0)
        {
            res[0] = start;
            for (int i = 1; i < res.length; i++)
            {
                res[i] = res[i - 1] + step;
            }
        }
        /*for (int i=start, j=0; i<end; i+=step)
        res[j++] = i;*/
        return (res);
    }

    /**
    Returns an int-array with values in the range [start,end[.
     */
    public static int[] range(int start, int end)
    {
        return (range(start, end, 1));
    }

    /**
    Returns an int-array with values in the range [0,end[.
     */
    public static int[] range(int end)
    {
        return (range(0, end, 1));
    }

    public static void save(int[] arr, String file)
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(arr);
            out.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void save(int[][] arr, String file)
    {
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(arr);
            out.close();
        } catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static int[][] loadMatrix(String file)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            int[][] arr = (int[][]) in.readObject();
            in.close();
            return (arr);
        } catch (Exception e)
        {
            return (null);
        }
    }

    public static int[] load(String file)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            int[] arr = (int[]) in.readObject();
            in.close();
            return (arr);
        } catch (Exception e)
        {
            return (null);
        }
    }

    public static int[] getIntArray(int v)
    {
        int[] res =
        {
            v
        };
        return (res);
    }

    public static int[] getIntArray(int v1, int v2)
    {
        int[] res =
        {
            v1, v2
        };
        return (res);
    }

    public static int[] getIntArray(int v1, int v2, int v3)
    {
        int[] res =
        {
            v1, v2, v3
        };
        return (res);
    }

    public static int[] getRepeat(int val, int n)
    {
        int[] res = new int[n];
        java.util.Arrays.fill(res, val);
        return (res);
    }

    /**
    Sorts the given indexes using binSort ascending according to the
    given properties.
     */
    public static int[] binSort(int[] indexes, int[] properties)
    {
        int min = PrimitiveIntTools.min(properties);
        int max = PrimitiveIntTools.max(properties);
        int nbins = max - min + 1;
        LinkedList[] bins = new LinkedList[nbins];
        for (int i = 0; i < bins.length; i++)
        {
            bins[i] = new LinkedList();
        }

        for (int i = 0; i < indexes.length; i++)
        {
            bins[properties[i] - min].add(new Integer(indexes[i]));
        }

        int[] result = new int[indexes.length];
        for (int i = 0, k = 0; i < bins.length; i++)
        {
            Iterator it = bins[i].iterator();
            while (it.hasNext())
            {
                result[k++] = ((Integer) it.next()).intValue();
            }
        }
        return (result);
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public static int[] smallestIndexes(int[] arr, int n)
    {
        return (PrimitiveDoubleTools.smallestIndexes(toDouble(arr), n));
    }

    public static int[] smallest(int[] arr, int n)
    {
        return (subarray(arr, smallestIndexes(arr, n)));
    }

    /**
    @return the indexes of the n largest values in arr.
     */
    public static int[] largestIndexes(int[] arr, int n)
    {
        return (PrimitiveDoubleTools.largestIndexes(toDouble(arr), n));
    }

    public static int[] largest(int[] arr, int n)
    {
        return (subarray(arr, largestIndexes(arr, n)));
    }
}
