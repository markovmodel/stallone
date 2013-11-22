/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.ISortDouble;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class QuickSortDouble implements ISortDouble
{
    private IDoubleArray unsorted;
    private IDoubleArray sorted;
    private IIntArray indexes;


    private void quicksort(int lo, int hi)
    {
        int i = lo, j = hi;
        double x = sorted.get((lo + hi) / 2);

        //  Aufteilung
        while (i <= j)
        {
            while (sorted.get(i) < x)
            {
                i++;
            }
            while (sorted.get(j) > x)
            {
                j--;
            }
            if (i <= j)
            {
                Doubles.util.exchange(sorted, i, j);
                Ints.util.exchange(indexes, i, j);
                i++;
                j--;
            }
        }

        // Rekursion
        if (lo < j)
        {
            quicksort(lo, j);
        }
        if (i < hi)
        {
            quicksort(i, hi);
        }
    }

    @Override
    public void setData(IDoubleArray data)
    {
        this.unsorted = data;
        this.sorted = data.copy();
        this.indexes = Ints.create.arrayRange(data.size());
    }

    @Override
    public void setData(IDoubleArray data, IDoubleArray target)
    {
        this.unsorted = data;
        this.sorted = target;
        this.indexes = Ints.create.arrayRange(data.size());
    }

    @Override
    public IDoubleArray sort()
    {
        if (unsorted.size() <= 1)
        {
            return(sorted);
        }
        quicksort(0, sorted.size() - 1);

        return(sorted);
    }

    @Override
    public IIntArray getSortedIndexes()
    {
        return(indexes);
    }

    @Override
    public IDoubleArray getSortedData()
    {
        return(sorted);
    }
}
