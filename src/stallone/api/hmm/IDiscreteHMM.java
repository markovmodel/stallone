/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.hmm;

import java.util.List;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public interface IDiscreteHMM extends IHMM
{
    /**
     * Returns the output probability matrix
     * @return m x n matrix containing the output probabilities. 
     * Index (i,j) yields the probability that hidden state i will produce observable symbol j.
     */
    public IDoubleArray getOutputProbabilities();

}
