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
package org.beiter.michael.authn.jaas.authenticator.jdbc.propsbuilder;

import org.apache.commons.lang3.StringUtils;
import org.beiter.michael.authn.jaas.authenticator.jdbc.DbProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class builds a set of {@link DbProperties} using the settings obtained from a
 * JAAS Properties Map.
 *
 * <p/>
 * Use the keys from the various KEY_* fields to properly populate the JAAS Properties Map before calling this class'
 * methods.
 */
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
// suppress warnings about the long variable names
@SuppressWarnings({"PMD.LongVariable"})
// CHECKSTYLE:ON
public final class JaasPropsBasedDbPropsBuilder {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JaasPropsBasedDbPropsBuilder.class);

    // #################
    // # Default values
    // #################

    /**
     * @see DbProperties#setJndiConnectionName(String)
     */
    public static final String DEFAULT_JNDI_NAME = null;

    /**
     * @see DbProperties#setSqlUserQuery(String)
     */
    public static final String DEFAULT_SQL_USER_QUERY = null;

    // #####################
    // # Configuration Keys
    // #####################

    /**
     * @see DbProperties#setJndiConnectionName(String)
     */
    public static final String KEY_JNDI_CONNECTION_NAME = "jaas.jdbc.jndi.name";

    /**
     * @see DbProperties#setSqlUserQuery(String)
     */
    public static final String KEY_SQL_USER_QUERY = "jaas.jdbc.sql.userQuery";


    /**
     * A private constructor to prevent instantiation of this class
     */
    private JaasPropsBasedDbPropsBuilder() {
    }

    /**
     * Creates a set of database properties that use the defaults as specified in this class.
     *
     * @return A set of database properties with (reasonable) defaults
     * @see JaasPropsBasedDbPropsBuilder
     */
    public static DbProperties buildDefault() {

        return build(new ConcurrentHashMap<String, String>());
    }

    /**
     * Initialize a set of database properties based on key / values in a <code>HashMap</code>.
     *
     * @param properties A <code>HashMap</code> with configuration properties as required by the init() method in JAAS,
     *                   using the keys as specified in this class
     * @return A <code>DbProperties</code> object with default values, plus the provided parameters
     */
    public static DbProperties build(final Map<String, ?> properties) {

        final DbProperties dbSpec = new DbProperties();
        String tmp = getOption(KEY_JNDI_CONNECTION_NAME, properties);
        if (StringUtils.isNotEmpty(tmp)) { // JNDI connection name can be null or empty
            dbSpec.setJndiConnectionName(tmp);
            logValue(KEY_JNDI_CONNECTION_NAME, tmp);
        } else {
            dbSpec.setJndiConnectionName(DEFAULT_JNDI_NAME);
            logDefault(KEY_JNDI_CONNECTION_NAME, DEFAULT_JNDI_NAME);
        }

        tmp = getOption(KEY_SQL_USER_QUERY, properties);
        if (StringUtils.isNotEmpty(tmp)) { // sql query cannot be null or empty, defaulting to null to catch it
            dbSpec.setSqlUserQuery(tmp);
            logValue(KEY_SQL_USER_QUERY, tmp);
        } else {
            dbSpec.setSqlUserQuery(DEFAULT_SQL_USER_QUERY);
            logDefault(KEY_SQL_USER_QUERY, DEFAULT_SQL_USER_QUERY);
        }

        return dbSpec;
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
    private static <T> T getOption(final String key, final Map<String, ?> properties) {

        // private method asserts
        assert key != null : "The key cannot be null";

        return (T) properties.get(key);
    }

    /**
     * Create a log entry when a value has been successfully configured.
     *
     * @param key   The configuration key
     * @param value The value that is being used
     */
    private static void logValue(final String key, final String value) {

        // Fortify will report a violation here because of disclosure of potentially confidential information.
        // However, the configuration keys are not confidential, which makes this a non-issue / false positive.
        if (LOG.isInfoEnabled()) {
            final StringBuilder msg = new StringBuilder("Key found in configuration ('")
                    .append(key)
                    .append("'), using configured value (not disclosed here for security reasons)");
            LOG.info(msg.toString());
        }

        // Fortify will report a violation here because of disclosure of potentially confidential information.
        // The configuration VALUES are confidential. DO NOT activate DEBUG logging in production.
        if (LOG.isDebugEnabled()) {
            final StringBuilder msg = new StringBuilder("Key found in configuration ('")
                    .append(key)
                    .append("'), using configured value ('");
            if (value == null) {
                msg.append("null')");
            } else {
                msg.append(value).append("')");
            }
            LOG.debug(msg.toString());
        }
    }

    /**
     * Create a log entry when a default value is being used in case the propsbuilder key has not been provided in the
     * configuration.
     *
     * @param key          The configuration key
     * @param defaultValue The default value that is being used
     */
    private static void logDefault(final String key, final String defaultValue) {

        // Fortify will report a violation here because of disclosure of potentially confidential information.
        // However, neither the configuration keys nor the default propsbuilder values are confidential, which makes
        // this a non-issue / false positive.
        if (LOG.isInfoEnabled()) {
            final StringBuilder msg = new StringBuilder("Key is not configured ('")
                    .append(key)
                    .append("'), using default value ('");
            if (defaultValue == null) {
                msg.append("null')");
            } else {
                msg.append(defaultValue).append("')");
            }
            LOG.info(msg.toString());
        }
    }
}
