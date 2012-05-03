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
package stallone.algebra;

import stallone.api.algebra.INumericalEquality;
import stallone.api.algebra.IComplexNumber;



/**
 * @author  Martin Senne
 */
public class ScalarNumericalEquality implements INumericalEquality<IComplexNumber> {

    @Override
    public boolean numericallyEqual(final IComplexNumber o1, final IComplexNumber o2, final double precision) {
        final double deltaReal = Math.abs(o1.getRe() - o2.getRe());
        final double deltaImaginary = Math.abs(o1.getIm() - o2.getIm());

        return ((deltaReal < precision) && (deltaImaginary < precision));
    }
}
