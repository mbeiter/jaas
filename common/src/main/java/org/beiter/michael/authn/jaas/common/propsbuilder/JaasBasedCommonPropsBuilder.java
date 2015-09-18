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
package org.beiter.michael.authn.jaas.common.propsbuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class builds a set of {@link CommonProperties} using the settings obtained from a
 * JAAS Properties Map.
 * <p>
 * <p>
 * Use the keys from the various KEY_* fields to properly populate the JAAS Properties Map before calling this class'
 * methods.
 */
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
// suppress warnings about the long variable names
@SuppressWarnings({"PMD.LongVariable"})
// CHECKSTYLE:ON
public final class JaasBasedCommonPropsBuilder {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(JaasBasedCommonPropsBuilder.class);

    // #################
    // # Default values
    // #################

    /**
     * @see CommonProperties#setAuditClassName(String)
     */
    public static final String DEFAULT_AUDIT_CLASS_NAME =
            "org.beiter.michael.authn.jaas.common.audit.SampleAuditLogger";

    /**
     * @see CommonProperties#setAuditEnabled(boolean)
     */
    public static final boolean DEFAULT_AUDIT_IS_ENABLED = false;

    /**
     * @see CommonProperties#setAuditSingleton(boolean)
     */
    public static final boolean DEFAULT_AUDIT_IS_SINGLETON = true;

    /**
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    public static final String DEFAULT_MESSAGEQ_CLASS_NAME =
            "org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger";

    /**
     * @see CommonProperties#setMessageQueueEnabled(boolean)
     */
    public static final boolean DEFAULT_MESSAGEQ_IS_ENABLED = false;

    /**
     * @see CommonProperties#setMessageQueueSingleton(boolean)
     */
    public static final boolean DEFAULT_MESSAGEQ_IS_SINGLETON = true;

    /**
     * @see CommonProperties#setPasswordAuthenticatorClassName(String)
     */
    public static final String DEFAULT_PASSWORD_AUTHENTICATOR_CLASS_NAME = null;

    /**
     * @see CommonProperties#setPasswordAuthenticatorSingleton(boolean)
     */
    public static final boolean DEFAULT_PASSWORD_AUTHENTICATOR_IS_SINGLETON = true;

    /**
     * @see CommonProperties#setPasswordValidatorClassName(String)
     */
    public static final String DEFAULT_PASSWORD_VALIDATOR_CLASS_NAME = null;

    /**
     * @see CommonProperties#setPasswordValidatorSingleton(boolean)
     */
    public static final boolean DEFAULT_PASSWORD_VALIDATOR_IS_SINGLETON = true;

    // #####################
    // # Configuration Keys
    // #####################

    /**
     * @see CommonProperties#setAuditClassName(String)
     */
    public static final String KEY_AUDIT_CLASS_NAME = "jaas.audit.class";

    /**
     * @see CommonProperties#setAuditEnabled(boolean)
     */
    public static final String KEY_AUDIT_IS_ENABLED = "jaas.audit.isEnabled";

    /**
     * @see CommonProperties#setAuditSingleton(boolean) (boolean)
     */
    public static final String KEY_AUDIT_IS_SINGLETON = "jaas.audit.isSingleton";

    /**
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    public static final String KEY_MESSAGEQ_CLASS_NAME = "jaas.messageq.class";

    /**
     * @see CommonProperties#setMessageQueueEnabled(boolean)
     */
    public static final String KEY_MESSAGEQ_IS_ENABLED = "jaas.messageq.isEnabled";

    /**
     * @see CommonProperties#setMessageQueueSingleton(boolean)
     */
    public static final String KEY_MESSAGEQ_IS_SINGLETON = "jaas.messageq.isSingleton";

    /**
     * @see CommonProperties#setPasswordAuthenticatorClassName(String)
     */
    // Fortify will report a violation here for handling a hardcoded password, which is not the case.
    // This is a non-issue / false positive.
    public static final String KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME = "jaas.password.authenticator.class";

    /**
     * @see CommonProperties#setPasswordAuthenticatorSingleton(boolean)
     */
    // Fortify will report a violation here for handling a hardcoded password, which is not the case.
    // This is a non-issue / false positive.
    public static final String KEY_PASSWORD_AUTHENTICATOR_IS_SINGLETON = "jaas.password.authenticator.isSingleton";

    /**
     * @see CommonProperties#setPasswordValidatorClassName(String)
     */
    // Fortify will report a violation here for handling a hardcoded password, which is not the case.
    // This is a non-issue / false positive.
    public static final String KEY_PASSWORD_VALIDATOR_CLASS_NAME = "jaas.password.validator.class";

    /**
     * @see CommonProperties#setPasswordValidatorSingleton(boolean)
     */
    // Fortify will report a violation here for handling a hardcoded password, which is not the case.
    // This is a non-issue / false positive.
    public static final String KEY_PASSWORD_VALIDATOR_IS_SINGLETON = "jaas.password.validator.isSingleton";

    /**
     * A private constructor to prevent instantiation of this class
     */
    private JaasBasedCommonPropsBuilder() {
    }

    /**
     * Creates a set of common properties that use the defaults as specified in this class.
     *
     * @return A set of database properties with (reasonable) defaults
     * @see JaasBasedCommonPropsBuilder
     */
    public static CommonProperties buildDefault() {

        return build(new ConcurrentHashMap<String, String>());
    }

    /**
     * Initialize a set of common properties based on key / values in a <code>HashMap</code>.
     *
     * @param properties A <code>HashMap</code> with configuration properties as required by the init() method in JAAS,
     *                   using the keys as specified in this class
     * @return A <code>DbProperties</code> object with default values, plus the provided parameters
     */
    // CHECKSTYLE:OFF
    // this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
    // suppress warnings about this method being too long (not much point in splitting up this one!)
    // suppress warnings about this method being too complex (can't extract a generic subroutine to reduce exec paths)
    @SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity"})
    // CHECKSTYLE:ON
    public static CommonProperties build(final Map<String, ?> properties) {

        Validate.notNull(properties);

        final CommonProperties commonProps = new CommonProperties();
        String tmp = getOption(KEY_AUDIT_CLASS_NAME, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setAuditClassName(tmp);
            logValue(KEY_AUDIT_CLASS_NAME, tmp);
        } else {
            commonProps.setAuditClassName(DEFAULT_AUDIT_CLASS_NAME);
            logDefault(KEY_AUDIT_CLASS_NAME, DEFAULT_AUDIT_CLASS_NAME);
        }

        tmp = getOption(KEY_AUDIT_IS_ENABLED, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setAuditEnabled(Boolean.parseBoolean(tmp));
            logValue(KEY_AUDIT_IS_ENABLED, tmp);
        } else {
            commonProps.setAuditEnabled(DEFAULT_AUDIT_IS_ENABLED);
            logDefault(KEY_AUDIT_IS_ENABLED, String.valueOf(DEFAULT_AUDIT_IS_ENABLED));
        }

        tmp = getOption(KEY_AUDIT_IS_SINGLETON, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setAuditSingleton(Boolean.parseBoolean(tmp));
            logValue(KEY_AUDIT_IS_SINGLETON, tmp);
        } else {
            commonProps.setAuditSingleton(DEFAULT_AUDIT_IS_SINGLETON);
            logDefault(KEY_AUDIT_IS_SINGLETON, String.valueOf(DEFAULT_AUDIT_IS_SINGLETON));
        }

        tmp = getOption(KEY_MESSAGEQ_CLASS_NAME, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setMessageQueueClassName(tmp);
            logValue(KEY_MESSAGEQ_CLASS_NAME, tmp);
        } else {
            commonProps.setMessageQueueClassName(DEFAULT_MESSAGEQ_CLASS_NAME);
            logDefault(KEY_MESSAGEQ_CLASS_NAME, DEFAULT_MESSAGEQ_CLASS_NAME);
        }

        tmp = getOption(KEY_MESSAGEQ_IS_ENABLED, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setMessageQueueEnabled(Boolean.parseBoolean(tmp));
            logValue(KEY_MESSAGEQ_IS_ENABLED, tmp);
        } else {
            commonProps.setMessageQueueEnabled(DEFAULT_MESSAGEQ_IS_ENABLED);
            logDefault(KEY_MESSAGEQ_IS_ENABLED, String.valueOf(DEFAULT_MESSAGEQ_IS_ENABLED));
        }

        tmp = getOption(KEY_MESSAGEQ_IS_SINGLETON, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setMessageQueueSingleton(Boolean.parseBoolean(tmp));
            logValue(KEY_MESSAGEQ_IS_SINGLETON, tmp);
        } else {
            commonProps.setMessageQueueSingleton(DEFAULT_MESSAGEQ_IS_SINGLETON);
            logDefault(KEY_MESSAGEQ_IS_SINGLETON, String.valueOf(DEFAULT_MESSAGEQ_IS_SINGLETON));
        }

        tmp = getOption(KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setPasswordAuthenticatorClassName(tmp);
            logValue(KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, tmp);
        } else {
            commonProps.setPasswordAuthenticatorClassName(DEFAULT_PASSWORD_AUTHENTICATOR_CLASS_NAME);
            logDefault(KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, DEFAULT_PASSWORD_AUTHENTICATOR_CLASS_NAME);
        }

        tmp = getOption(KEY_PASSWORD_AUTHENTICATOR_IS_SINGLETON, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setPasswordAuthenticatorSingleton(Boolean.parseBoolean(tmp));
            logValue(KEY_PASSWORD_AUTHENTICATOR_IS_SINGLETON, tmp);
        } else {
            commonProps.setPasswordAuthenticatorSingleton(DEFAULT_PASSWORD_AUTHENTICATOR_IS_SINGLETON);
            logDefault(KEY_PASSWORD_AUTHENTICATOR_IS_SINGLETON,
                    String.valueOf(DEFAULT_PASSWORD_AUTHENTICATOR_IS_SINGLETON));
        }

        tmp = getOption(KEY_PASSWORD_VALIDATOR_CLASS_NAME, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setPasswordValidatorClassName(tmp);
            logValue(KEY_PASSWORD_VALIDATOR_CLASS_NAME, tmp);
        } else {
            commonProps.setPasswordValidatorClassName(DEFAULT_PASSWORD_VALIDATOR_CLASS_NAME);
            logDefault(KEY_PASSWORD_VALIDATOR_CLASS_NAME, DEFAULT_PASSWORD_VALIDATOR_CLASS_NAME);
        }

        tmp = getOption(KEY_PASSWORD_VALIDATOR_IS_SINGLETON, properties);
        if (StringUtils.isNotEmpty(tmp)) {
            commonProps.setPasswordValidatorSingleton(Boolean.parseBoolean(tmp));
            logValue(KEY_PASSWORD_VALIDATOR_IS_SINGLETON, tmp);
        } else {
            commonProps.setPasswordValidatorSingleton(DEFAULT_PASSWORD_VALIDATOR_IS_SINGLETON);
            logDefault(KEY_PASSWORD_VALIDATOR_IS_SINGLETON, String.valueOf(DEFAULT_PASSWORD_VALIDATOR_IS_SINGLETON));
        }

        // set the additional properties, preserving the originally provided properties
        // create a defensive copy of the map and all its properties
        // the code looks a little complicated that "putAll()", but it catches situations where a Map is provided that
        // supports null values (e.g. a HashMap) vs Map implementations that do not (e.g. ConcurrentHashMap).
        final Map<String, String> tempMap = new ConcurrentHashMap<>();
        try {
            for (final Map.Entry<String, ?> entry : properties.entrySet()) {
                final String key = entry.getKey();
                final String value = (String) entry.getValue();

                if (value != null) {
                    tempMap.put(key, value);
                }
            }
        } catch (ClassCastException e) {
            final String error = "The values of the configured JAAS properties must be Strings. "
                    + "Sorry, but we do not support anything else here!";
            throw new IllegalArgumentException(error, e);
        }
        commonProps.setAdditionalProperties(tempMap);

        return commonProps;
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
