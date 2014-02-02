/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.function;

/**
 *
 * @author noe
 */
public interface IProductFunction extends IFunction
{
    /**
     * Returns the factors that make up the product
     * @return 
     */
    public IFunction[] factors();
}
