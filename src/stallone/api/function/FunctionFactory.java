/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

import stallone.function.JEPFunction;
import stallone.function.JEPFunctionC1;

/**
 *
 * @author noe
 */
public class FunctionFactory
{
    public IFunction function(String[] variables, String expression)
    {
        return(new JEPFunction(variables, expression));
    }

    public IFunction function(String expression)
    {
        return(new JEPFunction(expression));
    }

    public IFunctionC1 differentiableFunction(String[] variables, String expression, String... derivatives)
    {
        return(new JEPFunctionC1(variables, expression, derivatives));
    }

    public IFunctionC1 differentiableFunction(String expression, String... derivatives)
    {
        return(new JEPFunctionC1(expression, derivatives));
    }

    /*public IProductFunction product(IFunction... functions)
    {
    }*/
    
    public static void main(String[] args)
    {
        String[] var = {"x"};
        IFunction f = Functions.create.function("z*x+y");

        for (int i=0; i<10; i++)
        {
            System.out.println(f.f(i,i,i));
        }
    }
}
