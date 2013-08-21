/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.IDifferentiableMetric;
import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import java.util.ArrayList;
import stallone.doubles.fastutils.DoubleArrayList;


/**
 *
 * @author noe
 */
public class HarmonicNetwork extends AbstractPotential
{

    private IDoubleArray coordinates;
    private IDifferentiableMetric<IDoubleArray> metric;
    private int natoms = 0;
    private ArrayList<int[]> bonds = new ArrayList<int[]>();
    private DoubleArrayList d0 = new DoubleArrayList();
    private DoubleArrayList k = new DoubleArrayList();
    private double energy;
    private IDoubleArray gradient;

    public HarmonicNetwork(int _natoms)
    {
        this.natoms = _natoms;
        
        this.coordinates = Doubles.create.array(_natoms, 3);
        this.gradient = Doubles.create.array(_natoms, 3);
    }

    public void addSpring(int a1, int a2, double _d0, double _k)
    {
        bonds.add(new int[]
                {
                    a1, a2
                });
        d0.add(_d0);
        k.add(_k);
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

        // go through all bonds
        for (int i = 0; i < bonds.size(); i++)
        {
            // get actual distance
            int[] b = bonds.get(i);
            IDoubleArray c1 = coordinates.viewRow(b[0]);
            IDoubleArray c2 = coordinates.viewRow(b[1]);
            double d = metric.distance(c1, c2);

            // energy
            double e = 0.5 * k.get(i) * (d - d0.get(i)) * (d - d0.get(i));
            this.energy += e;
System.out.println(" b ["+b[0]+","+b[1]+"]\t"+e);

            // gradient
            double fm = k.get(i) * (d - d0.get(i));
            
            IDoubleArray mg1 = metric.gradientX(c1, c2);
            for (int dim = 0; dim < 3; dim++)
            {
                this.gradient.set(b[0], dim, this.gradient.get(b[0], dim) + fm * mg1.get(dim));
            }

            IDoubleArray mg2 = metric.gradientY(c1, c2);
            for (int dim = 0; dim < 3; dim++)
            {
                this.gradient.set(b[1], dim, this.gradient.get(b[1], dim) + fm * mg2.get(dim));
            }
        }

        return(true);
    }

    @Override
    public int getNDimensions()
    {
        return(natoms*3);
    }

    @Override
    public double getEnergy()
    {
        return(energy);
    }

    @Override
    public IDoubleArray getGradient()
    {
        return(gradient);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return(coordinates);
    }
    
    @Override
    public int getNumberOfVariables()
    {
        return(coordinates.size());
    }
    
}
