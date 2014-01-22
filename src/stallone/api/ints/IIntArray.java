/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.ints;


/**
 * TODO: change this copy paste documentation
 * This is a joint interface for all primitive double containers with a fixed order
 * of elements, e.g. arrays, tables, lists. In principle real-valued tensors can
 * also be handled by this object, although we currently provide no method to
 * <i>directly</i> access more than two indexes in order to keep the interface small.
 * Single-index based access should work on higher-order tensors as well.
 * All IDoubleArray's have a sequence in which individual elements can be iterated.
 * The implementing object defines this sequence. Index-based methods such as get(i),
 * set(i, x), and the iterator operate based on this sequence.
 *
 * This interface is the standard way of exchanging real-valued double containers,
 * in order to make all Classes operating with such objects compatible. This option
 * was chosen in order to prefer flexibility and ease-to-use over strict type specificity
 * Methods operating with double containers may return more specific objects,
 * but should accept IDoubleArray (or IComplexArray, see below) as parameters
 *
 * If you want to operate with complex numbers, check out IComplexArray
 *
 * @author noe
 */
public interface IIntArray extends Iterable<IIntElement>
{
    /**
     * Returns the number of elements
     * @return
     */
    public int size();

    /**
     * Returns the order of the double container, e.g.:
     * 0 for single number,
     * 1 for array/vector,
     * 2 for table/matrix,
     * 3 for order-3-tensor, etc.
     * @return
     */
    public int order();

    /**
     * Returns the number of rows. Order-1-arrays are by default row vectors, (rows()=size(), columns()=1)
     * @return
     */
    public int rows();

    /**
     * Returns the number of columns. Order-1-arrays are by default row vectors, (rows()=size(), columns()=1)
     */
    public int columns();

    /**
     * Returns element i
     * @param i
     * @return
     */
    public int get(int i);

    /**
     * Access to elements by two indexes
     * @param i
     * @param j
     * @return
     */
    public int get(int i, int j);

    /**
     * Sets the element i
     * @param i
     * @param x
     */
    public void set(int i, int x);

    /**
     * Sets the element i,j
     * @param i
     * @param j
     * @param x
     */
    public void set(int i, int j, int x);

    /**
     * Returns a primitive double array containing all elements of the container in its natural sequence
     * @return
     */
    public int[] getArray();

    /**
     * Returns a primitive double table containing all elements of the container in its natural two-index sequence.
     * If the underlying container is an order-1 array,
     * this method will either return a row (size()x1) vector or a column (1xsize()) vector.
     * By default, an order-1-array is treated as column vector, however the implementing object may
     * override this default and operate with row vectors.
     */
    public int[][] getTable();

    /**
     * Returns the i'th row as primitive array
     * @return
     */
    public int[] getRow(int i);

    public IIntArray viewRow(int i);

    /**
     * Returns the j'th column as primitive array
     * @return
     */
    public int[] getColumn(int j);

    public IIntArray viewColumn(int j);

    /**
     * Returns an iterator
     * @return
     */
    @Override
    public IIntIterator iterator();

    /**
     * Returns an iterator that iterators only over the non-zero elements
     * @return
     */
    public IIntIterator nonzeroIterator();

    /**
     * Deep copy
     * @return
     */
    public IIntArray copy();

    /**
     * Deep copy from other array
     * @return
     */
    public void copyFrom(IIntArray other);

    /**
     * Deep copy into other array
     * @return
     */
    public void copyInto(IIntArray other);


    /**
     * Creates a new empty array of the same type with given size
     * @param size
     * @return
     */
    public IIntArray create(int size);

    /**
     * Creates a new empty array of the same type with given size
     * @param size
     * @return
     */
    public IIntArray create(int rows, int columns);


    public boolean isSparse();

}
