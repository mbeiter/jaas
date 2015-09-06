/*
 * #%L
 * This file is part of a universal JAAS library, providing a universal JDBC
 * authenticator implementation.
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
package org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc;

import org.beiter.michael.db.ConnectionFactory;
import org.beiter.michael.db.ConnectionProperties;
import org.beiter.michael.db.FactoryException;
import org.beiter.michael.db.propsbuilder.MapBasedConnPropsBuilder;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A utility class that starts an H2 database server and populates it with test data for username / password validation
 * to test the JDBC database connection and JAAS module.
 */
public class H2Server {

    private static final Logger LOG = LoggerFactory.getLogger(H2Server.class);

    public static final String DRIVER = "org.h2.Driver";
    public static final String DATABASE = "h2test_db";
    public static final String URL = "jdbc:h2:mem:" + DATABASE;
    public static final String USER = "someUser";
    public static final String PASSWORD = "noPassword";

    private static AtomicBoolean serverRunning = new AtomicBoolean(false);
    private static volatile Server server;

    // prevent instantiation of this class
    private H2Server() {
    }

    /**
     * Initializes the default schema and sets default values (if feasible). This deletes any data that are already in
     * the database!
     *
     * @throws FactoryException When there is a problem getting a connection to the database
     * @throws SQLException     When there is a problem initializing the database. Note that this can leave the database in
     *                          an inconsistent state.
     */
    public static synchronized void init()
            throws FactoryException, SQLException {

        LOG.info("Initializing the in-memory database server with a default schema and default values");

        ConnectionProperties connSpec = MapBasedConnPropsBuilder.buildDefault();
        connSpec.setDriver(DRIVER);
        connSpec.setUrl(URL);
        connSpec.setUsername(USER);
        connSpec.setPassword(PASSWORD);

        Connection con = ConnectionFactory.getConnection(connSpec);

        String stmt = "DROP TABLE IF EXISTS user_plaintext";
        con.prepareStatement(stmt).execute();

        // a user table that stores the password in plain text together with the username
        stmt = "CREATE TABLE user_plaintext ("
                + " id INT NOT NULL PRIMARY KEY,"
                + " domain VARCHAR(255) NOT NULL,"
                + " username VARCHAR(255) NOT NULL,"
                + " password VARCHAR(255)"
                + ")";
        con.prepareStatement(stmt).execute();

        stmt = "INSERT INTO user_plaintext (id, domain, username, password) VALUES "
                + "  (11, 'domain1', 'user1', 'user1')"
                + ", (12, 'domain1', 'user2', '')"
                + ", (13, 'domain1', 'user3', 'password3')"
                + ", (21, 'domain2', 'user1', 'password1')"
                + ", (22, 'domain2', 'user2', 'password2')";
        con.prepareStatement(stmt).execute();

        // check that the user insert was successful
        stmt = "SELECT COUNT(id) AS count FROM user_plaintext";
        ResultSet rs = con.prepareStatement(stmt).executeQuery();

        if (rs.next()) {
            if (rs.getInt("count") != 5) {
                String error = "Result set row count mismatch: expected 5, found " + rs.getInt("count");
                LOG.warn(error);
                throw new IllegalStateException(error);
            }
        } else {
            String error = "The result set is empty";
            LOG.warn(error);
            throw new IllegalStateException(error);
        }

        con.close();
    }

    /**
     * Starts the server, but does not perform any database schema initialization
     *
     * @throws SQLException When the server cannot be started
     */
    public static void start()
            throws SQLException {

        LOG.info("Checking if the in-memory database server can be started");

        // make sure the URL type and database type match
        if (!URL.startsWith("jdbc:h2:mem:")) {
            final String error = "Not an H2 connection URL: " + URL;
            LOG.error(error);
            throw new IllegalStateException(error);
        }

        // load the driver
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            // a SQL exception is not the ideal thing to throw here, but it is good enough for a testing setup like this
            String error = "Driver class not found, cannot start the DB server: " + DRIVER;
            LOG.error(error, e);
            throw new SQLException(error, e);
        }

        // start the server only if there is not already a server running
        if (serverRunning.compareAndSet(false, true)) {

            try {
                LOG.info("Starting the in-memory database server");
                server = Server.createTcpServer();
                server.start();
                LOG.info("In-memory database server started");
            } catch (SQLException e) {
                serverRunning.set(false);
                LOG.warn("In-memory database server not started dues to error");
                throw new SQLException(e);
            }
        } else {
            LOG.info("In-memory database server already running");
        }


        // connect to the DB (using the internal H2 connection pool) to trigger creation of an empty database owned by
        // the correct user. The H2 documentation says that even if we throw the pool away immediately, using the pool
        // is still faster than getting a direct connection with the driver manager...
        JdbcConnectionPool cp = JdbcConnectionPool.create(URL, USER, PASSWORD);
        Connection conn = cp.getConnection();
        conn.close();
        cp.dispose();
    }

    /**
     * Stops the server
     */
    public static void stop() {

        LOG.info("Checking if the in-memory database server can be stopped");

        // stop the server only if there is a server running
        if (serverRunning.compareAndSet(true, false)) {

            LOG.info("Stopping the in-memory database server");
            server.stop();
            LOG.info("In-memory database server stopped");
        } else
            LOG.info("In-memory database server not running");
    }

    /**
     * Restarts the server, but does not perform any database schema initialization
     *
     * @throws SQLException When the server cannot be started
     */
    public static void restart()
            throws SQLException {

        stop();
        start();
    }
}
