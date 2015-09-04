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
package org.beiter.michael.authn.jaas.common.validator;

import org.beiter.michael.authn.jaas.common.FactoryException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PasswordValidatorFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(PasswordValidatorFactoryTest.class);

    /**
     * Reset the password validator factory to allow creating several instances of the underlying password validator
     * implementations.
     */
    @Before
    public void unsetSingletonInFactory() {

        PasswordValidatorFactory.reset();
    }

    /**
     * A non-existing class name (i.e. a class not in the class path) should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getNonExistingImplementationTest()
            throws FactoryException {

        PasswordValidatorFactory.getInstance("someGarbageName", new ConcurrentHashMap<String, Object>());
    }

    /**
     * An invalid class name (i.e. a class of the wrong type) should throw an exception
     */
    @Test(expected = FactoryException.class)
    public void getInvalidImplementationTest()
            throws FactoryException {

        PasswordValidatorFactory.getInstance(String.class.getCanonicalName(), new ConcurrentHashMap<String, Object>());
    }

    /**
     * Retrieve a specific implementation of the PasswordValidator interface, and assert that the returned
     * implementation equals the requested implementation.
     */
    @Test
    public void getSpecificImplementationTest() {

        String className = "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator";

        PasswordValidator passwordValidator;
        try {
            passwordValidator = PasswordValidatorFactory.getInstance(className, new ConcurrentHashMap<String, Object>());
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The class instantiated by the factory does not match the expected class";
        assertThat(error, PlainTextPasswordValidator.class.getCanonicalName(), is(equalTo(className)));
    }

    /**
     * Retrieve two instances of a specific implementation of the PasswordValidator interface, and assert that the
     * two returned objects are identical (i.e. the factory returns a singleton).
     * <p/>
     * Then the factory is reset, and another instance is retrieved. If the factory resets properly, the third instance
     * must be unequal to the first two instances.
     */
    @Test
    public void factoryReturnsSingletonTest() {

        String className = "org.beiter.michael.authn.jaas.common.validator.PlainTextPasswordValidator";

        PasswordValidator passwordValidator1, passwordValidator2;
        try {
            passwordValidator1 = PasswordValidatorFactory.getInstance(className, new ConcurrentHashMap<String, Object>());
            passwordValidator2 = PasswordValidatorFactory.getInstance(className, new ConcurrentHashMap<String, Object>());
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        String error = "The factory does not return a singleton";
        assertThat(error, passwordValidator1, is(sameInstance(passwordValidator2)));

        // reset the factory
        PasswordValidatorFactory.reset();

        // now test that the factory return a new object (i.e. a new singleton)
        PasswordValidator passwordValidator3;
        try {
            passwordValidator3 = PasswordValidatorFactory.getInstance(className, new ConcurrentHashMap<String, Object>());
        } catch (FactoryException e) {
            AssertionError ae = new AssertionError("Instantiation error");
            ae.initCause(e);
            throw ae;
        }

        error = "The factory does not return a singleton, or does not reset properly";
        assertThat(error, passwordValidator1, is(not(sameInstance(passwordValidator3))));
    }
}
