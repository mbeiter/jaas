/*
 * #%L
 * This file is part of a universal JAAS library, providing common functionality
 * for a username / password style JAAS module.
 * %%
 * Copyright (C) 2014 - 2016 Michael Beiter <michael@beiter.org>
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
 * A factory to create instances of objects that implement the
 * {@link PasswordAuthenticator} interface.
 */
public final class PasswordAuthenticatorFactory {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordAuthenticatorFactory.class);

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
     * Return a new, fully initialized instance of an {@link PasswordAuthenticator} class to use for JAAS
     * authentication.
     * <p>
     * Classes implementing the {@link PasswordAuthenticator} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the PasswordAuthenticator interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@code PasswordAuthenticator} interface
     * @throws FactoryException When the class cannot be instantiated
     * @throws NullPointerException     When the {@code className} or {@code properties} are {@code null}
     * @throws IllegalArgumentException When {@code className} is empty
     */
    public static PasswordAuthenticator getInstance(final String className, final CommonProperties properties)
            throws FactoryException {

        Validate.notBlank(className, "The validated character sequence 'className' is null or empty");
        Validate.notNull(properties, "The validated object 'properties' is null");

        final Class<? extends PasswordAuthenticator> authNticatorClazz;
        try {
            authNticatorClazz = Class.forName(className).asSubclass(PasswordAuthenticator.class);
        } catch (ClassNotFoundException e) {
            // Fortify will report a violation here because of disclosure of potentially confidential information.
            // However, the class name is not confidential, which makes this a non-issue / false positive.
            final String error = "Class not found: " + className;
            LOG.warn(error);
            throw new FactoryException(error, e);
        } catch (ClassCastException e) {
            final String error = "The provided registry factory class name ('" + className
                    + "') is not a subclass of '" + PasswordValidator.class.getCanonicalName() + "'";
            LOG.warn(error);
            throw new FactoryException(error, e);
        }

        final PasswordAuthenticator pwAuthenticator;
        try {
            final Constructor<? extends PasswordAuthenticator> constructor
                    = authNticatorClazz.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                final String error = "Constructor of class '" + authNticatorClazz.getCanonicalName()
                        + "' is not accessible, changing the accessible flag to instantiate the class";
                LOG.info(error);
                constructor.setAccessible(true);
            }
            pwAuthenticator = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException | IllegalArgumentException e) {
            final String error = "Cannot instantiate class '" + authNticatorClazz.getCanonicalName() + "'";
            LOG.warn(error, e);
            throw new FactoryException(error, e);
        }

        pwAuthenticator.init(properties);

        return pwAuthenticator;
    }

    /**
     * Return a singleton, fully initialized instance of an {@link PasswordAuthenticator} class to use for JAAS
     * authentication.
     * <p>
     * Retrieving a singleton by this method will cause the factory to keep state, and store a reference to the
     * singleton for later use. You may reset the factory state using the {@code reset()} method to retrieve a new
     * / different singleton the next time this method is called..
     * <p>
     * Note that any properties of the singleton (e.g. configuration) cannot necessarily be changed easily. You may call
     * the singleton's {@code init()} method, but depending on the implementation provided by the respective class, this
     * may or may not have the expected effect.
     * <p>
     * If you need tight control over the singleton, including its lifecycle and configuration, or you require more than
     * one singleton that are different in their internal state (e.g. with different configurations), then you should
     * create such objects with the {@code getInstance()} method and maintain their state as "singletons" in your
     * application's business logic.
     * <p>
     * Classes implementing the {@link PasswordAuthenticator} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the PasswordAuthenticator interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@code PasswordAuthenticator} interface
     * @throws FactoryException When the class cannot be instantiated
     * @throws NullPointerException     When the {@code className} or {@code properties} are {@code null}
     * @throws IllegalArgumentException When {@code className} is empty
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static PasswordAuthenticator getSingleton(final String className, final CommonProperties properties)
            throws FactoryException {

        Validate.notBlank(className, "The validated character sequence 'className' is null or empty");
        Validate.notNull(properties, "The validated object 'properties' is null");

        if (passwordAuthenticatorInstance == null) {
            synchronized (PasswordAuthenticatorFactory.class) {
                if (passwordAuthenticatorInstance == null) {

                    passwordAuthenticatorInstance = getInstance(className, properties);
                }
            }
        }

        return passwordAuthenticatorInstance;
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
        if (passwordAuthenticatorInstance != null) {
            synchronized (PasswordAuthenticatorFactory.class) {
                if (passwordAuthenticatorInstance != null) {
                    passwordAuthenticatorInstance = null;
                }
            }
        }
    }
}
