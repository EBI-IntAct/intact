/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.OrganismTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ProteinInteractorTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.ProteinInteractorParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.LabelValueBean;
import uk.ac.ebi.intact.model.Xref;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ProteinInteractorParserTest extends TestCase {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public ProteinInteractorParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( ProteinInteractorParserTest.class );
    }

    private Xref getXrefByCvDatabase( final Collection xrefs, final String db ) {

        for ( Iterator iterator = xrefs.iterator(); iterator.hasNext(); ) {
            final Xref xref = (Xref) iterator.next();
            if ( db.equals( xref.getCvDatabase().getShortLabel() ) ) {
                return xref;
            }
        }
        return null;
    }

    public void testProcessWithBioSource() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.PROTEIN_INTERACTOR_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        LabelValueBean lvb = null;
        final ProteinInteractorParser proteinInteractor = new ProteinInteractorParser( null, element );
        lvb = proteinInteractor.process();

        assertNotNull( lvb );
        assertEquals( "EBI-111", lvb.getLabel() );
        final ProteinInteractorTag protein = (ProteinInteractorTag) lvb.getValue();
        assertNotNull( protein );

        OrganismTag organism = protein.getOrganism();
        assertNotNull( organism );
        assertEquals( "4932", organism.getTaxId() );

        XrefTag xref = protein.getPrimaryXref();
        assertNotNull( xref );
        assertEquals( "uniprotkb", xref.getDb() );
        assertEquals( "P12345", xref.getId() );
        assertEquals( "blablabla", xref.getSecondary() );
        assertEquals( "2.46", xref.getVersion() );
    }

    public void testProcessWithoutBioSource() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.PROTEIN_INTERACTOR_WITHOUT_BIOSOURCE );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        LabelValueBean lvb = null;
        final ProteinInteractorParser proteinInteractor = new ProteinInteractorParser( null, element );
        lvb = proteinInteractor.process();

        assertNotNull( lvb );
        assertEquals( "EBI-333", lvb.getLabel() );
        final ProteinInteractorTag protein = (ProteinInteractorTag) lvb.getValue();
        assertNotNull( protein );

        OrganismTag organism = protein.getOrganism();
        assertNull( organism );

        XrefTag xref = protein.getPrimaryXref();
        assertNotNull( xref );
        assertEquals( "uniprotkb", xref.getDb() );
        assertEquals( "P12345", xref.getId() );
        assertEquals( "blablabla", xref.getSecondary() );
        assertEquals( "2.46", xref.getVersion() );
    }
}
