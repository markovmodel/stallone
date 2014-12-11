/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static stallone.api.API.*;
import stallone.api.algebra.IEigenvalueDecomposition;
import stallone.api.algebra.IEigenvalueSolver;
import stallone.api.cluster.IClustering;
import stallone.api.datasequence.IDataSequenceLoader;
import stallone.api.datasequence.IDataReader;
import stallone.api.discretization.IDiscretization;
import stallone.api.doubles.IDoubleArray;
import stallone.api.hmm.IHMM;
import stallone.api.hmm.ParameterEstimationException;
import stallone.api.ints.IIntArray;
import stallone.api.mc.ITransitionMatrixSampler;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class Test
{
    public static void main(String[] args) 
            throws FileNotFoundException, IOException, ParameterEstimationException
    {                
        List<String> names = new ArrayList();
        names.add("/Users/noe/data/software_projects/TestProject/src/test/dtraj_stride10.dat");
        List<IIntArray> dtrajs = intseq.loadIntSequences(names);

        IHMM pmm = hmm.pmm(dtrajs, 2, 10);
    }
}
