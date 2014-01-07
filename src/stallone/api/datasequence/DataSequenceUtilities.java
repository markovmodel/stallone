/*
 * TODO: This class needs a major cleanup!
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.datasequence;

import static stallone.api.API.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.datasequence.*;

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
        List<IDataSequence> res = new ArrayList<IDataSequence>();
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
            list.add(loader.get(itraj, iindex));
        }

        res.add(list);

        return (res);
    }


    /**
     * Loads the content of the specified file as a data sequence
     * @param inputFile the input file. The file type is 
     * determined from the extension
     * @return a data sequence
     */
    public IDataSequence loadSequence(String inputFile)
    {
        IDataSequence res = null;
        try
        {
            IDataReader input = dataNew.reader(inputFile);
            res = input.load();
        }
        catch (IOException ex)
        {
            Logger.getLogger(DataSequenceUtilities.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return(res);
    }

    /**
     * Transforms the data sequence to a two-dimensional array, where the
     * rows hold the serialized data sets
     * @param inp a data sequence
     * @return a double array
     */
    public IDoubleArray toArray(IDataSequence inp)
    {
        int n = inp.size();
        int d = inp.dimension();
        IDoubleArray M = doublesNew.matrix(n, d);
        IDoubleArray r;
        for (int i=0; i<n; i++)
        {
            r = inp.get(i);
            for (int j=0; j<d; j++)
            {
                M.set(i,j, r.get(j));
            }
        }
        return (M);
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
            IDataReader input = DataSequence.create.readerASCII(inputFile);
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

    public void writeSequence(IDataSequence data, String file)
            throws IOException
    {
        IDataWriter writer = DataSequence.create.writer(file, data.size(), data.dimension());
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
