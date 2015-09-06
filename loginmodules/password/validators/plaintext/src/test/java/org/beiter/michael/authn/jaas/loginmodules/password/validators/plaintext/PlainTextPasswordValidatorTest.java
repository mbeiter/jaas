/*
 * #%L
 * This file is part of a universal JAAS library, providing a plaintext password
 * validator.
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
package org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PlainTextPasswordValidatorTest {

    /**
     * Test that, if one of the two passwords is null ("provided password" or "stored password"), the result of the
     * validation is false
     */
    @Test
    public void validateNullPasswordsTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PlainTextPasswordValidator validator = new PlainTextPasswordValidator();
        validator.init(commonProps);

        String error = "Validating a provided password with null value should be 'false'";
        assertThat(error, validator.validate(null, "SomePassword".toCharArray()), is(equalTo(false)));

        error = "Validating a provided password against a stored value with null value should be 'false'";
        assertThat(error, validator.validate("SomePassword".toCharArray(), null), is(equalTo(false)));
    }

    /**
     * Test that the validation result is false if the passwords do not match
     */
    @Test
    public void validateMismatchingPasswordsTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PlainTextPasswordValidator validator = new PlainTextPasswordValidator();
        validator.init(commonProps);

        String error = "Validating two mismatched passwords should be 'false'";
        assertThat(error, validator.validate("SomePassword".toCharArray(), "SomeOtherPassword".toCharArray()),
                is(equalTo(false)));
    }

    /**
     * Test that the validation result is true if the passwords match
     */
    @Test
    public void validateMatchingPasswordsTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PlainTextPasswordValidator validator = new PlainTextPasswordValidator();
        validator.init(commonProps);

        String error = "Validating two matching passwords should be 'true'";
        assertThat(error, validator.validate("Password".toCharArray(), "Password".toCharArray()), is(equalTo(true)));
    }
}


