package stallone.datasequence.io;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.datasequence.DataSequenceSkipTimeColumn;
import stallone.doubles.PrimitiveDoubleTools;

public class testDataSeqSkipTimeCol
{
    /**
     * datasequence with first column skipped in access patterns (get/iterator etc.)
     */
    private DataSequenceSkipTimeColumn seq;
    
    /**
     * normal datasequence
     */
    private IDataSequence all;

    @Before
    public void setUp() throws Exception
    {
        int n = 10;
        int dim = 4;
        IDoubleArray[] content = new IDoubleArray[n];

        for (int i = 0; i < content.length; i++)
        {
            // FIXME: this crashes
            // content[i] = API.doublesNew.listRandom(dim);
            double[] res = PrimitiveDoubleTools.randomArray(dim);
            content[i] = API.doublesNew.array(res);
        }

        all = API.dataNew.array(content);
        seq = new DataSequenceSkipTimeColumn(all);
    }

    @Test
    public void testSize()
    {
        assertEquals(all.size(), seq.size());
    }

    @Test
    public void testDimension()
    {
        assertEquals(all.dimension() - 1, seq.dimension());
    }

    @Test
    public void testGet()
    {
        IDoubleArray without_t = seq.get(0);
        IDoubleArray with_t = all.get(0);

        assertEquals(without_t.size(), with_t.size() - 1);

        for (int i = 0; i < without_t.size(); i++)
        {
            assertEquals(without_t.get(i), with_t.get(i + 1), 1e-15);
        }
    }

    @Test
    public void testIterator()
    {
        Iterator<IDoubleArray> iter = seq.iterator();

        List<IDoubleArray> byIndex = new ArrayList<IDoubleArray>();
        List<IDoubleArray> byIter = new ArrayList<IDoubleArray>();

        for (int i = 0; i < seq.size(); i++)
        {
            byIndex.add(seq.get(i));
        }

        while (iter.hasNext())
        {
            byIter.add(iter.next());
        }

        assertEquals(byIndex, byIter);
    }
}
