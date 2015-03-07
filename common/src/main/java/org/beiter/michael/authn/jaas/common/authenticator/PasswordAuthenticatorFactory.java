/*
 * #%L
 * This file is part of a common library for a set of universal JAAS modules.
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
package org.beiter.michael.authn.jaas.common.authenticator;

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.beiter.michael.authn.jaas.common.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A factory to create instances of objects that implement the
 * {@link org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticator} interface.
 */
public final class PasswordAuthenticatorFactory {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordAuthenticatorFactory.class);

    /**
     * The default message queue implementation
     */
    @SuppressWarnings("PMD.LongVariable")
    public static final String DEFAULT_AUTHENTICATOR =
            "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator";

    /**
     * The singleton instance of the password authenticator
     */
    @SuppressWarnings("PMD.LongVariable")
    private static volatile PasswordAuthenticator passwordAuthenticatorInstance;

    /**
     * A private constructor to prevent instantiation of this class
     */
    private PasswordAuthenticatorFactory() {
    }

    /**
     * Return the default instance of a {@link PasswordAuthenticator} class to use for JAAS authentication.
     *
     * @param properties The properties to initialize the instance with
     * @return The default instance of a class implementing the {@link PasswordAuthenticator} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    public static PasswordAuthenticator getInstance(final Map<String, ?> properties)
            throws FactoryException {

        return getInstance(DEFAULT_AUTHENTICATOR, properties);
    }

    /**
     * Return an instance of an {@link PasswordAuthenticator} class to use for JAAS authentication.
     * <p/>
     * Classes implementing the {@link PasswordAuthenticator} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the PasswordAuthenticator interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@code PasswordAuthenticator} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static PasswordAuthenticator getInstance(final String className, final Map<String, ?> properties)
            throws FactoryException {

        Validate.notBlank(className);
        Validate.notNull(properties);

        if (passwordAuthenticatorInstance == null) {
            synchronized (PasswordAuthenticatorFactory.class) {
                if (passwordAuthenticatorInstance == null) {

                    final Class<? extends PasswordAuthenticator> authNticatorClazz;
                    try {
                        authNticatorClazz = Class.forName(className).asSubclass(PasswordAuthenticator.class);
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

                    try {
                        final Constructor<? extends PasswordAuthenticator> constructor
                                = authNticatorClazz.getDeclaredConstructor();
                        if (!constructor.isAccessible()) {
                            final String error = "Constructor of class '" + authNticatorClazz.getCanonicalName()
                                    + "' is not accessible, changing the accessible flag to instantiate the class";
                            LOG.info(error);
                            constructor.setAccessible(true);
                        }
                        passwordAuthenticatorInstance = constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException | IllegalArgumentException e) {
                        final String error = "Cannot instantiate class '" + authNticatorClazz.getCanonicalName() + "'";
                        LOG.warn(error, e);
                        throw new FactoryException(error, e);
                    }

                    passwordAuthenticatorInstance.init(properties);
                }
            }
        }

        return passwordAuthenticatorInstance;
    }

    /**
     * Resets the internal state of the factory.
     */
    // CHECKSTYLE:OFF
    // this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
    @SuppressWarnings({"PMD.NonThreadSafeSingleton", "PMD.NullAssignment"})
    // CHECKSTYLE:ON
    public static void reset() {

        // Unset the instance singleton that has been created earlier
        // The double-check idiom is safe and acceptable here (Bloch, 2nd ed. p 284)
        // null-assignments for de-referencing objects are okay
        if (passwordAuthenticatorInstance != null) {
            synchronized (PasswordAuthenticatorFactory.class) {
                if (passwordAuthenticatorInstance != null) {
                    passwordAuthenticatorInstance = null;
                }
            }
        }
    }
}
