/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import java.util.List;
import stallone.api.ints.IIntArray;


/**
 *
 * @author noe
 */
public interface IHMMOptimizer
{
    public void run()
            throws ParameterEstimationException;

    public IHMM getHMM();
}
