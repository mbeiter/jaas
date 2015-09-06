/*
 * #%L
 * This file is part of a universal JAAS library, providing a dummy authenticator
 * for password based credential authentication.
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
package org.beiter.michael.authn.jaas.loginmodules.authenticators.dummy;

import org.beiter.michael.authn.jaas.common.CommonProperties;
import org.beiter.michael.authn.jaas.common.FactoryException;
import org.beiter.michael.authn.jaas.common.propsbuilder.JaasBasedCommonPropsBuilder;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordAuthenticator;
import org.beiter.michael.authn.jaas.loginmodules.password.common.PasswordAuthenticatorFactory;
import org.beiter.michael.authn.jaas.loginmodules.password.authenticators.dummy.DummyPasswordAuthenticator;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PasswordAuthenticatorFactoryExtendedTest {

    /**
     * The logger object for this class
     */
    private static final Logger LOG = LoggerFactory.getLogger(PasswordAuthenticatorFactoryExtendedTest.class);

    /**
     * The test class to instantiate
     */
    private static final String className = "org.beiter.michael.authn.jaas.loginmodules.password.authenticators.dummy.DummyPasswordAuthenticator";

    /**
     * Reset the password authenticator factory to allow creating several instances of the underlying password
     * authenticator implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        PasswordAuthenticatorFactory.reset();
    }

    /**
     * Retrieve a specific implementation of the PasswordAuthenticator interface, and assert that the returned
     * implementation equals the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordAuthenticator passwordAuthenticator;
        try {
            passwordAuthenticator = PasswordAuthenticatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, DummyPasswordAuthenticator.class.getCanonicalName(), is(equalTo(className)));
        assertThat(error, passwordAuthenticator.getClass().getCanonicalName(), is(equalTo(className)));
    }

    /**
     * Retrieve two instances of a specific implementation of the PasswordAuthenticator interface, and asserts that the
     * returned objects are two separate instances.
     */
    @Test
    public void twoInstancesAreDifferentTest() {

        CommonProperties commonProps = JaasBasedCommonPropsBuilder.buildDefault();

        PasswordAuthenticator passwordAuthenticator1, passwordAuthenticator2;
        try {
            passwordAuthenticator1 = PasswordAuthenticatorFactory.getInstance(className, commonProps);
            passwordAuthenticator2 = PasswordAuthenticatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory returns a singleton instead of a new object";
        assertThat(error, passwordAuthenticator1, is(not(sameInstance(passwordAuthenticator2))));
    }

    /**
     * Retrieve two singleton instances of a specific implementation of the PasswordAuthenticator interface, and asserts
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
        PasswordAuthenticator passwordAuthenticator1, passwordAuthenticator2;
        try {
            passwordAuthenticator1 = PasswordAuthenticatorFactory.getSingleton(className, commonProps);
            passwordAuthenticator2 = PasswordAuthenticatorFactory.getSingleton(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, passwordAuthenticator1, is(sameInstance(passwordAuthenticator2)));

        // then test that a regular (non-singleton) instance is different
        PasswordAuthenticator passwordAuthenticator3;
        try {
            passwordAuthenticator3 = PasswordAuthenticatorFactory.getInstance(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }
        error = "The factory returns a singleton instead of a new object";
        assertThat(error, passwordAuthenticator1, is(not(sameInstance(passwordAuthenticator3))));
        assertThat(error, passwordAuthenticator2, is(not(sameInstance(passwordAuthenticator3))));

        // reset the factory
        PasswordAuthenticatorFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        PasswordAuthenticator passwordAuthenticator4;
        try {
            passwordAuthenticator4 = PasswordAuthenticatorFactory.getSingleton(className, commonProps);
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, passwordAuthenticator1, is(not(sameInstance(passwordAuthenticator4))));
        assertThat(error, passwordAuthenticator2, is(not(sameInstance(passwordAuthenticator4))));
        assertThat(error, passwordAuthenticator3, is(not(sameInstance(passwordAuthenticator4))));
    }
}
