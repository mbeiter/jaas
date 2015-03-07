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

import org.beiter.michael.authn.jaas.common.Events;
import org.beiter.michael.authn.jaas.common.JaasConfigOptions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AuditLoggerTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuditLoggerTest.class);

    private Field fieldAuditLogger_enabled;

    /**
     * Make some of the private fields in the AuditLogger class accessible.
     * <p/>
     * This is executed before every test to ensure consistency even if one of the tests mock with field accessibility.
     */
    @Before
    public void makeAuditLoggerPrivateFieldsAccessible() {

        // make the "enabled" field in the default implementation accessible
        try {
            fieldAuditLogger_enabled = AuditLogger.class.getDeclaredField("enabled");
        } catch (NoSuchFieldException e) {
            AssertionError ae = new AssertionError("An expected private field does not exist");
            ae.initCause(e);
            throw ae;
        }
        fieldAuditLogger_enabled.setAccessible(true);
    }

    /**
     * Initialize the class with some configuration settings, and makes sure that they are accepted
     * <p/>
     * This method tests the "enabled" parameter in the configuration
     */
    @Test
    public void initForEnabledParameterTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        AuditLogger audit = new AuditLogger();

        // 1: Test that a value of "true" is accepted
        // ------------------------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "true");
        audit.init(config);

        String error = "The configuration parameter " + JaasConfigOptions.AUDIT_ENABLED.getName() + " is not used in the default audit implementation";
        try {
            assertThat(error, fieldAuditLogger_enabled.getBoolean(audit), is(equalTo(true)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }

        // 2: Test that a value of "TrUe" is accepted (case insensitive config)
        // ------------------------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "TrUe");
        audit.init(config);


        error = "The configuration parameter " + JaasConfigOptions.AUDIT_ENABLED.getName() + " is not used in the default audit implementation";
        try {
            assertThat(error, fieldAuditLogger_enabled.getBoolean(audit), is(equalTo(true)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }

        // 3: Test that a value different than any spelling of "true" is accepted and interpreted as "false"
        // ------------------------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "not_true");
        audit.init(config);


        error = "The configuration parameter " + JaasConfigOptions.AUDIT_ENABLED.getName() + " is not used in the default audit implementation";
        try {
            assertThat(error, fieldAuditLogger_enabled.getBoolean(audit), is(equalTo(false)));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test auditing in the default domain
     */
    @Test
    public void defaultDomainAuditTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        AuditLogger audit = new AuditLogger();

        // 1: Test with auditing enabled
        // -----------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "true");
        audit.init(config);
        audit.audit(Events.AUTHN_SUCCESS, "userId_1");

        // 2: Test with auditing disabled
        // ------------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "false");
        audit.init(config);
        audit.audit(Events.AUTHN_FAILURE, "userId_2");
    }

    /**
     * Tests auditing in a specific domain
     */
    @Test
    public void specificDomainAuditTest() {

        Map<String, Object> config = new ConcurrentHashMap<String, Object>();
        AuditLogger audit = new AuditLogger();

        // 1: Test with auditing enabled
        // -----------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "true");
        audit.init(config);
        audit.audit(Events.AUTHN_SUCCESS, "domain_1", "userName_1");

        // 2: Test with auditing disabled
        // ------------------------------
        config.put(JaasConfigOptions.AUDIT_ENABLED.getName(), "false");
        audit.init(config);
        audit.audit(Events.AUTHN_FAILURE, "domain_2", "userName_2");
    }
}
