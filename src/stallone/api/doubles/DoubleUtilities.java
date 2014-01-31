/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.doubles;

import static stallone.api.API.*;

import java.io.IOException;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import java.io.PrintStream;
import java.util.*;
import stallone.doubles.DenseDoubleArray;
import stallone.doubles.DoubleIO;
import stallone.doubles.QuickSortDouble;

/**
 *
 * @author noe
 */
public class DoubleUtilities
{

    private static DoubleIO io;


    /**
     * Sets the values to be the elements of the array. 
     */ 
    public void set(IDoubleArray arr, double[] values)
    {
        if (arr instanceof DenseDoubleArray)
            ((DenseDoubleArray)arr).set(values);
        else
            throw new UnsupportedOperationException("Mass value setting not (yet) supported for the given matrix type.");

    }

    public void fill(IDoubleArray arr, double d)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            arr.set(i, d);
        }
    }

    public void copyInto(IDoubleArray source, int start1, int end1, IDoubleArray target, int start2)
    {
        if (start1 < 0 || end1 >= source.size() || start1>=end1 || start2 < 0 || start2+(end1-start1) >= target.size())
            throw(new IllegalArgumentException("Attempting array copy with illegal indexes: from "+start1+" - "+end1+" / "+source.size()+". To "+start2+" - "+start2+(end1-start1)+" / "+target.size()));
        for (int i=start1; i<end1; i++)
            target.set(start2+i, source.get(i));
    }

    /**
     * Turns this array into its mirror image, i.e. puts the last indexes first and vice versa.
     * @param arr
     */
    public void mirror(IDoubleArray arr)
    {
        int n = arr.size();
        int nhalf = n / 2;
        for (int i = 0; i < nhalf; i++)
        {
            double h = arr.get(i);
            arr.set(i, arr.get(n - 1 - i));
            arr.set(n - 1 - i, h);
        }
    }

    public void exchange(IDoubleArray arr, int i, int j)
    {
        double h = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, h);
    }

    public double sum(IDoubleArray arr)
    {
        double res = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            res += arr.get(i);
        }
        return (res);
    }

    public int count(IDoubleArray arr, double val)
    {
        int c = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) == val)
            {
                c++;
            }
        }
        return (c);
    }

    /**
    Returns true if and only if the arrays are of equal size and
    all elements are equal up to the given tolerance
     */
    public boolean equal(IDoubleArray arr1, IDoubleArray arr2, double tol)
    {
        if (arr1.size() != arr2.size())
        {
            return (false);
        }
        for (int i = 0; i < arr1.size(); i++)
        {
            double v1 = arr1.get(i);
            double v2 = arr2.get(i);

            if (v1 == v2)
            {
                continue;
            }
            else if (Math.abs(v1 - v2) > tol)
            {
                return (false);
            }
        }
        return (true);
    }

    /**
     * Tests for exactly equal arrays
     * @param arr1
     * @param arr2
     * @return
     */
    public boolean equal(IDoubleArray arr1, IDoubleArray arr2)
    {
        return (equal(arr1, arr2, 0));
    }

    public int maxIndex(IDoubleArray arr)
    {
        double m = Double.NEGATIVE_INFINITY;
        int im = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) > m)
            {
                m = arr.get(i);
                im = i;
            }
        }
        return (im);
    }

    public double max(IDoubleArray arr)
    {
        return (arr.get(maxIndex(arr)));
    }

    public int minIndex(IDoubleArray arr)
    {
        double m = Double.POSITIVE_INFINITY;
        int im = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) < m)
            {
                m = arr.get(i);
                im = i;
            }
        }
        return (im);
    }

    public double min(IDoubleArray arr)
    {
        return (arr.get(minIndex(arr)));
    }

    public <T extends IDoubleArray> T subToNew(T arr, int start, int end)
    {
        T res = (T)arr.create(end - start);
        for (int i = start; i < end; i++)
        {
            if (res instanceof IDoubleList)
                ((IDoubleList)res).append(arr.get(i));
            else
                res.set(i - start, arr.get(i));
        }
        return (res);
    }

    public <T extends IDoubleArray> T subToNew(T arr, IIntArray indexes)
    {
        T res = (T)arr.create(indexes.size());
        for (int i = 0; i < indexes.size(); i++)
        {
            if (res instanceof IDoubleList)
                ((IDoubleList)res).append(arr.get(indexes.get(i)));
            else
                res.set(i, arr.get(indexes.get(i)));
        }
        return (res);
    }

    public <T extends IDoubleArray> T insertToNew(T arr, int index, double v)
    {
        T res = (T)arr.create(arr.size() + 1);
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        res.set(j, v);
        j++;
        for (int i = index; i < arr.size(); i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        return (res);
    }

    public <T extends IDoubleArray> T mergeToNew(Collection<T> a)
    {
        int size = 0;
        T template = null;
        for (Iterator<T> it = a.iterator(); it.hasNext();)
        {
            T c = it.next();
            if (template == null)
            {
                template = c;
            }
            size += c.size();
        }

        T res = (T)template.create(size);
        int j = 0;
        for (Iterator<T> it = a.iterator(); it.hasNext();)
        {
            T c = it.next();
            for (int i = 0; i < c.size(); i++)
            {
                res.set(j, c.get(i));
                j++;
            }
        }

        return (res);
    }

    public <T extends IDoubleArray> T mergeToNew(T a1, T a2)
    {
        T res = (T)a1.create(a1.size() + a2.size());
        int j = 0;
        for (int i = 0; i < a1.size(); i++)
        {
            res.set(j, a1.get(i));
            j++;
        }
        for (int i = 0; i < a2.size(); i++)
        {
            res.set(j, a2.get(i));
            j++;
        }
        return (res);
    }

    public <T extends IDoubleArray> T mergeToNew(T a1, double a2)
    {
        T res = (T)a1.create(a1.size() + 1);
        for (int i = 0; i < a1.size(); i++)
        {
            res.set(i, a1.get(i));
        }
        res.set(res.size() - 1, a2);
        return (res);
    }

    public boolean isSorted(IDoubleArray arr)
    {
        for (int i=0; i<arr.size()-1; i++)
            if (arr.get(i) > arr.get(i+1))
                return false;
        return true;
    }

    public void sort(IDoubleArray arr)
    {
        QuickSortDouble qs = new QuickSortDouble();
        qs.setData(arr, arr);
        qs.sort();
    }

    public IIntArray sortedIndexes(IDoubleArray arr)
    {
        QuickSortDouble qs = new QuickSortDouble();
        qs.setData(arr, arr.copy());
        qs.sort();
        return (qs.getSortedIndexes());
    }

    public <T extends IDoubleArray> T cleanToNew(T arr)
    {
        T uncleaned = (T)arr.copy();
        if (uncleaned.size() == 0)
        {
            return (uncleaned);
        }

        // sort
        Doubles.util.sort(uncleaned);

        // retrieve non-doubles
        IIntList Iclean = Ints.create.list(uncleaned.size());
        Iclean.set(0, 0);
        for (int i = 1; i < uncleaned.size(); i++)
        {
            if (uncleaned.get(i) != uncleaned.get(i - 1))
            {
                Iclean.append(i);
            }
        }

        return (subToNew(uncleaned, Iclean));
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public IIntArray smallestIndexes(IDoubleArray arr, int n)
    {
        IIntArray indexes = sortedIndexes(arr);
        return (Ints.util.subToNew(indexes, 0, n));
    }

    public <T extends IDoubleArray> T smallest(T arr, int n)
    {
        return (subToNew(arr, smallestIndexes(arr, n)));
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public IIntArray largestIndexes(IDoubleArray arr, int n)
    {
        IIntArray indexes = sortedIndexes(arr);
        IIntArray largest = Ints.util.subToNew(indexes, indexes.size() - n, indexes.size());
        Ints.util.mirror(largest);
        return (largest);
    }

    public <T extends IDoubleArray> T largest(T arr, int n)
    {
        return (subToNew(arr, largestIndexes(arr, n)));
    }

    public <T extends IDoubleArray> T smallValues(T arr, double maxValue)
    {
        return (subToNew(arr, smallValueIndexes(arr, maxValue)));
    }

    public IIntArray smallValueIndexes(IDoubleArray arr, double maxValue)
    {
        IIntList res = Ints.create.list(0);
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) <= maxValue)
            {
                res.append(i);
            }
        }
        return (res);
    }

    public <T extends IDoubleArray> T largeValues(T arr, double minValue)
    {
        return (subToNew(arr, largeValueIndexes(arr, minValue)));
    }

    public IIntArray largeValueIndexes(IDoubleArray arr, double minValue)
    {
        IIntList res = Ints.create.list(0);
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) >= minValue)
            {
                res.append(i);
            }
        }
        return (res);
    }

    /**
    @return all values with minVal <= val < maxVal
     */
    public <T extends IDoubleArray> T within(T arr, double minVal, double maxVal)
    {
        return (subToNew(arr, withinIndexes(arr, minVal, maxVal)));
    }

    /**
    @return all indexes whose values are minVal <= val < maxVal
     */
    public IIntArray withinIndexes(IDoubleArray arr, double minVal, double maxVal)
    {
        IIntList res = Ints.create.list(arr.size() / 2);
        for (int i = 0; i < arr.size(); i++)
        {
            if (minVal <= arr.get(i) && arr.get(i) < maxVal)
            {
                res.append(i);
            }
        }
        return (res);
    }

    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public int findForward(IDoubleArray arr, double val, int from)
    {
        for (int i = from; i < arr.get(i); i++)
        {
            if (arr.get(i) == val)
            {
                return i;
            }
        }
        return (-1);
    }

    public int findForward(IDoubleArray arr, double val)
    {
        return (findForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public int findBackwards(IDoubleArray arr, double val, int from)
    {
        for (int i = from; i >= 0; i--)
        {
            if (arr.get(i) == val)
            {
                return i;
            }
        }
        return (-1);
    }

    public int findBackwards(IDoubleArray arr, double val)
    {
        return (findBackwards(arr, val, 0));
    }

    private void findAll(IDoubleArray arr, double val, IIntList to)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) == val)
            {
                to.append(i);
            }
        }
    }

    public IIntList findAll(IDoubleArray arr, double val)
    {
        IIntList res = Ints.create.list(0);
        findAll(arr, val, res);
        return (res);
    }

    public IIntList findAll(IDoubleArray arr, IDoubleArray vals)
    {
        IIntList res = Ints.create.list(0);
        for (int i = 0; i < vals.size(); i++)
        {
            findAll(arr, vals.get(i), res);
        }
        return (res);
    }

    public boolean contains(IDoubleArray arr, double val)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) == val)
            {
                return (true);
            }
        }
        return (false);
    }

    public boolean containsAny(IDoubleArray arr, IDoubleArray val)
    {
        for (int i = 0; i < val.size(); i++)
        {
            if (contains(arr, val.get(i)))
            {
                return (true);
            }
        }
        return (false);
    }

    public boolean containsAll(IDoubleArray arr, IDoubleArray val)
    {
        for (int i = 0; i < val.size(); i++)
        {
            if (!contains(arr, val.get(i)))
            {
                return (false);
            }
        }
        return (true);
    }

    /**
     * Finds the element of arr that is closest to num. Only works if the array is sorted
     * @param arr
     * @param num
     * @return
     */
    public int findClosest(IDoubleArray arr, double num)
    {
        int i = locateSorted(arr, num);

        if (i == 0)
            return i;
        if (i == arr.size())
            return i-1;

        if (Math.abs(arr.get(i-1)-num) < Math.abs(arr.get(i)-num))
            return i-1;
        else
            return i;
    }

    /**
    Locates, by binary search the index of the first value that is >= the
    given number.
    Returns arr.size, if all array numbers are smaller.
    Will only work if arr is sorted.
     */
    public int locateSorted(IDoubleArray arr, double num)
    {
        int l = 0, m = arr.size() / 2, r = arr.size();
        boolean found = false;
        while (!found)
        {
            if (num < arr.get(m))
            {
                r = m;
                m = (r + l) / 2;
            }
            else if (num > arr.get(m))
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
            else if (arr.get(m - 1) != arr.get(m))
            {
                found = true;
            }
            else
            {
                m--;
            }
        }

        // move one right if arr[m] is smaller than num
        if (num > arr.get(m))
        {
            m++;
        }

        return (m);
    }

    /**
     * Inserts the given number into the sorted array arr. If arr is not
     * sorted, arr will be inserted somewhere.
     * WARNING: If the underlying array is primitive or an arraylist, this will be
     * very slow, because a whole new array copy must be built!
     */
    public void insertSorted(IDoubleList arr, double num)
    {
        int i = locateSorted(arr, num);
        arr.insert(i, num);
    }

    /**
    Inserts the given number into the sorted array arr, if
    max(arr) < num. The array will maintain its size, therefore
    the largest array element is lost.
    Will only work if arr is sorted.
     */
    public int insertSortedFixed(IDoubleArray arr, double num)
    {
        if (num >= arr.get(arr.size() - 1))
        {
            return (-1);
        }
        int i = arr.size() - 1;
        for (; i >= 1; i--)
        {
            if (arr.get(i - 1) >= num)
            {
                arr.set(i, arr.get(i - 1));
            }
            else
            {
                break;
            }
        }
        arr.set(i, num);
        return (i);
    }

    /**
     *   Finds the first occurrence of num in the sorted array arr.
     *   If arr is not sorted, this will not work.
     * @return -1 if num is not found
     */
    public int findSorted(IDoubleArray arr, double num)
    {
        int i = locateSorted(arr, num);
        if (arr.get(i) == num)
        {
            return (i);
        }
        else
        {
            return (-1);
        }
    }

    /**
    Returns the intersection of the two given arrays
     */
    public <T extends IDoubleArray> T intersectionToNew(T arr1, T arr2)
    {
        T arr1c = cleanToNew(arr1);
        T arr2c = cleanToNew(arr2);
        IDoubleList res = Doubles.create.list(Math.min(arr1c.size(), arr2c.size()));
        for (int i = 0; i < arr1c.size(); i++)
        {
            for (int j = 0; j < arr2c.size(); j++)
            {
                if (arr1c.get(i) == arr2c.get(j))
                {
                    res.append(arr1c.get(i));
                    break;
                }
            }
        }
        T resfinal = (T)arr1.create(res.size());
        copyInto(res, 0, res.size(), resfinal, 0);
        return (resfinal);
    }

    public <T extends IDoubleArray> T  unionToNew(T arr1, T arr2)
    {
        return (cleanToNew(mergeToNew(arr1, arr2)));
    }

    public <T extends IDoubleArray> T removeIndexToNew(T arr, int index)
    {
        T res = (T) arr.create(arr.size() - 1);
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        for (int i = index + 1; i < arr.size(); i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        return (res);
    }

    public <T extends IDoubleArray> T  removeIndexToNew(T arr, IIntArray index)
    {
        if (arr.size() == 0)
        {
            return (arr);
        }
        IIntArray keep = Ints.create.arrayRange(arr.size());
        keep = Ints.util.removeValueToNew(keep, index);
        return (subToNew(arr, keep));
    }

    /**
    Returns arr1 with all values occuring in arr2 removed. Does not change
    the order of arr1.
     */
    public <T extends IDoubleArray> T  removeValueToNew(T arr1, IDoubleArray vals)
    {
        IIntArray indexes = this.findAll(arr1, vals);
        return (removeIndexToNew(arr1, indexes));
    }

    public <T extends IDoubleArray> T  removeValueToNew(T arr1, int a2)
    {
        return (removeValueToNew(arr1, Doubles.create.arrayFrom(a2)));
    }

    public String toString(IDoubleArray arr)
    {
        return (DoubleIO.toString(arr));
    }

    public void print(IDoubleArray arr, PrintStream out)
    {
        DoubleIO.print(arr, out);
    }

    public void print(IDoubleArray arr, String del)
    {
        DoubleIO.print(arr, del);
    }

    public String toString(IDoubleArray arr, String del)
    {
        return (DoubleIO.toString(arr, del));
    }

    public void print(IDoubleArray arr)
    {
        DoubleIO.print(arr);
    }

    public String toString(IDoubleArray arr, String del,
            int predig, int postdig)
    {
        return (DoubleIO.toString(arr, del, predig, postdig));
    }

    public void print(IDoubleArray arr, String del, int predig, int postdig)
    {
        DoubleIO.print(arr, del, predig, postdig);
    }

    // ********************************************************************************
    //
    // Table operations
    //
    // ********************************************************************************


    public double sumRow(IDoubleArray arr, int row)
    {
        double res = 0;
        for (int i = 0; i < arr.columns(); i++)
        {
            res += arr.get(row,i);
        }
        return (res);
    }

    public double sumCol(IDoubleArray arr, int col)
    {
        double res = 0;
        for (int i = 0; i < arr.rows(); i++)
        {
            res += arr.get(i,col);
        }
        return (res);
    }

    /**
     * Transpose in place. Only possible if nrows = ncolumns
     * @param tab
     */
    public void transpose(IDoubleArray tab)
    {
        int nrows = tab.rows();
        int ncols = tab.columns();

        if (nrows != ncols)
        {
            throw (new IllegalArgumentException("Cannot transpose in place if nrows differs from ncolumns. Use createTranspose in the factory"));
        }

        for (int i = 0; i < nrows; i++)
        {
            for (int j = i + 1; j < ncols; j++)
            {
                double h = tab.get(i, j);
                tab.set(i, j, tab.get(j, i));
                tab.set(j, i, h);
            }
        }
    }

    public void copyInto(IDoubleArray aFrom, int rowA0, int colA0, int rowA1, int colA1,
            IDoubleArray bTo, int rowB0, int colB0)
    {
        if (rowA0 < 0 || colA0 < 0 || bTo.rows() < rowB0 + (rowA1 - rowA0) || bTo.columns() < colB0 + (colA1 - colA0))
        {
            throw (new IllegalArgumentException("Trying to copy array over boundaries of target"));
        }

        for (int i = rowA0; i < rowA1; i++)
        {
            for (int j = colA0; j < colA1; j++)
            {
                bTo.set(i + rowA0, j + colA0, aFrom.get(i, j));
            }
        }
    }

    public void copyInto(IDoubleArray aFrom, IDoubleArray bTo, int rowB0, int colB0)
    {
        copyInto(aFrom, 0, 0, aFrom.rows(), aFrom.columns(), bTo, rowB0, colB0);
    }

    public IDoubleArray merge(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.columns() != a2.columns())
        {
            throw (new IllegalArgumentException("Trying to merge incompatible tables"));
        }

        IDoubleArray res = a1.create(a1.rows() + a2.rows(), a1.columns());
        copyInto(a1, res, 0, 0);
        copyInto(a2, res, a1.rows(), 0);

        return (res);
    }

    public IDoubleArray mergeColumns(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.rows() != a2.rows())
        {
            throw (new IllegalArgumentException("Trying to merge incompatible tables"));
        }

        IDoubleArray res = a1.create(a1.rows(), a1.columns()+a2.columns());
        copyInto(a1, res, 0, 0);
        copyInto(a2, res, 0, a1.columns());

        return (res);
    }

    public <T extends IDoubleArray> T   insertRowToNew(T a, int rowIndex, IDoubleArray r)
    {
        if (a.columns() != r.size())
        {
            throw (new IllegalArgumentException("Trying to merge incompatible tables"));
        }

        T res = (T)a.create(a.rows() + 1, a.columns());

        copyInto(a, 0, 0, rowIndex, a.columns(), res, 0, 0);
        for (int i=0; i<r.size(); i++)
            res.set(rowIndex, i, r.get(i));
        copyInto(a, rowIndex, 0, a.rows(), a.columns(), res, rowIndex + 1, 0);

        return (res);
    }

    public <T extends IDoubleArray> T subTable(T a1, IIntArray rowIndexes, IIntArray colIndexes)
    {
        T res = (T) a1.create(rowIndexes.size(), colIndexes.size());
        for (int i = 0; i < rowIndexes.size(); i++)
        {
            for (int j = 0; j < colIndexes.size(); j++)
            {
                res.set(i, j, a1.get(rowIndexes.get(i), colIndexes.get(j)));
            }
        }
        return (res);
    }

    public <T extends IDoubleArray> T subRows(T a1, IIntArray rowIndexes)
    {
        return subTable(a1, rowIndexes, Ints.create.arrayRange(a1.columns()));
    }

    public <T extends IDoubleArray> T subColumns(T a1, IIntArray colIndexes)
    {
        return subTable(a1, Ints.create.arrayRange(a1.rows()), colIndexes);
    }

    /**


    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public int findRowForward(IDoubleArray arr, IDoubleArray val, int from)
    {
        for (int i = from; i < arr.get(i); i++)
        {
            if (equal(arr.viewRow(i), val))
            {
                return i;
            }
        }
        return (-1);
    }

    public int findRowForward(IDoubleArray arr, IDoubleArray val)
    {
        return (findRowForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public int findBackwards(IDoubleArray arr, IDoubleArray val, int from)
    {
        for (int i = from; i >= 0; i--)
        {
            if (equal(arr.viewRow(i), val))
            {
                return i;
            }
        }
        return (-1);
    }

    public int findBackwards(IDoubleArray arr, IDoubleArray val)
    {
        return (findBackwards(arr, val, 0));
    }

    public IIntArray findAllRows(IDoubleArray arr, IDoubleArray val)
    {
        IIntList res = Ints.create.list(0);
        for (int i = 0; i < arr.size(); i++)
        {
            if (equal(arr.viewRow(i), val))
            {
                res.append(i);
            }
        }
        return (res);
    }

    /**
     * Counts the rows equal to val
     * @param tab
     * @param val
     * @return
     */
    public int countRows(IDoubleArray tab, IDoubleArray val)
    {
        int c = 0;
        for (int i = 0; i < tab.rows(); i++)
        {
            if (this.equal(tab.viewRow(i), val))
            {
                c++;
            }
        }
        return (c);
    }

    public int countNonzero(IDoubleArray arr)
    {
        int res = 0;
        for (IDoubleIterator it = arr.nonzeroIterator(); 
                it.hasNext(); 
                res++);
        return res;
    }
    
    public int[] nonzeroIndexes1D(IDoubleArray arr)
    {
        IIntList list = intsNew.list(arr.rows());
        for (IDoubleIterator it = arr.nonzeroIterator(); it.hasNext();)
        {
            IDoubleElement de = it.next();
            list.append(de.index());
        }
        return list.getArray();
    }

    public int[][] nonzeroIndexes2D(IDoubleArray arr)
    {
        int[][] res = new int[countNonzero(arr)][2];
        int k = 0;
        for (IDoubleIterator it = arr.nonzeroIterator(); it.hasNext();)
        {
            IDoubleElement de = it.next();
            res[k][0] = de.row();
            res[k][1] = de.column();
            k++;
        }
        return res;
    }
    
    public double maxInLine(IDoubleArray arr, int i)
    {
        return (max(arr.viewRow(i)));
    }

    public double maxInColumn(IDoubleArray arr, int i)
    {
        return (max(arr.viewColumn(i)));
    }

    public <T extends IDoubleArray> T removeRow(T arr, int index)
    {
        return (removeIndex(arr, Ints.create.arrayFrom(index)));
    }

    public <T extends IDoubleArray> T removeIndex(T arr, IIntArray index)
    {
        if (arr.size() == 0)
        {
            return (arr);
        }
        IIntArray keep = Ints.util.removeValueToNew(Ints.create.arrayRange(arr.size()), index);
        return (subRows(arr, keep));
    }

    public String toString(IDoubleArray arr, String coldel, String linedel)
    {
        return (io.toString(arr, coldel, linedel));
    }

    public void print(IDoubleArray arr, String coldel, String linedel)
    {
        io.print(arr, coldel, linedel);
    }

    public String toString(IDoubleArray arr, String coldel, String linedel,
            int predig, int postdig)
    {
        return (io.toString(arr, coldel, linedel, predig, postdig));
    }

    public void print(IDoubleArray arr, String coldel, String linedel, int predig, int postdig)
    {
        io.print(arr, coldel, linedel, predig, postdig);
    }

    // ********************************************************************************
    //
    // List of Arrays operations
    //
    // ********************************************************************************

    public <T extends IDoubleArray> List<T> merge(List<T> l1, List<T> l2)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (T o : l1)
            res.add(o);
        for (T o : l2)
            res.add(o);
        return(res);
    }

    public <T extends IDoubleArray> List<T> subset(List<T> l1, IIntArray indexes)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (int i = 0; i<indexes.size(); i++)
            res.add(l1.get(indexes.get(i)));
        return(res);
    }

    public void saveMatrixDense(IDoubleArray matrix, String filename) 
            throws IOException
    {
        DoubleIO.writeMatrixDense(matrix, filename);
    }

    public void saveMatrixSparse(IDoubleArray matrix, String filename) throws IOException
    {
        DoubleIO.writeMatrixSparse(matrix, filename);
    }

    public void writeMatrixDense(IDoubleArray matrix, Appendable app) 
            throws IOException
    {
        DoubleIO.writeMatrixDense(matrix, app);
    }

    public void writeMatrixSparse(IDoubleArray matrix, Appendable app) 
            throws IOException
    {
        DoubleIO.writeMatrixSparse(matrix, app);
    }
    
}
