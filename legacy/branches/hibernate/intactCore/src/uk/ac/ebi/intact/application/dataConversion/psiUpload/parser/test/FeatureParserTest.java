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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.FeatureTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.LocationTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.FeatureParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.Message;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

import java.util.Collection;
import java.util.Iterator;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class FeatureParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public FeatureParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( FeatureParserTest.class );
    }

    private FeatureTag parse( String xmlContent ) {

        MessageHolder.getInstance().clearParserMessage();

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        FeatureTag feature = FeatureParser.process( element );

        Collection messages = MessageHolder.getInstance().getParserMessages();
        if ( messages.size() > 0 ) {
            for ( Iterator iterator = messages.iterator(); iterator.hasNext(); ) {
                Message message = (Message) iterator.next();

                System.out.println( message );
            }
        }

        return feature;
    }

    public void testValidFeature() {

        FeatureTag feature = parse( MockXmlContent.FEATURE_VALID );

        assertNotNull( feature );
        assertNotNull( feature.getShortlabel() );
        assertEquals( "my feature", feature.getShortlabel() );
        assertEquals( "my feature bla bla bla", feature.getFullname() );

        assertNotNull( feature.getFeatureType() );
        assertNotNull( feature.getFeatureType().getPsiDefinition() );
        XrefTag type = feature.getFeatureType().getPsiDefinition();
        assertNotNull( type );
        assertEquals( "MI:1234", type.getId() );
        assertEquals( "psi-mi", type.getDb() );
        assertEquals( "formylation reaction", type.getSecondary() );
        assertEquals( "", type.getVersion() );

        assertNotNull( feature.getXrefs() );
        assertEquals( 1, feature.getXrefs().size() );
        XrefTag xref = (XrefTag) feature.getXrefs().iterator().next();
        assertNotNull( xref );
        assertEquals( "IPR001977", xref.getId() );
        assertEquals( "interpro", xref.getDb() );
        assertEquals( "Depp_CoAkinase", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );

        assertNotNull( feature.getLocation() );
        LocationTag location = feature.getLocation();
        assertEquals( 2, location.getFromIntervalStart() );
        assertEquals( 5, location.getFromIntervalEnd() );
        assertEquals( 9, location.getToIntervalStart() );
        assertEquals( 9, location.getToIntervalEnd() );

        assertNotNull( feature.getFeatureDetection() );
        assertNotNull( feature.getFeatureDetection().getPsiDefinition() );
        XrefTag detection = feature.getFeatureDetection().getPsiDefinition();
        assertEquals( "MI:0113", detection.getId() );
        assertEquals( "psi-mi", detection.getDb() );
        assertEquals( "", detection.getSecondary() );
        assertEquals( "", detection.getVersion() );
    }

    public void testMinimalFeature() {

        FeatureTag feature = parse( MockXmlContent.FEATURE_MINIMAL );

        assertNotNull( feature );
        assertNotNull( feature.getShortlabel() );
        assertEquals( "my feature", feature.getShortlabel() );
        assertNull( feature.getFullname() );

        assertNotNull( feature.getXrefs() );
        assertEquals( 0, feature.getXrefs().size() );

        XrefTag xref = (XrefTag) feature.getFeatureType().getPsiDefinition();
        assertNotNull( xref );
        assertEquals( "MI:1234", xref.getId() );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "formylation reaction", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );

        assertNotNull( feature.getXrefs() );
        assertEquals( 0, feature.getXrefs().size() );

        assertNotNull( feature.getLocation() );
        LocationTag location = feature.getLocation();
        assertEquals( 2, location.getFromIntervalStart() );
        assertEquals( 5, location.getFromIntervalEnd() );
        assertEquals( 9, location.getToIntervalStart() );
        assertEquals( 9, location.getToIntervalEnd() );

        assertNotNull( feature.getFeatureDetection() );
        assertNotNull( feature.getFeatureDetection().getPsiDefinition() );
        XrefTag detection = feature.getFeatureDetection().getPsiDefinition();
        assertEquals( "MI:0113", detection.getId() );
        assertEquals( "psi-mi", detection.getDb() );
        assertEquals( "", detection.getSecondary() );
        assertEquals( "", detection.getVersion() );
    }

    public void testFeatureWithout() {

        FeatureTag feature = null;
        try {
            MessageHolder.getInstance().clearParserMessage();
            feature = parse( MockXmlContent.FEATURE_NOT_VALID_1 );
        } catch ( IllegalArgumentException e ) {
            // ok
            assertTrue( MessageHolder.getInstance().getParserMessages().size() > 0 );
        }

        assertNull( feature );

    }


}