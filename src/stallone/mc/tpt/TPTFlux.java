/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.tpt;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.Ints;
import stallone.api.ints.IIntArray;
import stallone.api.algebra.*;
import stallone.api.mc.MarkovModel;
import stallone.api.mc.tpt.ITPTFlux;
import stallone.mc.StationaryDistribution;


/**
 *
 * @author noe
 */
public class TPTFlux implements ITPTFlux
{
    private IIntArray A,B;
    private Committor committor;
    private StationaryDistribution statdist;
    private IDoubleArray P; // propagator or generator
    private IDoubleArray pi;
    private IDoubleArray qforward, qbackward;
    private IDoubleArray flux, netflux;
    private double totalflux, kAB;
    
    /**
     * Constructs a TPT object from the rate- or transition matrix M from set A to set B
     * @param M the transition or rate matrix
     * @param _A
     * @param _B 
     */
    public TPTFlux(IDoubleArray M, IIntArray _A, IIntArray _B)
    {
        this.A = _A;
        this.B = _B;
        this.committor = new Committor(M.rows(),A,B);
        this.statdist = new StationaryDistribution();

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
        
    }
    
    public final void setTransitionMatrix(IDoubleArray _T)
    {
        P = _T;
        this.committor.setTransitionMatrix(_T);
        this.statdist.setT(_T);
    }

    public final void setRateMatrix(IDoubleArray _K)
    {
        P = _K;
        this.committor.setRateMatrix(_K);
        this.statdist.setK(_K);
    }
    
    /**
     * optional
     * @param pi 
     */
    public void setStationaryDistribution(IDoubleArray _pi)
    {
        this.pi = Doubles.create.array(_pi.getArray());
    }
    
    public void calculate()
    {
        // stationary distribution if necessary
        if (pi == null)
            pi = statdist.calculate();
        
        // committor
        qbackward = committor.backwardCommittor();
        qforward = committor.forwardCommittor();
        
        // flux
	this.flux = P.create(P.rows(), P.columns());
        for (IDoubleIterator it = P.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
            if (!Ints.util.contains(B,i) && !Ints.util.contains(A,j))
		flux.set(i,j, pi.get(i) * qbackward.get(i) * P.get(i,j) * qforward.get(j));
        }

        // net flux
	this.netflux = P.create(P.rows(), P.columns());
        for (IDoubleIterator it = P.nonzeroIterator(); it.hasNext(); it.advance())
        {
            int i = it.row();
            int j = it.column();
		if (flux.get(i,j) > flux.get(j,i))
		    netflux.set(i,j, flux.get(i,j) - flux.get(j,i));
        }        
        
        // total flux
        totalflux = 0;
        for (int i=0; i<A.size(); i++)
            totalflux += Doubles.util.sum(flux.viewRow(i));
        
        // kAB
        kAB = totalflux / Doubles.util.sum(Algebra.util.multiplyElementsToNew(pi, qbackward));
    }
    
    public IDoubleArray getStationaryDistribution()
    {
        return(pi);
    }
    
    public IDoubleArray getBackwardCommittor()
    {
        return(qbackward);
    }

    public IDoubleArray getForwardCommittor()
    {
        return(qforward);
    }

    public IDoubleArray getFlux()
    {
        return(flux);
    }

    public IDoubleArray getNetFlux()
    {
        return(netflux);
    }
    
    public double getTotalFlux()
    {
        return(totalflux);
    }
    
    public double getRate()
    {
        return(kAB);
    }
}
