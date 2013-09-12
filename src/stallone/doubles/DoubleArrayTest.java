/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class DoubleArrayTest
{
    public static void assertEqual(IDoubleArray a1, IDoubleArray a2, double tolerance)
    {
        assertEqualDimensions(a1,a2);
        for (int i=0; i<a1.rows(); i++)
        {
            for (int j=0; j<a1.columns(); j++)
            {
                if (Math.abs(a1.get(i,j) - a2.get(i,j)) > tolerance)
                    throw(new IllegalArgumentException("Array elements differ more than admitted tolerance "+tolerance+": a1["+i+","+j+"] = "+a1.get(i,j)+"\ta2["+i+","+j+"] = "+a2.get(i,j)));
            }
        }
    }

    public static void assertEqualSize(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.size() != a2.size())
            throw(new IllegalArgumentException("Arrays have different sizes: \n"+
                    a1.rows()+"x"+a1.columns()+") has size"+a1.size()+"\n"+
                    a2.rows()+"x"+a2.columns()+") has size"+a2.size()));
    }
    
    
    public static void assertEqualOrder(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.order() != a2.order())
            throw(new IllegalArgumentException("Arrays have different orders: \n"+
                    a1.rows()+"x"+a1.columns()+") has order"+a1.order()+"\n"+
                    a2.rows()+"x"+a2.columns()+") has order"+a2.order()));
    }
    
    public static void assertEqualDimensions(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.rows() != a2.rows() || a1.columns() != a2.columns())
            throw(new IllegalArgumentException("Arrays have different dimensions: \n"+
                    a1.rows()+"x"+a1.columns()+") vs\n"+
                    a2.rows()+"x"+a2.columns()+")"));            
    }
    
    public static void assertCanMultiply(IDoubleArray a1, IDoubleArray a2)
    {
        if (a1.columns() != a2.rows())
            throw(new IllegalArgumentException("Arrays have incompatible dimensions for multiplication: \n"+
                    a1.rows()+"x"+a1.columns()+") vs\n"+
                    a2.rows()+"x"+a2.columns()+")"));
    }
    
    public static void assertRows(IDoubleArray a1, int nrows)
    {
        if (nrows != a1.rows())
            throw(new IllegalArgumentException("Array of dimension "+a1.rows()+" x "+a1.columns()+" found but expected: "+nrows+" rows"));
    }

    public static void assertColumns(IDoubleArray a1, int ncols)
    {
        if (ncols != a1.columns())
            throw(new IllegalArgumentException("Array of dimension "+a1.rows()+" x "+a1.columns()+" found but expected: "+ncols+" columns"));
    }

    public static void assertSquare(IDoubleArray a1)
    {
        if (a1.rows() != a1.columns())
            throw(new IllegalArgumentException("Array of dimension "+a1.rows()+" x "+a1.columns()+" found but expected square matrix"));
    }
    
    public static void assertOrder(IDoubleArray a1, int order)
    {
        if (order != a1.order())
            throw(new IllegalArgumentException("Array of dimension "+a1.rows()+" x "+a1.columns()+" found but expected and order-"+order+" array"));
    }

    public static void assertSize(IDoubleArray a1, int size)
    {
        if (size != a1.size())
            throw(new IllegalArgumentException("Array of dimension "+a1.rows()+" x "+a1.columns()+" found but expected and size-"+size+" array"));
    }
    
    public static void assertRowExists(IDoubleArray a1, int row)
    {
        if (row < 0 && row >= a1.rows())
            throw(new IllegalArgumentException("Trying to access row: "+row+" in array that has "+a1.rows()+" rows."));
    }
    
    public static void assertColumnExists(IDoubleArray a1, int col)
    {
        if (col < 0 && col >= a1.columns())
            throw(new IllegalArgumentException("Trying to access row: "+col+" in array that has "+a1.columns()+" rows."));
    }
    
    public static void assertIndexExists(IDoubleArray a1, int row, int col)
    {
        assertRowExists(a1, row);
        assertColumnExists(a1, row);
    }
    
}
