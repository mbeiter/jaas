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

/**
 * This class encapsulates user information obtained from a JDBC database
 */
public final class UserRecord {

    /**
     * The user's domain used for authentication
     */
    private final String domain;

    /**
     * The user's username used for authentication
     */
    private final String userName;

    /**
     * The user's user ID (as retrieved from the database)
     */
    private final String userId;

    /**
     * The user's credential (as retrieved from the database)
     */
    private final String credential;

    /**
     * Create an instance of a user
     *
     * @param domain     The user's domain used for authentication
     * @param userName   The user's username used for authentication
     * @param userId     The user's user ID (as retrieved from the database)
     * @param credential The user's credential (as retrieved from the database)
     */
    public UserRecord(final String domain, final String userName, final String userId, final String credential) {

        this.domain = domain;
        this.userName = userName;
        this.userId = userId;
        this.credential = credential;
    }

    /**
     * Return the user's domain used for authentication
     *
     * @return The user's domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Return the user's username used for authentication
     *
     * @return The user's username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Return the user's user ID (as retrieved from the database)
     *
     * @return The user's user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Return the user's credential (as retrieved from the database)
     *
     * @return The user's credential
     */
    public String getCredential() {
        return credential;
    }
}
