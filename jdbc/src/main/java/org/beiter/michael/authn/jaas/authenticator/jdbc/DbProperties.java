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
package org.beiter.michael.authn.jaas.authenticator.jdbc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class specifies database properties.
 */
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
// suppress warnings about the constructor (required for producing java docs)
// suppress warnings about the long variable names that are "inherited" from Apache DBCP (which I used as a blueprint)
@SuppressWarnings({"PMD.UnnecessaryConstructor", "PMD.LongVariable"})
// CHECKSTYLE:ON
public class DbProperties {

    /**
     * @see DbProperties#setJndiConnectionName(String)
     */
    private String jndiConnectionName;

    /**
     * @see DbProperties#setSqlUserQuery(String)
     */
    private String sqlUserQuery;

    /**
     * @see DbProperties#setAdditionalProperties(Map <String, String>)
     */
    private Map<String, String> additionalProperties;

    /**
     * Constructs an empty set of database properties, with most values being set to <code>null</code>, 0, or empty
     * (depending on the type of the property). Usually this constructor is used if this configuration POJO is populated
     * in an automated fashion (e.g. injection). If you need to build them manually (possibly with defaults), use or
     * create a properties builder.
     * <p/>
     * You can change the defaults with the setters.
     *
     * @see org.beiter.michael.authn.jaas.authenticator.jdbc.propsbuilder.JaasPropsBasedDbPropsBuilder#buildDefault()
     * @see org.beiter.michael.authn.jaas.authenticator.jdbc.propsbuilder.JaasPropsBasedDbPropsBuilder#build(
     *java.util.Map)
     */
    public DbProperties() {

        // no code here, constructor just for java docs
    }

    /**
     * Creates a set of database properties from an existing set of database properties, making a defensive copy.
     *
     * @see DbProperties()
     * @param properties The set of properties to copy
     */
    public DbProperties(final DbProperties properties) {

        this();

        setJndiConnectionName(properties.getJndiConnectionName());
        setSqlUserQuery(properties.getSqlUserQuery());
        setAdditionalProperties(properties.getAdditionalProperties());
    }

    /**
     * @return The JNDI connection name to connect to (if JNDI is being used)
     * @see DbProperties#setJndiConnectionName(String)
     */
    public final String getJndiConnectionName() {

        // no need for defensive copies of String

        return jndiConnectionName;
    }

    /**
     * Set the JNDI connection name to connect to (if JNDI is being used)
     *
     * @param jndiConnectionName A JNDI connection name
     */
    public final void setJndiConnectionName(final String jndiConnectionName) {

        // no need for validation, as we cannot possible validate all JNDI connection names and null is allowed

        // no need for defensive copies of String

        this.jndiConnectionName = jndiConnectionName;
    }

    /**
     * @return The SQL query to retrieve user information
     * @see DbProperties#setSqlUserQuery(String)
     */
    public final String getSqlUserQuery() {

        // no need for defensive copies of String

        return sqlUserQuery;
    }

    /**
     * The SQL query to retrieve user information, according to the specification in
     * {@link JdbcPasswordAuthenticator#authenticate(String, String, char[],
     * org.beiter.michael.authn.jaas.common.validator.PasswordValidator)}
     *
     * @param sqlUserQuery A SQL user query string
     */
    public final void setSqlUserQuery(final String sqlUserQuery) {

        // no need for more detailed validation, as we cannot possible validate all SQL dialects

        // no need for defensive copies of String

        this.sqlUserQuery = sqlUserQuery;
    }

    /**
     * @return Any additional properties stored in this object that have not explicitly been parsed
     * @see DbProperties#setAdditionalProperties(Map <String, String>)
     */
    public final Map<String, String> getAdditionalProperties() {

        // create a defensive copy of the map and all its properties
        if (this.additionalProperties == null) {
            return null;
        } else {
            final Map<String, String> tempMap = new ConcurrentHashMap<>();
            tempMap.putAll(additionalProperties);

            return tempMap;
        }
    }

    /**
     * Any additional properties which have not been parsed, and for which no getter/setter exists, but are to be
     * stored in this object nevertheless.
     * <p>
     * This property is commonly used to preserve original properties from upstream components that are to be passed
     * on to downstream components unchanged. This properties set may or may not include properties that have been
     * extracted from the map, and been made available through this POJO.
     * <p>
     * Note that these additional properties may be <code>null</code> or empty, even in a fully populated POJO where
     * other properties commonly have values assigned to.
     *
     * @param additionalProperties The additional properties to store
     */
    // CHECKSTYLE:OFF
    // this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
    // suppress warnings about the null assignment. This is to allow the caller to dereference the object.
    @SuppressWarnings({"PMD.NullAssignment"})
    // CHECKSTYLE:ON
    public final void setAdditionalProperties(final Map<String, String> additionalProperties) {

        // create a defensive copy of the map and all its properties
        if (additionalProperties == null) {
            this.additionalProperties = null;
        } else {
            this.additionalProperties = new ConcurrentHashMap<>();
            this.additionalProperties.putAll(additionalProperties);
        }
    }
}
