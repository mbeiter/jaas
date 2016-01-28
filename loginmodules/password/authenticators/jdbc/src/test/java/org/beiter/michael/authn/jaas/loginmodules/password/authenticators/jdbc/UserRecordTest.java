/*
 * #%L
 * This file is part of a universal JAAS library, providing a universal JDBC
 * authenticator implementation.
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
package org.beiter.michael.authn.jaas.loginmodules.password.authenticators.jdbc;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class UserRecordTest {

    /**
     * Test that the domain assigned to a user record is correctly stored and returned
     */
    @Test
    public void domainTest() {

        final String domain = "someDomain";
        final String userName = "someUserName";
        final String userId = "someUserID";
        final String credential = "someCredential";

        UserRecord userRecord = new UserRecord(domain, userName, userId, credential);

        String error = "Domain does not match expected value";
        assertThat(error, userRecord.getDomain(), is(equalTo(domain)));
    }

    /**
     * Test that the user name assigned to a user record is correctly stored and returned
     */
    @Test
    public void userNameTest() {

        final String domain = "someDomain";
        final String userName = "someUserName";
        final String userId = "someUserID";
        final String credential = "someCredential";

        UserRecord userRecord = new UserRecord(domain, userName, userId, credential);

        String error = "User name does not match expected value";
        assertThat(error, userRecord.getUserName(), is(equalTo(userName)));
    }

    /**
     * Test that the user ID assigned to a user record is correctly stored and returned
     */
    @Test
    public void userIdTest() {

        final String domain = "someDomain";
        final String userName = "someUserName";
        final String userId = "someUserID";
        final String credential = "someCredential";

        UserRecord userRecord = new UserRecord(domain, userName, userId, credential);

        String error = "User ID does not match expected value";
        assertThat(error, userRecord.getUserId(), is(equalTo(userId)));
    }

    /**
     * Test that the credential assigned to a user record is correctly stored and returned
     */
    @Test
    public void credentialTest() {

        final String domain = "someDomain";
        final String userName = "someUserName";
        final String userId = "someUserID";
        final String credential = "someCredential";

        UserRecord userRecord = new UserRecord(domain, userName, userId, credential);

        String error = "Credential does not match expected value";
        assertThat(error, userRecord.getCredential(), is(equalTo(credential)));
    }
}
