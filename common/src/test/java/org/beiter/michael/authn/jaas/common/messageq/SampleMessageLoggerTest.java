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
package org.beiter.michael.authn.jaas.common.messageq;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.Events;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasPropsBasedCommonPropsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class SampleMessageLoggerTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(SampleMessageLoggerTest.class);

    private Field fieldMessageLogger_enabled;

    /**
     * Make some of the private fields in the SampleMessageLogger class accessible.
     * <p/>
     * This is executed before every test to ensure consistency even if one of the tests mock with field accessibility.
     */
    @Before
    public void makeMessageLoggerPrivateFieldsAccessible() {

        // make the "enabled" field in the default implementation accessible
        try {
            fieldMessageLogger_enabled = SampleMessageLogger.class.getDeclaredField("enabled");
        } catch (NoSuchFieldException e) {
            AssertionError ae = new AssertionError("An expected private field does not exist");
            ae.initCause(e);
            throw ae;
        }
        fieldMessageLogger_enabled.setAccessible(true);
    }

    /**
     * Initialize the class with some configuration settings, and makes sure that they are accepted
     * <p/>
     * This method tests the "enabled" parameter in the configuration
     */
    @Test
    public void initForEnabledParameterTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        SampleMessageLogger message = new SampleMessageLogger();

        // 1: Test that a value of "true" is accepted
        // ------------------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        message.init(commonProps, config);

        String error = "The configuration parameter " + JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED + " is not used in the default message implementation";
        try {
            assertThat(error, fieldMessageLogger_enabled.getBoolean(message), is(equalTo(true)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }

        // 2: Test that a value of "TrUe" is accepted (case insensitive config)
        // ------------------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "TrUe");
        commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        message.init(commonProps, config);


        error = "The configuration parameter " + JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED + " is not used in the default message implementation";
        try {
            assertThat(error, fieldMessageLogger_enabled.getBoolean(message), is(equalTo(true)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }

        // 3: Test that a value different than any spelling of "true" is accepted and interpreted as "false"
        // ------------------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "not_true");
        commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        message.init(commonProps, config);


        error = "The configuration parameter " + JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED + " is not used in the default message implementation";
        try {
            assertThat(error, fieldMessageLogger_enabled.getBoolean(message), is(equalTo(false)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test messaging in the default domain
     */
    @Test
    public void defaultDomainMessageTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        SampleMessageLogger message = new SampleMessageLogger();

        // 1: Test with messaging enabled
        // -----------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        message.init(commonProps, config);
        message.create(Events.AUTHN_SUCCESS, "userId_1");

        // 2: Test with messaging disabled
        // -------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        message.init(commonProps, config);
        message.create(Events.AUTHN_FAILURE, "userId_2");
    }

    /**
     * Tests messaging in a specific domain
     */
    @Test
    public void specificDomainMessageTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);
        SampleMessageLogger message = new SampleMessageLogger();

        // 1: Test with messaging enabled
        // -----------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        message.init(commonProps, config);
        message.create(Events.AUTHN_SUCCESS, "domain_1", "userName_1");

        // 2: Test with messaging disabled
        // ------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        message.init(commonProps, config);
        message.create(Events.AUTHN_FAILURE, "domain_2", "userName_2");
    }
}
