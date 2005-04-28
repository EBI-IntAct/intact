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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.ConfidenceTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.ConfidenceParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class ConfidenceParserTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public ConfidenceParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( ConfidenceParserTest.class );
    }

    private ConfidenceTag parse( String xmlContent ) {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        return ConfidenceParser.process( element );
    }

    public void test() {

        ConfidenceTag confidence = parse( "<confidence unit=\"Hybrigenics PBS(r)\" value=\"A\"/>" ); // MockXmlContent.CONFIDENCE_1

        assertNotNull( confidence );

        assertEquals( "Hybrigenics PBS(r)", confidence.getUnit() );
        assertEquals( "A", confidence.getValue() );
    }
}
