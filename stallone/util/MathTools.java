/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.util;

public class MathTools {

    /**
    Faster implementation of acos as in Math (at least faster for Linux)
     */
    public static double acos(double arg) {
        if (arg < 0) {
            return (Math.PI - Math.atan(Math.sqrt(1 / (arg * arg) - 1)));
        } else {
            return (Math.atan(Math.sqrt(1 / (arg * arg) - 1)));
        }
    }

    public static int sign(double f) {
        if (f > 0) {
            return (1);
        }
        if (f < 0) {
            return (-1);
        }
        return (0);
    }

    /**
    @return lbound if number < lbound, ubound if number > ubound and number
    otherwise
     */
    public static double bound(double number, double lbound, double ubound) {
        if (number < lbound) {
            return (lbound);
        }
        if (number > ubound) {
            return (ubound);
        }
        return (number);
    }

    /**
    Returns a double random number between u and l (l is exclusive)
     */
    public static double randomDouble(double l, double u) {
        if (u <= l) {
            throw (new IllegalArgumentException("Exit in randomDouble: u must be larger than l"));
        }
        return (l + (u - l) * Math.random());
    }

    /**
    Generates an integer random number between l (inclusive) and u (exclusive)
     */
    public static int randomInt(int l, int u) {
        if (u <= l) {
            throw (new RuntimeException("Exit in randomInt: u must be larger than l"));
        }
        return ((int) (l + (u - l) * Math.random()));
    }

    /**
    Generates -1 or +1, with equal likelihood.
     */
    public static int randomSign() {
        if (Math.random() < 0.5) {
            return (-1);
        } else {
            return (1);
        }
    }

    public static double min(double m1, double m2, double m3) {
        if (m1 <= m2 && m1 <= m3) {
            return (m1);
        }
        if (m2 <= m1 && m2 <= m3) {
            return (m2);
        }
        return (m3);
    }

    public static double min(double m1, double m2, double m3, double m4) {
        return (Math.min(m1, MathTools.min(m2, m3, m4)));
    }

    public static double max(double m1, double m2, double m3) {
        if (m1 >= m2 && m1 >= m3) {
            return (m1);
        }
        if (m2 >= m1 && m2 >= m3) {
            return (m2);
        }
        return (m3);
    }

    public static double max(double m1, double m2, double m3, double m4) {
        return (Math.max(m1, MathTools.max(m2, m3, m4)));
    }

    public static double degreeMod(double angle) {
        if (angle < -180) {
            return (angle + 360);
        }
        if (angle > 180) {
            return (angle - 360);
        }
        return (angle);
    }

    /** If -180 < diff = angle2-angle1 < 180: returns diff
    if diff < - 180 returns 360+diff.
    if diff > 180 returns 360-diff.
     */
    public static double degreeDiff(double angle1, double angle2) {
        return (degreeMod(angle2 - angle1));
    }

    public static double degreeAvg(double angle1, double angle2) {
        return (degreeMod(angle1 + 0.5 * degreeDiff(angle1, angle2)));
    }

    public static double degreeAvg(double[] angles) {
        double[] rangles = new double[angles.length];
        for (int i = 0; i < rangles.length; i++) {
            rangles[i] = Math.PI * angles[i] / 180.0;
        }
        return (180.0 * radAvg(rangles) / Math.PI);
    }

    public static double radMod(double angle) {
        if (angle < -Math.PI) {
            return (angle + 2 * Math.PI);
        }
        if (angle > Math.PI) {
            return (angle - 2 * Math.PI);
        }
        return (angle);
    }

    /** If -180 < diff = angle2-angle1 < 180: returns diff
    if diff < - 180 returns 360+diff.
    if diff > 180 returns 360-diff.
     */
    public static double radDiff(double angle1, double angle2) {
        return (radMod(angle2 - angle1));
    }

    /** If -180 < diff = angle2-angle1 < 180: returns diff
    if diff < - 180 returns 360+diff.
    if diff > 180 returns 360-diff.
     */
    public static double radAvg(double angle1, double angle2) {
        return (radMod(angle1 + radDiff(angle1, angle2)));
    }

    public static double radAvg(double[] angles) {
        double nom = 0.0, dnom = 0.0;
        for (int i = 0; i < angles.length; i++) {
            nom += Math.sin(angles[i]);
            dnom += Math.cos(angles[i]);
        }
        return (Math.atan2(nom, dnom));
    }

    /**
    ceils to the next upper n*(10^power),
    e.g.:
    ceilTo(0.034, 1)  = 10
    ceilTo(0.034, 0)  = Math.ceil(0.034) = 1
    ceilTo(0.034, -1) = 0.1
    ceilTo(0.034, -2) = 0.04
     */
    public static double ceilTo(double value, double power) {
        double fac = Math.pow(10, power);
        return (fac * Math.ceil(value / fac));
    }

    /**
    floors to the next lower n*(10^power),
    e.g.:
    floorTo(1.034, 1)  = 0
    floorTo(1.034, 0)  = Math.ceil(0.034) = 1
    floorTo(1.034, -1) = 1.0
    floorTo(1.034, -2) = 1.03
     */
    public static double floorTo(double value, double power) {
        double fac = Math.pow(10, power);
        return (fac * Math.floor(value / fac));
    }

    public static double factorial(int n) {
        if (n > 170) {
            throw (new RuntimeException("Can't compute factorial(n) for n>170"));
        }

        double f = 1;
        for (int i = n; i > 1; i--) {
            f *= i;
        }
        return (f);
    }

    public static double doubleFactorial(int n) {
        if (n > 300) {
            throw (new RuntimeException("Can't compute doubleFactorial(n) for n>300"));
        }

        double f = 1;
        for (int i = n; i > 1; i -= 2) {
            f *= i;
        }
        return (f);
    }

    /**
    @return the surface area of a hypersphere with radius R and dimension n
     */
    public static double hyperSphereSurfaceArea(int n, double R) {
        int k;
        if (isEven(n)) {
            k = n / 2;
        } else {
            k = (n - 1) / 2;
        }
        double S = Math.pow(2, n - k) * Math.pow(Math.PI, k) * Math.pow(R, n - 1)
                * n / doubleFactorial(n);
        return (S);
    }

    /**
    @return the volume of a hypersphere with radius R and dimension n
     */
    public static double hyperSphereVolume(int n, double R) {
        double S = hyperSphereSurfaceArea(n, 1.0);
        double V = S * Math.pow(R, n) / (double) n;
        return (V);
    }

    public static boolean isEven(int n) {
        return (n % 2 == 0);
    }

    /**
     * Fits the curve y= a+bx and returns[a,b].
     * @param x set of x-values
     * @param y set of y-values
     * @return [a,b]
     */
    public static double[] linearRegression(double[] x, double[] y)
    {
        double xysum = 0;
        double xxsum = 0;
        for (int i=0; i<x.length; i++)
        {
            xysum += x[i]*y[i];
            xxsum += x[i]*x[i];
        }

        double xsum = 0, ysum = 0;
        for (int i=0; i<x.length; i++)
            xsum += x[i];
        for (int i=0; i<y.length; i++)
            ysum += y[i];
        double n = x.length;

        double b = (xysum - xsum*ysum/n)/(xxsum - xsum*xsum/n);
        double a = (ysum/n) - (b*xsum/n);
        double[] res = {a,b};
        return(res);
    }

}
