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
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordAuthenticator;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordValidator;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc.propsbuilder.JaasBasedConnPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc.propsbuilder.JaasBasedDbPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext.PlainTextPasswordValidator;
import org.beiter.michael.db.FactoryException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test the {@link JdbcPasswordAuthenticator} using a connection pool based connection.
 * This class focuses on the connection pool piece in the authentication flow.
 */
public class JdbcPasswordAuthenticatorPoolTest {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcPasswordAuthenticatorPoolTest.class);

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
     * Test that a missing driver name in the configuration throws a LoginException
     */
    @Test(expected = LoginException.class)
    public void missingDriverNameTest()
            throws LoginException {

        Map<String, String> properties = new ConcurrentHashMap<>();
        properties.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        properties.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        properties.put(JaasBasedDbPropsBuilder.KEY_SQL_USER_QUERY,
                "SELECT id, password FROM user_plaintext WHERE domain = ? AND username = ?");
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
     * Authenticate a user with the correct credentials (i.e. username = password), using a plain text password
     * validator via a JDBC connection.
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
}
