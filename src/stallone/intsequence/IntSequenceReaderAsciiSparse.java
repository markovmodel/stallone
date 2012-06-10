/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.intsequence;

import java.io.IOException;
import stallone.api.strings.Strings;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.api.ints.Ints;
import stallone.api.intsequence.IIntReader;
import stallone.io.CachedAsciiFileReader;






/**
 *
 * @author noe
 */
public class IntSequenceReaderAsciiSparse extends CachedAsciiFileReader
    implements IIntReader
{    
    private boolean scanned = false;
    
    //private boolean uniformDimension = false;
    private int dimension = 0;
    private int line = 0;
    
    private IIntList times = Ints.create.list(0);
    private IIntList data = Ints.create.list(0);

    public IntSequenceReaderAsciiSparse()
    {
    }
    
    public IntSequenceReaderAsciiSparse(String _file)
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
            times.clear();
            data.clear();
            super.scan();
            scanned = true;
        }
    }
        
    @Override
    //TODO: should check whether all lines are consistent
    protected boolean scanLine(String textline, int currentLineNumber)
    {
        String[] words = Strings.util.split(textline);
        
        double dtime = Strings.util.toDouble(words[0]);
        if ((int)dtime - dtime != 0)
        {
            throw new NumberFormatException("time "+dtime+" is no integer. Cannot be processed by this reader");
        }
        int val = Strings.util.toInt(words[1]);
        
        times.append((int)dtime);
        data.append(val);
        
        return (words.length == 2);
    }
        
    @Override
    public int size()
    {
        int firstTime = times.get(0);
        int lastTime = times.get(times.size()-1);
        return (lastTime-firstTime+1);
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
        // set relative to start time
        i -= times.get(0);
        
        int j = Ints.util.locateSorted(times, i);
        int indexAt = times.get(j);
        
        if (i == indexAt)
            return data.get(j);
        else
            return data.get(j-1);
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
        //System.out.println("loading "+filename+" size = "+size()+" firstTime = "+times.get(0));
        IIntArray res = Ints.create.array(size());
        
        int firstTime = times.get(0);
        int currentTime = times.get(0);
        int lastData = data.get(0);
        res.set(currentTime-firstTime, lastData);

        for (int i=1; i<times.size(); i++)
        {
            while(currentTime < times.get(i))
            {
                //System.out.println(i+" "+currentTime+"\t"+firstTime+"\t"+(currentTime-firstTime)+"\t"+times.get(i));
                res.set(currentTime-firstTime, lastData);
                currentTime++;
            }
            if (currentTime == times.get(i))
            {
                lastData = data.get(i);
                res.set(currentTime-firstTime, lastData);
                currentTime++;
            }
        }
        return res;
    }
          
    
    public static void main(String[] args) throws IOException
    {
        IntSequenceReaderAsciiSparse reader = new IntSequenceReaderAsciiSparse("/Users/noe/data/my_papers/fret_nienhaus/DAse_multiple_Mg/DAse_ribozyme_0mM/tmp/hmm-4/r47_oxy_0011.path");
        reader.scan();
        
        for (int i=0; i<reader.size(); i++)
        {
            System.out.println(i+" "+reader.get(i));
        }
        /*IIntArray res = reader.load();
        for (int i=0; i<res.size(); i++)
        {
            System.out.println(i+" "+res.get(i));
        }*/
    }
}
