/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.potential;

import stallone.api.doubles.IDoubleArray;
import stallone.api.potential.IEnergyModel;
import java.util.ArrayList;
import stallone.doubles.PrimitiveDoubleArray;

/**
 * Energy model that is a sum of several terms, i.e. A full energy model plus bias or field terms
 * @author noe
 */
public class CompositeEnergyModel extends AbstractPotential
{
    private ArrayList<IEnergyModel> models = new ArrayList<IEnergyModel>();
    private int nDIM = 0;
    private IDoubleArray coordinates;
    private double energy = 0;
    private IDoubleArray grad = null;

    public CompositeEnergyModel()
    {
    }

    public void add(IEnergyModel m)
    {
        if (this.models.isEmpty() || m.getNDimensions() == nDIM)
        {
            this.models.add(m);
            this.nDIM = m.getNDimensions();
        }
        else
        {
            throw (new IllegalArgumentException("Trying to add energy model with "
                    + m.getNDimensions() + " dimensions while expecting " + nDIM));
        }
    }

    @Override
    public void setCoordinates(IDoubleArray crd)
    {
        if (coordinates == null)
            coordinates = new PrimitiveDoubleArray(crd.getArray());
        else
            coordinates.copyFrom(crd);
        for (int i = 0; i < models.size(); i++)
        {
            models.get(i).setCoordinates(crd);
        }
    }

    @Override
    public boolean calculate()
    {
        // initialize
        this.energy = 0;
        this.grad = coordinates.copy();
        for (int i=0; i<this.grad.size(); i++)
            this.grad.set(i,0);

        // add up energies and gradients
        for (int i = 0; i < models.size(); i++)
        {
            boolean success = models.get(i).calculate();
            if (!success)
                return(false);
            this.energy += models.get(i).getEnergy();
            IDoubleArray gradother = models.get(i).getGradient();
            for (int j=0; j<grad.size(); j++)
            {
                this.grad.set(j, this.grad.get(j) + gradother.get(j));
            }
        }

        return(true);
    }

    @Override
    public int getNDimensions()
    {
        return (nDIM);
    }

    @Override
    public double getEnergy()
    {
        return(this.energy);
    }

    @Override
    public IDoubleArray getGradient()
    {
        return(this.grad);
    }

    @Override
    public IDoubleArray getCoordinates()
    {
        return(this.coordinates);
    }

    public int getNModels()
    {
        return(this.models.size());
    }

    @Override
    public int getNumberOfVariables()
    {
        return(models.get(0).getNDimensions());
    }

}
