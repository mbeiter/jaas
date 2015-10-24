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

import org.apache.commons.lang3.Validate;

import javax.security.auth.login.LoginException;

/**
 * A utility class with various utility methods.
 */
@SuppressWarnings("PMD.ShortClassName")
public final class Util {

    /**
     * A private constructor to prevent instantiation of this class
     */
    private Util() {
    }

    /**
     * Create a {@code LoginException} with a cause.
     *
     * @param message the detail message (which is saved for later retrieval by the
     *                {@link java.lang.Exception#getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link java.lang.Exception#getCause()}
     *                method). (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @return a new {@code LoginException}
     * @throws NullPointerException     When the {@code message} or {@code cause} are {@code null}
     * @throws IllegalArgumentException When {@code message} is empty
     * @see Exception#Exception(String, Throwable)
     */
    public static LoginException newLoginException(final String message, final Throwable cause) {

        Validate.notBlank(message, "The validated character sequence 'message' is null or empty");
        Validate.notNull(cause, "The validated object 'cause' is null");

        final LoginException loginException = new LoginException(message);
        loginException.initCause(cause);
        return loginException;
    }
}
