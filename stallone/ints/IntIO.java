/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ints;

import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import java.io.IOException;
import java.util.StringTokenizer;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class IntIO
{
    
    // ************************************************************************
    //
    // Double Array Output
    //
    // ************************************************************************
    
    public static void print(IIntArray arr, String del, Appendable app)
    {
        print(arr, "", del, app);
    }
    
    public static void print(IIntArray arr, String del,
            int predig, int postdig, Appendable app)
    {
        print(arr, "", del, predig, postdig, app);
    }
        
    public static String toString(IIntArray arr, String del)
    {
        StringBuilder strb = new StringBuilder();
        print(arr, ", ", strb);
        return(strb.toString());
    }

    public static void print(IIntArray arr, String del)
    {
        print(arr, del, System.out);
    }
    
    
    public static String toString(IIntArray arr, String del,
            int predig, int postdig)
    {
        StringBuilder strb = new StringBuilder();
        print(arr, del, predig, postdig, strb);
        return(strb.toString());
    }
    
    public static void print(IIntArray arr, String del, int predig, int postdig)
    {
        print(arr, del, predig, postdig, System.out);
    }    
    
    // ************************************************************************
    //
    // Double Array Input
    //
    // ************************************************************************

    
    public static IIntArray readDoubleArray(String str, String delimiters)
    {
        StringTokenizer tok = new StringTokenizer(str, delimiters);
        int n = tok.countTokens();
        IIntArray res = Ints.create.array(n);
        int k = 0;
        while(tok.hasMoreTokens())
            res.set(k++, StringTools.toInt(tok.nextToken()));
        return(res);
    }
    
    public static IIntArray readDoubleArray(String str)
    {
        return(readDoubleArray(str," ,;\t\n"));
    }    
    
    // ************************************************************************
    //
    // Double Table
    //
    // ************************************************************************

    public static void print(IIntArray arr, String coldel, String linedel, Appendable app)
    {
        try
        {
        for (int i = 0; i < arr.rows(); i++)
        {
            for (int j = 0; j < arr.columns(); j++)
            {
                app.append(String.valueOf(arr.get(i,j)));
                app.append(coldel);
            }
            app.append(linedel);
        }
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
    
    public static void print(IIntArray arr, String coldel, String linedel,
            int predig, int postdig, Appendable app)
    {        
        try
        {
        for (int i = 0; i < arr.rows(); i++)
        {
            for (int j = 0; j < arr.columns(); j++)
            {
                app.append(StringTools.toPrecision(arr.get(i,j), predig, postdig));
                app.append(coldel);
            }
            app.append(linedel);
        }
        }
        catch (IOException e)
        {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
        
    public static String toString(IIntArray arr)
    {
        if (arr.order() <= 1)
            return(toString(arr, ", "));
        if (arr.order() == 2)
            return (toString(arr, ", ", "\n"));

        throw(new RuntimeException("Trying to print array with order "+arr.order()+". Currently not implemented"));
    }
    
    public static void print(IIntArray arr, Appendable out)
    {
        if (arr.order() <= 1)
            print(arr, ", ", out);
        if (arr.order() == 2)
            print(arr, ", ", "\n", out);

        throw(new RuntimeException("Trying to print array with order "+arr.order()+". Currently not implemented"));
    }

    public static void print(IIntArray arr)
    {
        print(arr, System.out);
    }

    public static String toString(IIntArray arr, String coldel, String linedel)
    {
        StringBuilder strbuf = new StringBuilder();
        print(arr, coldel, linedel, strbuf);
        return (strbuf.toString());
    }
    
    public static void print(IIntArray arr, String coldel, String linedel)
    {
        print(arr, coldel, linedel, System.out);
    }
    
    public static String toString(IIntArray arr, String coldel, String linedel,
            int predig, int postdig)
    {
        StringBuilder strbuf = new StringBuilder("");
        print(arr, coldel, linedel, predig, postdig, strbuf);
        return (strbuf.toString());
    }    
    
    public static void print(IIntArray arr, String coldel, String linedel, int predig, int postdig)
    {
        print(arr, coldel, linedel, predig, postdig, System.out);
    }    
    
    // ************************************************************************
    //
    // Double Table Input
    //
    // ************************************************************************

    /*public static IDataList<IDoubleArray> readDoubleArrays(String str, String colDelimiters, String lineDelimiters)
    {
        StringTokenizer tok = new StringTokenizer(str, lineDelimiters);
        int n = tok.countTokens();
        IDataList<IDoubleArray> res = new DataList<IDoubleArray>(n);
        for (int i=0; i<n; i++)
            res.set(i, readDoubleArray(str, colDelimiters));
        return(res);
    }
    
    public static IDoubleTable readDoubleTable(String str, String colDelimiters, String lineDelimiters)
    {
        IDataList<IDoubleArray> arrays = readDoubleArrays(str, colDelimiters, lineDelimiters);
        int col = 0;
        
        for (int i=0; i<arrays.size(); i++)
            if (arrays.get(i).size() > col)
                col = arrays.get(i).size();

        return(Doubles.data.createMatrix(arrays));
    }
    
    public static IDoubleTable readDoubleTable(String str)
    {
        return(readDoubleTable(str," ,;\t", "\n"));
    }    */    
}
