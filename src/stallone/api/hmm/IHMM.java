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
public interface IHMM
{
    public IHMMParameters getParameters();
    
    public IHMMHiddenVariables getHidden(int itraj);
    
    public List<IIntArray> viterbi();
    
    public double getLogLikelihood();    
    
    public double[] getLogLikelihoodHistory();    
    
}
