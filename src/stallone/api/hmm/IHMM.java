/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

/**
 *
 * @author noe
 */
public interface IHMM
{
    public IHMMParameters getParameters();
    
    public IHMMHiddenVariables getHidden(int itraj);
    
    public double getLogLikelihood();    
}
