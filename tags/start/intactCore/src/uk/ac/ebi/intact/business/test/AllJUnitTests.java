/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.business.test;

import junit.framework.*;


/**
 *  This class is the template test class which can be used by any
 * tool to auto-generate test suites. Currently for the business package there
 * is only the test case for the IntactHelper class in this suite.
 */
public class AllJUnitTests extends TestCase {

   public AllJUnitTests(String name) {

       super(name);
   }

    public static Test suite() {

        TestSuite suite = new TestSuite();

        //only one class to test in this package (so far!)
        suite.addTest(IntactHelperTest.suite());
        return suite;
    }
}
