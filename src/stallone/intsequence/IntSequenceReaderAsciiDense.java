/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.intsequence;

import java.io.IOException;
import stallone.api.strings.Strings;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.api.intsequence.IIntReader;
import stallone.io.CachedAsciiFileReader;






/**
 *
 * @author noe
 */
public class IntSequenceReaderAsciiDense extends CachedAsciiFileReader
    implements IIntReader
{    
    private boolean scanned = false;
    
    //private boolean uniformDimension = false;
    private int dimension = 0;
    private int line = 0;

    public IntSequenceReaderAsciiDense()
    {
    }
    
    public IntSequenceReaderAsciiDense(String _file)
            throws IOException
    {
        super(_file);
        this.setSource(_file);
        this.open();
    }

    @Override
    public final void setSource(String _file)
    {
        if (!_file.equals(filename))
        {
            super.filename = _file;
            this.scanned = false;
        }
    }
    
    @Override
    public final void open()
            throws IOException
    {
        super.open();
        scan();
    }    
    
    @Override
    public final void scan()
            throws IOException
    {
        if (!scanned)
        {
            super.scan();
            scanned = true;
        }
    }
        
    @Override
    //TODO: should check whether all lines are consistent
    protected boolean scanLine(String textline, int currentLineNumber)
    {
        String[] words = Strings.util.split(textline);
        return (words.length == 1);
    }
        
    @Override
    public int size()
    {
        return(super.getNumberOfLines());
    }


    @Override
    public long memorySize()
    {
        // rough estimate
        return(size()*4);
    }    

    @Override
    public int get(int i)
    {
        String strline = super.getLine(i);
    
        int res = 0;
        try
        {
            res = Strings.util.toInt(strline);
        }
        catch(NumberFormatException e)
        {
            throw(new IllegalArgumentException("Line "+i+" of file "+filename+" is not an integer:\n "+strline));
        }
        return res;
    }
    
    @Override
    public void close()
            throws IOException
    {
        super.close();
    }

    @Override
    public IIntArray load()
    {
        int size = size();
        IIntArray res = Ints.create.array(size);
        for (int i=0; i<size; i++)
            res.set(i, get(i));
        return res;
    }
          
}
