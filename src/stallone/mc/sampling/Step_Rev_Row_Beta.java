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
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class Step_Rev_Row_Beta implements IReversibleSamplingStep
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
    
    int nprop=0;
    int nacc=0;    

    public Step_Rev_Row_Beta()
    {}

    @Override
    public void init(IDoubleArray _C, IDoubleArray _T, IDoubleArray _mu)
    {
        this.n = _C.rows();
        this.C = _C;
        this.T = _T;
        this.mu = _mu;

        // data holders
        this.backupRow = new double[n];

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

        // init beta distributions
        rowDistribution = new Beta[Csum.length];
        for (int i = 0; i < Csum.length; i++)
        {
            //double alpha = Csum[i] - C.get(i, i) + 1;
            double alpha = Csum[i] + dof[i] - C.get(i, i) - 1;
            double beta = C.get(i, i) + 1;
            rowDistribution[i] = new Beta(alpha, beta, new MersenneTwister());
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
    private void backupRow(int row)
    {
        for (int k=0; k<n; k++)
            backupRow[k] = T.get(row, k);
    }

    private void restoreRow(int row)
    {
        for (int k=0; k<n; k++)
            T.set(row, k, backupRow[k]);
    }

    /**
     * Samples from one row shift distribution via a beta distribution
     */
    public void sampleRow(int i)
    {
        double x = rowDistribution[i].nextDouble();
        double a = x / (1.0 - T.get(i, i));

        // backup
        backupRow(i);

        // update matrix
        double sum = 0;
        for (int k = 0; k < T.columns(); k++)
        {
            if (k != i)
            {
                T.set(i, k, T.get(i, k) * a);
                //ensureValidElement(i, k);
                sum += T.get(i, k);
            }
        }
        T.set(i, i, 1 - sum);

        // check if there are problems and then revert to backup
        if (TransitionMatrixSamplingTools.isRowIn01(T, i))
        {
            u.set(i, u.get(i) + Math.log(a));
            mu.set(i, Math.exp(-u.get(i)));

            // rescale u if necessary
            if (Math.abs(doubles.min(u)) > 1)
            {
                alg.addTo(u, -doubles.min(u));
                for (int k=0; k<T.columns(); k++)
                    mu.set(k, Math.exp(-u.get(k)));
            }
        }
        else
        {
            restoreRow(i);
        }
    }

    @Override
    public boolean step()
    {
        int i = MathTools.randomInt(0, T.rows());
        while (C.get(i, i) <= 0 || dof[i] < 2)
        {
            i = MathTools.randomInt(0, T.rows());
        }

        sampleRow(i);
        this.nprop++;
        this.nacc++;

        return true;
    }

    public int[] getStepCount(){
        int[] count=new int[2];
        count[0]=this.nprop;
        count[1]=this.nacc;
        return count;
    }
}
