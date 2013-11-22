/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.discretization;

import stallone.discretization.VoronoiDiscretization;
import stallone.api.datasequence.IDataSequence;
import stallone.api.doubles.IDoubleArray;
import stallone.api.doubles.IMetric;
import stallone.api.function.*;
import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;
import stallone.cluster.CoreDiscretization;
import stallone.datasequence.DataList;
import stallone.doubles.EuclideanDistance;
import stallone.discretization.GriddedFunctionOnDemand;

/**
 *
 * @author noe
 */
public class DiscretizationFactory
{
    public IGriddedFunction createGridDiscretization(IFunction _F, IDoubleArray bounds, double boxsize)
    {
        IGriddedFunction g = new GriddedFunctionOnDemand(_F, bounds, boxsize);
        return(g);
    }

    public IGriddedFunction createGridDiscretization(IFunction _F, IDoubleArray griddef)
    {
        IGriddedFunction g = new GriddedFunctionOnDemand(_F, griddef);
        return(g);
    }



    public IDiscretization voronoiDiscretization(IDataSequence centers,
    		IMetric<IDoubleArray> metric)
    {
        VoronoiDiscretization vd = new VoronoiDiscretization(centers, metric);
        return (vd);
    }

    public IDiscretization voronoiDiscretization(IDataSequence centers)
    {
        VoronoiDiscretization vd = new VoronoiDiscretization(centers, new EuclideanDistance());
        return (vd);
    }

    public IDiscretization coreDiscretization(IDataSequence _centers,
    		double _radius, IMetric<IDoubleArray> _metric)
    {
        CoreDiscretization disc = new CoreDiscretization(_centers, _radius, _metric);
        return (disc);
    }

    public IDiscretization coreDiscretization(IDataSequence _centers, double _radius)
    {
        CoreDiscretization disc = new CoreDiscretization(_centers,
        		_radius, new EuclideanDistance());
        return (disc);
    }

    public IDiscretization regularSelectionDiscretization(IDataSequence data,
    		IMetric<IDoubleArray> metric, int k)
    {
        DataList clusterCenters = new DataList(k);
        IIntArray indexes = Ints.create.arrayRange(0, data.size(), data.size() / k);
        for (int i = 0; i < indexes.size(); i++)
        {
            clusterCenters.set(i, data.get(indexes.get(i)));
        }
        VoronoiDiscretization vd = new VoronoiDiscretization(clusterCenters, metric);
        return (vd);
    }

    public IDiscretization regularSelectionDiscretization(IDataSequence data, int k)
    {
        return (regularSelectionDiscretization(data, new EuclideanDistance(), k));
    }

    public IDiscretization randomSelectionDiscretization(IDataSequence data,
    		IMetric<IDoubleArray> metric, int k)
    {
        DataList clusterCenters = new DataList(k);
        IIntArray indexes = Ints.create.arrayRandomIndexes(data.size(), k);
        for (int i = 0; i < indexes.size(); i++)
        {
            clusterCenters.add(data.get(indexes.get(i)));
        }

        VoronoiDiscretization vd = new VoronoiDiscretization(clusterCenters, metric);
        return (vd);
    }

    public IDiscretization randomSelectionDiscretization(IDataSequence data, int k)
    {
        return (randomSelectionDiscretization(data, new EuclideanDistance(), k));
    }

}
