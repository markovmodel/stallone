/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence.io;

import stallone.api.datasequence.IDataList;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.DataSequence;
import stallone.api.datasequence.IDataReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import stallone.api.doubles.*;
import stallone.datasequence.DataSequenceLoaderIterator;
import stallone.io.CachedAsciiFileReader;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class AsciiDataSequenceReader
        extends CachedAsciiFileReader
        implements IDataReader
{

    private int noOfElements = 0;
    private int dimension = 0;
    private boolean noElementsDetermined = false;
    private int dataStartLine = 0;
    /**
     * The column which stores time information, by default this is column 1. If
     * timeColumn is set to -1, that means, there is no time information.
     */
    private int timeColumn = -1;
    /**
     * The columns, which contain data
     */
    private int[] selectedColumns = null;

    public AsciiDataSequenceReader(String filename)
            throws FileNotFoundException, IOException
    {
        super(filename);
    }

    /**
     *
     * @param referencedReader
     * @param dataStartLine
     * @param dataEndLine
     * @param timeColumn
     * @param selectedColumns
     */
    public AsciiDataSequenceReader(String filename, int _dataStartLine, int _timeColumn, int[] _selectedColumns)
            throws FileNotFoundException, IOException
    {
        super(filename);

        this.dataStartLine = _dataStartLine;
        //this.dataEndLine = dataEndLine;

        /*
         * if ( dataEndLine <= dataStartLine ) { throw new
         * IllegalArgumentException("Invalid start position of trajectory."); }
         */

        this.timeColumn = _timeColumn;
        this.selectedColumns = _selectedColumns;
    }

    @Override
    protected boolean scanLine(String textline, int currentLineNumber)
    {
        if (selectedColumns == null)
        {
            try
            {
                double[] dline = StringTools.toDoubleArray(textline);
                dimension = dline.length;
                return true;
            }
            catch(Exception e)
            {return false;}
        }
        else
        {
            String[] words = StringTools.split(textline);
            words = StringTools.subarray(words, selectedColumns);
            for (int i = 0; i < words.length; i++)
            {
                if (!StringTools.isDouble(words[i]))
                {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     *
     * @param lineNumber
     * @return
     */
    private String[] readTokens(int lineNumber)
    {
        if ((lineNumber >= 0) && (lineNumber < size()))
        {
            String currentLine = super.getLine(dataStartLine + lineNumber);
            String[] elements = CachedAsciiFileReader.whiteSpacePattern.split(currentLine.trim());
            return elements;
        }
        else
        {
            throw new IllegalArgumentException("Invalid line " + lineNumber + " requested.");
        } // end if-else
    }

    @Override
    public void setSource(String name)
    {
        super.setFilename(name);
    }

    @Override
    public int size()
    {
        return getNumberOfLines();
    }

    @Override
    public int dimension()
    {
        if (selectedColumns != null)
            return selectedColumns.length;
        else
            return dimension;
    }

    @Override
    public long memorySize()
    {
        return (8 * size() * dimension());
    }

    /**
     *
     * @param frameIndex
     * @return
     */
    @Override
    public double getTime(int frameIndex)
    {
        if (timeColumn == -1)
        {
            return frameIndex;
        }

        String[] entries = readTokens(frameIndex);

        double value = 0;
        try
        {
            value = Double.parseDouble(entries[timeColumn]);
        } catch (NumberFormatException nfe)
        {
            System.out.println("frameIndex : " + frameIndex);
            //System.out.println(dataStartLine + " " + dataEndLine);
        }
        return value;
    }

    @Override
    public IDoubleArray get(int index)
    {
        return get(index, null);
    }

    @Override
    public IDoubleArray getView(int index)
    {
        return (get(index));
    }

    /**
     *
     * @param frameIndex
     * @param factory
     * @return
     */
    public IDoubleArray get(int frameIndex, IDoubleArray target)
    {
        String[] entries = readTokens(frameIndex);
        if (target == null)
        {
            if (selectedColumns == null)
            {
                target = Doubles.create.array(entries.length);
            }
            else
            {
                target = Doubles.create.array(selectedColumns.length);
            }
        }

        try
        {
            if (selectedColumns == null)
            {
                for (int i = 0; i < entries.length; i++)
                {
                    double value = Double.parseDouble(entries[i]);
                    target.set(i, value);
                }

            }
            else
            {
                // creation should be moved outside
                int n = selectedColumns.length;

                for (int i = 0; i < n; i++)
                {
                    int pos = selectedColumns[i];
                    double value = Double.parseDouble(entries[pos]);
                    target.set(i, value);
                }
            }
        } catch (NumberFormatException nfe)
        {
            System.out.println("frameIndex : " + frameIndex);
            //System.out.println(dataStartLine + " " + dataEndLine);
        }

        return target;
    }

    @Override
    public IDataSequence load()
    {
        IDataList res = DataSequence.create.createDatalist();
        for (Iterator<IDoubleArray> it = iterator(); it.hasNext();)
        {
            res.add(it.next());
        }

        return res;
    }

    @Override
    public Iterator<IDoubleArray> iterator()
    {
        return new DataSequenceLoaderIterator(this);
    }
}
