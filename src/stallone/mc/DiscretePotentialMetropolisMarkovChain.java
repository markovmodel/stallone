/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;
import stallone.api.function.*;

/**
Generates a time- and space-discrete trajectory
@author noe
 */
public class DiscretePotentialMetropolisMarkovChain extends MarkovChain
{
    public DiscretePotentialMetropolisMarkovChain(IGriddedFunction f, double kT)
    {
        IDoubleArray T = Doubles.create.array(f.size(), f.size());
        
        for (int i=0; i<f.size(); i++)
        {
            IDoubleArray xi = f.get(i);
            double Ei = f.f(xi);
            IIntArray neighbors = f.getNeighborIndexes(i);

            double psum = 0;            
            
            for (int j=0; j<neighbors.size(); j++)
            {
                int n = neighbors.get(j);
                IDoubleArray xj = f.get(neighbors.get(j));
                double Ej = f.f(xj);
                double pjump = (1.0 / neighbors.size()) * Math.min(1, Math.exp(-(Ej-Ei) / kT));
                psum += pjump;
                T.set(i, n, pjump);
            }
            T.set(i,i, 1.0-psum);
        }
        
        super.init(T);
    }
    
}
