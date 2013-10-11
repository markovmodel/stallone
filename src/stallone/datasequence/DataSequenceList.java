/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.ArrayList;
import stallone.api.datasequence.IDataSequence;

/**
 *
 * @author noe
 */
public class DataSequenceList extends ArrayList<IDataSequence>
{
    public int dimension()
    {
        return(get(0).dimension());
    }
}
