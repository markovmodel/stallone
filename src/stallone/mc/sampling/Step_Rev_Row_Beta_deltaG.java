/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import cern.jet.random.Beta;
import cern.jet.random.engine.MersenneTwister;
import static stallone.api.API.*;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.mc.IReversibleSamplingStep;
import stallone.api.mc.IDeltaGDistribution;
import stallone.util.MathTools;

/**
 *
 * @author noe, cwehmeyer
 */
public class Step_Rev_Row_Beta_deltaG implements IReversibleSamplingStep
{
    // input, fixed
    private int n;
    private IDoubleArray C;
    private int[] dof;
    private double[] Csum;

    // variables
    private IDoubleArray mu;
    private IDoubleArray u;
    private IDoubleArray T;

    // the beta distributions for sampling the rows
    private Beta[] rowDistribution;

    // pre-instantiated data holders
    private double[] backupRow;
    private double[] backupMu;
    private double[] backupU;
    
    // object for accepting to the given DeltaG distribution
    private IDeltaGDistribution dG;

    private MersenneTwister mt;

    public Step_Rev_Row_Beta_deltaG(IDeltaGDistribution _dG)
    {
        this.dG = _dG;
    }

    @Override
    public void init(IDoubleArray _C, IDoubleArray _T, IDoubleArray _mu)
    {
        this.n = _C.rows();
        this.C = _C;
        this.T = _T;
        this.mu = _mu;

        // data holders
        this.backupRow = new double[n];
        this.backupMu = new double[n];
        this.backupU = new double[n];

        // free energies
        u = doublesNew.array(n);
        for (int i=0; i<u.size(); i++)
            u.set(i, -Math.log(mu.get(i)));

        // degrees of freedom.
        this.dof = new int[C.rows()];
        this.Csum = new double[C.rows()];

        for (IDoubleIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            if (C.get(i, j) >= -1)
            {
                dof[i]++;
                Csum[i] += C.get(i, j);
            }
        }

        mt = new MersenneTwister();

        // init beta distributions
        rowDistribution = new Beta[Csum.length];
        for ( int i = 0; i < Csum.length; i++ )
        {
            //double alpha = Csum[i] - C.get(i, i) + 1;
            double alpha = Csum[i] + dof[i] - C.get(i,i) - 1;
            double beta = C.get(i, i) + 1;
            rowDistribution[i] = new Beta( alpha, beta, mt );
        }

        // check counts.
        if (!checkCounts())
        {
            throw (new IllegalArgumentException("This Matrix cannot be sampled reversibly as it has no row with positive diagonal counts and at least 2 degrees of freedom"));
        }
    }


    /**
     * Checks whether there is at least one row that can be sampled from
     * @return
     */
    protected final boolean checkCounts()
    {
        boolean valid = false;
        for (int i = 0; i < C.rows(); i++)
        {
            if (C.get(i, i) > -1 && dof[i] >= 2)
            {
                valid = true;
            }
        }

        return (valid);
    }


    /**
     * backs up row i
     * @param row
     */
    private void backupRow( int row )
    {
        for (int k=0; k<n; k++)
            backupRow[k] = T.get(row,k);
    }

    private void restoreRow( int row )
    {
        for (int k=0; k<n; k++)
            T.set( row, k, backupRow[k] );
    }

    private void backupVecs()
    {
        for ( int k=0; k<n; k++ )
        {
            backupMu[k] = mu.get(k);
            backupU[k] = u.get(k);
        }
    }

    private void restoreVecs()
    {
        for ( int k=0; k<n; k++ )
        {
            mu.set( k, backupMu[k] );
            u.set( k, backupU[k] );
        }
    }

    /**
     * Samples from one row shift distribution via a beta distribution
     */
    public void sampleRow( int i, IDeltaGDistribution dG )
    {
        double x = rowDistribution[i].nextDouble();
        if ( ( 0.0 == x) || ( 1.0 == x ) )
            return;
        double alpha = x / ( 1.0 - T.get(i,i) );
        backupRow( i );
        double sum = 0.0;
        for ( int k=0; k<T.columns(); ++k )
        {
            if ( k == i )
                continue;
            T.set( i, k, T.get(i,k) * alpha );
            sum += T.get(i,k);
        }
        T.set( i, i, 1.0 - sum );
        if ( ! TransitionMatrixSamplingTools.isRowIn01( T, i ) )
        {
            restoreRow( i );
            return;
        }
        backupVecs();
        u.set( i, u.get(i) + Math.log( alpha ) );
        mu.set( i, Math.exp( -u.get(i) ) );
        double minu = doubles.min( u );
        if ( Math.abs( minu ) > 1.0 )
        {
            alg.addTo( u, -minu );
            for ( int k=0; k<T.columns(); ++k )
                mu.set( k, Math.exp( -u.get(k) ) );
        }
        if ( dG.accept( mu, mt.nextDouble() ) )
            return;
        restoreRow( i );
        restoreVecs();
    }

    @Override
    public boolean step( )
    {
        int i = MathTools.randomInt( 0, T.rows() );
        while ( C.get(i,i) <= 0 || dof[i] < 2 )
        {
            i = MathTools.randomInt( 0, T.rows() );
        }

        sampleRow( i, this.dG );

        return true;
    }

}
