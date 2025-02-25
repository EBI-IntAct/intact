// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi25.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.PsiDownloadTest;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.AbstractXref2Xml;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Xref2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi25.Xref2xmlPSI2;
import uk.ac.ebi.intact.model.CvDatabase;
import uk.ac.ebi.intact.model.CvXrefQualifier;
import uk.ac.ebi.intact.model.Xref;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Xref2xmlPSI25Test extends PsiDownloadTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( Xref2xmlPSI25Test.class );
    }

    ////////////////////////
    // Tests

    public void testBuildXref_primaryRef_nullArguments() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.getVersion25() );

        // create a container
        Element xrefElement = session.createElement( "xref" );

        // call the method we are testing
        Element primaryRef = null;

        try {
            primaryRef = Xref2xmlFactory.getInstance( session ).createPrimaryRef( session, xrefElement, null );
            fail( "giving a null xref should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( primaryRef );

        // create the IntAct object
        Xref xref = new Xref( owner, uniprot, "P12345", "P67890", "56", identity );

        try {
            primaryRef = Xref2xmlFactory.getInstance( session ).createPrimaryRef( null, xrefElement, xref );
            fail( "giving a null session should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( primaryRef );

        try {
            primaryRef = Xref2xmlFactory.getInstance( session ).createPrimaryRef( session, null, xref );
            fail( "giving a null parent Element should throw an exception" );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( primaryRef );
    }

    public void testBuildXref_primaryRef() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.getVersion25() );

        // create a container
        Element xrefElement = session.createElement( "xref" );

        // create the IntAct object
        Xref xref = new Xref( owner, uniprot, "P12345", null, null, identity );

        // call the method we are testing
        Element primaryRef = Xref2xmlFactory.getInstance( session ).createPrimaryRef( session, xrefElement, xref );
        assertNotNull( primaryRef );

        // check that we have a primaryRef attached to the given parent tag
        assertEquals( 1, xrefElement.getChildNodes().getLength() );
        Element _primaryRef = (Element) xrefElement.getChildNodes().item( 0 );
        assertEquals( primaryRef, _primaryRef );

        // check content of the tag
        assertEquals( AbstractXref2Xml.PRIMARY_REF, primaryRef.getNodeName() );
        assertEquals( CvDatabase.UNIPROT, primaryRef.getAttribute( Xref2xmlPSI2.XREF_DB ) );
        assertEquals( CvDatabase.UNIPROT_MI_REF, primaryRef.getAttribute( Xref2xmlPSI2.XREF_DB_AC ) );
        assertEquals( "P12345", primaryRef.getAttribute( Xref2xmlPSI2.XREF_ID ) );
        assertEquals( "", primaryRef.getAttribute( Xref2xmlPSI2.SECONDARY_REF ) );
        assertEquals( "", primaryRef.getAttribute( Xref2xmlPSI2.XREF_VERSION ) );
        assertEquals( CvXrefQualifier.IDENTITY, primaryRef.getAttribute( Xref2xmlPSI2.XREF_REFTYPE ) );
        assertEquals( CvXrefQualifier.IDENTITY_MI_REF, primaryRef.getAttribute( Xref2xmlPSI2.XREF_REFTYPE_AC ) );
    }

    public void testBuildXref_secondaryRef() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.getVersion25() );

        // create a container
        Element xrefElement = session.createElement( "xref" );

        // create the IntAct object
        Xref xref = new Xref( owner, uniprot, "P12345", null, null, identity );

        // call the method we are testing
        Element secondaryRef = Xref2xmlFactory.getInstance( session ).createSecondaryRef( session, xrefElement, xref );
        assertNotNull( secondaryRef );

        // check that we have a secondaryRef attached to the given parent tag
        assertEquals( 1, xrefElement.getChildNodes().getLength() );
        Element _primaryRef = (Element) xrefElement.getChildNodes().item( 0 );
        assertEquals( secondaryRef, _primaryRef );

        // check content of the tag
        assertEquals( AbstractXref2Xml.SECONDARY_REF, secondaryRef.getNodeName() );
        assertEquals( CvDatabase.UNIPROT, secondaryRef.getAttribute( Xref2xmlPSI2.XREF_DB ) );
        assertEquals( CvDatabase.UNIPROT_MI_REF, secondaryRef.getAttribute( Xref2xmlPSI2.XREF_DB_AC ) );
        assertEquals( "P12345", secondaryRef.getAttribute( Xref2xmlPSI2.XREF_ID ) );
        assertEquals( "", secondaryRef.getAttribute( Xref2xmlPSI2.SECONDARY_REF ) );
        assertEquals( "", secondaryRef.getAttribute( Xref2xmlPSI2.XREF_VERSION ) );
        assertEquals( CvXrefQualifier.IDENTITY, secondaryRef.getAttribute( Xref2xmlPSI2.XREF_REFTYPE ) );
        assertEquals( CvXrefQualifier.IDENTITY_MI_REF, secondaryRef.getAttribute( Xref2xmlPSI2.XREF_REFTYPE_AC ) );
    }
}