/**
 * Provides interfaces, configurable factories, and default implementations for validating credentials in the JAAS
 * modules.
 *
 * Interfaces and factories provided at this time include:
 * <ul>
 *     <li>An interface and factory to validate username / password style credentials</li>
 * </ul>
 *
 * Default implementations provided at this time include:
 * <ul>
 *     <li>A default implementation that validates plain-text passwords (<strong>not secure</strong>, and should not
 *     be used in production environments)</li>
 * </ul>
 */
package org.beiter.michael.authn.jaas.common.validator;
