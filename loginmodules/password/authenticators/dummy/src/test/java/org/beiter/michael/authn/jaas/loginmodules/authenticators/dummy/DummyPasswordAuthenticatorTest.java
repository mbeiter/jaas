/*
 * #%L
 * This file is part of a universal JAAS library, providing a dummy authenticator
 * for password based credential authentication.
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
package org.beiter.michael.authn.jaas.loginmodules.authenticators.dummy;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.UserPrincipal;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordAuthenticator;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.dummy.DummyPasswordAuthenticator;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordValidator;
import org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext.PlainTextPasswordValidator;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.security.Principal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DummyPasswordAuthenticatorTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(DummyPasswordAuthenticatorTest.class);

    /**
     * Authenticate a user with invalid credentials (i.e. username != password), using a plain text password
     * validator.
     */
    @Test(expected = FailedLoginException.class)
    public void authenticationFailTest()
            throws FailedLoginException {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        PasswordAuthenticator pwAuthenticator = new DummyPasswordAuthenticator();
        pwAuthenticator.init(commonProps);

        String domain = "domain";
        String username = "username";
        char[] password = "password".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                // rethrow as FailedLoginException according to JAAS spec if authN failed due to an incorrect password
                throw (FailedLoginException) e;
            } else {
                AssertionError ae = new AssertionError("Authentication error");
                ae.initCause(e);
                throw ae;
            }
        }
    }

    /**
     * Authenticate a user with the correct credentials (i.e. username = password), using a plain text password
     * validator.
     */
    @Test
    public void authenticationSuccessfulTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        PasswordAuthenticator pwAuthenticator = new DummyPasswordAuthenticator();
        pwAuthenticator.init(commonProps);

        String domain = "domain";
        String username = "username";
        char[] password = username.toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Authentication error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Authenticate a user with the correct credentials (i.e. username = password), using a plain text password
     * validator, and assert that the authenticated subject is populated correctly
     */
    @Test
    public void authenticationSuccessfulWithSubjectTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        PasswordAuthenticator pwAuthenticator = new DummyPasswordAuthenticator();
        pwAuthenticator.init(commonProps);

        String domain = "domain";
        String username = "username";
        char[] password = username.toCharArray();

        Subject subject;
        try {
            subject = pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Authentication error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The number of principals in subject does not match the expected value";
        assertThat(error, subject.getPrincipals().size(), is(equalTo(1)));

        error = "The returned principal object is not a Principal - this should never happen!";
        assertThat(error, subject.getPrincipals().toArray()[0], is(instanceOf(Principal.class)));

        Principal principal = (Principal) subject.getPrincipals().toArray()[0];
        error = "Subject name (i.e. subject unique ID) mismatch";
        // the DummyAuthenticator defines the principal unique ID as "ID:" + username
        assertThat(error, principal.getName(), is(equalTo("ID:" + username)));

        // check advanced properties of the UserPrincipal object
        error = "The returned principal object is not a UserPrincipal";
        assertThat(error, subject.getPrincipals().toArray()[0], is(instanceOf(UserPrincipal.class)));

        UserPrincipal userPrincipal = (UserPrincipal) subject.getPrincipals().toArray()[0];

        error = "Subject domain mismatch";
        assertThat(error, userPrincipal.getDomain(), is(equalTo(domain)));

        error = "Subject username mismatch";
        assertThat(error, userPrincipal.getUsername(), is(equalTo(username)));
    }
}
