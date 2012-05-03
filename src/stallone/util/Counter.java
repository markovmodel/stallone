/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package stallone.util;

import stallone.api.ints.IIntArray;
import stallone.api.ints.Ints;

/**
 *
 * @author noe
 */
public class Counter
{
    IIntArray nStates;
    IIntArray count;

    public Counter(IIntArray _nStates)
    {
	this.nStates = _nStates.copy();
	this.count = Ints.create.array(nStates.size());
    }

    public int getNstates()
    {
	int N = 1;
	for (int i=0; i<nStates.size(); i++)
	    N *= nStates.get(i);
	return(N);
    }

    public void set(IIntArray _count)
    {
	for (int i=0; i<nStates.size(); i++)
	    if (_count.get(i) >= nStates.get(i))
		throw(new IllegalArgumentException("Trying to set illegal count state "+_count.get(i)+" for counter index "+i));
	    else
		this.count.set(i, _count.get(i));
    }

    public IIntArray get()
    {
	return(count);
    }

    /**
       @return true, if incremented; false if overflow or illegal state.
     */
    public boolean inc(int state)
    {
	if (state < 0)
	    return(false);
	count.set(state, count.get(state)+1);
	if ((count.get(state)) == nStates.get(state))
	    {
		count.set(state, 0);
		return(inc(state-1));
	    }

	return(true);
    }

    public boolean inc()
    {
	return(inc(count.size()-1));
    }

    @Override
    public String toString()
    {
	StringBuilder buf = new StringBuilder(String.valueOf(count.get(0)));
	for (int i=1; i<count.size(); i++)
	    buf.append(", ").append(count.get(i));
	return(buf.toString());
    }
}
