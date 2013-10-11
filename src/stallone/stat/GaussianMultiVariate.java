/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

import stallone.api.doubles.IDoubleArray;
import stallone.api.algebra.*;

/**
 *
 * @author noe
 */
public class GaussianMultiVariate
{
    private IDoubleArray m;
    private IDoubleArray cov;
    private double amplitude;
    private IDoubleArray covInv;
    
    public GaussianMultiVariate(IDoubleArray m, IDoubleArray cov)
    {        
	this.amplitude = 1.0 / (Math.pow(2*Math.PI, (double)m.size()/2.0)
			    * Math.sqrt(Algebra.util.det(cov)));
    }

    public double f(IDoubleArray x)
    {    
        //IDoubleArray vx = Doubles.create.array(x.getArray());
        IDoubleArray dev = Algebra.util.subtract(x, m);
        IDoubleArray v2 = Algebra.util.product(dev, covInv);
        double res = Algebra.util.dot(v2, dev);

        return(amplitude * Math.exp(-0.5*res));
    }    
}
