/*
 * #%L
 * This file is part of a universal JAAS library, providing a universal JDBC
 * authenticator implementation.
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
package org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.UserPrincipal;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc.propsbuilder.JaasBasedConnPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordAuthenticator;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordValidator;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc.propsbuilder.JaasBasedDbPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext.PlainTextPasswordValidator;
import org.beiter.michael.db.FactoryException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Test the {@link JdbcPasswordAuthenticator} (using a JDBC pooled connection, but that is secondary - the test focuses
 * on the actual authentication process).
 */
public class JdbcPasswordAuthenticatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcPasswordAuthenticatorTest.class);

    private static final String DRIVER = H2Server.DRIVER;
    private static final String URL = H2Server.URL;
    private static final String USER = H2Server.USER;
    private static final String PASSWORD = H2Server.PASSWORD;

    /**
     * Start the in-memory database server
     *
     * @throws java.sql.SQLException When the startup fails
     */
    @BeforeClass
    public static void startDbServer()
            throws SQLException {

        H2Server.start();
    }

    /**
     * Stops the in-memory database server
     */
    @AfterClass
    public static void stopDbServer() {

        H2Server.stop();
    }

    /**
     * Initialize the database with a default database schema + values
     *
     * @throws FactoryException      When no DB connection can be obtained
     * @throws java.sql.SQLException When the initialization fails
     */
    @Before
    public void initDatabase()
            throws FactoryException, SQLException {

        H2Server.init();
    }

    /**
     * Test that a missing SQL user query in the configuration throws a LoginException
     */
    @Test(expected = LoginException.class)
    public void missingSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "invalidDomain";
        String username = "invalidUser";
        char[] password = "invalidPassword".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that an broken SQL user query (i.e. invalid SQL syntax) in the configuration throws a LoginException
     */
    @Test(expected = LoginException.class)
    public void brokenSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "invalidDomain";
        String username = "invalidUser";
        char[] password = "invalidPassword".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that a SQL user query with an incorrect table name in the configuration throws a LoginException
     */
    @Test(expected = LoginException.class)
    public void invalidTableSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM invalid_table WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "invalidDomain";
        String username = "invalidUser";
        char[] password = "invalidPassword".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that a SQL user query with an incorrect parameter count (1 instead of 2) in the WHERE clause throws a
     * LoginException
     */
    @Test(expected = LoginException.class)
    public void tooLittleParameterCountSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "invalidDomain";
        String username = "invalidUser";
        char[] password = "invalidPassword".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that a SQL user query with an incorrect parameter count (3 instead of 2) in the WHERE claus throws a
     * LoginException
     */
    @Test(expected = LoginException.class)
    public void tooManyParameterCountSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ? AND password = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "invalidDomain";
        String username = "invalidUser";
        char[] password = "invalidPassword".toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that a SQL user query with an incorrect result count (1 instead of 2) throws a LoginException
     */
    @Test(expected = LoginException.class)
    public void tooLittleResultCountSqlQueryTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "domain1";
        String username = "user2";
        char[] password = username.toCharArray();

        try {
            pwAuthenticator.authenticate(domain, username, password, pwValidator);
        } catch (LoginException e) {
            if (FailedLoginException.class.isInstance(e)) {
                AssertionError ae = new AssertionError("Authentication attempt although SQL query was missing");
                ae.initCause(e);
                throw ae;
            } else {
                // rethrow expected exception
                throw e;
            }
        }
    }

    /**
     * Test that trying to authenticate a non-existing user fails
     */
    @Test(expected = LoginException.class)
    public void nonExistingUserTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "domain1";
        String username = "invalidUser";
        char[] password = username.toCharArray();

        pwAuthenticator.authenticate(domain, username, password, pwValidator);
    }

    /**
     * Test that trying to authenticate a user with an empty password fails
     */
    @Test(expected = LoginException.class)
    public void emptyPasswordUserTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "domain1";
        String username = "user2";
        char[] password = username.toCharArray();

        pwAuthenticator.authenticate(domain, username, password, pwValidator);
    }

    /**
     * Authenticate a user with invalid credentials (i.e. username != password), using a plain text password
     * validator.
     */
    @Test(expected = FailedLoginException.class)
    public void authenticationFailTest()
            throws FailedLoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);


        String domain = "domain1";
        String username = "user3";
        char[] password = username.toCharArray();

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

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);

        String domain = "domain1";
        String username = "user1";
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

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedConnPropsBuilder.KEY_DRIVER, DRIVER);
        properties.put(JaasBasedConnPropsBuilder.KEY_URL, URL);
        properties.put(JaasBasedConnPropsBuilder.KEY_USERNAME, USER);
        properties.put(JaasBasedConnPropsBuilder.KEY_PASSWORD, PASSWORD);
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(properties);

        // create plain text password validator
        PasswordValidator pwValidator = new PlainTextPasswordValidator();
        pwValidator.init(commonProps);

        // create JDBC authenticator
        PasswordAuthenticator pwAuthenticator = new JdbcPasswordAuthenticator();
        pwAuthenticator.init(commonProps);

        String domain = "domain1";
        String username = "user1";
        char[] password = username.toCharArray();
        String userID = "11";

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
        assertThat(error, principal.getName(), is(equalTo(userID)));

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
