/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test;

import junit.framework.Test;
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
public class AnnotationParserTest extends ParserTest {

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( AnnotationParserTest.class );
    }

    private AnnotationTag parse( String xmlContent ) {

        clearParserMessages();

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        AnnotationTag annotation = AnnotationParser.process( element );
        displayExistingMessages();

        return annotation;
    }


    public void testProcess_basicComment() {

        AnnotationTag annotation = parse( MockXmlContent.ANNOTATION_1 );

        assertNotNull( annotation );
        assertEquals( "comment", annotation.getType() );
        assertEquals( "my comment", annotation.getText() );
    }

    public void testProcess_basicRemark() {

        AnnotationTag annotation = parse( MockXmlContent.ANNOTATION_2 );

        assertNotNull( annotation );
        assertEquals( "remark", annotation.getType() );
        assertEquals( "my remark", annotation.getText() );
    }

    public void testProcess_noType() {

        try {
            // an empty string is ok.
            AnnotationTag annotation = parse( MockXmlContent.ANNOTATION_3 );
        } catch ( IllegalArgumentException iae ) {
            fail( "The creation of an AnnotationTag didn't failed where it should have. A type is required" );
        }
    }

    public void testProcess_noText() {

        AnnotationTag annotation = null;
        try {
            // an empty string is ok.
            annotation = parse( MockXmlContent.ANNOTATION_4 );
            fail( "The creation of an AnnotationTag didn't failed where it should have. A type is required" );
        } catch ( IllegalArgumentException iae ) {
            // ok.
        }
    }
}
