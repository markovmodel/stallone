package stallone.ints;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntIterator;
import stallone.doubles.DoubleArrayView;
import stallone.doubles.ForeignBufferDoubleArray;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

public class ForeignBufferIntArray implements IIntArray
{
    private int rows;
    private int cols;
    private IntBuffer buff;
    
    // private default ctor.
    private ForeignBufferIntArray() {
    }

    public ForeignBufferIntArray(ByteBuffer bb, int rows, int cols) {
        if(bb.capacity() / Integer.SIZE < (rows * cols) / Integer.SIZE) {
            throw new OutOfMemoryError("byte buffer does not hold enough memory ("
                    + bb.capacity()/Integer.SIZE + ") to store " + rows*cols + " elements.");
        }
        
        bb.order(ByteOrder.nativeOrder());
        
        this.rows = rows;
        this.cols = cols;
        this.buff = bb.asIntBuffer();
    }

    @Override
    public int size()
    {
        return buff.capacity() / Integer.SIZE;
    }

    @Override
    public int order()
    {
        int order = 0;
        if (rows() > 1)
        {
            order++;
        }
        if (columns() > 1)
        {
            order++;
        }
        return order;
    }

    @Override
    public int rows()
    {
        return rows;
    }

    @Override
    public int columns()
    {
        return cols;
    }

    @Override
    public int get(int i)
    {
        return buff.get(i);
    }

    @Override
    public int get(int i, int j)
    {
        try
        {
            // column vector access
            if (cols == 1 && j == 0)
                return buff.get(i);
            // row vector access
            if (rows == 1 && i == 0)
                return buff.get(j);
            // matrix access
            return buff.get(i*cols + j);
        }
        catch(IndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error:" 
                    +"Trying to access element ("+i+","+j+") in a ("+rows+","+cols+") array"));
        }
    }

    @Override
    public void set(int i, int x)
    {
        buff.put(i, x);
    }

    @Override
    public void set(int i, int j, int x)
    {
        try
        {
            // column vector access
            if (cols == 1 && j == 0)
                buff.put(i, x);
            // row vector access
            else if (rows == 1 && i == 0)
                buff.put(j, x);
            // matrix access
            else {
                int index = i * cols + j;
                assert(i <= this.rows && j <= this.cols);
                assert(index % Double.SIZE) == 0; // byte boundary of double
                buff.put(index, x);
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            throw(new ArrayIndexOutOfBoundsException("Array index error:"+
                    "Trying to access element ("+i+","+j+") in a ("+rows+","+cols+") array"));
        }
    }

    @Override
    public int[] getArray()
    {
       if (buff.hasArray()) 
           return buff.array();
       
       int size = size();
       int[] res = new int[size];

       for (int i = 0; i < size; i++)
       {
           res[i] = get(i);
       }

       return res;
    }

    @Override
    public int[][] getTable()
    {
        int nrows = rows();
        int ncols = columns();
        int[][] res = new int[nrows][ncols];
        for (int i = 0; i < nrows; i++)
        {
            for (int j = 0; j < ncols; j++)
            {
                res[i][j] = get(i, j);
            }
        }

        return res;
    }

    @Override
    public int[] getRow(int row)
    {
        int ncols = columns();
        int[] res = new int[ncols];

        for (int i = 0; i < ncols; i++)
        {
            res[i] = get(row, i);
        }

        return res;
    }

    @Override
    public IIntArray viewRow(int i)
    {
        return (new IntArrayView(this, i, 0, i + 1, columns()));
    }

    @Override
    public int[] getColumn(int col)
    {
        int nrows = rows();
        int[] res = new int[nrows];

        for (int i = 0; i < nrows; i++)
        {
            res[i] = get(i, col);
        }

        return res;
    }

    @Override
    public IIntArray viewColumn(int j)
    {
        return (new IntArrayView(this, 0, j, rows(), j + 1));
    }

    @Override
    public IIntIterator iterator()
    {
        return new IntArrayIterator(this);
    }

    @Override
    public IIntIterator nonzeroIterator()
    {
        return new IntArrayNonzeroIterator(this);
    }

    @Override
    public IIntArray copy()
    {
        // we are doing some black magic here, to directly copy the memory!
        Field f; Method m; Unsafe unsafe;
        try
        {
            f = Unsafe.class.getDeclaredField("theUnsafe");
            m = Unsafe.class.getDeclaredMethod("copyMemory");
            f.setAccessible(true);
            m.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (SecurityException e)
        {
            e.printStackTrace();
            return null;
        } catch (NoSuchFieldException e)
        {
            e.printStackTrace();
            return null;
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
            return null;
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
            return null;
        }

        // allocate new memory for copy
        ByteBuffer clone = ByteBuffer.allocateDirect(buff.capacity());
        long src = ((DirectBuffer) buff).address();
        long dst = ((DirectBuffer) clone).address();
        // memcpy
        unsafe.copyMemory(src, dst, buff.capacity());
        return new ForeignBufferIntArray(clone, rows, cols);
    }

    @Override
    public void copyFrom(IIntArray other)
    {
        int size = size();
        if (size != other.size())
        {
            throw (new IllegalArgumentException("Incosistent sizes: This array has size "
                    + size + " the other array has size " + other.size()));
        }

        for (int i = 0; i < size; i++)
        {
            set(i, other.get(i));
        }
    }

    @Override
    public void copyInto(IIntArray other)
    {
        int size = size();
        if (size != other.size())
        {
            throw (new IllegalArgumentException("Incosistent sizes: This array has size "
                    + size + " the other array has size " + other.size()));
        }

        for (int i = 0; i < size; i++)
        {
            other.set(i, get(i));
        }
    }

    @Override
    public IIntArray create(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(Double.SIZE * size);
        return new ForeignBufferIntArray(bb, 1, size);
    }

    @Override
    public IIntArray create(int rows, int columns)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(Double.SIZE * rows * cols);
        return new ForeignBufferIntArray(bb, rows, cols);
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

}
