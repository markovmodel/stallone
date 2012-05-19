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
    public double[] run(int nsteps, double dectol)
            throws ParameterEstimationException;

    public IHMMParameters getParameters();
    
    public IHMMHiddenVariables getHidden(int itraj);
    
    public double getLogLikelihood();
}
