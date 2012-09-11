/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_tests;

import java.io.IOException;
import static stallone.api.API.*;
import stallone.api.datasequence.IDataReader;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;

/**
 *
 * @author noe
 */
public class XtcDistanceCorrelationMatrix
{
    private int natoms;
    private IIntArray indexSet;
    private int nSelectedAtoms;
    
    private double[][] tmpDistanceMatrix;
    private double[] tmpDistanceVector;
    
    public XtcDistanceCorrelationMatrix(int _natoms, IIntArray _indexSet)
    {
        this.natoms = _natoms;
        this.indexSet = _indexSet;
        this.nSelectedAtoms = _indexSet.size();
        
        this.tmpDistanceMatrix = new double[nSelectedAtoms][nSelectedAtoms];
        this.tmpDistanceVector = new double[(nSelectedAtoms*(nSelectedAtoms-1))/2];
    }
    
    public double[][] distanceMatrix(IDoubleArray X)
    {
        double dx, dy, dz;

        for (int i = 0; i < nSelectedAtoms; i++)
        {
            for (int j = 0; j < nSelectedAtoms; j++)
            {
                dx = X.get(i, 0) - X.get(j, 0);
                dy = X.get(i, 1) - X.get(j, 1);
                dz = X.get(i, 2) - X.get(j, 2);
                tmpDistanceMatrix[i][j] = Math.sqrt(dx * dx + dy * dy + dz * dz);
            }
        }

        return tmpDistanceMatrix;
    }
    
    public double[] distanceVector(IDoubleArray X)
    {
        double[][] M = distanceMatrix(X);
        int k=0;
        for (int i = 0; i < nSelectedAtoms-1; i++)
            for (int j = i+1; j < nSelectedAtoms; j++)
                tmpDistanceVector[k++] = M[i][j];
        return tmpDistanceVector;
    }

    public static void main(String[] args) throws IOException
    {
        String filename = args[0];
        int indexStep = str.toInt(args[1]);
        int tau = str.toInt(args[2]);
        String outname = args[3];

        // open traj
        IDataReader reader1 = dataNew.dataSequenceLoader(args[0]);
        IDataReader reader2 = dataNew.dataSequenceLoader(args[0]);
        int nframes = reader1.size();
        int ndimMol = reader1.dimension();

        // index set
        IIntArray I = intsNew.arrayRange(0, ndimMol, indexStep);
        int ndimDist = I.size();

        double[][] C = new double[ndimDist][ndimDist];
        double[] means = new double[ndimDist];

        /*
        // iterate file
        for (int t = 0; t < nframes - tau; t++)
        {
            IDoubleArray X1 = reader1.get(t);
            double[][] D1 = distanceMatrix(X1, I);
            IDoubleArray X2 = reader2.get(t + tau);
            double[][] D2 = distanceMatrix(X2, I);

            for (int i = 0; i < ndim; i++)
            {
                double x = X1.get(i);
                means[i] += x;

                for (int j = 0; j < ndim; j++)
                {
                    double y = X2.get(j);
                    C[i][j] += x * y;
                }
            }
        }

        // normalize
        for (int i = 0; i < ndim; i++)
        {
            means[i] /= nframes;

            for (int j = 0; j < ndim; j++)
            {
                C[i][j] /= nframes - tau;
            }
        }

        // remove mean
        for (int i = 0; i < ndim; i++)
        {
            for (int j = 0; j < ndim; j++)
            {
                C[i][j] -= means[i] * means[j];
            }
        }

        io.writeString(outname, doubleArrays.toString(C, " ", "\n"));
        */
    }
}
