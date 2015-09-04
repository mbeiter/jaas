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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.authenticator.jdbc.propsbuilder.JaasPropsBasedConnPropsBuilder;
import org.beiter.michael.authn.jaas.authenticator.jdbc.propsbuilder.JaasPropsBasedDbPropsBuilder;
import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.UserPrincipal;
import org.beiter.michael.authn.jaas.common.Util;
import org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticator;
import org.beiter.michael.authn.jaas.common.validator.PasswordValidator;
import org.beiter.michael.db.ConnectionProperties;
import org.beiter.michael.db.ConnectionFactory;
import org.beiter.michael.db.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This authenticator performs validation of username / password type credentials against JDBC databases.
 * <p/>
 * While any type of JDBC database should be supported, this class has only been tested with MySQL.
 */
public class JdbcPasswordAuthenticator
        implements PasswordAuthenticator {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JdbcPasswordAuthenticator.class);

    /**
     * The JNDI name of the connection (if JNDI being used)
     */
    // Using an AtomicReference is not required here, but will not add any significant overhead, makes Findbugs happy,
    // and may be useful in the future for compare-and-set operations.
    private static AtomicReference<DbProperties> dbProps = new AtomicReference<>();

    /**
     * The connection pool spec (if a connection pool is being used instead of JNDI)
     */
    // Using an AtomicReference is not required here, but will not add any significant overhead, makes Findbugs happy,
    // and may be useful in the future for compare-and-set operations.
    private static AtomicReference<ConnectionProperties> connProps = new AtomicReference<>();

    /**
     * {@inheritDoc}
     * <p/>
     * See module documentation for a list of available options.
     */
    @Override
    public final void init(final CommonProperties properties) {

        Validate.notNull(properties);

        // no need for defensive copies here, as all internal config values are calculated

        // some caching would be nice, but then we would have to allow resets in case the application wants to reload
        // its configuration at some point - seems not worth the hassle at this point.
        LOG.info("Parsing connection properties configuration");
        connProps.set(JaasPropsBasedConnPropsBuilder.build(properties.getAdditionalProperties()));

        // same statement about caching :)
        LOG.info("Parsing database properties configuration");
        dbProps.set(JaasPropsBasedDbPropsBuilder.build(properties.getAdditionalProperties()));
    }

    /**
     * Authenticate a user by validating the user's password, returning a Subject with one or more {@code Principal}s
     * set (if validation was successful), or throw a {@code LoginException} (if validation fails)
     * <p/>
     * This method connects to the database as configured in the the configuration provided during initialization, and
     * uses the configured parameterized SQL query to retrieve a user's ID and credential using the provided userName
     * and domain to locate them in the database. If initialization has not been completed, the authentication fails.
     * <p/>
     * The configured SQL query must take two query parameters, the first being the domain, the second being the
     * userName. If the domain is not used in the database, the SQL query must be crafted so that the first parameter
     * is irrelevant.
     * <p/>
     * The configured SQL query must return two columns, the first being the user's ID, the second being the credential
     * against which the provided password is to be validated.
     * <p/>
     * If the JDBC call failed, no record could be found, the parameters do not match, or similar SQL issues ocured,
     * a {@link javax.security.auth.login.LoginException} is thrown.
     * <p/>
     * The provided password is validated against the credential using the parent class' {@code passwordValidator}
     * object.
     * <p/>
     * If the validation fails (i.e. the provided password does not match the credential), a
     * {@link javax.security.auth.login.FailedLoginException} is thrown.
     * <p/>
     * If the validation is successful, a {@code Subject} is populated with three principals is returned: The
     * user's ID as returned by the database, and both the user provided domain and the user provided principal (i.e.
     * the the identifiers used to authenticate the users).
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
    // The logical flow in this method is not overly complex, the warning results from parameter validation
    // CHECKSTYLE:OFF
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    // CHECKSTYLE:ON
    public final Subject authenticate(final String domain, final String userName, final char[] password,
                                      final PasswordValidator passwordValidator)
            throws LoginException {

        // make sure the credentials are not null
        if (domain == null || userName == null || password == null) {
            throw new LoginException("The credentials cannot be null");
        }

        // SQL query is required
        if (StringUtils.isBlank(dbProps.get().getSqlUserQuery())) {
            final String error = "Invalid SQL user authentication query (query is null or empty)";
            LOG.warn(error);
            throw new LoginException(error);
        }

        final UserRecord userRecord = getUserRecord(domain, userName);

        if (userRecord.getUserId() == null || userRecord.getUserId().length() == 0) {
            final String error = "User ID for username '" + userName + "' is null or empty in the database";
            LOG.warn(error);
            throw new LoginException(error);
        }

        if (userRecord.getCredential() == null || userRecord.getCredential().length() == 0) {
            final String error = "Credential for username '" + userName + "' / user ID '" + userRecord.getUserId()
                    + "' is null or empty in the database";
            LOG.warn(error);
            throw new LoginException(error);
        }

        // no need for defensive copies of Strings, but create a defensive copy of the password
        final char[] myPassword = password.clone();

        // convert the credential string to a char array
        final char[] myCredential = userRecord.getCredential().toCharArray();

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
        final UserPrincipal userPrincipal = new UserPrincipal(userRecord.getUserId(), domain, userName);

        // wrap the principal in a Subject
        final Subject subject = new Subject();
        subject.getPrincipals().add(userPrincipal);

        return subject;
    }

    /**
     * Retrieve a user record from the database, with the user record being uniquely identified through {@code domain}
     * and {@code userName}.
     *
     * @param domain   The domain in which the {@code userName} is located
     * @param userName The username to search for
     * @return A user record containing domain, username, user ID (from the DB), and credentials (from the DB)
     * @throws LoginException When the JDBC cvonnection failed, or the username / domain combination could not be found
     */
    // The SQL statement is retrieved from the configuration, and the admin is trusted
    @edu.umd.cs.findbugs.annotations.SuppressWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
    private UserRecord getUserRecord(final String domain, final String userName)
            throws LoginException {

        String userId;
        String credential;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getDatabaseConnection();

            statement = connection.prepareStatement(dbProps.get().getSqlUserQuery());
            statement.setString(1, domain);
            statement.setString(2, userName);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                userId = resultSet.getString(1);
                credential = resultSet.getString(2);
            } else {
                final String error = "Username '" + userName + "' does not exist (query returned zero results)";
                LOG.warn(error);
                throw new LoginException(error);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            final String error = "Error executing SQL query";
            LOG.warn(error, e);
            throw Util.newLoginException(error, e);
        } finally {
            DbUtil.close(resultSet);
            DbUtil.close(statement);
            DbUtil.close(connection);
        }

        return new UserRecord(domain, userName, userId, credential);
    }

    /**
     * Obtain a database connection - either a JNDI connection (directly from the factory), or a pooled JDBC connection
     *
     * @return a pooled database connection
     * @throws LoginException when the connection cannot be retrieved from JNDI or the connection pool, or the pool
     *                        cannot be created
     */
    private Connection getDatabaseConnection()
            throws LoginException {

        Connection connection;
        if (StringUtils.isNotEmpty(dbProps.get().getJndiConnectionName())) {
            try {
                connection = ConnectionFactory.getConnection(dbProps.get().getJndiConnectionName());
            } catch (FactoryException e) {
                final String error = "Could not retrieve JNDI database connection";
                LOG.warn(error, e);
                throw Util.newLoginException(error, e);
            }
        } else {
            try {
                // connection spec is required
                if (connProps.get() == null) {
                    final String error = "Database connection pool configuration has not been provided or initialized";
                    LOG.warn(error);
                    throw new FactoryException(error);
                }

                // driver is required
                if (StringUtils.isBlank(connProps.get().getDriver())) {
                    final String error = "Invalid database driver (driver name is null or empty)";
                    LOG.warn(error);
                    throw new FactoryException(error);
                }

                // url is required
                if (StringUtils.isBlank(connProps.get().getUrl())) {
                    final String error = "Invalid database URL (URL is null or empty)";
                    LOG.warn(error);
                    throw new FactoryException(error);
                }

                connection = ConnectionFactory.getConnection(connProps.get());

            } catch (FactoryException e) {
                final String error = "Could not create pooled database connection";
                LOG.warn(error, e);
                throw Util.newLoginException(error, e);
            }
        }

        return connection;
    }
}
