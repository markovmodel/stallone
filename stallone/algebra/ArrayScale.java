/*
 *  File:
 *  System:
 *  Module:
 *  Author:
 *  Copyright:
 *  Source:              $HeadURL: $
 *  Last modified by:    $Author: $
 *  Date:                $Date: $
 *  Version:             $Revision: $
 *  Description:
 *  Preconditions:
 */
package stallone.algebra;

import stallone.api.doubles.IDoubleIterator;
import stallone.api.doubles.IDoubleArray;
import stallone.api.complex.IComplexArray;
import stallone.api.complex.IComplexIterator;
import stallone.api.algebra.*;

/**
 * Generic implementation to scale a vector by a scalar.
 *
 * @author  Martin Senne, Christoph Thöns
 */
public class ArrayScale
{
    /**
     * Scales the real array a by the factor k and writes the result to the target
     * WITHOUT making sure that the target is zero.
     * @param a
     * @param k
     * @param target 
     */
    public void scaleQuick(final IDoubleArray a, final double k, final IDoubleArray target)
    {
        for (IDoubleIterator it = a.nonzeroIterator(); it.hasNext(); it.advance())
            target.set(it.row(), it.column(), k*it.get());
    }
    
    /**
     * Scales the real array by the factor k
     * @param a
     * @param k 
     */
    public void scale(final IDoubleArray a, double k)
    {
        for (IDoubleIterator it = a.nonzeroIterator(); it.hasNext(); it.advance())
            it.set(k*it.get());
    }
    
    /**
     * Scales the real array by the factor k and writes the result to the target
     * @param a
     * @param k
     * @param target
     * @param zeroTarget 
     */
    /**
     * Scales the array by the factor k
     * @param a
     * @param k 
     */
    public void scale(final IDoubleArray a, final double k, final IDoubleArray target)
    {
        target.zero();
        scaleQuick(a, k, target);
    }
    

    /**
     * Scales the complex array a in place without making sure target is initialized
     */
    public void scaleQuick(final IComplexArray a, final IComplexNumber k, final IComplexArray target)
    {
        if (a.isReal() && k.isPurelyReal())
            scale(a,k.getRe(),target);
        else        
        {
            for (IComplexIterator it = a.nonzeroComplexIterator(); it.hasNext(); it.advance())
            {
                int i = it.row();
                int j = it.column();
                double re = it.getRe()*k.getRe()-it.getIm()*k.getIm();
                double im = it.getRe()*k.getIm()+it.getIm()*k.getRe();
                target.setRe(i, j, re);
                target.setIm(i, j, im);
            }
        }
    }
    
    public void scale(final IComplexArray a, final IComplexNumber k, final IComplexArray target)
    {
        target.zero();
        scaleQuick(a,k,target);
    }
    
    /**
     * Scales the complex array a in place
     */
    public void scale(final IComplexArray a, final IComplexNumber k)
    {
        if (a.isReal() && k.isPurelyReal())
            scale(a,k.getRe());
        else        
        {
            for (IComplexIterator it = a.nonzeroComplexIterator(); it.hasNext(); it.advance())
            {
                double re = it.getRe()*k.getRe()-it.getIm()*k.getIm();
                double im = it.getRe()*k.getIm()+it.getIm()*k.getRe();
                it.setRe(re);
                it.setIm(im);
            }
        }
    }
    
}
