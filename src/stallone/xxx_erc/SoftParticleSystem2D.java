/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.xxx_erc;

import stallone.api.algebra.Algebra;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.potential.IEnergyModel;

/**
 *
 * @author noe
 */
public class SoftParticleSystem2D implements IEnergyModel
{
    // box parameters
    private double boxsize = 1;
    // core repulsion parameters
    private int N = 0;
    private IDoubleArray sizes;
    private double kCore;
    
    // current coordinates, energy, gradient
    private IDoubleArray crd;
    private double energy;
    private IDoubleArray grad;

    public SoftParticleSystem2D(double _boxsize, int _N, IDoubleArray _sizes, double _kCore)
    {
        this.boxsize = _boxsize;
        this.N = _N;
        this.sizes = _sizes;
        this.kCore = _kCore;
        
        // initialize other arrays
        crd = Doubles.create.arrayRandom(2*N);
        crd = Doubles.create.matrixReshape(crd, N, 2);
        Algebra.util.scale(boxsize, crd);
        energy = 0;
        grad = Doubles.create.array(N,2);
    }
    
    @Override
    public void setCoordinates(IDoubleArray coordinates)
    {
        this.crd = coordinates;
    }

    private double mapToBox(double x)
    {
        while (x < 0)
            x += boxsize;
        while (x > boxsize)
            x -= boxsize;
        return x;
    }
    
    public void restrictToBox()
    {
        for (int i=0; i<N; i++)
        {
            //System.out.println(crd.get(i,0)+" % "+boxsize+" = "+(crd.get(i,0)%boxsize));
            crd.set(i,0, mapToBox(crd.get(i,0)));
            crd.set(i,1, mapToBox(crd.get(i,1)));
        }
    }
    
    private double dCoord(double x1, double x2)
    {
        double ddirect = x1-x2;
        double d = ddirect;
        
        double dplus1 = ddirect + boxsize;
        if (Math.abs(dplus1) < Math.abs(d))
            d = dplus1;
        
        double dminus1 = ddirect - boxsize;
        if (Math.abs(dminus1) < Math.abs(d))
            d = dplus1;
        
        return d;
    }
    
    @Override
    public boolean calculate()
    {
        energy = 0;
        grad.zero();
        
        restrictToBox();

        // calculate core repulsions
        for (int i=0; i<N-1; i++)
        {
            double sizei = sizes.get(i);
            
            for (int j=i+1; j<N; j++)
            {
                double sizej = sizes.get(j);
                double sizeTot = sizei + sizej;
                
                double dx = dCoord(crd.get(i,0),crd.get(j,0));
                double dy = dCoord(crd.get(i,1),crd.get(j,1));
                double dij = Math.sqrt(dx*dx + dy*dy);
             
                energy += 0.5 * kCore * (sizeTot - dij);
                grad.set(i, 0, grad.get(i,0) + kCore * (sizeTot-dij) * 2.0 * dx / dij );
                grad.set(j, 0, grad.get(j,0) - kCore * (sizeTot-dij) * 2.0 * dx / dij );
                grad.set(i, 1, grad.get(i,1) + kCore * (sizeTot-dij) * 2.0 * dy / dij );
                grad.set(j, 1, grad.get(j,1) - kCore * (sizeTot-dij) * 2.0 * dy / dij );
            }
        }

        return true;
    }

    @Override
    public int getNDimensions()
    {
        return 2;
    }

    @Override
    public double getEnergy()
    {
        return energy;
    }

    @Override
    public IDoubleArray getGradient()
    {
        return grad;
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return crd;
    }
    
}
