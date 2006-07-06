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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.LocationTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.LocationParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.report.MessageHolder;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class LocationParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public LocationParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( LocationParserTest.class );
    }

    private LocationTag parse( String xmlContent, boolean shouldHaveParsingError ) {

        MessageHolder.getInstance().clearParserMessage();

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();


        LocationTag location = LocationParser.process( element );

        if ( shouldHaveParsingError ) {
            assertTrue( MessageHolder.getInstance().getParserMessages().size() > 0 );
        } else {
            assertTrue( MessageHolder.getInstance().getParserMessages().size() == 0 );
        }

        return location;
    }

    /**
     * Check on valid data
     */
    public void testValidFeature1() {

        LocationTag location = parse( MockXmlContent.LOCATION_1, false );

        assertNotNull( location );
        assertEquals( 2, location.getFromIntervalStart() );
        assertEquals( 2, location.getFromIntervalEnd() );
        assertEquals( 10, location.getToIntervalStart() );
        assertEquals( 13, location.getToIntervalEnd() );
    }

    public void testValidFeature2() {

        LocationTag location = parse( MockXmlContent.LOCATION_2, false );

        assertNotNull( location );
        assertEquals( 10, location.getFromIntervalStart() );
        assertEquals( 13, location.getFromIntervalEnd() );
        assertEquals( 99, location.getToIntervalStart() );
        assertEquals( 99, location.getToIntervalEnd() );
    }

    public void testValidFeature3() {

        LocationTag location = parse( MockXmlContent.LOCATION_3, false );

        assertNotNull( location );
        assertEquals( 7, location.getFromIntervalStart() );
        assertEquals( 7, location.getFromIntervalEnd() );
        assertEquals( 7, location.getToIntervalStart() );
        assertEquals( 7, location.getToIntervalEnd() );
    }

    public void testValidFeature4() {

        LocationTag location = parse( MockXmlContent.LOCATION_4, false );

        assertNotNull( location );
        assertEquals( 122, location.getFromIntervalStart() );
        assertEquals( 122, location.getFromIntervalEnd() );
        assertEquals( 122, location.getToIntervalStart() );
        assertEquals( 122, location.getToIntervalEnd() );
    }

    public void testValidFeature5() {

        LocationTag location = parse( MockXmlContent.LOCATION_5, false );

        assertNotNull( location );
        assertEquals( 0, location.getFromIntervalStart() );
        assertEquals( 0, location.getFromIntervalEnd() );
        assertEquals( 0, location.getToIntervalStart() );
        assertEquals( 0, location.getToIntervalEnd() );
    }


    /**
     * Error cases
     */
    public void testNonValidFeature1() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_1, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature2() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_2, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature3() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_3, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature4() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_4, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature5() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_5, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature6() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_6, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

    public void testNonValidFeature7() {

        LocationTag location = null;
        try {
            location = parse( MockXmlContent.LOCATION_WRONG_7, true );
        } catch ( IllegalArgumentException e ) {
            // ok
        }

        assertNull( location );
    }

}