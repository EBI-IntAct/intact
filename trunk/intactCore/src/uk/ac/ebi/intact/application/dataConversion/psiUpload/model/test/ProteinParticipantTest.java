/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.model.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinParticipantTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public ProteinParticipantTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( ProteinParticipantTest.class );
    }

    public void testProcess_ok() {

        XrefTag xrefCellType = new XrefTag( XrefTag.PRIMARY_REF, "id", "db" );
        CellTypeTag cellType = new CellTypeTag( xrefCellType, "shortlabel" );

        XrefTag xrefTissue = new XrefTag( XrefTag.PRIMARY_REF, "id", "db" );
        TissueTag tissue = new TissueTag( xrefTissue, "shortlabel" );

        OrganismTag organism = new OrganismTag( "1234", cellType, tissue );

        XrefTag xrefUniprot = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );

        ProteinInteractorTag proteinInteractor = new ProteinInteractorTag( xrefUniprot, organism );

        AnnotationTag annotation = new AnnotationTag( "expressedIn", "12345:biosource" );
        ExpressedInTag expressedIn = new ExpressedInTag( annotation );

        ProteinParticipantTag proteinParticipant = new ProteinParticipantTag( proteinInteractor, "bait", expressedIn );

        assertNotNull( proteinParticipant );
        assertEquals( expressedIn, proteinParticipant.getExpressedIn() );
        assertEquals( "bait", proteinParticipant.getRole() );
        assertEquals( proteinInteractor, proteinParticipant.getProteinInteractor() );
    }


    public void testProcess_ok_minimal_param() {

        XrefTag xrefUniprot = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );

        ProteinInteractorTag proteinInteractor = new ProteinInteractorTag( xrefUniprot, null );

        ProteinParticipantTag proteinParticipant = new ProteinParticipantTag( proteinInteractor, "bait", null );

        assertNotNull( proteinParticipant );
        assertEquals( null, proteinParticipant.getExpressedIn() );
        assertEquals( "bait", proteinParticipant.getRole() );
        assertEquals( proteinInteractor, proteinParticipant.getProteinInteractor() );
    }


    public void testProcess_error_empty_role() {

        XrefTag xrefUniprot = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );

        ProteinInteractorTag proteinInteractor = new ProteinInteractorTag( xrefUniprot, null );

        ProteinParticipantTag proteinParticipant = null;
        try {
            proteinParticipant = new ProteinParticipantTag( proteinInteractor, "", null );
            fail( "Should not allow to create a proteinParticipant with empty role." );
        } catch ( Exception e ) {
            // ok
        }

        assertNull( proteinParticipant );
    }


    public void testProcess_error_role_null() {

        XrefTag xrefUniprot = new XrefTag( XrefTag.PRIMARY_REF, "P12345", "uniprot" );

        ProteinInteractorTag proteinInteractor = new ProteinInteractorTag( xrefUniprot, null );

        ProteinParticipantTag proteinParticipant = null;
        try {
            proteinParticipant = new ProteinParticipantTag( proteinInteractor, null, null );
            fail( "Should not allow to create a proteinParticipant with null role." );
        } catch ( Exception e ) {
            // ok
        }

        assertNull( proteinParticipant );
    }


    public void testProcess_error_no_protein() {

        ProteinParticipantTag proteinParticipant = null;
        try {
            proteinParticipant = new ProteinParticipantTag( null, "bait", null );
            fail( "Should not allow to create a proteinParticipant with a null proteinInteractor." );
        } catch ( Exception e ) {
            // ok
        }

        assertNull( proteinParticipant );
    }
}