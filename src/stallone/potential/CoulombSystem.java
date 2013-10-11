/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.IDifferentiableMetric;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import stallone.api.ints.IIntArray;


/**
 *
 * @author noe
 */
public class CoulombSystem  extends AbstractPotential
{
    private static double epsilon0 = 0.0005727657013; // in elementary charges * nm^-1 * kJ mol^-1
    private double epsilonr = 1;

    private IDoubleArray coordinates;
    private IDifferentiableMetric<IDoubleArray> metric;
    private int natoms = 0;
    private boolean[][] bonded;
    // particle radius
    private IDoubleArray charges;

    private double energy;
    private IDoubleArray gradient;

    public CoulombSystem(IDoubleArray _charges, double _epsilonr)
    {
        this.natoms = _charges.size();
        this.bonded = new boolean[_charges.size()][_charges.size()];
        this.charges = _charges;
        this.epsilonr = _epsilonr;

        this.gradient = Doubles.create.array(_charges.size(), 3);
    }

    public CoulombSystem(IDoubleArray _charges, IIntArray _bonded, double _epsilonr)
    {
        if (_bonded.columns() != 2)
            throw(new IllegalArgumentException("_bonded must be a nx2 table"));
        
        this.natoms = _charges.size();
        this.bonded = new boolean[_charges.size()][_charges.size()];
        for (int i=0; i<_bonded.size(); i++)
            this.bonded[_bonded.get(i,0)][_bonded.get(i,1)] = true;
        this.charges = _charges;
        this.epsilonr = _epsilonr;

        this.gradient = Doubles.create.array(_charges.size(), 3);
    }
    
    public CoulombSystem(IDoubleArray _charges, boolean[][] _bonded, double _epsilonr)
    {
        this.natoms = _charges.size();
        this.bonded = _bonded;
        this.charges = _charges;
        this.epsilonr = _epsilonr;

        this.gradient = Doubles.create.array(_charges.size(), 3);
    }

    public void setMetric(IDifferentiableMetric<IDoubleArray> m)
    {
        metric = m;
    }

    @Override
    public void setCoordinates(IDoubleArray _coordinates)
    {
        coordinates = (IDoubleArray) _coordinates.copy();
    }

    @Override
    public boolean calculate()
    {
        // initialize energy and gradient
        this.energy = 0;
        for (int i = 0; i < gradient.size(); i++)
        {
            this.gradient.set(i, 0);
        }

        // go through all pairs
        for (int i = 0; i < natoms - 1; i++)
        {
            for (int j = i + 1; j < natoms; j++)
            {
                if (bonded[i][j])
                {
                    continue;
                }

                // get actual distance
                IDoubleArray c1 = coordinates.viewRow(i);
                IDoubleArray c2 = coordinates.viewRow(j);
                double d = metric.distance(c1, c2);

                // energy
                double e = charges.get(i) * charges.get(j) / (4*Math.PI*epsilon0*epsilonr*d);
                this.energy += e;
//System.out.println(" c ["+i+","+j+"]\t"+e);
                
                // gradient
                double fm = -charges.get(i) * charges.get(j) / (4*Math.PI*epsilon0*epsilonr*d*d);

                IDoubleArray mg1 = metric.gradientX(c1, c2);
                for (int dim = 0; dim < 3; dim++)
                {
                    this.gradient.set(i, dim, this.gradient.get(i, dim) + fm * mg1.get(dim));
                }

                IDoubleArray mg2 = metric.gradientY(c1, c2);
                for (int dim = 0; dim < 3; dim++)
                {
                    this.gradient.set(j, dim, this.gradient.get(j, dim) + fm * mg2.get(dim));
                }
            }
        }

        return (true);
    }

    @Override
    public int getNDimensions()
    {
        return (natoms * 3);
    }

    @Override
    public double getEnergy()
    {
        return (energy);
    }

    @Override
    public IDoubleArray getGradient()
    {
        return (gradient);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return (coordinates);
    }

        @Override
    public int getNumberOfVariables()
    {
        return(coordinates.size());
    }

    
}
