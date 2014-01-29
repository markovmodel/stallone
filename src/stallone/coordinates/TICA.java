/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.coordinates;

import static stallone.api.API.*;

import java.io.FileNotFoundException;

import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.coordinates.ITICA;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.dynamics.IIntegratorThermostatted;
import stallone.api.potential.IEnergyModel;

/**
 * Computes TICA using brute-force matrix inversion of the covariance matrix.
 * This is not numerically stable and should be enhanced.
 * @author noe
 */
public class TICA implements ITICA
{
    // lag time
    private int lag;
    // count and mean
    private IDoubleArray c,  mean;
    // product and covariance matrix
    private IDoubleArray CC, Cov;
    // time-lagged product and covariance matrix
    private IDoubleArray CCTau, CovTau;
    // input dimension
    private int dimIn;
    // total number of data points
    private int N;
    
    // results
    private IDoubleArray evalTICA;
    private IDoubleArray evecTICA;
    
    
    // output dimension
    private int dimOut;
    
    public TICA(IDataInput _source, int _lag)
    {
        this.lag = _lag;
        init(_source.dimension());

        for (IDataSequence seq : _source.sequences())
            addData(seq);
        
        computeTransform();
    }

    public TICA(IDataSequence _source, int _lag)
    {
        this.lag = _lag;
        init(_source.dimension());
        addData(_source);        
        computeTransform();
    }

    public TICA(int _lag)
    {
        this.lag = _lag;
    }
    
    final private void init(int _dimIn)
    {
        this.dimIn = _dimIn;
        this.c = doublesNew.array(dimIn);
        this.mean = null;
        this.CC = doublesNew.matrix(dimIn, dimIn);
        this.Cov = null;
        this.CCTau = doublesNew.matrix(dimIn, dimIn);
        this.CovTau = null;
    }
    

    /**
     * adds data to prepare the transform computation
     * @param data The data input
     */
    @Override
    final public void addData(IDataSequence data)
    {
        if (this.dimIn == 0 && this.N == 0)
            init(data.dimension());

        for (IDoubleArray[] X : data.pairs(lag))
        {
            for (int i=0; i<dimIn; i++)
            {
                // add counts
                this.c.set(i, this.c.get(i)+X[0].get(i));
                
                // add product matrix (symmetric)
                for (int j=i; j<dimIn; j++)
                {
                    double xij = X[0].get(i) * X[0].get(j);
                    this.CC.set(i,j, this.CC.get(i,j)+xij);
                    this.CC.set(j,i, this.CC.get(i,j));
                }

                // add time-lagged product matrix (nonsymmetric)
                for (int j=0; j<dimIn; j++)
                {
                    double xij = X[0].get(i) * X[1].get(j);
                    this.CCTau.set(i,j, this.CCTau.get(i,j)+xij);
                }
            }
        }
        
        N += data.size() - lag;
    }
    
    /**
     * recomputes the transform based on all data added to this point. 
     * If the coordinate transform is constant, this call has no effect.
     * @param X A data sequence. 
     */
    @Override
    final public void computeTransform()
    {
        // compute mean
        mean = alg.scaleToNew(1.0/(double)N, c);
        IDoubleArray meanT = alg.transposeToNew(mean);
        IDoubleArray M = alg.product(mean, meanT); // matrix of <x><y>
        // compute covariance matrix
        Cov = alg.subtract(CC, M); // mean-free products
        alg.scale(1.0/(double)(N-lag-1), Cov); // covariances
        // compute time-lagged covariance matrix
        CovTau = alg.subtract(CCTau, M); // mean-free products
        CovTau = alg.addWeightedToNew(0.5, CovTau, 0.5, alg.transposeToNew(CovTau)); // symmetrize
        alg.scale(1.0/(double)(N-lag-1), CovTau); // covariances
        // simple implementation of TICA (note: this is not numerically robust!)
        IDoubleArray W = alg.product(alg.inverse(Cov), CovTau);
        IEigenvalueDecomposition evd = alg.evd(W);
        this.evalTICA = evd.getEvalNorm();
        this.evecTICA = evd.getRightEigenvectorMatrix().viewReal();
        // Whiten data
    }

    @Override
    public IDoubleArray getMeanVector()
    {
        return mean;
    }

    @Override
    public IDoubleArray getCovarianceMatrix()
    {
        return Cov;
    }
    
    @Override
    public IDoubleArray getCovarianceMatrixLagged()
    {
        return CovTau;
    }
    
    @Override
    public void setDimension(int d)
    {
        dimOut = d;
    }

    @Override
    public IDoubleArray getEigenvalues()
    {
        return evalTICA;
    }

    @Override
    public IDoubleArray getEigenvector(int i)
    {
        return evecTICA.viewColumn(i);
    }

    @Override
    public IDoubleArray getEigenvectorMatrix()
    {
        return evecTICA;
    }

    /**
     * Projects x onto the principal subspace with given output dimension;
     * @param x
     */
    @Override
    public IDoubleArray transform(IDoubleArray x)
    {
        IDoubleArray out = doublesNew.array(dimOut);
        transform(x, out);
        return out;
    }    


    /**
     * Projects the in-array onto the out array. The dimension of the out array
     * is used to determine the target dimension;
     * @param in
     * @param out 
     */
    @Override
    public void transform(IDoubleArray in, IDoubleArray out)
    {
        // subtract mean
        IDoubleArray x = alg.subtract(in, mean);
        
        // make a row
        if (x.rows() > 1)
            x = alg.transposeToNew(x);
        
        IDoubleArray y = alg.product(x, evecTICA);
        int d = Math.min(in.size(),out.size());
        for (int i=0; i<d; i++)
            out.set(i, y.get(i));
    }
    
    @Override
    public int dimension()
    {
        return dimOut;
    }
    
    public static void main(String[] args) throws FileNotFoundException
    {        
        // Using the function: 1/4 x^4 - 1/2 x^2 + 1/2 y^2
        IEnergyModel pot = potNew.multivariateFromExpression(new String[]{"x","y"},
                "1/4 x^4 - 1/2 x^2 + 1/2 y^2", // function expression
                "x^3-x", "y"); // derivatives
        // integrator
        IDoubleArray masses = doublesNew.arrayFrom(1.0, 1.0);
        double dt = 0.1, gamma = 1, kT = 0.2;
        IIntegratorThermostatted langevin = dynNew.langevinLeapFrog(pot, masses, 0.1, gamma, kT);
        // run
        IDoubleArray x0 = doublesNew.arrayFrom(0,0);
        int nsteps = 100000, nsave = 10;
        IDataSequence seq = dyn.run(x0, langevin, nsteps, nsave);
        
        // TICA
        int lag = 1;
        TICA tica = new TICA(lag);
        tica.addData(seq);
        tica.computeTransform();
        
        System.out.println("mean: \t"+doubles.toString(tica.mean, "\t"));
        System.out.println("cov: \t"+doubles.toString(tica.Cov, "\t", "\n"));
        System.out.println("covTau: \t"+doubles.toString(tica.CovTau, "\t", "\n"));
        System.out.println();
        System.out.println("eval: \t"+doubles.toString(tica.getEigenvalues(), "\t"));
        System.out.println("evec1: \t"+doubles.toString(tica.getEigenvector(0), "\t"));
        System.out.println("evec2: \t"+doubles.toString(tica.getEigenvector(1), "\t"));
        
        tica.setDimension(1);
        IDoubleArray y1 = tica.transform(doublesNew.arrayFrom(2,2));
        System.out.println("y1 = \t"+doubles.toString(y1, "\t"));

        IDoubleArray y2 = tica.transform(doublesNew.arrayFrom(4,4));
        System.out.println("y2 = \t"+doubles.toString(y2, "\t"));
    }
}