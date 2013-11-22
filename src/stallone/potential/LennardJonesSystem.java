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
public class LennardJonesSystem  extends AbstractPotential
{

    private IDoubleArray coordinates;
    private IDifferentiableMetric<IDoubleArray> metric;
    private int natoms = 0;
    private boolean[][] bonded;
    // particle radius
    private IDoubleArray r;
    // well depth
    private IDoubleArray eps;
    private double energy;
    private IDoubleArray gradient;

    public LennardJonesSystem(int _natoms, IDoubleArray radii, IDoubleArray epsilons)
    {
        this.natoms = _natoms;
        this.r = radii;
        this.eps = epsilons;
        this.bonded = new boolean[_natoms][_natoms];

        this.coordinates = Doubles.create.array(_natoms, 3);
        this.gradient = Doubles.create.array(_natoms, 3);
    }

    public LennardJonesSystem(int _natoms, IDoubleArray radii, IDoubleArray epsilons, boolean[][] _bonded)
    {
        this.natoms = _natoms;
        this.r = radii;
        this.eps = epsilons;
        this.bonded = _bonded;

        this.coordinates = Doubles.create.array(_natoms, 3);
        this.gradient = Doubles.create.array(_natoms, 3);
    }

    public LennardJonesSystem(int _natoms, IDoubleArray radii, IDoubleArray epsilons, IIntArray _bonded)
    {
        this.natoms = _natoms;
        this.r = radii;
        this.eps = epsilons;
        this.bonded = new boolean[_natoms][_natoms];
        for (int i=0; i<_bonded.rows(); i++)
            this.bonded[_bonded.get(i,0)][_bonded.get(i,1)] = true;

        this.coordinates = Doubles.create.array(_natoms, 3);
        this.gradient = Doubles.create.array(_natoms, 3);
    }

    public void setMetric(IDifferentiableMetric<IDoubleArray> m)
    {
        metric = m;
    }

    @Override
    public void setCoordinates(IDoubleArray _coordinates)
    {
        coordinates.copyFrom(_coordinates);
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

                // effective epsilon
                double eps2 = 2 * eps.get(i) * eps.get(j) / (eps.get(i) + eps.get(j));

                // effective distance
                double r2 = 2*Math.sqrt(r.get(i) * r.get(j));

                // energy
                double r2d = r2 / d;
                double e = 4 * eps2 * (Math.pow(r2d, 12) - Math.pow(r2d, 6));
                this.energy += e;
System.out.println(" vdw ["+i+","+j+"]\t"+e+"\t at d = "+d+" \t with eps = "+eps2+"\t r = "+r2);

                // gradient
                double fm = 4 * eps2 * (6 * Math.pow(r2d, 6) / d - 12 * Math.pow(r2d, 12) / d);

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
