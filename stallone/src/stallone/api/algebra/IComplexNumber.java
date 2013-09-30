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
 * Interface for complex number data structure based on primitive doubles. Warning: Can be very inefficient and/or
 * memory consuming, if improperly used. Especially try to avoid to put ComplexD into arrays and collections.
 *
 * <p>Better use IComplexVector or IComplexMatrix.</p>
 *
 * @author  Martin Senne, Tomaso Frigato
 */
public interface IComplexNumber {

    /**
     * Retrieves real part of this complex number.
     *
     * @return  the real part of this complex number.
     */
    double get();

    /**
     * Retrieves real part of this complex number.
     *
     * @return  the real part of this complex number.
     */
    double getRe();

    /**
     * Retrieves imaginary part of this complex number.
     *
     * @return  the imaginary part of this complex number.
     */
    double getIm();

    /**
     * Sets the real part of this complex number.
     *
     * @param  real  the real part the complex number to be set.
     */
    void set(double real);
    
    /**
     * Sets the real part of this complex number.
     *
     * @param  real  the real part the complex number to be set.
     */
    void setRe(double real);

    /**
     * Sets the imaginary part of this complex number.
     *
     * @param  imag  the imaginary part of the complex number to be set.
     */
    void setIm(double imag);

    /**
     * Sets real and imaginary part of this complex number.
     *
     * @param  real       the real part of the complex number to be set.
     * @param  imaginary  the imaginary part of the complex number to be set.
     */
    void setComplex(double real, double imaginary);

    /**
     * Sets {@code this} complex number.
     *
     * @param  source  the complex number to be set.
     */
    void setScalar(IComplexNumber source);

    /**
     * Create a new copy of this variable.
     *
     * @return  a new copy.
     */
    IComplexNumber copy();

    /**
     * Copies {@code this} complex number to the complex number {@code target}.
     *
     * @param   target  the target {@code IScalar} object.
     *
     * @return  the target object filled with the content of {@code this} complex number.
     */
    IComplexNumber copy(IComplexNumber target);

    /**
     * Gets length (l-2 norm) of this scalar.
     *
     * @return  length.
     */
    double abs();

    /**
     * Inverts this complex number in place.
     *
     * @return  the inverse of this complex number.
     */
    IComplexNumber invertInplace();

    /**
     * Creates a new complex number with the inverted value of this complex number.
     *
     * @return  a new complex being the inverse of this complex number.
     */
    IComplexNumber invert();

    /**
     * Place inverted value of this scalar into target scalar.
     *
     * @param   target  object where to place inverted scalar.
     *
     * @return  target
     */
    IComplexNumber invert(IComplexNumber target);

    /**
     * Negates this complex number in place.
     *
     * @return  {@code this} complex number with negated real and imaginary parts.
     */
    IComplexNumber negateInplace();

    /**
     * Creates a new complex number which is the negative of {@code this} complex number.
     *
     * @return  a new complex number, negative of {@code this} number
     */
    IComplexNumber negate();

    /**
     * Place negated value of this scalar into target scalar.
     *
     * @param   target  object where to place negated scalar.
     *
     * @return  target
     */
    IComplexNumber negate(IComplexNumber target);

    /**
     * Negates the imaginary part of this complex number in place.
     *
     * @return  {@code this} complex number with negated imaginary part.
     */
    IComplexNumber conjInplace();

    /**
     * Create a new complex number which is the conjugate of {@code this} complex number.
     *
     * @return  a new complex, conjugate of {@code this}
     */
    IComplexNumber conj();

    /**
     * Place conjugated value of this scalar into target scalar.
     *
     * @param   target  object where to place conjugated scalar.
     *
     * @return  target is the scalar to place value into.
     */
    IComplexNumber conj(IComplexNumber target);

    /**
     * Checks if {@code this} complex number is zero.
     *
     * @return  true if the both real and imaginary part of {@code this} complex number are zero.
     */
    boolean isZero();

    /**
     * Checks if {@code this} complex number is purely real.
     *
     * @return  true if the imaginary part of {@code this} complex number is zero.
     */
    boolean isPurelyReal();

    /**
     * Checks if {@code this} complex number is purely imaginary.
     *
     * @return  true if the real part of {@code this} complex number is zero.
     */
    boolean isPurelyImaginary();

    /**
     * Whether this scalar can store both real and imaginary values.
     *
     * @return  true if complex scalar, false for real scalar.
     */
    boolean storesComplex();

    /**
     * Multiply by other scalar. Modifies the current IScalar, where multBy() is called on.
     *
     * @param   other  the multiplicand
     *
     * @return  this (multiplied with other).
     */
    IComplexNumber multBy(IComplexNumber other);

    /**
     * Add other scalar. Modifies the current IScalar, where add() is called on.
     *
     * @param   other  the scalar to be added.
     *
     * @return  this
     */
    IComplexNumber add(IComplexNumber other);
    
    IComplexNumber addRe(double re);
    
    IComplexNumber addIm(double im);
}
