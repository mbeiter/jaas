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

import org.beiter.michael.authn.jaas.common.ConfigOptions;
import org.beiter.michael.db.ConnectionPoolSpec;
import org.beiter.michael.db.ConnectionSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This class provides utility methods to process the JDBC configuration settings and make them available as higher
 * level objects.
 */
// This class has high cyclomatic complexity due to the required configuration validation rules.
// This also triggers the "God Class" report to be triggered.
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity", "PMD.GodClass"})
// CHECKSTYLE:ON
public final class ConfigUtil {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * A private constructor to prevent instantiation of this class
     */
    private ConfigUtil() {
    }

    /**
     * Get a connectionspec based on the provided configuration.
     * <p/>
     * While {@code url}, {@code user}, and {@code password} are set in the returned spec (i.e. not {@code null}),
     * these values may be empty if not required by the database.
     *
     * @param properties The configuration properties to create the connection spec from
     * @return A connection spec
     */
    public static ConnectionSpec getConnectionSpec(final Map<String, ?> properties) {

        // url, user, and password may be blank
        String url = getOption(JdbcConfigOptions.POOL_URL, properties);
        if (url == null) {
            url = "";
        }
        String user = getOption(JdbcConfigOptions.POOL_USER, properties);
        if (user == null) {
            user = "";
        }
        String password = getOption(JdbcConfigOptions.POOL_PASSWORD, properties);
        if (password == null) {
            // FORTIFY AUDITOR:
            // This parameter depends on the database runtime configuration, and is not a code issues
            password = "";
        }

        return new ConnectionSpec(url, user, password);
    }

    /**
     * Get a connection pool spec based on the provided configuration.
     *
     * @param properties The configuration properties to create the connection pool spec from
     * @return A connection pool spec
     */
    // Method length is long due to the high number of available config options
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public static ConnectionPoolSpec getConnectionPoolSpec(final Map<String, ?> properties) {

        final ConnectionPoolSpec tempConnPoolSpec = new ConnectionPoolSpec();

        String value = getOption(JdbcConfigOptions.POOL_MAXTOTAL, properties);
        if (value != null && value.matches("^-?\\d+$")) {
            tempConnPoolSpec.setMaxTotal(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_MAXTOTAL.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_MAXIDLE, properties);
        if (value != null && value.matches("^-?\\d+$")) {
            tempConnPoolSpec.setMaxIdle(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_MAXIDLE.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_MINIDLE, properties);
        if (value != null && value.matches("^\\d+$")) {
            tempConnPoolSpec.setMinIdle(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_MINIDLE.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_MAXWAITMILLIS, properties);
        if (value != null && value.matches("^-?\\d+$")) {
            tempConnPoolSpec.setMaxWaitMillis(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_MAXWAITMILLIS.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TESTONCREATE, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setTestOnCreate(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TESTONCREATE.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TESTONBORROW, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setTestOnBorrow(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TESTONBORROW.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TESTONRETURN, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setTestOnReturn(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TESTONRETURN.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TESTWHILEIDLE, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setTestWhileIdle(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TESTWHILEIDLE.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TIMEBETWEENEVICTIONRUNS, properties);
        if (value != null && value.matches("^-?\\d+$")) {
            tempConnPoolSpec.setTimeBetweenEvictionRunsMillis(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TIMEBETWEENEVICTIONRUNS.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_NUMTESTSPEREVICTIONRUN, properties);
        if (value != null && value.matches("^\\d+$")) {
            tempConnPoolSpec.setNumTestsPerEvictionRun(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_NUMTESTSPEREVICTIONRUN.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_ISLIFO, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setLifo(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_ISLIFO.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_AUTOCOMMIT, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setDefaultAutoCommit(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_AUTOCOMMIT.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_READONLY, properties);
        if (value != null && value.equalsIgnoreCase("true")) {
            tempConnPoolSpec.setDefaultReadOnly(true);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_READONLY.getName() + " is not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_TRANSACTIONISOLATION, properties);
        if (value != null && value.matches("^\\d+$")) {
            tempConnPoolSpec.setDefaultTransactionIsolation(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_TRANSACTIONISOLATION.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_VALIDATIONQUERY, properties);
        if (value != null && value.length() > 0) {
            tempConnPoolSpec.setValidationQuery(value);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_VALIDATIONQUERY.getName() + " not set");
            }
        }

        value = getOption(JdbcConfigOptions.POOL_MAXCONNLIFETIME, properties);
        if (value != null && value.matches("^-?\\d+$")) {
            tempConnPoolSpec.setMaxConnLifetimeMillis(Integer.parseInt(value));
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Option " + JdbcConfigOptions.POOL_MAXCONNLIFETIME.getName() + " not set");
            }
        }

        return tempConnPoolSpec;
    }

    /**
     * Return the value of a JAAS configuration parameter.
     *
     * @param <T>        The type of the element
     * @param key        The key to retrieve from the options
     * @param properties The properties to retrieve values from
     * @return The configuration value for the provided key
     */
    @SuppressWarnings("unchecked")
    private static <T> T getOption(final ConfigOptions key, final Map<String, ?> properties) {

        // private method asserts
        assert key != null : "The key cannot be null";

        return (T) properties.get(key.getName());
    }
}
