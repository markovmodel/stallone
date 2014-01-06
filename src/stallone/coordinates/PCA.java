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
import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataList;
/**
 *
 * @author noe
 */
public class PCA implements IPCA
{
    // count and mean
    private IDoubleArray c,  mean;
    // product and covariance matrix
    private IDoubleArray CC, Cov;
    // input dimension
    private int dimIn;
    // total number of data points
    private int N;
    
    
    private IDoubleArray eval;
    private IDoubleArray evec;
    // output dimension
    private int dimOut;
    
    public PCA(IDataInput _source)
    {
        init(_source.dimension());

        for (IDataSequence seq : _source.sequences())
            addData(seq);
        
        computeTransform();
    }

    public PCA(IDataSequence _source)
    {
        init(_source.dimension());
        addData(_source);        
        computeTransform();
    }
    
    public PCA()
    {
    }
    
    final private void init(int _dimIn)
    {
        this.dimIn = _dimIn;
        this.c = doublesNew.array(dimIn);
        this.mean = null;
        this.CC = doublesNew.matrix(dimIn, dimIn);
        this.Cov = null;
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

        for (IDoubleArray x : data)
        {   
            for (int i=0; i<dimIn; i++)
            {
                // add counts
                this.c.set(i, this.c.get(i)+x.get(i));
                
                // add product matrix
                for (int j=i; j<dimIn; j++)
                {
                    double xij = x.get(i) * x.get(j);
                    this.CC.set(i,j, this.CC.get(i,j)+xij);
                    this.CC.set(j,i, this.CC.get(i,j));
                }
            }
        }
        
        N += data.size();
    }
    
    /**
     * recomputes the transform based on all data added to this point. 
     * If the coordinate transform is constant, this call has no effect.
     * @param X A data sequence. 
     */
    @Override
    final public void computeTransform()
    {
        mean = alg.scaleToNew(1.0/(double)N, c);
        IDoubleArray meanT = alg.transposeToNew(mean);
        IDoubleArray M = alg.product(mean, meanT); // matrix of <x><y>
        Cov = alg.subtract(CC, M); // mean-free products
        alg.scale(1.0/(double)(N-1), Cov); // covariances
        IEigenvalueDecomposition evd = alg.evd(Cov);
        this.eval = evd.getEvalNorm();
        this.evec = evd.getRightEigenvectorMatrix().viewReal();
    }
    
    public IDoubleArray getMeanVector()
    {
        return mean;
    }

    public IDoubleArray getCovarianceMatrix()
    {
        return Cov;
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
    
    public static void main(String[] args) 
            throws IOException
    {
        /*Random rand = new Random();
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
        System.out.println("mean: \t"+doubles.toString(pca.mean, "\t"));
        System.out.println("cov: \t"+doubles.toString(pca.Cov, "\t", "\n"));
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
        
        IDataInput dataInput = dataNew.dataInput("/Users/noe/data/temp/tmp_ca.dcd");
        PCA pca = new PCA(dataInput);
    }
}