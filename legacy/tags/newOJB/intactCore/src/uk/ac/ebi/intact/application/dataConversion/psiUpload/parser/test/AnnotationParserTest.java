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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.AnnotationTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.AnnotationParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotationParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public AnnotationParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( AnnotationParserTest.class );
    }


    public void testProcess_basicComment() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.ANNOTATION_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        AnnotationTag annotation = null;
        try {
            annotation = AnnotationParser.process( element );
        } catch ( IllegalArgumentException iae ) {
            fail( "The creation of an AnnotationTag failed where it shouldn't have." );
        }
        assertNotNull( annotation );
        assertEquals( "comment", annotation.getType() );
        assertEquals( "my comment", annotation.getText() );
    }

    public void testProcess_basicRemark() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.ANNOTATION_2 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        AnnotationTag annotation = null;
        try {
            annotation = AnnotationParser.process( element );
        } catch ( IllegalArgumentException iae ) {
            fail( "The creation of an AnnotationTag failed where it shouldn't have." );
        }
        assertNotNull( annotation );
        assertEquals( "remark", annotation.getType() );
        assertEquals( "my remark", annotation.getText() );
    }

    public void testProcess_noType() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.ANNOTATION_3 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        try {
            // an empty string is ok.
            AnnotationParser.process( element );
        } catch ( IllegalArgumentException iae ) {
            fail( "The creation of an AnnotationTag didn't failed where it should have. A type is required" );
        }
    }

    public void testProcess_noText() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.ANNOTATION_4 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        try {
            AnnotationParser.process( element );
            fail( "The creation of an AnnotationTag didn't failed where it should have. A text is required" );
        } catch ( IllegalArgumentException iae ) {
            // ok !
        }
    }
}
