/*
 * Copyright (c) 2002 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.application.dataConversion.psiUpload.util.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.util.DOMUtil;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 * 
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class DOMUtilTest extends TestCase {

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public DOMUtilTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all
     * the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( DOMUtilTest.class );
    }

    public void testGetFirstElement() {

        useGetFirstElement( MockXmlContent.OCCURENCE_OF_TAG_AT_DIFFERENT_LEVEL );
        useGetFirstElement( MockXmlContent.OCCURENCE_OF_TAG_AT_DIFFERENT_LEVEL_2 );
    }

    private void useGetFirstElement( String xmlContent ) {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( xmlContent );
        final Document document = MockDocumentBuilder.build( is );
        final Element root = document.getDocumentElement();

        Element colorElement = DOMUtil.getFirstElement( root, "color" );
        String color = DOMUtil.getSimpleElementText( colorElement );
        assertEquals( color, "blue" );

        Element hairElement = DOMUtil.getFirstElement( root, "hair" );
        colorElement = DOMUtil.getFirstElement( hairElement, "color" );
        color = DOMUtil.getSimpleElementText( colorElement );
        assertEquals( color, "red" );
    }
}
