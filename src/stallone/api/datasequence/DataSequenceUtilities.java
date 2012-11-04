/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.datasequence.*;
import stallone.doubles.PrimitiveDoubleTable;

/**
 *
 * @author noe
 */
public class DataSequenceUtilities
{
    
    /**
     * The entire subset, as given by the index set is loaded and returned.
     * @param indexes nx2 array with trajectory and within-trajectory indexes
     * @return 
     */
    public List<IDataSequence> loadSubset(IDataSequenceLoader loader, IIntArray indexes)
            throws IOException
    {
        List<IDataSequence> res = new ArrayList();
        DataList list = null;                
        int lastTraj = -1;

        for (int i = 0; i < indexes.rows(); i++)
        {
            // read trajectory index and open trajectory if necessary
            int itraj = indexes.get(i, 0);
            if (itraj != lastTraj)
            {
                if (list != null)
                {
                    res.add(list);
                }

                list = new DataList();
                lastTraj = itraj;
            }

            int iindex = indexes.get(i, 1);
            list.add(loader.load(itraj, iindex));
        }

        res.add(list);

        return (res);
    }

    public List<IDoubleArray> readDataList(IDataSequence inp)
    {
        ArrayList<IDoubleArray> list = new ArrayList<IDoubleArray>();
        for (int i = 0; i < inp.size(); i++)
        {
            list.add(inp.get(i));
        }
        return (list);
    }
    
    public IDataSequence readDataList(String inputFile)
    {
        IDataSequence res = null;
        try
        {
            IDataReader input = DataSequence.create.createASCIIDataReader(inputFile);
            res = input.load();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(DataSequenceUtilities.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return(res);
    }

    public IDoubleArray readDataTable(IDataSequence inp)
    {
        IDoubleArray line1 = inp.get(0);
        IDoubleArray table = new PrimitiveDoubleTable(inp.size(), line1.size());
        for (int i = 0; i < line1.size(); i++)
        {
            table.set(0, i, line1.get(i));
        }

        for (int i = 1; i < inp.size(); i++)
        {
            IDoubleArray linei = inp.get(i);
            
            for (int j = 0; j < linei.size(); j++)
            {
                table.set(i, j, linei.get(j));
            }
        }

        return (table);
    }
    
    public IDoubleArray readDataTable(String inputFile)
    {
        IDoubleArray res = null;
        try
        {
            IDataReader input = DataSequence.create.createASCIIDataReader(inputFile);
            input.scan();
            res = new PrimitiveDoubleTable(input.size(), input.dimension());
            int line = 0;
            for (Iterator<IDoubleArray> it = input.iterator(); it.hasNext(); line++)
            {
                IDoubleArray arr = it.next();
                for (int i=0; i<arr.size(); i++)
                    res.set(line, i, arr.get(i));
            }
        } 
        catch (IOException ex)
        {
            Logger.getLogger(DataSequenceUtilities.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return(res);
    }
    
    public IDataSequence readDataSequence(String inputFile)
    {
        return(new DataSequenceArray(readDataTable(inputFile)));
    }
    
    public IDoubleArray readColumn(IDataSequence inp, int columnIndex)
    {
        IDoubleArray col = Doubles.create.array(inp.size());
        for (int i=0; i<inp.size(); i++)
            col.set(i, inp.get(i).get(columnIndex));
        return(col);
    }    
    
    public IDoubleArray readColumn(String inputFile, int columnIndex)
    {
        IDoubleArray res = null;

        try
        {
            IDataReader input = DataSequence.create.createASCIIDataReader(inputFile);
            input.scan();
            res = Doubles.create.denseColumn(input.size());
            for (int i=0; i<res.size(); i++)
                res.set(i, input.get(i).get(columnIndex));
        } 
        catch (IOException ex)
        {
            Logger.getLogger(DataSequenceUtilities.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return(res);
    }    
    
    public void writeData(IDataSequence data, String file)
            throws IOException
    {
        IDataWriter writer = DataSequence.create.createDataWriter(file, data.size(), data.dimension());
        writer.addAll(data);
        writer.close();
    }

    public int size(List<IDataSequence> data)
    {
        int size = 0;
        for (int i=0; i<data.size(); i++)
            size += data.size();
        return size;
    }
    
    public IDataSequence concat(List<IDataSequence> data)
    {
        return new DataSequenceConcatenated(data);
    }
    
    public IDataSequence concat(List<IDataSequence> data, int interleaf)
    {
        return new DataSequenceConcatenatedInterleaved(data, interleaf);
    }

    public IDoubleArray mean(Iterable<IDoubleArray> data)
    {
        IDoubleArray res = null;
        int N = 0;
        
        for (IDoubleArray x : data)
        {
            if (res == null)
            {
                res = x.copy();
            }
            else
            {
                Algebra.util.addTo(res, x);
            }
            
            N++;
        }
        
        Algebra.util.scale(1.0/(double)N, res);
        return res;
    }
    
    /**
     * Gyration radius of the data
     * @param data
     * @return 
     */
    public double rgyr(Iterable<IDoubleArray> data)
    {
        IDoubleArray mean = mean(data);
        
        double sum = 0;
        int N = 0;
        
        for (IDoubleArray x : data)
        {
            double d = Algebra.util.norm(Algebra.util.subtract(x, mean));
            sum += d*d;
            N++;
        }
        
        return (1.0 / ((double)N))*Math.sqrt(sum);
    }

    /**
     * Maximum radius of the data, i.e. maximum distance from the center
     * @param data
     * @return 
     */
    public double rmax(Iterable<IDoubleArray> data)
    {
        IDoubleArray mean = mean(data);

        double max = 0;
        
        for (IDoubleArray x : data)
        {
            double d = Algebra.util.norm(Algebra.util.subtract(x, mean));
            if (d > max)
                max = d;
        }

        return max;
    }    
}
