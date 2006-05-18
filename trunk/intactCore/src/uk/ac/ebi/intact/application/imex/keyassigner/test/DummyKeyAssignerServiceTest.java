/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */

package uk.ac.ebi.intact.application.imex.keyassigner.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.imex.id.IMExRange;
import uk.ac.ebi.intact.application.imex.keyassigner.DummyKeyAssignerService;
import uk.ac.ebi.intact.application.imex.keyassigner.KeyAssignerServiceException;
import uk.ac.ebi.intact.application.imex.keyassigner.KeyAssignerServiceI;

/**
 * DummyKeyAssignerService Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since <pre>05/16/2006</pre>
 */
public class DummyKeyAssignerServiceTest extends TestCase {

    public DummyKeyAssignerServiceTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        return new TestSuite( DummyKeyAssignerServiceTest.class );
    }

    /////////////////////
    // Tests

    public void testGetAccessions() {
        KeyAssignerServiceI service = new DummyKeyAssignerService( 1, 1 );
        try {
            IMExRange range = service.getAccessions( 5 );

            assertEquals( 1, range.getSubmissionId() );
            assertEquals( 1, range.getFrom() );
            assertEquals( 5, range.getTo() );

            range = service.getAccessions( 2 );

            assertEquals( 2, range.getSubmissionId() );
            assertEquals( 6, range.getFrom() );
            assertEquals( 7, range.getTo() );

            range = service.getAccessions( 10 );

            assertEquals( 3, range.getSubmissionId() );
            assertEquals( 8, range.getFrom() );
            assertEquals( 17, range.getTo() );

        } catch ( KeyAssignerServiceException e ) {
            fail();
        }
    }
}
