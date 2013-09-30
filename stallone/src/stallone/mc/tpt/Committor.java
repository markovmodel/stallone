/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.tpt;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.algebra.*;
import stallone.api.mc.MarkovModel;
import stallone.mc.StationaryDistribution;

/**
 *
 * @author noe
 */
public class Committor
{
    private IDoubleArray T,K; // transition matrix and generator or pseudogenerator matrix
    private IDoubleArray pi; // stationary distribution
    private IIntArray A,B,AB,notAB; // A and B states
    private IDoubleArray qforward=null, qbackward=null;
    
    public Committor(IDoubleArray M, IIntArray _A, IIntArray _B)
    {
        if (MarkovModel.util.isTransitionMatrix(M))
        {
            setTransitionMatrix(M);
        }
        else if (MarkovModel.util.isTransitionMatrix(M))
        {
            setRateMatrix(M);
        }
        else
        {
            throw (new IllegalArgumentException("Trying to construct TPT with a matrix that is neither a transition nor a rate matrix"));
        }

        this.A = _A;
        this.B = _B;
        this.AB = Ints.util.mergeToNew(A,B);
        this.notAB = Ints.util.removeValueToNew(Ints.create.arrayRange(M.rows()), AB);
    }
    
    public Committor(int nstates, IIntArray _A, IIntArray _B)
    {
        this.A = _A;
        this.B = _B;
        this.AB = Ints.util.mergeToNew(A,B);
        this.notAB = Ints.util.removeValueToNew(Ints.create.arrayRange(nstates), AB);
    }
    
    public final void setTransitionMatrix(IDoubleArray _T)
    {
        qforward = null;
        qbackward = null;
        
        this.T = _T;
        
        this.K = _T.copy();
        for (int i=0; i<T.rows(); i++)
            K.set(i,i,K.get(i,i)-1);        
    }
    
    public final void setRateMatrix(IDoubleArray _K)
    {
        qforward = null;
        qbackward = null;

        this.K = _K;
        
        this.T = _K.copy();
        for (int i=0; i<T.rows(); i++)
            this.T.set(i,i,this.T.get(i,i)+1);
    }

    /**
     * Needed for the backward committor. If not given, it will be calculated from the transition or rate matrix.
     * @param pi 
     */
    public void setStationaryDistribution(IDoubleArray _pi)
    {
        qbackward = null;
        this.pi = _pi;
    }
    
    public IDoubleArray forwardCommittor()
    {
        if (qforward != null)
            return(qforward);
        
        IDoubleArray U = K.view(notAB.getArray(), notAB.getArray());

        IDoubleArray v = Doubles.create.array(notAB.size());
        for (int i=0; i<v.size(); i++)
            for (int k=0; k<B.size(); k++)
                v.set(i, v.get(i)-K.get(notAB.get(i),B.get(k)));

        IDoubleArray qI = Algebra.util.solve(U, v);

        qforward = Doubles.create.array(K.rows());
        for (int i=0; i<A.size(); i++)
            qforward.set(A.get(i), 0);
        for (int i=0; i<B.size(); i++)
            qforward.set(B.get(i), 1);
        for (int i=0; i<notAB.size(); i++)
            qforward.set(notAB.get(i), qI.get(i));

        return(qforward);
    }

    public IDoubleArray backwardCommittor()
    {
        if (qbackward != null)
            return(qbackward);

        if (pi == null)
            pi = StationaryDistribution.calculate(T);
        
        IDoubleArray U = Doubles.create.array(notAB.size(),notAB.size());
	for (int i=0; i<U.rows(); i++)
	    for (int j=0; j<U.columns(); j++)
		U.set(i,j, pi.get(notAB.get(j)) * K.get(notAB.get(j),notAB.get(i)) / pi.get(notAB.get(i)));

        IDoubleArray v = Doubles.create.array(notAB.size());
        for (int i=0; i<v.size(); i++)
            for (int k=0; k<A.size(); k++)
                v.set(i, v.get(i) - pi.get(A.get(k))*K.get(A.get(k),notAB.get(i)) / pi.get(notAB.get(i)));

        IDoubleArray qI = Algebra.util.solve(U, v);

        qbackward = Doubles.create.array(K.rows());
        for (int i=0; i<A.size(); i++)
            qbackward.set(A.get(i), 1);
        for (int i=0; i<B.size(); i++)
            qbackward.set(B.get(i), 0);
        for (int i=0; i<notAB.size(); i++)
            qbackward.set(notAB.get(i), qI.get(i));

        return(qbackward);
    }

    
}
