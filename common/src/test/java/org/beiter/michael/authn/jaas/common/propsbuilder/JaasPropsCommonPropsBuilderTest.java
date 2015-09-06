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

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JaasPropsCommonPropsBuilderTest {

    /**
     * default audit class name test
     */
    @Test
    public void defaultAuditClassNameTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "audit class name does not match expected default value";
        assertThat(error, commonProps.getAuditClassName(),
                is(equalTo(JaasBasedCommonPropsBuilder.DEFAULT_AUDIT_CLASS_NAME)));
        error = "audit class name does not match expected value";
        commonProps.setAuditClassName("42");
        assertThat(error, commonProps.getAuditClassName(), is(equalTo("42")));
    }

    /**
     * audit class name test
     */
    @Test
    public void auditClassNameTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_CLASS_NAME, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "audit class name does not match expected default value";
        assertThat(error, commonProps.getAuditClassName(),
                is(equalTo(JaasBasedCommonPropsBuilder.DEFAULT_AUDIT_CLASS_NAME)));

        map.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_CLASS_NAME, "42");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "audit class name does not match expected value";
        assertThat(error, commonProps.getAuditClassName(), is(equalTo("42")));
    }

    /**
     * default audit is_enabled test
     */
    @Test
    public void defaultAuditIsEnabledTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "audit is_enabled does not match expected default value";
        assertThat(error, commonProps.isAuditEnabled(), is(equalTo(false)));
        error = "audit is_enabled does not match expected value";
        commonProps.setAuditEnabled(true);
        assertThat(error, commonProps.isAuditEnabled(), is(equalTo(true)));
    }

    /**
     * audit is_enabled test
     */
    @Test
    public void auditIsEnabledTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "audit is_enabled does not match expected default value";
        assertThat(error, commonProps.isAuditEnabled(), is(equalTo(false)));

        map.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "asdf");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "audit is_enabled does not match expected value";
        assertThat(error, commonProps.isAuditEnabled(), is(equalTo(false)));

        map.put(JaasBasedCommonPropsBuilder.KEY_AUDIT_IS_ENABLED, "tRuE");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "audit is_enabled does not match expected value";
        assertThat(error, commonProps.isAuditEnabled(), is(equalTo(true)));
    }

    /**
     * default messageQ class name test
     */
    @Test
    public void defaultMessageQClassNameTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "messageQ class name does not match expected default value";
        assertThat(error, commonProps.getMessageQueueClassName(),
                is(equalTo(JaasBasedCommonPropsBuilder.DEFAULT_MESSAGEQ_CLASS_NAME)));
        error = "messageQ class name does not match expected value";
        commonProps.setMessageQueueClassName("42");
        assertThat(error, commonProps.getMessageQueueClassName(), is(equalTo("42")));
    }

    /**
     * messageQ class name test
     */
    @Test
    public void messageQClassNameTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "messageQ class name does not match expected default value";
        assertThat(error, commonProps.getMessageQueueClassName(),
                is(equalTo(JaasBasedCommonPropsBuilder.DEFAULT_MESSAGEQ_CLASS_NAME)));

        map.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_CLASS_NAME, "42");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "messageQ class name does not match expected value";
        assertThat(error, commonProps.getMessageQueueClassName(), is(equalTo("42")));
    }

    /**
     * default messageQ is_enabled test
     */
    @Test
    public void defaultMessageQIsEnabledTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "messageQ is_enabled does not match expected default value";
        assertThat(error, commonProps.isMessageQueueEnabled(), is(equalTo(false)));
        error = "messageQ is_enabled does not match expected value";
        commonProps.setMessageQueueEnabled(true);
        assertThat(error, commonProps.isMessageQueueEnabled(), is(equalTo(true)));
    }

    /**
     * messageQ is_enabled test
     */
    @Test
    public void messageQIsEnabledTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "messageQ is_enabled does not match expected default value";
        assertThat(error, commonProps.isMessageQueueEnabled(), is(equalTo(false)));

        map.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "asdf");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "messageQ is_enabled does not match expected value";
        assertThat(error, commonProps.isMessageQueueEnabled(), is(equalTo(false)));

        map.put(JaasBasedCommonPropsBuilder.KEY_MESSAGEQ_IS_ENABLED, "tRuE");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "messageQ is_enabled does not match expected value";
        assertThat(error, commonProps.isMessageQueueEnabled(), is(equalTo(true)));
    }

    /**
     * default password authenticator class name test
     */
    @Test
    public void defaultPasswordAuthenticatorClassNameTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "password authenticator class name does not match expected default value";
        assertThat(error, commonProps.getPasswordAuthenticatorClassName(), is(nullValue()));
        error = "password authenticator class name does not match expected value";
        commonProps.setPasswordAuthenticatorClassName("42");
        assertThat(error, commonProps.getPasswordAuthenticatorClassName(), is(equalTo("42")));
    }

    /**
     * password authenticator class name test
     */
    @Test
    public void passwordAuthenticatorClassNameTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "password authenticator class name does not match expected default value";
        assertThat(error, commonProps.getPasswordAuthenticatorClassName(), is(nullValue()));

        map.put(JaasBasedCommonPropsBuilder.KEY_PASSWORD_AUTHENTICATOR_CLASS_NAME, "42");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "password authenticator class name does not match expected value";
        assertThat(error, commonProps.getPasswordAuthenticatorClassName(), is(equalTo("42")));
    }

    /**
     * default password validator class name test
     */
    @Test
    public void defaultPasswordValidatorClassNameTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        String error = "password validator class name does not match expected default value";
        assertThat(error, commonProps.getPasswordValidatorClassName(), is(nullValue()));
        error = "password validator class name does not match expected value";
        commonProps.setPasswordValidatorClassName("42");
        assertThat(error, commonProps.getPasswordValidatorClassName(), is(equalTo("42")));
    }

    /**
     * password validator class name test
     */
    @Test
    public void passwordValidatorClassNameTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, null);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);
        String error = "password validator class name does not match expected default value";
        assertThat(error, commonProps.getPasswordValidatorClassName(), is(nullValue()));

        map.put(JaasBasedCommonPropsBuilder.KEY_PASSWORD_VALIDATOR_CLASS_NAME, "42");
        commonProps = JaasBasedCommonPropsBuilder.build(map);
        error = "password validator class name does not match expected value";
        assertThat(error, commonProps.getPasswordValidatorClassName(), is(equalTo("42")));
    }

    /**
     * additionalProperties test: make sure that the additional properties are being set to a new object (i.e. a
     * defensive copy is being made)
     */
    @Test
    public void additionalPropertiesNoSingletonTest() {

        String key = "some property";
        String value = "some value";

        Map<String, String> map = new HashMap<>();

        map.put(key, value);
        CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(map);

        String error = "The properties builder returns a singleton";
        assertThat(error, map, is(not(sameInstance(commonProps.getAdditionalProperties()))));
    }
}
