/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.doubles;

import stallone.api.doubles.IDifferentiableMetric;
import stallone.api.doubles.IDoubleArray;


/**
 *
 * @author noe
 */
public class EuclideanDistance implements IDifferentiableMetric<IDoubleArray>
{
    private double d = 0;
    private int i = 0, size = 0;

    @Override
    public double distance(IDoubleArray x, IDoubleArray y)
    {
        d = 0;
        size = x.size();
        for (i = 0; i < size; i++)
        {
            double dd = x.get(i) - y.get(i);
            d += dd * dd;
        }
        d = Math.sqrt(d);
        return (d);
    }

    @Override
    public IDoubleArray gradientX(IDoubleArray x, IDoubleArray y)
    {
        double dxy = distance(x, y);
        IDoubleArray res = x.copy();
        size = res.size();

        if (dxy == 0)
        {
            for (i = 0; i < size; i++)
            {
                res.set(i, 0);
            }
        }
        else
        {
            for (i = 0; i < size; i++)
            {
                res.set(i, (x.get(i) - y.get(i)) / dxy);
            }
        }
        return (res);
    }

    @Override
    public IDoubleArray gradientY(IDoubleArray x, IDoubleArray y)
    {
        double dxy = distance(x, y);
        IDoubleArray res = x.copy();

        if (dxy == 0)
        {
            for (i = 0; i < res.size(); i++)
            {
                res.set(i, 0);
            }
        }
        else
        {
            for (i = 0; i < res.size(); i++)
            {
                res.set(i, -(x.get(i) - y.get(i)) / dxy);
            }
        }
        return (res);
    }
}
