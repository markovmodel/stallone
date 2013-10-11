/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;

/**
 *
 * @author noe
 */
public class TransitionMatrixSamplerRevNew implements ITransitionMatrixSampler
{

    @Override
    public IDoubleArray sample(int steps)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double logLikelihood()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
//    protected IMatrix X, C;
//    protected double logLikelihood = 0;
//
//    protected IVector pi;
//    private int[] dof;
//    private double[] Csum;
//
//    public TransitionMatrixSamplerRevNew(IMatrix counts)
//    {
//        this.C = counts;
//        
//        // maximum likelihood estimate of X
//        TransitionMatrixEstimatorRev estimator = new TransitionMatrixEstimatorRev();
//        estimator.setCounts(counts);
//        estimator.estimate();
//        this.X = estimator.getSymmetricCorrelationMatrix();
//
//        init();
//    }
//
//    public TransitionMatrixSamplerRevNew(IMatrix counts, IMatrix Tinit)
//    {
//        this.C = counts;
//        
//        // calculate X
//        pi = MarkovModel.util.stationaryDistribution(Tinit);
//        IMatrix tmpX = Algebra.util.product(Algebra.create.diag(pi), Tinit);
//        this.X = Algebra.create.symmetric(tmpX);
//        
//        init();
//    }
//
//    protected final void init()
//    {
//        // stationary distribution
//        pi = Algebra.create.realVector(X.rows());
//        for (int i=0; i<pi.size(); i++)
//            pi.set(i, Doubles.util.sumRow(X, i));
//        
//        // initial log likelihood
//        this.logLikelihood = MarkovModel.util.logLikelihoodCorrelationMatrix(X, C);
//
//        // degrees of freedom and row sums of C
//        this.dof = new int[C.rows()];
//        this.Csum = new double[C.rows()];
//        
//        for (IMatrixIterator it = C.nonzeroIterator(); it.hasNext(); it.advance())
//        {
//            int i = it.getRow();
//            int j = it.getCol();
//            if (C.get(i, j) >= 0)
//            {
//                dof[i]++;
//                Csum[i] += C.get(i, j);
//            }
//        }
//
//        // check counts.
//        if (!checkCounts())
//        {
//            throw (new IllegalArgumentException("This Matrix cannot be sampled reversibly as it has not row with positive diagonal counts and at least 2 degrees of freedom"));
//        }
//    }
//
//    protected final boolean checkCounts()
//    {
//        for (int i = 0; i < C.rows(); i++)
//        {
//            if (C.get(i, i) < 1 || dof[i] < 2)
//            {
//                return(false);
//            }
//        }
//
//        return (true);
//    }
//
//    @Override
//    protected boolean step()
//    {
//	if (Math.random() < 0.5) // Reversible edge shift
//	{
//            return(stepReversibleEdgeShift());
//        }
//        {
//            return(stepNodeShift());
//        }
//    }
//
//        
//    //////////////////////////////////////////////////////////////////////////////// 
//    //
//    // Monte-Carlo Steps
//    //
//    //////////////////////////////////////////////////////////////////////////////// 
//    /**
//     * Conducts a single reversible edge shift MC step
//     * @returns true if the step has been accepted.
//     */
//    public boolean stepReversibleEdgeShift()
//    {
//        int i, j;
//        do
//        {
//            i = MathTools.randomInt(0, X.rows());
//            j = MathTools.randomInt(0, X.rows());
//        }
//        while (i == j);
//
//        //double[] pic = TransitionMatrix.distribution(new DenseDoubleMatrix2D(T));
//        double q = pi.get(j) / pi.get(i);
//        double dmin = Math.max(-(X.get(i, i)/pi.get(i)), -(Xq * T.get(j, j));
//        double dmax = Math.min(T.get(i, j), q * T.get(j, i));
//
//// 		if (C[i][j] < 0 || C[i][i] < 0 || C[j][i] < 0 || C[j][j] < 0)
//// 		    {
//// 			System.out.println("Trying to modify C: ");
//// 			System.out.println(C[i][j]+" "+C[i][i]+" "+C[j][i]+" "+C[j][j]);
//// 			System.out.println("T: ");
//// 			System.out.println(T[i][j]+" "+T[i][i]+" "+T[j][i]+" "+T[j][j]);
//// 			System.out.println("dmin = "+dmin);
//// 			System.out.println("dmax = "+dmax);
//// 		    }
//
//        if (dmin == dmax)
//        {
//            return (false);
//        }
//        
//        if (dmin > dmax)
//        {
//            throw(new RuntimeException("Error during reversible edge shift in Transition Matrix Sampling: "
//                    + "Have reached an inconsistency between elements. dmin > dmax with:"
//                    + "dmin = Math.max(-T.get(i, i), -q * T.get(j, j))"
//                    + dmin+" = Math.max(-"+T.get(i, i)+", -"+q+" * "+T.get(j, j)+")"
//                    + "dmax = Math.min(T.get(i, j), q * T.get(j, i))"
//                    + dmax+" = Math.min("+T.get(i, j)+", "+q+" * "+T.get(j, i)+")"
//                    + "at i = "+i+"   j = "+j));
//        }
//        
//        double d1 = 0, d2 = 0;
//
//        d1 = MathTools.randomDouble(dmin, dmax);
//        d2 = d1 / q;
//
//        double prop = Math.sqrt((((T.get(i, j) - d1) * (T.get(i, j) - d1)) + ((T.get(j, i) - d2) * (T.get(j, i) - d2)))
//                / ((T.get(i, j) * T.get(i, j)) + (T.get(j, i) * T.get(j, i))));
//
//        double pacc = prop
//                * Math.pow((T.get(i, i) + d1) / T.get(i, i), C.get(i, i))
//                * Math.pow((T.get(i, j) - d1) / T.get(i, j), C.get(i, j))
//                * Math.pow((T.get(j, j) + d2) / T.get(j, j), C.get(j, j))
//                * Math.pow((T.get(j, i) - d2) / T.get(j, i), C.get(j, i));
//
//        boolean accept = Math.random() <= pacc;
//
//        /*System.out.println("edge "+accept);
//        System.out.println("   "+pacc);
//        System.out.println("   "+C[i][i]+"\t"+T[i][i]+"\t+"+d1);
//        System.out.println("   "+C[i][j]+"\t"+T[i][j]+"\t-"+d1);
//        System.out.println("   "+C[j][j]+"\t"+T[j][j]+"\t+"+d2);
//        System.out.println("   "+C[j][i]+"\t"+T[j][i]+"\t-"+d2);
//        System.out.println();
//         */
//
//        if (accept)
//        {
//            T.add(i, i, d1);
//            T.add(i, j, -d1);
//            T.add(j, j, d2);
//            T.add(j, i, -d2);
//
//            // numerical corrections:
//            validateElement(i, j);
//            validateElement(i, i);
//            validateElement(j, i);
//            validateElement(j, j);
//            if (Math.random() < 0.0001) // do a row rescaling every 10000 steps
//            {
//                validateRow(i);
//                validateRow(j);
//            }
//
//
//// 			if (C[i][j] < 0 || C[i][i] < 0 || C[j][i] < 0 || C[j][j] < 0)
//// 			    {
//// 				System.out.println("Was successfull. New T");
//// 				System.out.println(T[i][j]+" "+T[i][i]+" "+T[j][i]+" "+T[j][j]);
//// 			    }
//
//        }
//
//        return (accept);
//    }
//
//    public boolean stepNodeShift()
//    {
//        int i = MathTools.randomInt(0, T.rows());
//        while (C.get(i, i) <= 0 || dof[i] < 2)
//        {
//            i = MathTools.randomInt(0, T.rows());
//        }
//
//        double maxTij = 0;
//        for (int j = 0; j < T.columns(); j++)
//        {
//            if (j != i)
//            {
//                if (T.get(i, j) > maxTij)
//                {
//                    maxTij = T.get(i, j);
//                }
//            }
//        }
//        double a = MathTools.randomDouble(0, 1 / (1 - T.get(i, i)));
//
//        double prop = Math.pow(a, dof[i] - 2);
//
//        double pacc = prop * Math.pow((a * T.get(i, i) - a + 1) / T.get(i, i), C.get(i, i))
//                * Math.pow(a, Csum[i] - C.get(i, i));
//
//        boolean accept = Math.random() <= pacc;
//
//        //System.out.println("node "+accept);
//        if (accept)
//        {
//            // update matrix
//            double sum = 0;
//            for (int k = 0; k < T.columns(); k++)
//            {
//                if (k != i)
//                {
//                    T.set(i, k, T.get(i, k) * a);
//                    sum += T.get(i, k);
//                }
//            }
//            T.set(i, i, 1 - sum);
//
//            // update stationary distribution
//            if (Math.random() < 0.99)
//            {
//                sum = 0;
//                for (int k = 0; k < pi.size(); k++)
//                {
//                    if (k != i)
//                    {
//                        pi.set(k, a * pi.get(k) / (pi.get(i) + a * (1 - pi.get(i))));
//                        sum += pi.get(k);
//                    }
//                }
//                pi.set(i, 1 - sum);
//            }
//            // now and then make sure that the right stat dist is computed
//            else
//            {
//                pi = MarkovModel.util.stationaryDistributionRevQuick(T);
//            }
//        }
//        
//        return(accept);
//    }

    @Override
    public void init(IDoubleArray counts)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void init(IDoubleArray counts, IDoubleArray Tinit)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
