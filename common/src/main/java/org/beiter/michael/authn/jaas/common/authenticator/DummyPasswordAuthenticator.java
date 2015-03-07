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

import org.beiter.michael.authn.jaas.common.UserPrincipal;
import org.beiter.michael.authn.jaas.common.Util;
import org.beiter.michael.authn.jaas.common.validator.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.Map;

/**
 * This authenticator performs validation of username / password type credentials by checking whether the provided
 * username matches the provided password. This is obviously not a very strong authenticator, and is intended to be
 * used for testing only.
 */
public class DummyPasswordAuthenticator
        implements PasswordAuthenticator {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(DummyPasswordAuthenticator.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(final Map<String, ?> properties) {
        // nothing to do here - this implementation does not take any properties
    }

    /**
     * Authenticate a user by validating the user's password, returning a Subject with one or more {@code Principal}s
     * set (if validation was successful), or throw a {@code LoginException} (if validation fails)
     * <p/>
     * This method checks whether the provided username matches the provided password. This requires a suitable plain
     * text password validator to work, as the actual validation is performed by the provided
     * {@code PasswordValidator}). The method also checks that the domain is not {@code null} (but it does allow an
     * empty String value).
     * <p/>
     * If the validation is successful, a {@code Subject} is populated with three principals is returned: The
     * user's ID as a concatenation of the username with the string "ID:", and both the user provided domain and the
     * user provided principal (i.e. the the identifiers used to authenticate the users).
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
    public final Subject authenticate(final String domain, final String userName, final char[] password,
                                      final PasswordValidator passwordValidator)
            throws LoginException {

        // make sure the credentials are not null
        if (domain == null) {
            throw new LoginException("The domain cannot be null");
        }
        if (userName == null) {
            throw new LoginException("The username cannot be null");
        }
        if (password == null) {
            throw new LoginException("The password cannot be null");
        }

        // no need for defensive copies of Strings, but create a defensive copy of the password
        final char[] myPassword = password.clone();

        // convert the username string to a char array
        final char[] myCredential = userName.toCharArray();

        // perform the password validation
        if (!passwordValidator.validate(myPassword, myCredential)) {
            final String error = "Invalid password for username '" + userName + "'";
            LOG.info(error);
            throw new FailedLoginException(error);
        }

        // The authentication was successful!
        // Create the subject and clean up confidential data as far as possible.

        // clear the char representation of the credential
        Util.zeroArray(myCredential);

        // clear the defensive copy of the password created earlier
        Util.zeroArray(myPassword);

        // create a principal that includes the username and domain name that were used to authenticate the user
        final UserPrincipal userPrincipal = new UserPrincipal("ID:" + userName, domain, userName);

        // wrap the principal in a Subject
        final Subject subject = new Subject();
        subject.getPrincipals().add(userPrincipal);

        return subject;
    }
}
