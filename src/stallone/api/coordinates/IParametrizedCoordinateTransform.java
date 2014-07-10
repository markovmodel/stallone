/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

import stallone.api.datasequence.IDataInput;
import stallone.api.datasequence.IDataSequence;

/**
 *
 * @author noe
 */
public interface IParametrizedCoordinateTransform extends ICoordinateTransform
{
    /**
     * Sets up everything such that the transform is ready to be computed
     */
    public void setupTransform(IDataInput input);
    
    /**
     * Sets up everything such that the transform is ready to be computed
     */
    public void setupTransform(IDataSequence input);
    
}
