/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * Implements the reversible element quadruple shift described
 * in Trendelkamp-Schroer and Noe JCP 2013.
 * 
 * This implementation adds a rejection step
 * according to Noe JCP 2008 in order to enable usage of this step
 * in a reversible sampler with varying stationary distribution.
 * 
 * @author trendelkamp
 */
public class Step_Rev_Quad_Gibbs_MC implements IReversibleSamplingStep 
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
    
    double Tnew_ij, Tnew_ji, Tnew_ii, Tnew_jj;
    double r, rprime;
    double pacc;
    
    int nprop=0;
    int nacc=0;

    public Step_Rev_Quad_Gibbs_MC()
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
     * 
     * The methods adds a rejection step according to Noe JCP2008 in order to
     * enable usage in a reversible sampler with varying stationary
     * distribution.
     * 
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
                this.nprop++;

                //Proposed quadruple
                Tnew_ij=x*Math.min(delta, lambda);
                Tnew_ii=delta-Tnew_ij;
                Tnew_ji=mu.get(i)/mu.get(j)*Tnew_ij;
                Tnew_jj=mu.get(i)/mu.get(j)*lambda-Tnew_ji;
                
                //Acceptance ratio according to Noe JCP08
                rprime=Math.sqrt(Tnew_ij*Tnew_ij+Tnew_ji*Tnew_ji);
                r=Math.sqrt(T.get(i,j)*T.get(i,j)+T.get(j,i)*T.get(j,i));                
                pacc=Math.min(1.0,rprime/r);
                
                if(randU.nextDouble()<=pacc){
                    //Update T
                    T.set(i,j, Tnew_ij);
                    T.set(i,i, Tnew_ii);
                    T.set(j,i, Tnew_ji);
                    T.set(j,j, Tnew_jj);
                    this.nacc++;
                }
                //Else do not update quadruple
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
    
    public int[] getStepCount(){
        int[] count=new int[2];
        count[0]=this.nprop;
        count[1]=this.nacc;
        return count;
    }
    
}
