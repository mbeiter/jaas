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

import org.apache.commons.lang3.Validate;
import org.beiter.michael.authn.jaas.common.Events;
import org.beiter.michael.authn.jaas.common.JaasConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This default audit implementation logs all audit messages to the Java logging subsystem.
 * <p/>
 * This is commonly <b>not</b> recommended for production.
 */
public class AuditLogger
        implements Audit {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(AuditLogger.class);

    /**
     * The name of the configuration parameter to control whether this class actually does anything
     */
    public static final String CFGPAR_ENABLED = JaasConfigOptions.AUDIT_ENABLED.getName();

    /**
     * The configuration parameter that controls whether this class actually does anything
     */
    private boolean enabled;

    /**
     * This constructor creates an instance of the class that has an enabled logger.
     */
    public AuditLogger() {

        // set default configuration
        enabled = true;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Available properties are:
     * <ul>
     * <li>default.audit.enabled: whether this audit module is enabled (true/false)</li>
     * </ul>
     */
    @Override
    public final void init(final Map<String, ?> properties) {

        Validate.notNull(properties);

        // no need for defensive copies here, as all internal config values are calculated

        final Object confParamEnabled = properties.get(CFGPAR_ENABLED);
        enabled = confParamEnabled != null && String.class.isInstance(confParamEnabled)
                && ((String) confParamEnabled).equalsIgnoreCase("true");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final void audit(final Events event, final String userId) {

        // PMD does not recognize the guarded log statement
        if (enabled && LOG.isInfoEnabled()) {
            LOG.info("[AUDIT] " + event.getValue() + ". User ID '" + userId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // Check is broken [LOG.info()]: PMD reports issues although log stmt is guarded. @todo revisit when upgrading PMD.
    @SuppressWarnings("PMD.GuardLogStatementJavaUtil")
    public final void audit(final Events event, final String domain, final String username) {

        // PMD does not recognize the guarded log statement
        if (enabled && LOG.isInfoEnabled()) {
            LOG.info("[AUDIT] " + event.getValue() + ". User name '" + username + "', domain '" + domain + "'");
        }
    }
}
