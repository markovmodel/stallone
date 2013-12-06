/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.api.coordinates;

/**
 *
 * @author noe
 */
public interface IDatasetTransform
{
    public void setInput(String[] inputfiles);
    
    public void calculateTransform();
    
    public void writeOutput();
    
    public void differentNAme();
}
