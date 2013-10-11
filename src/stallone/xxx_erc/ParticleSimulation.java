/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_erc;

import java.io.IOException;
import java.io.PrintStream;
import stallone.api.datasequence.IDataWriter;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.dynamics.Dynamics;
import stallone.datasequence.io.DcdWriter;
import stallone.dynamics.IIntegratorThermostatted;
import stallone.util.StringTools;

/**
 *
 * @author noe
 */
public class ParticleSimulation
{
    public static IDoubleArray addZeroColumn(IDoubleArray arr)
    {
        IDoubleArray res = Doubles.create.array(arr.rows(), 3);
        for (int i=0; i<res.rows(); i++)
        {
            res.set(i,0,arr.get(i,0));
            res.set(i,1,arr.get(i,1));
        }
        return res;
    }

    public static void writeXYZ(IDoubleArray x0, String filename)
            throws IOException
    {
        PrintStream out = new PrintStream(filename);
        out.println(x0.rows());
        out.println("PARTICLE 2D");
        for (int i=0; i<x0.rows(); i++)
        {
            out.print(" C");
            for (int d = 0; d<3; d++)
                out.print(StringTools.flushRight(StringTools.toPrecision(x0.get(i,d), 4, 6), 10));
            out.println();
        }
        out.close();
    }
    
    public static void main(String[] args)
            throws IOException
    {
        int N = 10;
        IDoubleArray masses = Doubles.create.array(2*N, 1.0);
        IDoubleArray sizes = Doubles.create.array(N, 10.0);
        SoftParticleSystem2D model = new SoftParticleSystem2D(100.0, N, sizes, 1.0);

        
        double dt = 0.01;
        double gamma = 1;
        double kT = 1;
        IIntegratorThermostatted integrator = Dynamics.create.brownianDynamicsEuler(model, masses, dt, gamma, kT);
        integrator.setX(model.getCoordinates());

        int nsteps = 10000;
        int nwrite = 10;
        int nburnin = 1000;
        int nout = (nsteps-nburnin)/nwrite-1;

        //IDataWriter output = DataSequence.create.createConsoleDataOutput(2*N);
        String outdir = "/Users/noe/data/temp/particle2d/";
        writeXYZ(addZeroColumn(model.getCoordinates()), outdir+"tmp.xyz");
        IDataWriter output = new DcdWriter(outdir+"tmp.dcd", nout, 3*N);
        
        for (int i=0; i<nsteps; i++)
        {
            integrator.step();
            if (i % nwrite == 0 && i > nburnin)
            {
                model.restrictToBox();
                output.add(addZeroColumn(model.getCoordinates()));
                //output.add(model.getCoordinates());
            }
        }
        //Dynamics.util.run(model.getCoordinates(), integrator, output, 10000, 10);
        
        output.close();
    }
}
