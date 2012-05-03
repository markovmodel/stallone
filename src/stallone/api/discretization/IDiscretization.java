package stallone.api.discretization;

import stallone.api.doubles.IDoubleArray;




/**
 * @author Frank Noe
 */
public interface IDiscretization 
{

    /**
     * Get the cluster index, the datapoint p belongs to.
     *
     * @param   p  is the object to assign to a cluster
     *
     * @return  the cluster index.
     */
    public int assign(IDoubleArray p);

    /**
     * Gets the representative grid point
     * @param p
     * @return 
     */
    public IDoubleArray getRepresentative(IDoubleArray p);
    
    /**
     * Get the membership assignment of object p.
     *
     * @param   p  is the object whose fuzzy membership assignment is retrieved.
     *
     * @return  the membership assignment of p.
     */
    public IDoubleArray assignFuzzy(IDoubleArray p);
}
