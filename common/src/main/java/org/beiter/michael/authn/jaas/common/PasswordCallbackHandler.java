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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * This class implements a JAAS callback handler for username / password based authentication.
 */
public class PasswordCallbackHandler
        implements CallbackHandler {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordCallbackHandler.class);

    /**
     * The white label domain as provided during the login process
     */
    private final String domain;
    /**
     * The username as provided during the login process
     */
    private final String username;
    /**
     * The password as provided during the login process
     */
    private final char[] password;

    /**
     * Create an instance of the PasswordCallbackHandler.
     * <p/>
     * Note that {@code domain}, {@code username}, and {@code password} may be {@code null}.
     *
     * @param domain   The white label domain
     * @param username The username
     * @param password The password
     */
    // The null assignment allows to make password final
    @SuppressWarnings("PMD.NullAssignment")
    public PasswordCallbackHandler(final String domain, final String username, final char[] password) {

        // no need for defensive copies of Strings, but create a defensive copy of the password
        this.domain = domain;
        this.username = username;
        if (password == null) {
            this.password = null;
        } else {
            this.password = password.clone();
        }

    }

    /**
     * Retrieve or display the information requested in the provided Callbacks.
     * <p/>
     * The {@code handle} method implementation checks the
     * instance(s) of the {@code Callback} object(s) passed in
     * to retrieve or display the requested information.
     *
     * @param callbacks an array of {@code Callback} objects provided by an underlying security service which contains
     *                  the information requested to be retrieved or displayed.
     * @throws java.io.IOException          if an input or output error occurs. <p>
     * @throws UnsupportedCallbackException if the implementation of this method does not support one or more of the
     *                                      Callbacks specified in the {@code callbacks} parameter.
     */
    @Override
    public final void handle(final Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        for (final Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                final NameCallback nameCallback = (NameCallback) callback;
                nameCallback.setName(username);
            } else if (callback instanceof PasswordCallback) {
                final PasswordCallback passwordCallback = (PasswordCallback) callback;
                passwordCallback.setPassword(password);
            } else if (callback instanceof TextInputCallback) {
                final TextInputCallback textInputCallback = (TextInputCallback) callback;
                textInputCallback.setText(domain);
            } else {
                final String error = "Unsupported callback: "
                        + callback.getClass().getCanonicalName() + " Allowed callbacks are: "
                        + NameCallback.class.getCanonicalName() + " OR "
                        + PasswordCallback.class.getCanonicalName() + " OR "
                        + TextInputCallback.class.getCanonicalName();
                LOG.warn(error);
                throw new UnsupportedCallbackException(callback, error);
            }
        }
    }
}
