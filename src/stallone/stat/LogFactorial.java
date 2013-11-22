/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.stat;

/**
 *
 * @author noe
 */
public class LogFactorial
{
    private int nmax;
    private double[] preCalculated;

    public LogFactorial(int _nmax)
    {
        this.nmax = _nmax;
        preCalculated = new double[_nmax+1];

        for (int i=0; i<=_nmax; i++)
            preCalculated[i] = calculateNow(i);
    }

    private static double calculateNow(int n)
    {
        double logfac = 0;

        for (int i = 1; i <= n; i++)
        {
            logfac += Math.log(i);
        }

        return (logfac);
    }

    public double calculate(int n)
    {
        if (n <= nmax)
            return preCalculated[n];
        return calculateNow(n);
    }
}
