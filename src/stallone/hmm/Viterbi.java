/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import static stallone.api.API.*;

import java.util.ArrayList;
import java.util.List;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class Viterbi
{
    private IHMMForwardModel forwardModel;
    private ArrayList<IIntArray> paths = new ArrayList();
    
    public Viterbi(IHMMForwardModel _forwardModel)
    {
        this.forwardModel = _forwardModel;
        for (int i=0; i<_forwardModel.getNObs(); i++)
            paths.add(this.calculate(i));
    }
    
    private IIntArray calculate(int itraj)
    {
        int ntime = forwardModel.getNObs(itraj);
        //System.out.println("traj = "+itraj+"  ntime = "+ntime);
        int nstates = forwardModel.getNStates();
        
        double[][] T1 = new double[ntime][nstates];
        int[][] ptr = new int[ntime][nstates];
        
        // first timestep
        for (int s=0; s<nstates; s++)
        {
            T1[0][s] = forwardModel.getP0(itraj, s) * forwardModel.getPout(itraj, 0, s);
        }

        // next timesteps
        double pmax;
        int kmax;
        for (int t=1; t<ntime; t++)
        {
            for (int s=0; s<nstates; s++)
            {
                pmax = 0;
                kmax = 0;
                for (int k=0; k<nstates; k++)
                {
                    double p = T1[t-1][k] * forwardModel.getPtrans(itraj, t-1, k, s);
                    if (p > pmax)
                    {
                        pmax = p;
                        kmax = k;
                    }
                }
                T1[t][s] = pmax * forwardModel.getPout(itraj, t, s);
                ptr[t][s] = kmax;
            }
            // renormalize
            T1[t] = doubleArrays.multiply(1.0/doubleArrays.sum(T1[t]), T1[t]);

            /*doubleArrays.print(T1[t]," ");
            System.out.println();
            intArrays.print(ptr[t]," ");
            System.out.println();
            */
        }
        //System.exit(0);
        IIntArray path = intsNew.array(ntime);
        path.set(ntime-1, doubleArrays.maxIndex(T1[ntime-1]));
        for (int t=ntime-2; t>=0; t--)
        {
            path.set(t, ptr[t+1][path.get(t+1)]);
        }
        
        return path;
    }
    
    
    public IIntArray getPath(int i)
    {
        return paths.get(i);
    }
    
    public List<IIntArray> getPaths()
    {
        return paths;
    }
}
