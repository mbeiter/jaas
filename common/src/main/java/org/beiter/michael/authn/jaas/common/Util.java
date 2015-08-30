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
package org.beiter.michael.authn.jaas.common;

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.audit.Audit;
import org.beiter.michael.authn.jaas.common.audit.AuditException;
import org.beiter.michael.authn.jaas.common.messageq.MessageQ;
import org.beiter.michael.authn.jaas.common.messageq.MessageQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.Arrays;

/**
 * A utility class with various utility methods.
 */
@SuppressWarnings("PMD.ShortClassName")
public final class Util {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    /**
     * A private constructor to prevent instantiation of this class
     */
    private Util() {
    }

    /**
     * Zero the contents of the provided array.
     *
     * @param src the array to zeroArray
     */
    public static void zeroArray(final char[] src) {

        if (src != null) {
            Arrays.fill(src, '\0');
        }
    }

    /**
     * Zero the contents of the provided array.
     *
     * @param src the array to zeroArray
     */
    public static void zeroArray(final byte[] src) {

        if (src != null) {
            Arrays.fill(src, (byte) 0);
        }
    }

    /**
     * Create a {@code LoginException} with a cause.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link java.lang.Exception#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link java.lang.Exception#getCause()}
     *                method). (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @return a new {@code LoginException}
     * @see Exception#Exception(String, Throwable)
     */
    public static LoginException newLoginException(final String message, final Throwable cause) {

        Validate.notNull(message);
        Validate.notNull(cause);

        final LoginException loginException = new LoginException(message);
        loginException.initCause(cause);
        return loginException;
    }


    /**
     * Audit an event.
     * <p/>
     * If {@code audit} is {@code null}, then auditing is considered disabled
     *
     * @param audit    The audit object to use for auditing the event
     * @param domain   The user's domain
     * @param username The user's username
     *                 * @param event     The event to audit
     * @param error    The error message to be logged in the application log if auditing fails (i.e. no audit message
     *                 can be created)
     * @throws LoginException If auditing fails (i.e. no audit message can be created)
     */
    public static void auditEvent(final Audit audit, final String domain, final String username,
                                  final Events event, final String error)
            throws LoginException {

        Validate.notBlank(domain);
        Validate.notBlank(username);
        Validate.notNull(event);
        Validate.notBlank(error);

        // if auditing is disabled, the audit object will not have been initialized
        if (audit == null) {
            // string concatenation is only executed if log level is actually enabled
            if (LOG.isDebugEnabled()) {
                LOG.debug("Auditing has been disabled, not creating event '" + event.getValue()
                        + "' for '" + username + "@" + domain + "'");
            }
        } else {
            try {
                audit.audit(event, domain, username);
            } catch (AuditException e) {
                LOG.warn(error, e);
                throw Util.newLoginException(error, e);
            }
        }
    }

    /**
     * Post a message queue event.
     * <p/>
     * If {@code messageQ} is {@code null}, then messaging is considered disabled.
     *
     * @param messageQ The message queue object to use for posting the message
     * @param domain   The user's domain
     * @param username The user's username
     * @param event    The event to post
     * @param error    The error message to be logged in the application log if the message queue post fails (i.e. no
     *                 message queue event can be created)
     * @throws LoginException If posting to the message queue fails (i.e. no message can be created)
     */
    public static void postMessage(final MessageQ messageQ, final String domain, final String username,
                                   final Events event, final String error)
            throws LoginException {

        Validate.notBlank(domain);
        Validate.notBlank(username);
        Validate.notNull(event);
        Validate.notBlank(error);

        // if message queues are disabled, the messageQ object will not have been initialized
        if (messageQ == null) {
            // string concatenation is only executed if log level is actually enabled
            if (LOG.isDebugEnabled()) {
                LOG.debug("Messages queues have been disabled, not creating event '"
                        + event.getValue() + "' for '" + username + "@" + domain + "'");
            }
        } else {
            try {
                messageQ.create(event, domain, username);
            } catch (MessageQException e) {
                LOG.warn(error, e);
                throw Util.newLoginException(error, e);
            }
        }
    }
}
