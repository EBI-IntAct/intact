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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.*;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.ExperimentDescriptionParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.LabelValueBean;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.util.Collection;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentDescriptionParserTest extends TestCase {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public ExperimentDescriptionParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( ExperimentDescriptionParserTest.class );
    }

    public static void checkExperimentContent_gavin_2002( final ExperimentDescriptionTag experiment ) {

        assertEquals( "gavin-2002", experiment.getShortlabel() );
        assertEquals( experiment.getFullname(), "Functional organization of the yeast proteome by " +
                                                "systematic analysis of protein complexes." );

        // HostOrganism
        final HostOrganismTag hostOrganism = experiment.getHostOrganism();
        assertNotNull( hostOrganism );
        assertEquals( "4932", hostOrganism.getTaxId() );

        // assertEquals\((.*)\, (.*)\)
        // assertEquals\\( $2\\, $1 \\)


        // Pubmed ids
        XrefTag bibref = experiment.getBibRef();
        assertNotNull( bibref );

        assertEquals( true, bibref.isPrimaryRef() );
        assertEquals( false, bibref.isSecondaryRef() );
        assertEquals( "11805826", bibref.getId() );
        assertEquals( "pubmed", bibref.getDb() );
        assertEquals( "", bibref.getVersion() );
        assertEquals( "", bibref.getSecondary() );

        final Collection secondaryBibRefs = experiment.getAdditionalBibRef();
        assertNotNull( secondaryBibRefs );
        assertEquals( 1, secondaryBibRefs.size() );

        bibref = (XrefTag) secondaryBibRefs.iterator().next();
        assertEquals( false, bibref.isPrimaryRef() );
        assertEquals( true, bibref.isSecondaryRef() );
        assertEquals( "11809999", bibref.getId() );
        assertEquals( "pubmed", bibref.getDb() );
        assertEquals( "", bibref.getVersion() );
        assertEquals( "", bibref.getSecondary() );

        // Xrefs
        final Collection xrefs = experiment.getXrefs();
        assertNotNull( xrefs );
        assertEquals( 3, xrefs.size() );

        XrefTag xref = Utilities.getXrefByCvDatabase( xrefs, "go" );
        assertNotNull( xref );
        assertEquals( true, xref.isPrimaryRef() );
        assertEquals( false, xref.isSecondaryRef() );
        assertEquals( "GO:0000000", xref.getId() );
        assertEquals( "go", xref.getDb() );
        assertEquals( "versionX", xref.getVersion() );
        assertEquals( "blabla", xref.getSecondary() );

        xref = Utilities.getXrefByCvDatabase( xrefs, "psi-mi" );
        assertNotNull( xref );
        assertEquals( false, xref.isPrimaryRef() );
        assertEquals( true, xref.isSecondaryRef() );
        assertEquals( "MI:0082", xref.getId() );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "", xref.getVersion() );
        assertEquals( "", xref.getSecondary() );

        xref = Utilities.getXrefByCvDatabase( xrefs, "foo-mi" );
        assertNotNull( xref );
        assertEquals( false, xref.isPrimaryRef() );
        assertEquals( true, xref.isSecondaryRef() );
        assertEquals( "FOO:0082", xref.getId() );
        assertEquals( "foo-mi", xref.getDb() );
        assertEquals( "", xref.getVersion() );
        assertEquals( "", xref.getSecondary() );


        // Annotations
        final Collection annotations = experiment.getAnnotations();
        assertNotNull( annotations );
        assertEquals( 3, annotations.size() );

        AnnotationTag annotation = Utilities.getAnnotationByType( annotations, "comment" );
        assertNotNull( annotation );
        assertEquals( "comment", annotation.getType() );
        assertEquals( "a first comment.", annotation.getText() );

        annotation = Utilities.getAnnotationByType( annotations, "test" );
        assertNotNull( annotation );
        assertEquals( "test", annotation.getType() );
        assertEquals( "A first test", annotation.getText() );

        annotation = Utilities.getAnnotationByType( annotations, "remark" );
        assertNotNull( annotation );
        assertEquals( "remark", annotation.getType() );
        assertEquals( "Oh! a remark", annotation.getText() );


        // Detection method
        final InteractionDetectionTag participantDetection = experiment.getInteractionDetection();
        assertNotNull( participantDetection );
        xref = participantDetection.getPsiDefinition();
        assertNotNull( xref );
        assertEquals( true, xref.isPrimaryRef() );
        assertEquals( false, xref.isSecondaryRef() );
        assertEquals( "MI:0109", xref.getId() );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "", xref.getVersion() );
        assertEquals( "", xref.getSecondary() );

        
        // Detection method
        final ParticipantDetectionTag cvIdentification = experiment.getParticipantDetection();
        assertNotNull( cvIdentification );
        xref = cvIdentification.getPsiDefinition();
        assertNotNull( xref );
        assertEquals( true, xref.isPrimaryRef() );
        assertEquals( false, xref.isSecondaryRef() );
        assertEquals( "MI:0082", xref.getId() );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "", xref.getVersion() );
        assertEquals( "", xref.getSecondary() );
    }

    public void testProcess() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.EXPERIMENT_DESCRIPTION_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        LabelValueBean lvb = null;
        final ExperimentDescriptionParser experimentDescription = new ExperimentDescriptionParser( null, element );
        lvb = experimentDescription.process();

        assertNotNull( lvb );

        assertEquals( "EBI-12", lvb.getLabel() );
        final ExperimentDescriptionTag experiment = (ExperimentDescriptionTag) lvb.getValue();
        checkExperimentContent_gavin_2002( experiment );
    }
}
