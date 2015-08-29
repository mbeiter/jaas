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
import org.beiter.michael.authn.jaas.common.FactoryException;
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

public class MessageQFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(MessageQFactoryTest.class);

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
     * Reset the messageQ factory to allow creating several instances of the underlying messageQ implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        MessageQFactory.reset();
    }

    /**
     * Retrieve the default implementation of the MessageQ interface, and asserts that this default implementation equals
     * the implementation shipped with this library.
     */
    @Test
    public void getDefaultImplementationTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        MessageQ messageQ;
        try {
            messageQ = MessageQFactory.getInstance(commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, SampleMessageLogger.class.isInstance(messageQ), is(equalTo(true)));
    }

    /**
     * Retrieve a specific implementation of the MessageQ interface, and asserts that the returned implementation equals
     * the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        String className = "org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger";

        MessageQ messageQ;
        try {
            messageQ = MessageQFactory.getInstance(className, commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, SampleMessageLogger.class.getCanonicalName(), is(equalTo(className)));
        assertThat(error, messageQ.getClass().getCanonicalName(), is(equalTo(className)));
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getNonExistingImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance("someGarbageName", commonProps, config);
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getInvalidImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance(String.class.getCanonicalName(), commonProps, config);
    }

    /**
     * Retrieve two instances of the default implementation of the MessageQ interface, and asserts that the two returned
     * objects are identical (i.e. the factory returns a singleton).
     * <p/>
     * Then the factory is reset, and another instance is retrieved. If the factory resets properly, the third instance
     * must be unequal to the first two instances.
     */
    @Test
    public void factoryReturnsSingletonTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        MessageQ messageQ1, messageQ2;
        try {
            messageQ1 = MessageQFactory.getInstance(commonProps, config);
            messageQ2 = MessageQFactory.getInstance(commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, messageQ1, is(equalTo(messageQ2)));

        // reset the factory
        MessageQFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        MessageQ messageQ3;
        try {
            messageQ3 = MessageQFactory.getInstance(commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, messageQ1, is(not(equalTo(messageQ3))));
    }

    /**
     * Retrieve the default implementation of the MessageQ interface, and asserts that this default implementation accepts
     * the provided configuration parameters during instantiation.
     */
    @Test
    public void defaultImplementationHonorsConfigTest() {

        // 1: Test that a value of "true" is accepted
        // -----------------------------------------------
        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        MessageQ messageQ;
        try {
            messageQ = MessageQFactory.getInstance(commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The configuration parameter " + JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED + " is not used in the default messageQ implementation";
        try {
            assertThat(error, fieldMessageLogger_enabled.getBoolean(messageQ), is(equalTo(true)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }

        // reset the factory
        MessageQFactory.reset();


        // 2: Test that a value of "false" is accepted
        // -----------------------------------------------
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        try {
            messageQ = MessageQFactory.getInstance(commonProps, config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The configuration parameter " + JaasPropsBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED + " is not used in the default messageQ implementation";
        try {
            assertThat(error, fieldMessageLogger_enabled.getBoolean(messageQ), is(equalTo(false)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }
    }
}
