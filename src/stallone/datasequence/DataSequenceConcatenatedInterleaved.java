/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stallone.datasequence;

import java.util.List;
import stallone.api.datasequence.IDataSequence;

/**
 *
 * @author noe
 */
public class DataSequenceConcatenatedInterleaved extends DataSequenceConcatenated
{
    private int step;

    public DataSequenceConcatenatedInterleaved(List<IDataSequence> _seqs, int _step)
    {
        this.seqs = _seqs;
        this.step = _step;

        for (int i=0; i<_seqs.size(); i++)
        {
            totalsize += _seqs.get(i).size() / step;

            // dimension
            if (dimension == -1)
                dimension = _seqs.get(i).dimension();
            else
                if (dimension != _seqs.get(i).dimension())
                    throw new IllegalArgumentException("Data Sequence List has inconsistent dimensionality");
        }

        microindex2trajindex = new int[totalsize];
        microindex2localindex = new int[totalsize];
        int k=0,l=0;
        for (int i=0; i<_seqs.size(); i++)
        {
            for (int j=0; j<_seqs.get(i).size(); j++)
            {
                if (k%step == 0)
                {
                    microindex2trajindex[l] = i;
                    microindex2localindex[l] = j;
                    l++;
                }
                k++;
            }
        }
    }


}
