package stallone.datasequence.io;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import stallone.api.datasequence.IDataSequence;
import stallone.datasequence.io.DcdReader;
import stallone.datasequence.io.DcdWriter;



public class TestDCDReaderWriter
{
    String output = "output.dcd";
    String input = "test/stallone/datasequence/io/input.dcd";
    DcdWriter writer;
    IDataSequence seq;
    
    @Before 
    public void setUp() throws IOException {
//        ConsoleHandler handler = new ConsoleHandler();
//        handler.setLevel(Level.FINEST);
//        Logger.getLogger(DcdReader.class.getName()).addHandler(handler);
        
        
        DcdReader reader = new DcdReader(input);
        seq = reader.load();
        writer = new DcdWriter(output, reader.size(), 3);
    }

    @Test
    public void testWrite()
    {
        writer.addAll(seq);
    }
    
    @Test
    public void compareWrittenWithPreviouslyRed() throws IOException {
        DcdReader reader = new DcdReader(output);
        IDataSequence outputSeq = reader.load();
        assertTrue("length of input and output", seq.size() == outputSeq.size());
        
        int size = seq.size();
        for(int i =0; i < size; i++) {
            assertArrayEquals(seq.get(i).getArray(),
                    outputSeq.get(i).getArray(), 1E-6);
        }
    }

}
