/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.strings;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import stallone.ints.PrimitiveIntTools;

/**
 *
 * @author noe
 */
public class StringUtilities
{
    public List<String> toList(String[] arr)
    {
        List<String> list = new ArrayList<String>();
        for (String s : arr)
            list.add(s);
        return list;
    }

    public String[] toArray(List<String> list)
    {
        String[] arr = new String[list.size()];
        for (int i=0; i<arr.length; i++)
            arr[i] = list.get(i);
        return arr;
    }

    public String[] getLines(String str)
    {
		StringTokenizer tok = new StringTokenizer(str, "\n");
		String[] arr = new String[tok.countTokens()];
		for (int i=0; i<arr.length; i++)
			arr[i] = tok.nextToken();

		return(arr);
    }

    public String[] split(String str)
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
    public String[][] split(String[] lines)
    {
	String[][] res = new String[lines.length][];
	for (int i=0; i<res.length; i++)
	    {
		res[i] = split(lines[i]);
	    }
	return(res);
    }

    public String mergeLines(String[] lines)
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
    public String[] purgeEmpty(String[] arr)
    {
	int[] drop = new int[0];
	for (int i=0; i<arr.length; i++)
	    if (split(arr[i]).length == 0)
		drop = PrimitiveIntTools.concat(drop, i);
	int[] include = PrimitiveIntTools.removeByValue
	    (PrimitiveIntTools.range(arr.length), drop);
	return(subarray(arr, include));
    }


    public String replaceAll(String str, String pattern, String value)
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

    public boolean isInt(String str)
    {
	try{Integer.valueOf(str); return(true);}
	catch(NumberFormatException e){return(false);}
    }


    public int leadingInt(String str)
    {
	int k=0;
	for (;k<str.length(); k++)
	    if (!Character.isDigit(str.charAt(k)))
		break;
	if (k == 0)
	    return(-1);
	return(toInt(str.substring(0, k)));
    }


    public boolean startsWithInt(String str)
    {
	String[] words = split(str);
	if (words.length == 0) return(false);
	if (isInt(words[0])) return(true);
	return(false);
    }


    /**
       @return the next sequence of numbers that can be converted to an
       integer. Does not recognize parts of doubles or negative ints.
       Returns -1, if no next integer is available.
     */
    public int nextInt(String str, int s)
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
    public long nextLong(String str, int s)
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


    public int toInt(String str)
    {
	return(Integer.valueOf(str).intValue());
    }

    public long toLong(String str)
    {
	return(Long.valueOf(str).longValue());
    }

    public String[][] transpose(String[][] arr)
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

    public int[] toIntArray(String[] str)
    {
	int[] res = new int[str.length];
	for (int i=0; i<str.length; i++)
	    res[i] = toInt(str[i]);
	return(res);
    }

    public int[][] toIntMatrix(String[][] str)
    {
	int[][] res = new int[str.length][str[0].length];
	int r = 0;
	for (int i=0; i<str.length; i++)
	    {
		if (str[i].length > 0)
		    r++;
		for (int j=0; j<str[i].length; j++)
		    res[i][j] = toInt(str[i][j]);
	    }
	return(PrimitiveIntTools.subarray(res, 0, r));
    }

    /**
       Tries to split the given String to an integer-Array
       (using whitespaces, commas oder semicolons as delimiters)
     */
    public int[] toIntArray(String str)
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

    public boolean isIntArray(String str)
    {
	try{toIntArray(str); return(true);}
	catch(Exception e){ return(false);}
    }


    public boolean isDouble(String str)
    {
	try{Double.valueOf(str); return(true);}
	catch(NumberFormatException e){return(false);}
    }

    public double toDouble(String str)
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

    public double[] toDoubleArray(String[] str)
    {
	double[] res = new double[str.length];
	for (int i=0; i<str.length; i++)
	    res[i] = toDouble(str[i]);
	return(res);
    }

    public double[][] toDoubleArray(String[][] str)
    {
	double[][] res = new double[str.length][];
	for (int i=0; i<str.length; i++)
	    res[i] = toDoubleArray(str[i]);
	return(res);
    }

    public boolean isDoubleArray(String str)
    {
	try{toDoubleArray(str); return(true);}
	catch(Exception e){ return(false);}
    }

    /**
       Tries to split the given String to an integer-Array
       (using whitespaces, commas oder semicolons as delimiters)
     */
    public double[] toDoubleArray(String str)
    {
        String[] words = str.split("[ ,\t]++");
        double[] res = new double[words.length];
        for (int i=0; i<words.length; i++)
        {
            try
            {
                res[i] = toDouble(words[i]);
            }
            catch(NumberFormatException e)
            {
                throw(new NumberFormatException("No double array: "+str));
            }
        }
        return(res);
    }

    public double[][] toDoubleTable(String str)
    {
        String[] lines = str.split("[;\n]");
        double[][] res = new double[lines.length][];
        for (int i=0; i<lines.length; i++)
            res[i] = toDoubleArray(lines[i]);
        return(res);
    }

    public String[] concat(String[] a1, String[] a2)
    {
	String[] res = new String[a1.length+a2.length];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	for (int i=0; i<a2.length; i++)
	    res[j++] = a2[i];
	return(res);
    }

    public String[][] concat(String[][] a1, String[][] a2)
    {
	String[][] res = new String[a1.length+a2.length][];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	for (int i=0; i<a2.length; i++)
	    res[j++] = a2[i];
	return(res);
    }

    public String[] concat(String[] a1, String a2)
    {
	String[] res = new String[a1.length+1];
	int j=0;
	for (int i=0; i<a1.length; i++)
	    res[j++] = a1[i];
	res[j] = a2;
	return(res);
    }

    public String[][] concat(String[][] a1, String[] a2)
    {
		String[][] res = new String[a1.length+1][];
		int j=0;
		for (int i=0; i<a1.length; i++)
			res[j++] = a1[i];
		res[j] = a2;
		return(res);
    }

    public boolean contains(String[] arr, String pattern)
    {
	for (int i=0; i<arr.length; i++)
	    if (arr[i].equals(pattern))
		return(true);
	return(false);
    }

    public int findForward(String[] arr, String pattern, int iStart)
    {
	for (int i=iStart; i<arr.length; i++)
	    if (arr[i].equals(pattern))
		return(i);
	return(-1);
    }

    public int findForward(String[] arr, String pattern)
    { return(findForward(arr,pattern,0));}

    public int findBackward(String[] arr, String pattern, int iStart)
    {
	for (int i=iStart; i>=0; i--)
	    if (arr[i].equals(pattern))
		return(i);
	return(-1);
    }

    public int findBackward(String[] arr, String pattern)
    { return(findForward(arr,pattern,arr.length-1));}

    public String[] copy(String[] arr)
    {
        String[] res = new String[arr.length];
        for (int i=0; i<res.length; i++)
            res[i] = arr[i];

        return(res);
    }

    public String[] subarray
	(String[] arr, int i1, int i2)
    {
	String[] res = new String[i2-i1];
	for (int i=i1; i<i2; i++)
	    res[i-i1] = arr[i];
	return(res);
    }

    public String[] subarray(String[] arr, int[] indexes)
    {
	String[] res = new String[indexes.length];
	for (int i=0, k=0; i<indexes.length; i++)
	    res[k++] = arr[indexes[i]];
	return(res);
    }

    public String[][] subarray
	(String[][] arr, int i1, int i2)
    {
	String[][] res = new String[i2-i1][];
	for (int i=i1; i<i2; i++)
	    res[i-i1] = arr[i];
	return(res);
    }

    public String[][] subarray(String[][] arr, int[] indexes)
    {
	String[][] res = new String[indexes.length][];
	for (int i=0, k=0; i<indexes.length; i++)
	    res[k++] = arr[indexes[i]];
	return(res);
    }

    /**
       Drops the selected lines
    */
    public String[] drop(String[] arr, int[] droplist)
    {
	int[] include = PrimitiveIntTools.removeByValue
	    (PrimitiveIntTools.range(arr.length), droplist);
	return(subarray(arr, include));
    }

    /**
       Drop all entries starting with the given pattern
    */
    public String[] dropLeading(String[] arr, String pattern)
    {
	int[] droplist = new int[0];
	for (int i=0; i<arr.length; i++)
	    if (arr[i].startsWith(pattern))
		droplist = PrimitiveIntTools.concat(droplist, i);
	return(drop(arr, droplist));
    }

    public String[] getColumn(String[][] arr, int k)
    {
	String[] res = new String[arr.length];
	for (int i=0; i<arr.length; i++)
	    res[i] = arr[i][k];
	return(res);
    }
}
