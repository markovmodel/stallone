/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static stallone.api.API.*;

import javax.naming.OperationNotSupportedException;
import stallone.api.datasequence.IDataReader;
import stallone.api.datasequence.IDataSequence;
import stallone.api.datasequence.IDataInput;
import stallone.coordinates.PCA;

/**
 *
 * @author noe
 */
public class CoordinateFactory
{
    public ICoordinateTransform pca(String input)
    {
        List<String> filenames = io.listFileNames(input);
        PCA pca = null;
        try
        {
            IDataInput datainput = dataNew.dataSequenceLoader(filenames);
            pca = new PCA(datainput);
        } 
        catch (IOException ex)
        {
            Logger.getLogger(CoordinateFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return pca;
    }

    public ICoordinateTransform pca(IDataSequence X)
    {
        return new PCA(X);
    }

    public ICoordinateTransform pca(IDataReader input)
    {
        PCA pca = new PCA();
        pca.addData(input.load());
        pca.computeTransform();
        return pca;
    }

    public ICoordinateTransform tica(String input)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ICoordinateTransform tica(IDataSequence X)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public ICoordinateTransform tica(IDataInput input)
    {
        //dataNew.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public ICoordinateTransform fileTransform(String cmd, String options)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
