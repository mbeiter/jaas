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

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.validator.PasswordValidator;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

/**
 * An interface representing a username / password based authentication mechanism.
 * <p/>
 * Classes implementing this interface <b>must</b> be thread safe.
 */
public interface PasswordAuthenticator {

    /**
     * Initializes the authenticator configuration
     * <p/>
     * A class implementing this interface must provide a reasonable default configuration and handle situations where
     * the {@code validate()} method is called without a previous call of {@code init()} (i.e. do not throw a runtime
     * exception).
     * <p/>
     * A class implementing this interface must ensure that subsequent calls to this method update the class'
     * configuration in a thread-safe way.
     *
     * @param properties The properties to initialize the message queue with. Supported "additionalParameters" may
     *                   vary with the implementing classes.
     */
    void init(CommonProperties properties);

    /**
     * Authenticate a user by validating the user's password, returning a Subject with one or more {@code Principal}s
     * set (if validation was successful), or throw a {@code LoginException} (if validation fails)
     *
     * @param domain            The white label domain in which the username is located
     * @param userName          The username to authenticate with
     * @param password          The password to authenticate with
     * @param passwordValidator The validator to use for the password
     * @return a callback for querying subject attributes.
     * @throws LoginException when this {@code LoginModule} is unable to perform the authentication. Catch a
     *                        {@link javax.security.auth.login.FailedLoginException} to determine if the authentication
     *                        failed due to an incorrect password
     */
    Subject authenticate(String domain, String userName, char[] password, PasswordValidator passwordValidator)
            throws LoginException;

}
