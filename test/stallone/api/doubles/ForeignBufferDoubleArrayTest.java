package stallone.api.doubles;

import static org.junit.Assert.fail;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import stallone.doubles.ForeignBufferDoubleArray;

public class ForeignBufferDoubleArrayTest
{
    IDoubleArray arr;

    @Before
    public void setUp() throws Exception
    {
        this.arr = new ForeignBufferDoubleArray(
                ByteBuffer.allocateDirect(10*Double.SIZE), 10, 1);
    }

    @Test
    public void testForeignBufferDoubleArray()
    {
        ByteBuffer buff = ByteBuffer.allocateDirect(10 * Double.SIZE);
        ForeignBufferDoubleArray array = new ForeignBufferDoubleArray(buff, 10, 1);
        
        Assert.assertEquals(10, array.rows());
        Assert.assertEquals(1, array.columns());
        Assert.assertEquals(10, array.size());
    }

    @Test
    public void testSetIntIntDouble()
    {
        for(int i = 0; i < 10; i++) {
            arr.set(i, i);
        }
        
        for(int i = 0; i < 10; i++) {
            Assert.assertEquals(i, arr.get(i), 0);
        }
    }
    
    //FIXME: index calculation does not check bounds...
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testSetOutOfBounds() {
//        arr.set(111, 42);
        arr.set(11, 11, 42);
    }
    
    @Test
    public void testGetIntInt()
    {
        // create matrix;
        ByteBuffer bb = ByteBuffer.allocateDirect(10*Double.SIZE);
        IDoubleArray a = new ForeignBufferDoubleArray(bb, 5, 5);
        
        // set values
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 5; j++) {
                a.set(i, j, i+j);
            }
        
        // compare values
        for(int i = 0; i < 5; i++)
            for(int j = 0; j < 5; j++) {
                double d = a.get(i, j);
                Assert.assertEquals(i+j, d, 0);
            }
    }

    @Test
    public void testCreateInt()
    {
        IDoubleArray a = arr.create(3);
        Assert.assertEquals(3, a.size());
    }
    
    public void testCopy() {
        IDoubleArray copy = arr.copy();
        for(int i = 0; i < arr.size(); i++) {
            Assert.assertEquals(arr.get(i), copy.get(i), 0);
        }
    }

}
