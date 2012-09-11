/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import stallone.ints.QuickSortInt;
import stallone.ints.IntIO;

/**
 *
 * @author noe
 */
public class IntUtilities
{ 
    private static IntIO io;
    
    public void fill(IIntArray arr, int d)
    {
        for (int i=0; i<arr.size(); i++)
            arr.set(i,d);
    }
        
    /**
     * Turns this array into its mirror image, i.e. puts the last indexes first and vice versa.
     * @param arr 
     */
    public void mirror(IIntArray arr)
    {
        int n = arr.size();
        int nhalf = n/2;
        for (int i = 0; i < nhalf; i++)
        {
            int h = arr.get(i);
            arr.set(i, arr.get(n-1-i));
            arr.set(n-1-i,h);
        }
    }

    public void exchange(IIntArray arr, int i, int j)
    {
        int h = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, h);
    }

    public IIntArray addToNew(IIntArray arr1, int v)
    {
        IIntArray res = arr1.copy();
        for (int i = 0; i < res.size(); i++)
        {
            res.set(i, res.get(i) + v);
        }
        return (res);
    }

    public IIntArray addToNew(IIntArray arr1, IIntArray arr2)
    {
        IIntArray res = arr1.copy();
        for (int i = 0; i < res.size(); i++)
        {
            res.set(i, res.get(i) + arr2.get(i));
        }
        return (res);
    }

    public IIntArray addWeightedToNew(int w1, IIntArray arr1, int w2, IIntArray arr2)
    {
        if (arr1.size() != arr2.size())
        {
            throw (new IllegalArgumentException("incompatible vectors!"));
        }
        IIntArray res = arr1.copy();
        for (int i = 0; i < arr1.size(); i++)
        {
            res.set(i, w1 * arr1.get(i) + w2 * arr2.get(i));
        }
        return (res);
    }    

    public void increment(IIntArray arr, int d)
    {
        for (int i=0; i<arr.size(); i++)
            arr.set(i, arr.get(i)+d);
    }

    public void increment(IIntArray arr, IIntArray d)
    {
        for (int i=0; i<arr.size(); i++)
            arr.set(i, arr.get(i)+d.get(i));
    }

    public void scale(int a, IIntArray arr)
    {
        for (int i=0; i<arr.size(); i++)
            arr.set(i, a*arr.get(i));
    }
    
    public IIntArray multiplyElementsToNew(IIntArray arr1, IIntArray arr2)
    {
        IIntArray res = arr1.create(arr1.size());
        for (int i=0; i<res.size(); i++)
            res.set(i, arr1.get(i)*arr2.get(i));
        return(res);
    }

    public IIntArray divideElementsToNew(IIntArray arr1, IIntArray arr2)
    {
        IIntArray res = arr1.create(arr1.size());
        for (int i=0; i<res.size(); i++)
            res.set(i, arr1.get(i)/arr2.get(i));
        return(res);
    }
    
    public void negate(IIntArray arr)
    {
        scale(-1, arr);
    }

    public void square(IIntArray arr)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            arr.set(i, arr.get(i)*arr.get(i));
        }
    }
    
    public double sum(IIntArray arr)
    {
        double res = 0;
        for (int i=0; i<arr.size(); i++)
            res += arr.get(i);
        return(res);
    }

    public double distance(IIntArray arr1, IIntArray arr2)
    {
        double d = 0;
        for (int i = 0; i < arr1.size(); i++)
        {
            double dev = arr1.get(i) - arr2.get(i);
            d += dev * dev;
        }
        return (Math.sqrt(d));
    }
    
    
    public int count(IIntArray arr, int val)
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
    public boolean equal(IIntArray arr1, IIntArray arr2)
    {
        if (arr1.size() != arr2.size())
        {
            return (false);
        }
        for (int i = 0; i < arr1.size(); i++)
        {
            double v1 = arr1.get(i);
            double v2 = arr2.get(i);
            
            if (v1 != v2)
            {
                return (false);
            }
        }
        return (true);
    }

    
    public int maxIndex(IIntArray arr)
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

    public int max(IIntArray arr)
    {
        return (arr.get(maxIndex(arr)));
    }


    public int minIndex(IIntArray arr)
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

    public int min(IIntArray arr)
    {
        return (arr.get(minIndex(arr)));
    }
    
    
    public IIntArray subToNew(IIntArray arr, int start, int end)
    {
        IIntArray res = Ints.create.array(end - start);
        for (int i = start; i < end; i++)
        {
            res.set(i - start, arr.get(i));
        }
        return (res);
    }

    public IIntArray subToNew(IIntArray arr, IIntArray indexes)
    {
        IIntArray res = Ints.create.array(indexes.size());
        for (int i = 0; i < indexes.size(); i++)
        {
            res.set(i, arr.get(indexes.get(i)));
        }
        return (res);
    }    
    
    public IIntArray insertToNew(IIntArray arr, int index, int v)
    {
        IIntArray res = arr.create(arr.size() + 1);
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        res.set(j,v);
        j++;
        for (int i = index; i < arr.size(); i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        return (res);
    }
     
    public IIntArray mergeToNew(Collection<IIntArray> a)
    {
        int size = 0;
        IIntArray template = null;
        for (Iterator<IIntArray> it = a.iterator(); it.hasNext();)
        {
            IIntArray c = it.next();
            if (template == null)
                template = c;
            size += c.size();
        }

        IIntArray res = template.create(size);
        int j=0;
        for (Iterator<IIntArray> it = a.iterator(); it.hasNext();)
        {
            IIntArray c = it.next();
            for (int i=0; i<c.size(); i++)
            {
                res.set(j, c.get(i));
                j++;
            }
        }

        return(res);
    }
    
    public IIntArray mergeToNew(IIntArray a1, IIntArray a2)
    {
        IIntArray res = a1.create(a1.size()+a2.size());
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

    public IIntArray mergeToNew(IIntArray a1, int a2)
    {
        IIntArray res = a1.create(a1.size() + 1);
        for (int i = 0; i < a1.size(); i++)
        {
            res.set(i, a1.get(i));
        }
        res.set(res.size()-1, a2);
        return (res);
    }    
    
    public void sort(IIntArray arr)
    {
        QuickSortInt qs = new QuickSortInt();
        qs.setData(arr, arr);
        qs.sort();
    }
    
    public IIntArray sortedIndexes(IIntArray arr)
    {
        QuickSortInt qs = new QuickSortInt();
        qs.setData(arr, arr.copy());
        qs.sort();
        return(qs.getSortedIndexes());
    }

    public IIntArray cleanToNew(IIntArray arr)
    {
        IIntArray uncleaned = arr.copy();
        if (uncleaned.size() == 0)
        {
            return (uncleaned);
        }
        
        // sort
        sort(uncleaned);

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
    public IIntArray smallestIndexes(IIntArray arr, int n)
    {
        IIntArray indexes = sortedIndexes(arr);
        return (subToNew(indexes, 0, n));
    }

    public IIntArray smallest(IIntArray arr, int n)
    {
        return (subToNew(arr, smallestIndexes(arr, n)));
    }

    /**
    @return the indexes of the n smalles values in arr.
     */
    public IIntArray largestIndexes(IIntArray arr, int n)
    {
        IIntArray indexes = sortedIndexes(arr);
        IIntArray largest = subToNew(indexes, indexes.size() - n, indexes.size());
        Ints.util.mirror(largest);
        return(largest);
    }

    public IIntArray largest(IIntArray arr, int n)
    {
        return (subToNew(arr, largestIndexes(arr, n)));
    }
    
    public IIntArray smallValues(IIntArray arr, int maxValue)
    {
        return(subToNew(arr, smallValueIndexes(arr, maxValue)));
    }

    public IIntArray smallValueIndexes(IIntArray arr, int maxValue)
    {
        IIntList res = Ints.create.list(arr.size()/2);
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) <= maxValue)
            {
                res.append(i);
            }
        }
        return (res);
    }

    public IIntArray largeValues(IIntArray arr, int minValue)
    {
        return(subToNew(arr, largeValueIndexes(arr, minValue)));
    }

    public IIntArray largeValueIndexes(IIntArray arr, int minValue)
    {
        IIntList res = Ints.create.list(arr.size()/2);
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
    public IIntArray within(IIntArray arr, int minVal, int maxVal)
    {
        return(subToNew(arr, withinIndexes(arr, minVal, maxVal)));
    }

    /**
    @return all indexes whose values are minVal <= val < maxVal
     */
    public IIntArray withinIndexes(IIntArray arr, int minVal, int maxVal)
    {
        IIntList res = Ints.create.list(arr.size()/2);
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
    public int findForward(IIntArray arr, int val, int from)
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

    public int findForward(IIntArray arr, int val)
    {
        return (findForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public int findBackwards(IIntArray arr, int val, int from)
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

    public int findBackwards(IIntArray arr, int val)
    {
        return (findBackwards(arr, val, 0));
    }


    private void findAll(IIntArray arr, int val, IIntList to)
    {
        for (int i = 0; i < arr.size(); i++)
        {
            if (arr.get(i) == val)
            {
                to.append(i);
            }
        }
    }
    
    public IIntList findAll(IIntArray arr, int val)
    {
        IIntList res = Ints.create.list(0);
        findAll(arr, val, res);
        return (res);
    }    
    
    public IIntList findAll(IIntArray arr, IIntArray vals)
    {
        IIntList res = Ints.create.list(0);
        for (int i=0; i<vals.size(); i++)
            findAll(arr, vals.get(i), res);
        return(res);
    }
    
    public boolean contains(IIntArray arr, int val)
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

    public boolean containsAny(IIntArray arr, IIntArray val)
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

    public boolean containsAll(IIntArray arr, IIntArray val)
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
    Locates, by binary search the index of the first value that is >= the
    given number.
    Returns arr.size, if all array numbers are smaller.
    Will only work if arr is sorted.
     */
    public int locateSorted(IIntArray arr, int num)
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
    public void insertSorted(IIntList arr, int num)
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
    public int insertSortedFixed(IIntArray arr, int num)
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
    public int findSorted(IIntArray arr, int num)
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
    public IIntList intersectionToNew(IIntArray arr1, IIntArray arr2)
    {
        IIntArray arr1c = cleanToNew(arr1);
        IIntArray arr2c = cleanToNew(arr2);
        IIntList res = Ints.create.list(Math.min(arr1c.size(), arr2c.size()));
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
        return (res);
    }
    
    public IIntArray unionToNew(IIntArray arr1, IIntArray arr2)
    {
        return(cleanToNew(mergeToNew(arr1,arr2)));
    }
        
    public IIntArray removeIndexToNew(IIntArray arr, int index)
    {
        IIntArray res = arr.create(arr.size() - 1);
        int j = 0;
        for (int i = 0; i < index; i++)
        {
            res.set(j,arr.get(i));
            j++;
        }
        for (int i = index + 1; i < arr.size(); i++)
        {
            res.set(j, arr.get(i));
            j++;
        }
        return (res);
    }

    public IIntArray removeIndexToNew(IIntArray arr, IIntArray indexes)
    {
        if (arr.size() == 0)
        {
            return (arr);
        }
        
        boolean[] exclude = new boolean[arr.size()];
        int nex = 0;
        for (int i = 0; i < indexes.size(); i++)
        {
            if (!exclude[indexes.get(i)])
            {
                exclude[indexes.get(i)] = true;
                nex++;
            }
        }
        IIntArray res = arr.create(arr.size() - nex);
        int j = 0;
        for (int i = 0; i < arr.size(); i++)
        {
            if (!exclude[i])
            {
                res.set(j, arr.get(i));
                j++;
            }
        }
        return (res);        
    }

    /**
    Returns arr1 with all values occuring in arr2 removed. Does not change
    the order of arr1.
     */
    public IIntArray removeValueToNew(IIntArray arr1, IIntArray vals)
    {
        IIntArray indexes = this.findAll(arr1, vals);
        return(removeIndexToNew(arr1, indexes));
    }


    public IIntArray removeValueToNew(IIntArray arr1, int a2)
    {
        return (removeValueToNew(arr1, Ints.create.arrayFrom(a2)));
    }
    

    public String toString(IIntArray arr, String coldel, String linedel)
    {
        return (io.toString(arr, coldel, linedel));
    }

    public void print(IIntArray arr, String coldel, String linedel)
    {
        io.print(arr, coldel, linedel);
    }

    public String toString(IIntArray arr, String coldel, String linedel,
            int predig, int postdig)
    {
        return (io.toString(arr, coldel, linedel, predig, postdig));
    }

    public void print(IIntArray arr, String coldel, String linedel, int predig, int postdig)
    {
        io.print(arr, coldel, linedel, predig, postdig);
    }
    

    
    
    
    // ********************************************************************************
    //
    // Table operations
    //
    // ********************************************************************************

    public int sumRow(IIntArray arr, int row)
    {
        int res = 0;
        for (int i = 0; i < arr.columns(); i++)
        {
            res += arr.get(row,i);
        }
        return (res);
    }

    public int sumCol(IIntArray arr, int col)
    {
        int res = 0;
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
    public void transpose(IIntArray tab)
    {
        int nrows = tab.rows();
        int ncols = tab.columns();
        
        if (nrows != ncols)
            throw(new IllegalArgumentException("Cannot transpose in place if nrows differs from ncolumns. Use createTranspose in the factory"));
        
        for (int i=0; i<nrows; i++)
        {
            for (int j=i+1; j<ncols; j++)
            {
                int h = tab.get(i,j);
                tab.set(i,j, tab.get(j,i));
                tab.set(j,i, h);
            }
        }
    }
    
    public void copyInto(IIntArray source, int from, int to, IIntArray target, int bFrom)
    {
        if (from < 0 || to > source.size() || to < from || bFrom < 0 || bFrom > target.size())
        {
            throw(new IllegalArgumentException("Trying to copy array over boundaries of target"));
        }
        
        for (int i=from; i<to; i++)
            target.set(i+bFrom, source.get(i));
    }

    public void copyInto(IIntArray aFrom, int rowA0, int colA0, int rowA1, int colA1, 
                         IIntArray bTo, int rowB0, int colB0)
    {
        if (rowA0 < 0 || colA0 < 0 || bTo.rows() < rowB0+(rowA1-rowA0) || bTo.columns() < colB0+(colA1-colA0))
        {
            throw(new IllegalArgumentException("Trying to copy array over boundaries of target"));
        }
        
        for (int i=rowA0; i<rowA1; i++)
        {
            for (int j=colA0; j<colA1; j++)
            {
                bTo.set(i+rowA0, j+colA0, aFrom.get(i,j));
            }
        }
    }

    public void copyRowsInto(IIntArray aFrom, IIntArray bTo, int rowB0, int colB0)
    {
        copyInto(aFrom, 0, 0, aFrom.rows(), aFrom.columns(), bTo, rowB0, colB0);
    }
    
    public IIntArray mergeRowsToNew(IIntArray a1, IIntArray a2)
    {
        if (a1.columns() != a2.columns())
        {
            throw(new IllegalArgumentException("Trying to merge incompatible tables"));
        }
        
        IIntArray res = Ints.create.table(a1.rows()+a2.rows(), a1.columns());
        copyRowsInto(a1, res, 0, 0);
        copyRowsInto(a2, res, a1.rows(), 0);
        
        return(res);
    }
        
    public IIntArray insertRowToNew(IIntArray a, int rowIndex, IIntArray r)
    {
        if (a.columns() != r.size())
        {
            throw(new IllegalArgumentException("Trying to merge incompatible tables"));
        }

        IIntArray res = Ints.create.table(a.rows()+1, a.columns());
        
        copyInto(a, 0, 0, rowIndex, a.columns(), res, 0, 0);
        copyRowsInto(Ints.create.tableReshape(r, 1, r.size()), res, rowIndex, 0);
        copyInto(a, rowIndex, 0, a.rows(), a.columns(), res, rowIndex+1, 0);
       
        return(res);
    }

    
    public IIntArray subToNew(IIntArray a1, IIntArray rowIndexes, IIntArray colIndexes)
    {
        IIntArray res = Ints.create.table(rowIndexes.size(), colIndexes.size());
        for (int i=0; i<rowIndexes.size(); i++)
        {
            for (int j=0; j<colIndexes.size(); j++)
            {
                res.set(i,j,a1.get(rowIndexes.get(i),colIndexes.get(j)));
            }
        }
        return(res);
    }
            
    public IIntArray subRowsToNew(IIntArray a1, IIntArray rowIndexes)
    {
        return subToNew(a1, rowIndexes, Ints.create.arrayRange(a1.columns()));
    }

    public IIntArray subColsToNew(IIntArray a1, IIntArray colIndexes)
    {
        return subToNew(a1, Ints.create.arrayRange(a1.rows()), colIndexes);
    }
    
    /**
    Returns the index with the next occurance of the given number, starting
    from index "from".
    Returns -1, if not found.
     */
    public int findRowForward(IIntArray arr, IIntArray val, int from)
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

    public int findRowForward(IIntArray arr, IIntArray val)
    {
        return (findRowForward(arr, val, 0));
    }

    /**
    Returns the index with the previous occurance of the given number,
    starting from index "from".
    Returns -1, if not found.
     */
    public int findBackwards(IIntArray arr, IIntArray val, int from)
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

    public int findBackwards(IIntArray arr, IIntArray val)
    {
        return (findBackwards(arr, val, 0));
    }


    public IIntArray findAllRows(IIntArray arr, IIntArray val)
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
    public int countRows(IIntArray tab, IIntArray val)
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

    public double maxInLine(IIntArray arr, int i)
    {
        return (max(arr.viewRow(i)));
    }

    public double maxInColumn(IIntArray arr, int i)
    {
        return (max(arr.viewColumn(i)));
    }
    
    public IIntArray removeRow(IIntArray arr, int index)
    {
        return(removeIndex(arr, Ints.create.arrayFrom(index)));
    }

    public IIntArray removeIndex(IIntArray arr, IIntArray index)
    {
        if (arr.size() == 0)
        {
            return (arr);
        }
        IIntArray keep = Ints.util.removeValueToNew(Ints.create.arrayRange(arr.size()), index);
        return (subRowsToNew(arr, keep));
    }    

    public String toString(IIntArray arr)
    {
        return (toString(arr, ",", "\n"));
    }

    public void print(IIntArray arr)
    {
        System.out.println(toString(arr));
    }


    
    // ********************************************************************************
    //
    // List of Arrays operations
    //
    // ********************************************************************************
    
    public <T extends IIntArray> List<T> merge(List<T> l1, List<T> l2)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (T o : l1)
            res.add(o);
        for (T o : l2)
            res.add(o);
        return(res);
    }
    
    public <T extends IIntArray> List<T> subset(List<T> l1, IIntArray indexes)
    {
        ArrayList<T> res = new ArrayList<T>();
        for (int i = 0; i<indexes.size(); i++)
            res.add(l1.get(indexes.get(i)));
        return(res);
    }    
}
