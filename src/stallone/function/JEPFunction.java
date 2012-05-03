/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.function;

import stallone.api.doubles.IDoubleArray;
import java.util.ArrayList;
import org.nfunk.jep.JEP;
import stallone.api.function.*;

/**
 *
 * @author noe
 */

public class JEPFunction implements IFunction
{
    protected JEP jep = new JEP();
    protected String[] variables;
    
    /**
     * Initilializes a real-valued function that is parsed with JEP. Variables are explicitly specified
     * @param _variables
     * @param _expression 
     */
    public JEPFunction(String[] _variables, String _expression)
    {
        jep.addStandardFunctions();
        jep.addStandardConstants();
        
        if (_variables == null)
        {
            jep.setAllowUndeclared(true);
            jep.parseExpression(_expression);
            
            ArrayList<String> var = new ArrayList<String>();
            if (jep.getVar("x") != null)
                var.add("x");
            for (int i=0; i<10; i++)
                if (jep.getVar("x"+i) != null)
                    var.add("x"+i);
            if (jep.getVar("y") != null)
                var.add("y");
            for (int i=0; i<10; i++)
                if (jep.getVar("y"+i) != null)
                    var.add("y"+i);
            if (jep.getVar("z") != null)
                var.add("z");
            for (int i=0; i<10; i++)
                if (jep.getVar("z"+i) != null)
                    var.add("z"+i);
            variables = new String[var.size()];
            var.toArray(variables);
        }
        else
        {
            this.variables = _variables;
            for (String var:_variables)
                jep.addVariable(var, 0);
            jep.parseExpression(_expression);
        }
    }

    
    /**
     * Initilializes a real-valued function that is parsed with JEP. Variables are implicitly specified:
     * Variables are ordered in the sequence x,x0,x1,x2,...,x9,y,y0,y1,y2,...,y9,z,z0,z1,z2,...,z9.
     * @param _variables
     * @param _expression 
     */
    public JEPFunction(String _expression)
    {
        this(null, _expression);
    }
    
    @Override
    public int getNumberOfVariables()
    {
        return(variables.length);
    }

    @Override
    public double f(double... x)
    {
        if (x.length != variables.length)
            throw(new IllegalArgumentException("Wrong number of variables: expecting "+variables.length+" but got "+x.length));

        for (int i=0; i<variables.length; i++)
            jep.addVariable(variables[i], x[i]);
        
        return(jep.getValue());
    }
    
    @Override
    public double f(IDoubleArray x)
    {
        return(f(x.getArray()));
    }
}
