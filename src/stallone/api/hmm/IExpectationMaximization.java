/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

/**
 *
 * @author noe
 */
public interface IExpectationMaximization extends IHMMOptimizer
{
    /**
     * Sets the number of EM steps after which the algorithm terminates
     * @param nsteps 
     */
    public void setMaximumNumberOfStep(int nsteps);

    /**
     * Sets the maximum admissible decrease of the likelihood over the previous maximum after which the optimization still continues.
     * @param _dectol 
     */
    public void setLikelihoodDecreaseTolerance(double _dectol);
 
    /**
     * Returns the likelihood history of the optimization
     * @return 
     */
    public double[] getLikelihoodHistory();
}
