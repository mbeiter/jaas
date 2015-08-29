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

/**
 * This class specifies database properties.
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
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    private String messageQueueClassName;

    /**
     * @see CommonProperties#setMessageQueueEnabled(boolean)
     */
    private boolean messageQueueEnabled;

    /**
     * @see CommonProperties#setPasswordAuthenticatorClassName(String)
     */
    private String passwordAuthenticatorClassName;

    /**
     * @see CommonProperties#setPasswordValidatorClassName(String)
     */
    private String passwordValidatorClassName;

    /**
     * Constructs an empty set of JAAS common properties, with most values being set to <code>null</code>, 0, or empty
     * (depending on the type of the property). Usually this constructor is used if this configuration POJO is populated
     * in an automated fashion (e.g. injection). If you need to build them manually (possibly with defaults), use or
     * create a properties builder.
     * <p/>
     * You can change the defaults with the setters.
     *
     * @see org.beiter.michael.authn.jaas.common.propsbuilder.JaasPropsBasedCommonPropsBuilder#buildDefault()
     * @see org.beiter.michael.authn.jaas.common.propsbuilder.JaasPropsBasedCommonPropsBuilder#build(java.util.Map)
     */
    public CommonProperties() {

        // no code here, constructor just for java docs
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
     * <p/>
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
     * @return The message queue class name to instantiate for creating event messages
     * @see CommonProperties#setMessageQueueClassName(String)
     */
    public final String getMessageQueueClassName() {

        // no need for defensive copies of String

        return messageQueueClassName;
    }

    /**
     * Set the message queue class name (i.e. the class to instantiate for creating even messages).
     * <p/>
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
     * <p/>
     * The class must implement the {@link org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticator}
     * interface.
     * <p/>
     *
     * @param passwordAuthenticatorClassName A class implementing the {@link org.beiter.michael.authn.jaas.common.authenticator.PasswordAuthenticator}
     *                                       interface
     */
    // CHECKSTYLE:ON
    public final void setPasswordAuthenticatorClassName(final String passwordAuthenticatorClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.passwordAuthenticatorClassName = passwordAuthenticatorClassName;
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
     * <p/>
     * The class must implement the {@link org.beiter.michael.authn.jaas.common.validator.PasswordValidator}
     * interface.
     * <p/>
     *
     * @param passwordValidatorClassName A class implementing the {@link org.beiter.michael.authn.jaas.common.validator.PasswordValidator}
     *                                   interface
     */
    // CHECKSTYLE:ON
    public final void setPasswordValidatorClassName(final String passwordValidatorClassName) {

        // no need for validation, as we cannot possible validate all class names and null is allowed.
        // the consumer will do null checks, and try to instantiate the class, and will check if it is the correct one.

        // no need for defensive copies of String

        this.passwordValidatorClassName = passwordValidatorClassName;
    }
}
