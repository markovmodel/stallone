/*
 *  File:
 *  System:
 *  Module:
 *  Author:
 *  Copyright:
 *  Source:              $HeadURL: $
 *  Last modified by:    $Author: $
 *  Date:                $Date: $
 *  Version:             $Revision: $
 *  Description:
 *  Preconditions:
 */
package stallone.api.algebra;

/**
 * Interface to compare two objects for numerical equality.
 *
 * @author  Martin Senne
 */
public interface INumericalEquality<T> {

    /**
     * Compare two object and return true, if they are numerically identical.
     *
     * @param   o1         first object
     * @param   o2         second object
     * @param   precision
     *
     * @return
     */
    public boolean numericallyEqual(T o1, T o2, double precision);
}
