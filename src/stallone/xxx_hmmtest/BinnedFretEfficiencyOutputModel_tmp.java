/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_hmmtest;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.function.IParametricFunction;
import stallone.api.stat.IParameterEstimator;

/**
 *
 * @author noe
 */
public class BinnedFretEfficiencyOutputModel_tmp implements IParametricFunction, IParameterEstimator
{
    // pre-calculated for speed-up

    private double E;
    // model parameters including background noise
    private double pBGgreen, pBGred, pMol;

    public BinnedFretEfficiencyOutputModel_tmp(double _E)
    {
        E = _E;
    }

    public BinnedFretEfficiencyOutputModel_tmp(double pBackgroundGreen, double pBackgroundRed, double pMolecule)
    {
    }

    @Override
    public IDoubleArray getParameters()
    {
        return Doubles.create.arrayFrom(E);
    }

    @Override
    public void setParameters(IDoubleArray par)
    {
        E = par.get(0);
    }

    @Override
    public int getNumberOfVariables()
    {
        return (1);
    }

    private double logFactorial(int n)
    {
        double logfac = 0;

        for (int i = 1; i <= n; i++)
        {
            logfac += Math.log(i);
        }

        return (logfac);
    }

    private double logBinomial(int n, int k)
    {
        if (n < k)
        {
            throw (new IllegalArgumentException("Trying to calculate binomial coefficient with n<k"));
        }

        double logBin = 0;

        logBin += logFactorial(n);
        logBin -= logFactorial(n - k);
        logBin -= logFactorial(k);

        return (logBin);
    }

    public double pQBinomial(int nGreen, int nRed)
    {
        int n = nGreen + nRed;

        // funny cases
        if ((E == 0 && nRed > 0) || (E == 1 && nGreen > 0))
        {
            return (0);
        }

        double logP = 0;
        logP += logBinomial(n, nRed);
        if (E != 0)
        {
            logP += nRed * Math.log(E);
        }
        if (E != 1)
        {
            logP += nGreen * Math.log(1 - E);
        }

        return (Math.exp(logP));
    }

    public double pQbackground(int nGreen, int nRed, double pBGgreen, double pBGred, double pIMol)
    {
        double p = 0;
        double logpBGAcc = Math.log(pBGred);
        double logpBGDon = Math.log(pBGgreen);
        double logpIMol = Math.log(pIMol);
        double logE = Math.log(E);
        double log1mE = Math.log(1 - E);

        for (int nAbg = 0; nAbg <= nRed; nAbg++)
        {
            for (int nDbg = 0; nDbg <= nGreen; nDbg++)
            {
                int nRedMol = nRed - nAbg;
                int nGreenMol = nGreen - nDbg;

                if (!((E == 0 && nRedMol > 0) // E=0 but still acceptor photons? Impossible
                        || (E == 1 && nGreenMol > 0))) // E=1 but still donor photons? Impossible
                {
                    double logP = 0;

                    if (pBGred > 0) // if p=0 nothing happens because we multiply with 1
                    {
                        logP += nAbg * logpBGAcc;
                    }
                    logP -= logFactorial(nAbg);

                    if (pIMol > 0) // if p=0 nothing happens because we multiply with 1
                    {
                        logP += nRedMol * logpIMol;
                    }
                    logP -= logFactorial(nRedMol);

                    if (E > 0) // if E=0 nothing happens because we multiply with 1
                    {
                        logP += nRedMol * logE;
                    }

                    if (pBGgreen != 0) // if p=0 nothing happens because we multiply with 1
                    {
                        logP += nDbg * logpBGDon;
                    }
                    logP -= logFactorial(nDbg);

                    if (pIMol > 0) // if p=0 nothing happens because we multiply with 1
                    {
                        logP += nGreenMol * logpIMol;
                    }
                    logP -= logFactorial(nGreenMol);

                    if (E < 1) // if E=1 nothing happens because we multiply with 1
                    {
                        logP += nGreenMol * log1mE;
                    }

                    p += Math.exp(logP);
                }
            }
        }

        return (p);
    }

    /**
     *
     * @param x Either 2 or 5 coordinates. Required: (nD,nA): number of green
     * (donor) and number of red (acceptor) photons. Optional: (pBGD,pBGA,pMol):
     * probability or rate of green (donor) background, red (acceptor)
     * background, and molecule intensity.
     * @return probability density
     */
    @Override
    public double f(double... x)
    {
        if (x.length == 2)
        {
            return pQBinomial((int) x[0], (int) x[1]);
        }
        else if (x.length == 5)
        {
            return pQbackground((int) x[0], (int) x[1], x[2], x[3], x[4]);
        }
        else
        {
            throw new IllegalArgumentException("Illegal number of arguments: expecting 2 or 5.");
        }
    }

    /**
     *
     * @param x (nD,nA): number of green (donor) and number of red (acceptor)
     * photons.
     * @return probability density
     */
    @Override
    public double f(IDoubleArray x)
    {
        if (x.size() == 2)
        {
            return pQBinomial((int) x.get(0), (int) x.get(1));
        }
        else if (x.size() == 5)
        {
            return pQbackground((int) x.get(0), (int) x.get(1), x.get(2), x.get(3), x.get(4));
        }
        else
        {
            throw new IllegalArgumentException("Illegal number of arguments: expecting 2 or 5.");
        }
    }

    @Override
    public IDoubleArray estimate(IDataSequence data)
    {
        initialize();
        addToEstimate(data);
        return getEstimate();
    }

    @Override
    public IDoubleArray estimate(IDataSequence data, IDoubleArray weights)
    {
        initialize();
        addToEstimate(data,weights);
        return getEstimate();
    }
    private double sumRunning = 0, totalWeightRunning = 0;

    @Override
    public void initialize()
    {
        sumRunning = 0;
        totalWeightRunning = 0;
    }

    @Override
    public void initialize(IDoubleArray initPar)
    {
        sumRunning = 0;
        totalWeightRunning = 0;
    }

    @Override
    public void addToEstimate(IDataSequence data)
    {
        IDoubleArray weights = Doubles.create.array(data.size());
        Doubles.util.fill(weights, 1.0);
        addToEstimate(data, weights);
    }

    @Override
    public void addToEstimate(IDataSequence data, IDoubleArray weights)
    {
        if (data.dimension() == 2)
        {
            double nGtot=0, nRtot=0;
            IDoubleArray x;
            double w,wtot=0;
            
            //System.out.println("Adding with dimension 2");
            for (int t = 0; t<data.size(); t++)
            {
                w = weights.get(t);                
                wtot += w;
                x = data.get(t);
                nGtot += w*x.get(0);
                nRtot += w*x.get(1);
            }

            sumRunning += wtot * nRtot / (nGtot + nRtot);
            totalWeightRunning += wtot;
        }
        else if (data.dimension() == 5)
        {
            // Poisson rates
            double pBGgreen_traj=data.get(0).get(2);
                    double pBGred_traj=data.get(0).get(3);
                    double pMol_traj=data.get(0).get(4);
            
            // total observed counts
            double nGtot = 0, nRtot = 0, wtot = 0;
            // current counts and weights
            double nGreen, nRed, w;
            // current data
            IDoubleArray arr;

            for (int t = 0; t < data.size(); t++)
            {
                arr = data.get(t);
                
                w = weights.get(t);

                nGtot += w * arr.get(0);
                nRtot += w * arr.get(1);
                wtot += w;
            }

            if ((nGtot + nRtot) > 0)
            {
                double nsRed = pBGred_traj / pMol_traj;
                double nsGreen = pBGgreen_traj / pMol_traj;
                double E_traj = (nRtot / (nRtot + nGtot)) * (1 + nsRed + nsGreen) - nsRed;

                // bound Efficiency - be careful this is not a likelihood maximization!!
                E_traj = Math.min(Math.max(0, E_traj), 1);
                
                sumRunning += E_traj * wtot;
                totalWeightRunning += wtot;
            }
        }
        else
        {
            throw new IllegalArgumentException("Illegal number of arguments: expecting 2 or 5.");
        }
    }

    @Override
    public IDoubleArray getEstimate()
    {
        double mean = sumRunning / totalWeightRunning;
        return Doubles.create.arrayFrom(mean);
    }

    @Override
    public BinnedFretEfficiencyOutputModel_tmp copy()
    {
        return (new BinnedFretEfficiencyOutputModel_tmp(E));
    }
}
