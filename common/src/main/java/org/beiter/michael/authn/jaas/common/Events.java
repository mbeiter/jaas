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

/**
 * The events that are available in a JAAS module.
 */
public enum Events {
    //CHECKSTYLE:OFF
    AUTHN_ATTEMPT("A user's credentials have been successfully validated"),
    AUTHN_FAILURE("A user's credentials could not be validated"),
    AUTHN_ERROR("A service failure prevented the system from completing the request"),
    AUTHN_COMMIT_FAILURE("A commit was aborted due to earlier failures"),
    AUTHN_ABORT_FAILURE("A login attempt was aborted due to earlier credential validation failures"),
    AUTHN_ABORT_COMMIT("Although the authentication succeeded, a login attempt was aborted due to the commit failing"),
    AUTHN_ABORT_CHAIN("Although the authentication and the commit succeeded, a login attempt was aborted due to the commit of another module failing"),
    AUTHN_SUCCESS("A user has been successfully authenticated"),
    AUTHN_LOGOUT("A user has been logged out");

    private final String value;

    private Events(final String keyName) {
        this.value = keyName;
    }


    public String getValue() {
        return value;
    }
    //CHECKSTYLE:ON
}
