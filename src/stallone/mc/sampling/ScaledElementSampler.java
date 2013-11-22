/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.mc.sampling;

import cern.jet.random.Beta;
import cern.jet.random.Exponential;
import cern.jet.random.Uniform;

/**
 *
 * @author trendelkamp
 */
public class ScaledElementSampler {

    public static double modePoint(double a, double b, double c , double d){

            double s=2.0*(a+b+c);
            double x0=(a+b)*d+(a+c);
            double D2=Math.pow(x0,2)-4.0*a*d*(a+b+c);

            return 1.0/s*(x0-Math.sqrt(D2));
        }

    public static double densityScaled(double x, double a, double b,
                double c,double d, double xm){

            return Math.pow(x/xm, a)*Math.pow((1.0-x)/(1.0-xm),b)*Math.pow((d-x)/(d-xm),c);
        }

    public static double logdensityScaled(double x, double a, double b,
            double c, double d, double xm){

        //Case a>0, b>0
        if(a>1e-15 && b>1e-15){
            return  a*(Math.log(x)-Math.log(xm))+
                b*(Math.log(1.0-x)-Math.log(1.0-xm))+
                c*(Math.log(d-x)-Math.log(d-xm));
            }
        //Case a=0, b>0
        else if(a<1e-15 && b>1e-15){
            return  b*(Math.log(1.0-x)-Math.log(1.0-xm))+
                c*(Math.log(d-x)-Math.log(d-xm));
        }
        //Case a>0, b=0
        else if(a>1e-15 && b<1e-15){
            return  a*(Math.log(x)-Math.log(xm))+
                c*(Math.log(d-x)-Math.log(d-xm));
        }
        //Case a=0, b=0
        else{
            return  c*(Math.log(d-x)-Math.log(d-xm));
        }
    }

    public static double logdensity(double x, double a, double b,
            double c, double d){
        //Case a>0, b>0
        if(a>1e-15 && b>1e-15){
                return  a*Math.log(x)+b*Math.log(1.0-x)+c*Math.log(d-x);
            }
        //Case a=0, b>0
        else if(a<1e-15 && b>1e-15){
            return b*Math.log(1.0-x)+c*Math.log(d-x);
        }
        //Case a>0, b=0
        else if(a>1e-15 && b<1e-15){
            return a*Math.log(x)+c*Math.log(d-x);
        }
        //Case a=0, b=0
        else{
            return c*Math.log(d-x);
        }
    }


    public static double logdensityD1(double x, double a, double b,
            double c, double d){
        //Case a>0, b>0
        if(a>1e-15 && b>1e-15){
                return  a/x-b/(1.0-x)-c/(d-x);
            }
        //Case a=0, b>0
        else if(a<1e-15 && b>1e-15){
            return -b/(1.0-x)-c/(d-x);
        }
        //Case a>0, b=0
        else if(a>1e-15 && b<1e-15){
            return a/x-c/(d-x);
        }
        //Case a=0, b=0
        else{
            return -c/(d-x);
        }
    }

    public static double logdensityD2(double x, double a, double b,
            double c, double d){
        //Case a>0, b>0
        if(a>1e-15 && b>1e-15){
                return  -a/Math.pow(x,2)-b/Math.pow((1.0-x),2)-c/Math.pow((d-x),2);
            }
        //Case a=0, b>0
        else if(a<1e-15 && b>1e-15){
            return -b/Math.pow((1.0-x),2)-c/Math.pow((d-x),2);
        }
        //Case a>0, b=0
        else if(a>1e-15 && b<1e-15){
            return -a/Math.pow(x,2)-c/Math.pow((d-x),2);
        }
        //Case a=0, b=0
        else{
            return -c/Math.pow((d-x),2);
        }
    }

    public static double sampleExponentialRestricted(Exponential randE, double upperBound){
        boolean accept=false;
        double E=0.0;
        while(!accept){
            E=randE.nextDouble();
            accept=(E<=upperBound);
        }
        return E;
    }

    public static double sampleThreePieces(Uniform randU, Exponential randE,
            double p1, double p2, double p3,
            double xl, double ql, double al,
            double xu, double qu, double au,
            double a, double b, double c, double d, double xm){
        double V;
        double U;
        double E;
        double X=0.0;

        boolean accept=false;
        while(!accept){
            V=randU.nextDouble();
            if(V<p1){
                U=randU.nextDouble();
                E=sampleExponentialRestricted(randE, xl*al);
                X=-E/al+xl;
                accept=(Math.log(U)+ql+al*(X-xl)<=logdensityScaled(X,a,b,c,d,xm));
            }
            else if(V<p1+p2){
                U=randU.nextDouble();
                X=randU.nextDoubleFromTo(xl, xu);
                accept=(Math.log(U)<=logdensityScaled(X,a,b,c,d,xm));
            }
            else{
                U=randU.nextDouble();
                E=sampleExponentialRestricted(randE,(1.0-xu)*(-au));
                X=-E/au+xu;
                accept=(Math.log(U)+qu+au*(X-xu)<=logdensityScaled(X,a,b,c,d,xm));
            }
        }
        return X;

    }

    public static double sample(Uniform randU, Exponential randE, Beta randB,
            double a, double b, double c,double d){
        double X;
        double U;

        double xm;
        double sigma;

        double xl;
        double ql;
        double al;

        double xu;
        double qu;
        double au;

        double wl;
        double wm;
        double wu;
        double w;

        double pl;
        double pm;
        double pu;

        //Case c=0
        if(Math.abs(c)<1e-15){
            X=randB.nextDouble(a+1.0, b+1.0);
        }

        //Case c>0
        else{
            //Case d=0
            if(Math.abs(d-1.0)<1e-15){
                X=randB.nextDouble(a+1.0,b+c+1.0);
            }
            //Case d>0
            else{
                //Test for feasible rejection from the Beta-density
                if(c*Math.log((d-1.0)/d)>Math.log(0.8)){
                   do{
                       U=randU.nextDouble();
                       X=randB.nextDouble(a+1.0,b+1.0);
                   } while (Math.log(U)>c*Math.log((d-X)/d));
                }

                //Else use piecewise exponential bounding density
                else{
                    //Case a>0, b>0
                    if(Math.abs(a)>1e-15 && Math.abs(b)>1e-15){
                        xm=modePoint(a,b,c,d);
                        sigma=1.0/Math.sqrt(-logdensityD2(xm,a,b,c,d));
                        xl=Math.max(0.0,xm-sigma);
                        xu=Math.min(1.0,xm+sigma);
                    }
                    //Case a>0, b=0
                    else if(Math.abs(a)>1e-15 && Math.abs(b)<1e-15){
                        xm=Math.min((a*d)/(a+c),1.0);
                        //Case xm<1
                        if(xm<1){
                            sigma=1.0/Math.sqrt(-logdensityD2(xm,a,b,c,d));
                            xl=Math.max(0.0,xm-sigma);
                            xu=Math.min(1.0,xm+sigma);
                        }
                        //Case xm>=1
                        else{
                            xl=1.0;
                            xu=1.0;
                        }
                    }
                    //Case a=0, b>0
                    else if(Math.abs(a)<1e-15 && Math.abs(b)>1e-15){
                        xl=0.0;
                        xu=0.0;
                        xm=0.0;
                    }
                    //Case a=0, b=0
                    else{
                        xl=0.0;
                        xu=0.0;
                        xm=0.0;
                    }
                    //Weight of the central enveloping piece (uniform)

                    wm=xu-xl;

                    //Case xl>0
                    if(xl>0.0){
                        al=logdensityD1(xl,a,b,c,d);
                        ql=logdensityScaled(xl,a,b,c,d,xm);
                        wl=Math.exp(ql)/al*(-1.0)*Math.expm1(-al*xl);
                    }
                    //Case xl=0
                    else{
                        xl=0.0;
                        wl=0.0;
                        al=Double.POSITIVE_INFINITY;
                        ql=Double.NEGATIVE_INFINITY;
                    }


                    //Case xu<1
                    if(xu<1.0){
                        au=logdensityD1(xu,a,b,c,d);
                        qu=logdensityScaled(xu,a,b,c,d,xm);
                        wu=Math.exp(qu)/au*Math.expm1(au*(1.0-xu));
                    }
                    //Case xu=1
                    else{
                        xu=1.0;
                        wu=0.0;
                        au=Double.NEGATIVE_INFINITY;
                        qu=Double.NEGATIVE_INFINITY;
                    }

                    //Compute normalized weights = probabilities
                    w=wl+wm+wu;
                    pl=wl/w;
                    pm=wm/w;
                    pu=wu/w;

                    //Sample X


                    X=sampleThreePieces(randU, randE,
                            pl,pm,pu,
                            xl,ql,al,
                            xu,qu,au,
                            a,b,c,d,xm);

                    /*
                    X=Double.NaN;


                    System.out.println("wl: "+wl);
                    System.out.println("wm: "+wm);
                    System.out.println("wu: "+wu);

                    System.out.println("pl: "+pl);
                    System.out.println("pm: "+pm);
                    System.out.println("pu: "+pu);

                    System.out.println("xl: "+xl);
                    System.out.println("ql: "+ql);
                    System.out.println("al: "+al);

                    System.out.println("xu: "+xu);
                    System.out.println("qu: "+qu);
                    System.out.println("au: "+au);

                    System.out.println("xl*al: "+xl*al);
                    System.out.println("(1.0-xu)*(-au): "+(1.0-xu)*(-au));
                    *
                    */

                    }
                }
            }

        return X;
    }
}
