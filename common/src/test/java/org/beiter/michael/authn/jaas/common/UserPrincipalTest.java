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

import org.junit.Test;

import java.security.Principal;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class UserPrincipalTest {

    /**
     * Test that the principal ID assigned to a principal is correctly stored and returned
     * (using the Java Principal API)
     */
    @Test
    public void principalIdTest() {

        final String USER_ID = "someId";

        Principal principal = new UserPrincipal(USER_ID);

        String error = "Principal ID does not match expected value";
        assertThat(error, principal.getName(), is(equalTo(USER_ID)));
    }

    /**
     * Test that the principal ID assigned to a principal is correctly stored and returned
     */
    @Test
    public void userPrincipalIdTest() {

        final String USER_ID = "someId";

        UserPrincipal principal = new UserPrincipal(USER_ID);

        String error = "Principal ID does not match expected value";
        assertThat(error, principal.getName(), is(equalTo(USER_ID)));

        error = "Principal domain does not match expected value";
        assertNull(error, principal.getDomain());

        error = "Principal username does not match expected value";
        assertNull(error, principal.getUsername());
    }

    /**
     * Test that the principal information assigned to a principal is correctly stored and returned
     */
    @Test
    public void userPrincipalTest() {

        final String USER_ID = "someId";
        final String DOMAIN = "someDomain";
        final String USER_NAME = "someUserName";

        UserPrincipal principal = new UserPrincipal(USER_ID, DOMAIN, USER_NAME);

        String error = "Principal ID does not match expected value";
        assertThat(error, principal.getName(), is(equalTo(USER_ID)));

        error = "Principal domain does not match expected value";
        assertThat(error, principal.getDomain(), is(equalTo(DOMAIN)));

        error = "Principal username does not match expected value";
        assertThat(error, principal.getUsername(), is(equalTo(USER_NAME)));
    }
}
