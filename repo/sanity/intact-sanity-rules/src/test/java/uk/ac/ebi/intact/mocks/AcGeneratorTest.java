/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.mocks;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class AcGeneratorTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AcGeneratorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AcGeneratorTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testgetNextVal(){
        assertEquals("EBI-1", AcGenerator.getNextVal());
        assertEquals("EBI-2", AcGenerator.getNextVal());
        assertEquals("EBI-3", AcGenerator.getNextVal());
    }
}