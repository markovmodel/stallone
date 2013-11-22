/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.util;

import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import java.io.*;
import java.util.*;
import java.text.*;


public class StringTools
{
    /**
       converts a list of strings into a string array.
     */
    public static String[] List2Array(List<?> al)
    {
	int size = al.size();
	String[] res = new String[size];
	int k=0;
	Iterator<?> i = al.iterator();
	while (i.hasNext())
	    res[k++] = ((String)i.next());
	return(res);
    }


    /**
       Returns a String containing the content of the given buffer. The stream
       is supposed to be open, otherwise an IOException will be thrown.
       The stream is closed after the read operation.
       @throws IOException if the stream is not open or any other
       IOException occurs
    */
    public static String readStream(InputStream in)
        throws IOException
    {
        StringBuffer strbuf = new StringBuffer();
        int data = 0;
        while (data != -1)
            {
                data = in.read();
                if (data != -1)
                    strbuf.append((char)data);
            }
        in.close();
        return strbuf.toString();
    }

    public static int[] getIntColumn(String str, int col)
    {
        String[] lines = getLines(str);
        int[] integers = new int[lines.length];

        for (int i=0; i<lines.length; i++)
            {
                StringTokenizer tok = new StringTokenizer(lines[i]);
                for (int j=0; j<col; j++)
                    tok.nextToken();
                integers[i] = Integer.valueOf(tok.nextToken()).intValue();
            }

        return(integers);
    }

    /*
      Returns an array with strings which are a certain column in a given
      string. Column count starts with 0.
     */
    public static String[] getStringColumn(String str, int col)
    {
        String[] lines = getLines(str);
        String[] strings = new String[lines.length];

        for (int i=0; i<lines.length; i++)
            {
                StringTokenizer tok = new StringTokenizer(lines[i]);
                for (int j=0; j<col; j++)
                    tok.nextToken();
                strings[i] = tok.nextToken();
            }

        return(strings);
    }

    /*
      Returns an array with strings which are sequences of words from a given
      column on. Column count starts with 0.
     */
    public static String[] getStringMultiColumn(String str, int colFrom)
    {
        String[] lines = getLines(str);
        String[] strings = new String[lines.length];

        for (int i=0; i<lines.length; i++)
            {
                StringTokenizer tok = new StringTokenizer(lines[i]);
                StringBuffer strbuf = new StringBuffer();
                for (int j=0; j<colFrom; j++)
                    tok.nextToken();
                while(tok.hasMoreTokens())
                    {
                        strbuf.append(tok.nextToken());
                        if (tok.hasMoreTokens())
                            strbuf.append(" ");
                    }
                strings[i] = strbuf.toString();
            }

        return(strings);
    }

    public static String[] getLines(String str)
    {
		StringTokenizer tok = new StringTokenizer(str, "\n");
		String[] arr = new String[tok.countTokens()];
		for (int i=0; i<arr.length; i++)
			arr[i] = tok.nextToken();

		return(arr);
    }

    public static String[] split(String str)
    {
	StringTokenizer tok = new StringTokenizer(str);
	String[] arr = new String[tok.countTokens()];
	for (int i=0; i<arr.length; i++)
	    arr[i] = tok.nextToken();

	return(arr);
    }

    /**
       Splits each line in the given array and returns the result as matrix
     */
    public static String[][] split(String[] lines)
    {
	String[][] res = new String[lines.length][];
	for (int i=0; i<res.length; i++)
	    {
		res[i] = StringTools.split(lines[i]);
	    }
	return(res);
    }

    public static String mergeLines(String[] lines)
    {
	StringBuffer strbuf = new StringBuffer();
	for (int i=0; i<lines.length; i++)
	    strbuf.append(lines[i]+"\n");
	return(strbuf.toString());
    }

    /**
       Drops all Strings which contain nothing or only whitespaces and returns
       the remaining Strings.
     */
    public static String[] purgeEmpty(String[] arr)
    {
	IIntArray drop = Ints.create.array(0);
	for (int i=0; i<arr.length; i++)
	    if (StringTools.split(arr[i]).length == 0)
		drop = Ints.util.mergeToNew(drop, i);
	IIntArray include = Ints.util.removeValueToNew
	    (Ints.create.arrayRange(arr.length), drop);
	return(StringTools.subarray(arr, include));
    }


    public static String replaceAll(String str, String pattern, String value)
    {
	String res = str;
	int i = res.indexOf(pattern);
	while (i>-1)
	    {
		res = res.substring(0,i)+value
		    +res.substring(i+pattern.length(),res.length());
		i = res.indexOf(pattern);
	    }
	return(res);
    }

    public static boolean isInt(String str)
    {
	try{Integer.valueOf(str); return(true);}
	catch(NumberFormatException e){return(false);}
    }


    public static int leadingInt(String str)
    {
	int k=0;
	for (;k<str.length(); k++)
	    if (!Character.isDigit(str.charAt(k)))
		break;
	if (k == 0)
	    return(-1);
	return(toInt(str.substring(0, k)));
    }


    public static boolean startsWithInt(String str)
    {
	String[] words = StringTools.split(str);
	if (words.length == 0) return(false);
	if (StringTools.isInt(words[0])) return(true);
	return(false);
    }


    /**
       @return the next sequence of numbers that can be converted to an
       integer. Does not recognize parts of doubles or negative ints.
       Returns -1, if no next integer is available.
     */
    public static int nextInt(String str, int s)
    {
	int k=s;
	for (;k<str.length() && !Character.isDigit(str.charAt(k)); k++);
	int i1 = k;
	for (;k<str.length() && Character.isDigit(str.charAt(k)); k++);
	int i2 = k;
	if (i2-i1 < 1)
	    return(-1);
	return(toInt(str.substring(i1, i2)));
    }

    /**
       @return the next sequence of numbers that can be converted to an
       integer. Does not recognize parts of doubles or negative ints.
       Returns -1, if no next integer is available.
     */
    public static long nextLong(String str, int s)
    {
	int k=s;
	for (;k<str.length() && !Character.isDigit(str.charAt(k)); k++);
	int i1 = k;
	for (;k<str.length() && Character.isDigit(str.charAt(k)); k++);
	int i2 = k;
	if (i2-i1 < 1)
	    return(-1);
	return(toLong(str.substring(i1, i2)));
    }


    public static int toInt(String str)
    {
	return(Integer.valueOf(str).intValue());
    }

    public static long toLong(String str)
    {
	return(Long.valueOf(str).longValue());
    }

    public static String[][] transpose(String[][] arr)
    {
	int nCol = arr.length;
	int nLin = arr[0].length;
	for (int i=0; i<arr.length; i++)
	    if (arr[i].length != nLin)
		{
		    System.out.println(arr[i].length);
		throw(new IllegalArgumentException("Trying to transpose a non-matrix-array"));
		}

	String[][] res = new String[nLin][nCol];
	for (int i=0; i<res.length; i++)
	    for (int j=0; j<res[i].length; j++)
		res[i][j] = arr[j][i];
	return(res);
    }

    public static IIntArray toIntArray(String[] str)
    {
	IIntArray res = Ints.create.array(str.length);
	for (int i=0; i<str.length; i++)
	    res.set(i, toInt(str[i]));
	return(res);
    }

    public static IIntArray toIntMatrix(String[][] str)
    {
	IIntArray res = Ints.create.table(str.length,str[0].length);
	int r = 0;
	for (int i=0; i<str.length; i++)
	    {
		if (str[i].length > 0)
		    r++;
		for (int j=0; j<str[i].length; j++)
		    res.set(i,j, toInt(str[i][j]));
	    }
	return(Ints.util.subRowsToNew(res, Ints.create.arrayRange(r)));
    }

    /**
       Tries to split the given String to an integer-Array
       (using whitespaces, commas oder semicolons as delimiters)
     */
    public static IIntArray toIntArray(String str)
    {
	String[] strarr = split(str);
	if (isInt(strarr[0]))
	    return(toIntArray(strarr));
	strarr = str.split(",");
	if (isInt(strarr[0]))
	    return(toIntArray(strarr));
	strarr = str.split(";");
	if (isInt(strarr[0]))
	    return(toIntArray(strarr));
	throw(new IllegalArgumentException("No int array: "+str));
    }

    public static boolean isIntArray(String str)
    {
	try{toIntArray(str); return(true);}
	catch(Exception e){ return(false);}
    }


    public static boolean isDouble(String str)
    {
	try{Double.valueOf(str); return(true);}
	catch(NumberFormatException e){return(false);}
    }

    public static double toDouble(String str)
    {
        String enString = str;
        int i = str.indexOf(",");
        if (i != -1)
            {
                StringBuffer strbuf = new StringBuffer(str);
                strbuf.setCharAt(i,'.');
                enString = strbuf.toString();
            }
	return(Double.valueOf(str).doubleValue());
    }

    public static double[] toDoubleArray(String[] str)
    {
	double[] res = new double[str.length];
	for (int i=0; i<str.length; i++)
	    res[i] = toDouble(str[i]);
	return(res);
    }

    public static double[][] toDoubleArray(String[][] str)
    {
	double[][] res = new double[str.length][];
	for (int i=0; i<str.length; i++)
	    res[i] = toDoubleArray(str[i]);
	return(res);
    }

    public static boolean isDoubleArray(String str)
    {
	try{toDoubleArray(str); return(true);}
	catch(Exception e){ return(false);}
    }

    /**
       Tries to split the given String to an integer-Array
       (using whitespaces, commas oder semicolons as delimiters)
     */
    public static double[] toDoubleArray(String str)
    {
	String[] strarr = split(str);
	if (isDouble(strarr[0]))
	    return(toDoubleArray(strarr));
	strarr = str.split(",");
	if (isDouble(strarr[0]))
	    return(toDoubleArray(strarr));
	strarr = str.split(";");
	if (isDouble(strarr[0]))
	    return(toDoubleArray(strarr));
	throw(new IllegalArgumentException("No double array: "+str));
    }

    /**
       Adds blanks to the right until size is reached
     */
    public static String flushLeft(String str, int size)
    {
	StringBuffer strbuf = new StringBuffer(str);
	for (int i=str.length(); i<size; i++)
	    strbuf.append(" ");
	return(strbuf.toString());
    }

    /**
       Adds blanks to the left until size is reached
     */
    public static String flushRight(String str, int size)
    {
	StringBuffer strbuf = new StringBuffer();
	for (int i=str.length(); i<size; i++)
	    strbuf.append(" ");
	strbuf.append(str);
	return(strbuf.toString());
    }

	/**
	   Transforms a list of strings into a String-array
	 */
	public static String[] toStringArray(LinkedList<?> list)
	{
		String[] res = new String[list.size()];

		int k=0;
		Iterator<?> i=list.iterator();
		while(i.hasNext())
			res[k++] = (String)i.next();

		return(res);
	}


    /**
       Sorts the given array of Strings according to the numeric value
       of the first integer in each String.
       This is done by bubblesort (slow!)
     */
    public static String[] sortByFirstNumber(String[] arr)
    {
	for (int i=0; i<arr.length; i++)
	    for (int j=0; j<arr.length-1; j++)
		{
		    long i1 = nextLong(arr[j], 0);
		    if (i1 < 0) i1 = Integer.MAX_VALUE;
		    long i2 = nextLong(arr[j+1], 0);
		    if (i1 < 0) i2 = Integer.MAX_VALUE;

		    if (i1 > i2)
			{
			    String h = arr[j];
			    arr[j] = arr[j+1];
			    arr[j+1] = h;
			}
		}

	return(arr);
    }


    public static String[] concat(String[] a1, String[] a2)
    {
	String[] res = new String[a1.length+a2.length];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	for (int i=0; i<a2.length; i++)
	    res[j++] = a2[i];
	return(res);
    }

    public static String[][] concat(String[][] a1, String[][] a2)
    {
	String[][] res = new String[a1.length+a2.length][];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	for (int i=0; i<a2.length; i++)
	    res[j++] = a2[i];
	return(res);
    }

    public static String[] concat(String[] a1, String a2)
    {
	String[] res = new String[a1.length+1];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	res[j] = a2;
	return(res);
    }

    public static String[][] concat(String[][] a1, String[] a2)
    {
		String[][] res = new String[a1.length+1][];
		int j=0;
		for (int i=0; i<a1.length; i++)
			res[j++] = a1[i];
		res[j] = a2;
		return(res);
    }

    public static boolean contains(String[] arr, String pattern)
    {
	for (int i=0; i<arr.length; i++)
	    if (arr[i].equals(pattern))
		return(true);
	return(false);
    }

    public static int findForward(String[] arr, String pattern, int iStart)
    {
	for (int i=iStart; i<arr.length; i++)
	    if (arr[i].equals(pattern))
		return(i);
	return(-1);
    }

    public static int findForward(String[] arr, String pattern)
    { return(findForward(arr,pattern,0));}

    public static int findBackward(String[] arr, String pattern, int iStart)
    {
	for (int i=iStart; i>=0; i--)
	    if (arr[i].equals(pattern))
		return(i);
	return(-1);
    }

    public static int findBackward(String[] arr, String pattern)
    { return(findForward(arr,pattern,arr.length-1));}

    public static String[] copy(String[] arr)
    {
        String[] res = new String[arr.length];
        for (int i=0; i<res.length; i++)
            res[i] = arr[i];

        return(res);
    }

    public static String[] subarray
	(String[] arr, int i1, int i2)
    {
	String[] res = new String[i2-i1];
	for (int i=i1; i<i2; i++)
	    res[i-i1] = arr[i];
	return(res);
    }

    public static String[] subarray(String[] arr, int[] indexes)
    {
	String[] res = new String[indexes.length];
	for (int i=0, k=0; i<indexes.length; i++)
	    res[k++] = arr[indexes[i]];
	return(res);
    }

    public static String[] subarray(String[] arr, IIntArray indexes)
    {
        return subarray(arr, indexes.getArray());
    }

    public static String[][] subarray
	(String[][] arr, int i1, int i2)
    {
	String[][] res = new String[i2-i1][];
	for (int i=i1; i<i2; i++)
	    res[i-i1] = arr[i];
	return(res);
    }

    public static String[][] subarray(String[][] arr, IIntArray indexes)
    {
	String[][] res = new String[indexes.size()][];
	for (int i=0, k=0; i<indexes.size(); i++)
	    res[k++] = arr[indexes.get(i)];
	return(res);
    }

    /**
       Drops the selected lines
    */
    public static String[] drop(String[] arr, IIntArray droplist)
    {
	IIntArray include = Ints.util.removeValueToNew
	    (Ints.create.arrayRange(arr.length), droplist);
	return(subarray(arr, include));
    }

    /**
       Drop all entries starting with the given pattern
    */
    public static String[] dropLeading(String[] arr, String pattern)
    {
	IIntArray droplist = Ints.create.array(0);
	for (int i=0; i<arr.length; i++)
	    if (arr[i].startsWith(pattern))
		droplist = Ints.util.mergeToNew(droplist, i);
	return(drop(arr, droplist));
    }

    public static String[] getColumn(String[][] arr, int k)
    {
	String[] res = new String[arr.length];
	for (int i=0; i<arr.length; i++)
	    res[i] = arr[i][k];
	return(res);
    }


    /**
       flattens the given 2-dimensional String array into a one-dimensional
       String array
     */
    public static String[] flatten(String[][] arr)
    {
	int n = 0;
	for (int i=0; i<arr.length; i++)
	    n += arr[i].length;

	String[] res = new String[n];
	for (int i=0, k=0; i<arr.length; i++)
	    for (int j=0; j<arr[i].length; j++)
		res[k++] = arr[i][j];
	return(res);
    }


    /**
       flattens the given 3-dimensional String array into a two-dimensional
       String array
     */
    public static String[][] flatten(String[][][] arr)
    {
	int n = 0;
	for (int i=0; i<arr.length; i++)
	    n += arr[i].length;

	String[][] res = new String[n][];
	for (int i=0, k=0; i<arr.length; i++)
	    for (int j=0; j<arr[i].length; j++)
		res[k++] = arr[i][j];
	return(res);
    }

    public static String toString(String[] arr, String del)
    {
	if (arr == null)
	    return("null");
	if (arr.length == 0)
	    return("");

	StringBuffer strbuf = new StringBuffer(arr[0]);
	for (int i=1; i<arr.length; i++)
	    strbuf.append(del+arr[i]);
	return(strbuf.toString());
    }

    public static String toString(String[] arr)
    {return(toString(arr,","));}

    public static void print(String[] str)
    {System.out.print(toString(str));}

    public static void print(String[] str, String del)
    {System.out.print(toString(str, del));}

    public static void print(String[][] str, String coldel, String linedel)
    {
	for (int i=0; i<str.length; i++)
	    {
		print(str[i], coldel);
		System.out.print(linedel);
	    }
    }

    public static void print(String[][] str)
    {print(str, ",", "\n");}

    public static String toScientific(double I, int digits)
    {
	StringBuffer strbuf = new StringBuffer("0.");
	for (int i=0; i<digits; i++)
	    strbuf.append("#");
	strbuf.append("E0");
	DecimalFormat df = new DecimalFormat(strbuf.toString());
	return(df.format(I));
    }

    public static String toPrecision(double I, int digits1, int digits2)
    {
	if (digits2 == 0)
	    return(String.valueOf((int)Math.round(I)));

	StringBuffer strbuf = new StringBuffer("");
	for (int i=0; i<digits1-1; i++)
	    strbuf.append("#");
	strbuf.append("0.");
	for (int i=0; i<digits2; i++)
	    strbuf.append("#");
	DecimalFormat df = new DecimalFormat(strbuf.toString(), new DecimalFormatSymbols(Locale.ENGLISH));
	return(df.format(I));
    }
}
