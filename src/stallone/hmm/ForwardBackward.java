/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm;

import stallone.api.hmm.ParameterEstimationException;
import stallone.api.doubles.DoublesPrimitive;


/**
 * Estimates the hidden variables pertaining to one trajectory
 * @author noe
 */
public class ForwardBackward
{
    private HMMForwardModel model;

    public ForwardBackward(HMMForwardModel _model)
    {
        this.model = _model;
    }

    /**
        Calculates the hidden trajectory and writes into the supplied object.
     */
    public void computePath(int itraj, HMMHiddenVariables hidden)
            throws ParameterEstimationException
    {
        // output probabilities
        for (int t = 0; t < hidden.size(); t++)
        {
            for (int j = 0; j < model.getNStates(); j++)
            {
                hidden.setPout(t, j, model.getPout(itraj, t, j));
                //System.out.println("  pout "+t+" "+j+": "+model.getPout(itraj, t, j));
            }
            if (!hidden.checkPout(t))
            {
                throw(new ParameterEstimationException(
                        " \n\n======== Parameter Estimation Exception ========\n"
                        + "At trajectory "+itraj+", timestep "+t+"\n"
                        + "Observation = "+model.getObs(itraj, t)+"\n\n"
                        + "HMM parameters: \n"+model.getParameters()+"\n"
                        + "pout = "+DoublesPrimitive.util.toString(hidden.getPout(t),"\t")+"\n"
                        + "cannot normalize pout. Check IHMMModel implementation.\n"
                        + "================================================ \n"
                        ));
            }
        }

        // forward variables
        for (int j = 0; j < model.getNStates(); j++)
        {
            hidden.setAlpha(0, j, model.getP0(itraj, j) * hidden.getPout(0, j));
        }

        try
        {
            hidden.normalizeAlpha(0);
        }
        catch(RuntimeException e)
        {
            System.out.println("CAUGHT: "+e);
            System.out.println(" pout = "+DoublesPrimitive.util.toString(hidden.getPout(0),"\t"));
        }

        for (int t = 1; t < hidden.size(); t++)
        {
            for (int j = 0; j < model.getNStates(); j++)
            {
                hidden.setAlpha(t, j, 0);
                for (int i = 0; i < model.getNStates(); i++)
                {
                    hidden.addAlpha(t, j, hidden.getAlpha(t - 1, i) * model.getPtrans(itraj, t - 1, i, j) * hidden.getPout(t, j));
                }
            }

            try
            {
                hidden.normalizeAlpha(t);
            }
            catch(RuntimeException e)
            {
                System.out.println("CAUGHT: "+e);
                System.out.println(" pout = "+DoublesPrimitive.util.toString(hidden.getPout(0),"\t"));
            }
        }

        // backward variables
        for (int i = 0; i < model.getNStates(); i++)
        {
            hidden.setBeta(hidden.size() - 1, i, 1.0 / (double) model.getNStates());
        }
        hidden.normalizeBeta(hidden.size() - 1);

        //java.util.Arrays.fill(beta[beta.length-1], 1);
        for (int t = hidden.size() - 2; t >= 0; t--)
        {
            for (int i = 0; i < model.getNStates(); i++)
            {
                hidden.setBeta(t, i, 0);
                for (int j = 0; j < model.getNStates(); j++)
                {
                    hidden.addBeta(t, i, hidden.getBeta(t + 1, j) * model.getPtrans(itraj, t, i, j) * hidden.getPout(t + 1, j));
                }
            }

            hidden.normalizeBeta(t);
        }

        // gamma
        hidden.updateGamma();

//        for (int t=0; t<hidden.size(); t++)
//        {
//            for (int i=0; i<model.getNStates(); i++)
//            {
//                    if (t % 100 == 0)
//                        System.out.println(t+"\t"+i+"\t"+hidden.getPout(t,i)+"\t"+hidden.getAlpha(t, i)+"\t"+hidden.getBeta(t, i)+"\t"+hidden.getGamma(t, i));
//            }
//        }
    }

}
