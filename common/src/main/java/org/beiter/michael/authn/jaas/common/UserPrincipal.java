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
package org.beiter.michael.authn.jaas.common;

import org.apache.commons.lang3.Validate;

import java.security.Principal;

/**
 * This class implements a generic user principal.
 * <p>
 * In addition to the {@code name} attribute (which uniquely identifies this principal), it has two additional optional
 * attributes: a {@code username} and {@code domain} that were used during authentication.
 * <p>
 * Usually the principal uniquely identifies a specific identity by either the {@code name} attribute, or by the
 * combination of <{@code username} and {@code domain}.
 */
public class UserPrincipal
        implements Principal {

    /**
     * The principal's unique ID.
     * <p>
     * This is what is used as the "name" in Java Principal terms.
     */
    private final String name;

    /**
     * The principal's domain
     */
    private final String domain;

    /**
     * The principal's username
     */
    private final String username;

    /**
     * Create a new principal without a username or domain set (only use the principal's name).
     *
     * @param name The principal's identifier (the "name" in Java Principal terms).
     * @throws NullPointerException When the {@code name} is {@code null}
     */
    // The null assignment allows to make username and domain final
    @SuppressWarnings("PMD.NullAssignment")
    public UserPrincipal(final String name) {

        Validate.notNull(name, "The validated object 'name' is null");

        // no need for defensive copies of String

        this.name = name;

        // initialize the domain and username to null
        this.domain = null;
        this.username = null;
    }

    /**
     * Create a new principal with the username and domain that were used for authentication (in addition to the
     * principal's name).
     *
     * @param name     The principal's identifier (the "name" in Java Principal terms).
     * @param domain   The domain used to authenticate the principal
     * @param username The username used to authenticate the principal
     * @throws NullPointerException When the {@code name}, {@code domain}, or {@code username} are {@code null}
     */
    public UserPrincipal(final String name, final String domain, final String username) {

        Validate.notNull(name, "The validated object 'name' is null");
        Validate.notNull(domain, "The validated object 'domain' is null");
        Validate.notNull(username, "The validated object 'username' is null");

        // no need for defensive copies of String

        this.name = name;
        this.domain = domain;
        this.username = username;
    }

    @Override
    public final String getName() {

        // no need for defensive copies of String

        return name;
    }

    /**
     * Return the principal's domain that was used for authentication.
     * <p>
     * This is an optional field, and may return a {@code null} value if username and domain were not provided for
     * this principal.
     *
     * @return The principal's domain, or {@code null} if the domain has not been set.
     */
    public final String getDomain() {

        // no need for defensive copies of String

        return domain;
    }

    /**
     * Return the principal's username that was used for authentication.
     * <p>
     * This is an optional field, and may return a {@code null} value if username and domain were not provided for
     * this principal.
     *
     * @return The principal's username, or {@code null} if the username has not been set.
     */
    public final String getUsername() {

        // no need for defensive copies of String

        return username;
    }
}
