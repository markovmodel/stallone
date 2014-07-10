/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.coordinates;

import java.io.IOException;
import static stallone.api.API.*;

import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.coordinates.IPCA;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;

import java.util.Random;
import stallone.api.dataprocessing.IDataProcessor;
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataList;
import stallone.stat.RunningMomentsMultivariate;
/**
 *
 * @author noe
 */
public class PCA implements IPCA
{
    // input
    private IDataInput input;
    
    // running moments
    private RunningMomentsMultivariate moments;
    
    // input dimension
    private int dimIn;
    
    private IDoubleArray eval;
    private IDoubleArray evec;
    // output dimension
    private int dimOut;
    
    public PCA(IDataInput _source)
    {
        this.input = _source;
        this.setupTransform(_source);
    }
    
    public PCA()
    {
    }
    
            
    /**
     * recomputes the transform based on all data added to this point. 
     * If the coordinate transform is constant, this call has no effect.
     * @param X A data sequence. 
     */
    @Override
    public void setupTransform(IDataInput _input)
    {
        // set input dimension
        this.dimIn = this.input.dimension();
        if (this.dimOut == 0)
            this.dimOut = dimIn; // by default full output dimension
        moments = statNew.runningMomentsMultivar(dimIn);
        
        // iterate all data and feed moments
        for (IDataSequence x : _input.sequences())
            moments.addData(x);
        
        // compute transformation
        IDoubleArray Cov = moments.getCov();
        IEigenvalueDecomposition evd = alg.evd(Cov);
        this.eval = evd.getEvalNorm();
        this.evec = evd.getRightEigenvectorMatrix().viewReal();
    }

    @Override
    public void setupTransform(IDataSequence _input)
    {
        setupTransform(dataNew.dataInput(_input));
    }    
    

    //==========================================================================
    //
    // Getters
    //
    //==========================================================================
    
    
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
    public void setDimension(int d)
    {
        dimOut = d;
    }

    @Override
    public IDoubleArray getEigenvalues()
    {
        return eval;
    }

    @Override
    public IDoubleArray getEigenvector(int i)
    {
        return evec.viewColumn(i);
    }

    @Override
    public IDoubleArray getEigenvectorMatrix()
    {
        return evec;
    }

    
    //==========================================================================
    //
    // CoordinateTransform methods
    //
    //==========================================================================
    
    
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
        
        IDoubleArray y = alg.product(x, evec);
        int d = Math.min(in.size(),out.size());
        for (int i=0; i<d; i++)
            out.set(i, y.get(i));
    }
    
    @Override
    public int dimension()
    {
        return dimOut;
    }

    
    //==========================================================================
    //
    // Test main
    //
    //==========================================================================
    
    public static void main(String[] args) 
            throws IOException
    {
        /*
        Random rand = new Random();
        IDataList seq = dataNew.list();
        for (int i=0; i<10000; i++)
        {
            seq.add(doublesNew.arrayFrom(0.5*rand.nextGaussian()-2, 0.5*rand.nextGaussian()-2));
            seq.add(doublesNew.arrayFrom(0.5*rand.nextGaussian()+2, 0.5*rand.nextGaussian()+2));
            seq.add(doublesNew.arrayFrom(0.5*rand.nextGaussian()+4, 0.5*rand.nextGaussian()+4));
        }
        PCA pca = new PCA();
        pca.addData(seq);
        pca.computeTransform();
        System.out.println("mean: \t"+doubles.toString(pca.getMeanVector(), "\t"));
        System.out.println("cov: \t"+doubles.toString(pca.getCovarianceMatrix(), "\t", "\n"));
        System.out.println();
        System.out.println("eval: \t"+doubles.toString(pca.getEigenvalues(), "\t"));
        System.out.println("evec1: \t"+doubles.toString(pca.getEigenvector(0), "\t"));
        System.out.println("evec2: \t"+doubles.toString(pca.getEigenvector(1), "\t"));
        pca.setDimension(1);
        IDoubleArray y1 = pca.transform(doublesNew.arrayFrom(2,2));
        System.out.println("y1 = \t"+doubles.toString(y1, "\t"));
        IDoubleArray y2 = pca.transform(doublesNew.arrayFrom(4,4));
        System.out.println("y2 = \t"+doubles.toString(y2, "\t"));
*/
        IDataInput dataInput = dataNew.dataInput("/Users/noe/data/software_projects/emma2/ipython/resources/Trypsin_Ca_dt1ns.dcd");
        PCA pca = new PCA(dataInput);
    }


}