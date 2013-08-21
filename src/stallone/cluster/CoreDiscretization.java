/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.cluster;

import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.discretization.IDiscretization;

/**
 *
 * @author noe
 */
public class CoreDiscretization implements IDiscretization
{
    private IDataSequence centers;
    private double radius;
    private IMetric metric;
    
    public CoreDiscretization(IDataSequence _centers, 
    		double _radius, IMetric<IDoubleArray> _metric)
    {
        // make sure cores don't overlap
        for (int i=0; i<_centers.size()-1; i++)
            for (int j=i+1; j<_centers.size(); j++)
            {
                double d = _metric.distance(_centers.get(i), _centers.get(j));
                if (d < 2*_radius)
                    throw(new IllegalArgumentException("Illegal Core Discretization. Cores Overlap"));
            }
        
        this.centers = _centers;
        this.radius = _radius;
        this.metric = _metric;
        
    }

    @Override
    public int assign(IDoubleArray p)
    {
        for (int i=0; i<centers.size(); i++)
            if (metric.distance(centers.get(i), p) < radius)
                return(i);
        return(-1);
    }

    @Override
    public IDoubleArray assignFuzzy(IDoubleArray p)
    {
        IDoubleArray res = Doubles.create.array(centers.size());
        int s = assign(p);
        if (s != -1)
            res.set(s, 1);
        return(res);
    }
    
    
    @Override
    public IDoubleArray getRepresentative(IDoubleArray p)
    {
        return centers.get(assign(p));
    }
        
}
