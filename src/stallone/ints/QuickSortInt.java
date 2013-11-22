/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.ISortInt;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class QuickSortInt implements ISortInt
{
    private IIntArray unsorted;
    private IIntArray sorted;
    private IIntArray indexes;


    private void quicksort(int lo, int hi)
    {
        int i = lo, j = hi;
        int x = sorted.get((lo + hi) / 2);

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
                Ints.util.exchange(sorted, i, j);
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
    public void setData(IIntArray data)
    {
        this.unsorted = data;
        this.sorted = data.copy();
        this.indexes = Ints.create.arrayRange(data.size());
    }

    @Override
    public void setData(IIntArray data, IIntArray target)
    {
        this.unsorted = data;
        this.sorted = target;
        this.indexes = Ints.create.arrayRange(data.size());
    }

    @Override
    public IIntArray sort()
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
    public IIntArray getSortedData()
    {
        return(sorted);
    }
}
