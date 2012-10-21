/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.StringTokenizer;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.DoublesPrimitive;
import stallone.api.doubles.IDoubleArray;
import stallone.io.BlockFileReader;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class DoubleIO
{
    
    // ************************************************************************
    //
    // Double Array Output
    //
    // ************************************************************************
    
    public static void print(IDoubleArray arr, String del, Appendable app)
    {
        print(arr, del, del, app);
    }
    
    public static void print(IDoubleArray arr, String del,
            int predig, int postdig, Appendable app)
    {
        print(arr, del, del, predig, postdig, app);
    }
        
    public static String toString(IDoubleArray arr, String del)
    {
        StringBuilder strb = new StringBuilder();
        print(arr, "\t", strb);
        return(strb.toString());
    }

    public static void print(IDoubleArray arr, String del)
    {
        print(arr, del, System.out);
    }
    
    
    public static String toString(IDoubleArray arr, String del,
            int predig, int postdig)
    {
        StringBuilder strb = new StringBuilder();
        print(arr, del, predig, postdig, strb);
        return(strb.toString());
    }
    
    public static void print(IDoubleArray arr, String del, int predig, int postdig)
    {
        print(arr, del, predig, postdig, System.out);
    }    
    
    // ************************************************************************
    //
    // Double Array Input
    //
    // ************************************************************************

    
    public static IDoubleArray readDoubleArray(String str, String delimiters)
    {
        StringTokenizer tok = new StringTokenizer(str, delimiters);
        int n = tok.countTokens();
        IDoubleArray res = Doubles.create.array(n);
        int k = 0;
        while(tok.hasMoreTokens())
            res.set(k++, StringTools.toDouble(tok.nextToken()));
        return(res);
    }
    
    public static IDoubleArray readDoubleArray(String str)
    {
        return(readDoubleArray(str," ,;\t\n"));
    }    
    
    // ************************************************************************
    //
    // Double Table
    //
    // ************************************************************************

    public static void print(IDoubleArray arr, String coldel, String linedel, Appendable app)
    {
        try
        {
            for (int i = 0; i < arr.rows(); i++)
            {
                for (int j = 0; j < arr.columns(); j++)
                {
                    app.append(String.valueOf(arr.get(i,j)));
                    if (j < arr.columns()-1)
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
    
    public static void print(IDoubleArray arr, String coldel, String linedel,
            int predig, int postdig, Appendable app)
    {        
        try
        {
            for (int i = 0; i < arr.rows(); i++)
            {
                for (int j = 0; j < arr.columns(); j++)
                {
                    app.append(StringTools.toPrecision(arr.get(i,j), predig, postdig));
                    if (j < arr.columns()-1)
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
        
    public static String toString(IDoubleArray arr)
    {
        if (arr.order() <= 1 && arr.columns() == 1)
            return(toString(arr, "\n"));
        else if (arr.order() <= 1 && arr.rows() == 1)
            return(toString(arr, "\t"));
        else if (arr.order() == 2)
            return (toString(arr, "\t", "\n"));
        else 
            throw(new RuntimeException("Trying to print array with order "+arr.order()+". Currently not implemented"));
    }
    
    public static void print(IDoubleArray arr, Appendable out)
    {
        if (arr.order() <= 1 && arr.columns() == 1)
            print(arr, "\n", out);
        else if (arr.order() <= 1 && arr.rows() == 1)
            print(arr, "\t", out);
        else if (arr.order() == 2)
            print(arr, "\t", "\n", out);
        else
            throw(new RuntimeException("Trying to print array with order "+arr.order()+". Currently not implemented"));
    }

    public static void print(IDoubleArray arr)
    {
        print(arr, System.out);
    }

    public static String toString(IDoubleArray arr, String coldel, String linedel)
    {
        StringBuilder strbuf = new StringBuilder();
        print(arr, coldel, linedel, strbuf);
        return (strbuf.toString());
    }
    
    public static void print(IDoubleArray arr, String coldel, String linedel)
    {
        print(arr, coldel, linedel, System.out);
    }
    
    public static String toString(IDoubleArray arr, String coldel, String linedel,
            int predig, int postdig)
    {
        StringBuilder strbuf = new StringBuilder("");
        print(arr, coldel, linedel, predig, postdig, strbuf);
        return (strbuf.toString());
    }    
    
    public static void print(IDoubleArray arr, String coldel, String linedel, int predig, int postdig)
    {
        print(arr, coldel, linedel, predig, postdig, System.out);
    }    
    
    // ************************************************************************
    //
    // Double Table Input
    //
    // ************************************************************************

    private static boolean isSparseFormat(BlockFileReader reader)
    {
        // find out whether the matrix is saved in sparse or dense format.
        boolean sparse = false;
        // is there a header?
        String firstWord = reader.getWord(0,0);
        if (firstWord.equalsIgnoreCase("SPARSE") || firstWord.equalsIgnoreCase("DENSE"))
            sparse = firstWord.equalsIgnoreCase("SPARSE");
        else // no, then guess if this is sparse or not.
        {
            int[] libd = reader.getLargestIntBlockDimensions();
            int[] lnbd = reader.getLargestNumberBlockDimensions();
            
            if (libd[0] == lnbd[0] && libd[1] == 2 && lnbd[1] == 3) // guess this is sparse
                sparse = true;
        }
        
        return sparse;
    }
    
    public static IDoubleArray readDoubleMatrix(String file)
            throws FileNotFoundException, IOException
    {
        BlockFileReader reader = new BlockFileReader(file);
        reader.scan();
        
        // find out whether the matrix is saved in sparse or dense format.
        boolean sparse = isSparseFormat(reader);
        
        double[][] block = reader.getLargestDoubleBlock();
        
        IDoubleArray res = null;
        if (sparse)
        {
            int nrows = 0, ncols = 0;
            for (int i=0; i<block.length; i++)
            {
                if (block[i][0] > nrows)
                    nrows = (int)block[i][0];
                if (block[i][1] > ncols)
                    ncols = (int)block[i][1];
            }
            res = Doubles.create.matrix(nrows+1, ncols+1);
            for (int i=0; i<block.length; i++)
            {
                res.set((int)block[i][0], (int)block[i][1], block[i][2]);
            }
        }
        else
        {
            res = Doubles.create.array(block);
        }
        
        return res;
    }
    
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
