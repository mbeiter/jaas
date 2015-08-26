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

import org.beiter.michael.db.ConnectionProperties;
import org.beiter.michael.db.propsbuilder.MapBasedConnPropsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class builds a set of {@link ConnectionProperties} using the settings obtained from a
 * JAAS Properties Map.
 *
 * <p/>
 * Use the keys from the various KEY_* fields to properly populate the JAAS Properties Map before calling this class'
 * methods. Otherwise, the defaults from {@link org.beiter.michael.db.propsbuilder.MapBasedConnPropsBuilder} will be
 * used.
 */
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
// suppress warnings about the long variable names that are "inherited" from Apache DBCP (which I used as a blueprint)
@SuppressWarnings({"PMD.LongVariable"})
// CHECKSTYLE:ON
public final class JaasPropsBasedConnPropsBuilder {

    /**
     * The logger object for this class
     */
    //private static final Logger LOG = LoggerFactory.getLogger(JaasPropsBasedConnPropsBuilder.class);

    // #####################
    // # Configuration Keys
    // #####################

    /**
     * @see ConnectionProperties#setDriver(String)
     */
    public static final String KEY_DRIVER = "jaas.jdbc.jdbcPool.driver";

    /**
     * @see ConnectionProperties#setUrl(String)
     */
    public static final String KEY_URL = "jaas.jdbc.jdbcPool.url";

    /**
     * @see ConnectionProperties#setUsername(String)
     */
    public static final String KEY_USERNAME = "jaas.jdbc.jdbcPool.username";

    /**
     * @see ConnectionProperties#setPassword(String)
     */
    // Fortify will report a violation here for handling a hardcoded password, which is not the case.
    // This is a non-issue / false positive.
    public static final String KEY_PASSWORD = "jaas.jdbc.jdbcPool.password";

    /**
     * @see ConnectionProperties#setMaxTotal(int)
     */
    public static final String KEY_MAX_TOTAL = "jaas.jdbc.jdbcPool.maxTotal";

    /**
     * @see ConnectionProperties#setMaxIdle(int)
     */
    public static final String KEY_MAX_IDLE = "jaas.jdbc.jdbcPool.maxIdle";

    /**
     * @see ConnectionProperties#setMinIdle(int)
     */
    public static final String KEY_MIN_IDLE = "jaas.jdbc.jdbcPool.minIdle";

    /**
     * @see ConnectionProperties#setMaxWaitMillis(long)
     */
    public static final String KEY_MAX_WAIT_MILLIS = "jaas.jdbc.jdbcPool.maxWaitMillis";

    /**
     * @see ConnectionProperties#setTestOnCreate(boolean)
     */
    public static final String KEY_TEST_ON_CREATE = "jaas.jdbc.jdbcPool.testOnCreate";

    /**
     * @see ConnectionProperties#setTestOnBorrow(boolean)
     */
    public static final String KEY_TEST_ON_BORROW = "jaas.jdbc.jdbcPool.testOnBorrow";

    /**
     * @see ConnectionProperties#setTestOnReturn(boolean)
     */
    public static final String KEY_TEST_ON_RETURN = "jaas.jdbc.jdbcPool.testOnReturn";

    /**
     * @see ConnectionProperties#setTestWhileIdle(boolean)
     */
    public static final String KEY_TEST_WHILE_IDLE = "jaas.jdbc.jdbcPool.testWhileIdle";

    /**
     * @see ConnectionProperties#setTimeBetweenEvictionRunsMillis(long)
     */
    public static final String KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS =
            "jaas.jdbc.jdbcPool.timeBetweenEvictionRuns";

    /**
     * @see ConnectionProperties#setNumTestsPerEvictionRun(int)
     */
    public static final String KEY_NUM_TESTS_PER_EVICITON_RUN = "jaas.jdbc.jdbcPool.numTestsPerEvictionRun";

    /**
     * @see ConnectionProperties#setMinEvictableIdleTimeMillis(long)
     */
    public static final String KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS = "jaas.jdbc.jdbcPool.minEvictableTimeMillis";

    /**
     * @see ConnectionProperties#setSoftMinEvictableIdleTimeMillis(long)
     */
    public static final String KEY_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS =
            "jaas.jdbc.jdbcPool.softMinEvictableIdleTimeMillis";

    /**
     * @see ConnectionProperties#setLifo(boolean)
     */
    public static final String KEY_LIFO = "jaas.jdbc.jdbcPool.lifo";

    /**
     * @see ConnectionProperties#setDefaultAutoCommit(boolean)
     */
    public static final String KEY_AUTO_COMMIT = "jaas.jdbc.jdbcPool.autoCommit";

    /**
     * @see ConnectionProperties#setDefaultReadOnly(boolean)
     */
    public static final String KEY_READ_ONLY = "jaas.jdbc.jdbcPool.readOnly";

    /**
     * @see ConnectionProperties#setDefaultTransactionIsolation(int)
     */
    public static final String KEY_TRANSACTION_ISOLATION = "jaas.jdbc.jdbcPool.transactionIsolation";

    /**
     * @see ConnectionProperties#setCacheState(boolean)
     */
    public static final String KEY_CACHE_STATE = "jaas.jdbc.jdbcPool.cacheState";

    /**
     * @see ConnectionProperties#setValidationQuery(String)
     */
    public static final String KEY_VALIDATION_QUERY = "jaas.jdbc.jdbcPool.validationQuery";

    /**
     * @see ConnectionProperties#setMaxConnLifetimeMillis(long)
     */
    public static final String KEY_MAX_CONN_LIFETIME_MILLIS = "jaas.jdbc.jdbcPool.maxConnLifetimeMillis";


    /**
     * A private constructor to prevent instantiation of this class
     */
    private JaasPropsBasedConnPropsBuilder() {
    }

    /**
     * Creates a set of connection properties that use the defaults as specified in this class.
     *
     * @return A set of connection properties with (reasonable) defaults
     * @see JaasPropsBasedConnPropsBuilder
     */
    public static ConnectionProperties buildDefault() {

        return build(new ConcurrentHashMap<String, String>());
    }

    /**
     * Initialize a set of connection properties based on key / values in a <code>HashMap</code>.
     *
     * @param properties A <code>HashMap</code> with configuration properties as required by the init() method in JAAS,
     *                   using the keys as specified in this class
     * @return A <code>ConnectionProperties</code> object with default values, plus the provided parameters
     */
    public static ConnectionProperties build(final Map<String, ?> properties) {

        final HashMap<String, String> tmpConfig = new HashMap<>();
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_DRIVER, (String) getOption(KEY_DRIVER, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_URL, (String) getOption(KEY_URL, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_USERNAME, (String) getOption(KEY_USERNAME, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_PASSWORD, (String) getOption(KEY_PASSWORD, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MAX_TOTAL, (String) getOption(KEY_MAX_TOTAL, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MAX_IDLE, (String) getOption(KEY_MAX_IDLE, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MIN_IDLE, (String) getOption(KEY_MIN_IDLE, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MAX_WAIT_MILLIS,
                (String) getOption(KEY_MAX_WAIT_MILLIS, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TEST_ON_CREATE, (String) getOption(KEY_TEST_ON_CREATE, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TEST_ON_BORROW, (String) getOption(KEY_TEST_ON_BORROW, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TEST_ON_RETURN, (String) getOption(KEY_TEST_ON_RETURN, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TEST_WHILE_IDLE,
                (String) getOption(KEY_TEST_WHILE_IDLE, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS,
                (String) getOption(KEY_TIME_BETWEEN_EVICTION_RUNS_MILLIS, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_NUM_TESTS_PER_EVICITON_RUN,
                (String) getOption(KEY_NUM_TESTS_PER_EVICITON_RUN, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS, (
                String) getOption(KEY_MIN_EVICTABLE_IDLE_TIME_MILLIS, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS,
                (String) getOption(KEY_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_LIFO, (String) getOption(KEY_LIFO, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_AUTO_COMMIT, (String) getOption(KEY_AUTO_COMMIT, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_READ_ONLY, (String) getOption(KEY_READ_ONLY, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_TRANSACTION_ISOLATION,
                (String) getOption(KEY_TRANSACTION_ISOLATION, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_CACHE_STATE, (String) getOption(KEY_CACHE_STATE, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_VALIDATION_QUERY,
                (String) getOption(KEY_VALIDATION_QUERY, properties));
        tmpConfig.put(MapBasedConnPropsBuilder.KEY_MAX_CONN_LIFETIME_MILLIS,
                (String) getOption(KEY_MAX_CONN_LIFETIME_MILLIS, properties));

        // tmpConfig now holds all the configuration values with the keys expected by the Util libs connection
        // properties builder. We will let that library do all the hard work of setting reasonable defaults and
        // dealing with null values...
        return MapBasedConnPropsBuilder.build(tmpConfig);
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
}
