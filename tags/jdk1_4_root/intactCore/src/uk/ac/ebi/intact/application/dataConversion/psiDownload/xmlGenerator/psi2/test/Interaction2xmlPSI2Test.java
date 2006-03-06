// Copyright (c) 2002-2003 The European Bioinformatics Institute, and others.
// All rights reserved. Please see the file LICENSE
// in the root directory of this distribution.

package uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.psi2.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.PsiVersion;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.UserSessionDownload;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.PsiDownloadTest;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.model.TestableFeature;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.test.model.TestableProtein;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlFactory;
import uk.ac.ebi.intact.application.dataConversion.psiDownload.xmlGenerator.Interaction2xmlI;
import uk.ac.ebi.intact.application.dataConversion.util.DOMUtil;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * TODO document this ;o)
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class Interaction2xmlPSI2Test extends PsiDownloadTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( Interaction2xmlPSI2Test.class );
    }

    ////////////////////////////////////
    // Utility methods

    Interaction buildInteraction() {

        Collection experiments = new ArrayList( 1 );
        experiments.add( Experiment2xmlPSI2Test.buildExperiment() );

        CvInteractionType anInteractionType = new CvInteractionType( owner, "anInteractionType" );
        anInteractionType.addXref( new Xref( owner, psi, "MI:0055", null, null, identity ) );

        Interaction interaction = new InteractionImpl( experiments, anInteractionType, interactionType, "gene1-gene2-3", owner );
        interaction.setFullName( "interaction's fullname." );

        // add 2 components
        Protein protein1 = new TestableProtein( "EBI-11111", owner, yeast, "p1_yeast", proteinType, "AAAAAAAAAAAAAAAAAA" );
        Component component1 = new Component( owner, interaction, protein1, bait );
        interaction.addComponent( component1 );

        Protein protein2 = new TestableProtein( "EBI-22222", owner, yeast, "p2_yeast", proteinType, "ZZZZZZZZZZZ" );
        Component component2 = new Component( owner, interaction, protein2, prey );
        interaction.addComponent( component2 );
        // put features on that Interaction !!

        // add xrefs
        interaction.addXref( new Xref( owner, go, "GO:0000000", "a fake GO term", null, null ) );
        interaction.addXref( new Xref( owner, go, "GO:0000001", "an other fake GO term", null, null ) );
        interaction.addXref( new Xref( owner, pubmed, "12345678", null, null, null ) );

        // add annotations (internal + public)
        interaction.addAnnotation( new Annotation( owner, comment, "a comment on that interaction." ) );
        interaction.addAnnotation( new Annotation( owner, remark, "a remark about that interaction." ) );
        interaction.addAnnotation( new Annotation( owner, authorConfidence, "HIGH" ) );
        interaction.addAnnotation( new Annotation( owner, authorConfidence, "0.75" ) );
        interaction.addAnnotation( new Annotation( owner, confidence_mapping, "blablabla" ) );

        return interaction;
    }


    Interaction buildComplexInteraction() {

        Interaction interaction = buildInteraction();
        Collection components = interaction.getComponents();
        Iterator i = components.iterator();

        Component component = (Component) i.next();
        Component component2 = (Component) i.next();
        Feature feature1 = new TestableFeature( "EBI-f1", owner, "F1", component, formylation );
        Feature feature2 = new TestableFeature( "EBI-f2", owner, "F2", component2, hydroxylation );

        feature1.setBoundDomain( feature2 ); // that should cause an inferred interaction to be generated.

        component.addBindingDomain( feature1 );
        component2.addBindingDomain( feature2 );

        return interaction;
    }

    ////////////////////////
    // Tests

    private void testBuildInteraction_nullArguments( PsiVersion version ) {

        UserSessionDownload session = new UserSessionDownload( version );
        Interaction2xmlI i = Interaction2xmlFactory.getInstance( session );

        // create a container
        Element parent = session.createElement( "interactionList" );

        // call the method we are testing
        Element element = null;

        try {
            element = i.create( session, parent, null );
            fail( "giving a null Interaction should throw an exception" );
        } catch ( IllegalArgumentException iae ) {
            // ok
        }

        assertNull( element );

        // create the IntAct object

        Interaction interaction = buildInteraction();

        try {
            element = i.create( null, parent, interaction );
            fail( "giving a null session should throw an exception" );
        } catch ( IllegalArgumentException iae ) {
            // ok
        }

        assertNull( element );

        try {
            element = i.create( session, null, interaction );
            fail( "giving a null parent Element should throw an exception" );
        } catch ( IllegalArgumentException iae ) {
            // ok
        }

        assertNull( element );
    }

    public void testBuildInteraction_nullArguments_PSI2() {
        testBuildInteraction_nullArguments( PsiVersion.getVersion2() );
    }

    public void testBuildInteraction_full_ok_PSI2() {

        UserSessionDownload session = new UserSessionDownload( PsiVersion.getVersion2() );
        session.addAnnotationFilter( remark );

        Interaction2xmlI i = Interaction2xmlFactory.getInstance( session );

        // create a container
        Element parent = session.createElement( "interactionList" );

        // call the method we are testing
        Element element = null;

        // create the IntAct object
        Interaction interaction = buildComplexInteraction();

        // generating the PSI element...
        element = i.create( session, parent, interaction );

        // starting the checks...
        assertNotNull( element );

        // names availabilityRef availabilityDescription experimentList participantList interactionType confidence xref attributeList
        assertEquals( 9, element.getChildNodes().getLength() );

        // Checking names...
        // TODO write a method that returns an Element by name coming from the direct level
        Element names = (Element) element.getElementsByTagName( "names" ).item( 0 );
        assertNotNull( names );
        assertEquals( 2, names.getChildNodes().getLength() );
        assertHasShortlabel( names, "gene1-gene2-3" );
        assertHasFullname( names, "interaction's fullname." );

        // checking availabilityRef...

        // checking experimentList...
        Element experimentListElement = (Element) element.getElementsByTagName( "experimentList" ).item( 0 );
        assertNotNull( experimentListElement );
        assertEquals( 1, experimentListElement.getChildNodes().getLength() );
        Element experimentRefElement = (Element) experimentListElement.getElementsByTagName( "experimentRef" ).item( 0 );
        assertNotNull( experimentRefElement );
        long id = session.getExperimentIdentifier( (Experiment) interaction.getExperiments().iterator().next() );
        assertEquals( id + "", experimentRefElement.getAttribute( "ref" ) );

        // Checking participantList...
        Element participantList = (Element) element.getElementsByTagName( "participantList" ).item( 0 );
        assertNotNull( participantList );
        // TODO check that

        // Checking interactionType...
        Element interactionType = (Element) element.getElementsByTagName( "interactionType" ).item( 0 );
        assertNotNull( interactionType );
        assertEquals( 2, interactionType.getChildNodes().getLength() );
        // Checking interactionDetection's names...
        names = (Element) interactionType.getElementsByTagName( "names" ).item( 0 );
        assertNotNull( names );
        assertEquals( 1, names.getChildNodes().getLength() );
        assertHasShortlabel( names, "anInteractionType" );
        // Checking interactionType's PSI ID...
        Element xref = (Element) interactionType.getElementsByTagName( "xref" ).item( 0 );
        assertHasPrimaryRef( xref, "MI:0055", "psi-mi", null, null );

        // Checking confidence...
        Element confidence = (Element) element.getElementsByTagName( "confidence" ).item( 0 );
        assertNotNull( confidence );
        // TODO check that

        // Checking xref...
        xref = (Element) DOMUtil.getDirectElementsByTagName( element, "xref" ).iterator().next();
        assertNotNull( xref );
        assertEquals( 3, xref.getChildNodes().getLength() );
        assertHasPrimaryRef( xref, "GO:0000000", "go", "a fake GO term", null );
        assertHasSecondaryRef( xref, "GO:0000001", "go", "an other fake GO term", null );
        assertHasSecondaryRef( xref, "12345678", "pubmed", null, null );

        // Checking attributeList...
        Element attributeList = (Element) element.getElementsByTagName( "attributeList" ).item( 0 );
        assertNotNull( attributeList );
        // the remark should have been filtered out.
        assertEquals( 2, attributeList.getChildNodes().getLength() );
        assertHasAttribute( attributeList, "comment", "a comment on that interaction." );
    }
}