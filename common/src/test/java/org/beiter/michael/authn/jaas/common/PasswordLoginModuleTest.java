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

import org.beiter.michael.authn.jaas.common.audit.AuditFactory;
import org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticatorFactory;
import org.beiter.michael.authn.jaas.common.messageq.MessageQFactory;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasPropsBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.common.validator.PasswordValidatorFactory;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PasswordLoginModuleTest {

    /**
     * Reset all factory classes used by the abstract login module before the tests so that we can instantiate
     * different types of audit, message queue, and validator classes.
     */
    @Before
    public void resetFactories() {
        AuditFactory.reset();
        MessageQFactory.reset();
        PasswordValidatorFactory.reset();
        PasswordAuthenticatorFactory.reset();
    }

    /**
     * Test that the module can be successfully initialized with the default setting (empty options array)
     */
    @Test
    public void initializeDefault() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);
    }

    /**
     * Test that the module initialization will fail if a bogus audit class is provided in the configuration
     */
    @Test(expected = IllegalStateException.class)
    public void initializeBogusAudit() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_CLASS_NAME, "InvalidClass");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);
    }

    /**
     * Test that the module initialization will fail if a bogus message queue class is provided in the configuration
     */
    @Test(expected = IllegalStateException.class)
    public void initializeBogusMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, "InvalidClass");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);
    }

    /**
     * Test that the module initialization will fail if a bogus password validator class is provided in the
     * configuration
     */
    @Test(expected = IllegalStateException.class)
    public void initializeBogusValidator() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "InvalidClass");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);
    }

    /**
     * Test that the module initialization will fail if a bogus password authenticator class is provided in the
     * configuration
     */
    @Test(expected = IllegalStateException.class)
    public void initializeBogusAuthenticator() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "InvalidClass");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);
    }

    /**
     * Test that a login with valid credentials (username == password for the helper JAAS module used in this test) works
     */
    @Test
    public void loginSuccess() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a login with valid credentials (username == password for the helper JAAS module used in this test)
     * works with audit and message queue being turned on (default audit and message queue implementations are being used)
     */
    @Test
    public void loginSuccessWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }
    }


    /**
     * Test that a login with invalid credentials (username != password for the helper JAAS module used in this test)
     * fails
     */
    @Test(expected = LoginException.class)
    public void loginFailInvalidPassword() throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        loginModule.login();
    }

    /**
     * Test that a login with invalid credentials (username != password for the helper JAAS module used in this test)
     * fails with audit and message queue being turned on (default audit and message queue implementations are being used)
     */
    @Test(expected = LoginException.class)
    public void loginFailInvalidPasswordWithAuditAndMessageQ() throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        loginModule.login();
    }


    /**
     * Test that a login with a null callback handler fails
     */
    @Test(expected = NullPointerException.class)
    public void loginFailNullCallbackHandler()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), null, sharedState, options);

        loginModule.login();
    }

    /**
     * Test that a login with a callback handler that does not have a domain fails
     */
    @Test(expected = LoginException.class)
    public void loginFailDomainlessCallbackHandler()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandlerWithoutDomain("username", "username"),
                sharedState, options);

        loginModule.login();
    }

    /**
     * Test that a commit with valid credentials (username == password for the helper JAAS module used in this test)
     * works
     */
    @Test
    public void commitSuccess() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after successful login failed";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit with valid credentials (username == password for the helper JAAS module used in this test)
     * works with audit and message queue being turned on (default audit and message queue implementations are being used)
     */
    @Test
    public void commitSuccessWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after successful login failed";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit that is executed before a login fails
     */
    @Test
    public void commitFailBeforeLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Commit before successful login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }
    
    /**
     * Test that a commit that is executed before a login fails with audit and message queue being turned on (default 
     * audit and message queue implementations are being used)
     */
    @Test
    public void commitFailBeforeLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Commit before successful login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit that is executed after a failed login fails
     */
    @Test
    public void commitFailAfterFailedLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Commit after failed login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }


    /**
     * Test that a commit that is executed after a failed login fails with audit and message queue being turned on 
     * (default audit and message queue implementations are being used)
     */
    @Test
    public void commitFailAfterFailedLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Commit after failed login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit that is executed after a previous commit fails (this happens if a module instance is recycled)
     */
    @Test(expected = LoginException.class)
    public void commitFailAfterCompletedPreviousCommit()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        // first do a regular login and commit...
        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after successful login failed";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        // ...then try to commit again, which should throw an exception
        loginModule.commit();
    }

    /**
     * Test that a commit that is executed after a previous commit fails with audit and message queue being turned on
     * (default audit and message queue implementations are being used) -> this happens if a module instance is recycled
     */
    @Test(expected = LoginException.class)
    public void commitFailAfterCompletedPreviousCommitWithAuditAndMessageQ()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        // first do a regular login and commit...
        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after successful login failed";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        // ...then try to commit again, which should throw an exception
        loginModule.commit();
    }

    /**
     * Test that an abort after a commit with valid credentials (username == password for the helper JAAS module used
     * in this test) works
     */
    @Test
    public void abortSuccess() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"), sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort after a commit with valid credentials (username == password for the helper JAAS module used
     * in this test) works with audit and message queue being turned on (default audit and message queue implementations
     * are being used)
     */
    @Test
    public void abortSuccessWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"), sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed before a login works
     */
    @Test
    public void abortFailBeforeLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Abort before successful login must fail";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed before a login works with audit and message queue being turned on (default
     * audit and message queue implementations are being used)
     */
    @Test
    public void abortFailBeforeLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Abort before successful login must fail";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed after a failed login fails
     */
    @Test
    public void abortFailAfterFailedLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Abort after failed login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed after a failed login fails with audit and message queue being turned on
     * (default audit and message queue implementations are being used)
     */
    @Test
    public void abortFailAfterFailedLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Abort after failed login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort after a login with valid credentials (username == password for the helper JAAS module used
     * in this test), but before a commit, works as expected
     */
    @Test
    public void abortAfterLoginBeforeCommit() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login, but before commit, failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort after a login with valid credentials (username == password for the helper JAAS module used
     * in this test), but before a commit, works as expected with audit and message queue being turned on (default
     * audit and message queue implementations are being used)
     */
    @Test
    public void abortAfterLoginBeforeCommitWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login, but before commit, failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed after a previous abort fails
     */
    @Test
    public void abortFailAfterCompletedPreviousAbort()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        // first do a regular login + commit and abort...
        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        // ...then try to abort again, which should not throw any exceptions
        error = "Abort after previous abort login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that an abort that is executed after a previous abort fails with audit and message queue being turned on
     * (default audit and message queue implementations are being used)
     */
    @Test
    public void abortFailAfterCompletedPreviousAbortWithAuditAndMessageQ()
            throws LoginException {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        // first do a regular login + commit and abort...
        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Abort after successful login failed";
        try {
            assertThat(error, loginModule.abort(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        // ...then try to abort again, which should not throw any exceptions
        error = "Abort after previous abort login must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Abort error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout before a login works
     */
    @Test
    public void logoutSuccessBeforeLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Logout before login must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout before a login works with audit and message queue being turned on (default audit and message
     * queue implementations are being used)
     */
    @Test
    public void logoutSuccessBeforeLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        String error = "Logout before login must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a successful login before commit works
     */
    @Test
    public void logoutSuccessAfterLoginBeforeCommit() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a successful login before commit works with audit and message queue being turned on
     * (default audit and message queue implementations are being used)
     */
    @Test
    public void logoutSuccessAfterLoginBeforeCommitWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a failed login works
     */
    @Test
    public void logoutSuccessAfterFailedLogin() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a failed login works with audit and message queue being turned on (default audit and
     * message queue implementations are being used)
     */
    @Test
    public void logoutSuccessAfterFailedLoginWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "password"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            // ignore this exception so that the process can continue
            // this should obviously never not happen in real life applications
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a commit works
     */
    @Test
    public void logoutSuccessAfterLoginAfterCommit() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a logout after a commit works with audit and message queue being turned on (default audit and message
     * queue implementations are being used)
     */
    @Test
    public void logoutSuccessAfterLoginAfterCommitWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.commit();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Logout after login before commit must work";
        try {
            assertThat(error, loginModule.logout(), is(equalTo(true)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit after a logout fails
     */
    @Test
    public void commitFailAfterLogout() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.logout();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after logout must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that a commit after a logout fails with audit and message queue being turned on (default audit and message
     * queue implementations are being used)
     */
    @Test
    public void commitFailAfterLogoutWithAuditAndMessageQ() {

        Map<String, String> sharedState = new ConcurrentHashMap<>();
        Map<String, String> options = new ConcurrentHashMap<>();
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.authenticator.DummyPasswordAuthenticator");
        options.put(JaasPropsBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator");

        PasswordLoginModule loginModule = new PasswordLoginModule();
        loginModule.initialize(new Subject(), createCallbackHandler("domain", "username", "username"),
                sharedState, options);

        try {
            loginModule.login();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Login error");
            ae.initCause(e);
            throw ae;
        }

        try {
            loginModule.logout();
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Logout error");
            ae.initCause(e);
            throw ae;
        }

        String error = "Commit after logout must fail";
        try {
            assertThat(error, loginModule.commit(), is(equalTo(false)));
        } catch (LoginException e) {
            AssertionError ae = new AssertionError("Commit error");
            ae.initCause(e);
            throw ae;
        }
    }


    // Utility methods
    // ---------------

    private static CallbackHandler createCallbackHandler(final String domain, final String username, final String password) {

        return new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback c : callbacks) {
                    if (c instanceof TextInputCallback) {
                        ((TextInputCallback) c).setText(domain);
                    } else if (c instanceof NameCallback) {
                        ((NameCallback) c).setName(username);
                    } else if (c instanceof PasswordCallback) {
                        ((PasswordCallback) c).setPassword(password.toCharArray());
                    } else {
                        throw new UnsupportedCallbackException(c);
                    }

                }
            }
        };
    }

    private static CallbackHandler createCallbackHandlerWithoutDomain(final String username, final String password) {

        return new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                for (Callback c : callbacks) {
                    if (c instanceof NameCallback) {
                        ((NameCallback) c).setName(username);
                    } else if (c instanceof PasswordCallback) {
                        ((PasswordCallback) c).setPassword(password.toCharArray());
                    } else {
                        throw new UnsupportedCallbackException(c);
                    }
                }
            }
        };
    }
}
