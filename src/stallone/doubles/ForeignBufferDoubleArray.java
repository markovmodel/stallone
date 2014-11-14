package stallone.doubles;

import static stallone.api.API.alg;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import stallone.api.API;
import stallone.api.doubles.IDoubleArray;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

public class ForeignBufferDoubleArray extends AbstractDoubleArray implements
        IDoubleArray
{
    private DoubleBuffer buff;
    private int rows, cols;
    
    // private default ctor.
    private ForeignBufferDoubleArray() {
    }
    
    /**
     * 
     * @param bb
     * @param rows
     * @param cols
     */
    public ForeignBufferDoubleArray(ByteBuffer bb, int rows, int cols) {
        if(bb.capacity() / Double.SIZE < (rows * cols) / Double.SIZE) {
            throw new OutOfMemoryError("byte buffer does not hold enough memory ("
                    + bb.capacity()/Double.SIZE + ") to store " + rows*cols + " elements.");
        }
        // ensure this is native order. Java uses per default big endian,
        // no matter of native byte order.
        bb.order(bb.order().nativeOrder());
        this.buff = bb.asDoubleBuffer();
        this.rows = rows;
        this.cols = cols;
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
    public double get(int i, int j)
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
    public void set(int i, int j, double x)
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
    /**
     * performs direct system memory copy of this vector
     */
    public IDoubleArray copy()
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
        return new ForeignBufferDoubleArray(clone, rows, cols);
    }

    @Override
    public IDoubleArray create(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * Double.SIZE);
        ForeignBufferDoubleArray da = new ForeignBufferDoubleArray(bb, 1, size);
        return da;
    }

    @Override
    public IDoubleArray create(int rows, int columns)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(Double.SIZE*rows*columns);
        ForeignBufferDoubleArray da = new ForeignBufferDoubleArray(bb, rows, columns);
        return da;
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }
    
    public static void main(String[] args)
    {
        int rows = 10;//Integer.parseInt(args[0]);
        System.out.println("rows: " + rows);
        int cols = rows;
        int size = rows*cols;
        ByteBuffer bb = ByteBuffer.allocateDirect(Double.SIZE/8*size);
        ForeignBufferDoubleArray da = new ForeignBufferDoubleArray(bb, rows,cols);
        IDoubleArray d2 = da.create(cols,rows);
        API.doubles.fill(da, 1);
        API.doubles.fill(d2, 2);
        System.out.println("arrays filled");
        
        IDoubleArray da2 = alg.product(da, d2);
        System.out.println("product calculated.\n"+da2);
    }

}
