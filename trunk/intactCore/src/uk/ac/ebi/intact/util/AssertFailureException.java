/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.util;

/**
 * Support for assert. This class is thrown when an assert fails.
 * This is an unchecked exception as it is subclassed on <code>Error</code>.
 *
 * <p>
 * This class is based on the Ex_AssertFailure class described in ATOMS project
 * (wwwatoms.atnf.csiro.au) by David Loone.
 *
 * @author Sugath Mudali (smudali@ebi.ac.uk)
 * @version $Id$
 */
public final class AssertFailureException extends Error {

    /**
     * Default constructor.
     */
    public AssertFailureException() {
        super();
    }

    /**
     * Make an assertion exception with a string to identify the assertion.
     *
     * @param assertStr A string to identify the assertion.
     */
    public AssertFailureException(String assertStr) {
        super(assertStr);
    }
 }
