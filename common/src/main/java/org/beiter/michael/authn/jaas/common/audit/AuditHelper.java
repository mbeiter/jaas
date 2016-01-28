/*
 * #%L
 * This file is part of a common library for a set of universal JAAS modules.
 * %%
 * Copyright (C) 2014 - 2016 Michael Beiter <michael@beiter.org>
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

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.Events;
import org.beiter.michael.authn.jaas.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * A helper class with utility methods to audit events
 */
public final class AuditHelper {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuditHelper.class);

    /**
     * A private constructor to prevent instantiation of this class
     */
    private AuditHelper() {
    }

    /**
     * Audit an event (during a login workflow).
     * <p>
     * If {@code audit} is {@code null}, then auditing is considered disabled
     *
     * @param audit    The audit object to use for auditing the event
     * @param domain   The user's domain
     * @param username The user's username
     *                 * @param event     The event to audit
     * @param error    The error message to be logged in the application log if auditing fails (i.e. no audit message
     *                 can be created)
     * @throws LoginException           If auditing fails (i.e. no audit message can be created)
     * @throws NullPointerException     When the {@code domain}, {@code username}, {@code event}, or {@code error} are
     *                                  {@code null}
     * @throws IllegalArgumentException When {@code domain}, {@code username}, or {@code error} are empty
     */
    public static void auditEvent(final Audit audit, final String domain, final String username,
                                  final Events event, final String error)
            throws LoginException {

        // "audit" may be null, not validating here (see below)
        Validate.notBlank(domain, "The validated character sequence 'domain' is null or empty");
        Validate.notBlank(username, "The validated character sequence 'username' is null or empty");
        Validate.notNull(event, "The validated object 'event' is null");
        Validate.notBlank(error, "The validated character sequence 'error' is null or empty");

        // if auditing is disabled, the audit object will not have been initialized
        if (audit == null) {
            // string concatenation is only executed if log level is actually enabled
            if (LOG.isDebugEnabled()) {
                LOG.debug("Auditing has been disabled, not creating event '" + event.getValue()
                        + "' for '" + username + "@" + domain + "'");
            }
        } else {
            try {
                audit.audit(event, domain, username);
            } catch (AuditException e) {
                LOG.warn(error, e);
                throw Util.newLoginException(error, e);
            }
        }
    }
}
