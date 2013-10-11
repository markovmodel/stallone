/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.algebra;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.complex.ComplexNumber;
import stallone.api.complex.IComplexIterator;
import stallone.api.complex.IComplexArray;
import stallone.api.algebra.*;

/**
 *
 * @author noe
 */
public class ArrayNorm //implements INorm
{

    //@Override
    public double norm(IDoubleArray v)
    {
        return (norm(v, 2));
    }

    //@Override
    public double norm(IDoubleArray v, int p)
    {
        double sum = 0;

        double abs = 0;
        for (IDoubleIterator it = v.nonzeroIterator(); it.hasNext(); it.advance())
        {
            abs = it.get();
            sum += Math.pow(abs, p);
        }

        return Math.pow(sum, 1.0 / (double) p);
    }

    //@Override
    public double norm(IComplexArray v)
    {
        return (norm(v, 2));
    }

    //@Override
    public double norm(IComplexArray v, int p)
    {
        double sum = 0;

        final IComplexNumber o1 = ComplexNumber.createZero();
        double abs = 0;

        for (IComplexIterator it = v.nonzeroComplexIterator(); it.hasNext(); it.advance())
        {
            o1.setComplex(it.getRe(), it.getIm());
            abs = o1.abs();
            sum += Math.pow(abs, p);
        }

        return sum;
    }
}
