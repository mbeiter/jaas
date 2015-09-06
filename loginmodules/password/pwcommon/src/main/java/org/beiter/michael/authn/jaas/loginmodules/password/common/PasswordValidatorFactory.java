/*
 * #%L
 * This file is part of a universal JAAS library, providing common functionality
 * for a username / password style JAAS module.
 * %%
 * Copyright (C) 2014 - 2015 Michael Beiter <michael@beiter.org>
 * %%
 * All rights reserved.
 * .
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the copyright holder nor the names of the
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * .
 * .
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.beiter.michael.authn.jaas.loginmodules.password.common;

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A factory to create instances of objects that implement the {@link PasswordValidator} interface.
 */
public final class PasswordValidatorFactory {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordValidatorFactory.class);

    /**
     * The singleton instance of the password validator
     */
    @SuppressWarnings("PMD.LongVariable")
    private static volatile PasswordValidator passwordValidatorInstance;

    /**
     * A private constructor to prevent instantiation of this class
     */
    private PasswordValidatorFactory() {
    }

    /**
     * Return a new, fully initialized instance of a {@link PasswordValidator} class to use for JAAS authentication.
     * <p>
     * Classes implementing the {@link PasswordValidator} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the PasswordValidator interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@code PasswordValidator} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    public static PasswordValidator getInstance(final String className, final CommonProperties properties)
            throws FactoryException {

        Validate.notBlank(className);
        Validate.notNull(properties);

        final Class<? extends PasswordValidator> validatorClazz;
        try {
            validatorClazz = Class.forName(className).asSubclass(PasswordValidator.class);
        } catch (ClassNotFoundException e) {
            final String error = "Class not found: " + className;
            LOG.warn(error);
            throw new FactoryException(error, e);
        } catch (ClassCastException e) {
            final String error = "The provided registry factory class name ('" + className
                    + "') is not a subclass of '" + PasswordValidator.class.getCanonicalName() + "'";
            LOG.warn(error);
            throw new FactoryException(error, e);
        }

        final PasswordValidator pwValidator;
        try {
            final Constructor<? extends PasswordValidator> constructor
                    = validatorClazz.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                final String error = "Constructor of class '" + validatorClazz.getCanonicalName()
                        + "' is not accessible, changing the accessible flag to instantiate the class";
                LOG.info(error);
                constructor.setAccessible(true);
            }
            pwValidator = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | IllegalArgumentException e) {
            final String error = "Cannot instantiate class '" + validatorClazz.getCanonicalName() + "'";
            LOG.warn(error, e);
            throw new FactoryException(error, e);
        }

        pwValidator.init(properties);

        return pwValidator;
    }

    /**
     * Return a singleton, fully initialized instance of a {@link PasswordValidator} class to use for
     * JAAS authentication.
     * <p>
     * Classes implementing the {@link PasswordValidator} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the PasswordValidator interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@code PasswordValidator} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static PasswordValidator getSingleton(final String className, final CommonProperties properties)
            throws FactoryException {

        Validate.notBlank(className);
        Validate.notNull(properties);

        if (passwordValidatorInstance == null) {
            synchronized (PasswordValidatorFactory.class) {
                if (passwordValidatorInstance == null) {


                    passwordValidatorInstance = getInstance(className, properties);
                }
            }
        }

        return passwordValidatorInstance;
    }

    /**
     * Resets the internal state of the factory, which causes the
     * {@link PasswordAuthenticatorFactory#getSingleton(String, CommonProperties)} method to return a new
     * {@link PasswordAuthenticator} instance the next time it is called.
     */
    // CHECKSTYLE:OFF
    // this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
    @SuppressWarnings({"PMD.NonThreadSafeSingleton", "PMD.NullAssignment"})
    // CHECKSTYLE:ON
    public static void reset() {

        // Unset the instance singleton that has been created earlier
        // The double-check idiom is safe and acceptable here (Bloch, 2nd ed. p 284)
        // null-assignments for de-referencing objects are okay
        if (passwordValidatorInstance != null) {
            synchronized (PasswordValidatorFactory.class) {
                if (passwordValidatorInstance != null) {
                    passwordValidatorInstance = null;
                }
            }
        }
    }
}
