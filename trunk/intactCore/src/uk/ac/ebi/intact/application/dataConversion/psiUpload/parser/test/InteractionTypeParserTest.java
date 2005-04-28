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
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.InteractionTypeTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.model.XrefTag;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.InteractionTypeParser;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockDocumentBuilder;
import uk.ac.ebi.intact.application.dataConversion.psiUpload.parser.test.mock.MockXmlContent;
import uk.ac.ebi.intact.util.test.mocks.MockInputStream;

/**
 * That class .
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 */
public class InteractionTypeParserTest extends TestCase {

    private static final String NEW_LINE = System.getProperty( "line.separator" );

    /**
     * Constructs a NewtServerProxyTest instance with the specified name.
     *
     * @param name the name of the test.
     */
    public InteractionTypeParserTest( final String name ) {
        super( name );
    }

    /**
     * Returns this test suite. Reflection is used here to add all the testXXX() methods to the suite.
     */
    public static Test suite() {
        return new TestSuite( InteractionTypeParserTest.class );
    }

    public void testProcess() {

        final MockInputStream is = new MockInputStream();
        is.setBuffer( MockXmlContent.INTERACTION_TYPE_1 );
        final Document document = MockDocumentBuilder.build( is );
        final Element element = document.getDocumentElement();

        InteractionTypeTag cvInteractionType = InteractionTypeParser.process( element );
        assertNotNull( cvInteractionType );
        XrefTag xref = cvInteractionType.getPsiDefinition();
        assertNotNull( xref );
        assertEquals( "psi-mi", xref.getDb() );
        assertEquals( "MI:xxx", xref.getId() );
        assertEquals( "", xref.getSecondary() );
        assertEquals( "", xref.getVersion() );
    }
}
