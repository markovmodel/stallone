/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;


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
