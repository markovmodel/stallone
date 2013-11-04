package stallone.api.algebra;

import stallone.api.complex.IComplexArray;
import stallone.api.doubles.IDoubleArray;

public interface INorm
{

    //@Override
    public abstract double norm(IDoubleArray v);

    //@Override
    public abstract double norm(IDoubleArray v, int p);

    //@Override
    public abstract double norm(IComplexArray v);

    //@Override
    public abstract double norm(IComplexArray v, int p);

}