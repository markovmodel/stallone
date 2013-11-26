/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.hmm.pmm;

import static stallone.api.API.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import stallone.api.API;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.cluster.IClustering;
import stallone.api.doubles.IDoubleArray;
import stallone.util.CommandLineParser;

/**
 *
 * @author noe
 */
public class AssignEigenvectorsCmd
{

    private String inDir;
    private int reflag;
    private double minOverlap;
    private IDoubleArray pibig0, Rbig0;

    public boolean parseArguments(String[] args)
            throws FileNotFoundException, IOException
    {
        CommandLineParser parser = new CommandLineParser();
        // input
        parser.addStringCommand("i", true);

        // reference lag time
        parser.addIntCommand("reflag", true);
        parser.addDoubleCommand("minoverlap", true);

        if (!parser.parse(args))
        {
            return false;
        }

        inDir = parser.getString("i");
        reflag = parser.getInt("reflag");
        minOverlap = parser.getDouble("minoverlap");

        return true;
    }

    public void setReference(IDoubleArray _pibig0, IDoubleArray _Rbig0)
    {
        pibig0 = _pibig0;
        Rbig0 = _Rbig0;
    }

    public double overlap(IDoubleArray ri, int iref, IDoubleArray pibig)
    {
        // normalize reference eigenvector
        IDoubleArray ri0 = Rbig0.viewColumn(iref);
        alg.scale(1.0 / Math.sqrt(alg.dot(ri0, ri0, pibig0)), ri0);

        // normalize this eigenvector
        //alg.scale(1.0 / Math.sqrt(alg.dot(ri, ri, pibig0)), ri);
        alg.scale(1.0 / Math.sqrt(alg.dot(ri, ri, pibig)), ri);

        // symmetric weight
        IDoubleArray pisym = pibig0.copy();
        for (int i=0; i<pisym.size(); i++)
            pisym.set(i, Math.sqrt(pibig0.get(i)*pibig.get(i)));
        
        //return Math.abs(alg.dot(ri0, ri, pibig0));
        return Math.abs(alg.dot(ri0, ri, pisym));
    }

    public static String getUsageString()
    {
        return //EMMACitations.getCurrentVersionOutput()
                ""
                + "\n"
                + "=======================================\n"
                + " AssignEigenvectorsCmd"
                + "\n"
                + "=======================================\n"
                + "Usage: " + "\n"
                + "\n"
                + "Mandatory input and output options: " + "\n"
                + " -i <input-directory>+\n"
                + "\n"
                + " -reflag <reference-lagtime>" + "\n"
                + " -minoverlap <minimum-overlap>" + "\n"
                ;

    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException
    {        
        // if no input, print usage String
        if (args.length == 0)
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        AssignEigenvectorsCmd cmd = new AssignEigenvectorsCmd();
        // Parse input. If incorrect, say what's wrong and print usage String
        if (!cmd.parseArguments(args))
        {
            System.out.println(getUsageString());
            System.exit(0);
        }

        // read lag times
        IDoubleArray lagFile = doublesNew.fromFile(cmd.inDir + "/hmm-its.dat");
        int[] lagtimes = intArrays.from(lagFile.getColumn(0));

        // reference
        IDoubleArray pibig0 = doublesNew.fromFile(cmd.inDir + "/hmm-pibig-lag" + cmd.reflag + ".dat");
        IDoubleArray Rbig0 = doublesNew.fromFile(cmd.inDir + "/hmm-Rbig-lag" + cmd.reflag + ".dat");
        cmd.setReference(pibig0, Rbig0);

        // read matrices and Chi
        for (int tau : lagtimes)
        {
            IDoubleArray TC = doublesNew.fromFile(cmd.inDir + "/hmm-TC-lag" + tau + ".dat");
            IDoubleArray timescales = msm.timescales(TC, tau);
            IDoubleArray pibig = doublesNew.fromFile(cmd.inDir + "/hmm-pibig-lag" + tau + ".dat");
            IDoubleArray Rbig = doublesNew.fromFile(cmd.inDir + "/hmm-Rbig-lag" + tau + ".dat");

            System.out.print(tau + "\t");
            for (int i = 1; i < Rbig.columns(); i++)
            {
                IDoubleArray ri = Rbig.viewColumn(i);

                double max = 0;
                int argmax = i;
                for (int j = 1; j < Rbig.columns(); j++)
                {
                    double o = cmd.overlap(ri, j, pibig);
                    if (o > max)
                    {
                        max = o;
                        argmax = j;
                    }
                }
                
                if (max > cmd.minOverlap)
                {
                    System.out.print(timescales.get(argmax-1)+"\t");
                }
                else
                {
                    System.out.print(0+"\t");
                }
                //System.out.print(max + "("+argmax+")" + "\t");
            }
            System.out.println();
        }

    }
}
