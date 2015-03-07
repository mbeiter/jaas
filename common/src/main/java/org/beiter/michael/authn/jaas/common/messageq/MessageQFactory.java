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
package org.beiter.michael.authn.jaas.common.messageq;

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * A factory to create instances of objects that implement the {@link MessageQ} interface.
 */
public final class MessageQFactory {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(MessageQFactory.class);

    /**
     * The default message queue implementation
     */
    public static final String DEFAULT_MESSAGEQ = "org.beiter.michael.authn.jaas.common.messageq.MessageLogger";

    /**
     * The singleton instance of the message queue
     */
    private static volatile MessageQ messageQInstance;

    /**
     * A private constructor to prevent instantiation of this class
     */
    private MessageQFactory() {
    }

    /**
     * Return the default instance of a {@link MessageQ} class to use for JAAS event messageing.
     *
     * @param properties The properties to initialize the instance with
     * @return The default instance of a class implementing the {@link MessageQ} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    public static MessageQ getInstance(final Map<String, ?> properties)
            throws FactoryException {

        return getInstance(DEFAULT_MESSAGEQ, properties);
    }

    /**
     * Return an instance of a {@link MessageQ} class to use for JAAS event messageing.
     * <p/>
     * Classes implementing the {@link MessageQ} interface <b>must</b> be thread safe.
     *
     * @param className  The name of a class that implements the Message interface
     * @param properties The properties to initialize the instance with
     * @return An instance of a class implementing the {@link MessageQ} interface
     * @throws FactoryException When the class cannot be instantiated
     */
    @SuppressWarnings("PMD.NonThreadSafeSingleton")
    public static MessageQ getInstance(final String className, final Map<String, ?> properties)
            throws FactoryException {

        Validate.notBlank(className);
        Validate.notNull(properties);

        // The double-check idiom is safe and acceptable here (Bloch, 2nd ed. p 284)
        if (messageQInstance == null) {
            synchronized (MessageQFactory.class) {
                if (messageQInstance == null) {

                    final Class<? extends MessageQ> messageClazz;
                    try {
                        messageClazz = Class.forName(className).asSubclass(MessageQ.class);
                    } catch (ClassNotFoundException e) {
                        final String error = "Class not found: " + className;
                        LOG.warn(error);
                        throw new FactoryException(error, e);
                    } catch (ClassCastException e) {
                        final String error = "The provided registry factory class name ('" + className
                                + "') is not a subclass of '" + MessageQ.class.getCanonicalName() + "'";
                        LOG.warn(error);
                        throw new FactoryException(error, e);
                    }

                    try {
                        final Constructor<? extends MessageQ> constructor = messageClazz.getDeclaredConstructor();
                        if (!constructor.isAccessible()) {
                            final String error = "Constructor of class '" + messageClazz.getCanonicalName()
                                    + "' is not accessible, changing the accessible flag to instantiate the class";
                            LOG.info(error);
                            constructor.setAccessible(true);
                        }
                        messageQInstance = constructor.newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException | IllegalArgumentException e) {
                        final String error = "Cannot instantiate class '" + messageClazz.getCanonicalName() + "'";
                        LOG.warn(error, e);
                        throw new FactoryException(error, e);
                    }

                    messageQInstance.init(properties);
                }
            }
        }

        return messageQInstance;
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
        if (messageQInstance != null) {
            synchronized (MessageQFactory.class) {
                if (messageQInstance != null) {
                    messageQInstance = null;
                }
            }
        }
    }
}
