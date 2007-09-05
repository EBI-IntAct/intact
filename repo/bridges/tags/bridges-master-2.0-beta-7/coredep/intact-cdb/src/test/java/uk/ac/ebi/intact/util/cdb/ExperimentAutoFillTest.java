/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util.cdb;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * TODO comment this
 *
 * @author Catherine Leroy (cleroy@ebi.ac.uk)
 * @version $Id$
 * @since TODO
 */
public class ExperimentAutoFillTest  extends TestCase {
    public ExperimentAutoFillTest( String name ) {
	super( name );
    }

    public void setUp() throws Exception {
	super.setUp();
    }

    public void tearDown() throws Exception {
	super.tearDown();
    }

    public static Test suite() {
	return new TestSuite( ExperimentShortlabelGeneratorTest.class );
    }


    /////////////////////
    // Tests

    public void testGetFullname() {

	ExperimentAutoFill eaf = null;
	try {                              
	    eaf = new ExperimentAutoFill("15084279");
	} catch (PublicationNotFoundException e) {
	    fail();
	    e.printStackTrace();
	} catch (UnexpectedException e) {
	    fail();
	    e.printStackTrace();
	}
	assertEquals("Drebrin is a novel connexin-43 binding partner that links gap junctions to the submembrane cytoskeleton.",
		     eaf.getFullname());
    }

}
