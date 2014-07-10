/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.dataprocessing;

/**
 *
 * @author noe
 */
public interface IPipeline
{
    public void run();

    public void cleanup();
            
}
