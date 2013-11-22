/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.ints.PrimitiveIntTools;
import java.io.*;
import java.util.List;

public class PrimitiveDoubleTools
{
    public static double[] List2Array(List<?> al)
    {
        int size = al.size();
        double[] res = new double[size];
//        for (int i = 0; i < size; i++)
//        {
//            res[i] = ((Double) al.get(i)).intValue();
//        }
        System.arraycopy(al, 0, res, 0, size);
        return (res);
    }

    public static double[][] List2Array2(List<?> al)
    {
        int size = al.size();
        double[][] res = new double[size][];
        for (int i = 0; i < size; i++)
        {
            res[i] = (double[]) al.get(i);
        }
        return (res);
    }

    public static double[][][] List2Array3(List<?> al)
    {
        int size = al.size();
        double[][][] res = new double[size][][];
        for (int i = 0; i < size; i++)
        {
            res[i] = (double[][]) al.get(i);
        }
        return (res);
    }

    public static double[] createInitialized(int size, double d)
    {
        double[] res = new double[size];
        java.util.Arrays.fill(res, d);
        return(res);
    }

    public static double[] getDoubleArray(double... d)
    {
        double[] arr = new double[d.length];
        System.arraycopy(d, 0, arr, 0, d.length);
        return (arr);
    }

    public static double[] from(int[] a)
    {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = (double) a[i];
        }
        return (res);
    }

    public static double[][] from(int[][] a)
    {
        double[][] res = new double[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = from(a[i]);
        }
        return (res);
    }
    public static double[] from(float[] a)
    {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = (double) a[i];
        }
        return (res);
    }

    public static double[][] from(float[][] a)
    {
        double[][] res = new double[a.length][];
        for (int i = 0; i < a.length; i++)
        {
            res[i] = from(a[i]);
        }
        return (res);
    }

    public static int[] lengths(double[][] M)
    {
        int[] res = new int[M.length];
        for (int i = 0; i < M.length; i++)
        {
            res[i] = M[i].length;
        }
        return (res);
    }

    public static double[][] unitMatrix(int n)
    {
        double[][] res = new double[n][n];
        for (int i = 0; i < n; i++)
        {
            res[i][i] = 1;
        }
        return (res);
    }

    /**
    @return a array of size n filled with random number out of [0,1[
     */
    public static double[] randomArray(int n)
    {
        double[] res = new double[n];
        for (int i = 0; i < n; i++)
        {
            res[i] = Math.random();
        }
        return (res);
    }

    /**
    @return an unit vector into a random direction
     */
    public static double[] randomDirection(int n)
    {
        double[] res = randomArray(n);
        res = PrimitiveDoubleTools.add(res, -0.5);
        return (normalize(res));
    }

    public static double[] getRepeat(double val, int n)
    {
        double[] res = new double[n];
        java.util.Arrays.fill(res, val);
        return (res);
    }

    /**
    @return an array of doubles, starting with the value start, ending
    with a value < end. All values are obtained by incrementing the
    previous value by step.
     */
    public static double[] range(double start, double end, double step)
    {
        int N = (int) Math.ceil((end - start) / step);
        double[] res = new double[N];
        res[0] = start;
        for (int i = 1; i < res.length; i++)
        {
            res[i] = res[i - 1] + step;
        }
        return (res);
    }

    public static double[] mirror(double[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[res.length - 1 - i] = arr[i];
        }
        return (res);
    }

    public static double[] diagonal(double[][] M)
    {
        double[] res = new double[M.length];
        for (int i = 0; i < M.length; i++)
        {
            res[i] = M[i][i];
        }
        return (res);
    }

    public static double[][] transpose(double[][] arr)
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

        double[][] res = new double[nLin][nCol];
        for (int i = 0; i < res.length; i++)
        {
            for (int j = 0; j < res[i].length; j++)
            {
                res[i][j] = arr[j][i];
            }
        }
        return (res);
    }

    public static double[][] translate(double[][] crds, double[] T)
    {
        double[][] res = copy(crds);
        for (int i = 0; i < res.length; i++)
        {
            increment(res[i], T);
        }
        return (res);
    }

    public static double[] square(double[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] += arr[i] * arr[i];
        }
        return (res);
    }

    public static double norm(double[] arr)
    {
        double n = 0;
        for (int i = 0; i < arr.length; i++)
        {
            n += arr[i] * arr[i];
        }
        return (Math.sqrt(n));
    }

    public static double[] normalize(double[] arr)
    {
        double n = norm(arr);
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = arr[i] / n;
        }
        return (res);
    }

    /**
     * Scalar product
     */
    public static double dot(double[] arr1, double[] arr2)
    {
        double v = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            v += arr1[i] * arr2[i];
        }
        return (v);
    }

    /**
     * Weighted scalar product
     */
    public static double dot(double[] arr1, double[] arr2, double[] w)
    {
        double v = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            v += w[i] * arr1[i] * arr2[i];
        }
        return (v);
    }

    public static double[] add(double[] arr1, double v)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] + v;
        }
        return (res);
    }

    public static double[] add(double[] arr1, double[] arr2)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] + arr2[i];
        }
        return (res);
    }

    public static double[][] add(double[][] arr1, double[][] arr2)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = new double[arr1[i].length];
            for (int j = 0; j < arr1[i].length; j++)
            {
                res[i][j] = arr1[i][j] + arr2[i][j];
            }
        }
        return (res);
    }

    public static double[][] add(double[][] arr1, double v)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = add(arr1[i], v);
        }
        return (res);
    }

    public static double[] addWeighted(double w1, double[] arr1,
            double w2, double[] arr2)
    {
        if (arr1.length != arr2.length)
        {
            throw (new IllegalArgumentException("incompatible vectors!"));
        }
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = w1 * arr1[i] + w2 * arr2[i];
        }
        return (res);
    }

    public static double[] insert(double[] arr, int index, double v)
    {
        double[] res = new double[arr.length + 1];
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

    public static double[][] insert(double[][] arr, int index, double[] v)
    {
        double[][] res = new double[arr.length + 1][];
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
    Should later be replace by binsearch.
    Locates the index of the first value that is >= the given number.
    Returns arr.size, if all array numbers are smaller.
    Will only work if arr is sorted.
     */
    /*public static int locateSorted(double[] arr, double num)
    {
    for (int i=0; i<arr.length; i++)
    if (arr[i] >= num)
    return(i);
    return(arr.length);
    }*/
    /**
    Locates, by binary search the index of the first value that is >= the
    given number.
    Returns arr.size, if all array numbers are smaller.
    Will only work if arr is sorted.
     */
    public static int locateSorted(double[] arr, double num)
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
    Finds the first occurrence of num in the sorted array arr.
    If arr is not sorted, this will not work.
     */
    public static int findSorted(double[] arr, double num)
    {
        int i = locateSorted(arr, num);
        if (arr[i] == num)
        {
            return (i);
        }
        else
        {
            return (-1);
        }
    }

    /**
    Inserts the given number into the sorted array arr. If arr is not
    sorted, arr will be inserted somewhere.
     */
    public static double[] insertSorted(double[] arr, double num)
    {
        int i = locateSorted(arr, num);
        return (insert(arr, i, num));
    }

    /**
    Inserts the given number into the sorted array arr, if
    max(arr) < num. The array will maintain its size, therefore
    the largest array element is lost.
    Will only work if arr is sorted.
     */
    public static int insertSortedFixed(double[] arr, double num)
    {
        if (num >= arr[arr.length - 1])
        {
            return (-1);
        }
        int i = arr.length - 1;
        for (; i >= 1; i--)
        {
            if (arr[i - 1] >= num)
            {
                arr[i] = arr[i - 1];
            }
            else
            {
                break;
            }
        }
        arr[i] = num;
        return (i);
    }

    /** arr1[i] += arr2[i] */
    public static void increment(double[] arr1, double[] arr2)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            arr1[i] += arr2[i];
        }
    }

    /** arr1[i] += arr2[i] */
    public static void increment(double[] arr1, double v)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            arr1[i] += v;
        }
    }

    /** arr1[i][j] += arr2[i][j] */
    public static void increment(double[][] arr1, double[][] arr2)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            for (int j = 0; j < arr1[i].length; j++)
            {
                arr1[i][j] += arr2[i][j];
            }
        }
    }

    /** arr1[i][j] += arr2[i][j] */
    public static void increment(double[][] arr1, double v)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            for (int j = 0; j < arr1[i].length; j++)
            {
                arr1[i][j] += v;
            }
        }
    }

    /** arr1[i] -= arr2[i] */
    public static void decrement(double[] arr1, double[] arr2)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            arr1[i] -= arr2[i];
        }
    }

    /** arr1[i][j] -= arr2[i][j] */
    public static void decrement(double[][] arr1, double[][] arr2)
    {
        for (int i = 0; i < arr1.length; i++)
        {
            for (int j = 0; j < arr1[i].length; j++)
            {
                arr1[i][j] -= arr2[i][j];
            }
        }
    }

    public static double[] subtract(double[] arr1, double[] arr2)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] - arr2[i];
        }
        return (res);
    }

    public static double[][] subtract(double[][] arr1, double[][] arr2)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = new double[arr1[i].length];
            for (int j = 0; j < arr1[i].length; j++)
            {
                res[i][j] = arr1[i][j] - arr2[i][j];
            }
        }
        return (res);
    }

    public static double[] multiply(double[] arr1, double[] arr2)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] * arr2[i];
        }
        return (res);
    }

    /**
    Element-wise multiplication result_ij = A_ij * B_ij.
     */
    public static double[][] multiply(double[][] arr1, double[][] arr2)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = new double[arr1[i].length];
            for (int j = 0; j < arr1[i].length; j++)
            {
                res[i][j] = arr1[i][j] * arr2[i][j];
            }
        }
        return (res);
    }

    public static double[] multiply(double f, double[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = f * arr[i];
        }
        return (res);
    }

    public static double[][] multiply(double f, double[][] arr)
    {
        double[][] res = new double[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = new double[arr[i].length];
            for (int j = 0; j < arr[i].length; j++)
            {
                res[i][j] = f * arr[i][j];
            }
        }
        return (res);
    }

    public static double[] negative(double[] arr)
    {
        return (multiply(-1, arr));
    }

    public static double[] invertElements(double[] arr)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = 1.0 / arr[i];
        }
        return (res);
    }

    public static double[][] negative(double[][] arr)
    {
        return (multiply(-1, arr));
    }

    public static double[][] invertElements(double[][] arr)
    {
        double[][] res = alloc(arr);
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                res[i][j] = 1.0 / arr[i][j];
            }
        }
        return (res);
    }

    public static double[] divide(double[] arr1, double[] arr2)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] / arr2[i];
        }
        return (res);
    }

    public static double[][] divide(double[][] arr1, double[][] arr2)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = new double[arr1[i].length];
            for (int j = 0; j < arr1[i].length; j++)
            {
                res[i][j] = arr1[i][j] / arr2[i][j];
            }
        }
        return (res);
    }

    public static double[] divide(double[] arr1, double d)
    {
        double[] res = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = arr1[i] / d;
        }
        return (res);
    }

    public static double[][] divide(double[][] arr1, double d)
    {
        double[][] res = new double[arr1.length][];
        for (int i = 0; i < arr1.length; i++)
        {
            res[i] = new double[arr1[i].length];
            for (int j = 0; j < arr1[i].length; j++)
            {
                res[i][j] = arr1[i][j] / d;
            }
        }
        return (res);
    }

    public static double distance(double[] arr1, double[] arr2)
    {
        double d = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            double dev = arr1[i] - arr2[i];
            d += dev * dev;
        }
        return (Math.sqrt(d));
    }

    /**
    returns the distance between the two matrices, which is equal to the
    square root of the summed squared distances between corresponding entries.
     */
    public static double distance(double[][] arr1, double[][] arr2)
    {
        double d = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            for (int j = 0; j < arr1[i].length; j++)
            {
                double dev = arr1[i][j] - arr2[i][j];
                d += dev * dev;
            }
        }
        return (Math.sqrt(d));
    }

    /**
    @return the nearest neighbor to v
     */
    public static int nearestNeighbor(double[][] crds, int v)
    {
        int imin = 0;
        double dmin = Double.POSITIVE_INFINITY;
        for (int i = 0; i < crds.length; i++)
        {
            double d = distance(crds[v], crds[i]);
            if (i != v && d < dmin)
            {
                dmin = d;
                imin = i;
            }
        }
        return (imin);
    }

    /**
    Returns true if and only if the arrays are of equal size and
    all elements are equal
     */
    public static boolean equal(double[] arr1, double[] arr2)
    {
        if (arr1 == null && arr2 == null)
        {
            return (true);
        }
        if ((arr1 == null && arr2 != null) || (arr1 != null && arr2 == null))
        {
            return (false);
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

    /**
    Returns true if and only if the arrays are of equal size and
    all elements are equal up to the given tolerance
     */
    public static boolean equal(double[] arr1, double[] arr2, double tol)
    {
        if (arr1.length != arr2.length)
        {
            return (false);
        }
        for (int i = 0; i < arr1.length; i++)
        {
            if (arr1[i] == arr2[i])
            {
                continue;
            }
            else if (arr1[i] == 0)
            {
                if (Math.abs(arr1[i] - arr2[i]) / Math.abs(arr2[i]) > tol)
                {
                    return (false);
                }
            }
            else if (arr2[i] == 0)
            {
                if (Math.abs(arr1[i] - arr2[i]) / Math.abs(arr1[i]) > tol)
                {
                    return (false);
                }
            }
            else if (Math.abs(arr1[i] - arr2[i]) / Math.abs(arr1[i]) > tol
                    || Math.abs(arr1[i] - arr2[i]) / Math.abs(arr2[i]) > tol)
            {
                return (false);
            }
        }
        return (true);
    }

    /**
    Returns true if and only if the arrays are of equal size and
    all elements are equal
     */
    public static boolean equal(double[][] arr1, double[][] arr2)
    {
        if (arr1.length != arr2.length)
        {
            return (false);
        }
        for (int i = 0; i < arr1.length; i++)
        {
            if (!equal(arr1[i], arr2[i]))
            {
                return (false);
            }
        }
        return (true);
    }

    /**
    Returns true if and only if the arrays are of equal size and
    all elements are equal
     */
    public static boolean equal(double[][] arr1, double[][] arr2, double tol)
    {
        if (arr1.length != arr2.length)
        {
            return (false);
        }
        for (int i = 0; i < arr1.length; i++)
        {
            if (!equal(arr1[i], arr2[i], tol))
            {
                return (false);
            }
        }
        return (true);
    }

    public static double[] clean(double[] uncleaned)
    {
        if (uncleaned.length == 0)
        {
            return (uncleaned);
        }
        java.util.Arrays.sort(uncleaned);
        double[] cleaned = new double[uncleaned.length];
        cleaned[0] = uncleaned[0];
        int k = 1;
        for (int i = 1; i < uncleaned.length; i++)
        {
            if (uncleaned[i] != uncleaned[i - 1])
            {
                cleaned[k++] = uncleaned[i];
            }
        }
        return (subarray(cleaned, 0, k));
    }

    /**
    Returns the intersection of the two given arrays
     */
    public static double[] intersect(double[] arr1, double[] arr2)
    {
        double[] arr1c = clean(arr1);
        double[] arr2c = clean(arr2);
        double[] res = new double[Math.min(arr1c.length, arr2c.length)];
        int k = 0;
        for (int i = 0; i < arr1c.length; i++)
        {
            for (int j = 0; j < arr2c.length; j++)
            {
                if (arr1c[i] == arr2c[j])
                {
                    res[k++] = arr1c[i];
                    break;
                }
            }
        }
        return (subarray(res, 0, k));
    }

    public static double[] smallValues(double[] arr, double maxValue)
    {
        double[] res = new double[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] <= maxValue)
            {
                res[k++] = arr[i];
            }
        }
        return (subarray(res, 0, k));
    }

    public static int[] smallValueIndexes(double[] arr, double maxValue)
    {
        int[] res = new int[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] <= maxValue)
            {
                res[k++] = i;
            }
        }
        return (PrimitiveIntTools.subarray(res, 0, k));
    }

    public static double[] largeValues(double[] arr, double minValue)
    {
        double[] res = new double[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] >= minValue)
            {
                res[k++] = arr[i];
            }
        }
        return (subarray(res, 0, k));
    }

    public static int[] largeValueIndexes(double[] arr, double minValue)
    {
        int[] res = new int[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] >= minValue)
            {
                res[k++] = i;
            }
        }
        return (PrimitiveIntTools.subarray(res, 0, k));
    }

    /**
    @return all values with minVal <= val < maxVal
     */
    public static double[] within(double[] arr, double minVal, double maxVal)
    {
        double[] res = new double[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (minVal <= arr[i] && arr[i] < maxVal)
            {
                res[k++] = arr[i];
            }
        }
        return (subarray(res, 0, k));
    }

    /**
    @return all indexes whose values are minVal <= val < maxVal
     */
    public static int[] withinIndexes(double[] arr, double minVal, double maxVal)
    {
        int[] res = new int[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (minVal <= arr[i] && arr[i] < maxVal)
            {
                res[k++] = i;
            }
        }
        return (PrimitiveIntTools.subarray(res, 0, k));
    }

    /**
    @return the geometric center, which is an array with the means of the
    columns
     */
    public static double[] center(double[][] crd)
    {
        double[] c = new double[crd[0].length];
        for (int i = 0; i < crd.length; i++)
        {
            for (int j = 0; j < crd[i].length; j++)
            {
                c[j] += crd[i][j];
            }
        }

        for (int j = 0; j < c.length; j++)
        {
            c[j] /= crd.length;
        }

        return (c);
    }

    /**
    @return the mean (or average) value of the array
     */
    public static double mean(double[] arr)
    {
        return (sum(arr) / arr.length);
    }

    /**
    @return the mean vector
     */
    public static double[] mean(double[][] arr)
    {
        double[] res = new double[arr[0].length];
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                res[j] += arr[i][j];
            }
        }
        return (PrimitiveDoubleTools.multiply(1 / (double) arr.length, res));
    }

    /**
    @return the variance from the mean. Returns -1 for single-value arrays
     */
    public static double variance(double[] arr)
    {
        if (arr.length == 1)
        {
            return (-1);
        }
        double meanval = mean(arr);
        double sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            double err = arr[i] - meanval;
            sum += err * err;
        }
        return (sum / (arr.length - 1));
    }

    /**
    @return the std dev from the mean
     */
    public static double stdDev(double[] arr)
    {
        return (Math.sqrt(variance(arr)));
    }

    /**
    Gives a lower estimate for the provided data which is with the
    probability ratioCorrect a lower bound.
     */
    public static double lowerEstimate(double[] arr, double ratioCorrect)
    {
        int nCorrect = (int) Math.round((1.0 - ratioCorrect) * (double) (arr.length - 1));
        double[] arrSort = copy(arr);
        java.util.Arrays.sort(arrSort);
        return (arrSort[nCorrect]);
    }

    /**
    Gives an upper estimate for the provided data which is with the
    probability ratioCorrect an upper bound.
     */
    public static double upperEstimate(double[] arr, double ratioCorrect)
    {
        int nCorrect = (int) Math.round(ratioCorrect * (double) (arr.length - 1));
        double[] arrSort = copy(arr);
        java.util.Arrays.sort(arrSort);
        return (arrSort[nCorrect]);
    }

    /**
    N = arr.length
     */
    public static double rmsd(double[] arr)
    {
        double sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            sum += arr[i] * arr[i];
        }
        return (Math.sqrt(sum / arr.length));
    }

    /**
    N = arr.length
     */
    public static double rmsd(double[] arr1, double[] arr2)
    {
        double sum = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            double d = arr1[i] - arr2[i];
            sum += d * d;
        }
        return (Math.sqrt(sum / arr1.length));
    }

    /**
    N = arr.length
     */
    public static double rmsd(double[][] arr1, double[][] arr2)
    {
        double sum = 0;
        for (int i = 0; i < arr1.length; i++)
        {
            double d = PrimitiveDoubleTools.distance(arr1[i], arr2[i]);
            sum += d * d;
        }
        return (Math.sqrt(sum / arr1.length));
    }

    /**
    N = arr.length
     */
    public static double rmsd(double[][] arr)
    {
        double sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                sum += arr[i][j] * arr[i][j];
            }
        }
        return (Math.sqrt(sum / arr.length));
    }

    /**
    Returns the sum over all fields
     */
    public static double sum(double[] arr)
    {
        double sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            sum += arr[i];
        }
        return sum;
    }

    /**
    Returns the sum over all fields
     */
    public static double sum(double[][] arr)
    {
        double sum = 0;
        for (int i = 0; i < arr.length; i++)
        {
            sum += sum(arr[i]);
        }
        return sum;
    }

    public static int[] toInt(double[] arr)
    {
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = (int) (arr[i]);
        }
        return (res);
    }

    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public static int findForward(double[] arr, double val, int from)
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

    public static int findForward(double[] arr, double val)
    {
        return (findForward(arr, val, 0));
    }

    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public static int findForward(double[][] arr, double[] val, int from)
    {
        for (int i = from; i < arr.length; i++)
        {
            if (PrimitiveDoubleTools.equal(arr[i], val))
            {
                return i;
            }
        }
        return (-1);
    }

    public static int findForward(double[][] arr, double[] val)
    {
        return (findForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public static int findBackwards(double[] arr, double val, int from)
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

    public static int findBackwards(double[] arr, double val)
    {
        return (findBackwards(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public static int findBackwards(double[][] arr, double[] val, int from)
    {
        for (int i = from; i >= 0; i--)
        {
            if (PrimitiveDoubleTools.equal(arr[i], val))
            {
                return i;
            }
        }
        return (-1);
    }

    public static int findBackwards(double[][] arr, double[] val)
    {
        return (findBackwards(arr, val, 0));
    }

    public static int[] findAll(double[] arr, double val)
    {
        int[] res = new int[0];
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == val)
            {
                res = PrimitiveIntTools.concat(res, i);
            }
        }
        return (res);
    }

    /**
    returns a true copy of the given double array
     */
    public static double[] copy(double[] arr)
    {
        if (arr == null)
        {
            return (null);
        }
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = arr[i];
        }
        return (res);
    }

    /**
    returns a true copy of the given two-dimensional double array.
    This can be of any form (i.e. matrix or jagged array).
     */
    public static double[][] copy(double[][] arr)
    {
        double[][] res = new double[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == null)
            {
                res[i] = null;
            }
            else
            {
                res[i] = new double[arr[i].length];
                for (int j = 0; j < arr[i].length; j++)
                {
                    res[i][j] = arr[i][j];
                }
            }
        }
        return (res);
    }

    /**
    allocates the size for a two-dimensional double array with the same
    sizes as the given integer array.
     */
    public static double[][] alloc(int[][] arr)
    {
        double[][] res = new double[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = new double[arr[i].length];
        }
        return (res);
    }

    public static void fill(double[][] arr, double v)
    {
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                arr[i][j] = v;
            }
        }
    }

    /**
    allocates the size for a two-dimensional double array with the same
    sizes as the given integer array.
     */
    public static double[][] alloc(double[][] arr)
    {
        double[][] res = new double[arr.length][];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = new double[arr[i].length];
        }
        return (res);
    }

    public static double[][] repeat(double[] arr, int n)
    {
        double[][] res = new double[n][];
        for (int i = 0; i < res.length; i++)
        {
            res[i] = PrimitiveDoubleTools.copy(arr);
        }
        return (res);
    }

    public static int maxIndex(double[] arr)
    {
        double m = Double.NEGATIVE_INFINITY;
        int im = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] > m)
            {
                m = arr[i];
                im = i;
            }
        }
        return (im);
    }

    public static double max(double[] arr)
    {
        return (arr[maxIndex(arr)]);
    }

    public static double max(double[][] arr)
    {
        double m = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                if (arr[i][j] > m)
                {
                    m = arr[i][j];
                }
            }
        }
        return (m);
    }

    public static double maxForLine(double[][] arr, int i)
    {
        return (max(arr[i]));
    }

    public static double maxForColumn(double[][] arr, int i)
    {
        return (max(getColumn(arr, i)));
    }

    public static int minIndex(double[] arr)
    {
        double m = Double.POSITIVE_INFINITY;
        int im = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] < m)
            {
                m = arr[i];
                im = i;
            }
        }
        return (im);
    }

    public static double min(double[] arr)
    {
        return (arr[minIndex(arr)]);
    }

    public static double min(double[][] arr)
    {
        double m = Double.POSITIVE_INFINITY;
        for (int i = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                if (arr[i][j] < m)
                {
                    m = arr[i][j];
                }
            }
        }
        return (m);
    }

    /**
    Returns the indexes of the minimum and maximum values in arr.
     */
    public static int[] extremesIndexes(double[] arr)
    {
        double l = Double.POSITIVE_INFINITY, u = Double.NEGATIVE_INFINITY;
        int[] I =
        {
            -1, -1
        };
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] < l)
            {
                l = arr[i];
                I[0] = i;
            }
            if (arr[i] > u)
            {
                u = arr[i];
                I[1] = i;
            }
        }
        return (I);
    }

    public static double[] extremes(double[] arr)
    {
        int[] I = extremesIndexes(arr);
        double[] res =
        {
            arr[I[0]], arr[I[1]]
        };
        return (res);
    }

    public static int count(double[] arr, double val)
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

    public static int count(double[][] arr, double[] val)
    {
        int c = 0;
        for (int i = 0; i < arr.length; i++)
        {
            if (PrimitiveDoubleTools.equal(arr[i], val))
            {
                c++;
            }
        }
        return (c);
    }


    /**
    Quicksorts the given array.
    @return the indexes of the elements in the original array
    in the sorted order
     */
    public static int[] sort(double[] arr)
    {
        if (arr.length == 0)
        {
            return (new int[0]);
        }
        int[] indexes = PrimitiveIntTools.range(arr.length);
        quicksort(arr, indexes, 0, arr.length - 1);
        return (indexes);
    }

    /**
    sorts only a copy of arr, i.e. arr is not modified.
     */
    public static int[] sortedIndexes(double[] arr)
    {
        if (arr.length == 0)
        {
            return (new int[0]);
        }
        return (sort(PrimitiveDoubleTools.copy(arr)));
    }

    private static void quicksort(double[] arr, int[] indexes, int lo, int hi)
    {
        int i = lo, j = hi;
        double x = arr[(lo + hi) / 2];

        //  Aufteilung
        while (i <= j)
        {
            while (arr[i] < x)
            {
                i++;
            }
            while (arr[j] > x)
            {
                j--;
            }
            if (i <= j)
            {
                exchange(arr, i, j);
                PrimitiveIntTools.exchange(indexes, i, j);
                i++;
                j--;
            }
        }

        // Rekursion
        if (lo < j)
        {
            quicksort(arr, indexes, lo, j);
        }
        if (i < hi)
        {
            quicksort(arr, indexes, i, hi);
        }
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public static int[] smallestIndexes(double[] arr, int n)
    {
        int[] indexes = sortedIndexes(arr);
        return (PrimitiveIntTools.subarray(indexes, 0, n));
    }

    public static double[] smallest(double[] arr, int n)
    {
        return (subarray(arr, smallestIndexes(arr, n)));
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public static int[] largestIndexes(double[] arr, int n)
    {
        int[] indexes = sortedIndexes(arr);
        return (PrimitiveIntTools.subarray(indexes, indexes.length - n, indexes.length));
    }

    public static double[] largest(double[] arr, int n)
    {
        return (subarray(arr, largestIndexes(arr, n)));
    }

    public static void exchange(double[] arr, int i, int j)
    {
        double t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
    }

    /**
    reshapes the given 1-dimensional double array into a two-dimensional
    double array of size d1*d2
     */
    public static double[][] reshape(double[] arr, int d1, int d2)
    {
        if (arr.length != d1 * d2)
        {
            throw (new IllegalArgumentException("Illegal array size"));
        }
        double[][] res = new double[d1][d2];
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
    flattens the given 2-dimensional double array into a two-dimensional
    double array
     */
    public static double[] flatten(double[][] arr)
    {
        int n = 0;
        for (int i = 0; i < arr.length; i++)
        {
            n += arr[i].length;
        }

        double[] res = new double[n];
        for (int i = 0, k = 0; i < arr.length; i++)
        {
            for (int j = 0; j < arr[i].length; j++)
            {
                res[k++] = arr[i][j];
            }
        }
        return (res);
    }

    public static double[] subarray(double[] a, int i1, int i2)
    {
        double[] res = new double[i2 - i1];
        for (int i = i1; i < i2; i++)
        {
            res[i - i1] = a[i];
        }
        return (res);
    }

    public static double[][] subarray(double[][] a, int i1, int i2)
    {
        double[][] res = new double[i2 - i1][];
        for (int i = i1; i < i2; i++)
        {
            res[i - i1] = a[i];
        }
        return (res);
    }

    /**
    selects by indexes
     */
    public static double[] subarray(double[] arr, int[] selection)
    {
        double[] res = new double[selection.length];
        for (int i = 0; i < selection.length; i++)
        {
            res[i] = arr[selection[i]];
        }
        return (res);
    }

    /**
    selects by indexes
     */
    public static double[][] subarray(double[][] arr, int[] selection)
    {
        double[][] res = new double[selection.length][];
        for (int i = 0; i < selection.length; i++)
        {
            res[i] = arr[selection[i]];
        }
        return (res);
    }

    /**
    selects a square sub-matrix
     */
    public static double[][] submatrix(double[][] arr, int[] selection)
    {
        double[][] M = new double[selection.length][selection.length];
        for (int i = 0; i < M.length; i++)
        {
            for (int j = 0; j < M.length; j++)
            {
                M[i][j] = arr[selection[i]][selection[j]];
            }
        }
        return (M);
    }

    public static double[] getColumn(double[][] arr, int k)
    {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
        {
            res[i] = arr[i][k];
        }
        return (res);
    }

    public static double[] concat(double[] a1, double[] a2)
    {
        double[] res = new double[a1.length + a2.length];
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

    public static double[][] concat(double[][] a1, double[][] a2)
    {
        double[][] res = new double[a1.length + a2.length][];
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

    public static double[] concat(double[] a1, double a2)
    {
        double[] res = new double[a1.length + 1];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        res[j] = a2;
        return (res);
    }

    public static double[][] concat(double[][] a1, double[] a2)
    {
        double[][] res = new double[a1.length + 1][];
        int j = 0;
        for (int i = 0; i < a1.length; i++)
        {
            res[j++] = a1[i];
        }
        res[j] = a2;
        return (res);
    }

    public static double[] removeByIndex(double[] arr, int index)
    {
        double[] res = new double[arr.length - 1];
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

    public static double[][] removeByIndex(double[][] arr, int index)
    {
        double[][] res = new double[arr.length - 1][];
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

    public static double[] removeByIndex(double[] arr, int[] index)
    {
        if (arr.length == 0)
        {
            return (arr);
        }
        int[] keep = PrimitiveIntTools.removeByValue(PrimitiveIntTools.range(arr.length), index);
        return (subarray(arr, keep));
    }

    public static double[][] removeByIndex(double[][] arr, int[] index)
    {
        if (arr.length == 0)
        {
            return (arr);
        }
        int[] keep = PrimitiveIntTools.removeByValue(PrimitiveIntTools.range(arr.length), index);
        return (subarray(arr, keep));
    }

    public static void print(double[] arr)
    {
        print(arr, " ");
    }

    public static void print(double[] arr, String del)
    {
        if (arr == null)
        {
            System.out.println("null");
        }
        if (arr.length == 0)
        {
            System.out.println("");
        }

        if (arr.length > 0)
        {
            System.out.print(arr[0]);
        }
        for (int i = 1; i < arr.length; i++)
        {
            System.out.print(del + arr[i]);
        }
    }

    /**
    Prints only integers (rounds to the full number)
     */
    public static void printInt(double[] arr, String del)
    {
        if (arr == null)
        {
            System.out.println("null");
        }
        if (arr.length == 0)
        {
            System.out.println("");
        }

        System.out.print((int) Math.round(arr[0]));
        for (int i = 1; i < arr.length; i++)
        {
            System.out.print(del + (int) Math.round(arr[i]));
        }
    }


    public static void print(double[][] arr)
    {
        print(arr, " ", "\n");
    }

    public static void print(double[][] arr, String coldel, String linedel)
    {
        for (int i = 0; i < arr.length; i++)
        {
            print(arr[i], coldel);
            System.out.print(linedel);
        }
    }


    public static String toString(double[] arr)
    {
        return (toString(arr, ","));
    }

    public static String toString(double[] arr, String del)
    {
        if (arr == null)
        {
            return ("null");
        }
        if (arr.length == 0)
        {
            return ("");
        }

        StringBuffer strbuf = new StringBuffer(String.valueOf(arr[0]));
        for (int i = 1; i < arr.length; i++)
        {
            strbuf.append(del + arr[i]);
        }
        return (strbuf.toString());
    }

    public static String toString(double[][] arr)
    {
        return (toString(arr, ",", "\n"));
    }

    public static String toString(double[][] arr, String coldel, String linedel)
    {
        StringBuffer strbuf = new StringBuffer("");
        for (int i = 0; i < arr.length; i++)
        {
            strbuf.append(toString(arr[i], coldel) + linedel);
        }
        return (strbuf.toString());
    }

    public static void save(double[] arr, String file)
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

    public static void save(double[][] arr, String file)
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

    public static double[][] loadMatrix(String file)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            double[][] arr = (double[][]) in.readObject();
            in.close();
            return (arr);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
            return (null);
        }
    }

    public static double[] load(String file)
    {
        try
        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            double[] arr = (double[]) in.readObject();
            in.close();
            return (arr);
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
            return (null);
        }
    }

}
