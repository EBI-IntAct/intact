/*
Copyright (c) 2002-2004 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.application.editor.test.pg;

// JUnit classes

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.editor.test.LoginTest;
import uk.ac.ebi.intact.application.editor.test.pg.biosource.BioSourceDisplayTest;
import uk.ac.ebi.intact.application.editor.test.pg.biosource.BioSourceTests;

/**
 * Testsuite for Postgres database.
 *
 * @author Sugath Mudali
 * @version $Id$
 */
public class AllJUnitTests extends TestCase {

    /**
     * The constructor with the test name.
     *
     * @param name the name of the test.
     */
    public AllJUnitTests(String name) {
        super(name);
    }

    /**
     * Returns a suite containing tests.
     * @return a suite containing tests.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();
//        suite.addTest(LoginTest.suite());
//        suite.addTest(SearchTest.suite());
        suite.addTest(BioSourceTests.suite());
        return suite;
    }
}
