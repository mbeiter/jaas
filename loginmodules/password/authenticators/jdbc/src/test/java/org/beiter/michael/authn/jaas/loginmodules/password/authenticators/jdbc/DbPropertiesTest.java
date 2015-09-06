/*
 * #%L
 * This file is part of a universal JAAS library, providing a universal JDBC
 * authenticator implementation.
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
package org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc;

import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc.propsbuilder.JaasBasedDbPropsBuilder;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class DbPropertiesTest {

    private Field field_additionalProperties;

    /**
     * Make some of the private fields in the DbProperties class accessible.
     * <p>
     * This is executed before every test to ensure consistency even if one of the tests mock with field accessibility.
     */
    @Before
    public void makeMessageLoggerPrivateFieldsAccessible() {

        // make the "enabled" field in the default implementation accessible
        try {
            field_additionalProperties = DbProperties.class.getDeclaredField("additionalProperties");
        } catch (NoSuchFieldException e) {
            AssertionError ae = new AssertionError("An expected private field does not exist");
            ae.initCause(e);
            throw ae;
        }
        field_additionalProperties.setAccessible(true);
    }

    /**
     * Test that the additional properties are copied inbound
     */
    @Test
    public void additionalPropertiesInboundDefensiveCopyTest() {

        String key = "some property";
        String value = "some value";

        Map<String, String> originalMap = new HashMap<>();

        originalMap.put(key, value);
        DbProperties dbProps = new DbProperties();
        dbProps.setAdditionalProperties(originalMap);

        String error = "The properties POJO does not create an inbound defensive copy";
        try {
            Map<String, String> mapInObject = (Map<String, String>) field_additionalProperties.get(dbProps);
            assertThat(error, mapInObject, is(not(sameInstance(originalMap))));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that the additional properties are copied outbound
     */
    @Test
    public void additionalPropertiesOutboundDefensiveCopyTest() {

        DbProperties dbProps = JaasBasedDbPropsBuilder.buildDefault();

        String error = "The properties POJO does not create an outbound defensive copy";
        try {
            Map<String, String> mapInObject = (Map<String, String>) field_additionalProperties.get(dbProps);
            assertThat(error, mapInObject, is(not(sameInstance(dbProps.getAdditionalProperties()))));
        } catch (IllegalAccessException e) {
            AssertionError ae = new AssertionError("Cannot access private field");
            ae.initCause(e);
            throw ae;
        }
    }

    /**
     * Test that the copy constructor creates a new object instance
     */
    @Test
    public void copyConstructorTest() {

        DbProperties dbProps1 = JaasBasedDbPropsBuilder.buildDefault();
        DbProperties dbProps2 = new DbProperties(dbProps1);

        String error = "The copy constructor does not create a new object instance";
        assertThat(error, dbProps1, is(not(sameInstance(dbProps2))));
    }
}
