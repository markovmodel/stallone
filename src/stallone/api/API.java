/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api;

import stallone.api.doubles.DoubleUtilities;
import stallone.api.doubles.DoubleFactory;
import stallone.api.strings.StringUtilities;
import stallone.api.strings.StringFactory;
import stallone.api.ints.IntFactory;
import stallone.api.ints.IntUtilities;
import stallone.api.datasequence.DataSequenceUtilities;
import stallone.api.datasequence.DataSequenceFactory;
import stallone.api.algebra.*;
import stallone.api.cluster.*;
import stallone.api.complex.ComplexFactory;
import stallone.api.complex.ComplexUtilities;
import stallone.api.discretization.DiscretizationFactory;
import stallone.api.discretization.DiscretizationUtilities;
import stallone.api.dynamics.DynamicsFactory;
import stallone.api.dynamics.DynamicsUtilities;
import stallone.api.io.*;
import stallone.api.function.*;
import stallone.api.graph.*;
import stallone.api.hmm.HMMFactory;
import stallone.api.hmm.HMMUtilities;
import stallone.api.intsequence.IntSequenceFactory;
import stallone.api.intsequence.IntSequenceUtilities;
import stallone.api.mc.*;
import stallone.api.potential.PotentialFactory;
import stallone.api.potential.PotentialUtilities;
import stallone.api.stat.*;
import stallone.doubles.PrimitiveDoubleTools;
import stallone.ints.PrimitiveIntTools;

/**
 *
 * @author noe
 */
public class API
{    
    public static IntUtilities ints = new IntUtilities();
    public static IntFactory intsNew = new IntFactory();
    public static PrimitiveIntTools intArrays = new PrimitiveIntTools();

    public static DoubleUtilities doubles = new DoubleUtilities();
    public static DoubleFactory doublesNew = new DoubleFactory();
    public static PrimitiveDoubleTools doubleArrays = new PrimitiveDoubleTools();
    
    public static IntSequenceUtilities intseq = new IntSequenceUtilities();
    public static IntSequenceFactory intseqNew = new IntSequenceFactory();
    
    public static DataSequenceUtilities data = new DataSequenceUtilities();
    public static DataSequenceFactory dataNew = new DataSequenceFactory();
    
    public static ComplexUtilities complex = new ComplexUtilities();
    public static ComplexFactory complexNew = new ComplexFactory();

    public static StringUtilities str = new StringUtilities();
    public static StringFactory strNew = new StringFactory();

    public static FunctionUtilities func = new FunctionUtilities();
    public static FunctionFactory funcNew = new FunctionFactory();

    public static PotentialUtilities pot = new PotentialUtilities();
    public static PotentialFactory potNew = new PotentialFactory();

    public static GraphUtilities graph = new GraphUtilities();
    public static GraphFactory graphNew = new GraphFactory();
    
    public static IOUtilities io = new IOUtilities();
    
    
    

    public static AlgebraUtilities alg = new AlgebraUtilities();
    public static AlgebraFactory algNew = new AlgebraFactory();

    public static StatisticsUtilities stat = new StatisticsUtilities();
    public static StatisticsFactory statNew = new StatisticsFactory();
    
    public static DiscretizationUtilities disc = new DiscretizationUtilities();
    public static DiscretizationFactory discNew = new DiscretizationFactory();

    public static ClusterUtilities cluster = new ClusterUtilities();
    public static ClusterFactory clusterNew = new ClusterFactory();
    
    public static MarkovModelUtilities msm = new MarkovModelUtilities();
    public static MarkovModelFactory msmNew = new MarkovModelFactory();

    public static DynamicsUtilities dyn = new DynamicsUtilities();
    public static DynamicsFactory dynNew = new DynamicsFactory();

    public static HMMUtilities hmm = new HMMUtilities();
    public static HMMFactory hmmNew = new HMMFactory();
    
}
