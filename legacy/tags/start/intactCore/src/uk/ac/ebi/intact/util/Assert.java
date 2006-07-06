/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 * Class containing support assertions. All the methods in
 * this class are static.
 *
 * <p>
 * If the system property <code>intact.env.ASSERT_ENABLE</code> exists,
 * then this is used to initially enable or disable assertions.
 * If the property does not exist, then assertions are initially enabled.
 * They can be enabled and disabled by calling the
 * <code>enable</code> and <code>disable</code> methods respectively.
 *
 * <p>
 * This class is based on the Assert class described in ATOMS project
 * (wwwatoms.atnf.csiro.au) by David Loone.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public final class Assert {

    /**
     * Keep track of whether assertions are curently enabled or not.
     */
    private static boolean theirEnabled = true;

    /**
     * Static initializer.
     * Use the system property <code>intact.env.ASSERT_ENABLE</code> value if
     * it exists.
     */
    static {
        // The property value.
        String assertEnable = System.getProperty("intact.env.ASSERT_ENABLE");
        if (assertEnable != null) {
            theirEnabled = Boolean.valueOf(assertEnable).booleanValue();
        }
        else {
            theirEnabled = true;
        }
    }

    /**
     * Constructor. Made it private to stop it from instantiating.
     */
    private Assert() {
    }

    /**
     * Enable assertions.
     */
    public static final void enable() {
        theirEnabled = true;
    }

    /**
     * Disable assertions.
     */
    public static final void disable() {
        theirEnabled = false;
    }

    /**
     * See whether assertions are enabled.
     *
     * @return returns <code>true</code> if assertions are enabled,
     *  or <code>false</code> if they are not.
     */
    public static final boolean isEnabled() {
        return theirEnabled;
    }

    /**
     * Make an assertion.
     *
     * @param assertion An expression that evaluates to a boolean.
     *
     * @exception AssertFailureException Thrown when assertions are enabled,
     *  and <code>assertion</code> is <code>false</code>.
     */
    public static final void assert(boolean assertion)
        throws AssertFailureException {
        // Only throw the exception if assertions are enabled.
        if (theirEnabled && !assertion) {
            throw new AssertFailureException();
        }
    }

    /**
     * Make an assertion.
     *
     * @param assertion An expression that evaluates to a boolean.
     * @param assertStr A string to identify the assertion.
     *
     * @exception AssertFailureException Thrown when assertions are enabled,
     *  and <code>assertion</code> is <code>false</code>.
     */
    public static final void assert(boolean assertion, String assertStr)
        throws AssertFailureException {
        // Only throw the exception if assertions are enabled.
        if (theirEnabled && !assertion) {
            throw new AssertFailureException(assertStr);
        }
    }

    /**
     * Make an assertion failure.
     *
     * @exception AssertFailureException Thrown when assertions are enabled.
     */
    public static final void fail() {
        // Only throw the exception if assertions are enabled.
        if (theirEnabled) {
            throw new AssertFailureException();
        }
    }

    /**
     * Make an assertion failure with a message.
     *
     * @param assertStr A string to identify the assertion.
     *
     * @exception AssertFailureException Thrown when assertions are enabled.
     */
    public static final void fail(String assertStr) throws AssertFailureException {
        // Only throw the exception if assertions are enabled.
        if (theirEnabled) {
            throw new AssertFailureException(assertStr);
        }
    }

    /**
     * Make an assertion failure.
     *
     * @param exception A exception to identify the assertion.
     *
     * @exception AssertFailureException Thrown when assertions are enabled.
     */
    public static final void fail(Throwable exception)
        throws AssertFailureException {
        // Print the exception stack trace.
        exception.printStackTrace();

        // Only throw the exception if assertions are enabled.
        if (theirEnabled) {
            throw new AssertFailureException(exception.toString());
        }
    }
}
