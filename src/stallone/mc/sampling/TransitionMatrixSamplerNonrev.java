/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import stallone.api.doubles.IDoubleArray;
import stallone.util.MathTools;

/**
 *
 * @author noe
 */
public class TransitionMatrixSamplerNonrev extends TransitionMatrixSamplerAbstract
{
        public TransitionMatrixSamplerNonrev(IDoubleArray counts)
    {
        super(counts);
    }

    public TransitionMatrixSamplerNonrev(IDoubleArray counts, IDoubleArray Tinit)
    {
        super(counts,Tinit);
    }

    @Override
    protected boolean step()
    {
	int i=0,j=0;//,k=0;
	do
	    {
		i = MathTools.randomInt(0,T.rows());
		j = MathTools.randomInt(0,T.rows());
	    }
	while (i==j);

	if (T.get(i,j) == 0 && T.get(i,i) == 0)
	    return(false);

	double d = MathTools.randomDouble(-T.get(i,j),T.get(i,i));
	double pacc =
	    Math.pow((T.get(i,j)+d)/T.get(i,j), C.get(i,j)) *
	    Math.pow((T.get(i,i)-d)/T.get(i,i), C.get(i,i));

	if (Math.random() <= pacc)
	    {
		T.set(i,j,T.get(i,j)+d);
		T.set(i,i,T.get(i,i)-d);

		// numerical corrections:
		ensureValidElement(i,j);
		ensureValidElement(i,i);
		if (Math.random() < 0.0001) // do a row rescaling every 10000 steps
		    ensureValidRow(i);
                return(true);
	    }
        return(false);
    }
}
