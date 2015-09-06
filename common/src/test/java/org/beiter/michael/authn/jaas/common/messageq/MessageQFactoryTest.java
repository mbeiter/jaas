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
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class MessageQFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(MessageQFactoryTest.class);

    /**
     * Reset the messageQ factory to allow creating several instances of the underlying messageQ implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        MessageQFactory.reset();
    }

    /**
     * A null class name (i.e. a class not in the class path) with message queues being enabled should throw an exception
     */
    @Test(expected = NullPointerException.class)
    public void getNullImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance(null, commonProps);
    }

    /**
     * A null class name (i.e. a class not in the class path) with message queues being disabled should NOT throw an exception
     */
    public void getNullImplementationMessageQDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance(null, commonProps);
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) with message queues being enabled should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getNonExistingImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance("someGarbageName", commonProps);
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) with message queues being disabled should NOT throw an exception
     */
    public void getNonExistingImplementationMessageQDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance("someGarbageName", commonProps);
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) with message queues being enabled should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getInvalidImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance(String.class.getCanonicalName(), commonProps);
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) with message queues being disabled should NOT throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getInvalidImplementationMessageQDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "false");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQFactory.getInstance(String.class.getCanonicalName(), commonProps);
    }

    /**
     * Retrieve a specific implementation of the MessageQ interface, and asserts that the returned implementation equals
     * the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, "org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQ messageQ;
        try {
            messageQ = MessageQFactory.getInstance(commonProps.getMessageQueueClassName(), commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, SampleMessageLogger.class.getCanonicalName(), is(equalTo(commonProps.getMessageQueueClassName())));
        assertThat(error, messageQ.getClass().getCanonicalName(), is(equalTo(commonProps.getMessageQueueClassName())));
    }

    /**
     * Retrieve two instances of a specific implementation of the MessageQ interface, and asserts that the returned
     * objects are two separate instances.
     */
    @Test
    public void twoInstancesAreDifferentTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, "org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        MessageQ messageQ1, messageQ2;
        try {
            messageQ1 = MessageQFactory.getInstance(commonProps.getMessageQueueClassName(), commonProps);
            messageQ2 = MessageQFactory.getInstance(commonProps.getMessageQueueClassName(), commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory returns a singleton instead of a new object";
        assertThat(error, messageQ1, is(not(sameInstance(messageQ2))));
    }

    /**
     * Retrieve two singleton instances of a specific implementation of the MessageQ interface, and asserts that the two
     * returned objects are identical (i.e. the factory returns a singleton).
     * <p>
     * Then, a regular (non-singleton) instance is retrieved, which are asserted to be different than the previously
     * retrieved objects.
     * <p>
     * Finally, the factory is reset, and another instance is retrieved. If the factory resets properly, the third
     * instance must be unequal to the first three instances.
     */
    @Test
    public void factoryReturnsSingletonTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "true");
        config.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, "org.beiter.michael.authn.jaas.common.messageq.SampleMessageLogger");
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(config);

        // test that two singletons retrieved from the factory are identical
        MessageQ messageQ1, messageQ2;
        try {
            messageQ1 = MessageQFactory.getSingleton(commonProps.getMessageQueueClassName(), commonProps);
            messageQ2 = MessageQFactory.getSingleton(commonProps.getMessageQueueClassName(), commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, messageQ1, is(sameInstance(messageQ2)));

        // then test that a regular (non-singleton) instance is different
        MessageQ messageQ3;
        try {
            messageQ3 = MessageQFactory.getInstance(commonProps.getMessageQueueClassName(), commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }
        error = "The factory returns a singleton instead of a new object";
        assertThat(error, messageQ1, is(not(sameInstance(messageQ3))));
        assertThat(error, messageQ2, is(not(sameInstance(messageQ3))));

        // reset the factory
        MessageQFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        MessageQ messageQ4;
        try {
            messageQ4 = MessageQFactory.getSingleton(commonProps.getMessageQueueClassName(), commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, messageQ1, is(not(sameInstance(messageQ4))));
        assertThat(error, messageQ2, is(not(sameInstance(messageQ4))));
        assertThat(error, messageQ3, is(not(sameInstance(messageQ4))));
    }
}
