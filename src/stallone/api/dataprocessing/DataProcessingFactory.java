/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dataprocessing;

import java.io.IOException;
import static stallone.api.API.*;

import java.util.List;
import stallone.api.coordinates.ICoordinateTransform;
import stallone.api.datasequence.*;
import stallone.dataprocessing.DataOutput;
import stallone.dataprocessing.Pipeline;


/**
 *
 * TODO: Move all reader and writer classes and interfaces to this package?
 * 
 * @author noe
 */
public class DataProcessingFactory
{
    // =========================================================================
    //
    // Individual processors
    //
    // =========================================================================
    
    /**
     * Creates a multi-trajectory loader
     * @param files
     * @return
     * @throws IOException 
     */
    public IDataSequenceLoader loader(List<String> files)
            throws IOException
    {
        return dataNew.multiSequenceLoader(files);
    }

    /**
     * Creates a multi-trajectory reader
     * @param files
     * @return
     * @throws IOException 
     */
    public IDataInput reader(List<String> files)
            throws IOException
    {
        return dataNew.multiSequenceLoader(files);
    }

    public IDataProcessor transformer(ICoordinateTransform T)
    {
        return null;
    }
    
    
    /**
     * Creates a multi-trajectory reader
     * @param files
     * @return
     * @throws IOException 
     */
    public IDataOutput writer(String _path, IDataWriter _writer, IDataInput _input)
            throws IOException
    {
        return new DataOutput(_path, _writer, _input);
    }

    
    /**
     * Creates a multi-trajectory reader
     * @param files
     * @return
     * @throws IOException 
     */
    public IDataOutput writer(String _path, String fileExtension, IDataInput _input)
            throws IOException
    {
        IDataWriter dw = dataNew.writer("x."+fileExtension, _input.numberOfSequences(), _input.dimension());
        return new DataOutput(_path, dw, _input);
    }
    

    // =========================================================================
    //
    // Processing logic
    //
    // =========================================================================

    public IPipeline pipeline(IDataProcessor... dp)
    {
        return new Pipeline(dp);
    }

}
