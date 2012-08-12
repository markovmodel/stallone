/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.estimator;

import stallone.api.doubles.IDoubleList;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.algebra.*;
import stallone.api.mc.*;

/**
 *
 * @author noe
 */
public final class TransitionMatrixEstimatorRev implements ITransitionMatrixEstimator
{
    // convergence

    private int nIterMax = 10000;
    private int nIterPer1 = 100;
    private IDoubleList logliks = Doubles.create.list(nIterMax);
    private IDoubleArray C;
    private double[] Crow;
    private IDoubleIterator itX;
    private IDoubleArray X;
    private double[] Xrow;
    private double Xsum;
    private boolean verbose = false;

    public TransitionMatrixEstimatorRev(IDoubleArray _C)
    {
        this.setCounts(_C);
    }

    public TransitionMatrixEstimatorRev()
    {
    }

    private void initX()
    {
        IDoubleArray CCT = Algebra.util.add(C, Algebra.util.transposeToNew(C));
        this.X = Doubles.create.symmetric(CCT);

        Algebra.util.scale(1.0 / Algebra.util.sum(this.X), this.X);

        this.itX = this.X.nonzeroIterator();        
        
        this.Xrow = new double[X.rows()];
        updateXrow();
    }

    private void updateXrow()
    {
        java.util.Arrays.fill(Xrow, 0);
        Xsum = 0;

        itX.reset();
        int i, j;
        
        while (itX.hasNext())
        {
            i = itX.row();
            j = itX.column();
//            System.out.println(i + " " + j + " " + X.get(i, j));

            Xrow[i] += X.get(i, j);            
            itX.advance();
        }
        
        for (i=0; i<Xrow[i]; i++)
        {
            Xsum += Xrow[i];
        }        
    }

    public double logL()
    {
        // compute likelihood using the matrix iterator
        double ll = 0;

        itX.reset();
        int i, j;

        //System.out.println("Calculating likelihood: ");
        while (itX.hasNext())
        {
            i = itX.row();
            j = itX.column();
            //System.out.println(" ("+i+","+j+") : "+X.get(i,j));

            if (X.get(i, j) > 0)
            {
                ll += C.get(i, j) * Math.log(X.get(i, j) / Xrow[i]);
                //System.out.println(" += "+(C.get(i, j) * Math.log(X.get(i, j) / Xrow[i])));
            }

            itX.advance();
        }

        return (ll);
    }

    private boolean isConverged()
    {
        // check for number of iterations
        if (this.logliks.size() >= this.nIterMax)
        {
            return (true);
        }

        // check for good convergence
        if (this.logliks.size() <= this.nIterPer1)
        {
            return (false);
        }

        int i2 = this.logliks.size() - 1;
        int i1 = i2 - this.nIterPer1;
        double dL = this.logliks.get(i2) - this.logliks.get(i1);

        return (dL <= 1.0);
    }

    /**
    Maximum likelihood reversible transition matrix
     */
    public void step()
    {
        //System.out.println("stepping: ");
        
        
        itX.reset();
        int i, j;
        double xij;
        
        while (itX.hasNext())
        {
            i = itX.row();
            j = itX.column();

            xij = (C.get(i, j) + C.get(j, i)) / (Crow[i] / Xrow[i] + Crow[j] / Xrow[j]);
            X.set(i, j, xij);
            //X.set(j, i, xij);

            itX.advance();
        }

        updateXrow();

        // correct elements if necessary.
        if (Math.abs(Xsum-1.0) > 1e-6)
        {
            Algebra.util.scale(1.0/Xsum, X);
        }
        
        double ll = logL();

        if (verbose)
        {
            System.out.println((logliks.size() + 1) + "\t" + ll);
        }

        //System.out.println("X = \n"+X+"\n");
        
        this.logliks.append(ll);
    }

    ////////////////////////////////////////////////////////////////
    //
    //  INTERFACE FUNCTIONS
    //
    ////////////////////////////////////////////////////////////////
    public void setMaxIter(int nmax)
    {
        this.nIterMax = nmax;
    }

    /**
     * Sets the convergence criterion. Convergence accepted when the likelihood has not changed more than 1
     * for nIterPer1 Consecutive steps.
     * @param nIterPer1
     */
    public void setConvergence(int niter)
    {
        this.nIterPer1 = niter;
    }

    @Override
    public void setCounts(IDoubleArray _C)
    {
        this.C = _C;

        this.Crow = new double[_C.rows()];
        for (int j = 0; j < Crow.length; j++)
        {
            this.Crow[j] = Doubles.util.sum(this.C.viewRow(j));
        }

        initX();

        double ll = logL();
        this.logliks = Doubles.create.list(nIterMax);
        this.logliks.append(ll);
    }

    @Override
    public void estimate()
    {
        while (!isConverged())
        {
            step();
        }
    }

    @Override
    public IDoubleArray getTransitionMatrix()
    {
        // output likelihood list
        //for (int i=0; i<this.logliks.size(); i++)
        //    System.out.println("# "+i+"\t"+this.logliks.get(i));

        IDoubleArray T = X.create(X.rows(), X.columns());
        for (IDoubleIterator it = X.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            T.set(i, j, X.get(i,j) / Xrow[i]);
            //if (i != j)
            //{
            //    T.set(j, i, X.get(j,i) / Xrow[j]);
            //}
        }

        return (T);
    }
    
    public IDoubleArray getSymmetricCorrelationMatrix()
    {
        return(X);
    }

    public double[] getLikelihoodHistory()
    {
        return (this.logliks.getArray());
    }

    public int getIterations()
    {
        return (this.logliks.size());
    }

    public void setVerbose(boolean _verbose)
    {
        this.verbose = _verbose;
    }
}
