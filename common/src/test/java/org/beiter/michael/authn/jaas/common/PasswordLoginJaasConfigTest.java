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

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import java.security.Principal;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Test the password login module using a JAAS configuration
 */
public class PasswordLoginJaasConfigTest {

    private static final String JAAS_CONFIG = "/JaasConfig.txt";
    private static final String JAAS_CONFIG_FILE_SYSTEM_PROPERTY_NAME = "java.security.auth.login.config";

    @Test
    public void dummyPasswordLoginTest() throws Exception {
        System.setProperty(JAAS_CONFIG_FILE_SYSTEM_PROPERTY_NAME, getClass().getResource(JAAS_CONFIG).getFile());

        String domain = "testDomain";
        String username = "testUsername";
        char[] password = username.toCharArray();

        // This is the JAAS login workflow...
        LoginContext loginContext = new LoginContext("PWLoginDummyAuthenticatorJaasConfig",
                new PasswordCallbackHandler(domain, username, password));
        loginContext.login();

        Subject subject = loginContext.getSubject();
        // ... JAAS login workflow complete!

        // This part is identical to the DummyPasswordAuthenticator authentication test
        // ----
        String error = "The number of principals in subject does not match the expected value";
        assertThat(error, subject.getPrincipals().size(), is(equalTo(1)));

        error = "The returned principal object is not a Principal - this should never happen!";
        assertThat(error, subject.getPrincipals().toArray()[0], is(instanceOf(Principal.class)));

        Principal principal = (Principal) subject.getPrincipals().toArray()[0];
        error = "Subject name (i.e. subject unique ID) mismatch";
        // the DummyAuthenticator defines the principal unique ID as "ID:" + username
        assertThat(error, principal.getName(), is(equalTo("ID:" + username)));

        // check advanced properties of the UserPrincipal object
        error = "The returned principal object is not a UserPrincipal";
        assertThat(error, subject.getPrincipals().toArray()[0], is(instanceOf(UserPrincipal.class)));

        UserPrincipal userPrincipal = (UserPrincipal) subject.getPrincipals().toArray()[0];

        error = "Subject domain mismatch";
        assertThat(error, userPrincipal.getDomain(), is(equalTo(domain)));

        error = "Subject username mismatch";
        assertThat(error, userPrincipal.getUsername(), is(equalTo(username)));
    }
}
