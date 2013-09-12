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
public final class TransitionMatrixEstimatorRevFixPi implements ITransitionMatrixEstimator
{
    // convergence

    //private int nIterMax = 10000;
    //private int nIterPer1 = 100;
    private int nIterMax = 1000000;
    private int nIterPer1 = 1000;
    private IDoubleList logliks = Doubles.create.list(nIterMax);

    private IDoubleArray C;    
    
    private IDoubleArray pi = null;
    private IDoubleArray X;
    private IDoubleIterator itX;

    private boolean verbose = false;
    
    public TransitionMatrixEstimatorRevFixPi(IDoubleArray _C, IDoubleArray _pi)
    {
        this.pi = _pi;
        this.setCounts(_C);        
    }

    public TransitionMatrixEstimatorRevFixPi(IDoubleArray _pi)
    {
        this.pi = _pi;
    }    
    
    private void initX()
    {
        // initial T(tau)
        IDoubleArray T = C.copy();
        for (int i = 0; i < T.rows(); i++)
        {
            IDoubleArray r = T.viewRow(i);
            Algebra.util.scale(Doubles.util.sum(r), r);
            /*double s = DoubleArrays.sum(T[i]);
            for (int j = 0; j < T.length; j++)
            {
                if (T[i][j] != 0)
                {
                    T[i][j] /= s;
                }
                else if (i == j)
                {
                    T[i][j] = 1;
                }
            }*/
        }

        // initial off-diagonals
        this.X = C.create(C.rows(),C.columns());
        for (IDoubleIterator it = T.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            
                if (i != j)
                {
                    X.set(i,j, 0.5 * (pi.get(i) * T.get(i,j) + pi.get(j) * T.get(j,i)));
                }

                if (Double.isNaN(X.get(i,j)))
                {
                    System.out.println("NaN: " + i + " " + j);
                }
        }

        // scale all off-diagonals if necessary to realize stationary dist.
        double o = 0;
        for (int i = 0; i < X.rows(); i++)
        {
            o = Math.max(o, Doubles.util.sum(X.viewRow(i)) / pi.get(i));
        }

        if (o > 0.9)
        {
            Algebra.util.scale((0.9 / o), X);
        }

        // enforce stationary distribution
        for (int i = 0; i < X.rows(); i++)
        {
            X.set(i,i, pi.get(i) - Doubles.util.sum(X.viewRow(i)));
        }
        
        this.itX = X.nonzeroIterator();
        
        // TEST
        /*System.out.println("Testing X: ");
        IDoubleArray T2 = X.copy();
        alg.normalizeRows(T2, 1);
        IDoubleArray pitest = msm.stationaryDistribution(T2);
        for (int i=0; i<pitest.size(); i++)
            System.out.println(pi.get(i)+"\t"+pitest.get(i));*/
    }
    
    private double logL()
    {        
        // compute likelihood using the matrix iterator
        double ll = 0;
        
        itX.reset();
        int i,j;
        
        while(itX.hasNext())
        {
            i = itX.row();
            j = itX.column();
            
            if (X.get(i,j) > 0)
            {
                ll += C.get(i,j) * Math.log(X.get(i,j) / pi.get(i));
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


    private double dLL(int i, int j, double d)
    {
        double dll = C.get(i,i)*Math.log(X.get(i,i) - d)
                    +C.get(j,j)*Math.log(X.get(j,j) - d)
                    +(C.get(i,j)+C.get(j,i))*Math.log(X.get(i,j) + d);
        return(dll);
    }
    
    private double opt(int i, int j, double dmin, double dmax)
    {
        double x_ii = X.get(i,i);
        double x_jj = X.get(j,j);
        double x_ij = X.get(i,j);
        double c_ii = C.get(i,i);
        double c_jj = C.get(j,j);
        double c_ij = C.get(i,j);
        double c_ji = C.get(j,i);

        double E = c_ij*x_ii + c_ji*x_ii + c_jj*x_ii - c_ii*x_ij - c_jj*x_ij + c_ii*x_jj + c_ij*x_jj + c_ji*x_jj;
        double A = Math.pow(-c_ij*x_ii - c_ji*x_ii - c_jj*x_ii + c_ii*x_ij + c_jj*x_ij - c_ii*x_jj - c_ij*x_jj - c_ji*x_jj, 2);
        double B = 4*(c_ii + c_ij + c_ji + c_jj)*(-c_jj*x_ii*x_ij + c_ij*x_ii*x_jj + c_ji*x_ii*x_jj - c_ii*x_ij*x_jj);
        double D = 2*(c_ii + c_ij + c_ji + c_jj);

        double d1 = (E - Math.sqrt(A - B))/D;
        double d2 = (E + Math.sqrt(A - B))/D;
        
        double lbest = dLL(i,j,0);
        double dbest = 0;
        
        double l = dLL(i,j,dmin);
        if (l>lbest)
        {
            lbest = l;
            dbest = dmin;
        }
        
        l = dLL(i,j,dmax);
        if (l>lbest)
        {
            lbest = l;
            dbest = dmax;
        }
        
        // test d1
        if (d1 >= dmin && d1 <= dmax)
        {
            l = dLL(i,j,d1);
            if (l>lbest)
            {
                lbest = l;
                dbest = d1;
            }
        }

        // test d2
        if (d2 >= dmin && d2 <= dmax)
        {
            l = dLL(i,j,d2);
            if (l>lbest)
            {
                lbest = l;
                dbest = d2;
            }
        }        
        
        return(dbest);                    
    }
    
    private void optimizeElement(int i, int j)
    {
        double dmin = -X.get(i,j);
        double dmax = Math.min(X.get(i,i), X.get(j,j));

        //System.out.println("X before: " + X[i][i] + "\t" + X[i][j] + "\t" + X[j][i] + "\t" + X[j][j]);

        double d = opt(i,j,dmin,dmax);
        // Newton step:
        /*double d = 0;
        for (int k = 0; k < 30; k++)
        {
            d = newtonStep(i, j, d);
        }

        if (d < dmin)
        {
            d = dmin;
        }
        if (d > dmax)
        {
            d = dmax;
        }*/

        //System.out.println("mod d " + d);
        X.set(i,i, X.get(i,i)-d);
        X.set(i,j, X.get(i,j)+d);
        X.set(j,i, X.get(j,i)+d);
        X.set(j,j, X.get(j,j)-d);
        //System.out.println("X after: " + X[i][i] + "\t" + X[i][j] + "\t" + X[j][i] + "\t" + X[j][j]);
    }

    private void step()
    {
        itX.reset();
        int i,j;
        
        while(itX.hasNext())
        {
            i = itX.row();
            j = itX.column();
                        
            if (i<j)
            {
                optimizeElement(i,j);
            }
            itX.advance();
        }
        
        double ll = logL();
        
        if (verbose)
        {
            //System.out.println(DoubleArrays.toString(X,"\t","\t"));
            System.out.println((logliks.size() + 1) + "\t" + ll);
        }        
        
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
        //    System.err.println("# "+i+"\t"+this.logliks.get(i));
        
        IDoubleArray T = X.create(X.rows(),X.columns());
        for (IDoubleIterator it = X.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            T.set(i, j, X.get(i,j) / pi.get(i));
            T.set(j, i, X.get(i,j) / pi.get(j));
        }

        return (T);
    }
        
    public double[] getLikelihoodHistory()
    {
        return(this.logliks.getArray());
    }
    
    public int getIterations()
    {
        return(this.logliks.size());
    }
    
    public void setVerbose(boolean _verbose)
    {
        this.verbose = _verbose;
    }
}
