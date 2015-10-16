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
import org.beiter.michael.authn.jaas.common.Events;

/**
 * An interface to connect a per-JAAS module auditing subsystem.
 * <p>
 * Classes implementing this interface <b>must</b> be thread safe.
 */
public interface Audit {

    /**
     * Initializes the audit subsystem configuration
     * <p>
     * A class implementing this interface may provide a reasonable default configuration and handle situations where
     * the {@code audit()} method is called without a previous call of {@code init()} (i.e. do not throw any
     * exception), but this is not guaranteed (i.e. calling the {@code audit()} method without proper initialization
     * may throw an exception, such as an {@link IllegalStateException} or a {@link AuditException}).
     * <p>
     * A class implementing this interface must ensure that subsequent calls to this method update the class'
     * configuration in a thread-safe way.
     *
     * @param properties The properties to initialize the audit subsystem with. Supported "additionalParameters" may
     *                   vary with the implementing classes.
     */
    void init(CommonProperties properties);

    /**
     * Audit an event for the provided user ID (i.e. for the principal)
     *
     * @param event  The event to audit
     * @param userId The user ID (globally unique) to audit activities for
     * @throws AuditException when the audit operation fails.
     */
    void audit(final Events event, final String userId)
            throws AuditException;

    /**
     * Audit an event for the provided username (<b>not</b> for the principal) in the specified white label domain.
     * <p>
     * A user is uniquely identified by the combination of domain and username.
     *
     * @param event    The event to audit
     * @param domain   The white label domain in which the username is located
     * @param username The username to audit activities for
     * @throws AuditException when the audit operation fails.
     */
    void audit(final Events event, final String domain, final String username)
            throws AuditException;
}
