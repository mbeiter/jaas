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
import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This default audit implementation logs all audit messages to the Java logging subsystem.
 * <p>
 * This is commonly <b>not</b> recommended for production.
 */
public class SampleAuditLogger
        implements Audit {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(SampleAuditLogger.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public final void init(final CommonProperties properties) {

        // nothing to do here - this implementation does not take any properties
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     When the {@code event} or {@code userId} are {@code null}
     * @throws IllegalArgumentException When {@code userId} is empty
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final void audit(final Events event, final String userId) {

        Validate.notNull(event, "The validated object 'event' is null");
        Validate.notBlank(userId, "The validated character sequence 'userId' is null or empty");

        // PMD does not recognize the guarded log statement
        if (LOG.isInfoEnabled()) {
            LOG.info("[AUDIT] " + event.getValue() + ". User ID '" + userId);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException     When the {@code event}, {@code domain} or {@code username} are {@code null}
     * @throws IllegalArgumentException When {@code domain}, {@code username} is empty
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final void audit(final Events event, final String domain, final String username) {

        Validate.notNull(event, "The validated object 'event' is null");
        Validate.notBlank(domain, "The validated character sequence 'domain' is null or empty");
        Validate.notBlank(username, "The validated character sequence 'username' is null or empty");

        // PMD does not recognize the guarded log statement
        if (LOG.isInfoEnabled()) {
            LOG.info("[AUDIT] " + event.getValue() + ". User name '" + username + "', domain '" + domain + "'");
        }
    }
}
