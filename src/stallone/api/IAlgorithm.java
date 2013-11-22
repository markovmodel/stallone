/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api;

/**
 *
 * General interface for algorithms. A unique interface for running pre-constructed
 * Algorithms is for example useful for distributed computing - where a Set of
 * Algorithms can be run in different threads
 *
 * @author noe
 */
public interface IAlgorithm
{
    /**
     * Runs the algorithm
     */
    public void perform();
}
