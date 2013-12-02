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
package stallone.doubles;

/**
 * Base class for sparse vector implementations.
 *
 * @author  Martin Senne
 */
public abstract class SparseVectorIndexMap
{
    // minimal cache:
    protected int lastRequestedIndex = -1;
    protected int lastRequestedPosition = -1;

    protected int size;

    /** default growth factor when augmenting internal array. */
    private static double GROWTH_FACTOR = 2.0d;
    /** Indices of the non-zero elements. */
    protected int[] nonZeroIndices;
    /** Number of usedNonZero entries of nonZeroIndices. */
    protected int usedNonZero;

    SparseVectorIndexMap(final int _size)
    {
        size = _size;
        usedNonZero = 0;
        nonZeroIndices = new int[0];
    }

    SparseVectorIndexMap(final SparseVectorIndexMap base)
    {
        size = base.size;
        this.usedNonZero = base.usedNonZero;
        this.nonZeroIndices = new int[usedNonZero];
        System.arraycopy(base.nonZeroIndices, 0, this.nonZeroIndices, 0, usedNonZero);
    }

    /**
     * Searches for the value {@code value} in a subset of nonZero array and returns the position in nonZeroIndices if
     * found.
     *
     * @param   value  value to search for.
     * @param   left   start position in the index.
     * @param   right  one past the end position in the index.
     *
     * @return  position in nonZeroIndices, whose value equals given value . -1 if not found.
     */
    private int getPosWhereValueEqualKey(final int value, int left, int right)
    {
        right--;

        while (left <= right)
        {
            final int middle = left + ((right - left) / 2);
            final int valueAtMiddle = nonZeroIndices[middle];

            if (valueAtMiddle < value)
            {
                left = middle + 1;
            }
            else if (valueAtMiddle > value)
            {
                right = middle - 1;
            }
            else
            {
                return middle;
            }
        }

        return -1;
    }

    /**
     * Searches for the value {@code value} in a subset of nonZero array and returns the position in nonZeroIndices if
     * found.
     *
     * @param   value  value to search for.
     * @param   left   start position in the index.
     * @param   right  one past the end position in the index.
     *
     * @return  position, whose value is equal or greater given value.
     */
    private int getPosWhereValueGreaterEqualKey(final int value, int left, int right)
    {

        // zero array?
        if (left == right)
        {
            return right;
        }

        right--; // last index

        int middle = left + ((right - left) / 2);

        // binary search
        while (left <= right)
        {
            middle = left + ((right - left) / 2);

            final int valueAtMiddle = nonZeroIndices[middle];

            if (valueAtMiddle < value)
            {
                left = middle + 1;
            }
            else if (valueAtMiddle > value)
            {
                right = middle - 1;
            }
            else
            {
                return middle;
            }
        }

        if (nonZeroIndices[middle] >= value)
        { // no direct match, but an inf/sup was found
            return middle;
        }
        else
        { // no inf / sup, return at the end of the array
            return middle + 1; // One past end
        }
    }

    /**
     * Find the value {@code index} and return its position in nonZeroIndices. If not found, reallocate nonZeroIndices,
     * create new space and return the new position.
     *
     * @param   index
     *
     * @return  position in nonZeroIndices.
     */
    protected int addIndex(final int index)
    {

        // Try to find column index
        final int pos = getPosWhereValueGreaterEqualKey(index, 0, usedNonZero);

        if ((pos < usedNonZero) && (nonZeroIndices[pos] == index))
        { // found
            lastRequestedPosition = pos;
            return pos;
        }
        else
        { // not found
            createRoomAt(pos); // augmentNonZero or shift to create space

            // Put in new structure
            nonZeroIndices[pos] = index;
            
            lastRequestedPosition = pos;

            return pos;
        }
    }

    protected void removeIndex(final int index)
    {
        final int pos = getPosWhereValueEqualKey(index, 0, usedNonZero);

        if (pos >= 0)
        {

            // found, so remove entry at pos
            killRoomAt(pos);
        }
    }

    /**
     * @param   index  to search.
     *
     * @return  position in nonZeroIndices, -1 if not found.
     */
    protected int getPosition(final int index)
    {
        if (index == lastRequestedIndex)
        {
            return (lastRequestedPosition);
        }
        else
        {
            lastRequestedIndex = index;
        }

        // System.out.println("getPosition ( " + index + ", 0, " + usedNonZero +")");
        return getPosWhereValueEqualKey(index, 0, usedNonZero);
    }

    private void createRoomAt(final int pos)
    {

        final int currentLength = nonZeroIndices.length;

        // Check available memory
        final int firstBlockLength = pos;
        final int secondBlockLength = (usedNonZero - pos);
        usedNonZero++;

        if (usedNonZero > currentLength)
        { // not enough available

            // If zero-length, use new length of 1, else double the bandwidth
            int newLength = 1;

            if (currentLength > 0)
            {
                newLength = (int) (currentLength * GROWTH_FACTOR); // this is our heurictics.
            }

            augmentNonZero(newLength, firstBlockLength, secondBlockLength);
            augmentData(newLength, firstBlockLength, secondBlockLength);
        }
        else
        {
            shiftNonZeroRight(firstBlockLength, secondBlockLength);
            shiftDataRight(firstBlockLength, secondBlockLength);
        }
    }

    private void killRoomAt(final int pos)
    {


        usedNonZero--;

        // Check available memory
        final int firstBlockLength = pos;
        final int secondBlockLength = (usedNonZero - pos);

        // implement shrinking array here!!!!
        //
        // final int currentLength = nonZeroIndices.length;
        // if ( usedNonZero < (int) (currentLength / GROWTH_FACTOR) ) { // we can shrink
        // new
        // }
        shiftNonZeroLeft(firstBlockLength, secondBlockLength);
        shiftDataLeft(firstBlockLength, secondBlockLength);
    }

    /**
     * Implement, such that a new structure of length newLength is constructed and.
     *
     * <ul>
     * <li>entries A from 0 (incl.) to firstBlockLength (excl.) get copied to new structure from 0 (incl) to
     * firstBlockLength</li>
     * <li>entries B from firstBlockLength (incl.) to firstBlockLength+seondBlockLength get copied to firstBlockLength+1
     * (incl.) to firstBlockLength+seondBlockLength+1</li>
     * </ul>
     *
     * <p>E.g.:</p>
     *
     * <pre>
    firstBlocKLength = 3
    secondBlockLength = 4
    newLength = 9
    AAABBBB    (old structure)
    AAAxBBBB-- (new structure)
     * </pre>
     *
     * @param  newLength
     * @param  firstBlockLength
     * @param  secondBlockLength
     */
    protected abstract void augmentData(int newLength, int firstBlockLength, int secondBlockLength);

    private void augmentNonZero(final int newLength, final int firstBlockLength, final int secondBlockLength)
    {
        // 111122 = nonZeroIndices

        // --------- = newNonZeroIndices
        final int[] newNonZeroIndices = new int[newLength];

        // copy entries with 1
        // --------- = newNonZeroIndices (before)
        // 111122 = nonZeroIndices
        // 1111----- = newNonZeroIndices (after)
        System.arraycopy(nonZeroIndices, 0, newNonZeroIndices, 0, firstBlockLength);

        // copy entries with 2
        // 1111----- = newNonZeroIndices (before)
        // 111122 = nonZeroIndices
        // 1111-22-- = newNonZeroIndices
        // ^ at this postion, new space for index
        System.arraycopy(nonZeroIndices, firstBlockLength, newNonZeroIndices, firstBlockLength + 1, secondBlockLength);

        // assign back
        nonZeroIndices = newNonZeroIndices;
    }

    /**
     * Implement, such that the existing structure is shifted, such that.
     *
     * <ul>
     * <li>entries from firstBlockLength (incl.) to firstBlockLength+seondBlockLength get copied to firstBlockLength+1
     * (incl.) to firstBlockLength+seondBlockLength+1</li>
     * </ul>
     *
     * <p>E.g.:</p>
     *
     * <pre>
    firstBlocKLength = 2
    secondBlockLength = 5
    --BBBBB--- (old structure)
    --xBBBBB-- (reallocated structure)
     * </pre>
     *
     * @param  newLength
     * @param  firstBlockLength
     * @param  secondBlockLength
     */
    protected abstract void shiftDataRight(int firstBlockLength, int secondBlockLength);

    private void shiftNonZeroRight(final int firstBlockLength, final int secondBlockLength)
    {
        System.arraycopy(nonZeroIndices, firstBlockLength, nonZeroIndices, firstBlockLength + 1, secondBlockLength);
    }

    protected abstract void shiftDataLeft(int firstBlockLength, int secondBlockLength);

    private void shiftNonZeroLeft(final int firstBlockLength, final int secondBlockLength)
    {
        System.arraycopy(nonZeroIndices, firstBlockLength + 1, nonZeroIndices, firstBlockLength, secondBlockLength);
    }

    /**
     * since this modifies internal datastructures it should not be public.
     */
    private void test()
    {
        nonZeroIndices = new int[]
        {
            1, 4, 5, 7
        };
        usedNonZero = 4;
        size = 8;
        System.out.println("exp: 1 + findIndexEqualKey(4): " + getPosWhereValueEqualKey(4, 0, usedNonZero));
        System.out.println("exp:-1 + findIndexEqualKey(6): " + getPosWhereValueEqualKey(6, 0, usedNonZero));
        System.out.println("exp: 0 + findIndexGreaterOrEqualKey(0): "
                + getPosWhereValueGreaterEqualKey(0, 0, usedNonZero));
        System.out.println("exp: 0 + findIndexGreaterOrEqualKey(1): "
                + getPosWhereValueGreaterEqualKey(1, 0, usedNonZero));
        System.out.println("exp: 1 + findIndexGreaterOrEqualKey(2): "
                + getPosWhereValueGreaterEqualKey(2, 0, usedNonZero));
        System.out.println("exp: 1 + findIndexGreaterOrEqualKey(3): "
                + getPosWhereValueGreaterEqualKey(3, 0, usedNonZero));
        System.out.println("exp: 1 + findIndexGreaterOrEqualKey(4): "
                + getPosWhereValueGreaterEqualKey(4, 0, usedNonZero));
        System.out.println("exp: 2 + findIndexGreaterOrEqualKey(5): "
                + getPosWhereValueGreaterEqualKey(5, 0, usedNonZero));
        System.out.println("exp: 3 + findIndexGreaterOrEqualKey(6): "
                + getPosWhereValueGreaterEqualKey(6, 0, usedNonZero));
        System.out.println("exp: 3 + findIndexGreaterOrEqualKey(7): "
                + getPosWhereValueGreaterEqualKey(7, 0, usedNonZero));
    }

    /**
     * Convert one dimensional int array to String.
     */
    public static String toString(final int[] arr, final String del)
    {

        if (arr == null)
        {
            return ("null");
        }

        if (arr.length == 0)
        {
            return ("");
        }

        final StringBuffer out = new StringBuffer(arr.length * 10);

        for (int i = 0; i < arr.length; i++)
        {
            out.append(String.valueOf(arr[i]));

            if ((i + 1) < arr.length)
            {
                out.append(del);
            }
        }

        return out.toString();
    }

    public static String toString(final double[] arr, final String del)
    {

        if (arr == null)
        {
            return ("null");
        }

        if (arr.length == 0)
        {
            return ("");
        }

        final StringBuffer out = new StringBuffer(arr.length * 10);

        for (int i = 0; i < arr.length; i++)
        {
            out.append(String.valueOf(arr[i]));

            if ((i + 1) < arr.length)
            {
                out.append(del);
            }
        }

        return out.toString();
    }
    
}
