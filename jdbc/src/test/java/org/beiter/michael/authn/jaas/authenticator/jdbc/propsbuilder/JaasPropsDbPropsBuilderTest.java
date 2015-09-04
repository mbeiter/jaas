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

import org.beiter.michael.authn.jaas.authenticator.jdbc.DbProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class JaasPropsDbPropsBuilderTest {

    /**
     * default jndi name test
     */
    @Test
    public void defaultJndiNameTest() {

        DbProperties dbProps = JaasPropsBasedDbPropsBuilder.buildDefault();

        String error = "jndi name does not match expected default value";
        assertThat(error, dbProps.getJndiConnectionName(), is(nullValue()));
        error = "jndi does not match expected value";
        dbProps.setJndiConnectionName("42");
        assertThat(error, dbProps.getJndiConnectionName(), is(equalTo("42")));
    }

    /**
     * jndi name test
     */
    @Test
    public void jndiNameTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasPropsBasedDbPropsBuilder.KEY_JNDI_CONNECTION_NAME, null);
        DbProperties dbProps = JaasPropsBasedDbPropsBuilder.build(map);
        String error = "jndi name does not match expected default value";
        assertThat(error, dbProps.getJndiConnectionName(), is(nullValue()));

        map.put(JaasPropsBasedDbPropsBuilder.KEY_JNDI_CONNECTION_NAME, "42");
        dbProps = JaasPropsBasedDbPropsBuilder.build(map);
        error = "jndi name does not match expected value";
        assertThat(error, dbProps.getJndiConnectionName(), is(equalTo("42")));

        DbProperties dbProps2 = new DbProperties(dbProps);
        error = "copy constructor does not copy field";
        assertThat(error, dbProps2.getJndiConnectionName(), is(equalTo("42")));
    }

    /**
     * default sql user query test
     */
    @Test
    public void defaultSqlUserQueryTest() {

        DbProperties dbProps = JaasPropsBasedDbPropsBuilder.buildDefault();

        String error = "sql user query does not match expected default value";
        assertThat(error, dbProps.getSqlUserQuery(), is(nullValue()));
        error = "sql user query does not match expected value";
        dbProps.setSqlUserQuery("42");
        assertThat(error, dbProps.getSqlUserQuery(), is(equalTo("42")));
    }

    /**
     * sql user query test
     */
    @Test
    public void sqlUserQueryTest() {

        Map<String, String> map = new HashMap<>();

        map.put(JaasPropsBasedDbPropsBuilder.KEY_SQL_USER_QUERY, null);
        DbProperties dbProps = JaasPropsBasedDbPropsBuilder.build(map);
        String error = "sql user query does not match expected default value";
        assertThat(error, dbProps.getSqlUserQuery(), is(nullValue()));

        map.put(JaasPropsBasedDbPropsBuilder.KEY_SQL_USER_QUERY, "42");
        dbProps = JaasPropsBasedDbPropsBuilder.build(map);
        error = "sql user query does not match expected value";
        assertThat(error, dbProps.getSqlUserQuery(), is(equalTo("42")));

        DbProperties dbProps2 = new DbProperties(dbProps);
        error = "copy constructor does not copy field";
        assertThat(error, dbProps2.getSqlUserQuery(), is(equalTo("42")));
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
        DbProperties dbProps = JaasPropsBasedDbPropsBuilder.build(map);

        String error = "The properties builder returns a singleton";
        assertThat(error, map, is(not(sameInstance(dbProps.getAdditionalProperties()))));
    }
}
