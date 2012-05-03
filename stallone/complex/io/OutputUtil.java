package stallone.complex.io;

import stallone.api.complex.IComplexArray;
import stallone.api.algebra.*;

/**
 *
 * @author Martin Senne
 */
public class OutputUtil
{

    public static String scalarToEasyString(IComplexNumber s)
    {
        return(scalarToEasyString(s.getRe(), s.getIm()));
    }

            public static String scalarToEasyString(double re, double im)
    {
        if (im == 0)
        {
            return Double.toString(re);
        }
        else
        {
            if (im > 0.0d)
            {
                return Double.toString(re) + "+" + im + "i";
            }
            else
            {
                return Double.toString(re) + im + "i";
            }
        }
    }

    public static String vectorToEasyString(IComplexArray v)
    {
        final StringBuilder builder = new StringBuilder();

        int n = v.size();
        for (int i = 0; i < n; i++)
        {
            builder.append(OutputUtil.scalarToEasyString(v.getRe(i), v.getIm(i)));

            if (i < (n - 1))
            {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

}
