/*
 * #%L
 * This file is part of a universal JAAS library, providing a plaintext password
 * validator.
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
package org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordValidator;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PasswordValidatorFactoryExtendedTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordValidatorFactoryExtendedTest.class);

    /**
     * The test class to instantiate
     */
    String className = "org.beiter.michael.authn.jaas.loginmodules.password.validators.plaintext.PlainTextPasswordValidator";

    /**
     * Reset the password validator factory to allow creating several instances of the underlying password validator
     * implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        PasswordValidatorFactory.reset();
    }

    /**
     * Retrieve a specific implementation of the PasswordValidator interface, and assert that the returned
     * implementation equals the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordValidator passwordValidator;
        try {
            passwordValidator = PasswordValidatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, PlainTextPasswordValidator.class.getCanonicalName(), is(equalTo(className)));
    }

    /**
     * Retrieve two instances of a specific implementation of the PasswordValidator interface, and asserts that the
     * returned objects are two separate instances.
     */
    @Test
    public void twoInstancesAreDifferentTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordValidator passwordValidator1, passwordValidator2;
        try {
            passwordValidator1 = PasswordValidatorFactory.getInstance(className, commonProps);
            passwordValidator2 = PasswordValidatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory returns a singleton instead of a new object";
        assertThat(error, passwordValidator1, is(not(sameInstance(passwordValidator2))));
    }

    /**
     * Retrieve two singleton instances of a specific implementation of the PasswordValidator interface, and asserts
     * that the two returned objects are identical (i.e. the factory returns a singleton).
     * <p>
     * Then, a regular (non-singleton) instance is retrieved, which are asserted to be different than the previously
     * retrieved objects.
     * <p>
     * Finally, the factory is reset, and another instance is retrieved. If the factory resets properly, the third
     * instance must be unequal to the first three instances.
     */
    @Test
    public void factoryReturnsSingletonTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        // test that two singletons retrieved from the factory are identical
        PasswordValidator passwordValidator1, passwordValidator2;
        try {
            passwordValidator1 = PasswordValidatorFactory.getSingleton(className, commonProps);
            passwordValidator2 = PasswordValidatorFactory.getSingleton(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, passwordValidator1, is(sameInstance(passwordValidator2)));

        // then test that a regular (non-singleton) instance is different
        PasswordValidator passwordValidator3;
        try {
            passwordValidator3 = PasswordValidatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }
        error = "The factory returns a singleton instead of a new object";
        assertThat(error, passwordValidator1, is(not(sameInstance(passwordValidator3))));
        assertThat(error, passwordValidator2, is(not(sameInstance(passwordValidator3))));

        // reset the factory
        PasswordValidatorFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        PasswordValidator passwordValidator4;
        try {
            passwordValidator4 = PasswordValidatorFactory.getSingleton(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, passwordValidator1, is(not(sameInstance(passwordValidator4))));
        assertThat(error, passwordValidator2, is(not(sameInstance(passwordValidator4))));
        assertThat(error, passwordValidator3, is(not(sameInstance(passwordValidator4))));
    }
}
