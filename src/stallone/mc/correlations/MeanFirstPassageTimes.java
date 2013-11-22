/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.mc.correlations;

import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.ints.IIntList;
import stallone.api.ints.Ints;

/**
 *
 * @author noe
 */
public final class MeanFirstPassageTimes
{
    IDoubleArray T;
    IIntArray target;
    IDoubleArray mfpts;

    public MeanFirstPassageTimes(IDoubleArray _T, IIntArray _target)
    {
        this.T = _T;
        this.target = _target;
        calculate();
    }

    public static IDoubleArray mfpt(IDoubleArray _T, IIntArray _target)
    {
        MeanFirstPassageTimes M = new MeanFirstPassageTimes(_T,_target);
        return(M.getMfpts());
    }

    public void setT(IDoubleArray _T)
    {
        this.T=_T;
    }

    public void setTarget(IIntArray _target)
    {
        this.target = _target;
    }

    public void calculate()
    {
        IDoubleArray A = T.copy();

        // nontarget rows
        for (int i=0; i<A.rows(); i++)
            {
                A.set(i, i, T.get(i,i)-1);
            }

        // target rows
        for (int i=0; i<target.size(); i++)
            {
                for (int j=0; j<A.columns(); j++)
                    {
                        A.set(target.get(i), j, 0);
                    }
                A.set(target.get(i), target.get(i), 1);
            }

        // identify 0-columns
        IIntList zeros = Ints.create.list(0);
        for (int j=0; j<A.columns(); j++)
            {
                boolean colzero = true;
                for (int i=0; i<A.rows(); i++)
                    if (A.get(i,j) != 0)
                        colzero = false;
                if (colzero)
                    zeros.append(j);
            }

        for (int j=0; j<A.columns(); j++)
            {
                if (!Ints.util.contains(target, j) && A.get(j,j) == 0)
                    zeros.append(j);
            }

        IIntArray nonzeros = Ints.util.removeValueToNew(Ints.create.listRange(0, A.rows()), zeros);

        // clean matrix
        IDoubleArray Aclean = A.view(nonzeros.getArray(), nonzeros.getArray());

        // rhs
        IDoubleArray b = Doubles.create.array(A.rows());
        for (int i=0; i<b.size(); i++)
            b.set(i, -1);
        for (int i=0; i<target.size(); i++)
            b.set(target.get(i), 0);

        // clean vector
        IDoubleArray bclean = Doubles.util.subToNew(b, nonzeros);
        // Attention: we had done the following here... does this make sense??
        // int[] tmp = {0};
        // DoubleMatrix2D Bclean = B.viewSelection(nonzeros, tmp);

        IDoubleArray X = Algebra.util.solve(Aclean, bclean);

        // reshuffle x into full vector
        this.mfpts = Doubles.create.array(T.rows(), Double.NaN);
        for (int i=0; i<nonzeros.size(); i++)
            mfpts.set(nonzeros.get(i), X.get(i));
    }

    public IDoubleArray getMfpts()
    {
        return(mfpts);
    }
}
