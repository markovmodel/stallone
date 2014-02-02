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
import stallone.stat.RunningMomentsMultivariate;

/**
 * Computes TICA using brute-force matrix inversion of the covariance matrix.
 * This is not numerically stable and should be enhanced.
 * @author noe
 */
public class TICA implements ITICA
{
    // lag time
    private int lag;
    // running moments
    RunningMomentsMultivariate moments;
    // input dimension
    private int dimIn;
    
    // results
    private IDoubleArray CovTauSym;
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
        if (this.dimOut == 0)
            this.dimOut = _dimIn; // by default full output dimension
        moments = statNew.runningMomentsMultivar(dimIn, lag);
    }
    

    /**
     * adds data to prepare the transform computation
     * @param data The data input
     */
    @Override
    final public void addData(IDataSequence data)
    {
        if (this.dimIn == 0)// && this.N == 0)
            init(data.dimension());

        moments.addData(data);
    }
    
    /**
     * recomputes the transform based on all data added to this point. 
     * If the coordinate transform is constant, this call has no effect.
     * @param X A data sequence. 
     */
    @Override
    final public void computeTransform()
    {
        // PCA
        IDoubleArray Cov = moments.getCov();
        IEigenvalueDecomposition evd = alg.evd(Cov);
        IDoubleArray evalPCA = evd.getEvalNorm();
        IDoubleArray evecPCA = evd.getRightEigenvectorMatrix().viewReal();
        
        // normalize principal components
        IDoubleArray S = doublesNew.array(evalPCA.size());
        for (int i=0; i<S.size(); i++)
            S.set(i, 1.0*Math.sqrt(evalPCA.get(i)));
        // normalize weights by dividing by the standard deviation of the pcs 
        IDoubleArray evecPCAscaled = alg.product(evecPCA, doublesNew.diag(S));

        // time-lagged covariance matrix
        this.CovTauSym = moments.getCovLagged();
        // symmetrize
        CovTauSym = alg.addWeightedToNew(0.5, CovTauSym, 0.5, alg.transposeToNew(CovTauSym)); // symmetrize

        // TICA weights
        IDoubleArray pcCovTau = alg.product(alg.product(alg.transposeToNew(evecPCAscaled), CovTauSym), evecPCAscaled);

        IEigenvalueDecomposition evd2 = alg.evd(pcCovTau);
        this.evalTICA = evd2.getEvalNorm();
        this.evecTICA = alg.product(evecPCAscaled, evd2.getRightEigenvectorMatrix().viewReal());        
    }

    @Override
    public IDoubleArray getMeanVector()
    {
        return moments.getMean();
    }

    @Override
    public IDoubleArray getCovarianceMatrix()
    {
        return moments.getCov();
    }
    
    @Override
    public IDoubleArray getCovarianceMatrixLagged()
    {
        return CovTauSym;
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
        // if necessary, flatten input data
        if (in.columns() != 1)
        {
            in = doublesNew.array(in.getArray());
        }

        // subtract mean
        IDoubleArray x = alg.subtract(in, moments.getMean());
        
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
        
        System.out.println("mean: \t"+doubles.toString(tica.getMeanVector(), "\t"));
        System.out.println("cov: \t"+doubles.toString(tica.getCovarianceMatrix(), "\t", "\n"));
        System.out.println("covTau: \t"+doubles.toString(tica.getCovarianceMatrixLagged(), "\t", "\n"));
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