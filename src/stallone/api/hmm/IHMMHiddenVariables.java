/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.api.hmm;

import stallone.api.doubles.IDoubleArray;

/**
 * Read-only interface to hidden path variables for external use
 * @author noe
 */
public interface IHMMHiddenVariables
{
	public int size();

        public int nStates();

        public int mostProbableState(int t);

        public int[] getMaxPath();

	public double getAlpha(int t, int s);

        public double getBeta(int t, int s);

        public double getGamma(int t, int s);

        /**
         * Gets all gamma values of state s
         * @param s
         * @return
         */
        public IDoubleArray getGammaByState(int s);

        public double getPout(int t, int s);

	public double logLikelihood();

}
