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
package org.beiter.michael.authn.jaas.common.audit;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasPropsBasedCommonPropsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AuditFactoryTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuditFactoryTest.class);

    /**
     * Reset the audit factory to allow creating several instances of the underlying audit implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        AuditFactory.reset();
    }

    /**
     * A null class name (i.e. a class not in the class path) with audit being enabled should throw an exception
     */
    @Test(expected = NullPointerException.class)
    public void getNullImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        AuditFactory.getInstance(null, config);
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) with audit being disabled should NOT throw an exception
     */
    public void getNullImplementationAuditDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        AuditFactory.getInstance(null, config);
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) with audit being enabled should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getNonExistingImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        AuditFactory.getInstance("someGarbageName", config);
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) with audit being disabled should NOT throw an exception
     */
    public void getNonExistingImplementationAuditDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        AuditFactory.getInstance("someGarbageName", config);
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) with audit being enabled should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getInvalidImplementationTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        AuditFactory.getInstance(String.class.getCanonicalName(), config);
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) with audit being disabled should NOT throw an exception
     */
    public void getInvalidImplementationAuditDisabledTest()
            throws FactoryException {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "false");
        AuditFactory.getInstance(String.class.getCanonicalName(), config);
    }

    /**
     * Retrieve a specific implementation of the Audit interface, and asserts that the returned implementation equals
     * the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_CLASS_NAME, "org.beiter.michael.authn.jaas.common.audit.SampleAuditLogger");
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        Audit audit;
        try {
            audit = AuditFactory.getInstance(commonProps.getAuditClassName(), config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, SampleAuditLogger.class.getCanonicalName(), is(equalTo(commonProps.getAuditClassName())));
        assertThat(error, audit.getClass().getCanonicalName(), is(equalTo(commonProps.getAuditClassName())));
    }

    /**
     * Retrieve two instances of a specific implementation of the Audit interface, and asserts that the two returned
     * objects are identical (i.e. the factory returns a singleton).
     * <p/>
     * Then the factory is reset, and another instance is retrieved. If the factory resets properly, the third instance
     * must be unequal to the first two instances.
     */
    @Test
    public void factoryReturnsSingletonTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "true");
        config.put(JaasPropsBasedCommonPropsBuilder.KEY_AUDIT_CLASS_NAME, "org.beiter.michael.authn.jaas.common.audit.SampleAuditLogger");
        CommonProperties commonProps = JaasPropsBasedCommonPropsBuilder.build(config);

        Audit audit1, audit2;
        try {
            audit1 = AuditFactory.getInstance(commonProps.getAuditClassName(), config);
            audit2 = AuditFactory.getInstance(commonProps.getAuditClassName(), config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, audit1, is(sameInstance(audit2)));

        // reset the factory
        AuditFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        Audit audit3;
        try {
            audit3 = AuditFactory.getInstance(commonProps.getAuditClassName(), config);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, audit1, is(not(sameInstance(audit3))));
    }
}
