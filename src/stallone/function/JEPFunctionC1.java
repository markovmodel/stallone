/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.function;

import stallone.api.doubles.Doubles;
import stallone.api.doubles.IDoubleArray;
import org.nfunk.jep.JEP;
import stallone.api.function.*;

/**
 *
 * @author noe
 */

public class JEPFunctionC1 extends JEPFunction implements IFunctionC1
{
    protected JEP[] gjep;
    private IDoubleArray grad;
    
    /**
     * Initilializes a real-valued function that is parsed with JEP. Variables are explicitly specified
     * @param _variables
     * @param _expression 
     */
    public JEPFunctionC1(String[] _variables, String _expression, String... _derivatives)
    {
        super(_variables, _expression);

        if (super.variables.length != _derivatives.length)
        {
            throw(new IllegalArgumentException("Number of derivatives must be identical to number of variables"));
        }
        
        // set gradient
        gjep = new JEP[super.variables.length];
        for (int i=0; i<super.variables.length; i++)
        {
            gjep[i] = new JEP();
            gjep[i].addStandardFunctions();
            gjep[i].addStandardConstants();
            for (String var:super.variables)
                gjep[i].addVariable(var, 0);
            gjep[i].parseExpression(_derivatives[i]);
        }
        
        grad = Doubles.create.array(super.variables.length);
    }

    public JEPFunctionC1(String _expression, String... _derivatives)
    {
        this(null, _expression, _derivatives);
    }
    
    
    @Override
    public IDoubleArray grad(double... x)
    {
        if (x.length != variables.length)
            throw(new IllegalArgumentException("Wrong number of variables"));

        for (int i=0; i<gjep.length; i++)
        {
            for (int j=0; j<variables.length; j++)
                gjep[i].addVariable(variables[j], x[j]);
        
            grad.set(i, gjep[i].getValue());
        }
        
        return(grad);        
    }
    
    @Override
    public IDoubleArray grad(IDoubleArray x)
    {
        return(grad(x.getArray()));
    }    

}
