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
package stallone.complex;

import stallone.api.algebra.IComplexNumber;


/**
 * ComplexScalar class operating on double primitives. <b>Warning</b>: Can be very * inefficient and/or memory
 * consuming, if improperly used. Especially try to avoid to put ComplexScalar into arrays and collections, due to the
 * memory overhead, when creating large collection.
 *
 * @author  Martin Senne
 */
public class ComplexNumber implements IComplexNumber {

    /** real part of this complex scalar. */
    private double real;

    /** imaginary part of this complex scalar. */
    private double imaginary;

    /**
     * Construct new complex number with real-part {@code real} and imaginary-part {@code imag}.
     *
     * @param  real  real part of new complex.
     * @param  imag  imaginary part of new complex.
     */
    public ComplexNumber(final double real, final double imag) {
        this.real = real;
        this.imaginary = imag;
    }

    /**
     * Construct new complex number with real-part {@code real} and imaginary part 0.0.
     *
     * @param  real  real part of new complex.
     */
    public ComplexNumber(final double real) {
        this.real = real;
        this.imaginary = 0.0d;
    }

    /**
     * Copy constructor.
     *
     * @param  other  is the complex number to copy.
     */
    public ComplexNumber(final IComplexNumber other) {
        this.real = other.getRe();
        this.imaginary = other.getIm();
    }

    /**
     * Create zero.
     *
     * @return  new complex number.
     */
    public static ComplexNumber createZero() {
        return new ComplexNumber(0.0d, 0.0d);
    }

    /**
     * Create one.
     *
     * @return  new complex number.
     */
    public static ComplexNumber createUnit() {
        return new ComplexNumber(1.0d, 0.0d);
    }

    /**
     * Create negated i.
     *
     * @return  new complex number.
     */
    public static ComplexNumber createNegatedUnit() {
        return new ComplexNumber(-1.0d, 0.0d);
    }

    /**
     * Create i.
     *
     * @return  new complex number.
     */
    public static ComplexNumber createI() {
        return new ComplexNumber(0.0d, 1.0d);
    }

    /**
     * Create negated i.
     *
     * @return  new complex number.
     */
    public static ComplexNumber createNegatedI() {
        return new ComplexNumber(0.0d, -1.0d);
    }

    /**
     * Create a string representation of this complex. I.e. 3.2 + 5.4i
     *
     * @return  String representing this complex number.
     */
    @Override
    public String toString() {

        return "(" + real + " " + imaginary + "i)";
            // return String.format("%+.8e%+.8e%s", real, imaginary, "i");
            /*
             * if(imaginary>=0.0d) { return real + "+" + imaginary + "i"; } else { return real + "" + imaginary + "i"; }
             */
    }

    /**
     * Set the real and imaginary part of this complex number to {@code real} and {@code imaginary}.
     *
     * @param  real       part to set for this complex.
     * @param  imaginary  part to set for this complex.
     */
    @Override
    public final void setComplex(final double real, final double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    /**
     * Retrieve real part of {@code this} complex number.
     *
     * @return  real part.
     */
    @Override
    public final double get() {
        return real;
    }

    /**
     * Retrieve real part of {@code this} complex number.
     *
     * @return  real part.
     */
    @Override
    public final double getRe() {
        return real;
    }

    /**
     * Retrieve imaginary part of {@code this} complex number.
     *
     * @return  imaginary part.
     */
    @Override
    public final double getIm() {
        return imaginary;
    }

    @Override
    public IComplexNumber copy() {
        return new ComplexNumber(this);
    }

    /**
     * Copy {@code this} complex number to the complex number <code>target</code>.
     *
     * @param   target  used to fill with values of this object
     *
     * @return  target object with the content of {@code this} complex number.
     */
    @Override
    public IComplexNumber copy(final IComplexNumber target) {
        target.setComplex(this.real, this.imaginary);

        return target;
    }

    /**
     * Get length (l-2 norm) of this complex.
     *
     * @return  length.
     */
    @Override
    public double abs() {
        return Math.sqrt((real * real) + (imaginary * imaginary));
    }

    /**
     * Invert {@code this} complex number inplace.
     *
     * @return  {@code this}
     */
    @Override
    public IComplexNumber invertInplace() {
        final double l = (real * real) + (imaginary * imaginary);
        real = real / l;
        imaginary = -imaginary / l;

        return this;
    }

    /**
     * Create a new complex number with the inverted value of {@code this} complex number.
     *
     * @return  a new complex inverted number.
     */
    @Override
    public IComplexNumber invert() {
        final double l = (real * real) + (imaginary * imaginary);

        return new ComplexNumber(real / l, -imaginary / l);
    }

    /**
     * Create a new complex number with the inverted value of {@code this} complex number.
     *
     * @return  a new complex inverted number.
     */
    @Override
    public IComplexNumber invert(final IComplexNumber target) {
        final double l = (real * real) + (imaginary * imaginary);
        target.setRe(real / l);
        target.setIm(-imaginary / l);

        return target;
    }

    @Override
    public IComplexNumber negateInplace() {
        real = -real;
        imaginary = -imaginary;

        return this;
    }

    @Override
    public IComplexNumber negate() {
        return new ComplexNumber(-this.real, -this.imaginary);
    }

    @Override
    public IComplexNumber negate(final IComplexNumber target) {
        target.setComplex(-real, -imaginary);

        return target;
    }

    @Override
    public IComplexNumber conjInplace() {
        imaginary = -imaginary;

        return this;
    }

    @Override
    public IComplexNumber conj() {
        return new ComplexNumber(this.real, -this.imaginary);
    }

    @Override
    public IComplexNumber conj(final IComplexNumber target) {
        target.setComplex(real, -imaginary);

        return target;
    }

    /**
     * Test for zero.
     *
     * @return  if complex is zero
     */
    @Override
    public boolean isZero() {
        return ((real == 0.0d) && (imaginary == 0.0d));
    }

    @Override
    public final boolean isPurelyReal() {
        return (imaginary == 0.0d);
    }

    @Override
    public final boolean isPurelyImaginary() {
        return (real == 0.0d);
    }

    @Override
    public void setScalar(final IComplexNumber complex) {
        real = complex.getRe();
        imaginary = complex.getIm();
    }

    @Override
    public void setRe(final double real) {
        this.real = real;
    }

    @Override
    public void setIm(final double imag) {
        this.imaginary = imag;
    }

    @Override
    public final boolean storesComplex() {
        return true;
    }

    @Override
    public IComplexNumber add(final IComplexNumber other) {
        real += other.getRe();
        imaginary += other.getIm();

        return this;
    }

    @Override
    public IComplexNumber multBy(final IComplexNumber other) {
        final double a_r = real;
        final double a_i = imaginary;

        final double b_r = other.getRe();
        final double b_i = other.getIm();

        real = (a_r * b_r) - (a_i * b_i);
        imaginary = (a_r * b_i) + (a_i * b_r);

        return this;
    }

    @Override
    public IComplexNumber addRe(double re)
    {
        real += re;
        return(this);
    }

    @Override
    public IComplexNumber addIm(double im)
    {
        imaginary += im;
        return(this);
    }

    @Override
    public void set(double real)
    {
        setRe(real);
    }
}
