/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import cern.jet.random.Beta;
import cern.jet.random.Exponential;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import stallone.api.doubles.IDoubleArray;
import stallone.api.mc.IReversibleSamplingStep;

/**
 *
 * Implements the reversible element quadruple shift described in Trendelkamp-Schroer and Noe JCP 2013
 *
 * TODO: This does not yet work in combination with the row shift. The proposal probability needs to corrected when mixing these steps
 * See Noe JCP 2008
 *
 * @author trendelkamp, noe
 */
public class Step_Rev_Quad_Trendelkamp implements IReversibleSamplingStep
{
    private int n;
    private IDoubleArray C;

    private IDoubleArray T;
    private IDoubleArray mu;

    // random number generator
    private MersenneTwister rand = new MersenneTwister();
    // various random generators for the reversible element shift.
    private Uniform randU = new Uniform(0.0, 1.0, rand);
    private Exponential randE = new Exponential(1.0, rand);
    private Beta randB = new Beta(1.0, 1.0, rand);

    private double Tii_backup, Tij_backup, Tji_backup, Tjj_backup;

    public Step_Rev_Quad_Trendelkamp()
    {}

    @Override
    public void init(IDoubleArray _C, IDoubleArray _T, IDoubleArray _mu)
    {
        this.n = _C.rows();
        this.C = _C;

        this.T = _T;
        this.mu = _mu;
    }

    /**
     * Gibbs sampling step of a random quadruple of four elements (i,j), (i,i), (j,j), (j,i)
     * according to the method described in Trendelkamp+Noe JCP 2013
     * @return
     */
    public void sampleQuad(int i, int j)
    {
    	//Ensure that the element is only updated if all counts are non-negative
    	//This ensures that the quad step can handle negative counts
    	//resulting from a prior with negative entries
    	if(i<j && C.get(i, j)+C.get(j, i)>=0.0 && C.get(i,i)>=0.0 && C.get(j, j)>=0.0){
            //Compute parameters
            double a=C.get(i,j)+C.get(j,i);

            double delta=T.get(i,i)+T.get(i,j);
            double lambda=mu.get(j)/mu.get(i)*(T.get(j,j)+T.get(j,i));
            double b,c,d;

            //Ensure that d won't grow out of bounds
            if(delta>1e-15 && lambda>1e-15)
            {
                //Assign parameters according to ordering of delta and lambda
                if(delta<=lambda){
                    b=C.get(i,i);
                    c=C.get(j,j);
                    d=lambda/delta;
                }
                else{
                    b=C.get(j,j);
                    c=C.get(i,i);
                    d=delta/lambda;
                }
                //Generate random variate
                double x=ScaledElementSampler.sample(randU, randE, randB, a, b, c, d);

                //Update T
                T.set(i,j, x*Math.min(delta,lambda));
                T.set(i,i, delta-T.get(i,j));
                T.set(j,i, mu.get(i)/mu.get(j)*T.get(i,j));
                T.set(j,j, mu.get(i)/mu.get(j)*lambda-T.get(j,i));
            }
            //Else do nothing.
    	}
    	//Else do noting
    }

    @Override
    public boolean step()
    {
        //Draw (i,j) uniformly from {0,..,n-1}x{0,...,n-1} subject to i<j
        int k=randU.nextIntFromTo(0, n-1);
        int l=randU.nextIntFromTo(0, n-1);
        //Exclude i=j
        while(k==l){
            k=randU.nextIntFromTo(0, n-1);
            l=randU.nextIntFromTo(0, n-1);
        }
        //Enforce i<j
        int i=Math.min(k,l);
        int j=Math.max(k,l);

        sampleQuad(i,j);
        return true;
    }

}
