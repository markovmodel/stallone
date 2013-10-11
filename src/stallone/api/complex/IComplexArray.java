/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.complex;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public interface IComplexArray extends IDoubleArray
{
    /**
     * 
     * @return true if all elements are real, false otherwise
     */
    public boolean isReal();
    
    /**
     * Returns element i
     * @param i
     * @return 
     */
    public double getRe(int i);
    
    /**
     * Access to elements by two indexes
     * @param i
     * @param j
     * @return 
     */
    public double getRe(int i, int j);
    
    /**
     * Sets the element i
     * @param i
     * @param x 
     */
    public void setRe(int i, double x);
    
    /**
     * Sets the element i,j
     * @param i
     * @param j
     * @param x 
     */
    public void setRe(int i, int j, double x);    

    /**
     * Returns element i
     * @param i
     * @return 
     */
    public double getIm(int i);
    
    /**
     * Access to elements by two indexes
     * @param i
     * @param j
     * @return 
     */
    public double getIm(int i, int j);
    
    /**
     * Sets the element i
     * @param i
     * @param x 
     */
    public void setIm(int i, double x);
    
    /**
     * Sets the element i,j
     * @param i
     * @param j
     * @param x 
     */
    public void setIm(int i, int j, double x);    
    
    /**
     * 
     */
    public void set(int i, double re, double im);
    
    /**
     * Sets a complex number
     * @param i
     * @param j
     * @param re
     * @param im 
     */
    public void set(int i, int j, double re, double im);
    
    /**
     * Returns a primitive double array containing all elements of the container in its natural sequence
     * @return 
     */
    public double[] getRealArray();

    /**
     * Returns a primitive double array containing all elements of the container in its natural sequence
     * @return 
     */
    public double[] getImaginaryArray();

    /**
     * Returns a primitive double table containing all elements of the container in its natural two-index sequence.
     * If the underlying container is an order-1 array, 
     * this method will either return a row (size()x1) vector or a column (1xsize()) vector.
     * By default, an order-1-array is treated as column vector, however the implementing object may
     * override this default and operate with row vectors.
     */
    public double[][] getRealTable();    
    /**
     * Returns a primitive double table containing all elements of the container in its natural two-index sequence.
     * If the underlying container is an order-1 array, 
     * this method will either return a row (size()x1) vector or a column (1xsize()) vector.
     * By default, an order-1-array is treated as column vector, however the implementing object may
     * override this default and operate with row vectors.
     */
    public double[][] getImaginaryTable();

    /**
     * Returns the i'th row as primitive array
     * @return 
     */
    public double[] getRealRow(int i);
    
    /**
     * Returns the i'th row as primitive array
     * @return 
     */
    public double[] getImaginaryRow(int i);
    
    /**
     * Returns the j'th column as primitive array
     * @return 
     */
    public double[] getRealColumn(int j);    

    /**
     * Returns the j'th column as primitive array
     * @return 
     */
    public double[] getImaginaryColumn(int j);    
    
    /**
     * Returns an iterator
     * @return 
     */
    public IComplexIterator complexIterator();
    
    /**
     * Returns an iterator that iterators only over the non-zero elements
     * @return 
     */
    public IComplexIterator nonzeroComplexIterator();
    
    /**
     * Deep copy
     * @return
     */
    @Override
    public IComplexArray copy();

    /**
     * Deep copy from other array
     * @return
     */
    public void copyFrom(IComplexArray other);
    
    /**
     * Deep copy into other array
     * @return
     */
    public void copyInto(IComplexArray other);    
    
    @Override
    public IComplexArray create(int size);
    
    @Override
    public IComplexArray create(int rows, int cols);
    
    @Override
    public IComplexArray viewRow(int row);
    
    @Override
    public IComplexArray viewColumn(int col);

    @Override
    public IComplexArray view(int[] selectedRows, int[] selectedColumns);
    
    @Override
    public IComplexArray viewBlock(int left, int top, int bottom, int right);

    public IDoubleArray viewReal();
    
    public IDoubleArray viewImaginary();
    
    /**
     * Returns a view to the given element, in order to manipulate it directly
     * @param i
     * @param j
     * @return 
     */
    //@Override
    //public IComplexElement viewElement(int i, int j);

    /**
     * 
     * Returns a view to the given element, in order to manipulate it directly
     * @param ij
     * @return 
     */
    //@Override
    //public IComplexElement viewElement(int ij);
    
}
