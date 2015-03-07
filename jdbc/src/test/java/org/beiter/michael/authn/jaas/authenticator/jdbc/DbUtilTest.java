/*
 * #%L
 * This file is part of a universal JDBC JAAS module.
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
package org.beiter.michael.authn.jaas.authenticator.jdbc;

import org.beiter.michael.db.ConnectionFactory;
import org.beiter.michael.db.ConnectionPoolSpec;
import org.beiter.michael.db.ConnectionSpec;
import org.beiter.michael.db.FactoryException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DbUtilTest {

    private static final Logger LOG = LoggerFactory.getLogger(DbUtilTest.class);

    private static final String DRIVER = H2Server.DRIVER;
    private static final String URL = H2Server.URL;
    private static final String USER = H2Server.USER;
    private static final String PASSWORD = H2Server.PASSWORD;

    /**
     * Start the in-memory database server
     *
     * @throws SQLException When the startup fails
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
     * @throws SQLException     When the initialization fails
     * @throws FactoryException When the factory cannot be initialized
     */
    @Before
    public void initDatabase()
            throws SQLException, FactoryException {

        H2Server.init();
    }

    /**
     * Test that the direct factory method returns a connection, and we can successfully close that connection. Also
     * test that the method can close a null connection or an already closed connection without error.
     */
    @Test
    public void closeConnectionTest() {

        ConnectionSpec connSpec = new ConnectionSpec(URL, USER, PASSWORD);
        ConnectionPoolSpec poolSpec = new ConnectionPoolSpec();
        poolSpec.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        Connection conn;
        try {
            conn = ConnectionFactory.getConnection(DRIVER, connSpec, poolSpec);

            String error = "The DB connection is null";
            assertThat(error, conn, notNullValue());

        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Factory error");
            ae.initCause(e);
            throw ae;
        }

        DbUtil.close(conn);

        try {
            String error = "Error closing the connection";
            assertThat(error, conn.isClosed(), is(true));
        } catch (SQLException e) {
            AssertionError ae = new AssertionError("Error when checking the status of the JDBC connection");
            ae.initCause(e);
            throw ae;
        }

        // try to close the connection again (should not throw any errors)
        DbUtil.close(conn);

        // try to close a null connection (should not throw any errors)
        conn = null;
        DbUtil.close(conn);
    }

    /**
     * Test that we can prepare a statement, and then can successfully close that statement (and the connection). Also
     * test that the method can close a null statement or an already closed statement without error.
     */
    @Test
    public void closeStatementTest() {

        ConnectionSpec connSpec = new ConnectionSpec(URL, USER, PASSWORD);
        ConnectionPoolSpec poolSpec = new ConnectionPoolSpec();
        poolSpec.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        Connection conn;
        PreparedStatement statement;
        try {
            conn = ConnectionFactory.getConnection(DRIVER, connSpec, poolSpec);
            statement = conn.prepareStatement("SELECT COUNT(id) AS count FROM user_plaintext");

            String error = "The prepared statement is null";
            assertThat(error, statement, notNullValue());

        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Factory error");
            ae.initCause(e);
            throw ae;
        } catch (SQLException e) {
            AssertionError ae = new AssertionError("Error when preparing the statement");
            ae.initCause(e);
            throw ae;
        }

        DbUtil.close(conn);
        DbUtil.close(statement);

        try {
            String error = "Error closing the statement";
            assertThat(error, statement.isClosed(), is(true));
        } catch (SQLException e) {
            AssertionError ae = new AssertionError("Error when checking the status of the prepared statement");
            ae.initCause(e);
            throw ae;
        }

        // try to close the statement again (should not throw any errors)
        DbUtil.close(statement);

        // try to close a null statement (should not throw any errors)
        statement = null;
        DbUtil.close(statement);
    }

    /**
     * Test that we can execute a prepared statement, browse a result set, and then can successfully close that result
     * set (and the connection + the statement). Also test that the method can close a null result set or an already
     * closed result set without error.
     */
    @Test
    public void closeResultSetTest() {

        ConnectionSpec connSpec = new ConnectionSpec(URL, USER, PASSWORD);
        ConnectionPoolSpec poolSpec = new ConnectionPoolSpec();
        poolSpec.setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        Connection conn;
        PreparedStatement statement;
        ResultSet resultSet;
        try {
            conn = ConnectionFactory.getConnection(DRIVER, connSpec, poolSpec);
            statement = conn.prepareStatement("SELECT COUNT(id) AS count FROM user_plaintext");

            resultSet = statement.executeQuery();

            String error = "The result set is null";
            assertThat(error, resultSet, notNullValue());

            if (resultSet.next()) {
                if (resultSet.getInt("count") != 5) {
                    error = "Result set row count mismatch: expected 5, found " + resultSet.getInt("count");
                    LOG.warn(error);
                    throw new IllegalStateException(error);
                }
            } else {
                error = "The result set is empty";
                LOG.warn(error);
                throw new IllegalStateException(error);
            }

        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Factory error");
            ae.initCause(e);
            throw ae;
        } catch (SQLException e) {
            AssertionError ae = new AssertionError("Error when executing the query or browsing the result set");
            ae.initCause(e);
            throw ae;
        }

        DbUtil.close(conn);
        DbUtil.close(statement);
        DbUtil.close(resultSet);

        try {
            String error = "Error closing the result set";
            assertThat(error, statement.isClosed(), is(true));
        } catch (SQLException e) {
            AssertionError ae = new AssertionError("Error when checking the status of the result set");
            ae.initCause(e);
            throw ae;
        }

        // try to close the result set again (should not throw any errors)
        DbUtil.close(resultSet);

        // try to close a null result set (should not throw any errors)
        resultSet = null;
        DbUtil.close(resultSet);
    }

}
