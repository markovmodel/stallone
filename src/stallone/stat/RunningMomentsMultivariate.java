/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import static stallone.api.API.*;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

/**
 *
 * Efficient computation of running moments for multivariate data
 * 
 * @author noe
 */
public class RunningMomentsMultivariate
{
    private int dim = 1;
    private int tau = 0;

    // temporary count vector
    private IDoubleArray c;
    // temporary product matrix
    private IDoubleArray cc;
    // time-lagged product matrix
    private IDoubleArray cctau;
    
    // mean vector
    private IDoubleArray mean;
    // covariance matrix
    private IDoubleArray Cov;
    // time-lagged covariance matrix
    private IDoubleArray Covtau;
    
    // number of samples so far
    private double n=0;

    /**
     * Creates a running moments calculator with given data dimension.
     * @param _dimension dimension of the data. Sets the size of the mean vector 
     * and the number of rows/columns in the covariance matrix.
     */
    public RunningMomentsMultivariate(int _dimension)
    {
        // set dimension
        this.dim = _dimension;
        c = doublesNew.array(dim);
        cc = doublesNew.array(dim,dim);
        mean = doublesNew.array(dim);
        Cov = doublesNew.array(dim,dim);
    }

    /**
     * Creates a running moments calculator  with given data dimension
     * including the time-lagged covariance matrix.
     * @param _dimension dimension of the data. Sets the size of the mean vector 
     * and the number of rows/columns in the covariance matrix.
     * @param _tau the time lag for the time-lagged covariance matrix
     */
    public RunningMomentsMultivariate(int _dimension, int _tau)
    {
        this(_dimension);
        this.tau = _tau;
        cctau = doublesNew.array(dim,dim);
        Covtau = doublesNew.array(dim,dim);        
    }
    
    /**
     * adds data to prepare the transform computation
     * @param data The data input
     */
    final public void addData(IDataSequence data)
    {
        if (data.dimension() != dim)
            throw new IllegalArgumentException("Data has incompatible dimension "+data.dimension()+". Expected "+dim);

        // new data size
        double m = data.size();
        
        // compute new data count
        c.zero();
        for (IDoubleArray x : data)
        {
            for (int i=0; i<dim; i++)
                this.c.set(i, this.c.get(i)+x.get(i));
        }
        
        // update mean
        mean = alg.addWeightedToNew(n/(n+m), mean, 1.0/(n+m), c);

        // compute new data product matrix
        cc.zero();
        for (IDoubleArray x : data)
        {   
            // mean-free data
            for (int i=0; i<dim; i++)
                x.set(i, x.get(i)-mean.get(i));
            
            for (int i=0; i<dim; i++)
            {
                for (int j=i; j<dim; j++)
                {
                    double xij = x.get(i) * x.get(j);
                    this.cc.set(i,j, this.cc.get(i,j)+xij);
                    this.cc.set(j,i, this.cc.get(i,j));
                }
            }
        }
        
        // update covariance matrix
        Cov = alg.addWeightedToNew(n/(n+m), Cov, 1.0/(n+m), cc);

        // also compute time-lagged covariance, when requested
        if (cctau != null)
        {
            // compute new data product matrix
            cctau.zero();
            for (IDoubleArray[] x : data.pairs(tau))
            {   
                // mean-free data
                for (int i=0; i<dim; i++)
                {
                    x[0].set(i, x[0].get(i)-mean.get(i));
                    x[1].set(i, x[1].get(i)-mean.get(i));
                }

                for (int i=0; i<dim; i++)
                {
                    for (int j=i; j<dim; j++)
                    {
                        this.cctau.set(i,j, this.cctau.get(i,j) + x[0].get(i)*x[1].get(j));
                        this.cctau.set(j,i, this.cctau.get(j,i) + x[0].get(j)*x[1].get(i));
                    }
                }
            }
            
            // update covariance matrix
            Covtau = alg.addWeightedToNew(n/(n+m), Covtau, 1.0/(n+m), cctau);
        }
        
        // update accumulated data size
        n += m;
    }    
    
    public IDoubleArray getMean()
    {
        return mean;
    }
    
    public IDoubleArray getCov()
    {
        return Cov;
    }

    public IDoubleArray getCovLagged()
    {
        return Covtau;
    }
    
    public int getNumberOfSamples()
    {
        return (int)n;
    }
    
}
