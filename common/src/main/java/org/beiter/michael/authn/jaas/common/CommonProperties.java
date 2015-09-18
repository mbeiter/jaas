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
package org.beiter.michael.authn.jaas.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class specifies common properties.
 */
// CHECKSTYLE:OFF
// this is flagged in checkstyle with a missing whitespace before '}', which is a bug in checkstyle
// suppress warnings about the constructor (required for producing java docs)
// suppress warnings about the long variable names that are "inherited" from Apache DBCP (which I used as a blueprint)
@SuppressWarnings({"PMD.UnnecessaryConstructor", "PMD.LongVariable"})
// CHECKSTYLE:ON
public class CommonProperties {

    /**
     * @see CommonProperties#setAuditClassName(String)
     */
    private String auditClassName;

    /**
     * @see CommonProperties#setAuditEnabled(boolean)
     */
    private boolean auditEnabled;

    /**
     * @see CommonProperties#setAuditSingleton(boolean)
     */
    private boolean auditSingleton;

    /**
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    private String messageQueueClassName;

    /**
     * @see CommonProperties#setMessageQueueEnabled(boolean)
     */
    private boolean messageQueueEnabled;

    /**
     * @see CommonProperties#setMessageQueueSingleton(boolean)
     */
    private boolean messageQueueSingleton;

    /**
     * @see CommonProperties#setPasswordAuthenticatorClassName(String)
     */
    private String passwordAuthenticatorClassName;

    /**
     * @see CommonProperties#setPasswordAuthenticatorSingleton(boolean)
     */
    private boolean passwordAuthenticatorSingleton;

    /**
     * @see CommonProperties#setPasswordValidatorClassName(String)
     */
    private String passwordValidatorClassName;

    /**
     * @see CommonProperties#setPasswordValidatorSingleton(boolean)
     */
    private boolean passwordValidatorSingleton;

    /**
     * @see CommonProperties#setAdditionalProperties(Map <String, String>)
     */
    private Map<String, String> additionalProperties = new ConcurrentHashMap<>();

    /**
     * Constructs an empty set of JAAS common properties, with most values being set to <code>null</code>, 0, or empty
     * (depending on the type of the property). Usually this constructor is used if this configuration POJO is populated
     * in an automated fashion (e.g. injection). If you need to build them manually (possibly with defaults), use or
     * create a properties builder.
     * <p>
     * You can change the defaults with the setters.
     *
     * @see org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder#buildDefault()
     * @see org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder#build(java.util.Map)
     */
    public CommonProperties() {

        // no code here, constructor just for java docs
    }

    /**
     * Creates a set of common properties from an existing set of common properties, making a defensive copy.
     *
     * @param properties The set of properties to copy
     * @see CommonProperties()
     */
    public CommonProperties(final CommonProperties properties) {

        this();

        setAuditClassName(properties.getAuditClassName());
        setAuditEnabled(properties.isAuditEnabled());
        setAuditSingleton(properties.isAuditSingleton());
        setMessageQueueClassName(properties.getMessageQueueClassName());
        setMessageQueueEnabled(properties.isMessageQueueEnabled());
        setMessageQueueSingleton(properties.isMessageQueueSingleton());
        setPasswordAuthenticatorClassName(properties.getPasswordAuthenticatorClassName());
        setPasswordAuthenticatorSingleton(properties.isPasswordAuthenticatorSingleton());
        setPasswordValidatorClassName(properties.getPasswordValidatorClassName());
        setPasswordValidatorSingleton(properties.isPasswordValidatorSingleton());
        setAdditionalProperties(properties.getAdditionalProperties());
    }

    /**
     * @return The audit class name to instantiate for auditing
     * @see CommonProperties#setAuditClassName(String)
     */
    public final String getAuditClassName() {

        // no need for defensive copies of String

        return auditClassName;
    }

    /**
     * Set the audit class name (i.e. the class to instantiate for auditing).
     * <p>
     * The class must implement the {@link org.beiter.michael.authn.jaas.common.audit.Audit} interface.
     *
     * @param auditClassName A class implementing the {@link org.beiter.michael.authn.jaas.common.audit.Audit} interface
     */
    public final void setAuditClassName(final String auditClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.auditClassName = auditClassName;
    }

    /**
     * @return Indication of whether auditing is enabled, or not
     * @see CommonProperties#setAuditEnabled(boolean)
     */
    public final boolean isAuditEnabled() {

        // no need for defensive copies of boolean

        return auditEnabled;
    }

    /**
     * Set the indication of whether auditing is enabled.
     *
     * @param auditEnabled the indication of whether auditing is enabled
     */
    public final void setAuditEnabled(final boolean auditEnabled) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.auditEnabled = auditEnabled;
    }

    /**
     * @return Indication of whether auditing is instantiated as a singleton, or not
     * @see CommonProperties#setAuditSingleton(boolean)
     */
    public final boolean isAuditSingleton() {

        // no need for defensive copies of boolean

        return auditSingleton;
    }

    /**
     * Set the indication of whether auditing is instantiated as a singleton.
     *
     * @param auditSingleton the indication of whether auditing is instantiated as a singleton
     */
    public final void setAuditSingleton(final boolean auditSingleton) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.auditSingleton = auditSingleton;
    }

    /**
     * @return The message queue class name to instantiate for creating event messages
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    public final String getMessageQueueClassName() {

        // no need for defensive copies of String

        return messageQueueClassName;
    }

    /**
     * Set the message queue class name (i.e. the class to instantiate for creating even messages).
     * <p>
     * The class must implement the {@link org.beiter.michael.authn.jaas.common.messageq.MessageQ} interface.
     *
     * @param messageQueueClassName A class implementing the
     *                              {@link org.beiter.michael.authn.jaas.common.messageq.MessageQ} interface
     */
    public final void setMessageQueueClassName(final String messageQueueClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.messageQueueClassName = messageQueueClassName;
    }

    /**
     * @return Indication of whether creation of event messages is enabled, or not
     * @see CommonProperties#setMessageQueueEnabled(boolean)
     */
    public final boolean isMessageQueueEnabled() {

        // no need for defensive copies of boolean

        return messageQueueEnabled;
    }

    /**
     * Set the indication of whether creation of event messages is enabled.
     *
     * @param messageQueueEnabled the indication of whether auditing is enabled
     */
    public final void setMessageQueueEnabled(final boolean messageQueueEnabled) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.messageQueueEnabled = messageQueueEnabled;
    }


    /**
     * @return Indication of whether message queues are instantiated as a singleton, or not
     * @see CommonProperties#setMessageQueueSingleton(boolean)
     */
    public final boolean isMessageQueueSingleton() {

        // no need for defensive copies of boolean

        return messageQueueSingleton;
    }

    /**
     * Set the indication of whether message queues are instantiated as a singleton.
     *
     * @param messageQueueSingleton the indication of whether message queues are instantiated as a singleton
     */
    public final void setMessageQueueSingleton(final boolean messageQueueSingleton) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.messageQueueSingleton = messageQueueSingleton;
    }

    /**
     * @return The password authenticator class name to instantiate for authenticating password style credentials
     * @see CommonProperties#setPasswordAuthenticatorClassName(String)
     */
    public final String getPasswordAuthenticatorClassName() {

        // no need for defensive copies of String

        return passwordAuthenticatorClassName;
    }

    // CHECKSTYLE:OFF
    // The comment line is too long, but breaking up the link in two lines breaks Javadoc.

    /**
     * Set the password authenticator class name. This class is instantiated for authenticating username / password
     * style credentials.
     * <p>
     * The class must implement the <code>PasswordAuthenticator</code> interface.
     * <p>
     *
     * @param passwordAuthenticatorClassName A class implementing the <code>PasswordAuthenticator</code> interface
     */
    // CHECKSTYLE:ON
    public final void setPasswordAuthenticatorClassName(final String passwordAuthenticatorClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.passwordAuthenticatorClassName = passwordAuthenticatorClassName;
    }


    /**
     * @return Indication of whether the password authenticator is instantiated as a singleton, or not
     * @see CommonProperties#setPasswordAuthenticatorSingleton(boolean)
     */
    public final boolean isPasswordAuthenticatorSingleton() {

        // no need for defensive copies of boolean

        return passwordAuthenticatorSingleton;
    }

    /**
     * Set the indication of whether the password authenticator is instantiated as a singleton.
     *
     * @param passwordAuthenticatorSingleton the indication of whether the password authenticator is instantiated as a
     *                                       singleton
     */
    public final void setPasswordAuthenticatorSingleton(final boolean passwordAuthenticatorSingleton) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.passwordAuthenticatorSingleton = passwordAuthenticatorSingleton;
    }

    /**
     * @return The password validator class name to instantiate for validating password style credentials
     * @see CommonProperties#setPasswordValidatorClassName(String)
     */
    public final String getPasswordValidatorClassName() {

        // no need for defensive copies of String

        return passwordValidatorClassName;
    }

    // CHECKSTYLE:OFF
    // The comment line is too long, but breaking up the link in two lines breaks Javadoc.

    /**
     * Set the password validator class name. This class is instantiated for validating username / password
     * style credentials.
     * <p>
     * The class must implement the <code>PasswordValidator</code> interface.
     * <p>
     *
     * @param passwordValidatorClassName A class implementing the <code>PasswordValidator</code> interface
     */
    // CHECKSTYLE:ON
    public final void setPasswordValidatorClassName(final String passwordValidatorClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.passwordValidatorClassName = passwordValidatorClassName;
    }

    /**
     * @return Indication of whether the password validator is instantiated as a singleton, or not
     * @see CommonProperties#setPasswordAuthenticatorSingleton(boolean)
     */
    public final boolean isPasswordValidatorSingleton() {

        // no need for defensive copies of boolean

        return passwordValidatorSingleton;
    }

    /**
     * Set the indication of whether the password validator is instantiated as a singleton.
     *
     * @param passwordValidatorSingleton the indication of whether the password validator is instantiated as a singleton
     */
    public final void setPasswordValidatorSingleton(final boolean passwordValidatorSingleton) {

        // no need for validation, as boolean cannot be null and all possible values are allowed
        // no need for defensive copies of boolean

        this.passwordValidatorSingleton = passwordValidatorSingleton;
    }

    /**
     * @return Any additional properties stored in this object that have not explicitly been parsed
     * @see CommonProperties#setAdditionalProperties(Map<String, String>)
     */
    public final Map<String, String> getAdditionalProperties() {

        // create a defensive copy of the map and all its properties
        if (this.additionalProperties == null) {
            // this should never happen!
            return new ConcurrentHashMap<>();
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
    public final void setAdditionalProperties(final Map<String, String> additionalProperties) {

        // create a defensive copy of the map and all its properties
        if (additionalProperties == null) {
            this.additionalProperties = new ConcurrentHashMap<>();
        } else {
            this.additionalProperties = new ConcurrentHashMap<>();
            this.additionalProperties.putAll(additionalProperties);
        }
    }
}
