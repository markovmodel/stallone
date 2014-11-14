package stallone.hmm;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import stallone.api.API;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.ints.IIntArray;
import stallone.intsequence.IntSequenceReaderAsciiDense;

public class testPmm
{
    private int lag;
    private int nHiddenStates;
    private List<IIntArray> dtrajs;

    @Before
    public void setUp() throws Exception
    {
        lag = 10;
        nHiddenStates = 2;
        // read discrete traj for double well
        String file = "dwell.dat";
        
        IntSequenceReaderAsciiDense foo = 
                new IntSequenceReaderAsciiDense(file);
        IIntArray arr = foo.load();
        dtrajs = new ArrayList<IIntArray>();
        dtrajs.add(arr);
    }

    @Test
    public void test() throws ParameterEstimationException
    {
        API.hmm.pmm(dtrajs, nHiddenStates, lag);
    }

}
