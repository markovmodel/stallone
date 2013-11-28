/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.hmm;

import static stallone.api.API.*;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IHMMParameters;
import stallone.api.io.IO;
import stallone.api.mc.MarkovModel;
import static stallone.doubles.DoubleArrayTest.*;

/**
 * This is a basic implementation of IHMMParameters that may be used. However,
 * the user can also choose to implement his own IHMMParameters.
 * This implementation contains transition matrix estimation and stationary
 * distribution estimation.
 * @author noe
 */
public class HMMParameters implements IHMMParameters
{
    private int nstates;
    private IDoubleArray T;
    private IDoubleArray p0;
    private boolean isReversible = false;
    private boolean isStationary = true;

    private int nparameters;
    private IDoubleArray[] parOut;

    public HMMParameters(int _nstates, boolean _isReversible, boolean _isStationary)
    {
        this.nstates = _nstates;
        this.isReversible = _isReversible;
        this.isStationary = _isStationary;
        this.parOut = new IDoubleArray[_nstates];
    }

    public HMMParameters(IDoubleArray _T, IDoubleArray _p0, IDoubleArray[] _parOut, boolean _isReversible, boolean _isStationary)
    {
        this.T = _T;
        this.nstates = T.rows();
        this.p0 = _p0;
        this.isReversible = _isReversible;
        this.isStationary = _isStationary;

        this.parOut = _parOut;

        // test input
        assertSquare(_T);
        assertOrder(_p0,1);
        assertSize(_p0,nstates);

        if (_parOut.length != nstates)
            throw(new IllegalArgumentException("Provided with "+_parOut.length+" parameter sets but expected "+nstates));
        nparameters = _parOut[0].size();
        for (IDoubleArray d: _parOut)
            if (d.size() != nparameters)
                throw(new IllegalArgumentException("Parameter sets don't have equal dimensions"));
    }

    /**
     * Creates deep copy
     * @return
     */
    @Override
    public IHMMParameters copy()
    {
        IDoubleArray[] newOut = new IDoubleArray[parOut.length];
        for (int i=0; i<newOut.length; i++)
            newOut[i] = parOut[i].copy();
        return(new HMMParameters(T.copy(), p0.copy(), newOut, isReversible, isStationary));
    }


    @Override
    public String toString()
    {
        StringBuilder strb = new StringBuilder();
        strb.append("T = \n");
        strb.append(Doubles.util.toString(T,"\t","\n")+"\n");
        strb.append("pi = "+Doubles.util.toString(p0,", ")+"\n");
        for (IDoubleArray d: parOut)
            strb.append("par = "+Doubles.util.toString(d,", ")+"\n");

        return(strb.toString());
    }

    @Override
    public int getNStates()
    {
        return nstates;
    }

    @Override
    public IDoubleArray getOutputParameters(int state)
    {
        return parOut[state];
    }
    
    @Override
    public IDoubleArray getOutputParameterMatrix()
    {
        double[][] res = new double[nstates][];
        for (int i=0; i<res.length; i++)
            res[i] = parOut[i].getArray();
        return doublesNew.array(res);
    }

    @Override
    public void setOutputParameters(int state, IDoubleArray par)
    {
        parOut[state] = par;
    }

    @Override
    public IDoubleArray getTransitionMatrix()
    {
        return T;
    }

    @Override
    public void setTransitionMatrix(IDoubleArray _T)
    {
        T = _T;
        if (isStationary)
        {
            p0 = MarkovModel.util.stationaryDistribution(T);
        }
        if (isReversible)
        {
            if (!MarkovModel.util.isReversible(T))
                IO.util.error("HMM Parameters are required to be reversible, but trying to set non-reversible transition matrix: \n"+Doubles.util.toString(_T,"\t","\n")+"\n");
        }
    }

    @Override
    public IDoubleArray getInitialDistribution()
    {
        return p0;
    }

    @Override
    public void setInitialDistribution(IDoubleArray _p0)
    {
        if (isStationary)
        {
            IO.util.error("HMM Parameters are set stationary, but trying to set initial distribution. This is prohibited.");
        }
        p0 = _p0;
    }

    @Override
    public boolean isReversible()
    {
        return isReversible;
    }

    @Override
    public boolean isStationary()
    {
        return isStationary;
    }

}
