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
import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;
import stallone.api.algebra.IComplexNumber;

import static stallone.doubles.DoubleArrayTest.*;

/**
 * Generic implementation of IVectorInnerProduct for real operands.
 *
 * @author  Martin Senne, Frank Noe
 */
public class InnerProduct //implements IVectorInnerProduct
{
    boolean complexConjugate;

    public InnerProduct(boolean _complexConjugate)
    {
        this.complexConjugate = _complexConjugate;
    }

    public double innerProduct(final IDoubleArray a, final IDoubleArray b)
    {
        assertOrder(a,1);
        assertOrder(b,1);

        // Holds to current sum
        double result = 0.0d;

        for (int i = 0; i < a.size(); i++)
        {
            result += a.get(i) * b.get(i);
        }

        return result;
    }

    public double innerProductSparse(final IDoubleArray a, final IDoubleArray b)
    {
        assertOrder(a,1);
        assertOrder(b,1);

        // Holds to current sum
        double result = 0.0d;

        for (SynchronousPairIterator it = new SynchronousPairIterator(a,b); it.hasNext(); it.advance())
        {
            result += it.get1() * it.get2();
        }

        return result;
    }

    public double innerProduct(final IDoubleArray a, final IDoubleArray b, final IDoubleArray w)
    {
        assertOrder(a,1);
        assertOrder(b,1);
        assertOrder(w,1);

        double result = 0.0d;

        for (int i = 0; i < a.size(); i++)
        {
            result += a.get(i) * b.get(i) * w.get(i);
        }

        return result;
    }

    public double innerProductSpasre(final IDoubleArray a, final IDoubleArray b, final IDoubleArray w)
    {
        assertOrder(a,1);
        assertOrder(b,1);
        assertOrder(w,1);

        double result = 0.0d;

        for (SynchronousPairIterator it = new SynchronousPairIterator(a,b); it.hasNext(); it.advance())
        {
            result += it.get1() * w.get(it.getIndex()) * it.get2();
        }

        return result;
    }

    //@Override
    public IComplexNumber innerProduct(final IComplexArray a, final IComplexArray b, final IDoubleArray w, final IComplexNumber target)
    {
        double aRe, aIm, wRe, bRe, bIm, sumRe=0, sumIm=0;
        // For each element ...
        for (int i = 0; i < a.size(); i++)
        {
            aRe = a.getRe(i);
            aIm = a.getIm(i);
            bRe = b.getRe(i);
            bIm = b.getIm(i);
            if (complexConjugate)
                bIm = -bIm;

            // weight
            if (w != null)
            {
                wRe = w.get(i);
                aRe *= wRe;
                aIm *= wRe;
            }

            sumRe += aRe*bRe - aIm*bIm;
            sumIm += aRe*bIm + aIm*bRe;
        }

        // Write back the result
        target.setComplex(sumRe, sumIm);

        // Simply return target
        return target;


    }

    //@Override
    public IComplexNumber innerProduct(final IComplexArray a, final IComplexArray b, final IComplexNumber target)
    {
        return (innerProduct(a, b, null, target));
    }

    class SynchronousPairIterator
    {
        private IDoubleIterator itA, itB;
        private int i;
        private double va, vb;

        private boolean hasNext = true;

        public SynchronousPairIterator(IDoubleArray _a, IDoubleArray _b)
        {
            itA = _a.nonzeroIterator();
            itB = _b.nonzeroIterator();

            advance();
        }

        public final void advance()
        {
            int i1 = itA.getIndex();
            int i2 = itB.getIndex();

            while (i1 != i2)
            {
                if (i1 < i2)
                {
                    if (itA.hasNext())
                    {
                        itA.advance();
                        i1 = itA.getIndex();
                    }
                    else
                    {
                        hasNext = false;
                        return;
                    }
                }
                else
                {
                    if (itB.hasNext())
                    {
                        itB.advance();
                        i1 = itB.getIndex();
                    }
                    else
                    {
                        hasNext = false;
                        return;
                    }
                }
            }

            i = i1;
            va = itA.get();
            vb = itB.get();
        }

        public boolean hasNext()
        {
            return hasNext;
        }

        public int getIndex()
        {
            return i;
        }

        public double get1()
        {
            return va;
        }

        public double get2()
        {
            return vb;
        }
    }


}

