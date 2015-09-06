/*
 * #%L
 * This file is part of a universal JAAS library, providing common functionality
 * for a username / password style JAAS module.
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
package org.beiter.michael.authn.jaas.loginmodules.password.common;

import org.beiter.michael.authn.jaas.common.CommonProperties;

/**
 * An interface to represent a password validation mechanism.
 * <p>
 * Classes implementing this interface <b>must</b> be thread safe.
 */
public interface PasswordValidator {

    /**
     * Initializes the password validator configuration.
     * <p>
     * A class implementing this interface must provide a reasonable default configuration and handle situations where
     * the {@code validate()} method is called without a previous call of {@code init()} (i.e. do not throw a runtime
     * exception).
     * <p>
     * A class implementing this interface must ensure that subsequent calls to this method update the class'
     * configuration in a thread-safe way.
     *
     * @param properties The properties to initialize the password validator with. Supported "additionalParameters" may
     *                   vary with the implementing classes.
     */
    void init(final CommonProperties properties);

    /**
     * Validate a user's password against a credential record.
     * <p>
     * A credential record may trivially be a plain text password (insecure, of course), but could also be a password
     * hash or a (serialized) data structure representing a data set for sophisticated validation algorithms.
     *
     * @param providedPassword The password to validate (commonly provided by the user)
     * @param storedCredential The record to validate against (commonly a credential record retrieved from a data store)
     * @return {@code true} if the credential could be validated, {@code false} otherwise (includes config issues)
     */
    // It would be pretty dumb to use varargs for the credential...
    @SuppressWarnings("PMD.UseVarargs")
    boolean validate(final char[] providedPassword, final char[] storedCredential);
}
