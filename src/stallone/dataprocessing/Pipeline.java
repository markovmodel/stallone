/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.dataprocessing;

import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.dataprocessing.IPipeline;

/**
 *
 * @author noe
 */
public class Pipeline implements IPipeline
{
    IDataProcessor[] processors;
    
    public Pipeline(IDataProcessor... dps)
    {
        processors = dps;
    }
    
    @Override
    public void run()
    {
        for (IDataProcessor p : processors)
            p.init();
        
        for (IDataProcessor p : processors)
            p.run();        
    }
    
    @Override
    public void cleanup()
    {
        for (IDataProcessor p : processors)
            p.cleanup();        
    }
}
