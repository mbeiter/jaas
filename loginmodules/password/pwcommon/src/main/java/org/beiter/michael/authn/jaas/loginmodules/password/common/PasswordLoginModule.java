/*
 * #%L
 * This file is part of a universal JAAS library, providing common functionality
 * for a username / password style JAAS module.
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
package org.beiter.michael.authn.jaas.loginmodules.password.common;

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.Events;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.beiter.michael.authn.jaas.common.Util;
import org.beiter.michael.authn.jaas.common.audit.Audit;
import org.beiter.michael.authn.jaas.common.audit.AuditFactory;
import org.beiter.michael.authn.jaas.common.audit.AuditHelper;
import org.beiter.michael.authn.jaas.common.messageq.MessageHelper;
import org.beiter.michael.authn.jaas.common.messageq.MessageQFactory;
import org.beiter.michael.authn.jaas.common.messageq.MessageQ;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;

/**
 * This class implements a JAAS login module for username / password based authentication.
 */
// The JAAS workflow has an inherently high level of complexity - ignoring these warnings, as we cannot fix JAAS
// CHECKSTYLE:OFF
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.StdCyclomaticComplexity", "PMD.ModifiedCyclomaticComplexity"})
// CHECKSTYLE:ON
public class PasswordLoginModule
        implements LoginModule {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordLoginModule.class);

    /**
     * The JAAS pSubject, which is part of the initial state and one of the provided arguments when the module is called
     */
    private Subject pSubject;

    /**
     * The JAAS callback handler, which is part of the initial state and one of the provided arguments when the module
     * is called
     */
    private CallbackHandler pCallbackHandler;

    /**
     * The username is provided during the login process, and we store a copy here in case of cascaded invocation
     */
    private String username;

    /**
     * The password is provided during the login process, and we store a copy here in case of cascaded invocation
     */
    private char[] password;

    /**
     * The white label domain is provided during the login process, and we store a copy here in case of cascaded
     * invocation
     */
    private String domain;

    /**
     * The uncommitted principals (this is != null once the login succeeded)
     */
    private Subject pendingSubject;
    /**
     * The committed principals (this is != null once the commit succeeded)
     */
    private Subject committedSubject;

    /**
     * The audit object is initialized based on the JAAS module configuration
     */
    private Audit audit;

    /**
     * The messageQ object is initialized based on the JAAS module configuration
     */
    private MessageQ messageQ;

    /**
     * The pwValidator object is initialized based on the JAAS module configuration
     */
    private PasswordValidator pwValidator;

    /**
     * The pwAuthenticator object is initialized based on the JAAS module configuration
     */
    private PasswordAuthenticator pwAuthenticator;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void initialize(final Subject subject, final CallbackHandler callbackHandler,
                                 final Map<String, ?> sharedState, final Map<String, ?> options) {

        LOG.debug("Initializing");

        Validate.notNull(subject);
        Validate.notNull(callbackHandler);
        Validate.notNull(sharedState);
        Validate.notNull(options);

        // keep a reference to the originally provided arguments (no defensive copy)
        this.pSubject = subject;
        this.pCallbackHandler = callbackHandler;

        // It would be nice to parse the configuration only once, and store it for later use. However, we are
        // deliberately NOT caching the parsed configuration, as JAAS does not offer a standard way to to reset the
        // cached variable, and allow users of the login module to reset the parsed config in case an app does need to
        // re-read its configuration.
        final CommonProperties commonProps = JaasBasedCommonPropsBuilder.build(options);

        // initialize the audit object
        initAudit(commonProps);

        // initialize the message object
        initMessageQueue(commonProps);

        // initialize the validator object
        initPwValidator(commonProps);

        // initialize the authenticator object
        initPwAuthenticator(commonProps);

        LOG.info("Initialization complete");
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} if authentication succeeds
     * @throws LoginException if this {@code LoginModule} is unable to perform the authentication. Catch a
     *                        {@link javax.security.auth.login.FailedLoginException} to determine if the authentication
     *                        failed (wrong username or password)
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final boolean login()
            throws LoginException {

        LOG.debug("Attempting login");

        if (pCallbackHandler == null) {
            final String error = "No CallbackHandler available to garner authentication information from the user";
            LOG.error(error);
            throw new LoginException(error);
        }
        Callback[] callbacks = new Callback[3];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PasswordCallback("Password: ", false);
        callbacks[2] = new TextInputCallback("Domain: ");

        try {
            pCallbackHandler.handle(callbacks);

            // store the username
            username = ((NameCallback) callbacks[0]).getName();

            // store the password (i.e. a copy of the password)
            final char[] tempPassword = ((PasswordCallback) callbacks[1]).getPassword();
            password = tempPassword.clone();

            // clear the password in the callback
            ((PasswordCallback) callbacks[1]).clearPassword();

            // store the domain
            domain = ((TextInputCallback) callbacks[2]).getText();
        } catch (java.io.IOException e) {
            cleanState();
            final String error = "Encountered an I/O exception during login";
            LOG.warn(error, e);
            throw Util.newLoginException(error, e);
        } catch (UnsupportedCallbackException e) {
            cleanState();
            final String error =
                    e.getCallback().toString() + " not available to garner authentication information from the user";
            LOG.warn(error, e);
            throw Util.newLoginException(error, e);
        }

        LOG.debug("Attempting login - discovered user '" + username + "@" + domain + "'");

        // Using a try/catch construct for managing control flows is really a bad idea.
        // Unfortunately, this is how JAAS works :-(
        try {
            // authenticate, and update state and pending subject if successful
            pendingSubject = pwAuthenticator.authenticate(domain, username, password, pwValidator);

            // then clear the password
            Util.zeroArray(password);

            final String baseError = new StringBuilder().
                    append("Login successful for '").
                    append(username).
                    append("@").
                    append(domain).
                    toString();
            AuditHelper.auditEvent(audit, domain, username, Events.AUTHN_ATTEMPT,
                    baseError + "', but cannot audit login attempt, and hence fail the operation");
            MessageHelper.postMessage(messageQ, domain, username, Events.AUTHN_ATTEMPT,
                    baseError + "', but cannot post MQ login attempt event, and hence fail the operation");

            // string concatenation is only executed if log level is actually enabled
            if (LOG.isInfoEnabled()) {
                LOG.info("Login complete for '" + username + "@" + domain + "'");
            }
            return true;
        } catch (LoginException e) {
            // the login failed

            // cache the username and domain, for they will be purged by "cleanState()"
            final String tempUsername = username;
            final String tempDomain = domain;

            cleanState();

            final String baseError = new StringBuilder().
                    append("Login failed for '").
                    append(tempUsername).
                    append("@").
                    append(tempDomain).
                    toString();
            AuditHelper.auditEvent(audit, tempDomain, tempUsername, Events.AUTHN_FAILURE,
                    baseError + "', but cannot audit login attempt");
            MessageHelper.postMessage(messageQ, tempDomain, tempUsername, Events.AUTHN_FAILURE,
                    baseError + "', but cannot post MQ login attempt event");

            final String error = "Login failed for '" + tempUsername + "@" + tempDomain + "'";
            LOG.info(error, e);

            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final boolean commit()
            throws LoginException {

        LOG.debug("Committing authentication");

        if (pendingSubject == null) {
            // the login method of this module has failed earlier, hence we do not need to clean up anything
            // return 'false' to indicate that this module's login and/or commit method failed
            // As the login method failed, the state of the module has already been cleared and we do not know
            // the username / domain anymore. Hence no auditing / message queue notification, and not verbose logging.

            LOG.debug("Not committing authentication, as the authentication has failed earlier (login method)");

            return false;
        } else {
            // The login has succeeded!
            // Store the principals from the pending subject both in the 'subject' object (because this is what JAAS
            // will use later on), but also create a new 'committedSubject' object that this module uses to a) keep
            // state and b) being able to remove the principals later

            LOG.debug("Committing authentication: '" + username + "@" + domain + "'");

            if (committedSubject == null) {
                committedSubject = new Subject();
            } else {

                // cache the username and domain, for they will be purged by "cleanState()"
                final String tempUsername = username;
                final String tempDomain = domain;

                cleanState();

                final String baseError = new StringBuilder().
                        append("Login post-processing failed for '").
                        append(tempUsername).
                        append("@").
                        append(tempDomain).
                        toString();
                AuditHelper.auditEvent(audit, tempDomain, tempUsername, Events.AUTHN_ERROR,
                        baseError + "', but cannot audit login attempt");
                MessageHelper.postMessage(messageQ, tempDomain, tempUsername, Events.AUTHN_ERROR,
                        baseError + "', but cannot post MQ login attempt event");

                final String error = "Expected the committed subject to be 'null' (yes, really <null>), but this was "
                        + "not the case! Has the commit method been called multiple times on the same object instance?";
                LOG.warn(error);
                throw new LoginException(error);
            }

            for (final Principal p : pendingSubject.getPrincipals()) {

                // 1. Add the principals to the 'subject' object
                if (!pSubject.getPrincipals().contains(p)) {
                    LOG.debug("Added principal " + p.getName() + " to subject");
                    pSubject.getPrincipals().add(p);
                }

                // 2. Add the principals to the 'committedSubject' object
                if (!committedSubject.getPrincipals().contains(p)) {
                    LOG.debug("Added principal " + p.getName() + " to committed subject");
                    committedSubject.getPrincipals().add(p);
                }
            }

            final String baseError = new StringBuilder().
                    append("Login succeeded for '").
                    append(username).
                    append("@").
                    append(domain).
                    toString();
            AuditHelper.auditEvent(audit, domain, username, Events.AUTHN_SUCCESS,
                    baseError + "', but cannot audit login success, and hence fail the operation");
            MessageHelper.postMessage(messageQ, domain, username, Events.AUTHN_SUCCESS,
                    baseError + "', but cannot post MQ login success event, and hence fail the operation");

            // string concatenation is only executed if log level is actually enabled
            if (LOG.isInfoEnabled()) {
                LOG.info("Authentication committed for '" + username + "@" + domain + "'");
            }

            // do not clean the state here, as we may still need it in case of an abort()
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final boolean abort()
            throws LoginException {

        if (pendingSubject == null) {
            // the login method of this module has failed earlier, hence we do not need to clean up anything
            // return 'false' to indicate that this module's login and/or commit method failed
            // As the login method failed, the state of the module has already been cleared and we do not know
            // the username / domain anymore. Hence no auditing / message queue notification, and not verbose logging.

            LOG.debug("Aborting authentication, as the authentication has failed earlier (login method)");

            return false;
        } else if (committedSubject == null) {
            // the login method of this module succeeded, but the overall authentication failed

            // string concatenation is only executed if log level is actually enabled
            if (LOG.isDebugEnabled()) {
                LOG.debug("Aborting authentication: '" + username + "@" + domain + "'");
            }

            // cache the username and domain, for they will be purged by "cleanState()"
            final String tempUsername = username;
            final String tempDomain = domain;

            cleanState();

            final String baseError = new StringBuilder().
                    append("Login post-processing failed after abort for '").
                    append(tempUsername).
                    append("@").
                    append(tempDomain).
                    toString();
            AuditHelper.auditEvent(audit, tempDomain, tempUsername, Events.AUTHN_ABORT_COMMIT,
                    baseError + "', but cannot audit login attempt");
            MessageHelper.postMessage(messageQ, tempDomain, tempUsername, Events.AUTHN_ABORT_COMMIT,
                    baseError + "', but cannot post MQ login attempt event");

            // string concatenation is only executed if log level is actually enabled
            if (LOG.isInfoEnabled()) {
                LOG.info("Authentication aborted for '" + tempUsername + "@" + tempDomain + "'");
            }
            return true;
        } else {
            // overall authentication succeeded and commit succeeded, but someone else's commit failed

            final String baseError = new StringBuilder().
                    append("Login post-processing failed after abort for '").
                    append(username).
                    append("@").
                    append(domain).
                    toString();
            AuditHelper.auditEvent(audit, domain, username, Events.AUTHN_ABORT_CHAIN,
                    baseError + "', but cannot audit login attempt");
            MessageHelper.postMessage(messageQ, domain, username, Events.AUTHN_ABORT_CHAIN,
                    baseError + "', but cannot post MQ login attempt event");

            // cache the username and domain, for they will be purged by "logout()"
            final String tempUsername = username;
            final String tempDomain = domain;

            logout();

            // string concatenation is only executed if log level is actually enabled
            if (LOG.isInfoEnabled()) {
                LOG.info("Authentication aborted for '" + tempUsername + "@" + tempDomain + "'");
            }
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final boolean logout()
            throws LoginException {

        final StringBuilder principals = new StringBuilder(":");

        // remove all the principals that we added in the commit() method from the 'subject' object
        // (that's why we stored our principals in the 'committedSubject' object...)
        if (committedSubject != null && committedSubject.getPrincipals() != null) {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Principal p : committedSubject.getPrincipals()) {
                pSubject.getPrincipals().remove(p);

                // string concatenation is only executed if log level is actually enabled
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Logging out subject: '" + p.getName() + "'");
                }

                principals.append(p.getName()).append(':');

                stringBuilder.delete(0, stringBuilder.length());
                final String baseError = stringBuilder.
                        append("Logout successful for '").
                        append(username).
                        append("@").
                        append(domain).
                        toString();
                AuditHelper.auditEvent(audit, domain, username, Events.AUTHN_LOGOUT,
                        baseError + "', but cannot audit logout attempt");
                MessageHelper.postMessage(messageQ, domain, username, Events.AUTHN_LOGOUT,
                        baseError + "', but cannot post MQ logout attempt event");
            }
        }

        cleanState();

        // string concatenation is only executed if log level is actually enabled
        if (LOG.isInfoEnabled()) {
            LOG.info("Principals logged out: '" + principals + "'");
        }

        return true;
    }

    /**
     * Clean up any state associated with the current login attempt.
     */
    @SuppressWarnings("PMD.NullAssignment")
    private void cleanState() {

        // null-assignments for de-referencing objects are okay
        domain = null;
        username = null;
        Util.zeroArray(password);
        pendingSubject = null;
        committedSubject = null;
    }

    /**
     * Initialize the instance-global audit object
     *
     * @param commonProps The parsed JAAS configuration, plus additional raw values
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    private void initAudit(final CommonProperties commonProps) {
        try {
            final String auditClassName = commonProps.getAuditClassName();

            // this would be harder to read when following PMD's advice - ignoring the PMD warning
            if (!commonProps.isAuditEnabled()) {
                final String error = "Auditing has been disabled in the JAAS configuration";
                LOG.info(error);
            } else if (auditClassName == null) {
                final String error =
                        "Auditing has been enabled in the JAAS configuration, but no audit class has been configured";
                LOG.error(error);
                throw new IllegalStateException(error);
            } else {
                if (commonProps.isAuditSingleton()) {
                    LOG.debug("Requesting singleton audit class instance of '" + auditClassName
                            + "' from the audit factory");
                    this.audit = AuditFactory.getSingleton(auditClassName, commonProps);
                } else {
                    LOG.debug("Requesting non-singleton audit class instance of '" + auditClassName
                            + "' from the audit factory");
                    this.audit = AuditFactory.getInstance(auditClassName, commonProps);
                }
            }
        } catch (FactoryException e) {
            final String error = "The audit class cannot be instantiated. This is most likely a configuration"
                    + " problem. Is the configured class available in the classpath?";
            LOG.error(error, e);
            throw new IllegalStateException(error, e);
        }
    }

    /**
     * Initialize the instance-global message queue object
     *
     * @param commonProps The parsed JAAS configuration, plus additional raw values
     */
    @SuppressWarnings("PMD.ConfusingTernary")
    private void initMessageQueue(final CommonProperties commonProps) {
        try {
            final String messageClassName = commonProps.getMessageQueueClassName();

            // this would be harder to read when following PMD's advice - ignoring the PMD warning
            if (!commonProps.isMessageQueueEnabled()) {
                final String error = "Message queue has been disabled in the JAAS configuration";
                LOG.info(error);
            } else if (messageClassName == null) {
                final String error = "Message queue has been enabled in the JAAS configuration, "
                        + "but no message queue class has been configured";
                LOG.error(error);
                throw new IllegalStateException(error);
            } else {
                if (commonProps.isMessageQueueSingleton()) {
                    LOG.debug("Requesting singleton message class instance of '" + messageClassName
                            + "' from the message factory");
                    this.messageQ = MessageQFactory.getSingleton(messageClassName, commonProps);
                } else {
                    LOG.debug("Requesting non-singleton message class instance of '" + messageClassName
                            + "' from the message factory");
                    this.messageQ = MessageQFactory.getInstance(messageClassName, commonProps);
                }
            }
        } catch (FactoryException e) {
            final String error = "The message class cannot be instantiated. This is most likely a configuration"
                    + " problem. Is the configured class available in the classpath?";
            LOG.error(error, e);
            throw new IllegalStateException(error, e);
        }
    }

    /**
     * Initialize the instance-global password validator object
     *
     * @param commonProps The parsed JAAS configuration, plus additional raw values
     */
    private void initPwValidator(final CommonProperties commonProps) {
        try {
            final String validatorClass = commonProps.getPasswordValidatorClassName();
            if (validatorClass == null) {
                final String error = "No password validator class has been configured in the JAAS configuration";
                LOG.error(error);
                throw new IllegalStateException(error);
            } else {
                if (commonProps.isPasswordValidatorSingleton()) {
                    // Fortify will report a violation here because of disclosure of potentially confidential
                    // information. However, the class name is not confidential, which makes this a non-issue / false
                    // positive.
                    LOG.debug("Requesting singleton validator class instance of '" + validatorClass
                            + "' from the validator factory");
                    this.pwValidator = PasswordValidatorFactory.getSingleton(validatorClass, commonProps);
                } else {
                    // Fortify will report a violation here because of disclosure of potentially confidential
                    // information. However, the class name is not confidential, which makes this a non-issue / false
                    // positive.
                    LOG.debug("Requesting non-singleton validator class instance of '" + validatorClass
                            + "' from the validator factory");
                    this.pwValidator = PasswordValidatorFactory.getInstance(validatorClass, commonProps);
                }
            }
        } catch (FactoryException e) {
            final String error = "The validator class cannot be instantiated. This is most likely a configuration"
                    + " problem. Is the configured class available in the classpath?";
            LOG.error(error, e);
            throw new IllegalStateException(error, e);
        }
    }

    /**
     * Initialize the instance-global password authenticator object
     *
     * @param commonProps The parsed JAAS configuration, plus additional raw values
     */
    private void initPwAuthenticator(final CommonProperties commonProps) {
        try {
            final String authNticatorClass = commonProps.getPasswordAuthenticatorClassName();
            if (authNticatorClass == null) {
                final String error = "No password authenticator class has been configured in the JAAS configuration";
                LOG.error(error);
                throw new IllegalStateException(error);
            } else {
                if (commonProps.isPasswordAuthenticatorSingleton()) {
                    // Fortify will report a violation here because of disclosure of potentially confidential
                    // information. However, the class name is not confidential, which makes this a non-issue / false
                    // positive.
                    LOG.debug("Requesting singleton authenticator class instance of '" + authNticatorClass
                            + "' from the authenticator factory");
                    this.pwAuthenticator = PasswordAuthenticatorFactory.getSingleton(authNticatorClass, commonProps);
                } else {
                    // Fortify will report a violation here because of disclosure of potentially confidential
                    // information. However, the class name is not confidential, which makes this a non-issue / false
                    // positive.
                    LOG.debug("Requesting non-singleton authenticator class instance of '" + authNticatorClass
                            + "' from the authenticator factory");
                    this.pwAuthenticator = PasswordAuthenticatorFactory.getInstance(authNticatorClass, commonProps);
                }
            }
        } catch (FactoryException e) {
            final String error = "The validator class cannot be instantiated. This is most likely a configuration"
                    + " problem. Is the configured class available in the classpath?";
            LOG.error(error, e);
            throw new IllegalStateException(error, e);
        }
    }

}
