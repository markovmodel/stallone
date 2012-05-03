/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.doubles.EuclideanDistance;
import stallone.util.MathTools;


/**
 *
 * @author noe
 */
public class FoldingModelSimple  extends AbstractPotential
{
    private double df, kf, du, ku;
    private int ndim = 3;
    
    private IDoubleArray center;
    private IDoubleArray crd;
    private double energy;
    private IDoubleArray grad;
    
    private EuclideanDistance euclid = new EuclideanDistance();
            
    /*
     * Folding model with 
     * d < df:  U = -kf/2 (d-df)^2
     * d > du:  U = ku/2 (d-du)^2
     * else     U = 0
     */
    public FoldingModelSimple(double _df, double _kf, double _du, double _ku, int _ndim)
    {
        if (_df < 0 || _kf < 0 || _du < 0 || _ku < 0 || _df > _du || ndim < 1)
            throw(new IllegalArgumentException("Illegal Parameters"));
        
        df = _df;
        kf = _kf;
        du = _du;
        ku = _ku;
        ndim = _ndim;
        
        
        this.center = Doubles.create.array(ndim);
        this.crd = Doubles.create.array(ndim);
    }
    
    @Override
    public void setCoordinates(IDoubleArray coordinates)
    {
        if (coordinates.size() != ndim)
            throw(new IllegalArgumentException("Illegal Coordinate dimension"));

        this.crd.copyFrom(coordinates);
    }

    @Override
    public boolean calculate()
    {
        double d = euclid.distance(center, crd);
        grad = euclid.gradientY(center, crd);
        double g = 0;
        
        if (d < df)
        {
            energy = -kf/2 * (d-df) * (d-df);
            g = -kf * (d-df);
        }
        else if (d > du)
        {
            energy = ku/2 * (d-du) * (d-du);
            g = ku * (d-du);
        }
        else
        {
            energy = 0;
        }
        
        for (int i=0; i<grad.size(); i++)
            grad.set(i, g*grad.get(i));
        
        return(true);
    }

    @Override
    public int getNDimensions()
    {
        return(ndim);
    }

    @Override
    public double getEnergy()
    {
        return(energy);
    }

    @Override
    public IDoubleArray getGradient()
    {
        return(grad);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return(crd);
    }
    
    /**
     * Calculates the probability density depending on the distance.
     * @param distance distance
     * @return 
     */
    public double probabilityDensity(double distance, double kT)
    {
        double d = distance;
        double e = 0;
        if (d < df)
        {
            e = -kf/2 * (d-df) * (d-df);
        }
        else if (d > du)
        {
            e = ku/2 * (d-du) * (d-du);
        }
        else
        {
            e = 0;
        }        
        
        double A = MathTools.hyperSphereSurfaceArea(ndim, distance);
        
        double p = Math.exp(-e/kT)*A;
        
        return(p);
    }
    
    @Override
    public int getNumberOfVariables()
    {
        return(crd.size());
    }
    
}
